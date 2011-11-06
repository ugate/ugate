package org.ugate.gui.components;

import javafx.scene.CacheHint;
import javafx.scene.Parent;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Shear;

/**
 * Simple 7 segment LED style digit. It supports the numbers 0 through 9.
 */
public class Digit extends Parent {
	public static final int NEGATIVE_SIGN_DIGIT = 10;
	private static final boolean[][] DIGIT_COMBINATIONS = new boolean[][] {
			new boolean[] { true, false, true, true, true, true, true },
			new boolean[] { false, false, false, false, true, false, true },
			new boolean[] { true, true, true, false, true, true, false },
			new boolean[] { true, true, true, false, true, false, true },
			new boolean[] { false, true, false, true, true, false, true },
			new boolean[] { true, true, true, true, false, false, true },
			new boolean[] { true, true, true, true, false, true, true },
			new boolean[] { true, false, false, false, true, false, true },
			new boolean[] { true, true, true, true, true, true, true },
			new boolean[] { true, true, true, true, true, false, true },
			new boolean[] { false, true, false, false, false, false, false } };
	private final Polygon[] polygons;
	private final Color onColor;
	private final Color offColor;
	private final Effect onEffect;
	private final Effect offEffect;
	private int number = 0;

	public Digit() {
		this(1, 0, null, null, null, null);
	}
	
	public Digit(final double scale) {
		this(scale, 0, null, null, null, null);
	}
	
	public Digit(final double scale, final int number) {
		this(scale, number, null, null, null, null);
	}

	public Digit(final double scale, final int number, final Color onColor, final Color offColor) {
		this(scale, number, onColor, offColor, null, null);
	}

	public Digit(final double scale, final int number, final Color onColor, final Color offColor,
			final Effect onEffect, final Effect offEffect) {
		setCache(true);
		setCacheHint(CacheHint.SPEED);
		polygons = createPolygons(scale);
		this.onColor = onColor != null ? onColor : getDefaultOnColor();
		this.offColor = offColor != null ? offColor : getDefaultOffColor();
		this.onEffect = onEffect != null ? onEffect : createDefaultOnEffect(scale);
		this.offEffect = offEffect != null ? offEffect
				: createDefaultOffEffect(scale);
		getChildren().addAll(polygons);
		getTransforms().add(new Shear(-0.1, 0));
		showNumber(number);
	}

	/**
	 * Shows a polygon number as a 7-segment LED
	 * 
	 * @param number
	 *            the number to show
	 */
	public void showNumber(final int number) {
		// default to 0 for non-valid numbers
		this.number = (number < 0 || number > NEGATIVE_SIGN_DIGIT) ? 0 : number;
		for (int i=0; i<7; i++) {
			polygons[i].setFill(DIGIT_COMBINATIONS[getNumber()][i] ? onColor
					: offColor);
			polygons[i].setEffect(DIGIT_COMBINATIONS[getNumber()][i] ? onEffect
					: offEffect);
		}
	}

	/**
	 * @return the numeric value of the digit
	 */
	public int getNumber() {
		return number;
	}
	
	/**
	 * @return the default segment color when it's on
	 */
	protected static Color getDefaultOnColor() {
		return Color.ORANGERED;
	}
	
	/**
	 * @return the default segment color when it's off
	 */
	protected static Color getDefaultOffColor() {
		return Color.rgb(50, 50, 50);
	}

	/**
	 * @return the default effect for a segment when it's on
	 */
	public static Effect createDefaultOnEffect(final double scale) {
		final Glow onEffect = new Glow(1.7f * scale);
		onEffect.setInput(createDefaultOffEffect(scale));
		return onEffect;
	}

	/**
	 * @return the default effect for a segment when it's off
	 */
	public static Effect createDefaultOffEffect(final double scale) {
		final InnerShadow is = new InnerShadow();
		is.setRadius(10 * scale);
		is.setWidth(21 * scale);
		is.setHeight(21 * scale);
		return is;
	}
	
	protected static Polygon[] createPolygons(final double scale) {
		return new Polygon[] {
				createPolygon(new double[] { 2f * scale, 0f * scale, 52f * scale, 0f * scale, 42f * scale, 10f * scale, 12f * scale, 10f * scale }),
				createPolygon(new double[] { 12 * scale, 49 * scale, 42 * scale, 49 * scale, 52 * scale, 54 * scale, 42 * scale, 59 * scale, 12 * scale,
						59 * scale, 2 * scale, 54 * scale }),
				createPolygon(new double[] { 12 * scale, 98 * scale, 42 * scale, 98 * scale, 52 * scale, 108 * scale, 2 * scale, 108 * scale }),
				createPolygon(new double[] { 0 * scale, 2 * scale, 10 * scale, 12 * scale, 10 * scale, 47 * scale, 0 * scale, 52 * scale }),
				createPolygon(new double[] { 44 * scale, 12 * scale, 54 * scale, 2 * scale, 54 * scale, 52 * scale, 44 * scale, 47 * scale }),
				createPolygon(new double[] { 0 * scale, 56 * scale, 10 * scale, 61 * scale, 10 * scale, 96 * scale, 0 * scale, 106 * scale }),
				createPolygon(new double[] { 44 * scale, 61 * scale, 54 * scale, 56 * scale, 54 * scale, 106 * scale, 44 * scale, 96 * scale }) };
	}
	
	protected static Polygon createPolygon(final double... points) {
		final Polygon pg = new Polygon(points);
		pg.setCache(true);
		pg.setCacheHint(CacheHint.SPEED);
		pg.setSmooth(false);
		return pg;
	}
}
