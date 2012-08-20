package org.ugate;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.ugate.resources.RS.KEYS;

/**
 * General utility
 */
public class UGateUtil {
	
	public static final Logger PLAIN_LOGGER = getLogger(UGateUtil.class);
	public static final KEYS HELP_TEXT_DEFAULT_KEY = KEYS.APP_HELP_DEFAULT;
	
	/**
	 * Font used
	 */
	public static final String FONT_NAME = "Verdana";
	
	/**
	 * Supported locals
	 */
	public static final Locale[] SUPPORTED_LOCALS = { Locale.ENGLISH };

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
	 * Abstraction to isolate {@linkplain LoggerFactory} errors from
	 * {@code static} initializations
	 * 
	 * @param clazz
	 *            the {@linkplain Class} to get the logger for
	 * @return exception safe logger generator
	 */
	public static final Logger getLogger(final Class<?> clazz) {
		try {
			return LoggerFactory.getLogger(clazz);
		} catch (final Throwable t) {
			System.out.println("Unable to start services");
			t.printStackTrace();
		}
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
	@SafeVarargs
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
	
	/**
	 * Converts an array of objects to a comma separated string
	 * 
	 * @param objs the objects
	 * @return a comma separated string
	 */
	public static String toString(final Collection<?> objs) {
		return objs != null ? toString(objs.toArray()) : toString(new Object[]{});
	}
	
	/**
	 * Converts an array of objects to a comma separated string
	 * 
	 * @param objs the objects
	 * @return a comma separated string
	 */
	public static String toString(final Object[] objs) {
		final StringBuilder sb = new StringBuilder();
		sb.append('[');
		if (objs != null) {
			int i = 0;
			for (final Object obj : objs) {
				sb.append(obj.toString());
				if (i < (objs.length - 1)) {
					sb.append(',');
				}
				i++;
			}
		}
		sb.append(']');
		return sb.toString();
	}
	
	/**
	 * @return the operating system name
	 */
	public static String os() {
		return System.getProperty("os.name").toLowerCase();
	}

	/**
	 * @return true when the operating system is <code>Windows</code> based
	 */
	public static boolean isWindows() {
		return os().indexOf("win") >= 0;
	}

	/**
	 * @return true when the operating system is <code>Solaris</code> based
	 */
	public static boolean isSolaris() {
		return os().indexOf("sunos") >= 0;
	}

	/**
	 * @return true when the operating system is <code>Mac</code> based
	 */
	public static boolean isMac() {
		return os().indexOf("mac") >= 0;
	}

	/**
	 * @return true when the operating system is <code>Linux</code> based
	 */
	public static boolean isLinux() {
		return os().indexOf("nux") >= 0;
	}

	/**
	 * @return true when the operating system is <code>Unix</code> based
	 */
	public static boolean isUnix() {
		return os().indexOf("nix") >= 0 || os().indexOf("nux") >= 0;
	}
	
	/**
	 * @return the processor bit
	 */
	public static int getBitness() {
		return Integer.parseInt(System.getProperty("sun.arch.data.model"));
	}
}
