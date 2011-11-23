package org.ugate.gui.components;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import org.ugate.gui.components.Gauge.IndicatorType;
import org.ugate.resources.RS;

/**
 * A label followed by a 7-segment digit readout (padded using the format value) with
 * a slider to adjust the value
 */
public class UGateGaugeDisplay extends VBox {
	
	public final HBox valueView;
	public final Gauge gauge;
	public final ImageView imageView;
	public final Digits gaugeDigits;
	
	/**
	 * Constructs a new slider gauge
	 * 
	 * @param indicatorType the gauge indicator type {@linkplain Gauge#indicatorType}
	 * @param sizeScale the gauge size scale {@linkplain Gauge#sizeScale}
	 * @param tickValueScale the gauge tick value scale {@linkplain Gauge#tickValueScale}
	 * @param tickValueZeroOffset the gauge tick value zero offset {@linkplain Gauge#tickValueZeroOffset}
	 * @param startAngle the start angle of the gauge {@linkplain Gauge#angleStart}
	 * @param angleLength the angle length of the gauge {@linkplain Gauge#angleLength}
	 * @param numberOfMajorTickMarks the number of major tick marks {@linkplain Gauge#numOfMajorTickMarks}
	 * @param numOfMinorTickMarksPerMajorTick the number of minor tick marks {@linkplain Gauge#numOfMinorTickMarksPerMajorTick}
	 * @param value the initial value of the gauge
	 * @param format the string format of the digits
	 * @param iconFileName the icon of the gauge
	 * @param toolTip the tool tip of the gauge
	 * @param onColor the color of the on digits
	 */
	public UGateGaugeDisplay(final IndicatorType indicatorType, final double sizeScale, final double tickValueScale, 
			final int tickValueZeroOffset, final double startAngle, final double angleLength, 
			final int numberOfMajorTickMarks, final int numOfMinorTickMarksPerMajorTick, 
			final Double value, final String format, final String iconFileName,
			final String toolTip, final Color onColor) {
		this(indicatorType, sizeScale, tickValueScale, tickValueZeroOffset, startAngle, angleLength, numberOfMajorTickMarks, 
				numOfMinorTickMarksPerMajorTick, value, format, iconFileName, toolTip, onColor, null, Orientation.VERTICAL);
	}

	/**
	 * Constructs a new slider gauge
	 * 
	 * @param indicatorType the gauge indicator type {@linkplain Gauge#indicatorType}
	 * @param sizeScale the gauge size scale {@linkplain Gauge#sizeScale}
	 * @param tickValueScale the gauge tick value scale {@linkplain Gauge#tickValueScale}
	 * @param tickValueZeroOffset the gauge tick value zero offset {@linkplain Gauge#tickValueZeroOffset}
	 * @param startAngle the start angle of the gauge {@linkplain Gauge#angleStart}
	 * @param angleLength the angle length of the gauge {@linkplain Gauge#angleLength}
	 * @param numberOfMajorTickMarks the number of major tick marks {@linkplain Gauge#numOfMajorTickMarks}
	 * @param numOfMinorTickMarksPerMajorTick the number of minor tick marks {@linkplain Gauge#numOfMinorTickMarksPerMajorTick}
	 * @param value the initial value of the gauge
	 * @param format the string format of the digits
	 * @param iconFileName the icon of the gauge
	 * @param toolTip the tool tip of the gauge
	 * @param onColor the color of the on digits
	 * @param offColor the color of the off digits
	 * @param orientation the orientation of the control
	 */
	public UGateGaugeDisplay(final IndicatorType indicatorType, final double sizeScale, final double tickValueScale, 
			final int tickValueZeroOffset, final double startAngle, final double angleLength, 
			final int numberOfMajorTickMarks, final int numOfMinorTickMarksPerMajorTick, 
			final Double value, final String format, final String iconFileName,
			final String toolTip, final Color onColor, final Color offColor, final Orientation orientation) {
		super(10d);
		setPadding(new Insets(20d, 10d, 20d, 10d));
		final boolean useInt = format.indexOf("d") > -1;
		gauge = new Gauge(indicatorType, sizeScale, tickValueScale, tickValueZeroOffset, startAngle, 
				angleLength, numberOfMajorTickMarks, numOfMinorTickMarksPerMajorTick);
		gauge.tickMarkLabelFillProperty.set(Color.TRANSPARENT);
		gauge.intensityIndicatorRegionsProperty.set(new Gauge.IntensityIndicatorRegions(50d, 30d, 20d, 
				Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT));
		gauge.tickValueProperty.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				gaugeDigits.setValue(useInt ? String.format(format, newValue.intValue()) :  
					String.format(format, newValue.floatValue()));
			}
		});
		//gauge.setTooltip(new Tooltip(toolTip));
		gaugeDigits = new Digits(useInt ? String.format(format, value.intValue()) :  
			String.format(format, value.floatValue()), 0.15f, onColor, offColor);
		gauge.setTickValue(value);
		gaugeDigits.setEffect(new DropShadow());
//		slider.valueProperty().addListener(new ChangeListener<Number>() {
//            public void changed(ObservableValue<? extends Number> ov,
//                Number old_val, Number new_val) {
//            	gaugeDigits.setValue(useInt ? String.format(format, new_val.intValue()) : 
//            		String.format(format, new_val.floatValue()));
//            }
//        });
        HBox.setMargin(gaugeDigits, new Insets(0, 5, 0, 5));
        //sliderValue.getTransforms().add(new Scale(0.2f, 0.2f, 0, 0));
        imageView = RS.imgView(iconFileName);
		final DropShadow outerGlow = new DropShadow();
		outerGlow.setOffsetX(0);
		outerGlow.setOffsetY(0);
		outerGlow.setColor(Color.ORANGERED);
		outerGlow.setRadius(10d);
        imageView.setEffect(outerGlow);
        
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
}