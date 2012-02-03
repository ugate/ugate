package org.ugate.wireless.data;

import org.apache.log4j.Logger;

/**
 * Sensor readings
 */
public class RxTxSensorReadings extends RxData  {
	
	private static final Logger log = Logger.getLogger(RxTxSensorReadings.class);
	private final int sonarFeet;
	private final int sonarInches;
	private final int microwaveCycleCnt;
	private final int irFeet;
	private final int irInches;
	private final int gateState;
	
	/**
	 * Constructor
	 * 
	 * @param nodeIndex the remote node index
	 * @param status the {@linkplain Status}
	 * @param signalStrength the signal strength
	 * @param sonarFeet sonar feet portion
	 * @param sonarInches the sonar inches portion
	 * @param microwaveCycleCnt the microwave cycle count
	 * @param irFeet the IR feet portion
	 * @param irInches the IR inches portion
	 * @param gateState the gate state
	 */
	public RxTxSensorReadings(final Integer nodeIndex, final Status status, final int signalStrength, final int sonarFeet, final int sonarInches, 
			final int microwaveCycleCnt, final int irFeet, final int irInches, final int gateState) {
		super(nodeIndex, status, signalStrength);
		this.sonarFeet = sonarFeet;
		this.sonarInches = sonarInches;
		this.microwaveCycleCnt = microwaveCycleCnt;
		this.irFeet = irFeet;
		this.irInches = irInches;
		this.gateState = gateState;
		log.debug("NEW " + this);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return String.format(
				"%1$s [Sonar Distance: %2$s' %3$s\"] [Speed: %4$s (changes/sec) %5$s (mm/sec) %6$s (inches/sec) %7$s (MPH)] " + 
				"[IR Distance: %8$s' %9$s\"] [Gate State: %10$s]", 
				super.toString(), getSonarFeet(), getSonarInches(), getMicrowaveCycleCnt(), getSpeedMillimetersPerSec(), 
				getSpeedInchesPerSec(), getSpeedMPH(), getIrFeet(), getIrInches(), getGateState());
	}

	/**
	 * @return the distance that the sonar was read at 
	 * 		(<code>feet</code> portion minus the <code>inches</code> from {@link #getSonarInches()})
	 */
	public int getSonarFeet() {
		return sonarFeet;
	}

	/**
	 * @return the distance that the sonar was read at 
	 * 		(<code>inches</code> portion minus the <code>feet</code> from {@link #getSonarFeet()})
	 */
	public int getSonarInches() {
		return sonarInches;
	}
	
	/**
	 * @return the distance that the sonar was read at (in <code>meters</code>)
	 */
	public double getSonarMeters() {
		return ((getSonarFeet() * 12) + getSonarInches()) * 0.0254d;
	}

	/**
	 * @return the distance that the IR was read at 
	 * 		(<code>feet</code> portion minus the <code>inches</code> from {@link #getIrInches()})
	 */
	public int getIrFeet() {
		return irFeet;
	}

	/**
	 * @return the distance that the IR was read at 
	 * 		(<code>inches</code> portion minus the <code>feet</code> from {@link #getIrFeet()})
	 */
	public int getIrInches() {
		return irInches;
	}

	/**
	 * @return the number of cycle changes clocked when the microwave sensor was read 
	 */
	public int getMicrowaveCycleCnt() {
		return microwaveCycleCnt;
	}

	/**
	 * @return the speed clocked when the microwave sensor was read (mm/second) 
	 * 		(calculated from {@link #getMicrowaveCycleCnt()}
	 */
	public long getSpeedMillimetersPerSec() {
		return getMicrowaveCycleCnt() * 30000 / 2105;
	}

	/**
	 * @return the speed clocked when the microwave sensor was read (inches/second) 
	 * 		(calculated from {@link #getMicrowaveCycleCnt()}
	 */
	public long getSpeedInchesPerSec() {
		return getMicrowaveCycleCnt() * 30000 / 53467;
	}

	/**
	 * @return the speed clocked when the microwave sensor was read (miles/hour) 
	 * 		(calculated from {@link #getMicrowaveCycleCnt()}
	 */
	public double getSpeedMPH() {
		return getSpeedInchesPerSec() * 30000 / 53467;
	}
	
	/**
	 * @return the state of the gate
	 */
	public int getGateState() {
		return gateState;
	}
}
