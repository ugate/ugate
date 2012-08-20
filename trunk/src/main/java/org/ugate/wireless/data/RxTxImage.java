package org.ugate.wireless.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ugate.UGateUtil;
import org.ugate.resources.RS;
import org.ugate.service.entity.jpa.RemoteNode;

/**
 * Image response that requires multiple received image transmissions chunks
 * before an image can be assembled/written
 */
public abstract class RxTxImage extends MultiRxData<List<RxTxImage.ImageChunk>> {

	private static final Logger log = LoggerFactory.getLogger(RxTxImage.class);
	private Calendar endTime = null;

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
	public RxTxImage(final RemoteNode remoteNode, final Status status,
			final int signalStrength, final List<RxTxImage.ImageChunk> data) {
		super(remoteNode, status, signalStrength,
				(data == null ? new ArrayList<RxTxImage.ImageChunk>() : data));
		log.debug("NEW " + this);
	}

	/**
	 * @return true when it has been determined that the last added image
	 *         segments contains an end of file termination character(s)
	 */
	public abstract boolean isEof();

	/**
	 * @return the file extension fo the image
	 */
	public abstract String getImageExtension();

	/**
	 * @return the current bytes of for the image chunks
	 */
	public ByteBuffer getBytes() {
		if (getData() == null) {
			return null;
		}
		int[] imageData = null;
		for (final ImageChunk imageChunk : getData()) {
			if (imageData == null) {
				imageData = imageChunk.data;
			} else {
				imageData = UGateUtil
						.arrayConcatInt(imageData, imageChunk.data);
			}
		}
		if (imageData == null) {
			return null;
		}
		final ByteBuffer byteBuffer = ByteBuffer.allocate(imageData.length);
		for (final int value : imageData) {
			// convert uint8_t to java integer
			// int byteValue = value & 0xFF;
			// byteBuffer.putInt(value);
			byteBuffer.put((byte) value);
		}
		return byteBuffer;
	}

	/**
	 * @return the file {@linkplain Path} to where the image will/has been
	 *         written
	 */
	public Path getImagePath() {
		// return "C:\\ugate\\" +
		// UGateUtil.calFormat(getCreated()).replaceAll(":", "-") + '.' +
		// getImageExtension();
		final String imgFileName = getCreatedTimeString().replaceAll(":", "-")
				+ '.' + getImageExtension();
		final Path imgRootPath = RS.workingDirectoryPath(
				Paths.get(getRemoteNode().getWorkingDir()), null);
		return Paths.get(imgRootPath.toAbsolutePath().toString(), imgFileName);
	}

	/**
	 * Adds a segment of image data to the image
	 * 
	 * @param data
	 *            the segment of image data
	 * @param startIndex
	 *            the start index to use for the data (in case data has
	 *            unrelated preceding bytes)
	 * @throws IllegalStateException
	 *             when the image has already been assembled/written
	 * @return the new image segment added
	 */
	public int[] addImageSegment(int[] data, int startIndex)
			throws IllegalStateException {
		if (endTime != null) {
			throw new IllegalStateException("Image has already been written to");
		}
		final ImageChunk imageChunk = new ImageChunk(data, startIndex,
				data.length - startIndex);
		getData().add(imageChunk);
		return imageChunk.data;
	}

	/**
	 * Writes all the previously added image chunk data to an image file
	 * 
	 * @return the newly written image capture
	 * @throws IOException
	 *             thrown if an error occurs when writing the image to disk
	 */
	public ImageCapture writeImageSegments() throws IOException {
		if (endTime != null) {
			throw new IOException(String.format(
					"Image has already been written to path %1$s at %2$s",
					getImagePath(), UGateUtil.calFormat(endTime)));
		}
		try {
			final ByteBuffer byteBuffer = getBytes();
			final Path filePath = getImagePath();
			writeImage(byteBuffer.array(), filePath);
			endTime = Calendar.getInstance();
			if (log.isInfoEnabled()) {
				log.info(String
						.format("Wrote (%1$s) bytes from (%2$s) image chunks to \"%3$s\" (took: %4$s)",
								byteBuffer.array().length, getData().size(),
								filePath, getCreatedTimeDiffernce(endTime)));
			}
			return new ImageCapture(getRemoteNode(), getStatus(),
					getSignalStrength(), filePath, byteBuffer.array().length);
		} finally {
			setData(new ArrayList<RxTxJPEG.ImageChunk>());
		}
	}

	/**
	 * @return a snapshot of the current image segments
	 *         {@linkplain ImageCapture#getFilePath()} will be <code>null</code>
	 *         until {@linkplain #writeImageSegments()} is called
	 */
	public ImageCapture createImageSegmentsSnapshot() {
		final ByteBuffer byteBuffer = getBytes();
		return new ImageCapture(getRemoteNode(), getStatus(),
				getSignalStrength(), null,
				byteBuffer != null ? byteBuffer.array().length : 0);
	}

	/**
	 * Writes the image to file
	 * 
	 * @param bytes
	 *            the image bytes
	 * @param filePath
	 *            the file {@linkplain Path}
	 * @throws IOException
	 *             any {@linkplain IOException} that may occur
	 */
	protected void writeImage(final byte[] bytes, final Path filePath)
			throws IOException {
		final File imageFile = filePath.toFile();
		if (!imageFile.exists()) {
			imageFile.createNewFile();
		}
		final FileOutputStream fos = new FileOutputStream(imageFile);
		try {
			fos.write(bytes);
		} finally {
			fos.close();
		}
	}

	/**
	 * Image chunk that represents a portion of an overall image
	 */
	public class ImageChunk {
		public int[] data;

		public ImageChunk(int[] data, int startIndex, int length) {
			this.data = Arrays.copyOfRange(data, startIndex, length
					+ startIndex);
			// log.debug("####### Image Chunk: " + this.data.length + " bytes");
		}
	}
}
