package org.ugate.xbee;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.ugate.UGateUtil;

public class TrippedImage {
	
	private static final Logger log = Logger.getLogger(TrippedImage.class);
	public final Calendar created;
	public final int feet;
	public final int inches;
	public final int initCommandASCII;
	public boolean hasError = false;
	private ArrayList<ImageChunk> imageChunks = new ArrayList<ImageChunk>();
	
	public TrippedImage(int initCommandASCII, int feet, int inches) {
		this.created = Calendar.getInstance();
		this.initCommandASCII = initCommandASCII;
		this.feet = feet;
		this.inches = inches;
		log.debug("NEW " + this);
	}
	
	@Override
	public String toString() {
		return "CREATED: " + UGateUtil.formatCal(this.created) + ", " + this.feet + "' " + this.inches + '"';
	}
	
	public int[] addImageChunkData(int[] data, int startIndex) {
		final ImageChunk imageChunk = new ImageChunk(data, startIndex, data.length - startIndex);
		imageChunks.add(imageChunk);
		return imageChunk.data;
	}
	
	public ImageFile writeImageChunkData() throws IOException {
		try {
			final Calendar ended = Calendar.getInstance();
			int[] imageData = null;
			for (ImageChunk imageChunk : imageChunks) {
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
			final String filePath = "C:\\ugate\\" + UGateUtil.formatCal(created).replaceAll(":", "-") + ".jpg";
			if (log.isInfoEnabled()) {
				log.info("Writting (" + imageData.length + ") bytes from (" + imageChunks.size() + ") image chunks to \"" + filePath + "\" (took: " + 
						UGateUtil.formatDateDifference(created.getTime(), ended.getTime()) + ')');
			}
			writeImage(byteBuffer.array(), filePath);
			return new ImageFile(filePath, imageData.length);
		} finally {
			imageChunks = new ArrayList<TrippedImage.ImageChunk>();
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
		/*
		InputStream in = new ByteArrayInputStream(byteBuffer.array());
		try {
			BufferedImage bufferedImage = ImageIO.read(in);
			File imageFile = new File("C:\\ugate\\" + id.replaceAll(":", "-") + ".jpg");
			
			ImageIO.write(bufferedImage, "jpg", imageFile); 
		} catch (Exception e) {
			in.close();
		}*/
	}
	
	public static int[] concatArray(int[] original, int[] appender) {
		int[] result = Arrays.copyOf(original, original.length + appender.length);
		System.arraycopy(appender, 0, result, original.length, appender.length);
		return result;
	}
	
	public class ImageFile {
		public final String filePath;
		public final int fileSize;
		public ImageFile(String filePath, int fileSize) {
			this.filePath = filePath;
			this.fileSize = fileSize;
		}
	}
	
	private class ImageChunk {
		public int[] data;
		public ImageChunk(int[] data, int startIndex, int length) {
			this.data = Arrays.copyOfRange(data, startIndex, length + startIndex);
			//log.debug("####### Image Chunk: " + this.data.length + " bytes");
		}
	}
}
