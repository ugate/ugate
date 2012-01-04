package org.ugate.wireless.data;

import java.util.Calendar;

import org.apache.log4j.Logger;
import org.ugate.UGateUtil;

/**
 * Wireless response data
 */
public abstract class RxData {

	private static final Logger log = Logger.getLogger(RxData.class);
	private final Integer nodeIndex;
	private final Calendar createdTime;
	private final int signalStrength;
	private Status status = Status.NORMAL;
	
	/**
	 * Constructor
	 * 
	 * @param nodeIndex the remote node index
	 * @param status the initial {@linkplain Status}
	 * @param signalStrength the signal strength
	 */
	public RxData(final Integer nodeIndex, final Status status, final int signalStrength) {
		this.nodeIndex = nodeIndex;
		setStatus(status);
		this.signalStrength = signalStrength;
		this.createdTime = Calendar.getInstance();
		log.debug("NEW " + this);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return String.format("NODE INDEX %4$s, STATUS: %1$s, SIGNAL STRENGTH: %2$s, CREATED: %3$s", 
				getStatus(), getSignalStrength(), UGateUtil.calFormat(getCreatedTime()), getNodeIndex());
	}

	/**
	 * @return the remote node index
	 */
	public Integer getNodeIndex() {
		return this.nodeIndex;
	}

	/**
	 * @return the response code if an error exists
	 */
	public Status getStatus() {
		return status;
	}
	
	/**
	 * @param status the status code
	 */
	public void setStatus(final Status status) {
		this.status = status;
	}
	
	/**
	 * @return the signal strength when data is received
	 */
	public int getSignalStrength() {
		return signalStrength;
	}
	
	/**
	 * @return date/time the data was created
	 */
	public Calendar getCreatedTime() {
		return createdTime;
	}
	
	/**
	 * @return date/time the data was created in a human readable format
	 */
	public String getCreatedTimeString() {
		return UGateUtil.calFormat(getCreatedTime());
	}
	
	/**
	 * Gets the date/time difference between the {@linkplain #getCreatedTime()} and the specified time
	 * 
	 * @param laterTime the date/time to subtract
	 * @return date/time difference in a human readable format
	 */
	public String getCreatedTimeDiffernce(final Calendar laterTime) {
		return UGateUtil.calFormatDateDifference(getCreatedTime().getTime(), laterTime.getTime());
	}
	

	/**
	 * Wireless transmission status codes
	 */
	public enum Status {
		NORMAL, GENERAL_FAILURE, PARSING_ERROR;
	}
}
