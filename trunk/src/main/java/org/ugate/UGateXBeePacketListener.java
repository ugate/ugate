package org.ugate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.ugate.UGateKeeperEvent.Type;
import org.ugate.resources.RS;
import org.ugate.wireless.data.ImageCapture;
import org.ugate.wireless.data.KeyCodes;
import org.ugate.wireless.data.RxData;
import org.ugate.wireless.data.RxData.Status;
import org.ugate.wireless.data.RxRawData;
import org.ugate.wireless.data.RxTxImage;
import org.ugate.wireless.data.RxTxJPEG;
import org.ugate.wireless.data.SensorReadings;
import org.ugate.wireless.data.SettingsData;

import com.rapplogic.xbee.api.ErrorResponse;
import com.rapplogic.xbee.api.PacketListener;
import com.rapplogic.xbee.api.XBeeResponse;
import com.rapplogic.xbee.api.wpan.RxResponse16;
import com.rapplogic.xbee.api.wpan.TxStatusResponse;
import com.rapplogic.xbee.util.ByteUtils;

/**
 * XBee packet listener
 */
public abstract class UGateXBeePacketListener implements PacketListener {

	private static final Logger log = Logger.getLogger(UGateXBeePacketListener.class);
	/**
	 * The index of the image start byte
	 */
	public static final int IMAGE_START_INDEX = 7;
	private volatile RxTxImage rxTxImage;
	private volatile int rxTxAttempts = 0;

	/**
	 * Process responses as they are received
	 * 
	 * @param response the XBee response
	 */
	@Override
	public void processResponse(final XBeeResponse response) {
		try {
			// API Mode Bytes:
			// 1=Start Delimiter, 2=Most Significant Byte, 3=Least Significant Byte, 4-n=Frame Data, n+1=Checksum
			// 4=API Identifier
			// 5=Status... Available values:
			// 0 = Hardware reset
			// 1 = Watchdog timer reset
			// 2 = Associated
			// 3 = Disassociated
			// 4 = Synchronization Lost (Beacon-enabled only )
			// 5 = Coordinator realignment
			// 6 = Coordinator started
			// 6=AT Command (D)
			// 7=AT Command (L)
			if (response instanceof RxResponse16) {
				handleRxResponse16((RxResponse16) response);
			} else if (response instanceof TxStatusResponse) {
				final Command command = extractCommand(response);
				final String rawBytes = ByteUtils.toBase16(response.getRawPacketBytes());
				final TxStatusResponse txResponse = (TxStatusResponse) response;
				RxData rd;
				if (txResponse.getStatus() == TxStatusResponse.Status.SUCCESS) {
					rd = new RxRawData<String>(null, Status.NORMAL, 0, rawBytes);
					processData(UGateKeeperEvent.Type.WIRELESS_DATA_TX_STATUS_RESPONSE_SUCCESS, command, null, rd, 
							RS.rbLabel("service.tx.response.success", rd, txResponse.getStatus()));
				} else {
					rd = new RxRawData<String>(null, Status.GENERAL_FAILURE, 0, rawBytes);
					processData(UGateKeeperEvent.Type.WIRELESS_DATA_TX_STATUS_RESPONSE_FAILED, command, null, rd, 
							RS.rbLabel("service.tx.response.error", rd, txResponse.getStatus()));
				}
			} else if (response instanceof ErrorResponse) {
				final Command command = extractCommand(response);
				final ErrorResponse errorResponse = (ErrorResponse) response;
				final String rawBytes = ByteUtils.toBase16(response.getRawPacketBytes());
				RxData rd;
				if (rxTxImage != null) {
					rxTxImage.setStatus(RxData.Status.PARSING_ERROR);
					rd = rxTxImage.createImageSegmentsSnapshot();
				} else {
					rd = new RxRawData<String>(null, Status.GENERAL_FAILURE, 0, rawBytes);
				}
				processData(UGateKeeperEvent.Type.WIRELESS_DATA_TX_STATUS_RESPONSE_FAILED, command, null, rd, 
						RS.rbLabel("service.tx.response.error", rd, errorResponse.getErrorMsg()));
				log.error("", errorResponse.getException());
			} else {
				final String rawBytes = ByteUtils.toBase16(response.getRawPacketBytes());
				int[] processedPacketBytes = response.getProcessedPacketBytes();
				if (processedPacketBytes != null && processedPacketBytes.length > 0) {
					/*
					 * for (int b : processedPacketBytes) { log.info(ByteUtils.formatByte(b)); }
					 */
					log.warn("Unused response type for \"" + response + "\"... Incoming RAW Data: \"" + ByteUtils.toBase16(processedPacketBytes) + '"');
					final RxData rd = new RxRawData<String>(null, Status.GENERAL_FAILURE, 0, rawBytes);
					processData(UGateKeeperEvent.Type.WIRELESS_DATA_TX_STATUS_RESPONSE_UNRECOGNIZED, null, null, rd, 
							RS.rbLabel("service.tx.response.unrecognized", rd, rawBytes));
				}
			}
		} catch (final Exception e) {
			log.error("An unexpected error occurred ", e);
		}
	}
	
	/**
	 * Remote XBee radio used for gate operations using a 16-bit address: 3333 
	 * (XBee must NOT be configured with "MY" set to FFFF)
	 * 
	 * @param rxResponse the RX response
	 */
	protected void handleRxResponse16(final RxResponse16 rxResponse) {
		final String remoteAddress = Integer.toHexString(rxResponse.getRemoteAddress().getAddress()[0]) + 
				Integer.toHexString(rxResponse.getRemoteAddress().getAddress()[1]);
		final Integer remoteIndex = UGateKeeper.DEFAULT.wirelessGetAddressIndex(remoteAddress);
		final Command command = extractCommand(rxResponse);
		if (command == null) {
			log.error(String.format("An unrecognized %1$s command was received from %2$s", 
					rxResponse.getData()[0], remoteAddress));
			return;
		}
		final int failures = rxResponse.getData()[1]; // TODO : Handle cases where failures exist
		final RxData.Status status = failures == 0 ? RxData.Status.NORMAL : RxData.Status.GENERAL_FAILURE;
		log.info(String.format("======= Recieved %1$s command from wireless address %2$s (signal strength: %3$s) with (%4$s) failures =======", 
				command, remoteAddress, rxResponse.getRssi(), failures));
		if (command == Command.CAM_TAKE_PIC) {
			ImageCapture ic;
			if (rxTxImage == null || rxTxImage.hasTimedOut()) {
				if (rxTxImage != null) {
					rxTxAttempts = 0;
					ic = rxTxImage.createImageSegmentsSnapshot();
					processData(UGateKeeperEvent.Type.WIRELESS_DATA_RX_FAILED, command, remoteAddress, ic, 
							RS.rbLabel("service.rx.image.timeout", ic));
				}
				// TODO : add check for what sensor tripped the image and image format detection (instead of using just JPEG)
				rxTxImage = new RxTxJPEG(remoteIndex, status, rxResponse.getRssi(), null);
				ic = rxTxImage.createImageSegmentsSnapshot();
				log.info(String.format("======= Receiving chunked image data (%1$s) =======", rxTxImage));
				processData(UGateKeeperEvent.Type.WIRELESS_DATA_RX_MULTIPART, command, remoteAddress, ic, 
						RS.rbLabel("service.rx.image.multipart", ic));
			}
			int[] imageChunk = rxTxImage.addImageSegment(rxResponse.getData(), IMAGE_START_INDEX);
			if (log.isDebugEnabled()) {
				log.debug(String.format("Sensor Tripped (%1$s, LENGTH: %2$s, RAW LENGTH: %3$s) DATA: %4$s", 
						rxTxImage, imageChunk.length, rxResponse.getLength().getLength(), ByteUtils.toBase16(imageChunk)));
			}
			if (rxTxImage.isEof()) {
				if (rxTxImage.getStatus() != RxData.Status.NORMAL) {
					final String retriesStr = UGateKeeper.DEFAULT.settingsGet(RemoteSettings.CAM_IMG_CAPTURE_RETRY_CNT,
							UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex());
					final int retries = retriesStr != null && retriesStr.length() > 0 ? Integer.parseInt(retriesStr) : 0;
					rxTxImage = null;
					ic = rxTxImage.createImageSegmentsSnapshot();
					if (retries != 0 && rxTxAttempts <= retries) {
						rxTxAttempts++;
						processData(UGateKeeperEvent.Type.WIRELESS_DATA_RX_FAILED_RETRYING, command, remoteAddress, ic, 
								RS.rbLabel("service.rx.image.lostpackets.retry", ic, rxTxAttempts, retries));
						UGateKeeper.DEFAULT.wirelessSendData(UGateKeeper.DEFAULT.wirelessGetAddressIndex(remoteAddress), command);
					} else {
						processData(UGateKeeperEvent.Type.WIRELESS_DATA_RX_FAILED, command, remoteAddress, ic, 
								RS.rbLabel("service.rx.image.lostpackets", ic, rxTxAttempts));
					}
				} else {
					try {
						ic = rxTxImage.writeImageSegments();
						processData(UGateKeeperEvent.Type.WIRELESS_DATA_RX_SUCCESS, command, remoteAddress, ic, 
								RS.rbLabel("service.rx.image.success", ic));
					} catch (IOException e) {
						log.info("Cannot save image ID: " + UGateUtil.calFormat(rxTxImage.getCreatedTime()), e);
					} finally {
						rxTxImage = null;
						rxTxAttempts = 0;
					}
				}
			}
		} else if (command == Command.ACCESS_CODE_CHANGE) {
			//final int hasFailures = rxResponse.getData()[1];
			final KeyCodes kc = new KeyCodes(remoteIndex, status, rxResponse.getRssi(), rxResponse.getData()[1], 
					rxResponse.getData()[2], rxResponse.getData()[3]);
			processData(UGateKeeperEvent.Type.WIRELESS_DATA_RX_SUCCESS, command, remoteAddress, kc, 
					RS.rbLabel("service.rx.keycodes", kc));
		} else if (command == Command.SENSOR_GET_READINGS) {
			log.info("=== Sensor Readings received ===");
			int i = 1;
			final SensorReadings sr = new SensorReadings(remoteIndex, status, rxResponse.getRssi(), 
					rxResponse.getData()[++i], rxResponse.getData()[++i], rxResponse.getData()[++i], 
					rxResponse.getData()[++i], rxResponse.getData()[++i], rxResponse.getData()[++i]);
			processData(UGateKeeperEvent.Type.WIRELESS_DATA_RX_SUCCESS, command, remoteAddress, sr, 
					RS.rbLabel("service.rx.readings", sr));
		} else if (command == Command.SENSOR_GET_SETTINGS) {
			int i = 1;
			final SettingsData sd = new SettingsData(remoteIndex, status, rxResponse.getRssi(), rxResponse.getData()[++i], 
					rxResponse.getData()[++i], rxResponse.getData()[++i], rxResponse.getData()[++i], 
					rxResponse.getData()[++i], rxResponse.getData()[++i], rxResponse.getData()[++i], 
					rxResponse.getData()[++i], rxResponse.getData()[++i], rxResponse.getData()[++i], 
					rxResponse.getData()[++i], rxResponse.getData()[++i], rxResponse.getData()[++i], 
					rxResponse.getData()[++i], rxResponse.getData()[++i], rxResponse.getData()[++i], 
					rxResponse.getData()[++i], rxResponse.getData()[++i], rxResponse.getData()[++i], 
					rxResponse.getData()[++i], rxResponse.getData()[++i], rxResponse.getData()[++i], 
					rxResponse.getData()[++i], rxResponse.getData()[++i], rxResponse.getData()[++i],
					rxResponse.getData()[++i], rxResponse.getData()[++i]);
			processData(UGateKeeperEvent.Type.WIRELESS_DATA_RX_SUCCESS, command, remoteAddress, sd, 
					RS.rbLabel("service.rx.settings", sd));
		} else {
			log.error("Unrecognized command: " + command);
		}
	}
	
	/**
	 * Processes data from a response and calls {@linkplain #handleEvent(UGateKeeperEvent)}
	 * 
	 * @param <V> the type of {@linkplain RxData}
	 * @param type the {@linkplain Type}
	 * @param command the {@linkplain Command}
	 * @param remoteAddress the remote address that is sending the data
	 * @param data the received data
	 * @param messages any messages that may have occurred
	 */
	protected <V extends RxData> void processData(final UGateKeeperEvent.Type type, final Command command, final String remoteAddress, 
			final V data, final String... messages) {
		if (messages != null && type == UGateKeeperEvent.Type.WIRELESS_DATA_RX_SUCCESS || 
				type == UGateKeeperEvent.Type.WIRELESS_DATA_TX_STATUS_RESPONSE_SUCCESS) {
			log.info(UGateUtil.toString(messages));
		} else if (messages != null) {
			log.warn(UGateUtil.toString(messages));
		}
		final Thread eventThread = new Thread(new Runnable() {
			@Override
			public void run() {
				final int nodeIndex = UGateKeeper.DEFAULT.wirelessGetAddressIndex(remoteAddress);
				final Map<Integer, String> addyMap = new HashMap<Integer, String>(1);
				addyMap.put(nodeIndex, remoteAddress);
				handleEvent(new UGateKeeperEvent<V>(this, type, addyMap, nodeIndex, null, command, null, data, messages));
			}
		}, UGateXBeePacketListener.class.getSimpleName() + "-event");
		eventThread.setDaemon(true);
		eventThread.start();
	}
	
	/**
	 * Handles {@linkplain UGateKeeperEvent}s extracted from packet data
	 * 
	 * @param <V> the type of {@linkplain RxData} values in the event
	 * @param event the event
	 */
	protected abstract <V extends RxData> void handleEvent(final UGateKeeperEvent<V> event);
	
	/**
	 * Extracts a command from the response (null if none can be found)
	 * 
	 * @param response the response
	 * @return the command
	 */
	protected static Command extractCommand(final XBeeResponse response) {
		try {
			return Command.lookup(response instanceof RxResponse16 ? ((RxResponse16) response).getData()[0] : response.getProcessedPacketBytes()[0]);
		} catch (final Throwable t) {
			log.error("Unable to extract command from: ", t);
		}
		return null;
	}
}
