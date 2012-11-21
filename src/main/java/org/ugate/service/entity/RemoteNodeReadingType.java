package org.ugate.service.entity;

import org.ugate.service.entity.jpa.RemoteNodeReading;

/**
 * {@linkplain RemoteNodeReading} {@linkplain IModelType} related to an
 * individual node devices located in a remote locations. <b>Each
 * {@linkplain #ordinal()} represents a the proper order that incoming data is
 * received from remote node devices</b>.
 */
public enum RemoteNodeReadingType implements IModelType<RemoteNodeReading> {
	SONAR_FEET("sonarFeet"),
	SONAR_INCHES("sonarInches"),
	PIR_INTENSITY("pirIntensity"),
	MICROWAVE_CYCLE_COUNT("microwaveCycleCount"),
	LASER_FEET("laserFeet"),
	LASER_INCHES("laserInches"),
	LASER_CALIBRATED_ANGLE_PAN("laserCalibratedAnglePan", ValueGroupType.INPUT,
			Command.SERVO_LASER_CALIBRATE),
	LASER_CALIBRATED_ANGLE_TILT("laserCalibratedAngleTilt",
			ValueGroupType.INPUT, Command.SERVO_LASER_CALIBRATE),
	GATE_STATE("gateState", ValueGroupType.TOGGLE,
			Command.GATE_TOGGLE_OPEN_CLOSE),
	FROM_MULTI_ALARM_TRIP_STATE("fromMultiState"),
	SIGNAL_STRENGTH("signalStrength"),
	READ_DATE("readDate");

	private final String key;
	private final ValueGroupType group;
	private final Command command;

	/**
	 * Constructor
	 * 
	 * @param key
	 *            the {@link #getKey()}
	 * @param group
	 *            {@link #getGroup()}
	 */
	private RemoteNodeReadingType(final String key) {
		this.key = key;
		this.group = ValueGroupType.INPUT;
		this.command = null;
	}

	/**
	 * Constructor
	 * 
	 * @param key
	 *            the {@link #getKey()}
	 * @param the
	 *            {@link #getGroup()}
	 */
	private RemoteNodeReadingType(final String key, final ValueGroupType group) {
		this.key = key;
		this.group = group;
		this.command = null;
	}

	/**
	 * Constructor
	 * 
	 * @param key
	 *            the {@link #getKey()}
	 * @param the
	 *            {@link #getGroup()}
	 * @param command
	 *            the {@link #getCommand()}
	 */
	private RemoteNodeReadingType(final String key, final ValueGroupType group,
			final Command command) {
		this.key = key;
		this.group = group;
		this.command = command;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getKey() {
		return key;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canRemote() {
		return true;
	}

	/**
	 * @return the {@link ValueGroupType}
	 */
	public ValueGroupType getGroup() {
		return group;
	}

	/**
	 * @return the associated {@link Command} (null if the
	 *         {@link RemoteNodeReadingType} is not associated with any
	 *         {@link Command})
	 */
	public Command getCommand() {
		return command;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getValue(final RemoteNodeReading remoteNodeReading)
			throws Throwable {
		return IModelType.ValueHelper.getValue(remoteNodeReading, this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setValue(final RemoteNodeReading remoteNodeReading,
			final Object value) throws Throwable {
		IModelType.ValueHelper.setValue(remoteNodeReading, this, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ValueType<RemoteNodeReading, Object> newValueType(
			final RemoteNodeReading remoteNodeReading) throws Throwable {
		return new ValueType<>(this, getValue(remoteNodeReading));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return String.format(
				"%1$s (key = %2$s, canRemote = %3$s, command = %4$s)",
				super.toString(), key, canRemote(), command);
	}
}
