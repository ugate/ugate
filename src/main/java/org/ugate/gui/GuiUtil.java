package org.ugate.gui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.ColorAdjustBuilder;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * General GUI utility
 */
public class GuiUtil {

	/**
	 * Private utility constructor
	 */
	private GuiUtil() {
	}
	
	/**
	 * Creates a modal alert that shows a progress display
	 * 
	 * @param parent the parent stage of the alert (should not be null)
	 * @return the progress alert
	 */
	public static Stage alertProgress(final Stage parent) {
		final Stage alert = alert(parent, 200, 150, 
				Modality.APPLICATION_MODAL, new ProgressIndicator());
		return alert;
	}
	
	/**
	 * Creates a service that will show a modal progress window that will automatically be shown when
	 * started and hidden when complete/failed/canceled
	 * 
	 * @param <T> the service task type
	 * @param parent the parent stage of the progress alert
	 * @param progressTask the progress task ran in the service
	 * @return the progress alert service
	 */
	public static <T> Service<T> alertProgress(final Stage parent, final Task<T> progressTask) {
		final ProgressIndicator pi = new ProgressIndicator();
		final Light light = new Light.Distant();
		final Lighting lighting = new Lighting();
		lighting.setSurfaceScale(3d);
		lighting.setLight(light);
		pi.setEffect(lighting);
		final Stage alert = alert(parent, 200, 150, Modality.APPLICATION_MODAL, pi);
		final Service<T> service = new Service<T>() {
			@Override
			protected Task<T> createTask() {
				return progressTask;
			}
		};
		final Effect origEffect = parent.getScene().getRoot().getEffect();
		progressTask.stateProperty().addListener(new ChangeListener<State>() {
			@Override
			public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
				if (newValue == State.RUNNING && !alert.isShowing()) {
					Platform.runLater(new Runnable() {
						public void run() {
							parent.getScene().getRoot().setEffect(ColorAdjustBuilder.create().brightness(-0.5d).build());
							alert.show();
						}
					});
				} else if (newValue == State.SUCCEEDED || newValue == State.FAILED || newValue == State.CANCELLED) {
					Platform.runLater(new Runnable() {
						public void run() {
							alert.hide();
							parent.getScene().getRoot().setEffect(origEffect);
						}
					});
				}
			}
		});
		return service;
	}

	/**
	 * Creates an alert {@linkplain Stage}
	 * 
	 * @param parent the parent of the alert (when present the alert will be centered over the parent)
	 * @param width the width of the alert
	 * @param height the height of the alert
	 * @param modality the {@linkplain Modality} of the alert
	 * @param children the children of the alert
	 * @return the alert
	 */
	public static Stage alert(final Stage parent, final double width, final double height, 
			final Modality modality, final Node... children) {
		final Stage stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initStyle(StageStyle.TRANSPARENT);
		final StackPane root = new StackPane();
		root.setPrefSize(width, height);
		if (children != null) {
			root.getChildren().addAll(children);
		}
		stage.setScene(new Scene(root, width, height, Color.TRANSPARENT));
		if (parent != null) {
			stage.initOwner(parent);
			stage.setX(parent.getX() + parent.getScene().getWidth() / 2d - width / 2d);
			stage.setY(parent.getY() + parent.getScene().getHeight() / 2d - height / 2d);
			parent.xProperty().addListener(new ChangeListener<Number>() {
				@Override
				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
					stage.setX(newValue.doubleValue() + parent.getScene().getWidth() / 2d - width / 2d);
				}
			});
			parent.yProperty().addListener(new ChangeListener<Number>() {
				@Override
				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
					stage.setY(newValue.doubleValue() + parent.getScene().getHeight() / 2d - height / 2d);
				}
			});
		}
		return stage;
	}
	
	/**
	 * Creates a {@linkplain Popup} version of the alert
	 * 
	 * @param width the width of the alert
	 * @param height the height of the alert
	 * @param children the children of the alert
	 * @return the alert
	 */
	public static Popup alert(final double width, final double height, 
			final Node... children) {
		final Popup alert = new Popup();
		alert.setAutoFix(true);
		alert.setAutoHide(false);
		alert.setHideOnEscape(false);
		alert.getContent().addAll(children);
		alert.sizeToScene();
		return alert;
	}
	
	/**
	 * Creates a time line that will alternate the colors of the drop shadow when played
	 *  
	 * @param ds the drop shadow
	 * @param onColor the on color
	 * @param offColor the off color
	 * @param cycleCount the cycle count
	 * @return the time line
	 */
	public static Timeline createDropShadowColorIndicatorTimline(final DropShadow ds, 
			final Color onColor, final Color offColor, final int cycleCount) {
		ds.setColor(offColor);
		final Timeline timeline = new Timeline();
		timeline.setCycleCount(cycleCount <=0 ? Timeline.INDEFINITE : cycleCount);
		timeline.setAutoReverse(true);
		final KeyFrame kf = new KeyFrame(Duration.millis(500), new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				ds.setColor(ds.getColor().equals(offColor) ? onColor : offColor);
			}
		});
		timeline.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				ds.setColor(offColor);
			}
		});
		timeline.getKeyFrames().add(kf);
		return timeline;
	}
	
	/**
	 * Creates a background display
	 * 
	 * @param padding the padding in the grid group
	 * @param gapBetweenChildren the vertical and/or horizontal gap between cells
	 * @param numItemsPerRow the number of items per row
	 * @param gaugeStyle true to style the children as gauges
	 * @param nodes the nodes to add to the display
	 * @return the background display
	 */
	public static final Region createBackgroundDisplay(final Insets padding, final double gapBetweenChildren, 
			final int numItemsPerRow, final boolean gaugeStyle, final Node... nodes) {
		final GridPane grid = new GridPane();
		grid.setPadding(padding);
		grid.setHgap(gapBetweenChildren);
		grid.setVgap(gapBetweenChildren);
		int col = -1, row = 0;
		for (final Node node : nodes) {
			if (gaugeStyle) {
				node.getStyleClass().add("gauge");
			}
			grid.add(node, ++col, row);
			row = col == (numItemsPerRow - 1) ? row + 1 : row;
			col = col == (numItemsPerRow - 1) ? -1 : col;
		}
//		grid.setAlignment(Pos.CENTER_RIGHT);
		grid.getStyleClass().add("background-display");
		return grid;
	}
	
	/**
	 * Checks the mouse event to see if it's a normal primary press of the mouse without
	 * any other keys held down
	 * 
	 * @param event the mouse pressed event
	 * @return true when just the primary is pressed
	 */
	public static boolean isPrimaryPress(final MouseEvent event) {
		return !(event.isMetaDown() || event.isControlDown() || event.isAltDown() || 
				event.isShiftDown() || event.isShortcutDown() || !event.isPrimaryButtonDown());
	}
}
