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
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
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
	
	public static final String HELP_TEXT_DEFAULT = "Right-Click on any control for help";

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
		final Stage alert = alert(parent, 200, 150, 
				Modality.APPLICATION_MODAL, new ProgressIndicator());
		progressTask.stateProperty().addListener(new ChangeListener<State>() {
			@Override
			public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
				if (newValue == State.RUNNING && !alert.isShowing()) {
					Platform.runLater(new Runnable() {
						public void run() {
							alert.show();
						}
					});
				} else if (newValue == State.SUCCEEDED || newValue == State.FAILED || newValue == State.CANCELLED) {
					Platform.runLater(new Runnable() {
						public void run() {
							alert.hide();
						}
					});
				}
			}
		});
		return new Service<T>() {
			@Override
			protected Task<T> createTask() {
				return progressTask;
			}
		};
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
	 * Adds the help text when right clicked
	 * 
	 * @param helpText the scroll pane the contains a label as it's content
	 * @param node the node to trigger the text
	 * @param text the text to show
	 */
	public static void addHelpText(final ScrollPane helpText, final Node node, final String text) {
		node.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(final MouseEvent event) {
				if (event.isSecondaryButtonDown()) {
					helpText.setVvalue(helpText.getVmin());
					((Label) helpText.getContent()).setText(text);
					event.consume();
				}
			}
		});
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
