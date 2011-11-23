package org.ugate.gui;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import org.ugate.UGateUtil;
import org.ugate.gui.components.Digits;
import org.ugate.gui.components.Gauge.IndicatorType;
import org.ugate.gui.components.ToggleSwitchPreferenceView;
import org.ugate.gui.components.UGateGaugeDisplay;
import org.ugate.resources.RS;

/**
 * Sonar IR sensor control view
 */
public class SonarIrControl extends ControlPane {
	
	public static final String FORMAT_SONAR = "%04.1f";
	public static final String FORMAT_PIR = "%04.1f";
	public static final Color COLOR_SONAR = Color.AQUA;
	public static final Color COLOR_PIR = Color.RED;

	@Override
	protected Node[] createLeftViewChildren() {
		final GridPane grid = new GridPane();
		final Label headerSonar = new Label("Sonar Configuration");
		headerSonar.getStyleClass().add("gauge-header");
		grid.add(headerSonar, 0, 0);
		final Label headerPIR = new Label("PIR Configuration");
		headerPIR.getStyleClass().add("gauge-header");
		grid.add(headerPIR, 1, 0);
		final ToggleSwitchPreferenceView sonarToggleSwitchView = new ToggleSwitchPreferenceView(UGateUtil.SV_SONAR_ALARM_ON_KEY, 
				RS.IMG_SONAR_ALARM_ON, RS.IMG_SONAR_ALARM_OFF, 
				"Toggle sonar intruder alarm that takes a picture, stores it on the computer, and sends email notification with image attachment (if on)");
		grid.add(sonarToggleSwitchView, 0, 1);
		final UGateGaugeDisplay sonarTripGauge = new UGateGaugeDisplay(IndicatorType.NEEDLE, NEEDLE_SIZE_SCALE,
				1d, 2, 0, 180d, 9, 4, 7d, FORMAT_SONAR, RS.IMG_RULER,
				"Sonar Distance Threshold: Distance at which an image will be taken and sent to the computer and recipients (if alarm is turned on)", 
				COLOR_SONAR);
		grid.add(sonarTripGauge, 0, 2);
		final UGateGaugeDisplay sonarTripRateGauge = new UGateGaugeDisplay(IndicatorType.NEEDLE, NEEDLE_SIZE_SCALE,
				1d, 0, 0, 180d, 61, 0, 0d, FORMAT_DELAY, RS.IMG_STOPWATCH,
				"Sonar Delay Between Photos: Delay in minutes between pictures taken/sent when an object is within the distance threshold.\n" +
				"When zero, there may still be a few seconds beween photos due to the wireless transfer rate", 
				COLOR_SONAR);
		grid.add(sonarTripRateGauge, 0, 3);
		final ToggleSwitchPreferenceView pirToggleSwitchView = new ToggleSwitchPreferenceView(UGateUtil.SV_IR_ALARM_ON_KEY, 
				RS.IMG_IR_ALARM_ON, RS.IMG_IR_ALARM_OFF,
				"Toggle PIR intruder alarm that takes a picture, stores it on the computer, and sends email notification with image attachment (if on)");
		grid.add(pirToggleSwitchView, 1, 1);
		final UGateGaugeDisplay pirTripGauge = new UGateGaugeDisplay(IndicatorType.NEEDLE, NEEDLE_SIZE_SCALE,
				1d, 2, 0, 180d, 24, 1, 15d, FORMAT_PIR, RS.IMG_RULER,
				"PIR Distance Threshold: Distance at which an image will be taken and sent to the computer and recipients (if alarm is turned on)", 
				COLOR_PIR);
		grid.add(pirTripGauge, 1, 2);
		final UGateGaugeDisplay pirTripRateGauge = new UGateGaugeDisplay(IndicatorType.NEEDLE, NEEDLE_SIZE_SCALE,
				1d, 0, 0, 180d, 61, 0, 0d, FORMAT_DELAY, RS.IMG_STOPWATCH,
				"PIR Delay Between Photos: Delay in minutes between pictures taken/sent when an object is within the distance threshold.\n" +
				"When zero, there may still be a few seconds beween photos due to the wireless transfer rate", 
				COLOR_PIR);
		grid.add(pirTripRateGauge, 1, 3);
		return new Node[] { grid };
	}

	@Override
	protected Node[] createCenterViewChildren() {
		final Label movementHeader = new Label("Sonar+PIR Pan/Tilt Angles");
		movementHeader.getStyleClass().add("gauge-header");
		final GridPane gridMovement = new GridPane();
		final UGateGaugeDisplay sonarIrPanGauge = new UGateGaugeDisplay(IndicatorType.KNOB, KNOB_SIZE_SCALE,
				10d, 0, 0, 180d, 19, 4, 90d, FORMAT_ANGLE, RS.IMG_PAN,
				"Sonar/IR Pan: Current trip alram sensor pan angle (in degrees)", COLOR_PAN_TILT);
		gridMovement.add(sonarIrPanGauge, 0, 0);
		final UGateGaugeDisplay sonarIrTiltGauge = new UGateGaugeDisplay(IndicatorType.KNOB, KNOB_SIZE_SCALE,
				10d, 0, 0, 180d, 19, 4, 90d, FORMAT_ANGLE, RS.IMG_TILT,
				"Sonar/IR Tilt: Current trip alarm sensor tilt angle (in degrees)", COLOR_PAN_TILT);
		gridMovement.add(sonarIrTiltGauge, 1, 0);
		
		final Label readingsHeader = new Label("Sonar/PIR Readings");
		readingsHeader.getStyleClass().add("gauge-header");
		final Digits sonarReading = new Digits(String.format(FORMAT_SONAR, 7.5f), 0.15f, COLOR_SONAR, null);
		final Digits pirReading = new Digits(String.format(FORMAT_SONAR, 15.5f), 0.15f, COLOR_PIR, null);
		final Group readingsGroup = createReadingsDisplay(sonarReading, pirReading);
		return new Node[] { movementHeader, gridMovement, readingsHeader, readingsGroup };
	}

	@Override
	protected Node[] createRightViewChildren() {
		final Label header = new Label("Actions");
		header.getStyleClass().add("gauge-header");
		final ImageView readingsGet = RS.imgView(RS.IMG_READINGS_GET);
		final ImageView settingsSet = RS.imgView(RS.IMG_SETTINGS_SET);
		settingsSet.setEffect(new DropShadow());
		return new Node[] { header, readingsGet, settingsSet };
	}
}
