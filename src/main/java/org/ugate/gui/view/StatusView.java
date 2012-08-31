package org.ugate.gui.view;

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
	public static final Color FILL_BASE = Color.web("#4977A3", 1);
	public static final Color FILL_OFF = Color.web("#523D00", 0.7);
	public static final Color FILL_ON = Color.web("#00FF00", 1);
	private Paint fill;
	public final double radius;
	public final Group statusIcon;
	private Timeline blinkTimeline;

	/**
	 * Constructor
	 * 
	 * @param addIcon
	 *            true to add the status icon as a child of the
	 *            {@linkplain StatusView}
	 */
	protected StatusView(final boolean addIcon) {
		this(addIcon, 0, null, RADIUS_MIN);
	}

	/**
	 * Constructor
	 * 
	 * @param addIcon
	 *            true to add the status icon as a child of the
	 *            {@linkplain StatusView}
	 * @param fill
	 *            the {@linkplain Paint} to fill the {@linkplain StatusView}
	 *            with
	 */
	protected StatusView(final boolean addIcon, final Color fill) {
		this(addIcon, 0, fill, RADIUS_MIN);
	}

	/**
	 * Constructor
	 * 
	 * @param addIcon
	 *            true to add the status icon as a child of the
	 *            {@linkplain StatusView}
	 * @param spacing
	 *            the spacing
	 */
	protected StatusView(final boolean addIcon, final double spacing) {
		this(addIcon, spacing, null, RADIUS_MIN);
	}

	/**
	 * Constructor
	 * 
	 * @param addIcon
	 *            true to add the status icon as a child of the
	 *            {@linkplain StatusView}
	 * @param spacing
	 *            the spacing
	 * @param fill
	 *            the {@linkplain Paint} to fill the {@linkplain StatusView}
	 *            with
	 * @param radius
	 *            the radius of the {@linkplain StatusView}
	 */
	protected StatusView(final boolean addIcon, final double spacing,
			final Paint fill) {
		this(addIcon, spacing, fill, RADIUS_MIN);
	}

	/**
	 * Constructor
	 * 
	 * @param addIcon
	 *            true to add the status icon as a child of the
	 *            {@linkplain StatusView}
	 * @param spacing
	 *            the spacing
	 * @param fill
	 *            the {@linkplain Paint} to fill the {@linkplain StatusView}
	 *            with
	 * @param radius
	 *            the radius of the {@linkplain StatusView}
	 */
	protected StatusView(final boolean addIcon, final double spacing,
			final Paint fill, final double radius) {
		super(spacing);
		this.fill = fill != null ? fill : FILL_OFF;
		this.radius = radius >= RADIUS_MIN ? radius : RADIUS_MIN;
		this.statusIcon = createStatusImage();
		if (addIcon) {
			getChildren().add(this.statusIcon);
		}
	}
	
	/**
	 * @return an indicator {@linkplain Group} that will visually indicate a
	 *         {@linkplain StatusView} state
	 */
	protected Group createStatusImage() {
		final Group circles = new Group();
		circles.getChildren().addAll(createStatusCircle(FILL_BASE, false),
				createStatusCircle(getFill(), true));
		return circles;
	}
	
	/**
	 * Creates an indicator {@linkplain Circle}
	 * 
	 * @param fill
	 *            the {@linkplain Paint} to fill the {@linkplain Circle} with
	 * @return the {@linkplain Circle}
	 */
	private Circle createStatusCircle(final Paint fill, final boolean on) {
		final Circle circle = new Circle(!on ? getRadius() : getRadius()
				* (getRadius() * 0.10d), Color.web("white", 0.05));
		circle.setCenterX(-getRadius());
		circle.setCenterY(-getRadius());
		circle.setFill(fill);
		circle.setStroke(Color.web("#D0E6FA", 0.16));
		circle.setStrokeType(StrokeType.OUTSIDE);
		circle.setStrokeWidth(2);
		circle.setEffect(LightingBuilder.create().light(new Light.Distant())
				.build());
		return circle;
	}
	
	/**
	 * Creates a {@linkplain Timeline} that will alternate the
	 * {@linkplain #getFill()} indefinitely
	 */
	public void blinkStart() {
		blinkStart(getFill(), getFill(), 0);
	}

	/**
	 * Creates a {@linkplain Timeline} that will alternate the
	 * {@linkplain #getFill()} for the specified cycle count
	 * 
	 * @param cycleCount
	 *            the cycle count of the {@linkplain Timeline}
	 */
	public void blinkStart(final int cycleCount) {
		blinkStart(getFill(), getFill(), cycleCount);
	}

	/**
	 * Creates a {@linkplain Timeline} that will alternate the
	 * {@linkplain #getFill()} for the specified cycle count
	 * 
	 * @param startFill
	 *            the {@linkplain Paint} begin state at the start of the status
	 *            blink
	 * @param endFill
	 *            the {@linkplain Paint} end state at the end of the status
	 *            blink
	 * @param cycleCount
	 *            the cycle count of the {@linkplain Timeline}
	 */
	public void blinkStart(final Paint startFill, final Paint endFill,
			final int cycleCount) {
		if (blinkTimeline != null) {
			blinkTimeline.stop();
		} else {
			blinkTimeline = new Timeline();
			blinkTimeline.setAutoReverse(true);
			final KeyFrame kf = new KeyFrame(Duration.millis(500),
					new EventHandler<ActionEvent>() {
						@Override
						public void handle(final ActionEvent event) {
							setFill(getFill() != startFill ? startFill : null);
						}
					});
			blinkTimeline.setOnFinished(new EventHandler<ActionEvent>() {
				@Override
				public void handle(final ActionEvent event) {
					setFill(endFill);
				}
			});
			blinkTimeline.getKeyFrames().add(kf);
		}
		setFill(startFill);
		blinkTimeline.setCycleCount(cycleCount <= 0 ? Timeline.INDEFINITE
				: cycleCount);
		blinkTimeline.playFromStart();
	}
	
	/**
	 * Stops the {@linkplain #blinkStart(Paint, Paint, int)}
	 * 
	 * @param paint
	 *            the {@linkplain Paint} to {@linkplain #setFill(Paint)} with
	 */
	public void blinkStop(final Paint paint) {
		if (blinkTimeline != null) {
			blinkTimeline.stop();
			setFill(paint);
			blinkTimeline = null;
		}
	}

	/**
	 * @return the {@linkplain Paint} used to fill the {@linkplain StatusView}
	 */
	public Paint getFill() {
		return fill;
	}

	/**
	 * @param fill
	 *            the {@linkplain Paint} used to fill the
	 *            {@linkplain StatusView}
	 */
	public void setFill(final Paint fill) {
		this.fill = fill == null ? FILL_OFF : fill;
		if (statusIcon != null) {
			((Circle)statusIcon.getChildren().get(1)).setFill(getFill());
		}
	}

	/**
	 * @return the radius of the {@linkplain #statusIcon}
	 */
	public double getRadius() {
		return radius;
	}
}
