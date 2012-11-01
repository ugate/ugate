package org.ugate.gui.view;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import org.ugate.gui.ControlBar;
import org.ugate.gui.ControlPane;
import org.ugate.gui.GuiUtil;
import org.ugate.gui.components.Gauge.IndicatorType;
import org.ugate.gui.components.UGateGaugeBox;
import org.ugate.resources.RS;
import org.ugate.resources.RS.KEY;
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

		final String feet = RS.rbLabel(KEY.FEET);
		final Label sonarThresholdLabel = createLabel(KEY.SONAR_THRESHOLD, feet);
		final UGateGaugeBox<RemoteNode> sonarTripGauge = new UGateGaugeBox<>(
				controlBar.getRemoteNodePA(),
				RemoteNodeType.SONAR_DISTANCE_THRES_FEET,
				RemoteNodeType.SONAR_DISTANCE_THRES_INCHES,
				IndicatorType.NEEDLE, THRESHOLD_SIZE_SCALE, 1d, 
				RemoteNodeType.SONAR_DISTANCE_THRES_FEET.getMin().intValue(), 0d, 180d, 
				RemoteNodeType.SONAR_DISTANCE_THRES_FEET.getMax().intValue() - 1,
				3, FORMAT_SONAR, RS.IMG_RULER, GuiUtil.COLOR_SONAR);
		//sonarTripGauge.gauge.tickMarkLabelFillProperty.set(Color.WHITE);
		controlBar.addHelpTextTrigger(sonarTripGauge, RS.rbLabel(KEY.SONAR_THRESHOLD_DESC, feet));
		sonarTripGauge.gauge.setIntensity(80d, 15d, 5d);
		sonarTripGauge.setMaxWidth(200d);
		final Parent sonarCell = createCell(sonarThresholdLabel, sonarTripGauge);
		
		add(sonarCell, ci, ri);

		final Label mwThreshold = createLabel(KEY.MW_THRESHOLD, feet);
		final UGateGaugeBox<RemoteNode> mwTripGauge = new UGateGaugeBox<>(
				controlBar.getRemoteNodePA(),
				RemoteNodeType.MW_SPEED_THRES_CYCLES_PER_SEC, null,
				IndicatorType.NEEDLE, THRESHOLD_SIZE_SCALE, 1d, 
				RemoteNodeType.MW_SPEED_THRES_CYCLES_PER_SEC.getMin().intValue(), 0d, 180d, 
				RemoteNodeType.MW_SPEED_THRES_CYCLES_PER_SEC.getMax().intValue() - 1,
				0, FORMAT_MW, RS.IMG_SPEEDOMETER, GuiUtil.COLOR_MW);
		controlBar.addHelpTextTrigger(mwTripGauge, RS.rbLabel(KEY.MW_THRESHOLD_DESC, feet));
		final Parent mwCell = createCell(mwThreshold, mwTripGauge);
		add(mwCell, ++ci, ri);

		final Label laserThreshold = createLabel(KEY.LASER_THRESHOLD, RS.rbLabel(KEY.FEET), feet);
		final UGateGaugeBox<RemoteNode> laserTripGauge = new UGateGaugeBox<>(
				controlBar.getRemoteNodePA(),
				RemoteNodeType.LASER_DISTANCE_THRES_FEET,
				RemoteNodeType.LASER_DISTANCE_THRES_INCHES,
				IndicatorType.NEEDLE, THRESHOLD_SIZE_SCALE, 4d, 
				RemoteNodeType.LASER_DISTANCE_THRES_FEET.getMin().intValue(), 0d, 180d, 
				RemoteNodeType.LASER_DISTANCE_THRES_FEET.getMax().intValue(),
				3, FORMAT_LASER, RS.IMG_RULER, GuiUtil.COLOR_LASER);
		//laserTripGauge.gauge.tickMarkLabelFillProperty.set(Color.WHITE);
		controlBar.addHelpTextTrigger(laserTripGauge, RS.rbLabel(KEY.LASER_THRESHOLD_DESC, feet));
		laserTripGauge.gauge.setIntensity(100d, 0d, 0d);
		final Parent laserCell = createCell(laserThreshold, laserTripGauge);
		add(laserCell, ++ci, ri);
	}

	protected void addDelayChildren(final int columnIndex, final int rowIndex) {
		final VBox sp = new VBox();
		final Label sonarDelayLabel = createLabel(KEY.SONAR_ALARM_DELAY);
		final UGateGaugeBox<RemoteNode> sonarTripRateGauge = new UGateGaugeBox<>(
				controlBar.getRemoteNodePA(),
				RemoteNodeType.SONAR_DELAY_BTWN_TRIPS, null,
				IndicatorType.NEEDLE, DELAY_SIZE_SCALE, 1d, 
				RemoteNodeType.SONAR_DELAY_BTWN_TRIPS.getMin().intValue(), 0d, 180d, 
				RemoteNodeType.SONAR_DELAY_BTWN_TRIPS.getMax().intValue() + 1, 0,
				FORMAT_DELAY, RS.IMG_STOPWATCH, GuiUtil.COLOR_SONAR);
		controlBar.addHelpTextTrigger(sonarTripRateGauge, RS.rbLabel(KEY.SONAR_ALARM_DELAY_DESC));
		sonarTripRateGauge.gauge.setIntensity(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);
		sp.getChildren().addAll(sonarDelayLabel, sonarTripRateGauge);

		final VBox pp = new VBox();
		final Label pirDelay = createLabel(KEY.PIR_ALARM_DELAY);
		final UGateGaugeBox<RemoteNode> pirTripRateGauge = new UGateGaugeBox<>(
				controlBar.getRemoteNodePA(),
				RemoteNodeType.PIR_DELAY_BTWN_TRIPS, null,
				IndicatorType.NEEDLE, DELAY_SIZE_SCALE, 1d, 
				RemoteNodeType.PIR_DELAY_BTWN_TRIPS.getMin().intValue(), 0d, 180d, 
				RemoteNodeType.PIR_DELAY_BTWN_TRIPS.getMax().intValue() + 1, 0,
				FORMAT_DELAY, RS.IMG_STOPWATCH, GuiUtil.COLOR_PIR);
		controlBar.addHelpTextTrigger(pirTripRateGauge, RS.rbLabel(KEY.PIR_ALARM_DELAY_DESC));
		pirTripRateGauge.gauge.setIntensity(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);
		pp.getChildren().addAll(pirDelay, pirTripRateGauge);

		final VBox mp = new VBox();
		final Label mwDelay = createLabel(KEY.MW_ALARM_DELAY);
		final UGateGaugeBox<RemoteNode> mwTripRateGauge = new UGateGaugeBox<>(
				controlBar.getRemoteNodePA(),
				RemoteNodeType.MW_DELAY_BTWN_TRIPS, null, IndicatorType.NEEDLE,
				DELAY_SIZE_SCALE, 1d, 
				RemoteNodeType.MW_DELAY_BTWN_TRIPS.getMin().intValue(), 0d, 180d, 
				RemoteNodeType.MW_DELAY_BTWN_TRIPS.getMax().intValue() + 1, 0, FORMAT_DELAY,
				RS.IMG_STOPWATCH, GuiUtil.COLOR_MW);
		controlBar.addHelpTextTrigger(mwTripRateGauge, RS.rbLabel(KEY.MW_ALARM_DELAY_DESC));
		mwTripRateGauge.gauge.setIntensity(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);
		mp.getChildren().addAll(mwDelay, mwTripRateGauge);

		final VBox lp = new VBox();
		final Label laserDelay = createLabel(KEY.LASER_ALARM_DELAY);
		final UGateGaugeBox<RemoteNode> laserTripRateGauge = new UGateGaugeBox<>(
				controlBar.getRemoteNodePA(),
				RemoteNodeType.LASER_DELAY_BTWN_TRIPS, null,
				IndicatorType.NEEDLE, DELAY_SIZE_SCALE, 1d, 
				RemoteNodeType.LASER_DELAY_BTWN_TRIPS.getMin().intValue(), 0d, 180d, 
				RemoteNodeType.LASER_DELAY_BTWN_TRIPS.getMax().intValue() + 1, 0,
				FORMAT_DELAY, RS.IMG_STOPWATCH, GuiUtil.COLOR_LASER);
		controlBar.addHelpTextTrigger(laserTripRateGauge, RS.rbLabel(KEY.LASER_ALARM_DELAY_DESC));
		laserTripRateGauge.gauge.setIntensity(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);
		lp.getChildren().addAll(laserDelay, laserTripRateGauge);
		
		final HBox p = new HBox(CHILD_PADDING);
		p.setAlignment(Pos.CENTER);
		p.getChildren().addAll(sp, pp, mp, lp);
		final Parent cell = createCell(p);
		add(cell, columnIndex, rowIndex, 3, 1);
	}
}
