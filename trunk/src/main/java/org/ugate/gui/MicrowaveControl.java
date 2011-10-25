package org.ugate.gui;

import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import org.ugate.UGateUtil;
import org.ugate.gui.components.ToggleSwitchPreferenceView;
import org.ugate.gui.components.UGateSliderGauge;
import org.ugate.resources.RS;

/**
 * Microwave sensor control view
 */
public class MicrowaveControl extends ControlPane {

	@Override
	protected Node[] createTopViewChildren() {
		return new Node[] {  };
	}

	@Override
	protected Node[] createLeftViewChildren() {
		final UGateSliderGauge mwPanGauge = new UGateSliderGauge(1, 180, 90, 1, "%03d", RS.IMG_PAN,
				"Microwave Pan: Current trip alram sensor pan angle (in degrees)", false, Color.YELLOW, null);
		return new Node[] { mwPanGauge };
	}

	@Override
	protected Node[] createCenterViewChildren() {
		final ImageView mwNavStatusButton = RS.imgView(RS.IMG_SENSOR_ARM);
		final ImageView mwNavButton = RS.imgView(RS.IMG_NAV_CAM);
		return new Node[] {mwNavStatusButton, mwNavButton};
	}

	@Override
	protected Node[] createRightViewChildren() {
		final ToggleSwitchPreferenceView mwToggleSwitchView = new ToggleSwitchPreferenceView(UGateUtil.SV_MW_ALARM_ON_KEY, 
				RS.IMG_MICROWAVE_ALARM_ON, RS.IMG_MICROWAVE_ALARM_OFF, 
				"Toggle Microwave intruder alarm that takes a picture, stores it on the computer, and sends email notification with image attachment (if on)");
		final VBox mwTripView = new VBox();
		final UGateSliderGauge mwTripGauge = new UGateSliderGauge(1.0f, 26.0f, 10.0f, 0.5f, "%04.1f", RS.IMG_SPEEDOMETER,
				"Microwave Speed Threshold: Cycles/Second at which an image will be taken and sent to the computer and recipients (if alarm is turned on)", 
				true, null, null);
		final UGateSliderGauge mwTripRateGauge = new UGateSliderGauge(0, 120, 0, 1, "%03d", RS.IMG_STOPWATCH,
				"Microwave Delay Between Photos: Delay in minutes between pictures taken/sent when an object is within the speed threshold.\n" +
				"When zero, there may still be a few seconds beween photos due to the wireless transfer rate", 
				true, null, null);
		mwTripView.getChildren().addAll(mwTripGauge, mwTripRateGauge);
		return new Node[] { mwToggleSwitchView, mwTripView };
	}
}
