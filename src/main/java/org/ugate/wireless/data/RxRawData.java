package org.ugate.wireless.data;

/**
 * Raw data from a received transmission
 * 
 * @param <T> the raw data type
 */
public class RxRawData<T> extends RxData {

	private T data;

	/**
	 * Constructor
	 * 
	 * @param nodeIndex the remote node index
	 * @param status the {@linkplain Status}
	 * @param signalStrength the signal strength
	 * @param data the image chunk data
	 */
	public RxRawData(final Integer nodeIndex, final Status status, final int signalStrength, final T data) {
		super(nodeIndex, status, signalStrength);
		this.data = data;
	}
	
	/**
	 * Shows the string representation of a wireless response
	 */
	@Override
	public String toString() {
		return String.format("%1$s, DATA: %2$s", 
				super.toString(), getData());
	}
	
	/**
	 * @return the sensor readings read when the image was taken
	 */
	public T getData() {
		return data;
	}
	
	/**
	 * Sets the data
	 * 
	 * @param data the data to set
	 */
	protected void setData(final T data) {
		this.data = data;
	}
}
