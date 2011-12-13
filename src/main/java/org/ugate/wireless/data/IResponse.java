package org.ugate.wireless.data;

import java.util.Calendar;

import org.ugate.Command;

public interface IResponse<T> {
	
	/**
	 * @return the initiating {@linkplain Command}
	 */
	Command getCommand();

	/**
	 * @return the response code if an error exists
	 */
	WirelessStatusCode getStatusCode();
	
	/**
	 * @param statusCode the status code
	 */
	void setStatusCode(final WirelessStatusCode statusCode);
	
	/**
	 * @return date/time the first image segment was added
	 */
	Calendar getCreated();
	
	/**
	 * @return true when the response has timed out from lack of activity
	 */
	public boolean hasTimedOut();
	
	/**
	 * @return the sensor readings read when the image was taken
	 */
	T getData();
}
