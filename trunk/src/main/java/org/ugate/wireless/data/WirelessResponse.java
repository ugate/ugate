package org.ugate.wireless.data;

import java.util.Calendar;

import org.apache.log4j.Logger;
import org.ugate.Command;
import org.ugate.UGateUtil;

/**
 * Wireless response
 */
public class WirelessResponse<T> implements IResponse<T> {
	
	private static final Logger log = Logger.getLogger(WirelessResponse.class);
	private final Calendar created;
	private final Command command;
	private Calendar startTime;
	private WirelessStatusCode statusCode = WirelessStatusCode.NONE;
	private T data;

	public WirelessResponse(final Command command, final WirelessStatusCode statusCode, final T data) {
		setStatusCode(statusCode);
		startTime = Calendar.getInstance();
		this.created = Calendar.getInstance();
		this.command = command;
		this.data = data;
		log.debug("NEW " + this);
	}
	
	/**
	 * Shows the string representation of a wireless response
	 */
	@Override
	public String toString() {
		return String.format("COMMAND: %1$s, CREATED: %2$s, DATA: %3$s", 
				getCommand().id, UGateUtil.calFormat(getCreated()), getData());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Command getCommand() {
		return command;
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
	public WirelessStatusCode getStatusCode() {
		return statusCode;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setStatusCode(final WirelessStatusCode statusCode) {
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
