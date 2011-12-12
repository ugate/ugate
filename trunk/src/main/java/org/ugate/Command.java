package org.ugate;

/**
 * Commands sent to remote nodes
 */
public enum Command {
	SERVO_TILT_UP(16), SERVO_TILT_DOWN(17), SERVO_PAN_RIGHT(18), SERVO_PAN_LEFT(19),
	IR_REMOTE_SESSION_RESET(20), SENSOR_ALARM_TOGGLE(21), CAM_TAKE_PIC(29), 
	ACCESS_CODE_CHANGE(37), SERVO_TOGGLE_CAM_SONARIR(58), GATE_TOGGLE_OPEN_CLOSE(59), 
	SERVO_CAM_MOVE(100), SERVO_SONAR_MOVE(101), SERVO_MICROWAVE_MOVE(102), 
	SENSOR_GET_READINGS(103), SENSOR_GET_SETTINGS(104), SENSOR_SET_SETTINGS(105);
	
	/**
	 * The id recognized by the remote node
	 */
	public final int id;
	private Command(final int id) {
		this.id = id;
	}
}
