package org.ugate.wireless.data;

import java.io.IOException;
import java.util.Arrays;

/**
 * Wireless receive / transmit image
 */
public interface RxTxImage<T> extends IResponse<T> {

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
	 * Image file data
	 */
	public class ImageFile {
		public final String filePath;
		public final int fileSize;
		public ImageFile(String filePath, int fileSize) {
			this.filePath = filePath;
			this.fileSize = fileSize;
		}
	}
	
	/**
	 * Image chunk that represents a portion of an overall image
	 */
	public class ImageChunk {
		public int[] data;
		public ImageChunk(int[] data, int startIndex, int length) {
			this.data = Arrays.copyOfRange(data, startIndex, length + startIndex);
			//log.debug("####### Image Chunk: " + this.data.length + " bytes");
		}
	}
}
