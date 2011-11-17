package org.ugate.gui.components;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

import org.ugate.gui.components.Gauge.IndicatorType;

public class GaugeDemo extends VBox {
	
	private Gauge activeIntensityAdjust;

	public GaugeDemo() {
		final Gauge gauge = new Gauge(IndicatorType.NEEDLE, 1d, 0.5d, 0, 0d, 180d, 10, 3);
		//gauge.setTickValue(1d);
		//gauge.intensityIndicatorRegionsProperty.setValue(new Gauge.IntensityIndicatorRegions(10d, 80d, 10d));
		gauge.setCache(false);
		final Gauge gaugeRegion1 = createRegionKnob(Color.RED, 
				gauge.intensityIndicatorRegionsProperty.getValue().getColor3SpanPercentage());
		final Gauge gaugeRegion2 = createRegionKnob(Color.GOLD,
				gauge.intensityIndicatorRegionsProperty.getValue().getColor2SpanPercentage());
		final Gauge gaugeRegion3 = createRegionKnob(Color.GREEN, 
				gauge.intensityIndicatorRegionsProperty.getValue().getColor1SpanPercentage());
		addIntensityChangeListener(gauge, gaugeRegion1, gaugeRegion2, gaugeRegion3);
		addIntensityChangeListener(gauge, gaugeRegion2, gaugeRegion1, gaugeRegion3);
		addIntensityChangeListener(gauge, gaugeRegion3, gaugeRegion1, gaugeRegion2);
		final VBox gaugeIntensitySliders = new VBox();
		gaugeIntensitySliders.getChildren().addAll(gaugeRegion1, gaugeRegion2, gaugeRegion3);
		
		final HBox row1 = new HBox();
		final Gauge gauge2 = new Gauge(IndicatorType.NEEDLE, 0.5d, 1d, -2, 45d, 90d, 5, 0);
		gauge2.setTickValue(0);
		row1.getChildren().addAll(gauge, gaugeIntensitySliders, gauge2);
		
		final HBox row2 = new HBox();
		final Gauge gauge3 = new Gauge(IndicatorType.NEEDLE, 0.5d, 0.5d, 0, 0d, 360d, 5, 4);
		final Gauge gauge4 = new Gauge(IndicatorType.NEEDLE, 0.5d, 1d, 0, 135d, 90d, 7, 0);
		final Gauge gauge5 = new Gauge(IndicatorType.KNOB, 0.5d, 1d, 0, 0d, 360d, 20, 0);
		final Gauge gauge6 = new Gauge(IndicatorType.NEEDLE, 0.5d, 1d, 0, 250d, 310d, 10, 0);
		gauge5.setTickValue(11.5d);
		row2.getChildren().addAll(gauge3, gauge4, gauge5, gauge6);
		
		getChildren().addAll(row1, row2);
	}
	
	private Gauge createRegionKnob(final Color color, final double percentValue) {
		final Gauge gaugeRegion = new Gauge(IndicatorType.KNOB, 0.5d, 10d, 10, 0d, 180d, 10, 4);
		gaugeRegion.indicatorFillProperty.set(Color.BLACK);
		//gaugeRegion.tickMarkLabelFillProperty.set(Color.TRANSPARENT);
		//gaugeRegion.majorTickMarkFillProperty.set(Color.TRANSPARENT);
		//gaugeRegion.minorTickMarkFillProperty.set(Color.TRANSPARENT);
		gaugeRegion.intensityIndicatorRegionsProperty.set(new Gauge.IntensityIndicatorRegions(50d, 30d, 20d, 
				Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT));
		gaugeRegion.dialCenterFillProperty.set(new RadialGradient(0, 0, gaugeRegion.centerX, gaugeRegion.centerY, 
				gaugeRegion.innerRadius, false, CycleMethod.NO_CYCLE, 
				new Stop(0, color), new Stop(1, color.darker())));
		gaugeRegion.setTickValue(percentValue);
		return gaugeRegion;
	}
	
	private void addIntensityChangeListener(final Gauge gauge, final Gauge color1, final Gauge color2, final Gauge color3) {
		//color1.setSnapToTicks(true);
		//color1.setBlockIncrement(1d);
		//color1.setMajorTickUnit(1d);
		//color1.setMinorTickCount(0);
		color1.angleProperty.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if (activeIntensityAdjust == null) {
					activeIntensityAdjust = color1;
					final double region1Perc = color1.getTickValue();
					final double region2Perc = gauge.intensityIndicatorRegionsProperty.getValue().getColor2SpanPercentage() - region1Perc;
					final double region3Perc = gauge.intensityIndicatorRegionsProperty.getValue().getColor3SpanPercentage() - region1Perc;
					final Gauge.IntensityIndicatorRegions gir = new Gauge.IntensityIndicatorRegions(
							(Double)newValue, region2Perc, region3Perc);
					color2.angleProperty.set(region2Perc);
					color3.angleProperty.set(region3Perc);
					gauge.intensityIndicatorRegionsProperty.setValue(gir);
					activeIntensityAdjust = null;
				}
			}
		});
	}
}
