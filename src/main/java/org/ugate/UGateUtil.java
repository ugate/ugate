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

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

/**
 * General utility
 */
public class UGateUtil {

	// commands
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
	public static final HashMap<Integer, String> CMDS = new HashMap<Integer, String>();
	static {
		CMDS.put(CMD_SERVO_TILT_UP, "Tilts the selected servo up");
		CMDS.put(CMD_SERVO_TILT_DOWN, "Tilts the selected servo down");
		CMDS.put(CMD_SERVO_PAN_RIGHT, "Pans the selected servo to the right");
		CMDS.put(CMD_SERVO_PAN_LEFT, "Pans the selected servo to the left");
		CMDS.put(CMD_IR_REMOTE_SESSION_RESET, "Resets the universal remote control session");
		CMDS.put(CMD_SENSOR_ALARM_TOGGLE, "Turns the sensor alarms on and off");
		CMDS.put(CMD_CAM_TAKE_PIC, "Takes a picture at a predefined resolution");
		CMDS.put(CMD_ACCESS_CODE_CHANGE, "Changes the access code");
		CMDS.put(CMD_SERVO_TOGGLE_CAM_SONARIR, "Toggle between the camera and sonar/IR servo");
		CMDS.put(CMD_GATE_TOGGLE_OPEN_CLOSE, "Toggle opening and closing the gate (if applicable)");
		CMDS.put(CMD_SERVO_CAM_MOVE, "Moves the camera (followed by a servo movement command)");
		CMDS.put(CMD_SERVO_SONAR_MOVE, "Moves the sonar/IR armature (followed by a servo movement command)");
		CMDS.put(CMD_SERVO_MICROWAVE_MOVE, "Moves the microwave armature (followed by a servo movement command)");
		CMDS.put(CMD_SENSOR_GET_READINGS, "Retrieves the current sensor readings");
		CMDS.put(CMD_SENSOR_GET_SETTINGS, "Gets all of the settings variables from the remote device");
		CMDS.put(CMD_SENSOR_SET_SETTINGS, "Sets all of the settings variables on the remote device");
	}
	/**
	 * Available XBee baud rates
	 */
	public static final Integer[] XBEE_BAUD_RATES = {1200, 2400, 4800, 9600, 19200, 38400, 57600, 115200, 230400};
	
	// preference values (host use only) settings values (host and node use)
	
	public static final String PV_SOUNDS_ON_KEY = "sounds.on";
	public static final String PV_USE_METRIC_KEY = "metric.on";
	public static final String PV_CAM_IMG_CAPTURE_RETRY_CNT_KEY = "cam.img.capture.retries";
	public static final String SV_WIRELESS_COM_PORT_KEY = "wireless.com.port";
	public static final String SV_WIRELESS_BAUD_RATE_KEY = "wireless.baud.rate";
	public static final String SV_WIRELESS_ADDRESS_HOST_KEY = "wireless.address.host";
	public static final String SV_WIRELESS_ADDRESS_NODE_PREFIX_KEY = "wireless.address.node.";
	public static final String MAIL_COMMAND_DELIMITER = ";";
	public static final String MAIL_RECIPIENTS_DELIMITER = ";";
	public static final String PV_MAIL_RECIPIENTS_KEY = "mail.recipients";
	public static final String PV_MAIL_RECIPIENTS_ON_KEY = "mail.recipients.on";
	public static final String PV_MAIL_SMTP_HOST_KEY = "mail.smtp.host";
	public static final String PV_MAIL_SMTP_PORT_KEY = "mail.smtp.port";
	public static final String PV_MAIL_IMAP_HOST_KEY = "mail.imap.host";
	public static final String PV_MAIL_IMAP_PORT_KEY = "mail.imap.port";
	public static final String PV_MAIL_USERNAME_KEY = "mail.username";
	public static final String PV_MAIL_PASSWORD_KEY = "mail.password";
	public static final String PV_MAIL_ALARM_ON_KEY = "mail.alarm.on";
	public static final String SV_ACCESS_CODE_1_KEY = "access.code.one";
	public static final String SV_ACCESS_CODE_2_KEY = "access.code.two";
	public static final String SV_ACCESS_CODE_3_KEY = "access.code.three";
	public static final String SV_SONAR_ALARM_ON_KEY = "sonar.alarm.on";
	public static final String SV_IR_ALARM_ON_KEY = "ir.alarm.on";
	public static final String SV_GATE_ACCESS_ON_KEY = "gate.access.on";
	public static final String SV_SONAR_DISTANCE_THRES_FEET_KEY = "sonar.distance.threshold.feet";
	public static final String SV_SONAR_DISTANCE_THRES_INCHES_KEY = "sonar.distance.threshold.inches";
	public static final String SV_SONAR_DELAY_BTWN_TRIPS_KEY = "sonar.trip.delay";
	public static final String SV_SONAR_IR_ANGLE_PAN_KEY = "sonar.ir.angle.pan";
	public static final String SV_SONAR_IR_ANGLE_TILT_KEY = "sonar.ir.angle.tilt";
	public static final String SV_IR_DISTANCE_THRES_FEET_KEY = "ir.distance.threshold.feet";
	public static final String SV_IR_DISTANCE_THRES_INCHES_KEY = "ir.distance.threshold.inches";
	public static final String SV_IR_DELAY_BTWN_TRIPS_KEY = "ir.trip.delay";
	public static final String SV_MW_ALARM_ON_KEY = "microwave.alarm.on";
	public static final String SV_MW_SPEED_THRES_CYCLES_PER_SEC_KEY = "microwave.speed.threshold";
	public static final String SV_MW_DELAY_BTWN_TRIPS_KEY = "microwave.trip.delay";
	public static final String SV_MW_ANGLE_PAN_KEY = "microwave.angle.pan";
	public static final String SV_MULTI_ALARM_TRIP_STATE_KEY = "multi.alarm.trip.state";
	public static final String SV_CAM_RES_KEY = "cam.resolution";
	public static final String SV_CAM_ANGLE_PAN_KEY = "cam.angle.pan";
	public static final String SV_CAM_ANGLE_TILT_KEY = "cam.angle.pan";
	public static final String SV_CAM_SONAR_TRIP_ANGLE_PAN_KEY = "cam.sonar.trip.angle.pan";
	public static final String SV_CAM_SONAR_TRIP_ANGLE_TILT_KEY = "cam.sonar.trip.angle.tilt";
	public static final String SV_CAM_IR_TRIP_ANGLE_PAN_KEY = "cam.ir.trip.angle.pan";
	public static final String SV_CAM_IR_TRIP_ANGLE_TILT_KEY = "cam.ir.trip.angle.tilt";
	public static final String SV_CAM_MW_TRIP_ANGLE_PAN_KEY = "cam.mw.trip.angle.pan";
	public static final String SV_CAM_MW_TRIP_ANGLE_TILT_KEY = "cam.mw.trip.angle.tilt";
	
	// wireless network addresses
	
	public static final int WIRELESS_ADDRESS_MAX_DIGITS = 4;
	public static final int WIRELESS_ADDRESS_NODE_1 = 0x33;
	public static final int WIRELESS_ADDRESS_HOST = 0x77;

	private static final String CAPTURE_PATH = "/ugate";
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
	 * @return the path to the image files
	 */
	public static File imagePath() {
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
		return imageFormatName(f);
	}

	/**
	 * Gets the image format name from an input stream
	 * 
	 * @param is
	 *            the input stream
	 * @return null if the format is not a known image format
	 */
	public static String imageFormatFromStream(final InputStream is) {
		return imageFormatName(is);
	}

	/**
	 * Returns the format name of the image in the object
	 * 
	 * @param o
	 *            can be either a File or InputStream object
	 * @return null if the format is not a known image format
	 */
	private static String imageFormatName(final Object o) {
		try {
			// Create an image input stream on the image
			final ImageInputStream iis = ImageIO.createImageInputStream(o);

			// Find all image readers that recognize the image format
			final Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
			if (!iter.hasNext()) {
				// No readers found
				return null;
			}

			// Use the first reader
			final ImageReader reader = iter.next();

			// Close stream
			iis.close();

			// Return the format name
			return reader.getFormatName();
		} catch (final Exception e) {
		}
		// The image could not be read
		return null;
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
