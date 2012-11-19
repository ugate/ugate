package org.ugate;

import static org.ugate.service.entity.RemoteNodeType.Type;

/**
 * Commands sent/received to/from remote devices
 */
public enum Command {
	SERVO_LASER_CALIBRATE(11, 1, 0),
	SERVO_TILT_UP(16, 1, 0),
	SERVO_TILT_DOWN(17, 1, 0),
	SERVO_PAN_RIGHT(18, 1, 0),
	SERVO_PAN_LEFT(19, 1, 0),
	IR_REMOTE_SESSION_RESET(20, 1, 0),
	SENSOR_ALARM_TOGGLE(21, 1, 0),
	CAM_TAKE_PIC(29, 1, 0),
	ACCESS_PIN_CHANGE(37, 1, 0),
	SERVO_TOGGLE_CAM_SONARIR_MICROWAVE(58, 1, 0),
	GATE_TOGGLE_OPEN_CLOSE(59, 1, 0),
	SERVO_CAM_MOVE(100, 3, 0),
	SERVO_SONAR_PIR_MOVE(101, 3, 0),
	SERVO_MICROWAVE_MOVE(102, 3, 0),
	SENSOR_GET_READINGS(103, 1, 8),
	SENSOR_GET_SETTINGS(104, 1, 37),
	SENSOR_SET_SETTINGS(105, 37, 0);

	private final int key;
	private final int txBytes;
	private final int rxBytes;
	private final Type type;

	/**
	 * Constructor
	 * 
	 * @param key
	 *            the {@link #getKey()}
	 * @param txDataBytes
	 *            the {@link #getRxBytes()}
	 * @param rxDataBytes
	 *            the {@link #getTxBytes()}
	 */
	private Command(final int key, 
			final int txDataBytes, final int rxDataBytes) {
		this.key = key;
		this.txBytes = txDataBytes;
		this.rxBytes = rxDataBytes;
		this.type = null;
	}

	/**
	 * Constructor
	 * 
	 * @param key
	 *            the {@link #getKey()}
	 * @param txDataBytes
	 *            the {@link #getRxBytes()}
	 * @param rxDataBytes
	 *            the {@link #getTxBytes()}
	 * @param type
	 *            the {@link #getType()}
	 */
	private Command(final int key, 
			final int txDataBytes, final int rxDataBytes, final Type type) {
		this.key = key;
		this.txBytes = txDataBytes;
		this.rxBytes = rxDataBytes;
		this.type = type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return String.format("%1$s (id = %2$s, txBytes = %3$s, rxBytes = %4$s)",
				super.toString(), key, txBytes, rxBytes);
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
	 * @return the {@link Type} of {@link Command}
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @return True when the  {@link Command} can receive data from remote devices
	 */
	public boolean canRx() {
		return rxBytes > 0;
	}

	/**
	 * @return True when the  {@link Command} can transmit data to remote devices
	 */
	public boolean canTx() {
		return txBytes > 0;
	}

	/**
	 * @return the number of bytes that is expected for the {@link Command}
	 *         (excluding the {@link #getKey()} byte)
	 */
	public int getTxBytes() {
		return txBytes;
	}

	/**
	 * @return the number of bytes that is expected for the {@link Command}
	 */
	public int getRxBytes() {
		return rxBytes;
	}

	/**
	 * @return the {@link #getTxBytes()} excluding the {@link #getKey()} byte
	 */
	public int getTxDataBytes() {
		return txBytes - 1;
	}

	/**
	 * @return the {@link #getRxBytes()} excluding the {@link #getKey()} byte
	 */
	public int getRxDataBytes() {
		return rxBytes - 1;
	}
}
