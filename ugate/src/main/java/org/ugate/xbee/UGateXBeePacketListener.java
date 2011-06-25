package org.ugate.xbee;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.ugate.UGateKeeper;
import org.ugate.UGateUtil;
import org.ugate.gui.UGateGUI;

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
	private TrippedImage trippedImage;

	/**
	 * Process responses as they are received
	 * 
	 * @param response the XBee response
	 */
	@Override
	public void processResponse(XBeeResponse response) {
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
			final RxResponse16 rxResponse = (RxResponse16) response;
			final int command = rxResponse.getData()[0];
			if (command == UGateUtil.CMD_TAKE_QVGA_PIC || command == UGateUtil.CMD_TAKE_VGA_PIC) {
				final int feet = rxResponse.getData()[1];
				final int inches = rxResponse.getData()[2];
				if (feet <= 0 && inches <= 0) {
					if (trippedImage != null) {
						if (trippedImage.hasError) {
							log.warn("======= LOST PACKETS WHEN CAPTURING IMAGE... RETRYING... [RSSI: " + rxResponse.getRssi() + "] =======");
							trippedImage = null;
							UGateKeeper.DEFAULT.xbeeSendData(UGateKeeper.DEFAULT.GATE_XBEE_ADDRESS, new int[]{command});
						} else {
							try {
								TrippedImage.ImageFile imageFile = trippedImage.writeImageChunkData();
								log.info("======= (" + imageFile.fileSize + ") Total Image Bytes [RSSI: " + rxResponse.getRssi() + "] =======");
								UGateGUI.mediaPlayerDoorBell.play();
								UGateKeeper.DEFAULT.emailSend("UGate Tripped", trippedImage.toString(), 
										UGateKeeper.DEFAULT.preferences.get(UGateKeeper.MAIL_USERNAME_KEY), 
										UGateKeeper.DEFAULT.preferences.get(UGateKeeper.MAIL_RECIPIENTS_KEY, UGateKeeper.MAIL_RECIPIENTS_DELIMITER).toArray(new String[]{}), 
										imageFile.filePath);
								UGateGUI.mediaPlayerComplete.play();
							} catch (IOException e) {
								log.info("Cannot save image ID: " + UGateUtil.formatCal(trippedImage.created), e);
							} finally {
								trippedImage = null;
							}
							// TODO : org.eclipse.swt.SWTException: Invalid thread access for: 
							// UGateMain.getInstance().getUGate().refreshRecentTableViewerData();
						}
					} else {
						log.error("Received termination notice, but no tripped image exists!!!");
					}
					return;
				}
				if (trippedImage == null) {
					trippedImage = new TrippedImage(command, feet, inches);
					log.info("======= Receiving chunked image data (RSSI: " + rxResponse.getRssi() + ", " + trippedImage + ") =======");
					UGateGUI.mediaPlayerCam.play();
				}
				int[] imageChunk = trippedImage.addImageChunkData(rxResponse.getData(), 3);
				if (log.isDebugEnabled()) {
					log.debug("Sensor Tripped (RSSI: " + rxResponse.getRssi() + ", " + trippedImage + ", LENGTH: " + imageChunk.length + 
							", RAW LENGTH: " + rxResponse.getLength().getLength() + ") DATA: " + ByteUtils.toBase16(imageChunk));
				}
			} else {
				log.warn("Unrecognized command: " + command);
			}
		} else if (response instanceof TxStatusResponse) {
			final TxStatusResponse txResponse = (TxStatusResponse) response;
			if (txResponse.getStatus() == TxStatusResponse.Status.SUCCESS) {
				log.info("Transmission response: STATUS: " + txResponse.getStatus());
				UGateGUI.mediaPlayerBlip.play();
			} else {
				log.error("Transmission response: STATUS: " + txResponse.getStatus());
				UGateGUI.mediaPlayerError.play();
			}
		} else if (response instanceof ErrorResponse) {
			if (trippedImage != null) {
				trippedImage.hasError = true;
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
	}
}
