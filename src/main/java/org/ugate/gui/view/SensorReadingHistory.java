package org.ugate.gui.view;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ugate.UGateUtil;
import org.ugate.gui.ControlBar;
import org.ugate.gui.components.SimpleCalendar;
import org.ugate.resources.RS;
import org.ugate.resources.RS.KEYS;
import org.ugate.service.ServiceProvider;
import org.ugate.service.entity.jpa.RemoteNodeReading;

/**
 * {@linkplain RemoteNodeReading} history
 */
public class SensorReadingHistory extends StackPane {
	
	private static final Logger log = LoggerFactory.getLogger(SensorReadingHistory.class);
	private final ControlBar cb;
	private final XYChart.Series<String, Number> laserSeries;
	private final XYChart.Series<String, Number> sonarSeries;
	private final XYChart.Series<String, Number> microwaveSeries;
	private final XYChart.Series<String, Number> pirSeries;
	private final XYChart.Series<String, Number> readTripsSeries;

	/**
	 * Constructor
	 * 
	 * @param controlBar
	 *            the {@linkplain ControlBar}
	 */
	public SensorReadingHistory(final ControlBar controlBar) {
		this.cb = controlBar;
		CategoryAxis xAxis = new CategoryAxis();
		NumberAxis yAxis = new NumberAxis();
		final LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
		chart.setTitle(RS.rbLabel(KEYS.LABEL_GRAPH_ALARM_NOTIFY));
		xAxis.setLabel(RS.rbLabel(KEYS.LABEL_GRAPH_AXIS_X));
		yAxis.setLabel(RS.rbLabel(KEYS.LABEL_GRAPH_AXIS_Y));
		laserSeries = new XYChart.Series<>();
		laserSeries.setName(RS.rbLabel(KEYS.LABEL_GRAPH_SERIES_ALARM_LASER));
		chart.getData().add(laserSeries);
		sonarSeries = new XYChart.Series<>();
		sonarSeries.setName(RS.rbLabel(KEYS.LABEL_GRAPH_SERIES_ALARM_SONAR));
		chart.getData().add(sonarSeries);
		microwaveSeries = new XYChart.Series<>();
		microwaveSeries.setName(RS.rbLabel(KEYS.LABEL_GRAPH_SERIES_ALARM_MICROWAVE));
		chart.getData().add(microwaveSeries);
		pirSeries = new XYChart.Series<>();
		pirSeries.setName(RS.rbLabel(KEYS.LABEL_GRAPH_SERIES_ALARM_PIR));
		chart.getData().add(pirSeries);
		readTripsSeries = new XYChart.Series<>();
		readTripsSeries.setName(RS.rbLabel(KEYS.LABEL_GRAPH_SERIES_ACTIVITY_READS));
		chart.getData().add(readTripsSeries);

		final SimpleCalendar simpleCalender = new SimpleCalendar();
		simpleCalender.setMaxSize(100d, 20d);
		final TextField dateField = new TextField(new SimpleDateFormat(
				"MM/dd/yyyy").format(simpleCalender.dateProperty().get()));
		dateField.setMaxSize(simpleCalender.getMaxWidth(), simpleCalender.getMaxHeight());
		dateField.setEditable(false);
		dateField.setDisable(true);
		simpleCalender.dateProperty().addListener(new ChangeListener<Date>() {
			@Override
			public void changed(final ObservableValue<? extends Date> ov,
					final Date oldDate, final Date newDate) {
				dateField.setText(new SimpleDateFormat("MM/dd/yyyy")
						.format(newDate));
				final Calendar cal = Calendar.getInstance();
				cal.setTime(newDate);
				updateAlarmSeries(cal);
			}
		});

		final HBox dateBox = new HBox();
		dateBox.setAlignment(Pos.BOTTOM_RIGHT);
		dateBox.getChildren().addAll(dateField, simpleCalender);

		setPadding(new Insets(10d));
		setAlignment(Pos.BOTTOM_RIGHT);
		getChildren().addAll(chart, dateBox);

		final Calendar cal = Calendar.getInstance();
		cal.setTime(simpleCalender.dateProperty().get());
		updateAlarmSeries(cal);
	}

	/**
	 * Updates the {@linkplain XYChart.Series} using
	 * {@linkplain RemoteNodeReading}(s) for a given {@linkplain Calendar}
	 * 
	 * @param cal
	 *            the {@linkplain Calendar} to get the
	 *            {@linkplain RemoteNodeReading}(s) for (will not use time)
	 */
	protected void updateAlarmSeries(final Calendar cal) {
		final List<RemoteNodeReading> rnrs = ServiceProvider.IMPL
				.getRemoteNodeService().findReadingsByDate(cb.getRemoteNode(),
						cal, true);
		// add zero plot for 24hr period for each series
		final Calendar c24 = Calendar.getInstance();
		c24.setTime(cal.getTime());
		laserSeries.getData().clear();
		sonarSeries.getData().clear();
		microwaveSeries.getData().clear();
		pirSeries.getData().clear();
		String time;
		for (final RemoteNodeReading rnr : rnrs) {
			c24.setTime(rnr.getReadDate());
			time = UGateUtil.calFormatTime(c24);
			switch (rnr.getFromMultiState()) {
			case 1:
				laserSeries.getData().add(new XYChart.Data<String, Number>(time, 1));
				break;
			case 2:
				microwaveSeries.getData().add(new XYChart.Data<String, Number>(time, 1));
				break;
			case 3:
				laserSeries.getData().add(new XYChart.Data<String, Number>(time, 2));
				microwaveSeries.getData().add(new XYChart.Data<String, Number>(time, 2));
				break;
			case 4:
				pirSeries.getData().add(new XYChart.Data<String, Number>(time, 1));
				break;
			case 5:
				laserSeries.getData().add(new XYChart.Data<String, Number>(time, 2));
				pirSeries.getData().add(new XYChart.Data<String, Number>(time, 2));
				break;
			case 6:
				microwaveSeries.getData().add(new XYChart.Data<String, Number>(time, 2));
				pirSeries.getData().add(new XYChart.Data<String, Number>(time, 2));
				break;
			case 7:
				laserSeries.getData().add(new XYChart.Data<String, Number>(time, 3));
				microwaveSeries.getData().add(new XYChart.Data<String, Number>(time, 3));
				pirSeries.getData().add(new XYChart.Data<String, Number>(time, 3));
				break;
			case 8:
				sonarSeries.getData().add(new XYChart.Data<String, Number>(time, 1));
				break;
			case 9:
				laserSeries.getData().add(new XYChart.Data<String, Number>(time, 2));
				sonarSeries.getData().add(new XYChart.Data<String, Number>(time, 2));
				break;
			case 10:
				microwaveSeries.getData().add(new XYChart.Data<String, Number>(time, 2));
				sonarSeries.getData().add(new XYChart.Data<String, Number>(time, 2));
				break;
			case 11:
				laserSeries.getData().add(new XYChart.Data<String, Number>(time, 3));
				microwaveSeries.getData().add(new XYChart.Data<String, Number>(time, 3));
				sonarSeries.getData().add(new XYChart.Data<String, Number>(time, 3));
				break;
			case 12:
				pirSeries.getData().add(new XYChart.Data<String, Number>(time, 2));
				sonarSeries.getData().add(new XYChart.Data<String, Number>(time, 2));
				break;
			case 13:
				laserSeries.getData().add(new XYChart.Data<String, Number>(time, 3));
				pirSeries.getData().add(new XYChart.Data<String, Number>(time, 3));
				sonarSeries.getData().add(new XYChart.Data<String, Number>(time, 3));
				break;
			case 14:
				microwaveSeries.getData().add(new XYChart.Data<String, Number>(time, 3));
				pirSeries.getData().add(new XYChart.Data<String, Number>(time, 3));
				sonarSeries.getData().add(new XYChart.Data<String, Number>(time, 3));
				break;
			case 15:
				laserSeries.getData().add(new XYChart.Data<String, Number>(time, 4));
				microwaveSeries.getData().add(new XYChart.Data<String, Number>(time, 4));
				pirSeries.getData().add(new XYChart.Data<String, Number>(time, 4));
				sonarSeries.getData().add(new XYChart.Data<String, Number>(time, 4));
				break;
			case 16:
				readTripsSeries.getData().add(new XYChart.Data<String, Number>(time, 1));
				break;
			default:
				log.warn(String.format(
						"%1$s for ID %2$s invalid for multi-state %3$s",
						RemoteNodeReading.class.getName(), rnr.getId(),
						rnr.getFromMultiState()));
			}
		}
	}
}
