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
import org.ugate.gui.components.UGateToggleSwitchPreferenceView;
import org.ugate.resources.RS;

/**
 * Sensor/Gate control view
 */
public class AlarmSettings extends ControlPane {

	private UGateToggleSwitchPreferenceView soundsToggleSwitch;
	private UGateToggleSwitchPreferenceView emailToggleSwitch;
	private UGateToggleSwitchPreferenceView imgResToggleSwitch;
	
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

		soundsToggleSwitch = new UGateToggleSwitchPreferenceView(
				RemoteSettings.SOUNDS_ON, UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex(), 
				RS.IMG_SOUND_ON, RS.IMG_SOUND_OFF);
		controlBar.addHelpTextTrigger(soundsToggleSwitch, RS.rbLabel("service.command.sounds.toggle"));
		emailToggleSwitch = new UGateToggleSwitchPreferenceView(RemoteSettings.MAIL_ALARM_ON, 
				UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex(), 
				RS.IMG_EMAIL_NOTIFY_ON, RS.IMG_EMAIL_NOTIFY_OFF);
		controlBar.addHelpTextTrigger(emailToggleSwitch, RS.rbLabel("mail.alarm.notify.desc"));
		imgResToggleSwitch = new UGateToggleSwitchPreferenceView(
				RemoteSettings.CAM_RES, UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex(), 
				RS.IMG_CAM_TOGGLE_VGA, RS.IMG_CAM_TOGGLE_QVGA, 
				RS.rbLabel("cam.resolution.vga"), RS.rbLabel("cam.resolution.qvga"));
		imgResToggleSwitch.getToggleItem().toggleSwitchImageView.setEffect(new DropShadow());
		controlBar.addHelpTextTrigger(imgResToggleSwitch, RS.rbLabel("cam.resolution.desc"));
	   
		final Group generalCell = createCell(false, true, soundLabel, soundsToggleSwitch, emailLabel, emailToggleSwitch,
				imgResLabel, imgResToggleSwitch);
		add(generalCell, 0, 0);
	}
	
	protected void addSettingsChildren() {
		final GridPane grid = new GridPane();
		
		final Label sonarThresholdLabel = createLabel("sonar.threshold");
		grid.add(sonarThresholdLabel, 0, 0);
		final UGateGaugePreferenceView sonarTripGauge = new UGateGaugePreferenceView(
				RemoteSettings.SONAR_DISTANCE_THRES_FEET, RemoteSettings.SONAR_DISTANCE_THRES_INCHES, 
				UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex(), 
				IndicatorType.NEEDLE, NEEDLE_SIZE_SCALE,
				1d, 2, 0, 180d, 9, 4, FORMAT_SONAR, RS.IMG_RULER, COLOR_SONAR);
		sonarTripGauge.gauge.tickMarkLabelFillProperty.set(Color.WHITE);
		controlBar.addHelpTextTrigger(sonarTripGauge, RS.rbLabel("sonar.threshold.desc"));
		sonarTripGauge.gauge.setIntensity(80d, 15d, 5d);
		grid.add(sonarTripGauge, 0, 1);
		
		final Label sonarDelayLabel = createLabel("sonar.alarm.delay");
		grid.add(sonarDelayLabel, 0, 2);
		final UGateGaugePreferenceView sonarTripRateGauge = new UGateGaugePreferenceView(
				RemoteSettings.SONAR_DELAY_BTWN_TRIPS, null, UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex(), 
				IndicatorType.NEEDLE, NEEDLE_SIZE_SCALE,
				1d, 0, 0, 180d, 61, 0, FORMAT_DELAY, RS.IMG_STOPWATCH, COLOR_SONAR);
		controlBar.addHelpTextTrigger(sonarTripRateGauge, RS.rbLabel("sonar.alarm.delay.desc"));
		sonarTripRateGauge.gauge.setIntensity(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);
		grid.add(sonarTripRateGauge, 0, 3);
		
		final Label pirDelay = createLabel("pir.alarm.delay");
		grid.add(pirDelay, 1, 2);
		final UGateGaugePreferenceView pirTripRateGauge = new UGateGaugePreferenceView(
				RemoteSettings.PIR_DELAY_BTWN_TRIPS, null, UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex(), 
				IndicatorType.NEEDLE, NEEDLE_SIZE_SCALE,
				1d, 0, 0, 180d, 61, 0, FORMAT_DELAY, RS.IMG_STOPWATCH, COLOR_PIR);
		controlBar.addHelpTextTrigger(pirTripRateGauge, RS.rbLabel("pir.alarm.delay.desc"));
		pirTripRateGauge.gauge.setIntensity(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);
		grid.add(pirTripRateGauge, 1, 3);

		final Label mwThreshold = createLabel("microwave.threshold");
		grid.add(mwThreshold, 2, 0);
		final UGateGaugePreferenceView mwTripGauge = new UGateGaugePreferenceView(
				RemoteSettings.MW_SPEED_THRES_CYCLES_PER_SEC, null, UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex(), 
				IndicatorType.NEEDLE, NEEDLE_SIZE_SCALE,
				1d, 2, 0, 180d, 50, 0, FORMAT_MW, RS.IMG_SPEEDOMETER, COLOR_MW);
		controlBar.addHelpTextTrigger(mwTripGauge, RS.rbLabel("microwave.threshold.desc"));
		grid.add(mwTripGauge, 2, 1);
		
		final Label mwDelay = createLabel("microwave.alarm.delay");
		grid.add(mwDelay, 2, 2);
		final UGateGaugePreferenceView mwTripRateGauge = new UGateGaugePreferenceView(
				RemoteSettings.MW_DELAY_BTWN_TRIPS, null, UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex(), 
				IndicatorType.NEEDLE, NEEDLE_SIZE_SCALE,
				1d, 0, 0, 180d, 61, 0, FORMAT_DELAY, RS.IMG_STOPWATCH, COLOR_MW);
		controlBar.addHelpTextTrigger(mwTripRateGauge, RS.rbLabel("microwave.alarm.delay.desc"));
		mwTripRateGauge.gauge.setIntensity(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);
		grid.add(mwTripRateGauge, 2, 3);
		
		final Label laserThreshold = createLabel("laser.threshold");
		grid.add(laserThreshold, 3, 0);
		final UGateGaugePreferenceView laserTripGauge = new UGateGaugePreferenceView(
				RemoteSettings.LASER_DISTANCE_THRES_FEET, RemoteSettings.LASER_DISTANCE_THRES_INCHES, 
				UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex(), 
				IndicatorType.NEEDLE, NEEDLE_SIZE_SCALE,
				2d, 0, 0d, 180d, 11, 4, FORMAT_LASER, RS.IMG_RULER, COLOR_LASER);
		laserTripGauge.gauge.tickMarkLabelFillProperty.set(Color.WHITE);
		controlBar.addHelpTextTrigger(laserTripGauge, RS.rbLabel("laser.threshold.desc"));
		sonarTripGauge.gauge.setIntensity(80d, 15d, 5d);
		grid.add(laserTripGauge, 3, 1);
		
		final Label laserDelay = createLabel("laser.alarm.delay");
		grid.add(laserDelay, 3, 2);
		final UGateGaugePreferenceView laserTripRateGauge = new UGateGaugePreferenceView(
				RemoteSettings.LASER_DELAY_BTWN_TRIPS, null, UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex(), 
				IndicatorType.NEEDLE, NEEDLE_SIZE_SCALE,
				1d, 0, 0, 180d, 61, 0, FORMAT_DELAY, RS.IMG_STOPWATCH, COLOR_LASER);
		controlBar.addHelpTextTrigger(laserTripRateGauge, RS.rbLabel("laser.alarm.delay.desc"));
		laserTripRateGauge.gauge.setIntensity(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);
		grid.add(laserTripRateGauge, 3, 3);
		
		final Group camCell = createCell(false, true, grid);
		add(camCell, 1, 0);
	}
}
