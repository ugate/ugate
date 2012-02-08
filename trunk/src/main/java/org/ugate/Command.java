package org.ugate;

/**
 * Commands sent/received to/from remote nodes
 */
public enum Command {
	SERVO_LASER_CALIBRATE(11, false, true),
	SERVO_TILT_UP(16, false, true), 
	SERVO_TILT_DOWN(17, false, true), 
	SERVO_PAN_RIGHT(18, false, true), 
	SERVO_PAN_LEFT(19, false, true),
	IR_REMOTE_SESSION_RESET(20, false, true), 
	SENSOR_ALARM_TOGGLE(21, false, true), 
	CAM_TAKE_PIC(29, false, true), 
	ACCESS_CODE_CHANGE(37, true, true), 
	SERVO_TOGGLE_CAM_SONARIR(58, false, true), 
	GATE_TOGGLE_OPEN_CLOSE(59, true, true), 
	SERVO_CAM_MOVE(100, false, true), 
	SERVO_SONAR_PIR__MOVE(101, false, true), 
	SERVO_MICROWAVE_MOVE(102, false, true),
	SENSOR_GET_READINGS(103, true, false), 
	SENSOR_GET_SETTINGS(104, true, false),
	SENSOR_SET_SETTINGS(105, false, true);
	
	/**
	 * The id recognized by the remote node
	 */
	public final int id;
	/**
	 * True when the command can receive data from remote nodes
	 */
	public final boolean canRx;
	/**
	 * True when the command can transmit data to remote nodes
	 */
	public final boolean canTx;
	
	/**
	 * Constructor
	 * 
	 * @param id the ID
	 * @param canRx can RX
	 * @param canTx can TX
	 */
	private Command(final int id, final boolean canRx, final boolean canTx) {
		this.id = id;
		this.canRx = canRx;
		this.canTx = canTx;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return String.format("%1$s (id = %2$s, canRx = %3$s, canTx = %4$s)", super.toString(), id, canRx, canTx);
	}
	
	/**
	 * Looks up a command by ID
	 * 
	 * @param id the ID to lookup
	 * @return the command (null if no command ID matches the supplied ID
	 */
	public static Command lookup(final int id) {
		for (final Command command : Command.values()) {
			if (id == command.id) {
				return command;
			}
		}
		return null;
	}
}
