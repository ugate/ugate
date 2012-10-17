package org.ugate.gui.view;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ugate.UGateUtil;
import org.ugate.gui.ControlBar;
import org.ugate.gui.GuiUtil;
import org.ugate.gui.components.SimpleCalendar;
import org.ugate.resources.RS;
import org.ugate.resources.RS.KEY;
import org.ugate.service.ServiceProvider;
import org.ugate.service.entity.jpa.RemoteNodeReading;

/**
 * {@linkplain RemoteNodeReading} history
 */
public class SensorReadingHistory extends StackPane {

	private static final Logger log = LoggerFactory
			.getLogger(SensorReadingHistory.class);
	private final ControlBar cb;
	private final CategoryAxis xAxis = new CategoryAxis();
	private final NumberAxis yAxis = new NumberAxis();
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
		final StackedBarChart<String, Number> chart = new StackedBarChart<>(
				xAxis, yAxis);
		chart.setTitle(RS.rbLabel(KEY.LABEL_GRAPH_ALARM_NOTIFY));
		// xAxis.setLabel(RS.rbLabel(KEYS.LABEL_GRAPH_AXIS_X));
		xAxis.setCategories(FXCollections.<String> observableArrayList());
		yAxis.setLabel(RS.rbLabel(KEY.LABEL_GRAPH_AXIS_Y));
		yAxis.setMinorTickVisible(false);
		yAxis.setAutoRanging(false);
		yAxis.setUpperBound(16d);
		yAxis.setTickUnit(1d);
		sonarSeries = new XYChart.Series<>();
		pirSeries = new XYChart.Series<>();
		microwaveSeries = new XYChart.Series<>();
		laserSeries = new XYChart.Series<>();
		readTripsSeries = new XYChart.Series<>();

		final SimpleCalendar simpleCalender = new SimpleCalendar();
		simpleCalender.setMaxSize(100d, 20d);
		final TextField dateField = new TextField(new SimpleDateFormat(
				"MM/dd/yyyy").format(simpleCalender.dateProperty().get()));
		dateField.setMaxSize(simpleCalender.getMaxWidth(),
				simpleCalender.getMaxHeight());
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
				populateData(chart, cal);
			}
		});

		final HBox dateBox = new HBox();
		dateBox.setAlignment(Pos.BOTTOM_RIGHT);
		dateBox.getChildren().addAll(dateField, simpleCalender);

		setPadding(new Insets(10d));
		setAlignment(Pos.BOTTOM_RIGHT);
		getChildren().addAll(chart, dateBox);

		sonarSeries.setName(RS.rbLabel(KEY.LABEL_GRAPH_SERIES_ALARM_SONAR));
		pirSeries.setName(RS.rbLabel(KEY.LABEL_GRAPH_SERIES_ALARM_PIR));
		microwaveSeries.setName(RS
				.rbLabel(KEY.LABEL_GRAPH_SERIES_ALARM_MICROWAVE));
		laserSeries.setName(RS.rbLabel(KEY.LABEL_GRAPH_SERIES_ALARM_LASER));
		readTripsSeries.setName(RS
				.rbLabel(KEY.LABEL_GRAPH_SERIES_ACTIVITY_READS));

		final Calendar cal = Calendar.getInstance();
		cal.setTime(simpleCalender.dateProperty().get());
		populateData(chart, cal);
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
	protected int populateData(final XYChart<String, Number> chart,
			final Calendar cal) {
		final List<RemoteNodeReading> rnrs = ServiceProvider.IMPL
				.getRemoteNodeService().findReadingsByDate(cb.getRemoteNode(),
						cal, true);
		// add zero plot for 24hr period for each series
		xAxis.getCategories().clear();
		sonarSeries.getData().clear();
		pirSeries.getData().clear();
		microwaveSeries.getData().clear();
		laserSeries.getData().clear();
		readTripsSeries.getData().clear();
		if (rnrs.size() > 0) {
			String time;
			final int d = 0;// Double.MIN_NORMAL;
			Number l = d, m = d, p = d, s = d, r = d;
			for (final RemoteNodeReading rnr : rnrs) {
				l = m = p = s = r = d;
				time = UGateUtil.dateFormatTime(rnr.getReadDate());
				xAxis.getCategories().add(time);
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
				int i = -1;
				addData(chart, sonarSeries, new XYChart.Data<>(time, s), ++i);
				addData(chart, pirSeries, new XYChart.Data<>(time, p), ++i);
				addData(chart, microwaveSeries, new XYChart.Data<>(time, m),
						++i);
				addData(chart, laserSeries, new XYChart.Data<>(time, l), ++i);
				addData(chart, readTripsSeries, new XYChart.Data<>(time, r),
						++i);
			}
			if (chart.getData().isEmpty()) {
				chart.setData(FXCollections.observableArrayList(Arrays.asList(
						sonarSeries, pirSeries, microwaveSeries, laserSeries,
						readTripsSeries)));
				chart.setLegendVisible(true);
			}
			// chart legend will not update using the data node style- need to
			// set it here
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					final Set<Node> nodes = chart
							.lookupAll(".chart-legend-item-symbol");
					int i = -1;
					for (final Node n : nodes) {
						applyBarFillColorHex(n, getBarFillColorHex(++i));
					}
				}
			});
		} else {
			if (!chart.getData().isEmpty()) {
				chart.getData().clear();
			}
			chart.setLegendVisible(false);
		}
		return rnrs.size();
	}

	/**
	 * Adds {@linkplain XYChart.Data} to a {@linkplain XYChart.Series} for a
	 * specified index and sets the fill based upon the index.
	 * 
	 * @param chart 
	 * 				the {@linkplain XYChart}
	 * @param series 
	 * 				the {@linkplain XYChart.Series
	 * @param data 
	 * 				the {@linkplain XYChart.Data}
	 * @param index 
	 * 				the index
	 */
	protected <X, Y> void addData(final XYChart<X, Y> chart,
			final XYChart.Series<X, Y> series, final XYChart.Data<X, Y> data,
			final int index) {
		series.getData().add(data);
		final String colorHex = getBarFillColorHex(index);
		series.nodeProperty().addListener(new ChangeListener<Node>() {
			@Override
			public void changed(final ObservableValue<? extends Node> ob,
					final Node oldNode, final Node newNode) {
				applyBarFillColorHex(newNode, colorHex);
			}
		});
		data.nodeProperty().addListener(new ChangeListener<Node>() {
			@Override
			public void changed(final ObservableValue<? extends Node> ob,
					final Node oldNode, final Node newNode) {
				applyBarFillColorHex(newNode, colorHex);
			}
		});
	}

	/**
	 * Gets the hexadecimal {@linkplain Color} used for items in the
	 * {@linkplain XYChart}
	 * 
	 * @param index
	 *            the index of the item
	 * @return the hexadecimal {@linkplain Color}
	 */
	protected String getBarFillColorHex(final int index) {
		final String color = GuiUtil.COLORS_CHART[index].toString();
		return '#' + color.substring(2, color.length() - 2);
	}

	/**
	 * Applies the hexadecimal {@linkplain Color} to a {@linkplain Node}
	 * 
	 * @param node
	 *            the {@linkplain Node} to apply the hexadecimal
	 *            {@linkplain Color} to
	 * @param colorHex
	 *            the hexadecimal {@linkplain Color}
	 */
	protected void applyBarFillColorHex(final Node node, final String colorHex) {
		node.setStyle("-fx-bar-fill: " + colorHex + ";");
		final DropShadow ds = new DropShadow();
		ds.setOffsetX(3.0);
		ds.setOffsetY(3.0);
		final Lighting lighting = new Lighting();
		lighting.setSurfaceScale(1d);
		lighting.setLight(new Light.Distant());
		ds.setInput(lighting);
		node.setEffect(ds);
	}
}
