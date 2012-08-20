package org.ugate.wireless.data;

import java.util.Calendar;

import org.ugate.service.entity.jpa.RemoteNode;

/**
 * Wireless response that requires multiple receive transmissions before
 * completion
 * 
 * @param <T>
 *            the data type
 */
public abstract class MultiRxData<T> extends RxRawData<T> {

	/**
	 * Constructor
	 * 
	 * @param remoteNode
	 *            the {@linkplain RemoteNode}
	 * @param status
	 *            the initial {@linkplain Status}
	 * @param signalStrength
	 *            the signal strength
	 * @param data
	 *            the data
	 */
	public MultiRxData(final RemoteNode remoteNode, final Status status,
			final int signalStrength, final T data) {
		super(remoteNode, status, signalStrength, data);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasTimedOut() {
		return Calendar.getInstance().getTimeInMillis()
				- getCreatedTime().getTimeInMillis() > 120000;
	}
}
