package org.ugate.resources;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.scene.CacheHint;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.AudioClip;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public class RS {

	public static final String HTML_SERVO_NAV = "nav.html";
	public static final String CSS_MAIN = "main.css";
	public static final String CSS_DISPLAY_SHELF = "displayshelf.css";
	public static final String IMG_LOGO_128 = "logo128x128.png";
	public static final String IMG_LOGO_64 = "logo64x64.png";
	public static final String IMG_LOGO_16 = "logo16x16.png";
	public static final String IMG_SKIN_MIN = "ugskin-btn-min32x10.png";
	public static final String IMG_SKIN_MAX = "ugskin-btn-max25x10.png";
	public static final String IMG_SKIN_RESTORE = "ugskin-btn-res25x10.png";
	public static final String IMG_SKIN_CLOSE = "ugskin-btn-close32x10.png";
	public static final String IMG_CONNECT = "power-red.png";
	public static final String IMG_PICS = "pics.png";
	public static final String IMG_WIRELESS = "wireless-globe.png";
	public static final String IMG_WIRELESS_ICON = "wireless-icon.png";
	public static final String IMG_GRAPH = "graph.png";
	public static final String IMG_LOGS = "logs.png";
	public static final String IMG_CAM_VGA = "cam-vga.png";
	public static final String IMG_CAM_QVGA = "cam-qvga.png";
	public static final String IMG_CAM_TOGGLE_VGA = "cam-toggle-vga.png";
	public static final String IMG_CAM_TOGGLE_QVGA = "cam-toggle-qvga.png";
	public static final String IMG_SENSOR_ARM = "camera.png";
	public static final String IMG_EMAIL_NOTIFY_ON = "email-notify-on.png";
	public static final String IMG_EMAIL_NOTIFY_OFF = "email-notify-off.png";
	public static final String IMG_EMAIL_ICON = "email-icon.png";
	public static final String IMG_GATE_ON = "gate-on.png";
	public static final String IMG_GATE_OFF = "gate-off.png";
	public static final String IMG_GATE_CLOSED = "gate-closed.png";
	public static final String IMG_GATE_OPENED = "gate-opened.png";
	public static final String IMG_RULER = "ruler.png";
	public static final String IMG_STOPWATCH = "stopwatch.png";
	public static final String IMG_SONAR_ALARM_ON = "sonar-alarm-on.png";
	public static final String IMG_SONAR_ALARM_OFF = "sonar-alarm-off.png";
	public static final String IMG_IR_ALARM_ON = "ir-alarm-on.png";
	public static final String IMG_IR_ALARM_OFF = "ir-alarm-off.png";
	public static final String IMG_MICROWAVE_ALARM_ON = "mw-alarm-on.png";
	public static final String IMG_MICROWAVE_ALARM_OFF = "mw-alarm-off.png";
	public static final String IMG_SPEEDOMETER = "speedometer.png";
	public static final String IMG_PAN = "pan.png";
	public static final String IMG_READINGS_GET = "readings-get48x48.png";
	public static final String IMG_SETTINGS_SET = "settings-set48x48.png";
	public static final String IMG_SONAR = "sonar32x32.png";
	public static final String IMG_PIR = "pir32x32.png";
	public static final String IMG_MICROWAVE = "mw32x32.png";
	public static final String IMG_SOUND_ON = "sound-on.png";
	public static final String IMG_SOUND_OFF = "sound-off.png";
	public static final String IMG_UNIVERSAL_REMOTE_ON = "universal-remote-on.png";
	public static final String IMG_UNIVERSAL_REMOTE_OFF = "universal-remote-off.png";
	private static final String RB_GUI = "LabelsBundle";
	private static final Map<String, Image> IMGS = new HashMap<String, Image>();
	
	private RS() {
	}
	
	/**
	 * Creates an image view from a set of cached images
	 * 
	 * @param fileName the image file name
	 * @return the image view
	 */
	public static ImageView imgView(final String fileName) {
		return imgView(img(fileName));
	}
	
	/**
	 * Creates an image view from a set of cached images
	 * 
	 * @param fileName the image file name
	 * @param key the preferences key to associate the image with
	 * @return the image view
	 */
	public static ImageView imgView(final String fileName, final String key) {
		return imgView(img(fileName), key);
	}
	
	/**
	 * Creates an image view
	 * 
	 * @param image the image
	 * @return the image view
	 */
	public static ImageView imgView(final Image image) {
		return imgView(image, null);
	}
	
	/**
	 * Creates an image view
	 * 
	 * @param image the image
	 * @param key the preferences key to associate the image with
	 * @return the image view
	 */
	public static ImageView imgView(final Image image, final String key) {
		final ImageView node = new ImageView(image);
		node.setSmooth(false);
		node.setCache(true);
		node.setCacheHint(CacheHint.SPEED);
		return node;
	}
	
	/**
	 * Gets an image from a set of cached images
	 * 
	 * @param fileName the image file name
	 * @return the image
	 */
	public static Image img(final String fileName) {
		if (IMGS.containsKey(fileName)) {
			return IMGS.get(fileName);
		} else {
			final Image img = new Image(path(fileName));
			IMGS.put(fileName, img);
			return img;
		}
	}
	
	/**
	 * Gets a resource stream
	 * 
	 * @param fileName the resource file name
	 * @return the resource stream
	 */
	public static InputStream stream(final String fileName) {
		return RS.class.getResourceAsStream(fileName);
	}
	
	/**
	 * Gets a resource path
	 * 
	 * @param fileName the resource file name
	 * @return the resource path
	 */
	public static String path(final String fileName) {
		return RS.class.getResource(fileName).toExternalForm();
	}
	
	/**
	 * Creates an audio clip based on the file path
	 * 
	 * @param fileName
	 *            the file path with file name
	 * @return the audio clip
	 */
	public static AudioClip audioClip(final String fileName) {
		return new AudioClip(RS.class.getResource(fileName).getPath()
				.replace("/C", "file"));
	}

	/**
	 * Gets the image format name from a file
	 * 
	 * @param f
	 *            the file
	 * @return null if the format is not a known image format
	 */
	public static String imgFormatInFile(final File f) {
		return imgFormatName(f);
	}

	/**
	 * Gets the image format name from an input stream
	 * 
	 * @param is
	 *            the input stream
	 * @return null if the format is not a known image format
	 */
	public static String imgFormatFromStream(final InputStream is) {
		return imgFormatName(is);
	}

	/**
	 * Returns the format name of the image in the object
	 * 
	 * @param o
	 *            can be either a File or InputStream object
	 * @return null if the format is not a known image format
	 */
	private static String imgFormatName(final Object o) {
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
	 * Gets the first available locale and the label resource bundles 
	 * value for the specified key
	 * 
	 * @param key the key of the resource bundle value
	 * @param formatArguments the {@linkplain String#format(Locale, String, Object...)} arguments
	 * @return the resource bundle value
	 */
	public static String rbLabel(final String key, final Object... formatArguments) {
		return rbLabel(Locale.getAvailableLocales()[0], key, formatArguments);
	}
	
	/**
	 * Gets the a label resource bundles value for the specified key
	 *  
	 * @param locale the locale of the resource bundle
	 * @param key the key of the resource bundle value
	 * @param formatArguments the {@linkplain String#format(Locale, String, Object...)} arguments
	 * @return the resource bundle value
	 */
	public static String rbLabel(final Locale locale, final String key, final Object... formatArguments) {
		return rbValue(RB_GUI, locale, key, formatArguments);
	}
	
	/**
	 * Gets the a resource bundles value for the specified key
	 *  
	 * @param rb the resource bundle name
	 * @param locale the locale of the resource bundle
	 * @param key the key of the resource bundle value
	 * @param formatArguments the {@linkplain String#format(Locale, String, Object...)} arguments
	 * @return the resource bundle value
	 */
	private static String rbValue(final String rb, final Locale locale, final String key, final Object... formatArguments) {
		final String rbStr = ResourceBundle.getBundle(rb, locale).getString(key);
		return formatArguments != null && formatArguments.length > 0 ? String.format(locale, rbStr, formatArguments) : rbStr;
	}
}
