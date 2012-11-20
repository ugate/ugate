package org.ugate.service.entity;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.ugate.service.entity.jpa.Host;
import org.ugate.service.entity.jpa.RemoteNode;

/**
 * {@linkplain RemoteNode} {@linkplain IModelType} related to an individual node
 * devices located in a remote locations. <b>Each {@linkplain #ordinal()}
 * represents the proper order that incoming data is received from remote node
 * devices</b>.
 */
public enum RemoteNodeType implements IModelType<RemoteNode> {
	ID("id", null, false),
	DEVICE_SOUNDS_ON("deviceSoundsOn", Type.ALARM_NOTIFY_TOGGLE, false),
	DEVICE_AUTO_SYNCHRONIZE("deviceAutoSynchronize", Type.ALARM_NOTIFY_TOGGLE,
			false),
	DEVICE_SYNCHRONIZED("deviceSynchronized", null, false),
	CAM_IMG_CAPTURE_RETRY_CNT("camImgCaptureRetryCnt", null, false),
	REPORT_READINGS("reportReadings", null, false),
	WIRELESS_ADDRESS("address", null, false),
	WIRELESS_WORKING_DIR_PATH("workingDir", null, false),
	MAIL_ALERT_ON("mailAlertOn", Type.ALARM_NOTIFY_TOGGLE, false),
	ALARMS_ON("alarmsOn", null, true),
	UNIVERSAL_REMOTE_ACCESS_ON("universalRemoteAccessOn",
			Type.UNIVERSAL_REMOTE_TOGGLE, true),
	UNIVERSAL_REMOTE_ACCESS_CODE_1("universalRemoteAccessCode1",
			Type.UNIVERSAL_REMOTE_CODE_RANGE, true),
	UNIVERSAL_REMOTE_ACCESS_CODE_2("universalRemoteAccessCode2",
			Type.UNIVERSAL_REMOTE_CODE_RANGE, true),
	UNIVERSAL_REMOTE_ACCESS_CODE_3("universalRemoteAccessCode3",
			Type.UNIVERSAL_REMOTE_CODE_RANGE, true),
	GATE_ACCESS_ON("gateAccessOn", Type.GATE_TOGGLE, true),
	SONAR_DISTANCE_THRES_FEET("sonarDistanceThresFeet",
			Type.SONAR_THRESHOLD_RANGE, true),
	SONAR_DISTANCE_THRES_INCHES("sonarDistanceThresInches",
			Type.SONAR_THRESHOLD_RANGE, true),
	SONAR_DELAY_BTWN_TRIPS("sonarDelayBtwnTrips", Type.SONAR_THRESHOLD_RANGE,
			true),
	PIR_DELAY_BTWN_TRIPS("pirDelayBtwnTrips", Type.PIR_THRESHOLD_RANGE, true),
	SONAR_PIR_ANGLE_PAN("sonarPirAnglePan", Type.SONAR_PIR_POSITION_RANGE, true),
	SONAR_PIR_ANGLE_TILT("sonarPirAngleTilt", Type.SONAR_PIR_POSITION_RANGE,
			true),
	MW_SPEED_THRES_CYCLES_PER_SEC("mwSpeedThresCyclesPerSec",
			Type.MW_THRESHOLD_RANGE, true),
	MW_DELAY_BTWN_TRIPS("mwDelayBtwnTrips", Type.MW_THRESHOLD_RANGE, true),
	MW_ANGLE_PAN("mwAnglePan", Type.MW_POSITION_RANGE, true),
	LASER_DISTANCE_THRES_FEET("laserDistanceThresFeet",
			Type.LASER_THRESHOLD_RANGE, true),
	LASER_DISTANCE_THRES_INCHES("laserDistanceThresInches",
			Type.LASER_THRESHOLD_RANGE, true),
	LASER_DELAY_BTWN_TRIPS("laserDelayBtwnTrips", Type.LASER_THRESHOLD_RANGE,
			true),
	LASER_ANGLE_PAN("laserAnglePan", null, true),
	LASER_ANGLE_TILT("laserAngleTilt", null, true),
	MULTI_ALARM_TRIP_STATE("multiAlarmTripState", Type.ALARM_TRIP_STATE_RANGE,
			true),
	CAM_RESOLUTION("camResolution", Type.CAM_SETTINGS_TOGGLE, true),
	CAM_ANGLE_PAN("camAnglePan", Type.CAM_POSITION_RANGE, true),
	CAM_ANGLE_TILT("camAngleTilt", Type.CAM_POSITION_RANGE, true),
	CAM_SONAR_TRIP_ANGLE_PRIORITY("camSonarTripAnglePriority",
			Type.CAM_POSITION_RANGE, true),
	CAM_SONAR_TRIP_ANGLE_PAN("camSonarTripAnglePan", Type.CAM_POSITION_RANGE,
			true),
	CAM_SONAR_TRIP_ANGLE_TILT("camSonarTripAngleTilt", Type.CAM_POSITION_RANGE,
			true),
	CAM_PIR_TRIP_ANGLE_PRIORITY("camPirTripAnglePriority",
			Type.CAM_POSITION_RANGE, true),
	CAM_PIR_TRIP_ANGLE_PAN("camPirTripAnglePan", Type.CAM_POSITION_RANGE, true),
	CAM_PIR_TRIP_ANGLE_TILT("camPirTripAngleTilt", Type.CAM_POSITION_RANGE,
			true),
	CAM_MW_TRIP_ANGLE_PRIORITY("camMwTripAnglePriority",
			Type.CAM_POSITION_RANGE, true),
	CAM_MW_TRIP_ANGLE_PAN("camMwTripAnglePan", Type.CAM_POSITION_RANGE, true),
	CAM_MW_TRIP_ANGLE_TILT("camMwTripAngleTilt", Type.CAM_POSITION_RANGE, true),
	CAM_LASER_TRIP_ANGLE_PRIORITY("camLaserTripAnglePriority",
			Type.CAM_POSITION_RANGE, true),
	CAM_LASER_TRIP_ANGLE_PAN("camLaserTripAnglePan", Type.CAM_POSITION_RANGE,
			true),
	CAM_LASER_TRIP_ANGLE_TILT("camLaserTripAngleTilt", Type.CAM_POSITION_RANGE,
			true);

	public static final int WIRELESS_ADDRESS_MAX_DIGITS = 4;
	private static AtomicInteger canRemoteCount = new AtomicInteger(-1);

	private final String key;
	private final boolean canRemote;
	private final Type type;

	/**
	 * Constructor
	 * 
	 * @param key
	 *            {@linkplain #getKey()}
	 * @param type
	 *            the {@link Type} that the {@link RemoteNodeType} belongs to
	 * @param canRemote
	 *            {@linkplain #canRemote()}
	 */
	private RemoteNodeType(final String key, final Type type,
			final boolean canRemote) {
		this.key = key;
		this.type = type;
		this.canRemote = canRemote;
	}

	/**
	 * @param host
	 *            the relative {@linkplain Host} of the {@linkplain RemoteNode}
	 * @param copyFrom
	 *            the {@linkplain RemoteNode} to copy fields from
	 * @return a new default {@linkplain RemoteNode}
	 */
	public static RemoteNode newDefaultRemoteNode(final Host host,
			final RemoteNode copyFrom) {
		if (host == null) {
			throw new NullPointerException(Host.class.getName()
					+ " cannot be null");
		}
		final RemoteNode rn = new RemoteNode();
		if (copyFrom != null) {
			for (final RemoteNodeType rnt : values()) {
				try {
					rnt.setValue(rn, rnt.getValue(copyFrom));
				} catch (final Throwable t) {
					throw new RuntimeException(
							String.format(
									"Unable to transfer data for %1$s from %2$s to %3$s",
									rnt.getKey(), copyFrom, rn), t);
				}
			}
		} else {
			// defaults
			rn.setCamAnglePan(90);
			rn.setCamAngleTilt(90);
			rn.setCamImgCaptureRetryCnt(3);
			rn.setCamLaserTripAnglePan(181);
			rn.setCamLaserTripAnglePriority(1);
			rn.setCamLaserTripAngleTilt(181);
			rn.setCamMwTripAnglePan(181);
			rn.setCamMwTripAnglePriority(3);
			rn.setCamMwTripAngleTilt(181);
			rn.setCamPirTripAnglePan(181);
			rn.setCamPirTripAnglePriority(2);
			rn.setCamPirTripAngleTilt(181);
			rn.setCamSonarTripAnglePan(181);
			rn.setCamSonarTripAnglePriority(4);
			rn.setCamSonarTripAngleTilt(181);
			rn.setDeviceSoundsOn(1);
			rn.setGateAccessOn(1);
			rn.setLaserDistanceThresFeet(24);
			rn.setMailAlertOn(1);
			rn.setMwSpeedThresCyclesPerSec(10);
			rn.setSonarDistanceThresFeet(7);
			rn.setUniversalRemoteAccessCode1(1);
			rn.setUniversalRemoteAccessCode2(2);
			rn.setUniversalRemoteAccessCode3(3);
		}
		rn.setHost(host);
		rn.setId(0);
		rn.setAddress(copyFrom != null ? copyFrom.getAddress() : "3333");
		rn.setCreatedDate(new Date());
		return rn;
	}

	/**
	 * @param host
	 *            the relative {@linkplain Host} of the {@linkplain RemoteNode}
	 * @return a new default {@linkplain RemoteNode}
	 */
	public static RemoteNode newDefaultRemoteNode(final Host host) {
		return newDefaultRemoteNode(host, null);
	}

	/**
	 * @return the number of elements that can remote
	 */
	public static int canRemoteCount() {
		if (canRemoteCount.get() < 0) {
			canRemoteCount.incrementAndGet();
			for (final RemoteNodeType rnt : values()) {
				if (rnt.canRemote()) {
					canRemoteCount.incrementAndGet();
				}
			}
		}
		return canRemoteCount.get();
	}

	/**
	 * @return the minimum allowed value
	 */
	public Long getMin() {
		return getMinMax(false);
	}

	/**
	 * @return the maximum allowed value
	 */
	public Long getMax() {
		return getMinMax(true);
	}

	/**
	 * @param isMax
	 *            true for maximum value, false for minimum value
	 * @return the maximum or minimum value
	 */
	private Long getMinMax(final boolean isMax) {
		try {
			final Field field = RemoteNode.class
					.getDeclaredField(this.getKey());
			if (isMax) {
				final Max max = field.getAnnotation(Max.class);
				return max != null ? max.value() : Long.MAX_VALUE;
			} else {
				final Min min = field.getAnnotation(Min.class);
				return min != null ? min.value() : Long.MIN_VALUE;
			}
		} catch (final Throwable t) {
			throw new RuntimeException(t);
		}
	}

	/**
	 * Determines if two {@linkplain RemoteNode}s have equivalent remote values
	 * 
	 * @param remoteNode1
	 *            the {@linkplain RemoteNode} to evaluate
	 * @param remoteNode2
	 *            the {@linkplain RemoteNode} to evaluate
	 * @return true when all the remote values are equivalent
	 */
	public static boolean remoteEquivalent(final RemoteNode remoteNode1,
			final RemoteNode remoteNode2) {
		if (remoteNode1 != null && remoteNode2 != null) {
			for (final RemoteNodeType rnt : RemoteNodeType.values()) {
				try {
					if (rnt.canRemote()
							&& rnt.getValue(remoteNode1) != rnt
									.getValue(remoteNode2)) {
						return false;
					}
				} catch (final Throwable t) {
					throw new IllegalArgumentException(String.format(
							"Unable to extract %1$s from both %2$s and %3$s",
							rnt, remoteNode1.getAddress(),
							remoteNode2.getAddress()), t);
				}
			}
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getValue(final RemoteNode remoteNode) throws Throwable {
		return IModelType.ValueHelper.getValue(remoteNode, this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setValue(final RemoteNode remoteNode, final Object value)
			throws Throwable {
		IModelType.ValueHelper.setValue(remoteNode, this, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ValueType<RemoteNode, Object> newValueType(final RemoteNode remoteNode) throws Throwable {
		return new ValueType<>(this, getValue(remoteNode));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return String.format("%1$s (key = %2$s, canRemote = %3$s, type = %4$s)",
				super.toString(), key, canRemote, type);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getKey() {
		return this.key;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canRemote() {
		return this.canRemote;
	}

	/**
	 * @return the {@link Type}
	 */
	public Type getType() {
		return type;
	}

	/**
	 * {@link RemoteNodeType} group used to organize {@link RemoteNode} fields
	 */
	public static enum Type {
		CAM_SETTINGS_TOGGLE(ValueGroupType.TOGGLE),
		CAM_POSITION_RANGE(ValueGroupType.RANGE),
		SONAR_PIR_POSITION_RANGE(ValueGroupType.RANGE),
		MW_POSITION_RANGE(ValueGroupType.RANGE),
		SONAR_THRESHOLD_RANGE(ValueGroupType.RANGE),
		PIR_THRESHOLD_RANGE(ValueGroupType.RANGE),
		MW_THRESHOLD_RANGE(ValueGroupType.RANGE),
		LASER_THRESHOLD_RANGE(ValueGroupType.RANGE),
		UNIVERSAL_REMOTE_TOGGLE(ValueGroupType.TOGGLE),
		UNIVERSAL_REMOTE_CODE_RANGE(ValueGroupType.RANGE),
		ALARM_TRIP_STATE_RANGE(ValueGroupType.RANGE),
		ALARM_NOTIFY_TOGGLE(ValueGroupType.TOGGLE),
		GATE_TOGGLE(ValueGroupType.TOGGLE);

		private final ValueGroupType group;

		/**
		 * Constructor
		 * 
		 * @param group
		 *            the {@link #getGroup()}
		 */
		private Type(final ValueGroupType group) {
			this.group = group;
		}

		/**
		 * @return the {@link ValueGroupType} that the {@link Type} belongs to
		 */
		public ValueGroupType getGroup() {
			return group;
		}
	}
}
