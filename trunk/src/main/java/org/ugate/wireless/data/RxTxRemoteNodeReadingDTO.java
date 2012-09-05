package org.ugate.wireless.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ugate.service.entity.jpa.RemoteNodeReading;

/**
 * Sensor readings
 */
public class RxTxRemoteNodeReadingDTO extends RxData  {
	
	private static final Logger log = LoggerFactory.getLogger(RxTxRemoteNodeReadingDTO.class);
	private final RemoteNodeReading remoteNodeReading;

	/**
	 * Constructor
	 * 
	 * @param remoteNodeReading
	 *            the {@linkplain RemoteNodeReading}
	 * @param status
	 *            the {@linkplain Status} of the transmission
	 * @param signalStrength
	 *            the signal strength at the time of transmission
	 */
	public RxTxRemoteNodeReadingDTO(final RemoteNodeReading remoteNodeReading,
			final Status status, final int signalStrength) {
		super(remoteNodeReading.getRemoteNode(), status, signalStrength);
		this.remoteNodeReading = remoteNodeReading;
		log.debug("NEW " + this);
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
