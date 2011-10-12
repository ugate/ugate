package org.ugate;

import java.io.File;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public class UGateUtil {

	public static final int CMD_SERVO_TILT_UP = 16;
	public static final int CMD_SERVO_TILT_DOWN = 17;
	public static final int CMD_SERVO_PAN_RIGHT = 18;
	public static final int CMD_SERVO_PAN_LEFT = 19;
	public static final int CMD_IR_REMOTE_SESSION_RESET = 20;
	public static final int CMD_SENSOR_ALARM_TOGGLE = 21;
	public static final int CMD_CAM_TAKE_PIC = 29;
	public static final int CMD_ACCESS_CODE_CHANGE = 37;
	public static final int CMD_SERVO_TOGGLE_CAM_SONARIR = 58;
	public static final int CMD_GATE_TOGGLE_OPEN_CLOSE = 59;
	public static final int CMD_SERVO_CAM_MOVE = 100;
	public static final int CMD_SERVO_SONAR_MOVE = 101;
	public static final int CMD_SERVO_MICROWAVE_MOVE = 102;
	public static final int CMD_SENSOR_GET_READINGS = 103;
	public static final int CMD_SENSOR_GET_SETTINGS = 104;
	public static final int CMD_SENSOR_SET_SETTINGS = 105;
	
	/**
	 * Commands that can be sent to the remote micro controller
	 */
	public static final HashMap<String, Integer> GATE_COMMANDS = new HashMap<String, Integer>();
	static {
		GATE_COMMANDS.put("Tilts the selected servo up", CMD_SERVO_TILT_UP);
		GATE_COMMANDS.put("Tilts the selected servo down", CMD_SERVO_TILT_DOWN);
		GATE_COMMANDS.put("Pans the selected servo to the right", CMD_SERVO_PAN_RIGHT);
		GATE_COMMANDS.put("Pans the selected servo to the left", CMD_SERVO_PAN_LEFT);
		GATE_COMMANDS.put("Resets the universal remote control session", CMD_IR_REMOTE_SESSION_RESET);
		GATE_COMMANDS.put("Turns the sensor alarms on and off", CMD_SENSOR_ALARM_TOGGLE);
		GATE_COMMANDS.put("Takes a picture at a predefined resolution", CMD_CAM_TAKE_PIC);
		GATE_COMMANDS.put("Changes the access code", CMD_ACCESS_CODE_CHANGE);
		GATE_COMMANDS.put("Toggle between the camera and sonar/IR servo", CMD_SERVO_TOGGLE_CAM_SONARIR);
		GATE_COMMANDS.put("Toggle opening and closing the gate (if applicable)", CMD_GATE_TOGGLE_OPEN_CLOSE);
		GATE_COMMANDS.put("Moves the camera (followed by a servo movement command)", CMD_SERVO_CAM_MOVE);
		GATE_COMMANDS.put("Moves the sonar/IR armature (followed by a servo movement command)", CMD_SERVO_SONAR_MOVE);
		GATE_COMMANDS.put("Moves the microwave armature (followed by a servo movement command)", CMD_SERVO_MICROWAVE_MOVE);
		GATE_COMMANDS.put("Retrieves the current sensor readings", CMD_SENSOR_GET_READINGS);
		GATE_COMMANDS.put("Gets all of the settings variables from the remote device", CMD_SENSOR_GET_SETTINGS);
		GATE_COMMANDS.put("Sets all of the settings variables on the remote device", CMD_SENSOR_SET_SETTINGS);
	}
	/**
	 * Available XBee baud rates
	 */
	public static final Integer[] XBEE_BAUD_RATES = {1200, 2400, 4800, 9600, 19200, 38400, 57600, 115200, 230400};
	public static final String XBEE_COM_PORT_KEY = "xbee.com.port";
	public static final String XBEE_BAUD_RATE_KEY = "xbee.baud.rate";
	public static final String MAIL_COMMAND_DELIMITER = ";";
	public static final String MAIL_RECIPIENTS_DELIMITER = ";";
	public static final String MAIL_RECIPIENTS_KEY = "mail.recipients";
	public static final String MAIL_RECIPIENTS_ON_KEY = "mail.recipients.on";
	public static final String MAIL_SMTP_HOST_KEY = "mail.smtp.host";
	public static final String MAIL_SMTP_PORT_KEY = "mail.smtp.port";
	public static final String MAIL_IMAP_HOST_KEY = "mail.imap.host";
	public static final String MAIL_IMAP_PORT_KEY = "mail.imap.port";
	public static final String MAIL_USERNAME_KEY = "mail.username";
	public static final String MAIL_PASSWORD_KEY = "mail.password";
	public static final String MAIL_ALARM_ON_KEY = "mail.alarm.on";
	public static final String SONAR_ALARM_ON_KEY = "sonar.alarm.on";
	public static final String IR_ALARM_ON_KEY = "ir.alarm.on";
	public static final String MICROWAVE_ALARM_ON_KEY = "microwave.alarm.on";
	public static final String GATE_ACCESS_ON_KEY = "gate.access.on";

	private static final String CAPTURE_PATH = "/ugate";
	public static final ExecutorService EXEC_SRVC = Executors.newCachedThreadPool();
	public static final String DATE_FORMAT_NOW = "yyyy-MM-dd hh:mm:ss";
	
	private static final long K = 1024;
	private static final long M = K * K;
	private static final long G = M * K;
	private static final long T = G * K;

	private UGateUtil() {
	}

	public static File getCapturePath() {
		File filePath = new File(CAPTURE_PATH);
		if (!filePath.exists()) {
			try {
				filePath.mkdirs();
			} catch (Exception e) {

			}
		}
		return filePath;
	}

	/**
	 * Gets the image format name from a file
	 * 
	 * @param f
	 *            the file
	 * @return null if the format is not a known image format
	 */
	public static String getImageFormatInFile(File f) {
		return getImageFormatName(f);
	}

	/**
	 * Gets the image format name from an input stream
	 * 
	 * @param is
	 *            the input stream
	 * @return null if the format is not a known image format
	 */
	public static String getImageFormatFromStream(InputStream is) {
		return getImageFormatName(is);
	}

	/**
	 * Returns the format name of the image in the object
	 * 
	 * @param o
	 *            can be either a File or InputStream object
	 * @return null if the format is not a known image format
	 */
	private static String getImageFormatName(Object o) {
		try {
			// Create an image input stream on the image
			ImageInputStream iis = ImageIO.createImageInputStream(o);

			// Find all image readers that recognize the image format
			Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
			if (!iter.hasNext()) {
				// No readers found
				return null;
			}

			// Use the first reader
			ImageReader reader = iter.next();

			// Close stream
			iis.close();

			// Return the format name
			return reader.getFormatName();
		} catch (Exception e) {
		}
		// The image could not be read
		return null;
	}

	public static String convertToByteLabel(final long value) {
		final long[] dividers = new long[] { T, G, M, K, 1 };
		final String[] units = new String[] { "TB", "GB", "MB", "KB", "B" };
		if (value < 0)
			throw new IllegalArgumentException("Invalid file size: " + value);
		String result = null;
		for (int i = 0; i < dividers.length; i++) {
			final long divider = dividers[i];
			if (value >= divider) {
				result = format(value, divider, units[i]);
				break;
			}
		}
		return result;
	}

	public static <T> T[] concatArray(T[] original, T[] appender) {
		T[] result = Arrays.copyOf(original, original.length + appender.length);
		System.arraycopy(appender, 0, result, original.length, appender.length);
		return result;
	}

	public static <T> T[] concatArrays(T[] original, T[]... rest) {
		int totalLength = original.length;
		for (T[] array : rest) {
			totalLength += array.length;
		}
		T[] result = Arrays.copyOf(original, totalLength);
		int offset = original.length;
		for (T[] array : rest) {
			System.arraycopy(array, 0, result, offset, array.length);
			offset += array.length;
		}
		return result;
	}

	private static String format(final long value, final long divider,
			final String unit) {
		final double result = divider > 1 ? (double) value / (double) divider
				: (double) value;
		return new DecimalFormat("#,##0.#").format(result) + " " + unit;
	}
	
	public static String formatCal(Calendar cal) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return sdf.format(cal.getTime());
	}

	public static String now() {
		return formatCal(Calendar.getInstance());
	}
	
	public static String formatDateDifference(Date start, Date end) {
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
