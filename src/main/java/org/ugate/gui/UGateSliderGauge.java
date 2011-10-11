package org.ugate.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import org.ugate.gui.components.Digits;
import org.ugate.resources.RS;

/**
 * A label followed by a 7-segment digit readout (padded using the format value) with
 * a slider to adjust the value
 */
public class UGateSliderGauge extends VBox {
	
	public final HBox valueView;
	public final Slider slider;
	public final ImageView imageView;
	public final Digits sliderValue;

	/**
	 * Constructs a new slider gauge
	 * 
	 * @param min minimum value of the slider
	 * @param max maximum value of the slider
	 * @param value the initial value of the slider
	 * @param increment the block increment of the slider
	 * @param format the string format of the slider 
	 * 		(should match the min/max/value type passed- only integer and float are supported)
	 * @param iconFileName the icon of the gauge
	 * @param toolTip the tool tip of the gauge
	 */
	public UGateSliderGauge(final Number min, final Number max, final Number value, 
			final float increment, final String format, final String iconFileName,
			final String toolTip, final boolean showSlider, final Color onColor, 
			final Color offColor) {
		final boolean useInt = format.indexOf("d") > -1;
		valueView = new HBox();
		valueView.getStyleClass().add("gauge");
		slider = new Slider();
		slider.setTooltip(new Tooltip(toolTip));
		slider.setPrefWidth(50);
		slider.setOrientation(Orientation.HORIZONTAL);
		slider.setMin(useInt ? min.intValue() : min.floatValue());
		slider.setMax(useInt ? max.intValue() : max.floatValue());
		slider.setValue(useInt ? value.intValue() : value.floatValue());
		slider.setBlockIncrement(increment);
        //slider.setShowTickLabels(true);
		//slider.setShowTickMarks(true);
        //slider.setMajorTickUnit(5);
        //slider.setMinorTickCount(1);
		sliderValue = new Digits(useInt ? String.format(format, value.intValue()) :  
			String.format(format, value.floatValue()), 0.15f, onColor, offColor);
		slider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) {
            	sliderValue.setValue(useInt ? String.format(format, new_val.intValue()) : 
            		String.format(format, new_val.floatValue()));
            }
        });
        HBox.setMargin(sliderValue, new Insets(0, 5, 0, 5));
        //sliderValue.getTransforms().add(new Scale(0.2f, 0.2f, 0, 0));
        imageView = RS.imgView(iconFileName);
        imageView.setStyle("-fx-width: 90%");
        sliderValue.setStyle("-fx-width: 10%");
        valueView.getChildren().addAll(imageView, sliderValue);
        if (showSlider) {
        	getChildren().addAll(valueView, slider);
        } else {
        	getChildren().add(valueView);
        }
	}
}