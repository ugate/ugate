package org.ugate.wireless.data;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ugate.Command;
import org.ugate.UGateEvent;
import org.ugate.UGateEvent.Type;
import org.ugate.UGateUtil;
import org.ugate.resources.RS;
import org.ugate.resources.RS.KEY;
import org.ugate.service.ServiceProvider;
import org.ugate.service.entity.RemoteNodeType;
import org.ugate.service.entity.jpa.RemoteNode;
import org.ugate.service.entity.jpa.RemoteNodeReading;
import org.ugate.wireless.data.RxData.Status;

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
	private final Map<String, RxTxImage> imgMap = new ConcurrentHashMap<>();

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
					processData(null, UGateEvent.Type.WIRELESS_DATA_TX_STATUS_RESPONSE_SUCCESS, command, rd, 
							RS.rbLabel(KEY.SERVICE_TX_RESPONSE_SUCCESS, rd, txResponse.getStatus()));
				} else {
					rd = new RxRawData<String>(null, Status.GENERAL_FAILURE, 0, rawBytes);
					processData(null, UGateEvent.Type.WIRELESS_DATA_TX_STATUS_RESPONSE_FAILED, command, rd, 
							RS.rbLabel(KEY.SERVICE_TX_RESPONSE_ERROR, rd, txResponse.getStatus()));
				}
			} else if (response instanceof ErrorResponse) {
				final Command command = extractCommand(response);
				final ErrorResponse errorResponse = (ErrorResponse) response;
				final String rawBytes = ByteUtils.toBase16(response.getRawPacketBytes());
				RxData rd;
				// TODO : need a way to determine what address the error came from for an ErrorResponse
//				if (imgMap.containsKey(errorResponse.getRemoteAddress())) {
//					imgMap.get(errorResponse.getRemoteAddress()).setStatus(RxData.Status.PARSING_ERROR);
//					rd = imgMap.get(errorResponse.getRemoteAddress()).createImageSegmentsSnapshot();
//					imgMap.remove(errorResponse.getRemoteAddress());
//				} else {
					rd = new RxRawData<String>(null, Status.GENERAL_FAILURE, 0, rawBytes);
//				}
				processData(null, UGateEvent.Type.WIRELESS_DATA_TX_STATUS_RESPONSE_FAILED, command, rd, 
						RS.rbLabel(KEY.SERVICE_TX_RESPONSE_ERROR, rd, errorResponse.getErrorMsg()));
				log.error("", errorResponse.getException());
			} else {
				final String rawBytes = ByteUtils.toBase16(response.getRawPacketBytes());
				int[] processedPacketBytes = response.getProcessedPacketBytes();
				if (processedPacketBytes != null && processedPacketBytes.length > 0) {
					/*
					 * for (int b : processedPacketBytes) { log.info(ByteUtils.formatByte(b)); }
					 */
					log.warn(String.format("Unused response type for \"%1$s\"... Incoming RAW Data: \"%2$s\"", 
							response, ByteUtils.toBase16(processedPacketBytes)));
					final RxData rd = new RxRawData<String>(null, Status.GENERAL_FAILURE, 0, rawBytes);
					processData(null, UGateEvent.Type.WIRELESS_DATA_TX_STATUS_RESPONSE_UNRECOGNIZED, null, rd, 
							RS.rbLabel(KEY.SERVICE_TX_RESPONSE_INVALID, rd, rawBytes));
				}
			}
		} catch (final Exception e) {
			log.error("An unexpected error occurred ", e);
		}
	}

	/**
	 * Finds an existing {@linkplain RemoteNode} from the
	 * {@linkplain RxResponse16#getRemoteAddress()}
	 * 
	 * @param rxResponse
	 *            the {@linkplain RxResponse16}
	 * @return the {@linkplain RemoteNode}
	 */
	protected RemoteNode findRemoteNode(final RxResponse16 rxResponse) {
		final String remoteAddress = Integer.toHexString(rxResponse.getRemoteAddress().getAddress()[0]) + 
				Integer.toHexString(rxResponse.getRemoteAddress().getAddress()[1]);
		if (remoteAddress.isEmpty()) {
			log.error(String .format("Received data from an unknown address %1$s... Discarding response...", 
					"NONE"));
			return null;
		}
		// TODO : RemoteNode should not be queried every time a response is received when multi-chunking data (like image) 
		final RemoteNode rn = ServiceProvider.IMPL.getWirelessService().findRemoteNodeByAddress(remoteAddress);
		if (rn == null) {
			log.error(String .format("Received data from an unknown address %1$s... Discarding response...", 
					remoteAddress));
			return null;
		}
		return rn;
	}
	/**
	 * Remote XBee radio used for gate operations using a 16-bit address: 3333
	 * (XBee must NOT be configured with "MY" set to FFFF)
	 * 
	 * @param rxResponse
	 *            the {@linkplain RxResponse16}
	 */
	protected void handleRxResponse16(final RxResponse16 rxResponse) {
		final RemoteNode rn = findRemoteNode(rxResponse);
		if (rn == null) {
			return;
		}
		final Command command = extractCommand(rxResponse);
		if (command == null) {
			log.error(String.format("An unrecognized %1$s command was received from %2$s", 
					rxResponse.getData()[0], rn.getAddress()));
			return;
		}
		final int failures = rxResponse.getData()[1]; // TODO : Handle cases where failures exist
		final RxData.Status status = failures == 0 ? RxData.Status.NORMAL : RxData.Status.GENERAL_FAILURE;
		log.info(String.format("======= Recieved %1$s command from wireless address %2$s (signal strength: %3$s) with (%4$s) failures =======", 
				command, rn.getAddress(), rxResponse.getRssi(), failures));
		if (command == Command.CAM_TAKE_PIC) {
			ImageCapture ic;
			RxTxImage rxTxImage = imgMap.containsKey(rn.getAddress()) ? imgMap.get(rn.getAddress()) : null;
			if (rxTxImage == null || rxTxImage.hasTimedOut()) {
				if (rxTxImage != null) {
					rxTxImage.resetRxTxAttempts();
					ic = rxTxImage.createImageSegmentsSnapshot();
					processData(rn, UGateEvent.Type.WIRELESS_DATA_RX_FAILED, command, ic, 
							RS.rbLabel(KEY.SERVICE_RX_IMAGE_TIMEOUT, ic));
				}
				// TODO : add check for what sensor tripped the image and image format detection (instead of using just JPEG)
				rxTxImage = new RxTxJPEG(rn, status, rxResponse.getRssi(), null);
				imgMap.put(rn.getAddress(), rxTxImage);
				ic = rxTxImage.createImageSegmentsSnapshot();
				log.info(String.format("======= Receiving chunked image data (%1$s) =======", rxTxImage));
				processData(rn, UGateEvent.Type.WIRELESS_DATA_RX_MULTIPART, command, ic, 
						RS.rbLabel(KEY.SERVICE_RX_IMAGE_MULTPART, ic));
			}
			int[] imageChunk = rxTxImage.addImageSegment(rxResponse.getData(), IMAGE_START_INDEX);
			if (log.isDebugEnabled()) {
				log.debug(String.format("Sensor Tripped (%1$s, LENGTH: %2$s, RAW LENGTH: %3$s) DATA: %4$s", 
						rxTxImage, imageChunk.length, rxResponse.getLength().getLength(), ByteUtils.toBase16(imageChunk)));
			}
			if (rxTxImage.isEof()) {
				if (rxTxImage.getStatus() != RxData.Status.NORMAL) {
					final int retries = rn.getCamImgCaptureRetryCnt();
					ic = rxTxImage.createImageSegmentsSnapshot();
					if (retries != 0 && rxTxImage.getRxTxAttempts() <= retries) {
						rxTxImage.incRxTxAttempts();
						processData(rn, UGateEvent.Type.WIRELESS_DATA_RX_FAILED_RETRYING, command, ic, 
								RS.rbLabel(KEY.SERVICE_RX_IMAGE_LOST_PACKETS_RETRY, ic, rxTxImage.getRxTxAttempts(), retries));
						ServiceProvider.IMPL.getWirelessService().sendData(rn, command, 0, false);
					} else {
						try {
							processData(rn, UGateEvent.Type.WIRELESS_DATA_RX_FAILED, command, ic, 
									RS.rbLabel(KEY.SERVICE_RX_IMAGE_LOST_PACKETS, ic, rxTxImage.getRxTxAttempts()));
						} finally {
							imgMap.remove(rn.getAddress());
						}
					}
				} else {
					try {
						ic = rxTxImage.writeImageSegments();
						processData(rn, UGateEvent.Type.WIRELESS_DATA_RX_SUCCESS, command, ic, 
								RS.rbLabel(KEY.SERVICE_RX_IMAGE_SUCCESS, ic));
					} catch (IOException e) {
						log.info("Cannot save image ID: " + UGateUtil.calFormat(rxTxImage.getCreatedTime()), e);
					} finally {
						imgMap.remove(rn.getAddress());
					}
				}
			}
		} else if (command == Command.ACCESS_PIN_CHANGE) {
			//final int hasFailures = rxResponse.getData()[1];
			final KeyCodes kc = new KeyCodes(rn, status, rxResponse.getRssi(), rxResponse.getData()[1], 
					rxResponse.getData()[2], rxResponse.getData()[3]);
			processData(rn, UGateEvent.Type.WIRELESS_DATA_RX_SUCCESS, command, kc, 
					RS.rbLabel(KEY.SERVICE_RX_KEYCODES, kc));
		} else if (command == Command.SERVO_LASER_CALIBRATE) {
			processData(rn, UGateEvent.Type.WIRELESS_DATA_RX_SUCCESS, command,  
					new RxRawData<Void>(rn, status, rxResponse.getRssi(), null), 
					failures > 0 ? RS.rbLabel(KEY.LASER_CALIBRATION_FAILED) : RS.rbLabel(KEY.LASER_CALIBRATION_SUCCESS));
		} else if (command == Command.SENSOR_GET_READINGS) {
			log.info("=== Sensor Readings received ===");
			int i = 1;
			final RemoteNodeReading rnr = new RemoteNodeReading();
			rnr.setRemoteNode(rn);
			rnr.setReadDate(new Date());
			rnr.setSonarFeet(rxResponse.getData()[++i]);
			rnr.setSonarInches(rxResponse.getData()[++i]);
			rnr.setMicrowaveCycleCount(rxResponse.getData()[++i]);
			rnr.setPirIntensity(rxResponse.getData()[++i]);
			rnr.setLaserFeet(rxResponse.getData()[++i]);
			rnr.setLaserInches(rxResponse.getData()[++i]);
			rnr.setGateState(rxResponse.getData()[++i]);
			final RxTxRemoteNodeReadingDTO sr = new RxTxRemoteNodeReadingDTO(rnr, status, rxResponse.getRssi());
			processData(rn, UGateEvent.Type.WIRELESS_DATA_RX_SUCCESS, command, sr, 
					RS.rbLabel(KEY.SERVICE_RX_READINGS, sr));
		} else if (command == Command.SENSOR_GET_SETTINGS) {
			// the number of response data and their order is important!!!
			int i = 1;
			final int[] sd = new int[RemoteNodeType.canRemoteCount()];
			for (int j = 0; j<RemoteNodeType.canRemoteCount(); j++) {
				sd[j] = rxResponse.getData()[++i];
			}
			// create a detached state remote node w/o modifying the existing local instance
			final RemoteNode rnFromRemote = RemoteNodeType.newDefaultRemoteNode(rn.getHost());
			final RxTxRemoteNodeDTO dto = new RxTxRemoteNodeDTO(rnFromRemote, status,
					rxResponse.getRssi(), sd);
			processData(rn, UGateEvent.Type.WIRELESS_DATA_RX_SUCCESS, command, dto, 
					RS.rbLabel(KEY.SERVICE_RX_SETTINGS, dto));
		} else if (command == Command.GATE_TOGGLE_OPEN_CLOSE) {
			
		} else {
			log.error("Unrecognized command: " + command);
		}
	}
	
	/**
	 * Processes data from a response and calls
	 * {@linkplain #handleEvent(UGateEvent)}
	 * 
	 * @param <V>
	 *            the type of {@linkplain RxData}
	 * @param remoteNode
	 *            the {@linkplain RemoteNode}
	 * @param type
	 *            the {@linkplain Type}
	 * @param command
	 *            the {@linkplain Command}
	 * @param data
	 *            the received data
	 * @param messages
	 *            any messages that may have occurred
	 */
	protected <V extends RxData> void processData(final RemoteNode remoteNode,
			final UGateEvent.Type type, final Command command,
			final V data, final String... messages) {
		if (messages != null && type == UGateEvent.Type.WIRELESS_DATA_RX_SUCCESS || 
				type == UGateEvent.Type.WIRELESS_DATA_TX_STATUS_RESPONSE_SUCCESS) {
			log.info(UGateUtil.toString(messages));
		} else if (messages != null) {
			log.warn(UGateUtil.toString(messages));
		}
		final Thread eventThread = new Thread(new Runnable() {
			@Override
			public void run() {
				handleEvent(new UGateEvent<RemoteNode, V>(
						remoteNode, type, true, null,
						command, null, data, messages));
			}
		}, UGateXBeePacketListener.class.getSimpleName() + "-event");
		eventThread.setDaemon(true);
		eventThread.start();
	}
	
	/**
	 * Handles {@linkplain UGateEvent}s extracted from packet data
	 * 
	 * @param <V> the type of {@linkplain RxData} values in the event
	 * @param event the event
	 */
	protected abstract <V extends RxData> void handleEvent(final UGateEvent<RemoteNode, V> event);
	
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
