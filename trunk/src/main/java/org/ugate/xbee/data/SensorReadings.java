package org.ugate.xbee.data;

import org.apache.log4j.Logger;

/**
 * Sensor readings
 */
public class SensorReadings {
	
	private static final Logger log = Logger.getLogger(SensorReadings.class);
	private final int sonarFeet;
	private final int sonarInches;
	private final int microwaveCycleCnt;
	private final int irFeet;
	private final int irInches;
	
	public SensorReadings(int sonarFeet, int sonarInches, int microwaveCycleCnt, int irFeet, int irInches) {
		this.sonarFeet = sonarFeet;
		this.sonarInches = sonarInches;
		this.microwaveCycleCnt = microwaveCycleCnt;
		this.irFeet = irFeet;
		this.irInches = irInches;
		log.debug("NEW " + this);
	}
	
	@Override
	public String toString() {
		return "[Sonar Distance: " + sonarFeet + '\'' + sonarInches
				+ "\"] [Speed: " + getMicrowaveCycleCnt() + " (changes/sec) "
				+ getSpeedMillimetersPerSec() + " (mm/sec) "
				+ getSpeedInchesPerSec() + " (inches/sec) " + getSpeedMPH()
				+ " (MPH)] [IR Disatance: " + getIrFeet() + '\''
				+ getIrInches() + "]";
	}

	/**
	 * @return the distance in feet that the image was taken at
	 * 		(difference from {@link #getSonarInches()}
	 */
	public int getSonarFeet() {
		return sonarFeet;
	}

	/**
	 * @return the distance in feet that the image was taken at 
	 * 		(difference from {@link #getSonarFeet()}
	 */
	public int getSonarInches() {
		return sonarInches;
	}

	/**
	 * @return the distance in feet that the image was taken at
	 * 		(difference from {@link #getSonarInches()}
	 */
	public int getIrFeet() {
		return irFeet;
	}

	/**
	 * @return the distance in feet that the image was taken at 
	 * 		(difference from {@link #getSonarFeet()}
	 */
	public int getIrInches() {
		return irInches;
	}

	/**
	 * @return the number of cycle changes clocked when the image was taken 
	 */
	public int getMicrowaveCycleCnt() {
		return microwaveCycleCnt;
	}

	/**
	 * @return the speed clocked when the image was taken (mm/second) 
	 * 		(calculated from {@link #getMicrowaveCycleCnt()}
	 */
	public long getSpeedMillimetersPerSec() {
		return getMicrowaveCycleCnt() * 30000 / 2105;
	}

	/**
	 * @return the speed clocked when the image was taken (inches/second) 
	 * 		(calculated from {@link #getMicrowaveCycleCnt()}
	 */
	public long getSpeedInchesPerSec() {
		return getMicrowaveCycleCnt() * 30000 / 53467;
	}

	/**
	 * @return the speed clocked when the image was taken (miles/hour) 
	 * 		(calculated from {@link #getMicrowaveCycleCnt()}
	 */
	public double getSpeedMPH() {
		return getSpeedInchesPerSec() * 30000 / 53467;
	}
}
