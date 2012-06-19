package org.ugate.gui;

import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import org.ugate.RemoteSettings;
import org.ugate.UGateKeeper;
import org.ugate.gui.components.Gauge.IndicatorType;
import org.ugate.gui.components.UGateGaugePreferenceView;
import org.ugate.gui.components.UGateToggleSwitchView;
import org.ugate.resources.RS;
import org.ugate.service.RemoteNodeType;
import org.ugate.service.entity.jpa.RemoteNode;

/**
 * Sensor/Gate control view
 */
public class AlarmSettings extends ControlPane {

	private UGateToggleSwitchView<RemoteNode> soundsToggleSwitch;
	private UGateToggleSwitchView<RemoteNode> emailToggleSwitch;
	private UGateToggleSwitchView<RemoteNode> imgResToggleSwitch;
	
	/**
	 * Constructor
	 * 
	 * @param controlBar the control bar
	 */
	public AlarmSettings(final ControlBar controlBar) {
		super(controlBar);
		addOptionChildren();
		addSettingsChildren();
	}
	
	protected void addOptionChildren() {
		final Label soundLabel = createLabel("service.command.sounds");
		final Label emailLabel = createLabel("mail.alarm.notify");
		final Label imgResLabel = createLabel("cam.resolution");

		soundsToggleSwitch = new UGateToggleSwitchView<>(
				controlBar.getRemoteNodePA(), RemoteNodeType.DEVICE_SOUNDS_ON,
				RS.IMG_SOUND_ON, RS.IMG_SOUND_OFF);
		controlBar.addHelpTextTrigger(soundsToggleSwitch, RS.rbLabel("service.command.sounds.toggle"));
		emailToggleSwitch = new UGateToggleSwitchView<>(
				controlBar.getRemoteNodePA(), RemoteNodeType.MAIL_ALERT_ON,
				RS.IMG_EMAIL_NOTIFY_ON, RS.IMG_EMAIL_NOTIFY_OFF);
		controlBar.addHelpTextTrigger(emailToggleSwitch, RS.rbLabel("mail.alarm.notify.desc"));
		imgResToggleSwitch = new UGateToggleSwitchView<>(
				controlBar.getRemoteNodePA(), RemoteNodeType.CAM_RESOLUTION,
				RS.IMG_CAM_TOGGLE_VGA, RS.IMG_CAM_TOGGLE_QVGA,
				RS.rbLabel("cam.resolution.vga"),
				RS.rbLabel("cam.resolution.qvga"));
		imgResToggleSwitch.getToggleItem().toggleSwitchImageView.setEffect(new DropShadow());
		controlBar.addHelpTextTrigger(imgResToggleSwitch, RS.rbLabel("cam.resolution.desc"));
	   
		final Group generalCell = createCell(false, true, soundLabel, soundsToggleSwitch, emailLabel, emailToggleSwitch,
				imgResLabel, imgResToggleSwitch);
		add(generalCell, 0, 0);
	}
	
	protected void addSettingsChildren() {
		final GridPane grid = new GridPane();
		
		// thresholds
		final GridPane tgrid = new GridPane();
		
		final Label sonarThresholdLabel = createLabel("sonar.threshold");
		tgrid.add(sonarThresholdLabel, 0, 0);
		final UGateGaugePreferenceView sonarTripGauge = new UGateGaugePreferenceView(
				RemoteSettings.SONAR_DISTANCE_THRES_FEET, RemoteSettings.SONAR_DISTANCE_THRES_INCHES, 
				UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex(), 
				IndicatorType.NEEDLE, THRESHOLD_SIZE_SCALE,
				1d, 2, 0, 180d, 9, 4, FORMAT_SONAR, RS.IMG_RULER, COLOR_SONAR);
		sonarTripGauge.gauge.tickMarkLabelFillProperty.set(Color.WHITE);
		controlBar.addHelpTextTrigger(sonarTripGauge, RS.rbLabel("sonar.threshold.desc"));
		sonarTripGauge.gauge.setIntensity(80d, 15d, 5d);
		tgrid.add(sonarTripGauge, 0, 1);
		
		final Label mwThreshold = createLabel("microwave.threshold");
		tgrid.add(mwThreshold, 2, 0);
		final UGateGaugePreferenceView mwTripGauge = new UGateGaugePreferenceView(
				RemoteSettings.MW_SPEED_THRES_CYCLES_PER_SEC, null, UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex(), 
				IndicatorType.NEEDLE, THRESHOLD_SIZE_SCALE,
				1d, 2, 0, 180d, 50, 0, FORMAT_MW, RS.IMG_SPEEDOMETER, COLOR_MW);
		controlBar.addHelpTextTrigger(mwTripGauge, RS.rbLabel("microwave.threshold.desc"));
		tgrid.add(mwTripGauge, 2, 1);
		
		final Label laserThreshold = createLabel("laser.threshold");
		tgrid.add(laserThreshold, 3, 0);
		final UGateGaugePreferenceView laserTripGauge = new UGateGaugePreferenceView(
				RemoteSettings.LASER_DISTANCE_THRES_FEET, RemoteSettings.LASER_DISTANCE_THRES_INCHES, 
				UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex(), 
				IndicatorType.NEEDLE, THRESHOLD_SIZE_SCALE,
				4d, 0, 0d, 180d, 9, 3, FORMAT_LASER, RS.IMG_RULER, COLOR_LASER);
		laserTripGauge.gauge.tickMarkLabelFillProperty.set(Color.WHITE);
		controlBar.addHelpTextTrigger(laserTripGauge, RS.rbLabel("laser.threshold.desc"));
		laserTripGauge.gauge.setIntensity(100d, 0d, 0d);
		tgrid.add(laserTripGauge, 3, 1);
		
		// delays
		final GridPane dgrid = new GridPane();
		
		final Label sonarDelayLabel = createLabel("sonar.alarm.delay");
		dgrid.add(sonarDelayLabel, 0, 0);
		final UGateGaugePreferenceView sonarTripRateGauge = new UGateGaugePreferenceView(
				RemoteSettings.SONAR_DELAY_BTWN_TRIPS, null, UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex(), 
				IndicatorType.NEEDLE, DELAY_SIZE_SCALE,
				1d, 0, 0, 180d, 61, 0, FORMAT_DELAY, RS.IMG_STOPWATCH, COLOR_SONAR);
		controlBar.addHelpTextTrigger(sonarTripRateGauge, RS.rbLabel("sonar.alarm.delay.desc"));
		sonarTripRateGauge.gauge.setIntensity(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);
		dgrid.add(sonarTripRateGauge, 0, 1);
		
		final Label pirDelay = createLabel("pir.alarm.delay");
		dgrid.add(pirDelay, 1, 0);
		final UGateGaugePreferenceView pirTripRateGauge = new UGateGaugePreferenceView(
				RemoteSettings.PIR_DELAY_BTWN_TRIPS, null, UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex(), 
				IndicatorType.NEEDLE, DELAY_SIZE_SCALE,
				1d, 0, 0, 180d, 61, 0, FORMAT_DELAY, RS.IMG_STOPWATCH, COLOR_PIR);
		controlBar.addHelpTextTrigger(pirTripRateGauge, RS.rbLabel("pir.alarm.delay.desc"));
		pirTripRateGauge.gauge.setIntensity(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);
		dgrid.add(pirTripRateGauge, 1, 1);
		
		final Label mwDelay = createLabel("microwave.alarm.delay");
		dgrid.add(mwDelay, 2, 0);
		final UGateGaugePreferenceView mwTripRateGauge = new UGateGaugePreferenceView(
				RemoteSettings.MW_DELAY_BTWN_TRIPS, null, UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex(), 
				IndicatorType.NEEDLE, DELAY_SIZE_SCALE,
				1d, 0, 0, 180d, 61, 0, FORMAT_DELAY, RS.IMG_STOPWATCH, COLOR_MW);
		controlBar.addHelpTextTrigger(mwTripRateGauge, RS.rbLabel("microwave.alarm.delay.desc"));
		mwTripRateGauge.gauge.setIntensity(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);
		dgrid.add(mwTripRateGauge, 2, 1);
		
		final Label laserDelay = createLabel("laser.alarm.delay");
		dgrid.add(laserDelay, 3, 0);
		final UGateGaugePreferenceView laserTripRateGauge = new UGateGaugePreferenceView(
				RemoteSettings.LASER_DELAY_BTWN_TRIPS, null, UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex(), 
				IndicatorType.NEEDLE, DELAY_SIZE_SCALE,
				1d, 0, 0, 180d, 61, 0, FORMAT_DELAY, RS.IMG_STOPWATCH, COLOR_LASER);
		controlBar.addHelpTextTrigger(laserTripRateGauge, RS.rbLabel("laser.alarm.delay.desc"));
		laserTripRateGauge.gauge.setIntensity(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);
		dgrid.add(laserTripRateGauge, 3, 1);
		
		grid.add(tgrid, 0, 0);
		grid.add(dgrid, 0, 1);
		final Group camCell = createCell(false, true, grid);
		add(camCell, 1, 0);
	}
}
