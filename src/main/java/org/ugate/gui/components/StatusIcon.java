package org.ugate.gui.components;

import javafx.animation.Animation.Status;
import javafx.animation.FillTransition;
import javafx.animation.FillTransitionBuilder;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.effect.Light;
import javafx.scene.effect.LightingBuilder;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Arc;
import javafx.util.Duration;

/**
 * Status indicator icon that can pulse an indicated {@linkplain Color} status
 */
public class StatusIcon extends Group {

	private Color staticColor;
	private ReadOnlyObjectWrapper<Paint> colorProperty = new ReadOnlyObjectWrapper<Paint>();
	private final double indicatorRadiusX;
	private final double indicatorRadiusY;
	private final double indicatorX;
	private final double indicatorY;
	private final Arc indicator;
	private final ImageView icon;
	private FillTransition fillTransition;

	/**
	 * Constructor
	 * 
	 * @param width
	 *            the width of the {@linkplain StatusIcon}
	 * @param height
	 *            the height of the {@linkplain StatusIcon}
	 * @param color
	 *            the initial {@linkplain StatusIcon#getStaticColor()}
	 */
	public StatusIcon(final ImageView icon, final Color color) {
		this(icon, icon.getImage().getWidth(), icon.getImage().getHeight(), color);
	}

	/**
	 * Constructor
	 * 
	 * @param icon
	 *            the {@linkplain ImageView} that will overlay the icon (should
	 *            have transparent sections so the the status will show through)
	 * @param color
	 *            the initial {@linkplain StatusIcon#getStaticColor()}
	 */
	public StatusIcon(final double width, final double height, final Color color) {
		this(null, width, height, color);
	}

	/**
	 * Constructor
	 * 
	 * @param icon
	 *            the {@linkplain ImageView} that will overlay the icon (should
	 *            have transparent sections so the the status will show through)
	 * @param width
	 *            the width of the {@linkplain StatusIcon}
	 * @param height
	 *            the height of the {@linkplain StatusIcon}
	 * @param color
	 *            the initial {@linkplain StatusIcon#getStaticColor()}
	 */
	protected StatusIcon(final ImageView icon, final double width,
			final double height, final Color color) {
		this.icon = icon;
		this.staticColor = color;
		this.indicatorRadiusX = (width / 2d) - (width / 2d * 0.1d);
		this.indicatorRadiusY = (height / 2d) - (height / 2d * 0.1d);
		this.indicatorX = this.indicatorRadiusX + (this.indicatorRadiusX * 0.15d);
		this.indicatorY = this.indicatorRadiusY + (this.indicatorRadiusY * 0.15d);
		this.indicator = new Arc(this.indicatorX,
				this.indicatorY, this.indicatorRadiusX, this.indicatorRadiusY, 0,
				360);
		this.indicator.setFill(this.staticColor);
		Bindings.bindBidirectional(this.colorProperty, this.indicator.fillProperty());
		this.indicator.setEffect(LightingBuilder
						.create()
						.light(new Light.Point(this.indicatorX * 0.5d, this.indicatorY * 0.5d,
								Math.max(this.indicatorX * 0.7d, this.indicatorY * 0.7d), null)).build());
		if (this.icon != null) {
			getChildren().addAll(this.indicator, this.icon);
		} else {
			getChildren().addAll(this.indicator);
		}
	}

	/**
	 * Sets the {@linkplain StatusIcon} fill {@linkplain Color} using a
	 * {@linkplain FillTransition}
	 * 
	 * @param duration
	 *            the {@linkplain FillTransition#getDuration()}
	 * @param fromColor
	 *            the {@linkplain FillTransition#getFromValue()}
	 * @param toColor
	 *            the {@linkplain FillTransition#getToValue()}
	 * @param cycleCount
	 *            the {@linkplain FillTransition#getCycleCount()}
	 */
	public void setStatusFill(final Duration duration, final Color fromColor,
			final Color toColor, final int cycleCount) {
		setStatusFill(duration, fromColor, toColor, this.staticColor, cycleCount, true,
				null, null, null);
	}

	/**
	 * Sets the {@linkplain StatusIcon} fill {@linkplain Color} using a
	 * {@linkplain FillTransition}
	 * 
	 * @param duration
	 *            the {@linkplain FillTransition#getDuration()}
	 * @param fromColor
	 *            the {@linkplain FillTransition#getFromValue()}
	 * @param toColor
	 *            the {@linkplain FillTransition#getToValue()}
	 * @param endColor
	 *            the fill {@linkplain StatusIcon#getStaticColor()} to set when
	 *            finished
	 * @param cycleCount
	 *            the {@linkplain FillTransition#getCycleCount()}
	 */
	public void setStatusFill(final Duration duration, final Color fromColor,
			final Color toColor, final Color endColor, final int cycleCount) {
		setStatusFill(duration, fromColor, toColor, endColor, cycleCount, true,
				null, null, null);
	}

	/**
	 * Sets the {@linkplain StatusIcon} fill {@linkplain Color} using a
	 * {@linkplain FillTransition}
	 * 
	 * @param duration
	 *            the {@linkplain FillTransition#getDuration()}
	 * @param fromColor
	 *            the {@linkplain FillTransition#getFromValue()}
	 * @param toColor
	 *            the {@linkplain FillTransition#getToValue()}
	 * @param endColor
	 *            the fill {@linkplain StatusIcon#getStaticColor()} to set when
	 *            finished
	 * @param cycleCount
	 *            the {@linkplain FillTransition#getCycleCount()}
	 * @param autoReverse
	 *            the {@linkplain FillTransition#isAutoReverse()}
	 * @param delay
	 *            the {@linkplain FillTransition#getDelay()}
	 * @param rate
	 *            the {@linkplain FillTransition#getRate()}
	 * @param finish
	 *            an optional {@linkplain Runnable} that will be
	 *            {@linkplain Runnable#run()} when
	 *            {@linkplain FillTransition#getOnFinished()}
	 */
	public void setStatusFill(final Duration duration, final Color fromColor,
			final Color toColor, final Color endColor, final int cycleCount,
			final boolean autoReverse, final Duration delay, final Double rate,
			final Runnable finish) {
		if (fillTransition == null) {
			fillTransition = FillTransitionBuilder.create().shape(indicator).build();
		} else {
			fillTransition.stop();
		}
		fillTransition.setFromValue(fromColor);
		fillTransition.setToValue(toColor);
		fillTransition.setDuration(duration);
		fillTransition.setCycleCount(cycleCount);
		fillTransition.setAutoReverse(autoReverse);
		if (delay != null) {
			fillTransition.setDelay(delay);
		}
		if (rate != null) {
			fillTransition.setRate(rate);
		}
		fillTransition.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				StatusIcon.this.setStatusFill(endColor);
				if (finish != null) {
					finish.run();
				}
			}
		});
		fillTransition.play();
	}

	/**
	 * Immediately sets the {@linkplain StatusIcon} fill {@linkplain Color}
	 * 
	 * @param color
	 *            the fill {@linkplain Color} to set as the
	 *            {@linkplain StatusIcon#getStaticColor()}
	 */
	public void setStatusFill(final Color color) {
		if (color != null) {
			this.staticColor = color;
		}
		if (getTransitionStatus() == Status.RUNNING) {
			fillTransition.stop();
		}
		fillTransition = null;
		indicator.setFill(this.staticColor);
	}

	/**
	 * @return the {@linkplain Status}
	 */
	public Status getTransitionStatus() {
		return fillTransition != null ? fillTransition.getStatus(): Status.STOPPED;
	}

	/**
	 * @return the {@linkplain StatusIcon}'s {@linkplain Arc#getRadiusX()}
	 */
	public double getIndicatorRadiusX() {
		return indicatorRadiusX;
	}

	/**
	 * @return the {@linkplain StatusIcon}'s {@linkplain Arc#getRadiusY()}
	 */
	public double getIndicatorRadiusY() {
		return indicatorRadiusY;
	}

	/**
	 * @return the {@linkplain StatusIcon}'s {@linkplain Arc#getCenterX()}
	 */
	public double getIndicatorX() {
		return indicatorX;
	}

	/**
	 * @return the {@linkplain StatusIcon}'s {@linkplain Arc#getCenterY()}
	 */
	public double getIndicatorY() {
		return indicatorY;
	}

	/**
	 * @return the <b>optional</b> {@linkplain StatusIcon}
	 *         {@linkplain ImageView}
	 */
	public ImageView getIcon() {
		return icon;
	}

	/**
	 * @return the {@linkplain StatusIcon} fill {@linkplain Color} that was last
	 *         set regardless if a {@linkplain FillTransition} is running
	 */
	public Color getStaticColor() {
		return staticColor;
	}

	/**
	 * @return the {@linkplain ReadOnlyObjectProperty} for the
	 *         {@linkplain StatusIcon} fill {@linkplain Color}
	 */
	public ReadOnlyObjectProperty<Paint> colorProperty() {
		return colorProperty.getReadOnlyProperty();
	}

	/**
	 * @return the {@linkplain StatusIcon} fill {@linkplain Color}
	 */
	public Paint getColor() {
		return colorProperty.get();
	}
}
