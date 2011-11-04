package org.ugate.gui.components;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import org.ugate.gui.components.Gauge.IndicatorType;

public class GaugeDemo extends VBox {
	
	private Slider intensityChangingSlider;

	public GaugeDemo() {
		final Gauge gauge = new Gauge(IndicatorType.NEEDLE, 0.5d, 0d, 180d);
		//gauge.intensityIndicatorRegionsProperty.setValue(new Gauge.IntensityIndicatorRegions(10d, 80d, 10d));
		//gauge.minorTickMarkOpacityProperty.set(0);
		//gauge.majorTickMarkOpacityProperty.set(0);
		final Slider gaugeColor1PercentageSlider = new Slider(0d, 100d, Gauge.INTENSITY_REGIONS_DEFAULT.getColor1SpanPercentage());
		final Slider gaugeColor2PercentageSlider = new Slider(0d, 100d, Gauge.INTENSITY_REGIONS_DEFAULT.getColor2SpanPercentage());
		final Slider gaugeColor3PercentageSlider = new Slider(0d, 100d, Gauge.INTENSITY_REGIONS_DEFAULT.getColor3SpanPercentage());
		addIntensityChangeListener(gauge, gaugeColor1PercentageSlider, gaugeColor2PercentageSlider, gaugeColor3PercentageSlider);
		addIntensityChangeListener(gauge, gaugeColor2PercentageSlider, gaugeColor1PercentageSlider, gaugeColor3PercentageSlider);
		addIntensityChangeListener(gauge, gaugeColor3PercentageSlider, gaugeColor1PercentageSlider, gaugeColor2PercentageSlider);
		final VBox gaugeIntensitySliders = new VBox();
		gaugeIntensitySliders.getChildren().addAll(gaugeColor1PercentageSlider, gaugeColor2PercentageSlider, gaugeColor3PercentageSlider);
		final HBox gaugeContainer = new HBox();
		gaugeContainer.getChildren().addAll(gauge, gaugeIntensitySliders);
		
		final Gauge gauge2 = new Gauge(IndicatorType.KNOB, 0.5d, 0d, 360d);
		
		getChildren().addAll(gaugeContainer, gauge2);
	}
	
	private void addIntensityChangeListener(final Gauge gauge, final Slider color1, final Slider color2, final Slider color3) {
		color1.setSnapToTicks(true);
		color1.setBlockIncrement(1d);
		color1.setMajorTickUnit(1d);
		color1.setMinorTickCount(0);
		color1.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if (intensityChangingSlider == null) {
					intensityChangingSlider = color1;
					final double difference = (Double)newValue - (Double)oldValue; //Math.abs((Double)oldValue - (Double)newValue);
					final double region2Perc = gauge.intensityIndicatorRegionsProperty.getValue().getColor2SpanPercentage() - difference;
					final double region3Perc = gauge.intensityIndicatorRegionsProperty.getValue().getColor3SpanPercentage() - difference;
					final Gauge.IntensityIndicatorRegions gir = new Gauge.IntensityIndicatorRegions(
							(Double)newValue, region2Perc, region3Perc);
					color2.setValue(region2Perc);
					color3.setValue(region3Perc);
					gauge.intensityIndicatorRegionsProperty.setValue(gir);
					intensityChangingSlider = null;
				}
			}
		});
	}
}
