package org.ugate.resources;

import java.io.InputStream;

import javafx.scene.CacheHint;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.AudioClip;

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
	public static final String IMG_GRAPH = "graph.png";
	public static final String IMG_LOGS = "logs.png";
	public static final String IMG_CAM_VGA = "cam-vga.png";
	public static final String IMG_CAM_DOME = "camera.png";
	public static final String IMG_NAV_CAM = "nav-cam.png";
	public static final String IMG_SENSOR_ARM = "camera.png";
	public static final String IMG_NAV_SENSOR = "nav-sensor.png";
	public static final String IMG_EMAIL_SELECTED = "email-selected.png";
	public static final String IMG_EMAIL_DESELECTED = "email.png";
	public static final String IMG_GATE_SELECTED = "gate-selected.png";
	public static final String IMG_GATE_DESELECTED = "gate.png";
	public static final String IMG_GATE_CLOSED = "gate-closed.png";
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
	public static final String IMG_TILT = "tilt.png";
	
	private RS() {
	}
	
	/**
	 * Gets an image view from a set of cached images
	 * 
	 * @param fileName the image file name
	 * @return the image view
	 */
	public static ImageView imgView(final String fileName) {
		final ImageView node = new ImageView(img(fileName));
		node.setSmooth(true);
		node.setCache(true);
		node.setCacheHint(CacheHint.SPEED);
		return node;
	}
	
	/**
	 * Gets an image view
	 * 
	 * @param image the image
	 * @return the image view
	 */
	public static ImageView imgView(final Image image) {
		final ImageView node = new ImageView(image);
		node.setSmooth(true);
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
		return new Image(path(fileName));
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
	public static AudioClip newAudioClip(String fileName) {
		return new AudioClip(RS.class.getResource(fileName).getPath()
				.replace("/C", "file"));
	}
}
