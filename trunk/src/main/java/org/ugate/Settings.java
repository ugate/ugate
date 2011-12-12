package org.ugate;

public enum Settings {
	PV_SOUNDS_ON_KEY("sounds.on"),
	PV_USE_METRIC_KEY("metric.on"),
	PV_CAM_IMG_CAPTURE_RETRY_CNT_KEY("cam.img.capture.retries"),
	SV_WIRELESS_COM_PORT_KEY("wireless.com.port"),
	SV_WIRELESS_BAUD_RATE_KEY("wireless.baud.rate"),
	SV_WIRELESS_ADDRESS_HOST_KEY("wireless.address.host"),
	SV_WIRELESS_ADDRESS_NODE_PREFIX_KEY("wireless.address.node."),
	MAIL_COMMAND_DELIMITER("),"),
	MAIL_RECIPIENTS_DELIMITER("),"),
	PV_MAIL_RECIPIENTS_KEY("mail.recipients"),
	PV_MAIL_RECIPIENTS_ON_KEY("mail.recipients.on"),
	PV_MAIL_SMTP_HOST_KEY("mail.smtp.host"),
	PV_MAIL_SMTP_PORT_KEY("mail.smtp.port"),
	PV_MAIL_IMAP_HOST_KEY("mail.imap.host"),
	PV_MAIL_IMAP_PORT_KEY("mail.imap.port"),
	PV_MAIL_USERNAME_KEY("mail.username"),
	PV_MAIL_PASSWORD_KEY("mail.password"),
	PV_MAIL_ALARM_ON_KEY("mail.alarm.on"),
	SV_ACCESS_CODE_1_KEY("access.code.one"),
	SV_ACCESS_CODE_2_KEY("access.code.two"),
	SV_ACCESS_CODE_3_KEY("access.code.three"),
	SV_SONAR_ALARM_ON_KEY("sonar.alarm.on"),
	SV_IR_ALARM_ON_KEY("ir.alarm.on"),
	SV_GATE_ACCESS_ON_KEY("gate.access.on"),
	SV_SONAR_DISTANCE_THRES_FEET_KEY("sonar.distance.threshold.feet"),
	SV_SONAR_DISTANCE_THRES_INCHES_KEY("sonar.distance.threshold.inches"),
	SV_SONAR_DELAY_BTWN_TRIPS_KEY("sonar.trip.delay"),
	SV_SONAR_IR_ANGLE_PAN_KEY("sonar.ir.angle.pan"),
	SV_SONAR_IR_ANGLE_TILT_KEY("sonar.ir.angle.tilt"),
	SV_IR_DISTANCE_THRES_FEET_KEY("ir.distance.threshold.feet"),
	SV_IR_DISTANCE_THRES_INCHES_KEY("ir.distance.threshold.inches"),
	SV_IR_DELAY_BTWN_TRIPS_KEY("ir.trip.delay"),
	SV_MW_ALARM_ON_KEY("microwave.alarm.on"),
	SV_MW_SPEED_THRES_CYCLES_PER_SEC_KEY("microwave.speed.threshold"),
	SV_MW_DELAY_BTWN_TRIPS_KEY("microwave.trip.delay"),
	SV_MW_ANGLE_PAN_KEY("microwave.angle.pan"),
	SV_MULTI_ALARM_TRIP_STATE_KEY("multi.alarm.trip.state"),
	SV_CAM_RES_KEY("cam.resolution"),
	SV_CAM_ANGLE_PAN_KEY("cam.angle.pan"),
	SV_CAM_ANGLE_TILT_KEY("cam.angle.pan"),
	SV_CAM_SONAR_TRIP_ANGLE_PAN_KEY("cam.sonar.trip.angle.pan"),
	SV_CAM_SONAR_TRIP_ANGLE_TILT_KEY("cam.sonar.trip.angle.tilt"),
	SV_CAM_IR_TRIP_ANGLE_PAN_KEY("cam.ir.trip.angle.pan"),
	SV_CAM_IR_TRIP_ANGLE_TILT_KEY("cam.ir.trip.angle.tilt"),
	SV_CAM_MW_TRIP_ANGLE_PAN_KEY("cam.mw.trip.angle.pan"),
	SV_CAM_MW_TRIP_ANGLE_TILT_KEY("cam.mw.trip.angle.tilt");
	
	/**
	 * The preference key used to extract the settings value
	 */
	public final String key;
	private Settings(final String key) {
		this.key = key;
	}
}
