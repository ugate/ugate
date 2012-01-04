package org.ugate.wireless.data;

import java.util.Calendar;

/**
 * Wireless response that requires multiple receive transmissions before completion
 *
 * @param <T> the data type
 */
public abstract class MultiRxData<T> extends RxRawData<T> {

	/**
	 * Constructor
	 * 
	 * @param nodeIndex the remote node index
	 * @param status the initial {@linkplain Status}
	 * @param signalStrength the signal strength
	 * @param data the data
	 */
	public MultiRxData(final Integer nodeIndex, final Status status, final int signalStrength, final T data) {
		super(nodeIndex, status, signalStrength, data);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean hasTimedOut() {
		return Calendar.getInstance().getTimeInMillis() - getCreatedTime().getTimeInMillis() > 120000;
	}
}
