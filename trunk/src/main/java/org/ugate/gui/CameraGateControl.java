package org.ugate.gui;

import java.util.List;

import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import org.ugate.Settings;
import org.ugate.UGateKeeper;
import org.ugate.gui.components.Gauge.IndicatorType;
import org.ugate.gui.components.UGateGaugePreferenceView;
import org.ugate.gui.components.UGateTextFieldPreferenceView;
import org.ugate.gui.components.UGateToggleSwitchPreferenceView;
import org.ugate.resources.RS;

/**
 * Camera control view
 */
public class CameraGateControl extends ControlPane {

	public static final double LABEL_WIDTH = 125d;
	private UGateTextFieldPreferenceView recipients;
	private UGateToggleSwitchPreferenceView recipientsToggleSwitch;
	
	public CameraGateControl(final ScrollPane helpText) {
		super(helpText);
		addCameraChildren();
		addCameraSensorChildren();
		addGateChildren();
	}

	protected void addCameraChildren() {
		final GridPane grid = new GridPane();
		grid.setHgap(0d);
		grid.setVgap(0d);
		final Label panHeader = new Label("Cam Pan Angle");
		panHeader.getStyleClass().add("gauge-header");
		grid.add(panHeader, 0, 0);
		final UGateGaugePreferenceView camPanGauge = new UGateGaugePreferenceView(
				Settings.SV_CAM_ANGLE_PAN_KEY, null, IndicatorType.KNOB, KNOB_SIZE_SCALE,
				10d, 0, 0d, 180d, 19, 0, FORMAT_ANGLE, RS.IMG_PAN, COLOR_PAN_TILT);
		addHelpText(camPanGauge, "Camera Pan: Current camera pan angle (in degrees)");
		grid.add(camPanGauge, 0, 1);
		final Label tiltHeader = new Label("Cam Tilt Angle");
		tiltHeader.getStyleClass().add("gauge-header");
		grid.add(tiltHeader, 1, 0);
		final ImageView tiltImgView = RS.imgView(camPanGauge.imageView.getImage());
		tiltImgView.setRotate(90d);
		final UGateGaugePreferenceView camTiltGauge = new UGateGaugePreferenceView(
				Settings.SV_CAM_ANGLE_TILT_KEY, null, IndicatorType.KNOB, KNOB_SIZE_SCALE,
				10d, 0, 0, 180d, 19, 0, FORMAT_ANGLE, tiltImgView, COLOR_PAN_TILT);
		addHelpText(camTiltGauge, "Camera Tilt: Current camera tilt angle (in degrees)");
		grid.add(camTiltGauge, 1, 1);
		final Label headerImageRes = new Label("Image Resolution");
		headerImageRes.getStyleClass().add("gauge-header");
//		final UGateGaugeDisplay camImgResGauge = new UGateGaugeDisplay(IndicatorType.KNOB, 0.12d,
//				0, 0, 70d, 40d, 1, 0, 0d, "%03d", RS.IMG_CAM_RESOLUTION,
//				"Sets the camera resolution of the images taken when an alarm is triggered", 
//				Color.LIGHTGREEN, null, Orientation.HORIZONTAL);
		final UGateToggleSwitchPreferenceView imgResToggleSwitch  = new UGateToggleSwitchPreferenceView(Settings.SV_CAM_RES_KEY, 
				RS.IMG_CAM_RESOLUTION, RS.IMG_CAM_RESOLUTION, "VGA", "QVGA");
		addHelpText(imgResToggleSwitch, 
				"Sets the camera resolution of the images taken when an alarm is triggered");
		final Label headerEmailConf = new Label("Email Alarm Notification");
		headerEmailConf.getStyleClass().add("gauge-header");
		recipientsToggleSwitch = new UGateToggleSwitchPreferenceView(Settings.PV_MAIL_ALARM_ON_KEY, 
				RS.IMG_EMAIL_SELECTED, RS.IMG_EMAIL_DESELECTED);
		addHelpText(recipientsToggleSwitch, 
				"Toggle sending email notifications for images taken (by alarm trip or manually)");
		recipients = new UGateTextFieldPreferenceView(Settings.PV_MAIL_RECIPIENTS_KEY, 
				UGateTextFieldPreferenceView.Type.TYPE_TEXT_AREA, "Recipients (semi-colon delimited emails)", 
				"Semi-colon delimited list of emails to send image to (blank if no emails should be sent)");
		recipients.textArea.setPrefRowCount(5);
		recipients.textArea.setWrapText(true);
		addHelpText(recipients, "Recipients that will receive an email notification with an image image attachment when the alarm criteria is met.");
		
		final Group camCell = createCell(false, true, grid, headerImageRes, imgResToggleSwitch, 
				headerEmailConf, recipientsToggleSwitch, recipients);
		add(camCell, 0, 0);
	}
	
	protected void addCameraSensorChildren() {
		final GridPane grid = new GridPane();
		final Label sonarPirPanHeader = new Label("Cam Pan Angle (On Sonar/PIR Alarm)");
		sonarPirPanHeader.setWrapText(true);
		sonarPirPanHeader.setPrefWidth(LABEL_WIDTH);
		sonarPirPanHeader.getStyleClass().add("gauge-header");
		grid.add(sonarPirPanHeader, 0, 0);
		final Label sonarPirTiltHeader = new Label("Cam Tilt Angle (On Sonar/PIR Alarm)");
		sonarPirTiltHeader.setWrapText(true);
		sonarPirTiltHeader.setPrefWidth(LABEL_WIDTH);
		sonarPirTiltHeader.getStyleClass().add("gauge-header");
		grid.add(sonarPirTiltHeader, 1, 0);
		final UGateGaugePreferenceView sonarPirPanGauge = new UGateGaugePreferenceView(
				Settings.SV_CAM_IR_TRIP_ANGLE_PAN_KEY, null, IndicatorType.KNOB, KNOB_SIZE_SCALE,
				10.7d, 0, 0, 180d, 18, 0, FORMAT_ANGLE, RS.IMG_PAN, COLOR_PAN_TILT);
		addHelpText(sonarPirPanGauge, "Sonar/PIR Pan: Current trip alram sensor pan angle in degrees. " + 
				"An angle of 181 indicates the camera will maintain it's position when the alaram is triggered.");
		grid.add(sonarPirPanGauge, 0, 1);
		final ImageView sonarPirTiltImgView = RS.imgView(sonarPirPanGauge.imageView.getImage());
		sonarPirTiltImgView.setRotate(90d);
		final UGateGaugePreferenceView sonarPirTiltGauge = new UGateGaugePreferenceView(
				Settings.SV_CAM_IR_TRIP_ANGLE_TILT_KEY, null, IndicatorType.KNOB, KNOB_SIZE_SCALE,
				10.7d, 0, 0, 180d, 18, 0, FORMAT_ANGLE, sonarPirTiltImgView, COLOR_PAN_TILT);
		addHelpText(sonarPirTiltGauge, "Sonar/PIR Tilt: Current trip alarm sensor tilt angle in degrees. " + 
				"An angle of 181 indicates the camera will maintain it's position when the alaram is triggered.");
		grid.add(sonarPirTiltGauge, 1, 1);
		final Label headerMwPanHeader = new Label("Cam Pan Angle (On Microwave Alarm)");
		headerMwPanHeader.setWrapText(true);
		headerMwPanHeader.setPrefWidth(LABEL_WIDTH);
		headerMwPanHeader.getStyleClass().add("gauge-header");
		grid.add(headerMwPanHeader, 0, 2);
		final Label mwPanHeader = new Label("Cam Tilt Angle (On Microwave Alarm)");
		mwPanHeader.setWrapText(true);
		mwPanHeader.setPrefWidth(LABEL_WIDTH);
		mwPanHeader.getStyleClass().add("gauge-header");
		grid.add(mwPanHeader, 1, 2);
		final UGateGaugePreferenceView mwPanGauge = new UGateGaugePreferenceView(
				Settings.SV_CAM_MW_TRIP_ANGLE_PAN_KEY, null, IndicatorType.KNOB, KNOB_SIZE_SCALE,
				10.7d, 0, 0, 180d, 18, 0, FORMAT_ANGLE, RS.IMG_PAN, COLOR_PAN_TILT);
		addHelpText(mwPanGauge, "Microwave Pan: Current trip alarm sensor pan angle in degrees. " + 
				"An angle of 181 indicates the camera will maintain it's position when the alaram is triggered.");
		grid.add(mwPanGauge, 0, 3);
		final UGateGaugePreferenceView mwTiltGauge = new UGateGaugePreferenceView(
				Settings.SV_CAM_MW_TRIP_ANGLE_TILT_KEY, null, IndicatorType.KNOB, KNOB_SIZE_SCALE,
				10.7d, 0, 0, 180d, 18, 0, FORMAT_ANGLE, RS.IMG_PAN, COLOR_PAN_TILT);
		addHelpText(mwTiltGauge, "Microwave Tilt: Current trip alarm sensor pan angle in degrees. " + 
				"An angle of 181 indicates the camera will maintain it's position when the alaram is triggered.");
		grid.add(mwTiltGauge, 1, 3);
		
		final Group cell = createCell(false, true, grid);
		add(cell, 1, 0);
	}
	
	protected void addGateChildren() {
		final Label gateHeader = new Label("Gate Configuration");
		gateHeader.getStyleClass().add("gauge-header");
		final UGateToggleSwitchPreferenceView gateToggleSwitchView = new UGateToggleSwitchPreferenceView(
				Settings.SV_GATE_ACCESS_ON_KEY, RS.IMG_GATE_SELECTED,
				RS.IMG_GATE_DESELECTED);
		addHelpText(gateToggleSwitchView, "Toogles gate access. When disabled the gate will not open regardless of key code entry using a remote control.");
		final Label gateCtrlHeader = new Label("Gate State (open/close)");
		gateCtrlHeader.getStyleClass().add("gauge-header");
		final ImageView gateToggleButton = RS.imgView(RS.IMG_GATE_CLOSED);
		final Group readingsGroup = Controls.createReadingsDisplay(PADDING_INSETS, CHILD_SPACING, 
				1, gateToggleButton);
		addHelpText(readingsGroup, "Toogles opening/closing gate");
		
		final Group cell = createCell(false, true, gateHeader, gateToggleSwitchView, 
				gateCtrlHeader, readingsGroup);
		add(cell, 2, 0);
	}
	
	public boolean addValues(final List<Integer> values) {
		// values need to be added in a predefined order
		UGateKeeper.DEFAULT.preferencesSet(Settings.PV_MAIL_RECIPIENTS_ON_KEY, 
				String.valueOf(recipientsToggleSwitch.getToggleItem().toggleSwitch.selectedProperty().get()));
		UGateKeeper.DEFAULT.preferencesSet(Settings.PV_MAIL_RECIPIENTS_KEY, recipients.textField.getText());
		values.add(recipientsToggleSwitch.getToggleItem().toggleSwitch.selectedProperty().get() ? 1 : 0);
		//values.add(recipientsToggleSwitch.toggleSwitch.selectedProperty().get() ? 1 : 0);
		return true;
	}
}
