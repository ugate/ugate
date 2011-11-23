package org.ugate.gui;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import org.ugate.UGateUtil;
import org.ugate.gui.components.Gauge.IndicatorType;
import org.ugate.gui.components.Digits;
import org.ugate.gui.components.ToggleSwitchPreferenceView;
import org.ugate.gui.components.UGateGaugeDisplay;
import org.ugate.resources.RS;

/**
 * Microwave sensor control view
 */
public class MicrowaveControl extends ControlPane {
	
	public static final String FORMAT_MW = "%04.1f";
	public static final Color COLOR_MW = Color.CHARTREUSE;

	@Override
	protected Node[] createLeftViewChildren() {
		final Label header = new Label("Micorwave Configuration");
		header.getStyleClass().add("gauge-header");
		final GridPane grid = new GridPane();
		final ToggleSwitchPreferenceView mwToggleSwitchView = new ToggleSwitchPreferenceView(UGateUtil.SV_MW_ALARM_ON_KEY, 
				RS.IMG_MICROWAVE_ALARM_ON, RS.IMG_MICROWAVE_ALARM_OFF, 
				"Toggle Microwave intruder alarm that takes a picture, stores it on the computer, and sends email notification with image attachment (if on)");
		grid.add(mwToggleSwitchView, 0, 0);
		final UGateGaugeDisplay mwTripGauge = new UGateGaugeDisplay(IndicatorType.NEEDLE, NEEDLE_SIZE_SCALE,
				1d, 2, 0, 180d, 9, 4, 5d, FORMAT_MW, RS.IMG_SPEEDOMETER,
				"Microwave Speed Threshold: Cycles/Second at which an image will be taken and sent to the computer and recipients (if alarm is turned on)", 
				COLOR_MW);
		grid.add(mwTripGauge, 0, 1);
		final UGateGaugeDisplay mwTripRateGauge = new UGateGaugeDisplay(IndicatorType.NEEDLE, NEEDLE_SIZE_SCALE,
				1d, 0, 0, 180d, 61, 0, 0d, FORMAT_DELAY, RS.IMG_STOPWATCH,
				"Microwave Delay Between Photos: Delay in minutes between pictures taken/sent when an object is within the speed threshold.\n" +
				"When zero, there may still be a few seconds beween photos due to the wireless transfer rate", 
				COLOR_MW);
		grid.add(mwTripRateGauge, 1, 1);
		return new Node[] { header, grid };
	}

	@Override
	protected Node[] createCenterViewChildren() {
		final Label headerMovement = new Label("Microwave Pan Angle");
		headerMovement.getStyleClass().add("gauge-header");
		final UGateGaugeDisplay mwPanGauge = new UGateGaugeDisplay(IndicatorType.KNOB, KNOB_SIZE_SCALE,
				10d, 0, 0, 180d, 19, 4, 90d, FORMAT_ANGLE, RS.IMG_PAN,
				"Microwave Pan: Current trip alram sensor pan angle (in degrees)", COLOR_PAN_TILT);
		
		final Label headerMW = new Label("Microwave Readings");
		headerMW.getStyleClass().add("gauge-header");
		final Digits mwReading = new Digits(String.format(FORMAT_MW, 7.5f), 0.15f, COLOR_MW, null);
		final Group readingsGroup = createReadingsDisplay(mwReading);
		return new Node[] { headerMovement, mwPanGauge, headerMW, readingsGroup };
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
