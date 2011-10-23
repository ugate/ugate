package org.ugate.wireless.data;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;

public interface RxTxImage {

	/**
	 * Adds a segment of image data to the image
	 * 
	 * @param data the segment of image data
	 * @param startIndex the start index to use for the data 
	 * 					(in case data has unrelated preceding bytes)
	 * @return the new image segment added
	 */
	int[] addImageSegment(int[] data, int startIndex);
	
	/**
	 * Writes all the previously added image chunk data to an image file
	 * 
	 * @return the newly written image file
	 * @throws IOException thrown if an error occurs when writing the image to disk
	 */
	ImageFile writeImageSegments() throws IOException;
	
	/**
	 * @return true when it has been determined that the last added image segments 
	 * 		contains an end of file termination character(s)
	 */
	boolean isEof();
	
	/**
	 * @return true if an error exists in the image segment sequence
	 */
	boolean getHasError();
	
	/**
	 * @param hasError set true if an error exists in the image segment sequence
	 */
	void setHasError(boolean hasError);
	
	/**
	 * @return date/time the first image segment was added
	 */
	Calendar getCreated();
	
	/**
	 * @return the sensor readings read when the image was taken
	 */
	SensorReadings getSensorReadings();
	
	/**
	 * @return true when the image segments have timed out from lack of activity
	 */
	public boolean hasTimedOut();
	
	public class ImageFile {
		public final String filePath;
		public final int fileSize;
		public ImageFile(String filePath, int fileSize) {
			this.filePath = filePath;
			this.fileSize = fileSize;
		}
	}
	public class ImageChunk {
		public int[] data;
		public ImageChunk(int[] data, int startIndex, int length) {
			this.data = Arrays.copyOfRange(data, startIndex, length + startIndex);
			//log.debug("####### Image Chunk: " + this.data.length + " bytes");
		}
	}
}
