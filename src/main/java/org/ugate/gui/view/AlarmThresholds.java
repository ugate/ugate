package org.ugate.gui.view;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import org.ugate.gui.ControlBar;
import org.ugate.gui.ControlPane;
import org.ugate.gui.components.Gauge.IndicatorType;
import org.ugate.gui.components.UGateGaugeBox;
import org.ugate.resources.RS;
import org.ugate.resources.RS.KEYS;
import org.ugate.service.entity.RemoteNodeType;
import org.ugate.service.entity.jpa.RemoteNode;

/**
 * Sensor/Gate control view
 */
public class AlarmThresholds extends ControlPane {

	/**
	 * Constructor
	 * 
	 * @param controlBar the control bar
	 */
	public AlarmThresholds(final ControlBar controlBar) {
		super(controlBar);
		int ci = -1;
		int ri = -1;
		addThresholdChildren(++ci, ++ri);
		addDelayChildren(ci, ++ri);
	}

	protected void addThresholdChildren(final int columnIndex, final int rowIndex) {
		int ci = columnIndex;
		int ri = rowIndex;

		final Label sonarThresholdLabel = createLabel(KEYS.SONAR_THRESHOLD);
		final UGateGaugeBox<RemoteNode> sonarTripGauge = new UGateGaugeBox<>(
				controlBar.getRemoteNodePA(),
				RemoteNodeType.SONAR_DISTANCE_THRES_FEET,
				RemoteNodeType.SONAR_DISTANCE_THRES_INCHES,
				IndicatorType.NEEDLE, THRESHOLD_SIZE_SCALE, 1d, 2, 0, 180d, 9,
				4, FORMAT_SONAR, RS.IMG_RULER, COLOR_SONAR);
		sonarTripGauge.gauge.tickMarkLabelFillProperty.set(Color.WHITE);
		controlBar.addHelpTextTrigger(sonarTripGauge, RS.rbLabel(KEYS.SONAR_THRESHOLD_DESC));
		sonarTripGauge.gauge.setIntensity(80d, 15d, 5d);
		sonarTripGauge.setMaxWidth(200d);
		final Parent sonarCell = createCell(sonarThresholdLabel, sonarTripGauge);
		
		add(sonarCell, ci, ri);

		final Label mwThreshold = createLabel(KEYS.MW_THRESHOLD);
		final UGateGaugeBox<RemoteNode> mwTripGauge = new UGateGaugeBox<>(
				controlBar.getRemoteNodePA(),
				RemoteNodeType.MW_SPEED_THRES_CYCLES_PER_SEC, null,
				IndicatorType.NEEDLE, THRESHOLD_SIZE_SCALE, 1d, 2, 0, 180d, 50,
				0, FORMAT_MW, RS.IMG_SPEEDOMETER, COLOR_MW);
		controlBar.addHelpTextTrigger(mwTripGauge, RS.rbLabel(KEYS.MW_THRESHOLD_DESC));
		final Parent mwCell = createCell(mwThreshold, mwTripGauge);
		add(mwCell, ++ci, ri);

		final Label laserThreshold = createLabel(KEYS.LASER_THRESHOLD);
		final UGateGaugeBox<RemoteNode> laserTripGauge = new UGateGaugeBox<>(
				controlBar.getRemoteNodePA(),
				RemoteNodeType.LASER_DISTANCE_THRES_FEET,
				RemoteNodeType.LASER_DISTANCE_THRES_INCHES,
				IndicatorType.NEEDLE, THRESHOLD_SIZE_SCALE, 4d, 0, 0d, 180d, 9,
				3, FORMAT_LASER, RS.IMG_RULER, COLOR_LASER);
		laserTripGauge.gauge.tickMarkLabelFillProperty.set(Color.WHITE);
		controlBar.addHelpTextTrigger(laserTripGauge, RS.rbLabel(KEYS.LASER_THRESHOLD_DESC));
		laserTripGauge.gauge.setIntensity(100d, 0d, 0d);
		final Parent laserCell = createCell(laserThreshold, laserTripGauge);
		add(laserCell, ++ci, ri);
	}

	protected void addDelayChildren(final int columnIndex, final int rowIndex) {
		final VBox sp = new VBox();
		final Label sonarDelayLabel = createLabel(KEYS.SONAR_ALARM_DELAY);
		final UGateGaugeBox<RemoteNode> sonarTripRateGauge = new UGateGaugeBox<>(
				controlBar.getRemoteNodePA(),
				RemoteNodeType.SONAR_DELAY_BTWN_TRIPS, null,
				IndicatorType.NEEDLE, DELAY_SIZE_SCALE, 1d, 0, 0, 180d, 61, 0,
				FORMAT_DELAY, RS.IMG_STOPWATCH, COLOR_SONAR);
		controlBar.addHelpTextTrigger(sonarTripRateGauge, RS.rbLabel(KEYS.SONAR_ALARM_DELAY_DESC));
		sonarTripRateGauge.gauge.setIntensity(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);
		sp.getChildren().addAll(sonarDelayLabel, sonarTripRateGauge);

		final VBox pp = new VBox();
		final Label pirDelay = createLabel(KEYS.PIR_ALARM_DELAY);
		final UGateGaugeBox<RemoteNode> pirTripRateGauge = new UGateGaugeBox<>(
				controlBar.getRemoteNodePA(),
				RemoteNodeType.PIR_DELAY_BTWN_TRIPS, null,
				IndicatorType.NEEDLE, DELAY_SIZE_SCALE, 1d, 0, 0, 180d, 61, 0,
				FORMAT_DELAY, RS.IMG_STOPWATCH, COLOR_PIR);
		controlBar.addHelpTextTrigger(pirTripRateGauge, RS.rbLabel(KEYS.PIR_ALARM_DELAY_DESC));
		pirTripRateGauge.gauge.setIntensity(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);
		pp.getChildren().addAll(pirDelay, pirTripRateGauge);

		final VBox mp = new VBox();
		final Label mwDelay = createLabel(KEYS.MW_ALARM_DELAY);
		final UGateGaugeBox<RemoteNode> mwTripRateGauge = new UGateGaugeBox<>(
				controlBar.getRemoteNodePA(),
				RemoteNodeType.MW_DELAY_BTWN_TRIPS, null, IndicatorType.NEEDLE,
				DELAY_SIZE_SCALE, 1d, 0, 0, 180d, 61, 0, FORMAT_DELAY,
				RS.IMG_STOPWATCH, COLOR_MW);
		controlBar.addHelpTextTrigger(mwTripRateGauge, RS.rbLabel(KEYS.MW_ALARM_DELAY_DESC));
		mwTripRateGauge.gauge.setIntensity(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);
		mp.getChildren().addAll(mwDelay, mwTripRateGauge);

		final VBox lp = new VBox();
		final Label laserDelay = createLabel(KEYS.LASER_ALARM_DELAY);
		final UGateGaugeBox<RemoteNode> laserTripRateGauge = new UGateGaugeBox<>(
				controlBar.getRemoteNodePA(),
				RemoteNodeType.LASER_DELAY_BTWN_TRIPS, null,
				IndicatorType.NEEDLE, DELAY_SIZE_SCALE, 1d, 0, 0, 180d, 61, 0,
				FORMAT_DELAY, RS.IMG_STOPWATCH, COLOR_LASER);
		controlBar.addHelpTextTrigger(laserTripRateGauge, RS.rbLabel(KEYS.LASER_ALARM_DELAY_DESC));
		laserTripRateGauge.gauge.setIntensity(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);
		lp.getChildren().addAll(laserDelay, laserTripRateGauge);
		
		final HBox p = new HBox(CHILD_PADDING);
		p.setAlignment(Pos.CENTER);
		p.getChildren().addAll(sp, pp, mp, lp);
		final Parent cell = createCell(p);
		add(cell, columnIndex, rowIndex, 3, 1);
	}
}
