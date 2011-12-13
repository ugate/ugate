package org.ugate;

import java.io.File;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

/**
 * General utility
 */
public class UGateUtil {
	
	public static final String HELP_TEXT_DEFAULT_KEY = "help.text.default";
	
//	/**
//	 * Commands that can be sent to the remote micro controller
//	 */
//	public static final HashMap<Command, String> CMDS = new HashMap<Command, String>();
//	static {
//		CMDS.put(Command.SERVO_TILT_UP, "Tilts the selected servo up");
//		CMDS.put(Command.SERVO_TILT_DOWN, "Tilts the selected servo down");
//		CMDS.put(Command.SERVO_PAN_RIGHT, "Pans the selected servo to the right");
//		CMDS.put(Command.SERVO_PAN_LEFT, "Pans the selected servo to the left");
//		CMDS.put(Command.IR_REMOTE_SESSION_RESET, "Resets the universal remote control session");
//		CMDS.put(Command.SENSOR_ALARM_TOGGLE, "Turns the sensor alarms on and off");
//		CMDS.put(Command.CAM_TAKE_PIC, "Takes a picture at a predefined resolution");
//		CMDS.put(Command.ACCESS_CODE_CHANGE, "Changes the access code");
//		CMDS.put(Command.SERVO_TOGGLE_CAM_SONARIR, "Toggle between the camera and sonar/IR servo");
//		CMDS.put(Command.GATE_TOGGLE_OPEN_CLOSE, "Toggle opening and closing the gate (if applicable)");
//		CMDS.put(Command.SERVO_CAM_MOVE, "Moves the camera (followed by a servo movement command)");
//		CMDS.put(Command.SERVO_SONAR_MOVE, "Moves the sonar/IR armature (followed by a servo movement command)");
//		CMDS.put(Command.SERVO_MICROWAVE_MOVE, "Moves the microwave armature (followed by a servo movement command)");
//		CMDS.put(Command.SENSOR_GET_READINGS, "Retrieves the current sensor readings");
//		CMDS.put(Command.SENSOR_GET_SETTINGS, "Gets all of the settings variables from the remote device");
//		CMDS.put(Command.SENSOR_SET_SETTINGS, "Sets all of the settings variables on the remote device");
//	}
	/**
	 * Available XBee baud rates
	 */
	public static final Integer[] XBEE_BAUD_RATES = {1200, 2400, 4800, 9600, 19200, 38400, 57600, 115200, 230400};
	
	/**
	 * Supported locals
	 */
	public static final Locale[] SUPPORTED_LOCALS = { Locale.ENGLISH }; 
	
	// preference values (host use only) settings values (host and node use)
	
	public static final String MAIL_COMMAND_DELIMITER = ";";
	public static final String MAIL_RECIPIENTS_DELIMITER = ";";
	
	// wireless network addresses
	
	public static final int WIRELESS_ADDRESS_MAX_DIGITS = 4;

	//public static final ExecutorService EXEC_SRVC = Executors.newCachedThreadPool();
	public static final String DATE_FORMAT_NOW = "yyyy-MM-dd hh:mm:ss";
	
	// byte sizes
	private static final long K = 1024;
	private static final long M = K * K;
	private static final long G = M * K;
	private static final long T = G * K;

	/**
	 * Private utility constructor
	 */
	private UGateUtil() {
	}

	/**
	 * Converts a value to a byte label for display purposes
	 * 
	 * @param value the value to format
	 * @return the label
	 */
	public static String byteConvertToLabel(final long value) {
		final long[] dividers = new long[] { T, G, M, K, 1 };
		final String[] units = new String[] { "TB", "GB", "MB", "KB", "B" };
		if (value < 0)
			throw new IllegalArgumentException("Invalid file size: " + value);
		String result = null;
		for (int i = 0; i < dividers.length; i++) {
			final long divider = dividers[i];
			if (value >= divider) {
				result = decimalFormatLabel(value, divider, units[i]);
				break;
			}
		}
		return result;
	}
	
	/**
	 * Concatenates two integer arrays
	 * 
	 * @param original the original
	 * @param appender the array to append
	 * @return the concatenated array
	 */
	public static int[] arrayConcatInt(final int[] original, final int[] appender) {
		final int[] result = Arrays.copyOf(original, original.length + appender.length);
		System.arraycopy(appender, 0, result, original.length, appender.length);
		return result;
	}

	/**
	 * Concatenates two arrays
	 * 
	 * @param <T> the array type
	 * @param original the original
	 * @param appender the array to append
	 * @return the concatenated array
	 */
	public static <T> T[] arrayConcat(final T[] original, final T[] appender) {
		final T[] result = Arrays.copyOf(original, original.length + appender.length);
		System.arraycopy(appender, 0, result, original.length, appender.length);
		return result;
	}

	/**
	 * Concatenates multiple arrays
	 * 
	 * @param <T> the array type
	 * @param original the original
	 * @param appenders the array to append
	 * @return the concatenated array
	 */
	public static <T> T[] arrayConcat(T[] original, T[]... appenders) {
		int totalLength = original.length;
		for (final T[] array : appenders) {
			totalLength += array.length;
		}
		final T[] result = Arrays.copyOf(original, totalLength);
		int offset = original.length;
		for (final T[] array : appenders) {
			System.arraycopy(array, 0, result, offset, array.length);
			offset += array.length;
		}
		return result;
	}

	/**
	 * Formats a decimal value using a specified divider
	 * 
	 * @param value the value to format
	 * @param divider the divider to use
	 * @param unit the unit label to append
	 * @return the decimal formatted label
	 */
	private static String decimalFormatLabel(final long value, final long divider,
			final String unit) {
		final double result = divider > 1 ? (double) value / (double) divider
				: (double) value;
		return new DecimalFormat("#,##0.#").format(result) + " " + unit;
	}
	
	/**
	 * Formats a calendar to the application wide format
	 * 
	 * @param cal the calendar to format
	 * @return the formated calendar
	 */
	public static String calFormat(final Calendar cal) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return sdf.format(cal.getTime());
	}

	/**
	 * @return the formatted current calendar
	 */
	public static String calNow() {
		return calFormat(Calendar.getInstance());
	}
	
	/**
	 * Formats the the difference in the dates
	 * 
	 * @param start the start date
	 * @param end the end date
	 * @return the formated date difference
	 */
	public static String calFormatDateDifference(final Date start, final Date end) {
		long l1 = start.getTime();
		long l2 = end.getTime();
		long diff = l2 - l1;

		long secondInMillis = 1000;
		long minuteInMillis = secondInMillis * 60;
		long hourInMillis = minuteInMillis * 60;
		long dayInMillis = hourInMillis * 24;
		long yearInMillis = dayInMillis * 365;

		long elapsedYears = diff / yearInMillis;
		diff = diff % yearInMillis;
		long elapsedDays = diff / dayInMillis;
		diff = diff % dayInMillis;
		long elapsedHours = diff / hourInMillis;
		diff = diff % hourInMillis;
		long elapsedMinutes = diff / minuteInMillis;
		diff = diff % minuteInMillis;
		long elapsedSeconds = diff / secondInMillis;
		
		return elapsedYears + " years, " + elapsedDays + " days, " + elapsedHours + " hrs, " + elapsedMinutes + " mins, " + elapsedSeconds + " secs";
	}
}
