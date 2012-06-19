package org.ugate.service;

import java.util.Date;

import org.ugate.service.entity.jpa.RemoteNode;

/**
 * {@linkplain RemoteNode} {@linkplain IModelType} related to an individual node
 * device located in a remote location. Each {@linkplain #ordinal()} represents
 * a remote setting index.
 */
public enum RemoteNodeType implements IModelType<RemoteNode> {
	DEVICE_SOUNDS_ON("deviceSoundsOn", false),
	CAM_IMG_CAPTURE_RETRY_CNT("camImgCaptureRetryCnt", false),
	WIRELESS_ADDRESS("address", false),
	WIRELESS_WORKING_DIR_PATH("workingDir", false),
	MAIL_ALERT_ON("mailAlertOn", false),
	UNIVERSAL_REMOTE_ACCESS_ON("universalRemoteAccessOn", true),
	UNIVERSAL_REMOTE_ACCESS_CODE_1("universalRemoteAccessCode1", true),
	UNIVERSAL_REMOTE_ACCESS_CODE_2("universalRemoteAccessCode2", true),
	UNIVERSAL_REMOTE_ACCESS_CODE_3("universalRemoteAccessCode3", true),
	GATE_ACCESS_ON("gateAccessOn", true),
	SONAR_DISTANCE_THRES_FEET("sonarDistanceThresFeet", true),
	SONAR_DISTANCE_THRES_INCHES("sonarDistanceThresInches", true),
	SONAR_DELAY_BTWN_TRIPS("sonarDelayBtwnTrips", true),
	PIR_DELAY_BTWN_TRIPS("pirDelayBtwnTrips", true),
	SONAR_PIR_ANGLE_PAN("sonarPirAnglePan", true),
	SONAR_PIR_ANGLE_TILT("sonarPirAngleTilt", true),
	MW_SPEED_THRES_CYCLES_PER_SEC("mwSpeedThresCyclesPerSec", true),
	MW_DELAY_BTWN_TRIPS("mwDelayBtwnTrips", true),
	MW_ANGLE_PAN("mwAnglePan", true),
	LASER_DISTANCE_THRES_FEET("laserDistanceThresFeet", true),
	LASER_DISTANCE_THRES_INCHES("laserDistanceThresInches", true),
	LASER_DELAY_BTWN_TRIPS("laserDelayBtwnTrips", true),
	LASER_ANGLE_PAN("laserAnglePan", true),
	LASER_ANGLE_TILT("laserAngleTilt", true),
	MULTI_ALARM_TRIP_STATE("multiAlarmTripState", true),
	CAM_RESOLUTION("camResolution", true),
	CAM_ANGLE_PAN("camAnglePan", true),
	CAM_ANGLE_TILT("camAngleTilt", true),
	CAM_SONAR_TRIP_ANGLE_PRIORITY("camSonarTripAnglePriority", true),
	CAM_SONAR_TRIP_ANGLE_PAN("camSonarTripAnglePan", true),
	CAM_SONAR_TRIP_ANGLE_TILT("camSonarTripAngleTilt", true),
	CAM_PIR_TRIP_ANGLE_PRIORITY("camPirTripAnglePriority", true),
	CAM_PIR_TRIP_ANGLE_PAN("camPirTripAnglePan", true),
	CAM_PIR_TRIP_ANGLE_TILT("camPirTripAngleTilt", true),
	CAM_MW_TRIP_ANGLE_PRIORITY("camMwTripAnglePriority", true),
	CAM_MW_TRIP_ANGLE_PAN("camMwTripAnglePan", true),
	CAM_MW_TRIP_ANGLE_TILT("camMwTripAngleTilt", true),
	CAM_LASER_TRIP_ANGLE_PRIORITY("camLaserTripAnglePriority", true),
	CAM_LASER_TRIP_ANGLE_PAN("camLaserTripAnglePan", true),
	CAM_LASER_TRIP_ANGLE_TILT("camLaserTripAngleTilt", true);

	private final String key;
	private final boolean canRemote;

	/**
	 * Constructor
	 * 
	 * @param key
	 *            {@linkplain #getKey()}
	 * @param canRemote
	 *            {@linkplain #canRemote()}
	 */
	private RemoteNodeType(final String key, final boolean canRemote) {
		this.key = key;
		this.canRemote = canRemote;
	}
	
	/**
	 * @return a new default {@linkplain RemoteNode}
	 */
	public static RemoteNode newDefaultRemoteNode() {
		final RemoteNode remoteNode = new RemoteNode();
		remoteNode.setDeviceSoundsOn(1);
		remoteNode.setGateAccessOn(1);
		remoteNode.setMailAlertOn(1);
		remoteNode.setUniversalRemoteAccessOn(0);
		remoteNode.setCreatedDate(new Date());
		return remoteNode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return String.format("%1$s (key = %2$s, canRemote = %3$s)", 
				super.toString(), key, canRemote);
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
}
