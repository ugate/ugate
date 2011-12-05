package org.ugate.wireless.data;

import java.util.Calendar;

public interface IResponse<T> {
	
	/**
	 * @return the initiating command
	 */
	int getCommand();

	/**
	 * @return the response code if an error exists
	 */
	StatusCode getStatusCode();
	
	/**
	 * @param statusCode the status code
	 */
	void setStatusCode(final StatusCode statusCode);
	
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
	
	/**
	 * Response codes
	 */
	public enum StatusCode {
		NONE, GENERAL_FAILURE, PARSING_ERROR;
	}
}
