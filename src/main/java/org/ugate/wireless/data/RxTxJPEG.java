package org.ugate.wireless.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.ugate.ByteUtils;
import org.ugate.Command;
import org.ugate.UGateUtil;

public class RxTxJPEG extends WirelessResponse<List<org.ugate.wireless.data.RxTxImage.ImageChunk>> 
	implements RxTxImage<List<org.ugate.wireless.data.RxTxImage.ImageChunk>> {
	
	private static final Logger log = Logger.getLogger(RxTxJPEG.class);
	public static final String JPEG_EXT = "jpg";
	public static final String JPEG_EOF = "0xff,0xd9";
	
	public RxTxJPEG(final Command command, final WirelessStatusCode statusCode, final List<ImageChunk> data) {
		super(command, statusCode, (data == null ? new ArrayList<ImageChunk>() : data));
		log.debug("NEW " + this);
	}
	
	@Override
	public int[] addImageSegment(int[] data, int startIndex) {
		final ImageChunk imageChunk = new ImageChunk(data, startIndex, data.length - startIndex);
		getData().add(imageChunk);
		return imageChunk.data;
	}
	
	public ImageFile writeImageSegments() throws IOException {
		try {
			final Calendar ended = Calendar.getInstance();
			int[] imageData = null;
			for (ImageChunk imageChunk : getData()) {
				if (imageData == null) {
					imageData = imageChunk.data;
				} else {
					imageData = concatArray(imageData, imageChunk.data);
				}
			}
			ByteBuffer byteBuffer = ByteBuffer.allocate(imageData.length);
			for (int value : imageData) {
				// convert uint8_t to java integer
				//int byteValue = value & 0xFF;
				//byteBuffer.putInt(value);
				byteBuffer.put((byte) value);
			}
			// TODO : dynamically set the path to the images
			final String filePath = "C:\\ugate\\" + UGateUtil.calFormat(getCreated()).replaceAll(":", "-") + '.' + JPEG_EXT;
			if (log.isInfoEnabled()) {
				log.info("Writting (" + imageData.length + ") bytes from (" + getData().size() + ") image chunks to \"" + filePath + "\" (took: " + 
						UGateUtil.calFormatDateDifference(getCreated().getTime(), ended.getTime()) + ')');
			}
			writeImage(byteBuffer.array(), filePath);
			return new ImageFile(filePath, imageData.length);
		} finally {
			setData(new ArrayList<RxTxJPEG.ImageChunk>());
		}
	}
	
	private void writeImage(byte[] bytes, String filePath) throws IOException {
		File imageFile = new File(filePath);
		if (!imageFile.exists()) {
			imageFile.createNewFile();
		}
		FileOutputStream fos = new FileOutputStream(imageFile);
		try {
			fos.write(bytes);
		} finally {
			fos.close();
		}
	}
	
	public static int[] concatArray(int[] original, int[] appender) {
		int[] result = Arrays.copyOf(original, original.length + appender.length);
		System.arraycopy(appender, 0, result, original.length, appender.length);
		return result;
	}
	
	/**
	 * @return true when the JPEG
	 */
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
			final String eofHex = ByteUtils.toBase16(new int[]{secondToLastByte, lastByte});
			return eofHex.toLowerCase().equals(JPEG_EOF);
		} catch (Exception e) {
			log.error("Unable to get EOF sequence", e);
		}
		setStatusCode(WirelessStatusCode.GENERAL_FAILURE);
		return true;
	}
}
