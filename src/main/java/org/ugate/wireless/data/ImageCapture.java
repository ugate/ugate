package org.ugate.wireless.data;

import java.lang.reflect.Field;

/**
 * Image capture from remote node
 */
public class ImageCapture extends RxData {

	private final String filePath;
	private final int fileSize;
	
	/**
	 * Constructor
	 * 
	 * @param nodeIndex the remote node index
	 * @param status the {@linkplain Status}
	 * @param signalStrength the signal strength
	 * @param filePath the file path
	 * @param fileSize the file size
	 */
	ImageCapture(final Integer nodeIndex, final Status status, final int signalStrength, 
			final String filePath, final int fileSize) {
		super(nodeIndex, status, signalStrength);
		this.filePath = filePath;
		this.fileSize = fileSize;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final Field[] fields = getClass().getDeclaredFields();
		final StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append(" [");
		for (final Field field : fields) {
			try {
				sb.append(field.getName());
				sb.append('=');
				sb.append(field.getInt(this));
				sb.append(", ");
			} catch (final Throwable t) {
				t.printStackTrace();
			}
		}
		sb.append(']');
		return sb.toString();
	}

	/**
	 * @return the filePath
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * @return the fileSize
	 */
	public int getFileSize() {
		return fileSize;
	}
}
