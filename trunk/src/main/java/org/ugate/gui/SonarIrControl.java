package org.ugate.gui;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import org.ugate.UGateUtil;
import org.ugate.gui.components.ToggleSwitchPreferenceView;
import org.ugate.gui.components.UGateSliderGauge;
import org.ugate.resources.RS;

/**
 * Sonar IR sensor control view
 */
public class SonarIrControl extends ControlPane {

	@Override
	protected Node[] createLeftViewChildren() {
		final UGateSliderGauge sonarIrPanGauge = new UGateSliderGauge(1, 180, 90, 1, "%03d", RS.IMG_PAN,
				"Sonar/IR Pan: Current trip alram sensor pan angle (in degrees)", false, Color.YELLOW, null);
		final UGateSliderGauge sonarIrTiltGauge = new UGateSliderGauge(1, 180, 90, 1, "%03d", RS.IMG_TILT,
				"Sonar/IR Tilt: Current trip alarm sensor tilt angle (in degrees)", false, Color.YELLOW, null);
		return new Node[] { sonarIrPanGauge, sonarIrTiltGauge };
	}

	@Override
	protected Node[] createCenterViewChildren() {
		final ImageView sonarIrNavStatusButton = RS.imgView(RS.IMG_SENSOR_ARM);
		final ImageView sonarIrNavButton = RS.imgView(RS.IMG_NAV_SENSOR);
		return new Node[] { sonarIrNavStatusButton, sonarIrNavButton };
	}

	@Override
	protected Node[] createRightViewChildren() {
		final ToggleSwitchPreferenceView sonarToggleSwitchView = new ToggleSwitchPreferenceView(UGateUtil.SV_SONAR_ALARM_ON_KEY, 
				RS.IMG_SONAR_ALARM_ON, RS.IMG_SONAR_ALARM_OFF, 
				"Toggle sonar intruder alarm that takes a picture, stores it on the computer, and sends email notification with image attachment (if on)");
		final VBox sonarTripView = new VBox();
		final UGateSliderGauge sonarTripGauge = new UGateSliderGauge(1.0f, 26.0f, 10.0f, 0.5f, "%04.1f", RS.IMG_RULER,
				"Sonar Distance Threshold: Distance at which an image will be taken and sent to the computer and recipients (if alarm is turned on)", 
				true, null, null);
		final UGateSliderGauge sonarTripRateGauge = new UGateSliderGauge(0, 120, 0, 1, "%03d", RS.IMG_STOPWATCH,
				"Sonar Delay Between Photos: Delay in minutes between pictures taken/sent when an object is within the distance threshold.\n" +
				"When zero, there may still be a few seconds beween photos due to the wireless transfer rate", 
				true, null, null);
		sonarTripView.getChildren().addAll(sonarTripGauge, sonarTripRateGauge);
		final ToggleSwitchPreferenceView irToggleSwitchView = new ToggleSwitchPreferenceView(UGateUtil.SV_IR_ALARM_ON_KEY, 
				RS.IMG_IR_ALARM_ON, RS.IMG_IR_ALARM_OFF,
				"Toggle IR intruder alarm that takes a picture, stores it on the computer, and sends email notification with image attachment (if on)");
		final VBox irTripView = new VBox();
		final UGateSliderGauge irTripGauge = new UGateSliderGauge(1.0f, 26.0f, 10.0f, 0.5f, "%04.1f", RS.IMG_RULER,
				"IR Distance Threshold: Distance at which an image will be taken and sent to the computer and recipients (if alarm is turned on)", 
				true, null, null);
		final UGateSliderGauge irTripRateGauge = new UGateSliderGauge(0, 120, 0, 1, "%03d", RS.IMG_STOPWATCH,
				"IR Delay Between Photos: Delay in minutes between pictures taken/sent when an object is within the distance threshold.\n" +
				"When zero, there may still be a few seconds beween photos due to the wireless transfer rate", 
				true, null, null);
		irTripView.getChildren().addAll(irTripGauge, irTripRateGauge);
		return new Node[] { sonarToggleSwitchView, sonarTripView, new Separator(Orientation.VERTICAL), irToggleSwitchView, irTripView };
	}
}
