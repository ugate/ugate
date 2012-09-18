package org.ugate.gui.view;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
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
	private static final Number DEFAULT_DATA_VALUE = 0;
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
		final BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
		chart.setTitle(RS.rbLabel(KEYS.LABEL_GRAPH_ALARM_NOTIFY));
		xAxis.setLabel(RS.rbLabel(KEYS.LABEL_GRAPH_AXIS_X));
		yAxis.setLabel(RS.rbLabel(KEYS.LABEL_GRAPH_AXIS_Y));
		laserSeries = new XYChart.Series<>();
		laserSeries.setName(RS.rbLabel(KEYS.LABEL_GRAPH_SERIES_ALARM_LASER));
		sonarSeries = new XYChart.Series<>();
		sonarSeries.setName(RS.rbLabel(KEYS.LABEL_GRAPH_SERIES_ALARM_SONAR));
		microwaveSeries = new XYChart.Series<>();
		microwaveSeries.setName(RS.rbLabel(KEYS.LABEL_GRAPH_SERIES_ALARM_MICROWAVE));
		pirSeries = new XYChart.Series<>();
		pirSeries.setName(RS.rbLabel(KEYS.LABEL_GRAPH_SERIES_ALARM_PIR));
		readTripsSeries = new XYChart.Series<>();
		readTripsSeries.setName(RS.rbLabel(KEYS.LABEL_GRAPH_SERIES_ACTIVITY_READS));

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
		
		chart.getData().add(laserSeries);
		chart.getData().add(sonarSeries);
		chart.getData().add(microwaveSeries);
		chart.getData().add(pirSeries);
		chart.getData().add(readTripsSeries);
	}

	/**
	 * Updates the {@linkplain XYChart.Series} using
	 * {@linkplain RemoteNodeReading}(s) for a given {@linkplain Calendar}
	 * 
	 * @param cal
	 *            the {@linkplain Calendar} to get the
	 *            {@linkplain RemoteNodeReading}(s) for (will not use time)
	 * @return the number of {@linkplain RemoteNodeReading}s
	 */
	protected int updateAlarmSeries(final Calendar cal) {
		final List<RemoteNodeReading> rnrs = ServiceProvider.IMPL
				.getRemoteNodeService().findReadingsByDate(cb.getRemoteNode(),
						cal, true);
		if (rnrs.size() <= 0) {
			laserSeries.getData().add(new XYChart.Data<>("", DEFAULT_DATA_VALUE));
			sonarSeries.getData().add(new XYChart.Data<>("", DEFAULT_DATA_VALUE));
			microwaveSeries.getData().add(new XYChart.Data<>("", DEFAULT_DATA_VALUE));
			pirSeries.getData().add(new XYChart.Data<>("", DEFAULT_DATA_VALUE));
			readTripsSeries.getData().add(new XYChart.Data<>("", DEFAULT_DATA_VALUE));
			return 0;
		}
		// add zero plot for 24hr period for each series
		laserSeries.getData().clear();
		sonarSeries.getData().clear();
		microwaveSeries.getData().clear();
		pirSeries.getData().clear();
		String time;
		Number l = 0, m = 0, p = 0, s = 0, r = 0;
		for (final RemoteNodeReading rnr : rnrs) {
			time = UGateUtil.dateFormatTime(rnr.getReadDate());
			switch (rnr.getFromMultiState()) {
			case 1:
				l = 1;
				break;
			case 2:
				m = 1;
				break;
			case 3:
				l = 2;
				m = 2;
				break;
			case 4:
				p = 1;
				break;
			case 5:
				l = 2;
				p = 2;
				break;
			case 6:
				m = 2;
				p = 2;
				break;
			case 7:
				l = 3;
				m = 3;
				p = 3;
				break;
			case 8:
				s = 1;
				break;
			case 9:
				l = 2;
				s = 2;
				break;
			case 10:
				m = 2;
				s = 2;
				break;
			case 11:
				l = 3;
				m = 3;
				s = 3;
				break;
			case 12:
				p = 2;
				s = 2;
				break;
			case 13:
				l = 3;
				p = 3;
				s = 3;
				break;
			case 14:
				m = 3;
				p = 3;
				s = 3;
				break;
			case 15:
				l = 4;
				m = 4;
				p = 4;
				s = 4;
				break;
			case 16:
				r = 1;
				break;
			default:
				log.warn(String.format(
						"%1$s for ID %2$s invalid for multi-state %3$s",
						RemoteNodeReading.class.getName(), rnr.getId(),
						rnr.getFromMultiState()));
			}
			laserSeries.getData().add(new XYChart.Data<>(time, l));
			microwaveSeries.getData().add(new XYChart.Data<>(time, m));
			pirSeries.getData().add(new XYChart.Data<>(time, p));
			sonarSeries.getData().add(new XYChart.Data<>(time, s));
			readTripsSeries.getData().add(new XYChart.Data<>(time, r));
		}
		return rnrs.size();
	}
}
