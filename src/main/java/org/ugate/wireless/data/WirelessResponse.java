package org.ugate.wireless.data;

import java.util.Calendar;

import org.apache.log4j.Logger;
import org.ugate.UGateUtil;

/**
 * Wireless response
 */
public class WirelessResponse<T> implements IResponse<T> {
	
	private static final Logger log = Logger.getLogger(WirelessResponse.class);
	private final Calendar created;
	private final int initCommandASCII;
	private Calendar startTime;
	private StatusCode statusCode = StatusCode.NONE;
	private T data;

	public WirelessResponse(final int initCommandASCII, final StatusCode statusCode, final T data) {
		setStatusCode(statusCode);
		startTime = Calendar.getInstance();
		this.created = Calendar.getInstance();
		this.initCommandASCII = initCommandASCII;
		this.data = data;
		log.debug("NEW " + this);
	}
	
	/**
	 * Shows the string representation of a wireless response
	 */
	@Override
	public String toString() {
		return "CREATED: " + UGateUtil.formatCal(this.created) + ", " + getData();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getCommand() {
		return initCommandASCII;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasTimedOut() {
		return Calendar.getInstance().getTimeInMillis() - startTime.getTimeInMillis() > 120000;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StatusCode getStatusCode() {
		return statusCode;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setStatusCode(final StatusCode statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Calendar getCreated() {
		return created;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
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
