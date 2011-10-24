package org.ugate.wireless.xbee;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.ugate.UGateKeeper;
import org.ugate.UGateUtil;
import org.ugate.gui.UGateGUI;
import org.ugate.wireless.data.RxTxImage;
import org.ugate.wireless.data.RxTxJPEG;
import org.ugate.wireless.data.SensorReadings;

import com.rapplogic.xbee.api.ErrorResponse;
import com.rapplogic.xbee.api.PacketListener;
import com.rapplogic.xbee.api.XBeeResponse;
import com.rapplogic.xbee.api.wpan.RxResponse16;
import com.rapplogic.xbee.api.wpan.TxStatusResponse;
import com.rapplogic.xbee.util.ByteUtils;

/**
 * XBee packet listener
 */
public class UGateXBeePacketListener implements PacketListener {

	private static final Logger log = Logger.getLogger(UGateXBeePacketListener.class);
	/**
	 * The index of the image start byte
	 */
	public static final int IMAGE_START_INDEX = 7;
	volatile private RxTxImage rxTxImage;

	/**
	 * Process responses as they are received
	 * 
	 * @param response the XBee response
	 */
	@Override
	public void processResponse(XBeeResponse response) {
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
				final TxStatusResponse txResponse = (TxStatusResponse) response;
				if (txResponse.getStatus() == TxStatusResponse.Status.SUCCESS) {
					log.debug("Transmission response: STATUS: " + txResponse.getStatus());
					UGateGUI.mediaPlayerBlip.play();
				} else {
					log.error("Transmission response: STATUS: " + txResponse.getStatus());
					UGateGUI.mediaPlayerError.play();
				}
			} else if (response instanceof ErrorResponse) {
				if (rxTxImage != null) {
					rxTxImage.setHasError(true);
				}
				log.info("Incoming RAW Bytes: \"" + ByteUtils.toBase16(response.getRawPacketBytes()) + '"');
				final ErrorResponse errorResponse = (ErrorResponse) response;
				log.error("ERROR " + errorResponse.toString(), errorResponse.getException());
				UGateGUI.mediaPlayerError.play();
			} else {
				int[] processedPacketBytes = response.getProcessedPacketBytes();
				if (processedPacketBytes != null && processedPacketBytes.length > 0) {
					/*
					 * for (int b : processedPacketBytes) { log.info(ByteUtils.formatByte(b)); }
					 */
					log.warn("Unused response type for \"" + response + "\"... Incoming RAW Data: \"" + ByteUtils.toBase16(processedPacketBytes) + '"');
					UGateGUI.mediaPlayerConfirm.play();
				}
			}
		} catch (Exception e) {
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
		final int command = rxResponse.getData()[0];
		final int failures = rxResponse.getData()[1]; // TODO : Handle cases where failures exist
		log.info(String.format("Recieved {0} command from wireless address {1} (signal strength: {2}) with {3} failures", 
				command, remoteAddress, rxResponse.getRssi(), failures));
		if (command == UGateUtil.CMD_CAM_TAKE_PIC) {
			if (rxTxImage == null || rxTxImage.hasTimedOut()) {
				if (rxTxImage != null) {
					log.warn("======= Last image capture timed out while recieving data... Starting new image capture =======");
				}
				rxTxImage = new RxTxJPEG(command, createSensorReadings(rxResponse));
				log.info("======= Receiving chunked image data (" + rxTxImage + ") =======");
				UGateGUI.mediaPlayerCam.play();
			}
			int[] imageChunk = rxTxImage.addImageSegment(rxResponse.getData(), IMAGE_START_INDEX);
			if (log.isDebugEnabled()) {
				log.debug("Sensor Tripped (" + rxTxImage + ", LENGTH: " + imageChunk.length + 
						", RAW LENGTH: " + rxResponse.getLength().getLength() + ") DATA: " + ByteUtils.toBase16(imageChunk));
			}
			if (rxTxImage.isEof()) {
				if (rxTxImage.getHasError()) {
					log.warn("======= LOST PACKETS WHEN CAPTURING IMAGE... RETRYING... =======");
					rxTxImage = null;
					UGateKeeper.DEFAULT.wirelessSendData(remoteAddress, new int[]{command});
				} else {
					try {
						RxTxJPEG.ImageFile imageFile = rxTxImage.writeImageSegments();
						log.info("======= (" + imageFile.fileSize + ") Total Image Bytes =======");
						UGateGUI.mediaPlayerDoorBell.play();
						/*UGateKeeper.DEFAULT.emailSend("UGate Tripped", trippedImage.toString(), 
								UGateKeeper.DEFAULT.preferences.get(UGateKeeper.MAIL_USERNAME_KEY), 
								UGateKeeper.DEFAULT.preferences.get(UGateKeeper.MAIL_RECIPIENTS_KEY, UGateKeeper.MAIL_RECIPIENTS_DELIMITER).toArray(new String[]{}), 
								imageFile.filePath);
						*/UGateGUI.mediaPlayerComplete.play();
					} catch (IOException e) {
						log.info("Cannot save image ID: " + UGateUtil.formatCal(rxTxImage.getCreated()), e);
					} finally {
						rxTxImage = null;
					}
					// TODO : org.eclipse.swt.SWTException: Invalid thread access for: 
					// UGateMain.getInstance().getUGate().refreshRecentTableViewerData();
				}
			}
		} else if (command == UGateUtil.CMD_ACCESS_CODE_CHANGE) {
			//final int hasFailures = rxResponse.getData()[1];
			final int keys[] =  new int[3];
			keys[0] = rxResponse.getData()[1];
			keys[1] = rxResponse.getData()[2];
			keys[2] = rxResponse.getData()[3];
			log.info("Access keys: " + keys[0] + ',' + keys[1] + ',' + keys[2]);
		} else if (command == UGateUtil.CMD_SENSOR_GET_READINGS) {
			final SensorReadings sr = createSensorReadings(rxResponse);
			log.info(String.format("=== Sensor Readings: %1$s ===", sr));
		} else if (command == UGateUtil.CMD_SENSOR_GET_SETTINGS) {
			int i = 1;
			log.info(String.format("=== Settings for %s START ===", remoteAddress));
			log.info(String.format("Access Key Code: %1$s, %2$s, %3$s", 
					rxResponse.getData()[++i], rxResponse.getData()[++i], rxResponse.getData()[++i]));
			log.info(String.format("Camera image resolution used when taking pictures (0=QVGA, 1=VGA): %1$s", 
					rxResponse.getData()[++i]));
			log.info(String.format("State of the alarms (0=off, 1=on): Sonar=%1$s, IR=%2$s, Microwave=%3$s, Gate=%4$s", 
					rxResponse.getData()[++i], rxResponse.getData()[++i], rxResponse.getData()[++i], rxResponse.getData()[++i]));
			log.info(String.format("Sonar distance threshold before trip: feet=%1$s, inches=%2$s, delay between trips=%3$s", 
					rxResponse.getData()[++i], rxResponse.getData()[++i], rxResponse.getData()[++i]));
			log.info(String.format("IR distance threshold before trip: feet=%1$s, inches=%2$s, delay between trips=%3$s", 
					rxResponse.getData()[++i], rxResponse.getData()[++i], rxResponse.getData()[++i]));
			log.info(String.format("Microwave speed threshold before trip: cycles/second=%1$s, delay between trips=%2$s", 
					rxResponse.getData()[++i], rxResponse.getData()[++i]));
			log.info(String.format("Multi-alarm trip state (0=Any sensor that is tripped will signal alarm, 1=Sonar and " +
						"IR have to be tripped in order to signal alarm, 2=Sonar and Microwave have to be tripped in order to " + 
						"signal alarm, 3=IR and Microwave have to be tripped in order to signal alarm, 4=Sonar, IR, and Microwave " + 
						"have to be tripped in order to signal alarm): %1$s", 
					rxResponse.getData()[++i]));
			log.info(String.format("=== Settings for %s END ===", remoteAddress));
		} else {
			log.warn("Unrecognized command: " + command);
		}
	}
	
	/**
	 * Creates sensor readings from a RX response
	 * @param rxResponse the response that contains the readings
	 * @return the sensor readings
	 */
	protected SensorReadings createSensorReadings(final RxResponse16 rxResponse) {
		return new SensorReadings(rxResponse.getData()[2], 
				rxResponse.getData()[3], rxResponse.getData()[4], 
				rxResponse.getData()[5], rxResponse.getData()[6]);
	}
}
