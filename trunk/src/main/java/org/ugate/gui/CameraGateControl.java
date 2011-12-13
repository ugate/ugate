package org.ugate.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import org.ugate.Command;
import org.ugate.IGateKeeperListener;
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
	
	public CameraGateControl(final Controls controls) {
		super(controls);
		addCameraChildren();
		addCameraSensorChildren();
		addGateChildren();
	}

	protected void addCameraChildren() {
		final GridPane grid = new GridPane();
		grid.setHgap(0d);
		grid.setVgap(0d);
		final Label panHeader = new Label(RS.rbLabel("cam.pan"));
		panHeader.getStyleClass().add("gauge-header");
		grid.add(panHeader, 0, 0);
		final UGateGaugePreferenceView camPanGauge = new UGateGaugePreferenceView(
				Settings.CAM_ANGLE_PAN_KEY, null, IndicatorType.KNOB, KNOB_SIZE_SCALE,
				10d, 0, 0d, 180d, 19, 0, FORMAT_ANGLE, RS.IMG_PAN, COLOR_PAN_TILT);
		controls.addHelpTextTrigger(camPanGauge, RS.rbLabel("cam.pan.desc"));
		grid.add(camPanGauge, 0, 1);
		final Label tiltHeader = new Label(RS.rbLabel("cam.tilt"));
		tiltHeader.getStyleClass().add("gauge-header");
		grid.add(tiltHeader, 1, 0);
		final ImageView tiltImgView = RS.imgView(camPanGauge.imageView.getImage());
		tiltImgView.setRotate(90d);
		final UGateGaugePreferenceView camTiltGauge = new UGateGaugePreferenceView(
				Settings.CAM_ANGLE_TILT_KEY, null, IndicatorType.KNOB, KNOB_SIZE_SCALE,
				10d, 0, 0, 180d, 19, 0, FORMAT_ANGLE, tiltImgView, COLOR_PAN_TILT);
		controls.addHelpTextTrigger(camTiltGauge, RS.rbLabel("cam.tilt"));
		grid.add(camTiltGauge, 1, 1);
		final Label headerImageRes = new Label(RS.rbLabel("cam.resolution"));
		headerImageRes.getStyleClass().add("gauge-header");
//		final UGateGaugeDisplay camImgResGauge = new UGateGaugeDisplay(IndicatorType.KNOB, 0.12d,
//				0, 0, 70d, 40d, 1, 0, 0d, "%03d", RS.IMG_CAM_RESOLUTION,
//				"Sets the camera resolution of the images taken when an alarm is triggered", 
//				Color.LIGHTGREEN, null, Orientation.HORIZONTAL);
		final UGateToggleSwitchPreferenceView imgResToggleSwitch  = new UGateToggleSwitchPreferenceView(Settings.CAM_RES_KEY, 
				RS.IMG_CAM_RESOLUTION, RS.IMG_CAM_RESOLUTION, 
				RS.rbLabel("cam.resolution.vga"), RS.rbLabel("cam.resolution.qvga"));
		controls.addHelpTextTrigger(imgResToggleSwitch, RS.rbLabel("cam.resolution.desc"));
		final Label headerEmailConf = new Label(RS.rbLabel("mail.alarm.notify"));
		headerEmailConf.getStyleClass().add("gauge-header");
		final UGateToggleSwitchPreferenceView recipientsToggleSwitch = new UGateToggleSwitchPreferenceView(
				Settings.MAIL_ALARM_ON_KEY, RS.IMG_EMAIL_SELECTED, RS.IMG_EMAIL_DESELECTED);
		controls.addHelpTextTrigger(recipientsToggleSwitch, RS.rbLabel("mail.alarm.notify.desc"));
		final UGateTextFieldPreferenceView recipients = new UGateTextFieldPreferenceView(Settings.MAIL_RECIPIENTS_KEY, 
				UGateTextFieldPreferenceView.Type.TYPE_TEXT_AREA, RS.rbLabel("mail.alarm.notify.emails"), 
				"");
		recipients.textArea.setPrefRowCount(5);
		recipients.textArea.setWrapText(true);
		recipients.textArea.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				if (!newValue) {
					UGateKeeper.DEFAULT.preferencesSet(Settings.MAIL_RECIPIENTS_KEY, 
							recipients.textArea.getText());
				}
			}
		});
		controls.addHelpTextTrigger(recipients, RS.rbLabel("mail.alarm.notify.emails.desc"));
		
		final Group camCell = createCell(false, true, grid, headerImageRes, imgResToggleSwitch, 
				headerEmailConf, recipientsToggleSwitch, recipients);
		add(camCell, 0, 0);
	}
	
	protected void addCameraSensorChildren() {
		final GridPane grid = new GridPane();
		final Label sonarPirPanHeader = new Label(RS.rbLabel("cam.pan.sonarpir"));
		sonarPirPanHeader.setWrapText(true);
		sonarPirPanHeader.setPrefWidth(LABEL_WIDTH);
		sonarPirPanHeader.getStyleClass().add("gauge-header");
		grid.add(sonarPirPanHeader, 0, 0);
		final Label sonarPirTiltHeader = new Label(RS.rbLabel("cam.tilt.sonarpir"));
		sonarPirTiltHeader.setWrapText(true);
		sonarPirTiltHeader.setPrefWidth(LABEL_WIDTH);
		sonarPirTiltHeader.getStyleClass().add("gauge-header");
		grid.add(sonarPirTiltHeader, 1, 0);
		final UGateGaugePreferenceView sonarPirPanGauge = new UGateGaugePreferenceView(
				Settings.CAM_IR_TRIP_ANGLE_PAN_KEY, null, IndicatorType.KNOB, KNOB_SIZE_SCALE,
				10.7d, 0, 0, 180d, 18, 0, FORMAT_ANGLE, RS.IMG_PAN, COLOR_PAN_TILT);
		controls.addHelpTextTrigger(sonarPirPanGauge, RS.rbLabel("cam.pan.sonarpir.desc"));
		grid.add(sonarPirPanGauge, 0, 1);
		final ImageView sonarPirTiltImgView = RS.imgView(sonarPirPanGauge.imageView.getImage());
		sonarPirTiltImgView.setRotate(90d);
		final UGateGaugePreferenceView sonarPirTiltGauge = new UGateGaugePreferenceView(
				Settings.CAM_IR_TRIP_ANGLE_TILT_KEY, null, IndicatorType.KNOB, KNOB_SIZE_SCALE,
				10.7d, 0, 0, 180d, 18, 0, FORMAT_ANGLE, sonarPirTiltImgView, COLOR_PAN_TILT);
		controls.addHelpTextTrigger(sonarPirTiltGauge, RS.rbLabel("cam.tilt.sonarpir.desc"));
		grid.add(sonarPirTiltGauge, 1, 1);
		final Label headerMwPanHeader = new Label(RS.rbLabel("cam.pan.microwave"));
		headerMwPanHeader.setWrapText(true);
		headerMwPanHeader.setPrefWidth(LABEL_WIDTH);
		headerMwPanHeader.getStyleClass().add("gauge-header");
		grid.add(headerMwPanHeader, 0, 2);
		final Label mwPanHeader = new Label(RS.rbLabel("cam.tilt.microwave"));
		mwPanHeader.setWrapText(true);
		mwPanHeader.setPrefWidth(LABEL_WIDTH);
		mwPanHeader.getStyleClass().add("gauge-header");
		grid.add(mwPanHeader, 1, 2);
		final UGateGaugePreferenceView mwPanGauge = new UGateGaugePreferenceView(
				Settings.CAM_MW_TRIP_ANGLE_PAN_KEY, null, IndicatorType.KNOB, KNOB_SIZE_SCALE,
				10.7d, 0, 0, 180d, 18, 0, FORMAT_ANGLE, RS.IMG_PAN, COLOR_PAN_TILT);
		controls.addHelpTextTrigger(mwPanGauge, RS.rbLabel("cam.pan.microwave.desc"));
		grid.add(mwPanGauge, 0, 3);
		final UGateGaugePreferenceView mwTiltGauge = new UGateGaugePreferenceView(
				Settings.CAM_MW_TRIP_ANGLE_TILT_KEY, null, IndicatorType.KNOB, KNOB_SIZE_SCALE,
				10.7d, 0, 0, 180d, 18, 0, FORMAT_ANGLE, RS.IMG_PAN, COLOR_PAN_TILT);
		controls.addHelpTextTrigger(mwTiltGauge, RS.rbLabel("cam.tilt.microwave.desc"));
		grid.add(mwTiltGauge, 1, 3);
		
		final Group cell = createCell(false, true, grid);
		add(cell, 1, 0);
	}
	
	protected void addGateChildren() {
		final Label gateHeader = new Label(RS.rbLabel("gate.conf"));
		gateHeader.getStyleClass().add("gauge-header");
		final UGateToggleSwitchPreferenceView gateToggleSwitchView = new UGateToggleSwitchPreferenceView(
				Settings.GATE_ACCESS_ON_KEY, RS.IMG_GATE_SELECTED,
				RS.IMG_GATE_DESELECTED);
		controls.addHelpTextTrigger(gateToggleSwitchView, RS.rbLabel("gate.toggle"));
		final Label gateCtrlHeader = new Label(RS.rbLabel("gate.state"));
		gateCtrlHeader.getStyleClass().add("gauge-header");
		final ImageView gateToggleButton = RS.imgView(RS.IMG_GATE_CLOSED);
		final Group gateGroup = Controls.createReadingsDisplay(PADDING_INSETS, CHILD_SPACING, 
				1, gateToggleButton);
		gateGroup.setCursor(Cursor.HAND);
		controls.addHelpTextTrigger(gateGroup, RS.rbLabel("gate.toggle.desc"));
		gateGroup.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(final MouseEvent event) {
				controls.createControlsService(Command.GATE_TOGGLE_OPEN_CLOSE).start();
			}
		});
		UGateKeeper.DEFAULT.preferencesAddListener(new IGateKeeperListener() {
			@Override
			public void handle(final IGateKeeperListener.Event type, final String node,
					final Settings key, final Command command, final String oldValue, 
					final String newValue) {
				if (command == null || command != Command.GATE_TOGGLE_OPEN_CLOSE) {
					return;
				}
				if (type == IGateKeeperListener.Event.SETTINGS_SEND_SUCCESS) {
					controls.setHelpText(null);
					if (gateToggleButton.getImage().equals(RS.img(RS.IMG_GATE_CLOSED))) {
						gateToggleButton.setImage(RS.img(RS.IMG_GATE_OPENED));
					} else {
						gateToggleButton.setImage(RS.img(RS.IMG_GATE_CLOSED));
					}
				} else if (type == IGateKeeperListener.Event.SETTINGS_SEND_FAILED) {
					controls.setHelpText(String.format(RS.rbLabel("gate.toggle.failed"),
							(controls.isPropagateSettingsToAllRemoteNodes() ? RS.rbLabel("all") : 
								controls.getRemoteNodeAddress())));
				}
			}
		});
		
		final Group cell = createCell(false, true, gateHeader, gateToggleSwitchView, 
				gateCtrlHeader, gateGroup);
		add(cell, 2, 0);
	}
}
