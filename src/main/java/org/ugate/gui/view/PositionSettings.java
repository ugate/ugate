package org.ugate.gui.view;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import org.ugate.gui.ControlBar;
import org.ugate.gui.ControlPane;
import org.ugate.gui.GuiUtil;
import org.ugate.gui.components.Gauge.IndicatorType;
import org.ugate.gui.components.UGateCtrlBox;
import org.ugate.gui.components.UGateGaugeBox;
import org.ugate.resources.RS;
import org.ugate.resources.RS.KEY;
import org.ugate.service.entity.Command;
import org.ugate.service.entity.Model;
import org.ugate.service.entity.RemoteNodeType;
import org.ugate.service.entity.jpa.RemoteNode;

/**
 * Camera control view
 */
public class PositionSettings extends ControlPane {
	
	public static final int MIN_SENSOR_INDEX = 1;
	public static final int MAX_SENSOR_INDEX = 4;
	private final AtomicBoolean NUMERIC_STEPPER_CHANGING = new AtomicBoolean(false);
	private UGateCtrlBox<RemoteNode, Model, Void> sonarAnglePriority;
	private UGateCtrlBox<RemoteNode, Model, Void> pirAnglePriority;
	private UGateCtrlBox<RemoteNode, Model, Void> mwAnglePriority;
	private UGateCtrlBox<RemoteNode, Model, Void> laserAnglePriority;
	
	/**
	 * Constructor
	 * 
	 * @param controlBar the control bar
	 */
	public PositionSettings(final ControlBar controlBar) {
		super(controlBar);
		addColumn1();
		addColumn2();
		addColumn3();
	}

	protected void addColumn1() {
		final GridPane grid = new GridPane();
		grid.setHgap(0d);
		grid.setVgap(0d);
		// cam pan
		final Label panHeader = createLabel(KEY.CAM_PAN);
		grid.add(panHeader, 0, 0);
		final UGateGaugeBox<RemoteNode> camPanGauge = new UGateGaugeBox<>(
				controlBar.getRemoteNodePA(), RemoteNodeType.CAM_ANGLE_PAN,
				null, IndicatorType.KNOB, KNOB_SIZE_SCALE, 10d, 0, 0d, 180d,
				19, 0, FORMAT_ANGLE, RS.IMG_PAN, GuiUtil.COLOR_CAM);
		controlBar.addHelpTextTrigger(camPanGauge, RS.rbLabel(KEY.CAM_PAN_DESC));
		grid.add(camPanGauge, 0, 1);
		// cam tilt
		final Label tiltHeader = createLabel(KEY.CAM_TILT);
		grid.add(tiltHeader, 1, 0);
		final ImageView tiltImgView = RS.imgView(camPanGauge.imageView.getImage(), false);
		tiltImgView.setRotate(90d);
		final UGateGaugeBox<RemoteNode> camTiltGauge = new UGateGaugeBox<>(
				controlBar.getRemoteNodePA(), RemoteNodeType.CAM_ANGLE_TILT,
				null, IndicatorType.KNOB, KNOB_SIZE_SCALE, 10d, 0, 0, 180d, 19,
				0, FORMAT_ANGLE, tiltImgView, GuiUtil.COLOR_CAM);
		controlBar.addHelpTextTrigger(camTiltGauge, RS.rbLabel(KEY.CAM_TILT));
		grid.add(camTiltGauge, 1, 1);
		// sonar/pir pan
		final Label sonarPirPanHeader = createLabel(KEY.SONAR_PIR_PAN);
		grid.add(sonarPirPanHeader, 0, 2);
		final UGateGaugeBox<RemoteNode> sonarPirPanGauge = new UGateGaugeBox<>(
				controlBar.getRemoteNodePA(),
				RemoteNodeType.SONAR_PIR_ANGLE_PAN, null, IndicatorType.KNOB,
				KNOB_SIZE_SCALE, 10d, 0, 0, 180d, 19, 0, FORMAT_ANGLE,
				RS.IMG_PAN, GuiUtil.COLOR_MULTI);
		controlBar.addHelpTextTrigger(sonarPirPanGauge, RS.rbLabel(KEY.SONAR_PIR_PAN_DESC));
		grid.add(sonarPirPanGauge, 0, 3);
		// sonar/pir tilt
		final Label sonarPirTiltHeader = createLabel(KEY.SONAR_PIR_TILT);
		grid.add(sonarPirTiltHeader, 1, 2);
		final ImageView sonarPirTiltImgView = RS.imgView(sonarPirPanGauge.imageView.getImage(), false);
		sonarPirTiltImgView.setRotate(90d);
		final UGateGaugeBox<RemoteNode> sonarPirTiltGauge = new UGateGaugeBox<>(
				controlBar.getRemoteNodePA(),
				RemoteNodeType.SONAR_PIR_ANGLE_TILT, null, IndicatorType.KNOB,
				KNOB_SIZE_SCALE, 10d, 0, 0, 180d, 19, 0, FORMAT_ANGLE,
				sonarPirTiltImgView, GuiUtil.COLOR_MULTI);
		controlBar.addHelpTextTrigger(sonarPirTiltGauge, RS.rbLabel(KEY.SONAR_PIR_TILT_DESC));
		grid.add(sonarPirTiltGauge, 1, 3);
		// microwave pan
		final Label headerMW = createLabel(KEY.MW_PAN);
		grid.add(headerMW, 0, 4);
		final UGateGaugeBox<RemoteNode> mwPanGauge = new UGateGaugeBox<>(
				controlBar.getRemoteNodePA(), RemoteNodeType.MW_ANGLE_PAN,
				null, IndicatorType.KNOB, KNOB_SIZE_SCALE, 10d, 0, 0, 180d, 19,
				0, FORMAT_ANGLE, RS.IMG_PAN, GuiUtil.COLOR_MW);
		controlBar.addHelpTextTrigger(mwPanGauge, RS.rbLabel(KEY.MW_PAN_DESC));
		grid.add(mwPanGauge, 0, 5);
		// laser calibration
		final Label headerLaser = createLabel(KEY.LASER_CALIBRATION);
		grid.add(headerLaser, 1, 4);
	    final Button laserCalibrate = new Button(); //new Button(RS.rbLabel("laser.calibration"));
	    laserCalibrate.setGraphic(RS.imgView(RS.IMG_LASER_CALIBRATE));
	    laserCalibrate.setMaxWidth(125d);
	    laserCalibrate.setWrapText(true);
	    laserCalibrate.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(final MouseEvent event) {
				if (GuiUtil.isPrimaryPress(event)) {
					controlBar.createCommandService(Command.SERVO_LASER_CALIBRATE, true);
				}
			}
		});
		controlBar.addHelpTextTrigger(laserCalibrate, RS.rbLabel(KEY.LASER_CALIBRATION_DESC));
		grid.add(laserCalibrate, 1, 5);
		
		final Parent camCell = createCell(grid);
		add(camCell, 0, 0);
	}
	
	protected void addColumn2() {
		final GridPane grid = new GridPane();
		//####### Sonar #######
		sonarAnglePriority = new UGateCtrlBox<>(controlBar.getRemoteNodePA(),
				RemoteNodeType.CAM_SONAR_TRIP_ANGLE_PRIORITY, PRIORITY_FORMAT,
				GuiUtil.COLOR_SONAR, MIN_SENSOR_INDEX, MAX_SENSOR_INDEX,
				RS.rbLabel(KEY.CAM_SONAR_TRIP_ANGLE_PRIORITY), null);
		controlBar.addHelpTextTrigger(sonarAnglePriority, RS.rbLabel(KEY.CAM_TRIP_ANGLE_PRIORITY_DESC,
				RS.rbLabel(KEY.CAM_SONAR_TRIP_ANGLE_PRIORITY)));
		addPriorityValueListener(sonarAnglePriority);
		grid.add(sonarAnglePriority, 0, 0, 2, 1);
		// cam sonar pan
		final Label camSonarPanHeader = createLabel(KEY.CAM_PAN_SONAR);
		grid.add(camSonarPanHeader, 0, 1);
		final UGateGaugeBox<RemoteNode> camSonarPanGauge = new UGateGaugeBox<>(
				controlBar.getRemoteNodePA(),
				RemoteNodeType.CAM_SONAR_TRIP_ANGLE_PAN, null,
				IndicatorType.KNOB, KNOB_SIZE_SCALE, 10.7d, 0, 0, 180d, 18, 0,
				FORMAT_ANGLE, RS.IMG_PAN, GuiUtil.COLOR_SONAR);
		controlBar.addHelpTextTrigger(camSonarPanGauge, RS.rbLabel(KEY.CAM_PAN_SONAR_DESC));
		grid.add(camSonarPanGauge, 0, 2);
		// cam sonar tilt
		final ImageView camSonarTiltImgView = RS.imgView(camSonarPanGauge.imageView.getImage(), false);
		camSonarTiltImgView.setRotate(90d);
		final Label camSonarTiltHeader = createLabel(KEY.CAM_TILT_SONAR);
		grid.add(camSonarTiltHeader, 1, 1);
		final UGateGaugeBox<RemoteNode> camSonarTiltGauge = new UGateGaugeBox<>(
				controlBar.getRemoteNodePA(),
				RemoteNodeType.CAM_SONAR_TRIP_ANGLE_TILT, null,
				IndicatorType.KNOB, KNOB_SIZE_SCALE, 10.7d, 0, 0, 180d, 18, 0,
				FORMAT_ANGLE, camSonarTiltImgView, GuiUtil.COLOR_SONAR);
		controlBar.addHelpTextTrigger(camSonarTiltGauge, RS.rbLabel(KEY.CAM_TILT_SONAR_DESC));
		grid.add(camSonarTiltGauge, 1, 2);
		//####### PIR #######
		pirAnglePriority = new UGateCtrlBox<>(controlBar.getRemoteNodePA(),
				RemoteNodeType.CAM_PIR_TRIP_ANGLE_PRIORITY, PRIORITY_FORMAT,
				GuiUtil.COLOR_PIR, MIN_SENSOR_INDEX, MAX_SENSOR_INDEX,
				RS.rbLabel(KEY.CAM_PIR_TRIP_ANGLE_PRIORITY), null);
		controlBar.addHelpTextTrigger(pirAnglePriority, RS.rbLabel(KEY.CAM_TRIP_ANGLE_PRIORITY_DESC,
				RS.rbLabel(KEY.CAM_PIR_TRIP_ANGLE_PRIORITY)));
		addPriorityValueListener(pirAnglePriority);
		grid.add(pirAnglePriority, 0, 3, 2, 1);
		// cam PIR pan
		final Label pirPanHeader = createLabel(KEY.CAM_PAN_PIR);
		grid.add(pirPanHeader, 0, 4);
		final UGateGaugeBox<RemoteNode> pirPanGauge = new UGateGaugeBox<>(
				controlBar.getRemoteNodePA(),
				RemoteNodeType.CAM_PIR_TRIP_ANGLE_PAN, null,
				IndicatorType.KNOB, KNOB_SIZE_SCALE, 10.7d, 0, 0, 180d, 18, 0,
				FORMAT_ANGLE, RS.IMG_PAN, GuiUtil.COLOR_PIR);
		controlBar.addHelpTextTrigger(pirPanGauge, RS.rbLabel(KEY.CAM_PAN_PIR_DESC));
		grid.add(pirPanGauge, 0, 5);
		// cam PIR tilt
		final Label pirTiltHeader = createLabel(KEY.CAM_TILT_PIR);
		grid.add(pirTiltHeader, 1, 4);
		final UGateGaugeBox<RemoteNode> pirTiltGauge = new UGateGaugeBox<>(
				controlBar.getRemoteNodePA(),
				RemoteNodeType.CAM_PIR_TRIP_ANGLE_TILT, null,
				IndicatorType.KNOB, KNOB_SIZE_SCALE, 10.7d, 0, 0, 180d, 18, 0,
				FORMAT_ANGLE, RS.IMG_PAN, GuiUtil.COLOR_PIR);
		controlBar.addHelpTextTrigger(pirTiltGauge, RS.rbLabel(KEY.CAM_TILT_PIR_DESC));
		grid.add(pirTiltGauge, 1, 5);
		
		final Parent cell = createCell(grid);
		add(cell, 1, 0);
	}
	
	protected void addColumn3() {
		final GridPane grid = new GridPane();
		//####### Microwave #######
		mwAnglePriority = new UGateCtrlBox<>(controlBar.getRemoteNodePA(),
				RemoteNodeType.CAM_MW_TRIP_ANGLE_PRIORITY, PRIORITY_FORMAT,
				GuiUtil.COLOR_MW, MIN_SENSOR_INDEX, MAX_SENSOR_INDEX,
				RS.rbLabel(KEY.CAM_MW_TRIP_ANGLE_PRIORITY), null);
		controlBar.addHelpTextTrigger(mwAnglePriority, RS.rbLabel(KEY.CAM_TRIP_ANGLE_PRIORITY_DESC,
				RS.rbLabel(KEY.CAM_MW_TRIP_ANGLE_PRIORITY)));
		addPriorityValueListener(mwAnglePriority);
		grid.add(mwAnglePriority, 0, 0, 2, 1);
		// cam microwave pan
		final Label mwPanHeader = createLabel(KEY.CAM_PAN_MW);
		grid.add(mwPanHeader, 0, 1);
		final UGateGaugeBox<RemoteNode> mwPanGauge = new UGateGaugeBox<>(
				controlBar.getRemoteNodePA(),
				RemoteNodeType.CAM_MW_TRIP_ANGLE_PAN, null, IndicatorType.KNOB,
				KNOB_SIZE_SCALE, 10.7d, 0, 0, 180d, 18, 0, FORMAT_ANGLE,
				RS.IMG_PAN, GuiUtil.COLOR_MW);
		controlBar.addHelpTextTrigger(mwPanGauge, RS.rbLabel(KEY.CAM_PAN_MW_DESC));
		grid.add(mwPanGauge, 0, 2);
		// cam microwave tilt
		final Label mwTiltHeader = createLabel(KEY.CAM_TILT_MW);
		grid.add(mwTiltHeader, 1, 1);
		final UGateGaugeBox<RemoteNode> mwTiltGauge = new UGateGaugeBox<>(
				controlBar.getRemoteNodePA(),
				RemoteNodeType.CAM_MW_TRIP_ANGLE_TILT, null,
				IndicatorType.KNOB, KNOB_SIZE_SCALE, 10.7d, 0, 0, 180d, 18, 0,
				FORMAT_ANGLE, RS.IMG_PAN, GuiUtil.COLOR_MW);
		controlBar.addHelpTextTrigger(mwTiltGauge, RS.rbLabel(KEY.CAM_TILT_MW_DESC));
		grid.add(mwTiltGauge, 1, 2);
		//####### Laser #######
		laserAnglePriority = new UGateCtrlBox<>(controlBar.getRemoteNodePA(),
				RemoteNodeType.CAM_LASER_TRIP_ANGLE_PRIORITY, PRIORITY_FORMAT,
				GuiUtil.COLOR_LASER, MIN_SENSOR_INDEX, MAX_SENSOR_INDEX,
				RS.rbLabel(KEY.CAM_LASER_TRIP_ANGLE_PRIORITY), null);
		controlBar.addHelpTextTrigger(laserAnglePriority, RS.rbLabel(KEY.CAM_TRIP_ANGLE_PRIORITY_DESC,
				RS.rbLabel(KEY.CAM_LASER_TRIP_ANGLE_PRIORITY)));
		addPriorityValueListener(laserAnglePriority);
		grid.add(laserAnglePriority, 0, 3, 2, 1);
		// cam laser pan
		final Label laserPanHeader = createLabel(KEY.CAM_PAN_LASER);
		grid.add(laserPanHeader, 0, 4);
		final UGateGaugeBox<RemoteNode> laserPanGauge = new UGateGaugeBox<>(
				controlBar.getRemoteNodePA(),
				RemoteNodeType.CAM_LASER_TRIP_ANGLE_PAN, null,
				IndicatorType.KNOB, KNOB_SIZE_SCALE, 10.7d, 0, 0, 180d, 18, 0,
				FORMAT_ANGLE, RS.IMG_PAN, GuiUtil.COLOR_LASER);
		controlBar.addHelpTextTrigger(laserPanGauge, RS.rbLabel(KEY.CAM_PAN_LASER_DESC));
		grid.add(laserPanGauge, 0, 5);
		// cam laser tilt
		final Label laserTiltHeader = createLabel(KEY.CAM_TILT_LASER);
		grid.add(laserTiltHeader, 1, 4);
		final UGateGaugeBox<RemoteNode> laserTiltGauge = new UGateGaugeBox<>(
				controlBar.getRemoteNodePA(),
				RemoteNodeType.CAM_LASER_TRIP_ANGLE_TILT, null,
				IndicatorType.KNOB, KNOB_SIZE_SCALE, 10.7d, 0, 0, 180d, 18, 0,
				FORMAT_ANGLE, RS.IMG_PAN, GuiUtil.COLOR_LASER);
		controlBar.addHelpTextTrigger(laserTiltGauge, RS.rbLabel(KEY.CAM_TILT_LASER_DESC));
		grid.add(laserTiltGauge, 1, 5);
		
		final Parent cell = createCell(grid);
		add(cell, 2, 0);
	}
	
	/**
	 * Adds a priority listener that will ensure the priorities are unique and
	 * in sequence by setting the old value of the changing priority control on
	 * the priority control that has the changing priority controls new value.
	 * 
	 * @param control
	 *            the control to listen for priority number changes
	 */
	protected void addPriorityValueListener(
			final UGateCtrlBox<RemoteNode, Model, Void> control) {
		control.valueProperty().addListener(new ChangeListener<Object>() {
			@Override
			public void changed(final ObservableValue<? extends Object> observable, 
					final Object oldValue, final Object newValue) {
				if (NUMERIC_STEPPER_CHANGING.get()) {
					// should be called immediately after the swap is made
					NUMERIC_STEPPER_CHANGING.set(false);
					return;
				}
				// swap old value 
				final List<UGateCtrlBox<RemoteNode, Model, Void>> a = Arrays
						.asList(sonarAnglePriority, pirAnglePriority,
								mwAnglePriority, laserAnglePriority);
		        final List<Integer> b = Arrays.asList(control == sonarAnglePriority ? null : 
		        	(Integer) sonarAnglePriority.getValue(),
		        		control == pirAnglePriority ? null : (Integer) pirAnglePriority.getValue(),
		        				control == mwAnglePriority ? null : (Integer) mwAnglePriority.getValue(),
		        						control == laserAnglePriority ? null : (Integer) laserAnglePriority.getValue());
		        NUMERIC_STEPPER_CHANGING.set(true);
		        a.get(b.indexOf(Integer.valueOf(newValue.toString()))).setValue(oldValue);
			}
		});
	}
}
