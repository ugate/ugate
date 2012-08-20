package org.ugate.wireless.data;

import java.lang.reflect.Field;
import java.nio.file.Path;

import org.ugate.service.entity.jpa.RemoteNode;

/**
 * Image capture from remote node
 */
public class ImageCapture extends RxData {

	private final Path filePath;
	private final int fileSize;

	/**
	 * Constructor
	 * 
	 * @param remoteNode
	 *            the {@linkplain RemoteNode}
	 * @param status
	 *            the {@linkplain Status}
	 * @param signalStrength
	 *            the signal strength
	 * @param filePath
	 *            the file {@linkplain Path}
	 * @param fileSize
	 *            the file size
	 */
	ImageCapture(final RemoteNode remoteNode, final Status status,
			final int signalStrength, final Path filePath, final int fileSize) {
		super(remoteNode, status, signalStrength);
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
	public Path getFilePath() {
		return filePath;
	}

	/**
	 * @return the fileSize
	 */
	public int getFileSize() {
		return fileSize;
	}
}
