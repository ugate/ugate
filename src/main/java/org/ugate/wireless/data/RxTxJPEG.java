package org.ugate.wireless.data;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ugate.ByteUtils;
import org.ugate.service.entity.jpa.RemoteNode;

/**
 * JPEG Image response that requires multiple received image transmissions
 * chunks before a JPEG image can be assembled/written
 */
public class RxTxJPEG extends RxTxImage {

	private static final Logger log = LoggerFactory.getLogger(RxTxJPEG.class);
	public static final String JPEG_EXT = "jpg";
	public static final String JPEG_EOF = "0xff,0xd9";

	/**
	 * Constructor
	 * 
	 * @param remoteNode
	 *            the {@linkplain RemoteNode}
	 * @param status
	 *            the {@linkplain Status}
	 * @param signalStrength
	 *            the signal strength
	 * @param data
	 *            the image chunk data
	 */
	public RxTxJPEG(final RemoteNode remoteNode, final Status status,
			final int signalStrength, final List<ImageChunk> data) {
		super(remoteNode, status, signalStrength,
				(data == null ? new ArrayList<ImageChunk>() : data));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEof() {
		try {
			final ImageChunk last = getData().get(getData().size() - 1);
			int lastByte = last.data[last.data.length - 1];
			int secondToLastByte;
			if (last.data.length < 2) {
				// in the rare case that the last data added has only one byte
				final ImageChunk last2 = getData().get(getData().size() - 2);
				secondToLastByte = last2.data[last2.data.length - 1];
			} else {
				secondToLastByte = last.data[last.data.length - 2];
			}
			final String eofHex = ByteUtils.toBase16(new int[] {
					secondToLastByte, lastByte });
			return eofHex.toLowerCase().equals(JPEG_EOF);
		} catch (Exception e) {
			log.error("Unable to get EOF sequence", e);
		}
		setStatus(Status.GENERAL_FAILURE);
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getImageExtension() {
		return JPEG_EXT;
	}
}
