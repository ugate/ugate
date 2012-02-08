package org.ugate;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Settings related to an individual node device located in a remote location
 */
public enum RemoteSettings implements ISettings {
	SOUNDS_ON("sounds.on", false),
	CAM_IMG_CAPTURE_RETRY_CNT("cam.img.capture.retries", false),
	WIRELESS_ADDRESS_NODE("wireless.address.node", false),
	WIRELESS_WORKING_DIR_PATH("wireless.working.dir.path", false),
	MAIL_ALARM_ON("mail.alarm.on", false),
	UNIVERSAL_REMOTE_ACCESS_ON("universal.remote.access.on", true),
	ACCESS_CODE_1("access.code.one", true),
	ACCESS_CODE_2("access.code.two", true),
	ACCESS_CODE_3("access.code.three", true),
	GATE_ACCESS_ON("gate.access.on", true),
	SONAR_DISTANCE_THRES_FEET("sonar.distance.threshold.feet", true),
	SONAR_DISTANCE_THRES_INCHES("sonar.distance.threshold.inches", true),
	SONAR_DELAY_BTWN_TRIPS("sonar.trip.delay", true),
	PIR_DELAY_BTWN_TRIPS("pir.trip.delay", true),
	SONAR_PIR_ANGLE_PAN("sonar.pir.angle.pan", true),
	SONAR_PIR_ANGLE_TILT("sonar.pir.angle.tilt", true),
	MW_SPEED_THRES_CYCLES_PER_SEC("microwave.speed.threshold", true),
	MW_DELAY_BTWN_TRIPS("microwave.trip.delay", true),
	MW_ANGLE_PAN("microwave.angle.pan", true),
	LASER_DISTANCE_THRES_FEET("laser.distance.threshold.feet", true),
	LASER_DISTANCE_THRES_INCHES("laser.distance.threshold.inches", true),
	LASER_DELAY_BTWN_TRIPS("laser.trip.delay", true),
	LASER_ANGLE_PAN("laser.angle.pan", true),
	LASER_ANGLE_TILT("laser.angle.tilt", true),
	MULTI_ALARM_TRIP_STATE("multi.alarm.trip.state", true),
	CAM_RES("cam.resolution", true),
	CAM_ANGLE_PAN("cam.angle.pan", true),
	CAM_ANGLE_TILT("cam.angle.pan", true),
	CAM_SONAR_TRIP_ANGLE_PRIORITY("cam.sonar.trip.angle.priority", true),
	CAM_SONAR_TRIP_ANGLE_PAN("cam.sonar.trip.angle.pan", true),
	CAM_SONAR_TRIP_ANGLE_TILT("cam.sonar.trip.angle.tilt", true),
	CAM_PIR_TRIP_ANGLE_PRIORITY("cam.pir.trip.angle.priority", true),
	CAM_PIR_TRIP_ANGLE_PAN("cam.pir.trip.angle.pan", true),
	CAM_PIR_TRIP_ANGLE_TILT("cam.pir.trip.angle.tilt", true),
	CAM_MW_TRIP_ANGLE_PRIORITY("cam.mw.trip.angle.priority", true),
	CAM_MW_TRIP_ANGLE_PAN("cam.mw.trip.angle.pan", true),
	CAM_MW_TRIP_ANGLE_TILT("cam.mw.trip.angle.tilt", true),
	CAM_LASER_TRIP_ANGLE_PRIORITY("cam.laser.trip.angle.priority", true),
	CAM_LASER_TRIP_ANGLE_PAN("cam.laser.trip.angle.pan", true),
	CAM_LASER_TRIP_ANGLE_TILT("cam.laser.trip.angle.tilt", true);
	
	public static final int WIRELESS_ADDRESS_START_INDEX = 1;
	public static final int WIRELESS_ADDRESS_MAX_DIGITS = 4;
	private final String key;
	private final boolean canRemote;
	private static AtomicInteger canRemoteCount = new AtomicInteger(-1);
	
	/**
	 * Constructor
	 * 
	 * @param key the key
	 * @param canRemote can remote
	 */
	private RemoteSettings(final String key, final boolean canRemote) {
		this.key = key;
		this.canRemote = canRemote;
	}
	
	/**
	 * @return the number of elements that can remote
	 */
	public static int canRemoteCount() {
		if (canRemoteCount.get() < 0) {
			canRemoteCount.incrementAndGet();
			for (final RemoteSettings rs : values()) {
				if (rs.canRemote()) {
					canRemoteCount.incrementAndGet();
				}
			}
		}
		return canRemoteCount.get();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return String.format("%1$s (key = %2$s, canRemote = %3$s)", super.toString(), key, canRemote);
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
