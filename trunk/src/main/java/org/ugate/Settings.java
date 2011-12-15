package org.ugate;

/**
 * Settings preference keys
 */
public enum Settings {
	SOUNDS_ON_KEY("sounds.on", false),
	UNIVERSAL_REMOTE_ACCESS_ON("universal.remote.access.on", false),
	USE_METRIC_KEY("metric.on", false),
	CAM_IMG_CAPTURE_RETRY_CNT_KEY("cam.img.capture.retries", false),
	WIRELESS_COM_PORT_KEY("wireless.com.port", false),
	WIRELESS_BAUD_RATE_KEY("wireless.baud.rate", false),
	WIRELESS_ADDRESS_HOST_KEY("wireless.address.host", false),
	WIRELESS_ADDRESS_NODE_PREFIX_KEY("wireless.address.node.", false),
	MAIL_INBOX_NAME("mail.inbox.name", false),
	MAIL_RECIPIENTS_KEY("mail.recipients", false),
	MAIL_RECIPIENTS_ON_KEY("mail.recipients.on", false),
	MAIL_SMTP_HOST_KEY("mail.smtp.host", false),
	MAIL_SMTP_PORT_KEY("mail.smtp.port", false),
	MAIL_IMAP_HOST_KEY("mail.imap.host", false),
	MAIL_IMAP_PORT_KEY("mail.imap.port", false),
	MAIL_USERNAME_KEY("mail.username", false),
	MAIL_PASSWORD_KEY("mail.password", false),
	MAIL_ALARM_ON_KEY("mail.alarm.on", false),
	ACCESS_CODE_1_KEY("access.code.one", true),
	ACCESS_CODE_2_KEY("access.code.two", true),
	ACCESS_CODE_3_KEY("access.code.three", true),
	SONAR_ALARM_ON_KEY("sonar.alarm.on", true),
	IR_ALARM_ON_KEY("ir.alarm.on", true),
	GATE_ACCESS_ON_KEY("gate.access.on", true),
	SONAR_DISTANCE_THRES_FEET_KEY("sonar.distance.threshold.feet", true),
	SONAR_DISTANCE_THRES_INCHES_KEY("sonar.distance.threshold.inches", true),
	SONAR_DELAY_BTWN_TRIPS_KEY("sonar.trip.delay", true),
	SONAR_IR_ANGLE_PAN_KEY("sonar.ir.angle.pan", true),
	SONAR_IR_ANGLE_TILT_KEY("sonar.ir.angle.tilt", true),
	IR_DISTANCE_THRES_FEET_KEY("ir.distance.threshold.feet", true),
	IR_DISTANCE_THRES_INCHES_KEY("ir.distance.threshold.inches", true),
	IR_DELAY_BTWN_TRIPS_KEY("ir.trip.delay", true),
	MW_ALARM_ON_KEY("microwave.alarm.on", true),
	MW_SPEED_THRES_CYCLES_PER_SEC_KEY("microwave.speed.threshold", true),
	MW_DELAY_BTWN_TRIPS_KEY("microwave.trip.delay", true),
	MW_ANGLE_PAN_KEY("microwave.angle.pan", true),
	MULTI_ALARM_TRIP_STATE_KEY("multi.alarm.trip.state", true),
	CAM_RES_KEY("cam.resolution", true),
	CAM_ANGLE_PAN_KEY("cam.angle.pan", true),
	CAM_ANGLE_TILT_KEY("cam.angle.pan", true),
	CAM_SONAR_TRIP_ANGLE_PAN_KEY("cam.sonar.trip.angle.pan", true),
	CAM_SONAR_TRIP_ANGLE_TILT_KEY("cam.sonar.trip.angle.tilt", true),
	CAM_IR_TRIP_ANGLE_PAN_KEY("cam.ir.trip.angle.pan", true),
	CAM_IR_TRIP_ANGLE_TILT_KEY("cam.ir.trip.angle.tilt", true),
	CAM_MW_TRIP_ANGLE_PAN_KEY("cam.mw.trip.angle.pan", true),
	CAM_MW_TRIP_ANGLE_TILT_KEY("cam.mw.trip.angle.tilt", true);
	
	/**
	 * The preference key used to extract the settings value
	 */
	public final String key;
	/**
	 * The flag that indicates if the settings is transferable to remote nodes
	 */
	public final boolean canRemote;
	private Settings(final String key, final boolean canRemote) {
		this.key = key;
		this.canRemote =canRemote;
	}
}
