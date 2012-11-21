package org.ugate.wireless.data;

import org.ugate.service.entity.jpa.RemoteNodeReading;

/**
 * Sensor readings
 */
public class RxTxRemoteNodeReadingDTO extends RxData  {

	private final RemoteNodeReading remoteNodeReading;

	/**
	 * Constructor
	 * 
	 * @param remoteNodeReading
	 *            the {@linkplain RemoteNodeReading}
	 * @param status
	 *            the {@linkplain Status} of the transmission
	 */
	public RxTxRemoteNodeReadingDTO(final RemoteNodeReading remoteNodeReading,
			final Status status) {
		super(remoteNodeReading.getRemoteNode(), status, remoteNodeReading.getSignalStrength());
		this.remoteNodeReading = remoteNodeReading;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return String.format(
				"%1$s [Sonar Distance: %2$s' %3$s\"] [Speed: %4$s (changes/sec) %5$s (mm/sec) %6$s (inches/sec) %7$s (MPH)] " + 
				"[Laser Distance: %8$s' %9$s\"] [PIR Intensity: %10$s\"] [Gate State: %11$s]", 
				super.toString(), getRemoteNodeReading().getSonarFeet(), getRemoteNodeReading().getSonarInches(), 
				getRemoteNodeReading().getMicrowaveCycleCount(), getRemoteNodeReading().getMicrowaveSpeedMillimetersPerSec(), 
				getRemoteNodeReading().getMicrowaveSpeedInchesPerSec(), getRemoteNodeReading().getMicrowaveSpeedMPH(), 
				getRemoteNodeReading().getLaserFeet(), getRemoteNodeReading().getLaserInches(), 
				getRemoteNodeReading().getPirIntensity(), getRemoteNodeReading().getGateState());
	}

	/**
	 * @return the number of cycle changes clocked when the microwave sensor was read 
	 */
	public RemoteNodeReading getRemoteNodeReading() {
		return remoteNodeReading;
	}

}
