package org.ugate;

/**
 * Settings preference keys
 */
public enum Settings {
	WORKING_DIR_PATH("working.dir.path", false),
	SOUNDS_ON("sounds.on", false),
	USE_METRIC("metric.on", false),
	CAM_IMG_CAPTURE_RETRY_CNT("cam.img.capture.retries", false),
	WIRELESS_COM_PORT("wireless.com.port", false),
	WIRELESS_BAUD_RATE("wireless.baud.rate", false),
	WIRELESS_ADDRESS_HOST("wireless.address.host", false),
	WIRELESS_ADDRESS_NODE_PREFIX("wireless.address.node.", false),
	MAIL_INBOX_NAME("mail.inbox.name", false),
	MAIL_RECIPIENTS("mail.recipients", false),
	MAIL_RECIPIENTS_ON("mail.recipients.on", false),
	MAIL_SMTP_HOST("mail.smtp.host", false),
	MAIL_SMTP_PORT("mail.smtp.port", false),
	MAIL_IMAP_HOST("mail.imap.host", false),
	MAIL_IMAP_PORT("mail.imap.port", false),
	MAIL_USERNAME("mail.username", false),
	MAIL_PASSWORD("mail.password", false),
	MAIL_ALARM_ON("mail.alarm.on", false),
	UNIVERSAL_REMOTE_ACCESS_ON("universal.remote.access.on", true),
	ACCESS_CODE_1("access.code.one", true),
	ACCESS_CODE_2("access.code.two", true),
	ACCESS_CODE_3("access.code.three", true),
	SONAR_ALARM_ON("sonar.alarm.on", true),
	IR_ALARM_ON("ir.alarm.on", true),
	GATE_ACCESS_ON("gate.access.on", true),
	SONAR_DISTANCE_THRES_FEET("sonar.distance.threshold.feet", true),
	SONAR_DISTANCE_THRES_INCHES("sonar.distance.threshold.inches", true),
	SONAR_DELAY_BTWN_TRIPS("sonar.trip.delay", true),
	SONAR_IR_ANGLE_PAN("sonar.ir.angle.pan", true),
	SONAR_IR_ANGLE_TILT("sonar.ir.angle.tilt", true),
	IR_DISTANCE_THRES_FEET("ir.distance.threshold.feet", true),
	IR_DISTANCE_THRES_INCHES("ir.distance.threshold.inches", true),
	IR_DELAY_BTWN_TRIPS("ir.trip.delay", true),
	MW_ALARM_ON("microwave.alarm.on", true),
	MW_SPEED_THRES_CYCLES_PER_SEC("microwave.speed.threshold", true),
	MW_DELAY_BTWN_TRIPS("microwave.trip.delay", true),
	MW_ANGLE_PAN("microwave.angle.pan", true),
	MULTI_ALARM_TRIP_STATE("multi.alarm.trip.state", true),
	CAM_RES("cam.resolution", true),
	CAM_ANGLE_PAN("cam.angle.pan", true),
	CAM_ANGLE_TILT("cam.angle.pan", true),
	CAM_SONAR_TRIP_ANGLE_PAN("cam.sonar.trip.angle.pan", true),
	CAM_SONAR_TRIP_ANGLE_TILT("cam.sonar.trip.angle.tilt", true),
	CAM_IR_TRIP_ANGLE_PAN("cam.ir.trip.angle.pan", true),
	CAM_IR_TRIP_ANGLE_TILT("cam.ir.trip.angle.tilt", true),
	CAM_MW_TRIP_ANGLE_PAN("cam.mw.trip.angle.pan", true),
	CAM_MW_TRIP_ANGLE_TILT("cam.mw.trip.angle.tilt", true);
	
	/**
	 * The preference key used to extract the settings value
	 */
	public final String key;
	/**
	 * The flag that indicates if the settings is transferable to remote nodes
	 */
	public final boolean canRemote;
	
	/**
	 * Constructor
	 * 
	 * @param key the key
	 * @param canRemote can remote
	 */
	private Settings(final String key, final boolean canRemote) {
		this.key = key;
		this.canRemote =canRemote;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return String.format("%1$s (key = %2$s, canRemote = %3$s)", super.toString(), key, canRemote);
	}
}
