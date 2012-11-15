package org.ugate;


/**
 * Commands sent/received to/from remote devices
 */
public enum Command {
	SERVO_LASER_CALIBRATE(11, true, true), 
	SERVO_TILT_UP(16, false, true), 
	SERVO_TILT_DOWN(17, false, true), 
	SERVO_PAN_RIGHT(18, false, true), 
	SERVO_PAN_LEFT(19, false, true), 
	IR_REMOTE_SESSION_RESET(20, false, true), 
	SENSOR_ALARM_TOGGLE(21, false, true), 
	CAM_TAKE_PIC(29, false, true), 
	ACCESS_PIN_CHANGE(37, true, true), 
	SERVO_TOGGLE_CAM_SONARIR_MICROWAVE(58, false, true), 
	GATE_TOGGLE_OPEN_CLOSE(59, true, true),  
	SERVO_CAM_MOVE(100, false, true, 2), 
	SERVO_SONAR_PIR_MOVE(101, false, true, 2), 
	SERVO_MICROWAVE_MOVE(102, false, true, 2), 
	SENSOR_GET_READINGS(103, true, false), 
	SENSOR_GET_SETTINGS(104, true, false), 
	SENSOR_SET_SETTINGS(105, false, true, 35);

	private final int key;
	private final boolean rx;
	private final boolean tx;
	private final int dataBytes;

	/**
	 * Constructor
	 * 
	 * @param key
	 *            the {@link #getKey()}
	 * @param isRx
	 *            the {@link #isRx()}
	 * @param isTx
	 *            the {@link #isTx()}
	 */
	private Command(final int key, final boolean isRx, final boolean isTx) {
		this.key = key;
		this.rx = isRx;
		this.tx = isTx;
		this.dataBytes = 0;
	}

	/**
	 * Constructor
	 * 
	 * @param key
	 *            the {@link #getKey()}
	 * @param isRx
	 *            the {@link #isRx()}
	 * @param isTx
	 *            the {@link #isTx()}
	 * @param the
	 *            {@link #getDataBytes()}
	 */
	private Command(final int key, final boolean isRx, final boolean isTx,
			final int byteCount) {
		this.key = key;
		this.rx = isRx;
		this.tx = isTx;
		this.dataBytes = byteCount;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return String.format("%1$s (id = %2$s, canRx = %3$s, canTx = %4$s)",
				super.toString(), key, rx, tx);
	}

	/**
	 * Looks up a command by ID
	 * 
	 * @param id
	 *            the ID to lookup
	 * @return the command (null if no command ID matches the supplied ID
	 */
	public static Command lookup(final int id) {
		for (final Command command : Command.values()) {
			if (id == command.key) {
				return command;
			}
		}
		return null;
	}

	/**
	 * @return The key recognized by the remote device
	 */
	public int getKey() {
		return key;
	}

	/**
	 * @return True when the  {@link Command} can receive data from remote devices
	 */
	public boolean isRx() {
		return rx;
	}

	/**
	 * @return True when the  {@link Command} can transmit data to remote devices
	 */
	public boolean isTx() {
		return tx;
	}

	/**
	 * @return the number of bytes that is expected for the {@link Command}
	 *         (excluding the {@link #getKey()} byte)
	 */
	public int getDataBytes() {
		return dataBytes;
	}
}
