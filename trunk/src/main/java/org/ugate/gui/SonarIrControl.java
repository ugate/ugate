package org.ugate.gui;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import org.ugate.UGateUtil;
import org.ugate.gui.components.Gauge.IndicatorType;
import org.ugate.gui.components.ToggleSwitchPreferenceView;
import org.ugate.gui.components.UGateGaugeDisplay;
import org.ugate.resources.RS;

/**
 * Sonar IR sensor control view
 */
public class SonarIrControl extends ControlPane {

	@Override
	protected Node[] createLeftViewChildren() {
		final Label header = new Label("Configuration");
		header.getStyleClass().add("gauge-header");
		final GridPane grid = new GridPane();
		final ToggleSwitchPreferenceView sonarToggleSwitchView = new ToggleSwitchPreferenceView(UGateUtil.SV_SONAR_ALARM_ON_KEY, 
				RS.IMG_SONAR_ALARM_ON, RS.IMG_SONAR_ALARM_OFF, 
				"Toggle sonar intruder alarm that takes a picture, stores it on the computer, and sends email notification with image attachment (if on)");
		grid.add(sonarToggleSwitchView, 0, 0);
		final UGateGaugeDisplay sonarTripGauge = new UGateGaugeDisplay(IndicatorType.NEEDLE, NEEDLE_SIZE_SCALE,
				1d, 2, 0, 180d, 9, 4, 7d, "%04.1f", RS.IMG_RULER,
				"Sonar Distance Threshold: Distance at which an image will be taken and sent to the computer and recipients (if alarm is turned on)", 
				Color.AQUA, null);
		grid.add(sonarTripGauge, 0, 1);
		final UGateGaugeDisplay sonarTripRateGauge = new UGateGaugeDisplay(IndicatorType.NEEDLE, NEEDLE_SIZE_SCALE,
				1d, 0, 0, 180d, 61, 0, 0d, "%03d", RS.IMG_STOPWATCH,
				"Sonar Delay Between Photos: Delay in minutes between pictures taken/sent when an object is within the distance threshold.\n" +
				"When zero, there may still be a few seconds beween photos due to the wireless transfer rate", 
				Color.AQUA, null);
		grid.add(sonarTripRateGauge, 0, 2);
		final ToggleSwitchPreferenceView irToggleSwitchView = new ToggleSwitchPreferenceView(UGateUtil.SV_IR_ALARM_ON_KEY, 
				RS.IMG_IR_ALARM_ON, RS.IMG_IR_ALARM_OFF,
				"Toggle IR intruder alarm that takes a picture, stores it on the computer, and sends email notification with image attachment (if on)");
		grid.add(irToggleSwitchView, 1, 0);
		final UGateGaugeDisplay irTripGauge = new UGateGaugeDisplay(IndicatorType.NEEDLE, NEEDLE_SIZE_SCALE,
				1d, 2, 0, 180d, 24, 1, 15d, "%04.1f", RS.IMG_RULER,
				"IR Distance Threshold: Distance at which an image will be taken and sent to the computer and recipients (if alarm is turned on)", 
				Color.RED, null);
		grid.add(irTripGauge, 1, 1);
		final UGateGaugeDisplay irTripRateGauge = new UGateGaugeDisplay(IndicatorType.NEEDLE, NEEDLE_SIZE_SCALE,
				1d, 0, 0, 180d, 61, 0, 0d, "%03d", RS.IMG_STOPWATCH,
				"IR Delay Between Photos: Delay in minutes between pictures taken/sent when an object is within the distance threshold.\n" +
				"When zero, there may still be a few seconds beween photos due to the wireless transfer rate", 
				Color.RED, null);
		grid.add(irTripRateGauge, 1, 2);
		return new Node[] { header, grid };
	}

	@Override
	protected Node[] createCenterViewChildren() {
		final Label header = new Label("Movement");
		header.getStyleClass().add("gauge-header");
		final GridPane grid = new GridPane();
		final UGateGaugeDisplay sonarIrPanGauge = new UGateGaugeDisplay(IndicatorType.KNOB, KNOB_SIZE_SCALE,
				10d, 0, 0, 180d, 19, 4, 90d, "%03d", RS.IMG_PAN,
				"Sonar/IR Pan: Current trip alram sensor pan angle (in degrees)", Color.YELLOW, null);
		grid.add(sonarIrPanGauge, 0, 0);
		final UGateGaugeDisplay sonarIrTiltGauge = new UGateGaugeDisplay(IndicatorType.KNOB, KNOB_SIZE_SCALE,
				10d, 0, 0, 180d, 19, 4, 90d, "%03d", RS.IMG_TILT,
				"Sonar/IR Tilt: Current trip alarm sensor tilt angle (in degrees)", Color.YELLOW, null);
		grid.add(sonarIrTiltGauge, 1, 0);
		return new Node[] { header, grid };
	}

	@Override
	protected Node[] createRightViewChildren() {
		final Label header = new Label("Actions");
		header.getStyleClass().add("gauge-header");
		return new Node[] { header };
	}
}
