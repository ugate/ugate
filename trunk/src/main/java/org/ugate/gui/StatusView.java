package org.ugate.gui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.effect.Light;
import javafx.scene.effect.LightingBuilder;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;

/**
 * Status view for creating status icon indicators
 */
public class StatusView extends VBox {
	
	public static final double RADIUS_MIN = 7;
	public static final Color COLOR_NOT_SET = Color.web("#4977A3", 1);
	public static final Color COLOR_ON = Color.web("#00FF00", 1);
	public static final Color COLOR_OFF = Color.web("#FF0000", 1);
	public final Color colorNotSet;
	public final Color colorOn;
	public final Color colorOff;
	public final double radius;
	public final Group statusIcon;
	protected final ControlBar cb;
	private Boolean on;
	private Timeline blinkTimeline;

	/**
	 * Constructor
	 * 
	 * @param controlBar the control bar
	 * @param addIcon true to add the status icon as a child of the status view
	 * @param colorNotSet the color when the status {@linkplain #on} is <code>null</code>
	 * @param colorOn the color when the status {@linkplain #on} is <code>true</code>
	 * @param colorOff the color when the status {@linkplain #on} is <code>false</code>
	 */
	protected StatusView(final ControlBar controlBar, final boolean addIcon) {
		this(controlBar, addIcon, 0, null, null, null, RADIUS_MIN);
	}
	
	/**
	 * Constructor
	 * 
	 * @param controlBar the control bar
	 * @param addIcon true to add the status icon as a child of the status view
	 */
	protected StatusView(final ControlBar controlBar, final boolean addIcon, final Color colorNotSet, 
			final Color colorOn, final Color colorOff) {
		this(controlBar, addIcon, 0, colorNotSet, colorOn, colorOff, RADIUS_MIN);
	}
	
	/**
	 * Constructor
	 * 
	 * @param controlBar the control bar
	 * @param addIcon true to add the status icon as a child of the status view
	 * @param spacing the spacing
	 */
	protected StatusView(final ControlBar controlBar, final boolean addIcon, final double spacing) {
		this(controlBar, addIcon, spacing, null, null, null, RADIUS_MIN);
	}

	/**
	 * Constructor
	 * 
	 * @param controlBar the control bar
	 * @param addIcon true to add the status icon as a child of the status view
	 * @param spacing the spacing
	 * @param colorNotSet the color when the status {@linkplain #on} is <code>null</code>
	 * @param colorOn the color when the status {@linkplain #on} is <code>true</code>
	 * @param colorOff the color when the status {@linkplain #on} is <code>false</code>
	 */
	protected StatusView(final ControlBar controlBar, final boolean addIcon, final double spacing,
			final Color colorNotSet, final Color colorOn, final Color colorOff, final double radius) {
		super(spacing);
		this.cb = controlBar;
		this.colorNotSet = colorNotSet != null ? colorNotSet : COLOR_NOT_SET;
		this.colorOn = colorOn != null ? colorOn : COLOR_ON;
		this.colorOff = colorOff != null ? colorOff : COLOR_OFF;
		this.radius = radius >= RADIUS_MIN ? radius : RADIUS_MIN;
		this.statusIcon = createStatusImage();
		if (addIcon) {
			getChildren().add(this.statusIcon);
		}
	}
	
	/**
	 * @return a status indicator {@linkplain Group} that will visually indicate an on/off status
	 */
	protected Group createStatusImage() {
		final Group circles = new Group();
		circles.getChildren().addAll(createStatusCircle(null), createStatusCircle(false));
		return circles;
	}
	
	/**
	 * Creates status indicator {@linkplain Circle} that will visually indicate an on/off status
	 * 
	 * @param on true to show the the status {@linkplain Circle} as on, false to show it as off
	 * @return the status {@linkplain Circle}
	 */
	private Circle createStatusCircle(final Boolean on) {
		this.on = on;
		final Circle circle = new Circle(on == null ? getRadius() : getRadius() * (getRadius() * 0.10d), Color.web("white", 0.05));
		circle.setFill(getStatusFill(this.on));
		circle.setStroke(Color.web("#D0E6FA", 0.16));
		circle.setStrokeType(StrokeType.OUTSIDE);
		circle.setStrokeWidth(2);
		circle.setEffect(LightingBuilder.create().light(new Light.Distant()).build());
		return circle;
	}
	
	/**
	 * Gets the status indicator fill
	 * 
	 * @param on true for a an on fill, false for an off fill
	 * @return the status fill
	 */
	private Paint getStatusFill(final Boolean on) {
		return on == null ? getColorNotSet() : on ? getColorOn() : getColorOff();
	}
	
	/**
	 * Sets the status fill for the indicator
	 * 
	 * @param on true to show the the status {@linkplain Circle} as on, false to show it as off
	 */
	public void setStatusFill(final Boolean on) {
		this.on = on;
		if (statusIcon != null) {
			((Circle)statusIcon.getChildren().get(1)).setFill(getStatusFill(this.on));
		}
	}
	
	/**
	 * Creates a time line that will alternate the status icon fill indefinitely
	 */
	public void blinkStart() {
		blinkStart(isOn(), isOn(), 0);
	}
	
	/**
	 * Creates a time line that will alternate the status icon fill for the specified cycle count
	 * 
	 * @param endOn the on state at the end of the status blink
	 * @param cycleCount the cycle count of the {@linkplain Timeline}
	 */
	public void blinkStart(final Boolean startOn, final Boolean endOn, final int cycleCount) {
		if (blinkTimeline != null) {
			blinkTimeline.stop();
			blinkTimeline = null;
		}
		blinkTimeline = new Timeline();
		blinkTimeline.setCycleCount(cycleCount <=0 ? Timeline.INDEFINITE : cycleCount);
		blinkTimeline.setAutoReverse(true);
		final KeyFrame kf = new KeyFrame(Duration.millis(500), new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				setStatusFill(isOn() == null || isOn() != startOn ? startOn : null);
			}
		});
		blinkTimeline.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				setStatusFill(endOn);
			}
		});
		blinkTimeline.getKeyFrames().add(kf);
		blinkTimeline.playFromStart();
	}
	
	/**
	 * Stops the status icon from blinking
	 * 
	 * @param on true to show the the status {@linkplain Circle} as on, false to show it as off
	 */
	public void blinkStop(final Boolean on) {
		if (blinkTimeline != null) {
			blinkTimeline.stop();
			setStatusFill(on);
			blinkTimeline = null;
		}
	}

	/**
	 * @return the radius of the {@linkplain #statusIcon}
	 */
	public double getRadius() {
		return radius;
	}

	/**
	 * @return null when not set, true when on, false when off
	 */
	public Boolean isOn() {
		return on;
	}

	/**
	 * @return the color when the status {@linkplain #on} is <code>null</code>
	 */
	public Color getColorNotSet() {
		return colorNotSet;
	}

	/**
	 * @return the color when the status {@linkplain #on} is <code>true</code>
	 */
	public Color getColorOn() {
		return colorOn;
	}

	/**
	 * @return the color when the status {@linkplain #on} is <code>false</code>
	 */
	public Color getColorOff() {
		return colorOff;
	}
}
