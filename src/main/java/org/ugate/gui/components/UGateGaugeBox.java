package org.ugate.gui.components;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
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
import javafx.scene.text.Font;

import org.ugate.gui.components.Gauge.IndicatorType;
import org.ugate.resources.RS;
import org.ugate.service.entity.IModelType;

/**
 * A {@linkplain Label} followed by a 7-segment {@linkplain Digits} readout (padded using the format value) with
 * a {@linkplain Gauge} to adjust the value
 */
public class UGateGaugeBox<T> extends VBox {
	
	public final HBox valueView;
	public final Gauge gauge;
	public final ImageView imageView;
	public Digits gaugeDigits;
	public final IntegerProperty wholeNumProperty = new SimpleIntegerProperty();
	public final IntegerProperty fractionProperty = new SimpleIntegerProperty();
	private boolean isInternalUpdate;
	
	/**
	 * Constructs a new gauge display
	 * 
	 * @param beanPathAdapter
	 *            the {@linkplain BeanPathAdapter} to bind to
	 * @param modelKeyWholeNum
	 *            the {@linkplain IModelType} of the field that will bound to
	 *            the integer portion of the value
	 * @param modelKeyFraction
	 *            the {@linkplain IModelType} of the field that will bound to
	 *            the decimal portion of the value
	 * @param indicatorType
	 *            the gauge indicator type {@linkplain Gauge#indicatorType}
	 * @param sizeScale
	 *            the gauge size scale {@linkplain Gauge#sizeScale}
	 * @param tickValueScale
	 *            the gauge tick value scale {@linkplain Gauge#tickValueScale}
	 * @param tickValueZeroOffset
	 *            the gauge tick value zero offset
	 *            {@linkplain Gauge#tickValueZeroOffset}
	 * @param startAngle
	 *            the start angle of the gauge {@linkplain Gauge#angleStart}
	 * @param angleLength
	 *            the angle length of the gauge {@linkplain Gauge#angleLength}
	 * @param numberOfMajorTickMarks
	 *            the number of major tick marks
	 *            {@linkplain Gauge#numOfMajorTickMarks}
	 * @param numOfMinorTickMarksPerMajorTick
	 *            the number of minor tick marks
	 *            {@linkplain Gauge#numOfMinorTickMarksPerMajorTick}
	 * @param format
	 *            the string format of the digits
	 * @param icon
	 *            the icon of the gauge
	 * @param onColor
	 *            the color of the on digits
	 */
	public UGateGaugeBox(final BeanPathAdapter<T> beanPathAdapter,
			final IModelType<T> modelKeyWholeNum,
			final IModelType<T> modelKeyFraction,
			final IndicatorType indicatorType, final double sizeScale,
			final double tickValueScale, final int tickValueZeroOffset,
			final double startAngle, final double angleLength,
			final int numberOfMajorTickMarks,
			final int numOfMinorTickMarksPerMajorTick, final String format,
			final ImageView icon, final Color onColor) {
		this(beanPathAdapter, modelKeyWholeNum, modelKeyFraction, indicatorType,
				sizeScale, tickValueScale, tickValueZeroOffset, startAngle,
				angleLength, numberOfMajorTickMarks,
				numOfMinorTickMarksPerMajorTick, null, format, icon, onColor,
				null, Orientation.VERTICAL);
	}
	
	/**
	 * Constructs a new gauge display
	 * 
	 * @param beanPathAdapter
	 *            the {@linkplain BeanPathAdapter} to bind to
	 * @param modelKeyWholeNum
	 *            the {@linkplain IModelType} of the field that will bound to
	 *            the integer portion of the value
	 * @param modelKeyFraction
	 *            the {@linkplain IModelType} of the field that will bound to
	 *            the decimal portion of the value
	 * @param indicatorType
	 *            the gauge indicator type {@linkplain Gauge#indicatorType}
	 * @param sizeScale
	 *            the gauge size scale {@linkplain Gauge#sizeScale}
	 * @param tickValueScale
	 *            the gauge tick value scale {@linkplain Gauge#tickValueScale}
	 * @param tickValueZeroOffset
	 *            the gauge tick value zero offset
	 *            {@linkplain Gauge#tickValueZeroOffset}
	 * @param startAngle
	 *            the start angle of the gauge {@linkplain Gauge#angleStart}
	 * @param angleLength
	 *            the angle length of the gauge {@linkplain Gauge#angleLength}
	 * @param numberOfMajorTickMarks
	 *            the number of major tick marks
	 *            {@linkplain Gauge#numOfMajorTickMarks}
	 * @param numOfMinorTickMarksPerMajorTick
	 *            the number of minor tick marks
	 *            {@linkplain Gauge#numOfMinorTickMarksPerMajorTick}
	 * @param format
	 *            the string format of the digits
	 * @param iconFileName
	 *            the icon of the gauge
	 * @param onColor
	 *            the color of the on digits
	 */
	public UGateGaugeBox(final BeanPathAdapter<T> beanPathAdapter,
			final IModelType<T> modelKeyWholeNum,
			final IModelType<T> modelKeyFraction,
			final IndicatorType indicatorType, final double sizeScale,
			final double tickValueScale, final int tickValueZeroOffset,
			final double startAngle, final double angleLength,
			final int numberOfMajorTickMarks,
			final int numOfMinorTickMarksPerMajorTick, final String format,
			final String iconFileName, final Color onColor) {
		this(beanPathAdapter, modelKeyWholeNum, modelKeyFraction, indicatorType,
				sizeScale, tickValueScale, tickValueZeroOffset, startAngle,
				angleLength, numberOfMajorTickMarks,
				numOfMinorTickMarksPerMajorTick, format, iconFileName, onColor,
				null, Orientation.VERTICAL);
	}
	
	/**
	 * Constructs a new gauge display
	 * 
	 * @param beanPathAdapter
	 *            the {@linkplain BeanPathAdapter} to bind to
	 * @param modelKeyWholeNum
	 *            the {@linkplain IModelType} of the field that will bound to
	 *            the integer portion of the value
	 * @param modelKeyFraction
	 *            the {@linkplain IModelType} of the field that will bound to
	 *            the decimal portion of the value
	 * @param indicatorType
	 *            the gauge indicator type {@linkplain Gauge#indicatorType}
	 * @param sizeScale
	 *            the gauge size scale {@linkplain Gauge#sizeScale}
	 * @param tickValueScale
	 *            the gauge tick value scale {@linkplain Gauge#tickValueScale}
	 * @param tickValueZeroOffset
	 *            the gauge tick value zero offset
	 *            {@linkplain Gauge#tickValueZeroOffset}
	 * @param startAngle
	 *            the start angle of the gauge {@linkplain Gauge#angleStart}
	 * @param angleLength
	 *            the angle length of the gauge {@linkplain Gauge#angleLength}
	 * @param numberOfMajorTickMarks
	 *            the number of major tick marks
	 *            {@linkplain Gauge#numOfMajorTickMarks}
	 * @param numOfMinorTickMarksPerMajorTick
	 *            the number of minor tick marks
	 *            {@linkplain Gauge#numOfMinorTickMarksPerMajorTick}
	 * @param tickValueFont
	 *            the {@linkplain Font} applied to the gauge
	 * @param format
	 *            the string format of the digits
	 * @param iconFileName
	 *            the icon of the gauge
	 * @param onColor
	 *            the color of the on digits
	 */
	public UGateGaugeBox(final BeanPathAdapter<T> beanPathAdapter,
			final IModelType<T> modelKeyWholeNum,
			final IModelType<T> modelKeyFraction,
			final IndicatorType indicatorType, final double sizeScale,
			final double tickValueScale, final int tickValueZeroOffset,
			final double startAngle, final double angleLength,
			final int numberOfMajorTickMarks,
			final int numOfMinorTickMarksPerMajorTick,
			final Font tickValueFont, final String format,
			final String iconFileName, final Color onColor) {
		this(beanPathAdapter, modelKeyWholeNum, modelKeyFraction, indicatorType,
				sizeScale, tickValueScale, tickValueZeroOffset, startAngle,
				angleLength, numberOfMajorTickMarks,
				numOfMinorTickMarksPerMajorTick, tickValueFont, format,
				iconFileName, onColor, null, Orientation.VERTICAL);
	}
	
	/**
	 * Constructs a new gauge display
	 * 
	 * @param beanPathAdapter
	 *            the {@linkplain BeanPathAdapter} to bind to
	 * @param modelKeyWholeNum
	 *            the {@linkplain IModelType} of the field that will bound to
	 *            the integer portion of the value
	 * @param modelKeyFraction
	 *            the {@linkplain IModelType} of the field that will bound to
	 *            the decimal portion of the value
	 * @param indicatorType
	 *            the gauge indicator type {@linkplain Gauge#indicatorType}
	 * @param sizeScale
	 *            the gauge size scale {@linkplain Gauge#sizeScale}
	 * @param tickValueScale
	 *            the gauge tick value scale {@linkplain Gauge#tickValueScale}
	 * @param tickValueZeroOffset
	 *            the gauge tick value zero offset
	 *            {@linkplain Gauge#tickValueZeroOffset}
	 * @param startAngle
	 *            the start angle of the gauge {@linkplain Gauge#angleStart}
	 * @param angleLength
	 *            the angle length of the gauge {@linkplain Gauge#angleLength}
	 * @param numberOfMajorTickMarks
	 *            the number of major tick marks
	 *            {@linkplain Gauge#numOfMajorTickMarks}
	 * @param numOfMinorTickMarksPerMajorTick
	 *            the number of minor tick marks
	 *            {@linkplain Gauge#numOfMinorTickMarksPerMajorTick}
	 * @param format
	 *            the string format of the digits
	 * @param iconFileName
	 *            the icon of the gauge
	 * @param onColor
	 *            the color of the on digits
	 * @param offColor
	 *            the color of the off digits
	 * @param orientation
	 *            the orientation of the control
	 */
	public UGateGaugeBox(final BeanPathAdapter<T> beanPathAdapter,
			final IModelType<T> modelKeyWholeNum,
			final IModelType<T> modelKeyFraction,
			final IndicatorType indicatorType, final double sizeScale,
			final double tickValueScale, final int tickValueZeroOffset,
			final double startAngle, final double angleLength,
			final int numberOfMajorTickMarks,
			final int numOfMinorTickMarksPerMajorTick, final String format,
			final String iconFileName, final Color onColor,
			final Color offColor, final Orientation orientation) {
		this(beanPathAdapter, modelKeyWholeNum, modelKeyFraction, indicatorType,
				sizeScale, tickValueScale, tickValueZeroOffset, startAngle,
				angleLength, numberOfMajorTickMarks,
				numOfMinorTickMarksPerMajorTick, null, format, RS
						.imgView(iconFileName), onColor, offColor, orientation);
	}
	
	/**
	 * Constructs a new gauge display
	 * 
	 * @param beanPathAdapter
	 *            the {@linkplain BeanPathAdapter} to bind to
	 * @param modelKeyWholeNum
	 *            the {@linkplain IModelType} of the field that will bound to
	 *            the integer portion of the value
	 * @param modelKeyFraction
	 *            the {@linkplain IModelType} of the field that will bound to
	 *            the decimal portion of the value
	 * @param nodeIndex
	 *            the node index (if applicable)
	 * @param indicatorType
	 *            the gauge indicator type {@linkplain Gauge#indicatorType}
	 * @param sizeScale
	 *            the gauge size scale {@linkplain Gauge#sizeScale}
	 * @param tickValueScale
	 *            the gauge tick value scale {@linkplain Gauge#tickValueScale}
	 * @param tickValueZeroOffset
	 *            the gauge tick value zero offset
	 *            {@linkplain Gauge#tickValueZeroOffset}
	 * @param startAngle
	 *            the start angle of the gauge {@linkplain Gauge#angleStart}
	 * @param angleLength
	 *            the angle length of the gauge {@linkplain Gauge#angleLength}
	 * @param numberOfMajorTickMarks
	 *            the number of major tick marks
	 *            {@linkplain Gauge#numOfMajorTickMarks}
	 * @param numOfMinorTickMarksPerMajorTick
	 *            the number of minor tick marks
	 *            {@linkplain Gauge#numOfMinorTickMarksPerMajorTick}
	 * @param tickValueFont
	 *            the {@linkplain Font} applied to the gauge
	 * @param format
	 *            the string format of the digits
	 * @param iconFileName
	 *            the icon of the gauge
	 * @param onColor
	 *            the color of the on digits
	 * @param offColor
	 *            the color of the off digits
	 * @param orientation
	 *            the orientation of the control
	 */
	public UGateGaugeBox(final BeanPathAdapter<T> beanPathAdapter,
			final IModelType<T> modelKeyWholeNum,
			final IModelType<T> modelKeyFraction,
			final IndicatorType indicatorType, final double sizeScale,
			final double tickValueScale, final int tickValueZeroOffset,
			final double startAngle, final double angleLength,
			final int numberOfMajorTickMarks,
			final int numOfMinorTickMarksPerMajorTick,
			final Font tickValueFont, final String format,
			final String iconFileName, final Color onColor,
			final Color offColor, final Orientation orientation) {
		this(beanPathAdapter, modelKeyWholeNum, modelKeyFraction, indicatorType,
				sizeScale, tickValueScale, tickValueZeroOffset, startAngle,
				angleLength, numberOfMajorTickMarks,
				numOfMinorTickMarksPerMajorTick, tickValueFont, format, RS
						.imgView(iconFileName), onColor, offColor, orientation);
	}

	/**
	 * Constructs a new gauge display
	 * 
	 * @param beanPathAdapter
	 *            the {@linkplain BeanPathAdapter} to bind to
	 * @param modelKeyWholeNum
	 *            the {@linkplain IModelType} of the field that will bound to
	 *            the integer portion of the value
	 * @param modelKeyFraction
	 *            the {@linkplain IModelType} of the field that will bound to
	 *            the decimal portion of the value
	 * @param indicatorType
	 *            the gauge indicator type {@linkplain Gauge#indicatorType}
	 * @param sizeScale
	 *            the gauge size scale {@linkplain Gauge#sizeScale}
	 * @param tickValueScale
	 *            the gauge tick value scale {@linkplain Gauge#tickValueScale}
	 * @param tickValueZeroOffset
	 *            the gauge tick value zero offset
	 *            {@linkplain Gauge#tickValueZeroOffset}
	 * @param startAngle
	 *            the start angle of the gauge {@linkplain Gauge#angleStart}
	 * @param angleLength
	 *            the angle length of the gauge {@linkplain Gauge#angleLength}
	 * @param numberOfMajorTickMarks
	 *            the number of major tick marks
	 *            {@linkplain Gauge#numOfMajorTickMarks}
	 * @param numOfMinorTickMarksPerMajorTick
	 *            the number of minor tick marks
	 *            {@linkplain Gauge#numOfMinorTickMarksPerMajorTick}
	 * @param tickValueFont
	 *            the {@linkplain Font} applied to the gauge
	 * @param format
	 *            the string format of the digits
	 * @param icon
	 *            the icon of the gauge
	 * @param onColor
	 *            the color of the on digits
	 * @param offColor
	 *            the color of the off digits
	 * @param orientation
	 *            the orientation of the control
	 */
	public UGateGaugeBox(final BeanPathAdapter<T> beanPathAdapter,
			final IModelType<T> modelKeyWholeNum,
			final IModelType<T> modelKeyFraction,
			final IndicatorType indicatorType, final double sizeScale,
			final double tickValueScale, final int tickValueZeroOffset,
			final double startAngle, final double angleLength,
			final int numberOfMajorTickMarks,
			final int numOfMinorTickMarksPerMajorTick,
			final Font tickValueFont, final String format,
			final ImageView icon, final Color onColor, final Color offColor,
			final Orientation orientation) {
		super(0d);
		//setPadding(new Insets(20d, 10d, 20d, 10d));
		
		// create the gauge
		gauge = new Gauge(indicatorType, sizeScale, tickValueScale, tickValueZeroOffset, startAngle, 
				angleLength, numberOfMajorTickMarks, numOfMinorTickMarksPerMajorTick, tickValueFont);
		gauge.tickMarkLabelFillProperty.set(Color.TRANSPARENT);
		if (indicatorType == IndicatorType.KNOB) {
			gauge.setIntensity(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);
		}
		gauge.tickValueProperty.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				setValueFromGauge(format, newValue);
			}
		});
		
		// create/bind the value display
		wholeNumProperty.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				setValueFromProperties(modelKeyWholeNum, modelKeyFraction,
						format, onColor, offColor);
			}
		});
		fractionProperty.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				setValueFromProperties(modelKeyWholeNum, modelKeyFraction,
						format, onColor, offColor);
			}
		});
		setValueFromProperties(modelKeyWholeNum, modelKeyFraction, format, onColor, offColor);
		beanPathAdapter.bindBidirectional(modelKeyWholeNum.getKey(), wholeNumProperty);
		if (modelKeyFraction != null) {
			beanPathAdapter.bindBidirectional(modelKeyFraction.getKey(), fractionProperty);
		}
        
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

	private void setValueFromProperties(final IModelType<T> modelKeyWholeNum,
			final IModelType<T> modelKeyFraction, final String format,
			final Color onColor, final Color offColor) {
		if (isInternalUpdate) {
			return;
		}
		try {
			isInternalUpdate = true;
			final boolean useInt = format.indexOf("d") > -1;
			final double val1 = (double) wholeNumProperty.get();
			final double val2 = fractionProperty != null ? (double) fractionProperty
					.get() : 0d;
			final Double newVal = val1 + val2;
			final String newValStr = useInt ? String.format(format,
					newVal.intValue()) : String.format(format, newVal.floatValue());
			if (gaugeDigits == null) {
				gaugeDigits = new Digits(newValStr, 0.15f, onColor, offColor);
				gaugeDigits.setEffect(new DropShadow());
		        HBox.setMargin(gaugeDigits, new Insets(0, 5, 0, 5));
		        //gaugeDigits.getTransforms().add(new Scale(0.2f, 0.2f, 0, 0));
				gaugeDigits.valueProperty().addListener(new ChangeListener<String>() {
					@Override
					public void changed(ObservableValue<? extends String> observable,
							String oldValue, String newValue) {
						setValueFromDigits(modelKeyWholeNum, modelKeyFraction, newValue);
					}
				});
			} else {
				gaugeDigits.setValue(newValStr);
			}
			gauge.setTickValue(newVal);
		} finally {
			isInternalUpdate = false;
		}
	}

	/**
	 * Sets the preference value using the preference keys
	 * 
	 * @param modelKeyWholeNum
	 *            the {@linkplain IModelType} of the field that will bound to
	 *            the integer portion of the value
	 * @param modelKeyFraction
	 *            the {@linkplain IModelType} of the field that will bound to
	 *            the decimal portion of the value
	 * @param newValue the new value to set
	 */
	private void setValueFromDigits(final IModelType<T> modelKeyWholeNum,
			final IModelType<T> modelKeyFraction, final String newValue) {
		if (isInternalUpdate) {
			return;
		}
		try {
			isInternalUpdate = true;
			if (modelKeyFraction != null) {
				final double value = Double.parseDouble(newValue);
				final int feet = (int) value;
				final int inches = (int) (12d * (value - feet));
				wholeNumProperty.set(feet);
				fractionProperty.set(inches);
			} else {
				final int value = Integer.parseInt(newValue);
				wholeNumProperty.set(value);
			}
		} finally {
			isInternalUpdate = false;
		}
	}
	
	/**
	 * Sets the {@linkplain Gauge} digit display value
	 * 
	 * @param format
	 *            the {@linkplain Digits} display value format
	 * @param digitsValue
	 *            the {@linkplain Digits} display value
	 */
	protected void setValueFromGauge(final String format,
			final Number digitsValue) {
		final boolean useInt = format.indexOf("d") > -1;
		final String digitsValueStr = useInt ? String.format(format,
				digitsValue.intValue()) : String.format(format,
				digitsValue.floatValue());
		setValueFromGauge(digitsValueStr);
	}
	
	/**
	 * Sets the {@linkplain Gauge} digit display value
	 * 
	 * @param digitsValue
	 *            the {@linkplain Digits} display value
	 */
	protected void setValueFromGauge(final String digitsValue) {
		gaugeDigits.setValue(digitsValue);
	}

	/**
	 * @return the {@linkplain IntegerProperty} for the whole number portion of
	 *         the {@linkplain Gauge}
	 */
	public IntegerProperty wholeNumProperty() {
		return wholeNumProperty;
	}

	/**
	 * @return the {@linkplain IntegerProperty} for the fraction portion (right
	 *         of the decimal point) of the {@linkplain Gauge}
	 */
	public IntegerProperty fractionProperty() {
		return fractionProperty;
	}
	
	// TODO : add metric option
//	protected boolean useMetric() {
//		final String useMetricStr = UGateKeeper.DEFAULT.preferencesGet(UGateUtil.PV_USE_METRIC_KEY);
//		return useMetricStr != null && useMetricStr.length() > 0 ? Integer.parseInt(useMetricStr) == 1 : false;
//	}
}