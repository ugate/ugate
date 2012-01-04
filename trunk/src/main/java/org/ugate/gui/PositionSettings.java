package org.ugate.gui;

import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import org.ugate.RemoteSettings;
import org.ugate.UGateKeeper;
import org.ugate.gui.components.Gauge.IndicatorType;
import org.ugate.gui.components.UGateGaugePreferenceView;
import org.ugate.resources.RS;

/**
 * Camera control view
 */
public class PositionSettings extends ControlPane {
	
	/**
	 * Constructor
	 * 
	 * @param controlBar the control bar
	 */
	public PositionSettings(final ControlBar controlBar) {
		super(controlBar);
		addCameraChildren();
		addCameraSensorChildren();
		addSensorChildren();
	}

	protected void addCameraChildren() {
		final GridPane grid = new GridPane();
		grid.setHgap(0d);
		grid.setVgap(0d);
		final Label panHeader = createLabel("cam.pan");
		grid.add(panHeader, 0, 0);
		final UGateGaugePreferenceView camPanGauge = new UGateGaugePreferenceView(
				RemoteSettings.CAM_ANGLE_PAN, null, UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex(),
				IndicatorType.KNOB, KNOB_SIZE_SCALE,
				10d, 0, 0d, 180d, 19, 0, FORMAT_ANGLE, RS.IMG_PAN, COLOR_CAM);
		controlBar.addHelpTextTrigger(camPanGauge, RS.rbLabel("cam.pan.desc"));
		grid.add(camPanGauge, 0, 1);
		final Label tiltHeader = createLabel("cam.tilt");
		grid.add(tiltHeader, 1, 0);
		final ImageView tiltImgView = RS.imgView(camPanGauge.imageView.getImage());
		tiltImgView.setRotate(90d);
		final UGateGaugePreferenceView camTiltGauge = new UGateGaugePreferenceView(
				RemoteSettings.CAM_ANGLE_TILT, null, UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex(), 
				IndicatorType.KNOB, KNOB_SIZE_SCALE,
				10d, 0, 0, 180d, 19, 0, FORMAT_ANGLE, tiltImgView, COLOR_CAM);
		controlBar.addHelpTextTrigger(camTiltGauge, RS.rbLabel("cam.tilt"));
		grid.add(camTiltGauge, 1, 1);
//		final UGateGaugeDisplay camImgResGauge = new UGateGaugeDisplay(IndicatorType.KNOB, 0.12d,
//				0, 0, 70d, 40d, 1, 0, 0d, "%03d", RS.IMG_CAM_RESOLUTION,
//				"Sets the camera resolution of the images taken when an alarm is triggered", 
//				Color.LIGHTGREEN, null, Orientation.HORIZONTAL);
		
		final Group camCell = createCell(false, true, grid);
		add(camCell, 0, 0);
	}
	
	protected void addCameraSensorChildren() {
		final GridPane grid = new GridPane();
		final Label sonarPirPanHeader = createLabel("cam.pan.sonarpir");
		grid.add(sonarPirPanHeader, 0, 0);
		final Label sonarPirTiltHeader = createLabel("cam.tilt.sonarpir");
		grid.add(sonarPirTiltHeader, 1, 0);
		final UGateGaugePreferenceView sonarPirPanGauge = new UGateGaugePreferenceView(
				RemoteSettings.CAM_IR_TRIP_ANGLE_PAN, null, UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex(), 
				IndicatorType.KNOB, KNOB_SIZE_SCALE,
				10.7d, 0, 0, 180d, 18, 0, FORMAT_ANGLE, RS.IMG_PAN, COLOR_MULTI);
		controlBar.addHelpTextTrigger(sonarPirPanGauge, RS.rbLabel("cam.pan.sonarpir.desc"));
		grid.add(sonarPirPanGauge, 0, 1);
		final ImageView sonarPirTiltImgView = RS.imgView(sonarPirPanGauge.imageView.getImage());
		sonarPirTiltImgView.setRotate(90d);
		final UGateGaugePreferenceView sonarPirTiltGauge = new UGateGaugePreferenceView(
				RemoteSettings.CAM_IR_TRIP_ANGLE_TILT, null, UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex(), 
				IndicatorType.KNOB, KNOB_SIZE_SCALE,
				10.7d, 0, 0, 180d, 18, 0, FORMAT_ANGLE, sonarPirTiltImgView, COLOR_MULTI);
		controlBar.addHelpTextTrigger(sonarPirTiltGauge, RS.rbLabel("cam.tilt.sonarpir.desc"));
		grid.add(sonarPirTiltGauge, 1, 1);
		final Label headerMwPanHeader = createLabel("cam.pan.microwave");
		grid.add(headerMwPanHeader, 0, 2);
		final Label mwPanHeader = createLabel("cam.tilt.microwave");
		grid.add(mwPanHeader, 1, 2);
		final UGateGaugePreferenceView mwPanGauge = new UGateGaugePreferenceView(
				RemoteSettings.CAM_MW_TRIP_ANGLE_PAN, null, UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex(), 
				IndicatorType.KNOB, KNOB_SIZE_SCALE,
				10.7d, 0, 0, 180d, 18, 0, FORMAT_ANGLE, RS.IMG_PAN, COLOR_MW);
		controlBar.addHelpTextTrigger(mwPanGauge, RS.rbLabel("cam.pan.microwave.desc"));
		grid.add(mwPanGauge, 0, 3);
		final UGateGaugePreferenceView mwTiltGauge = new UGateGaugePreferenceView(
				RemoteSettings.CAM_MW_TRIP_ANGLE_TILT, null, UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex(), 
				IndicatorType.KNOB, KNOB_SIZE_SCALE,
				10.7d, 0, 0, 180d, 18, 0, FORMAT_ANGLE, RS.IMG_PAN, COLOR_MW);
		controlBar.addHelpTextTrigger(mwTiltGauge, RS.rbLabel("cam.tilt.microwave.desc"));
		grid.add(mwTiltGauge, 1, 3);
		
		final Group cell = createCell(false, true, grid);
		add(cell, 1, 0);
	}
	
	protected void addSensorChildren() {
		final GridPane grid = new GridPane();
		final Label sonarPirPanHeader = createLabel("sonarpir.pan");
		grid.add(sonarPirPanHeader, 0, 0);
		final Label sonarPirTiltHeader = createLabel("sonarpir.tilt");
		grid.add(sonarPirTiltHeader, 1, 0);
		final Label headerMW = createLabel("microwave.pan");
		grid.add(headerMW, 0, 2);
		final UGateGaugePreferenceView sonarPirPanGauge = new UGateGaugePreferenceView(
				RemoteSettings.SONAR_IR_ANGLE_PAN, null, 
				UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex(), IndicatorType.KNOB, KNOB_SIZE_SCALE,
				10d, 0, 0, 180d, 19, 0, FORMAT_ANGLE, RS.IMG_PAN, COLOR_MULTI);
		controlBar.addHelpTextTrigger(sonarPirPanGauge, RS.rbLabel("sonarpir.pan.desc"));
		grid.add(sonarPirPanGauge, 0, 1);
		final ImageView sonarPirTiltImgView = RS.imgView(sonarPirPanGauge.imageView.getImage());
		sonarPirTiltImgView.setRotate(90d);
		final UGateGaugePreferenceView sonarPirTiltGauge = new UGateGaugePreferenceView(
				RemoteSettings.SONAR_IR_ANGLE_TILT, null, UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex(), 
				IndicatorType.KNOB, KNOB_SIZE_SCALE,
				10d, 0, 0, 180d, 19, 0, FORMAT_ANGLE, sonarPirTiltImgView, COLOR_MULTI);
		controlBar.addHelpTextTrigger(sonarPirTiltGauge, RS.rbLabel("sonarpir.tilt.desc"));
		grid.add(sonarPirTiltGauge, 1, 1);
		final UGateGaugePreferenceView mwPanGauge = new UGateGaugePreferenceView(
				RemoteSettings.MW_ANGLE_PAN, null, UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex(), 
				IndicatorType.KNOB, KNOB_SIZE_SCALE,
				10d, 0, 0, 180d, 19, 0, FORMAT_ANGLE, RS.IMG_PAN, COLOR_MW);
		controlBar.addHelpTextTrigger(mwPanGauge, RS.rbLabel("microwave.pan.desc"));
		grid.add(mwPanGauge, 0, 3);
		
		final Group cell = createCell(false, true, grid);
		add(cell, 2, 0);
	}
}
