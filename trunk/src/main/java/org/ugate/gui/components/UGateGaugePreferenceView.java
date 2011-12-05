package org.ugate.gui.components;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import org.ugate.UGateKeeper;
import org.ugate.gui.components.Gauge.IndicatorType;
import org.ugate.resources.RS;

/**
 * A {@linkplain Label} followed by a 7-segment {@linkplain Digits} readout (padded using the format value) with
 * a {@linkplain Gauge} to adjust the value
 */
public class UGateGaugePreferenceView extends VBox {
	
	public final HBox valueView;
	public final Gauge gauge;
	public final ImageView imageView;
	public final Digits gaugeDigits;
	
	/**
	 * Constructs a new gauge display
	 * 
	 * @param preferenceKeyInteger the preference key used to sync the controls integer portion of the value to
	 * @param preferenceKeyFraction the preference key used to sync the controls decimal portion of the value to
	 * @param indicatorType the gauge indicator type {@linkplain Gauge#indicatorType}
	 * @param sizeScale the gauge size scale {@linkplain Gauge#sizeScale}
	 * @param tickValueScale the gauge tick value scale {@linkplain Gauge#tickValueScale}
	 * @param tickValueZeroOffset the gauge tick value zero offset {@linkplain Gauge#tickValueZeroOffset}
	 * @param startAngle the start angle of the gauge {@linkplain Gauge#angleStart}
	 * @param angleLength the angle length of the gauge {@linkplain Gauge#angleLength}
	 * @param numberOfMajorTickMarks the number of major tick marks {@linkplain Gauge#numOfMajorTickMarks}
	 * @param numOfMinorTickMarksPerMajorTick the number of minor tick marks {@linkplain Gauge#numOfMinorTickMarksPerMajorTick}
	 * @param format the string format of the digits
	 * @param icon the icon of the gauge
	 * @param onColor the color of the on digits
	 */
	public UGateGaugePreferenceView(final String preferenceKeyInteger, final String preferenceKeyFraction, 
			final IndicatorType indicatorType, final double sizeScale, final double tickValueScale, 
			final int tickValueZeroOffset, final double startAngle, final double angleLength, 
			final int numberOfMajorTickMarks, final int numOfMinorTickMarksPerMajorTick, 
			final String format, final ImageView icon, final Color onColor) {
		this(preferenceKeyInteger, preferenceKeyFraction, indicatorType, sizeScale, tickValueScale, 
				tickValueZeroOffset, startAngle, angleLength, numberOfMajorTickMarks, 
				numOfMinorTickMarksPerMajorTick, format, icon, onColor, null, Orientation.VERTICAL);
	}
	
	/**
	 * Constructs a new gauge display
	 * 
	 * @param preferenceKeyInteger the preference key used to sync the controls integer portion of the value to
	 * @param preferenceKeyFraction the preference key used to sync the controls decimal portion of the value to
	 * @param indicatorType the gauge indicator type {@linkplain Gauge#indicatorType}
	 * @param sizeScale the gauge size scale {@linkplain Gauge#sizeScale}
	 * @param tickValueScale the gauge tick value scale {@linkplain Gauge#tickValueScale}
	 * @param tickValueZeroOffset the gauge tick value zero offset {@linkplain Gauge#tickValueZeroOffset}
	 * @param startAngle the start angle of the gauge {@linkplain Gauge#angleStart}
	 * @param angleLength the angle length of the gauge {@linkplain Gauge#angleLength}
	 * @param numberOfMajorTickMarks the number of major tick marks {@linkplain Gauge#numOfMajorTickMarks}
	 * @param numOfMinorTickMarksPerMajorTick the number of minor tick marks {@linkplain Gauge#numOfMinorTickMarksPerMajorTick}
	 * @param format the string format of the digits
	 * @param iconFileName the icon of the gauge
	 * @param onColor the color of the on digits
	 */
	public UGateGaugePreferenceView(final String preferenceKeyInteger, final String preferenceKeyFraction, 
			final IndicatorType indicatorType, final double sizeScale, final double tickValueScale, 
			final int tickValueZeroOffset, final double startAngle, final double angleLength, 
			final int numberOfMajorTickMarks, final int numOfMinorTickMarksPerMajorTick, 
			final String format, final String iconFileName, final Color onColor) {
		this(preferenceKeyInteger, preferenceKeyFraction, indicatorType, sizeScale, tickValueScale, 
				tickValueZeroOffset, startAngle, angleLength, numberOfMajorTickMarks, 
				numOfMinorTickMarksPerMajorTick, format, iconFileName, onColor, null, Orientation.VERTICAL);
	}
	
	/**
	 * Constructs a new gauge display
	 * 
	 * @param preferenceKeyInteger the preference key used to sync the controls integer portion of the value to
	 * @param preferenceKeyFraction the preference key used to sync the controls decimal portion of the value to
	 * @param indicatorType the gauge indicator type {@linkplain Gauge#indicatorType}
	 * @param sizeScale the gauge size scale {@linkplain Gauge#sizeScale}
	 * @param tickValueScale the gauge tick value scale {@linkplain Gauge#tickValueScale}
	 * @param tickValueZeroOffset the gauge tick value zero offset {@linkplain Gauge#tickValueZeroOffset}
	 * @param startAngle the start angle of the gauge {@linkplain Gauge#angleStart}
	 * @param angleLength the angle length of the gauge {@linkplain Gauge#angleLength}
	 * @param numberOfMajorTickMarks the number of major tick marks {@linkplain Gauge#numOfMajorTickMarks}
	 * @param numOfMinorTickMarksPerMajorTick the number of minor tick marks {@linkplain Gauge#numOfMinorTickMarksPerMajorTick}
	 * @param format the string format of the digits
	 * @param iconFileName the icon of the gauge
	 * @param onColor the color of the on digits
	 * @param offColor the color of the off digits
	 * @param orientation the orientation of the control
	 */
	public UGateGaugePreferenceView(final String preferenceKeyInteger, final String preferenceKeyFraction, 
			final IndicatorType indicatorType, final double sizeScale, final double tickValueScale, 
			final int tickValueZeroOffset, final double startAngle, final double angleLength, 
			final int numberOfMajorTickMarks, final int numOfMinorTickMarksPerMajorTick, 
			final String format, final String iconFileName, final Color onColor, 
			final Color offColor, final Orientation orientation) {
		this(preferenceKeyInteger, preferenceKeyFraction, indicatorType, sizeScale, tickValueScale, 
				tickValueZeroOffset, startAngle, angleLength, numberOfMajorTickMarks, 
				numOfMinorTickMarksPerMajorTick, format, RS.imgView(iconFileName), onColor, offColor, orientation);
	}

	/**
	 * Constructs a new gauge display
	 * 
	 * @param preferenceKeyInteger the preference key used to sync the controls integer portion of the value to
	 * @param preferenceKeyFraction the preference key used to sync the controls decimal portion of the value to
	 * @param indicatorType the gauge indicator type {@linkplain Gauge#indicatorType}
	 * @param sizeScale the gauge size scale {@linkplain Gauge#sizeScale}
	 * @param tickValueScale the gauge tick value scale {@linkplain Gauge#tickValueScale}
	 * @param tickValueZeroOffset the gauge tick value zero offset {@linkplain Gauge#tickValueZeroOffset}
	 * @param startAngle the start angle of the gauge {@linkplain Gauge#angleStart}
	 * @param angleLength the angle length of the gauge {@linkplain Gauge#angleLength}
	 * @param numberOfMajorTickMarks the number of major tick marks {@linkplain Gauge#numOfMajorTickMarks}
	 * @param numOfMinorTickMarksPerMajorTick the number of minor tick marks {@linkplain Gauge#numOfMinorTickMarksPerMajorTick}
	 * @param format the string format of the digits
	 * @param icon the icon of the gauge
	 * @param onColor the color of the on digits
	 * @param offColor the color of the off digits
	 * @param orientation the orientation of the control
	 */
	public UGateGaugePreferenceView(final String preferenceKeyInteger, final String preferenceKeyFraction, 
			final IndicatorType indicatorType, final double sizeScale, final double tickValueScale, 
			final int tickValueZeroOffset, final double startAngle, final double angleLength, 
			final int numberOfMajorTickMarks, final int numOfMinorTickMarksPerMajorTick, 
			final String format, final ImageView icon, final Color onColor, final Color offColor, 
			final Orientation orientation) {
		super(10d);
		setPadding(new Insets(20d, 10d, 20d, 10d));
		final boolean useInt = format.indexOf("d") > -1;
		
		// create the gauge
		gauge = new Gauge(indicatorType, sizeScale, tickValueScale, tickValueZeroOffset, startAngle, 
				angleLength, numberOfMajorTickMarks, numOfMinorTickMarksPerMajorTick);
		gauge.tickMarkLabelFillProperty.set(Color.TRANSPARENT);
		if (indicatorType == IndicatorType.KNOB) {
			gauge.setIntensity(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);
		}
		gauge.tickValueProperty.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				setGaugeDigitsValue(format, newValue);
			}
		});
		
		// create the value display
		String initPrefValStr = UGateKeeper.DEFAULT.preferencesGet(preferenceKeyInteger);
		String initPrefValStr2 = preferenceKeyFraction != null ? UGateKeeper.DEFAULT.preferencesGet(preferenceKeyFraction) : null;
		final double val1 = initPrefValStr != null && initPrefValStr.length() > 0 ? Double.valueOf(initPrefValStr) : 0d;
		final double val2 = initPrefValStr2 != null && initPrefValStr2.length() > 0 ? Double.valueOf(initPrefValStr2) : 0d;
		final Double initPrefVal = val1 + val2;
		gaugeDigits = new Digits(useInt ? String.format(format, initPrefVal.intValue()) :  
			String.format(format, initPrefVal.floatValue()), 0.15f, onColor, offColor);
		gauge.setTickValue(initPrefVal);
		gaugeDigits.setEffect(new DropShadow());
        HBox.setMargin(gaugeDigits, new Insets(0, 5, 0, 5));
        //gaugeDigits.getTransforms().add(new Scale(0.2f, 0.2f, 0, 0));
		gaugeDigits.getValueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				setPreferenceValue(preferenceKeyInteger, preferenceKeyFraction, newValue);
			}
		});
        
		// create the image view
        imageView = icon;
		final DropShadow outerGlow = new DropShadow();
		outerGlow.setOffsetX(0);
		outerGlow.setOffsetY(0);
		outerGlow.setColor(Color.ORANGERED);
		outerGlow.setRadius(10d);
        imageView.setEffect(outerGlow);
        
        // add the views
		valueView = new HBox();
		gaugeDigits.getStyleClass().add("gauge");
		gaugeDigits.setMaxHeight(imageView.getFitHeight());
		valueView.setAlignment(Pos.CENTER_LEFT);
        valueView.getChildren().addAll(imageView, gaugeDigits);
		if (orientation == null || orientation == Orientation.VERTICAL) {
			getChildren().addAll(valueView, gauge);
		} else {
			valueView.getChildren().add(gauge);
			getChildren().add(valueView);
		}
	}
	
	/**
	 * Sets the preference value using the preference keys
	 * 
	 * @param preferenceKeyInteger the preference key used to sync the controls integer portion of the value to
	 * @param preferenceKeyFraction the preference key used to sync the controls decimal portion of the value to
	 * @param newValue the new value to set
	 */
	protected void setPreferenceValue(final String preferenceKeyInteger, 
			final String preferenceKeyFraction, final String newValue) {
		if (preferenceKeyFraction != null) {
			final double value = Double.parseDouble(newValue);
			final int feet = (int) value;
			final int inches = (int) (12d * (value - feet));
			UGateKeeper.DEFAULT.preferencesSet(preferenceKeyInteger, String.valueOf(feet));
			UGateKeeper.DEFAULT.preferencesSet(preferenceKeyFraction, String.valueOf(inches));
		} else {
			UGateKeeper.DEFAULT.preferencesSet(preferenceKeyInteger, newValue);
		}
	}
	
	/**
	 * Sets the gauge digit display value
	 * 
	 * @param format the digit display value format
	 * @param digitsValue the digit display value
	 */
	protected void setGaugeDigitsValue(final String format, final Number digitsValue) {
		final boolean useInt = format.indexOf("d") > -1;
		final String digitsValueStr = useInt ? String.format(format, digitsValue.intValue()) :  
			String.format(format, digitsValue.floatValue());
		setGaugeDigitsValue(digitsValueStr);
	}
	
	/**
	 * Sets the gauge digit display value
	 * 
	 * @param digitsValue the digit display value
	 */
	protected void setGaugeDigitsValue(final String digitsValue) {
		gaugeDigits.setValue(digitsValue);
	}
	
	// TODO : add metric option
//	protected boolean useMetric() {
//		final String useMetricStr = UGateKeeper.DEFAULT.preferencesGet(UGateUtil.PV_USE_METRIC_KEY);
//		return useMetricStr != null && useMetricStr.length() > 0 ? Integer.parseInt(useMetricStr) == 1 : false;
//	}
}