package org.ugate;

import java.io.IOException;
import java.util.LinkedHashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ugate.UGateKeeperEvent.Type;
import org.ugate.resources.RS;
import org.ugate.resources.RS.KEYS;
import org.ugate.service.ServiceProvider;
import org.ugate.service.entity.RemoteNodeType;
import org.ugate.service.entity.jpa.RemoteNode;
import org.ugate.wireless.data.ImageCapture;
import org.ugate.wireless.data.KeyCodes;
import org.ugate.wireless.data.RxData;
import org.ugate.wireless.data.RxData.Status;
import org.ugate.wireless.data.RxRawData;
import org.ugate.wireless.data.RxTxImage;
import org.ugate.wireless.data.RxTxJPEG;
import org.ugate.wireless.data.RxTxRemoteNodeDTO;
import org.ugate.wireless.data.RxTxSensorReadings;

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

	private static final Logger log = LoggerFactory.getLogger(UGateXBeePacketListener.class);
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
							RS.rbLabel(KEYS.SERVICE_TX_RESPONSE_SUCCESS, rd, txResponse.getStatus()));
				} else {
					rd = new RxRawData<String>(null, Status.GENERAL_FAILURE, 0, rawBytes);
					processData(UGateKeeperEvent.Type.WIRELESS_DATA_TX_STATUS_RESPONSE_FAILED, command, null, rd, 
							RS.rbLabel(KEYS.SERVICE_TX_RESPONSE_ERROR, rd, txResponse.getStatus()));
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
						RS.rbLabel(KEYS.SERVICE_TX_RESPONSE_ERROR, rd, errorResponse.getErrorMsg()));
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
							RS.rbLabel(KEYS.SERVICE_TX_RESPONSE_INVALID, rd, rawBytes));
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
		// TODO : RemoteNode should not be queried every time a response is received when multi-chunking data (like image) 
		final RemoteNode rn = ServiceProvider.IMPL.getWirelessService().findRemoteNodeByAddress(remoteAddress);
		if (rn == null) {
			log.error(String .format("Received data from an unknown address %1$s... Discarding response...", remoteAddress));
			return;
		}
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
							RS.rbLabel(KEYS.SERVICE_RX_IMAGE_TIMEOUT, ic));
				}
				// TODO : add check for what sensor tripped the image and image format detection (instead of using just JPEG)
				rxTxImage = new RxTxJPEG(rn, status, rxResponse.getRssi(), null);
				ic = rxTxImage.createImageSegmentsSnapshot();
				log.info(String.format("======= Receiving chunked image data (%1$s) =======", rxTxImage));
				processData(UGateKeeperEvent.Type.WIRELESS_DATA_RX_MULTIPART, command, remoteAddress, ic, 
						RS.rbLabel(KEYS.SERVICE_RX_IMAGE_MULTPART, ic));
			}
			int[] imageChunk = rxTxImage.addImageSegment(rxResponse.getData(), IMAGE_START_INDEX);
			if (log.isDebugEnabled()) {
				log.debug(String.format("Sensor Tripped (%1$s, LENGTH: %2$s, RAW LENGTH: %3$s) DATA: %4$s", 
						rxTxImage, imageChunk.length, rxResponse.getLength().getLength(), ByteUtils.toBase16(imageChunk)));
			}
			if (rxTxImage.isEof()) {
				if (rxTxImage.getStatus() != RxData.Status.NORMAL) {
					final int retries = rn.getCamImgCaptureRetryCnt();
					rxTxImage = null;
					ic = rxTxImage.createImageSegmentsSnapshot();
					if (retries != 0 && rxTxAttempts <= retries) {
						rxTxAttempts++;
						processData(UGateKeeperEvent.Type.WIRELESS_DATA_RX_FAILED_RETRYING, command, remoteAddress, ic, 
								RS.rbLabel(KEYS.SERVICE_RX_IMAGE_LOST_PACKETS_RETRY, ic, rxTxAttempts, retries));
						ServiceProvider.IMPL.getWirelessService().sendData(rn, command);
					} else {
						processData(UGateKeeperEvent.Type.WIRELESS_DATA_RX_FAILED, command, remoteAddress, ic, 
								RS.rbLabel(KEYS.SERVICE_RX_IMAGE_LOST_PACKETS, ic, rxTxAttempts));
					}
				} else {
					try {
						ic = rxTxImage.writeImageSegments();
						processData(UGateKeeperEvent.Type.WIRELESS_DATA_RX_SUCCESS, command, remoteAddress, ic, 
								RS.rbLabel(KEYS.SERVICE_RX_IMAGE_SUCCESS, ic));
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
			final KeyCodes kc = new KeyCodes(rn, status, rxResponse.getRssi(), rxResponse.getData()[1], 
					rxResponse.getData()[2], rxResponse.getData()[3]);
			processData(UGateKeeperEvent.Type.WIRELESS_DATA_RX_SUCCESS, command, remoteAddress, kc, 
					RS.rbLabel(KEYS.SERVICE_RX_KEYCODES, kc));
		} else if (command == Command.SERVO_LASER_CALIBRATE) {
			processData(UGateKeeperEvent.Type.WIRELESS_DATA_RX_SUCCESS, command, remoteAddress, 
					new RxRawData<Void>(rn, status, rxResponse.getRssi(), null), 
					failures > 0 ? RS.rbLabel(KEYS.LASER_CALIBRATION_FAILED) : RS.rbLabel(KEYS.LASER_CALIBRATION_SUCCESS));
		} else if (command == Command.SENSOR_GET_READINGS) {
			log.info("=== Sensor Readings received ===");
			int i = 1;
			final RxTxSensorReadings sr = new RxTxSensorReadings(rn, status, rxResponse.getRssi(), 
					rxResponse.getData()[++i], rxResponse.getData()[++i], rxResponse.getData()[++i], 
					rxResponse.getData()[++i], rxResponse.getData()[++i], rxResponse.getData()[++i]);
			processData(UGateKeeperEvent.Type.WIRELESS_DATA_RX_SUCCESS, command, remoteAddress, sr, 
					RS.rbLabel(KEYS.SERVICE_RX_READINGS, sr));
		} else if (command == Command.SENSOR_GET_SETTINGS) {
			// the number of response data and their order is important!
			int i = 1;
			final int[] sd = new int[RemoteNodeType.canRemoteCount()];
			for (int j = 0; j<RemoteNodeType.canRemoteCount(); j++) {
				sd[j] = rxResponse.getData()[++i];
			}
			final RxTxRemoteNodeDTO dto = new RxTxRemoteNodeDTO(rn, status,
					rxResponse.getRssi(), sd);
			processData(UGateKeeperEvent.Type.WIRELESS_DATA_RX_SUCCESS, command, remoteAddress, dto, 
					RS.rbLabel(KEYS.SERVICE_RX_SETTINGS, dto));
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
				final LinkedHashSet<String> addys = new LinkedHashSet<>(1);
				addys.add(remoteAddress);
				handleEvent(new UGateKeeperEvent<V>(this, type, true, addys, null, command, null, data, messages));
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
