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
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.control.Label;
import javafx.scene.control.LabelBuilder;
import javafx.scene.control.ProgressBarBuilder;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.ColorAdjustBuilder;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import org.ugate.resources.RS;
import org.ugate.resources.RS.KEYS;

/**
 * General GUI utility
 */
public class GuiUtil {

	public static final Color COLOR_ON = Color.web("#00FF00", 1);
	public static final Color COLOR_OPEN = Color.YELLOW;
	public static final Color COLOR_OFF = Color.RED;
	public static final Color COLOR_CLOSED = Color.SLATEGRAY;
	public static final Color COLOR_SELECTED = Color.DEEPSKYBLUE;
	public static final Color COLOR_SELECTED_BLAND = Color.DEEPSKYBLUE.deriveColor(1d, 1d, 0.7d, 1d);
	public static final Color COLOR_SELECTING = Color.YELLOW;
	public static final Color COLOR_SELECTING_BLAND = COLOR_SELECTING.deriveColor(1d, 1d, 0.7d, 1d);
	public static final Color COLOR_UNSELECTED = Color.DARKSLATEGRAY;

	public static final Color COLOR_SONAR = Color.CYAN;
	public static final Color COLOR_PIR = Color.ORANGE.brighter();
	public static final Color COLOR_MW = Color.CHARTREUSE;
	public static final Color COLOR_LASER = Color.RED;
	public static final Color COLOR_READ = Color.DARKBLUE.darker();
	public static final Color[] COLORS_CHART = new Color[] {
			GuiUtil.COLOR_SONAR, GuiUtil.COLOR_PIR, GuiUtil.COLOR_MW,
			GuiUtil.COLOR_LASER, GuiUtil.COLOR_READ };

	public static final Color COLOR_CAM = Color.YELLOW;
	public static final Color COLOR_MULTI = Color.ORANGE;
	public static final Color COLOR_DATE = Color.WHITESMOKE;

	/**
	 * Private utility constructor
	 */
	private GuiUtil() {
	}

	/**
	 * Creates a modal alert that shows a {@linkplain ProgressIndicator} display
	 * 
	 * @param parent
	 *            the parent {@linkplain Stage} of the alert (should not be
	 *            null)
	 * @return the {@linkplain ProgressIndicator} alert
	 */
	public static Stage alertProgress(final Stage parent) {
		final Stage alert = alert(parent, 200, 150, 
				Modality.APPLICATION_MODAL, new ProgressIndicator());
		return alert;
	}
	
	/**
	 * Creates a {@linkplain Service} that will show a modal
	 * {@linkplain ProgressIndicator} {@linkplain Stage} that will automatically
	 * be shown when started and hidden when complete/failed/canceled
	 * 
	 * @param <T>
	 *            the {@linkplain Service} task type
	 * @param parent
	 *            the parent {@linkplain Stage} of the
	 *            {@linkplain ProgressIndicator} alert
	 * @param progressTask
	 *            the {@linkplain ProgressIndicator} {@linkplain Task} ran in
	 *            the {@linkplain Service}
	 * @return the {@linkplain ProgressIndicator} alert {@linkplain Service}
	 */
	public static <T> Service<T> alertProgress(final Stage parent, final Task<T> progressTask) {
		final Light light = new Light.Distant();
		final Lighting lighting = new Lighting();
		lighting.setSurfaceScale(1d);
		lighting.setLight(light);
		//final ProgressIndicator pi = ProgressIndicatorBuilder.create().maxWidth(200d).effect(lighting).build(); 
		final ProgressIndicator pi = ProgressBarBuilder.create().maxWidth(parent.getWidth() / 2d).maxHeight(25d).effect(lighting).build();
		final Stage alert = alert(parent, parent.getWidth(), parent.getHeight(), Modality.APPLICATION_MODAL, pi, 
				LabelBuilder.create().text(RS.rbLabel(KEYS.SENDING)).build());
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
	 * @param parent
	 *            the parent of the alert (when present the alert will be
	 *            centered over the parent)
	 * @param width
	 *            the width of the alert
	 * @param height
	 *            the height of the alert
	 * @param modality
	 *            the {@linkplain Modality} of the alert
	 * @param children
	 *            the children of the alert
	 * @return the alert
	 */
	public static Stage alert(final Stage parent, final double width, final double height, 
			final Modality modality, final Node... children) {
		final Stage stage = new Stage();
		stage.initModality(modality);
		stage.initStyle(StageStyle.TRANSPARENT);
		final StackPane root = new StackPane();
		root.setPrefSize(width, height);
		if (children != null) {
			root.getChildren().addAll(children);
		}
		if (parent != null) {
			stage.setScene(new Scene(root, parent.getWidth(), parent.getHeight(), Color.TRANSPARENT));
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
		} else {
			stage.setScene(new Scene(root, width, height, Color.TRANSPARENT));
		}
		return stage;
	}

	/**
	 * Creates a dialog window {@linkplain Stage} that is shown when the
	 * {@linkplain DialogService#start()} is called and hidden when the submit
	 * {@linkplain Service#restart()} returns {@linkplain State#SUCCEEDED}. When
	 * a {@linkplain Task} throws an {@linkplain Exception} the
	 * {@linkplain Exception#getMessage()} will be used to update the messageHeader of
	 * the dialog.
	 * 
	 * @param parent
	 *            the parent {@linkplain Stage}
	 * @param titleKey
	 *            the resource key for the {@linkplain Stage#setTitle(String)}
	 * @param headerText
	 *            the text displayed in the header of the {@linkplain Dialog}
	 * @param submitLabelKey
	 *            the resource key for the {@linkplain Button#setText(String)}
	 * @param width
	 *            the width of the {@linkplain Stage}
	 * @param height
	 *            the height of the {@linkplain Stage}
	 * @param submitService
	 *            the {@linkplain Service} ran whenever the submit
	 *            {@linkplain Button} is clicked
	 * @param children
	 *            the child {@linkplain Node}s that will be added between the
	 *            messageHeader and submit {@linkplain Button} (if any). If any of the
	 *            {@linkplain Node}s are {@linkplain Button}s they will be added
	 *            to the internal {@linkplain Button} {@linkplain FlowPane}
	 *            added to the bottom of the dialog.
	 * @return the {@linkplain DialogService}
	 */
	public static DialogService dialogService(final Stage parent, final KEYS titleKey, final String headerText, 
			final KEYS submitLabelKey, final double width, final double height, final Service<Void> submitService, 
			final Modality modality, final Node... children) {
		final Stage window = new Stage();
		window.initModality(modality);
		final Button submitBtn = ButtonBuilder.create().text(RS.rbLabel(submitLabelKey == null ? KEYS.SUBMIT : 
			submitLabelKey)).defaultButton(true).onAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent actionEvent) {
				submitService.restart();
			}
		}).build();
		final Dialog dialog = new Dialog(parent, window, titleKey, headerText, submitBtn, width, height, children);
		return new DialogService(dialog, submitService);
	}

	/**
	 * Dialog {@linkplain Stage}
	 */
	public static class Dialog {

		private final Stage parent;
		private final Stage stage;
		private final Text header;
		private final Text messageHeader;
		private final VBox content;

		/**
		 * Constructor
		 * 
		 * @param parent
		 *            the parent {@linkplain Stage}
		 * @param stage
		 *            the dialog {@linkplain Stage}
		 * @param titleKey
		 *            the {@linkplain KEYS} used for the title
		 * @param headerText
		 *            the text used for the header
		 * @param submitButton
		 *            the submit {@linkplain Button}
		 * @param width
		 *            the width
		 * @param height
		 *            the height
		 * @param children
		 *            the child {@linkplain Node}s
		 */
		public Dialog(final Stage parent, final Stage stage, final KEYS titleKey,
				final String headerText, final Button submitButton,
				final double width, final double height, final Node... children) {
			header = TextBuilder.create().text(headerText)
					.styleClass("dialog-title").wrappingWidth(width / 1.2d)
					.build();
			messageHeader = TextBuilder.create()
					.styleClass("dialog-message").wrappingWidth(width / 1.2d)
					.build();
			this.parent = parent;
			this.stage = stage;
			if (parent != null) {
				stage.initModality(Modality.APPLICATION_MODAL);
			}
			stage.initStyle(StageStyle.TRANSPARENT);
			stage.getIcons().add(RS.img(RS.IMG_LOGO_16));
			if (titleKey != null) {
				stage.setTitle(RS.rbLabel(titleKey));
			}
			content = VBoxBuilder.create().styleClass("dialog")
					.build();
			content.setMaxSize(width, height);
			stage.setScene(new Scene(content, width, height, Color.TRANSPARENT));
			stage.getScene().getStylesheets().add(RS.path(RS.CSS_MAIN));
			final FlowPane flowPane = new FlowPane();
			flowPane.setAlignment(Pos.CENTER);
			flowPane.setVgap(20d);
			flowPane.setHgap(10d);
			flowPane.setPrefWrapLength(width);
			if (submitButton != null) {
				flowPane.getChildren().add(submitButton);
			}
			content.getChildren().addAll(header, messageHeader);
			if (children != null && children.length > 0) {
				for (final Node node : children) {
					if (node == null) {
						continue;
					}
					if (node instanceof Button) {
						flowPane.getChildren().add(node);
					} else {
						content.getChildren().add(node);
					}
				}
			}
			content.getChildren().addAll(flowPane);
		}

		/**
		 * @return the parent {@linkplain Stage}
		 */
		public Stage getParent() {
			return parent;
		}

		/**
		 * @return the {@linkplain Dialog} {@linkplain Stage}
		 */
		public Stage getStage() {
			return stage;
		}

		/**
		 * @return the header text
		 */
		public Text getHeader() {
			return header;
		}

		/**
		 * @return the message header
		 */
		public Text getMessageHeader() {
			return messageHeader;
		}

		/**
		 * @return the content {@linkplain VBox}
		 */
		public VBox getContent() {
			return content;
		}
	}
	
	/**
	 * Creates a {@linkplain Popup} alert
	 * 
	 * @param width
	 *            the width of the {@linkplain Popup}
	 * @param height
	 *            the height of the {@linkplain Popup}
	 * @param headerText
	 *            the text to display in the header {@linkplain Region}
	 * @param closeButtonKey
	 *            the {@linkplain KEYS} used for the text of the alternative
	 *            close {@linkplain Button} when no children are specified
	 * @param children
	 *            the children of the {@linkplain Popup} (when null a close
	 *            button will be added)
	 * @return the {@linkplain Popup}
	 */
	public static Popup alert(final double width, final double height, 
			final String headerText, final KEYS closeButtonKey, final Node... children) {
		final Popup alert = new Popup();
		alert.setAutoFix(true);
		alert.setAutoHide(false);
		alert.setHideOnEscape(false);
		if (headerText != null && !headerText.isEmpty()) {
			alert.getContent().add(new Label(headerText));
		}
		if (children != null && children.length > 0) {
			alert.getContent().addAll(children);
		} else {
			final KEYS cbk = closeButtonKey != null ? closeButtonKey : KEYS.CLOSE;
			final Button cb = ButtonBuilder.create().text(RS.rbLabel(cbk)).build();
			cb.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(final MouseEvent event) {
					alert.hide();
				}
			});
		}
		alert.sizeToScene();
		return alert;
	}
	
	/**
	 * Creates a {@linkplain Timeline} that will alternate the
	 * {@linkplain Color}s of the {@linkplain DropShadow} when played
	 * 
	 * @param ds
	 *            the {@linkplain DropShadow}
	 * @param onColor
	 *            the on {@linkplain Color}
	 * @param offColor
	 *            the off {@linkplain Color}
	 * @param cycleCount
	 *            the {@linkplain Timeline#getCycleCount()}
	 * @return the {@linkplain Timeline}
	 */
	public static Timeline createDropShadowColorIndicatorTimeline(final DropShadow ds, 
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
	 * Creates a background display {@linkplain Region}
	 * 
	 * @param padding
	 *            the padding in the {@linkplain GridPane} group
	 * @param gapBetweenChildren
	 *            the vertical and/or horizontal gap between cells
	 * @param numItemsPerRow
	 *            the number of items per row
	 * @param gaugeStyle
	 *            true to style the children as gauges
	 * @param startCol
	 *            the column to start adding the {@linkplain Node}(s) to
	 * @param startRow
	 *            the row to start adding the {@linkplain Node}(s) to
	 * @param nodes
	 *            the {@linkplain Node}s to add to the display
	 * @return the background display {@linkplain Region}
	 */
	public static final GridPane createBackgroundDisplay(final Insets padding, final double gapBetweenChildren, 
			final int numItemsPerRow, final boolean gaugeStyle, final int startCol, final int startRow, 
			final Node... nodes) {
		final GridPane grid = new GridPane();
		if (padding != null) {
			grid.setPadding(padding);
		}
		grid.setHgap(gapBetweenChildren);
		grid.setVgap(gapBetweenChildren);
		int col = (startCol >= 0 ? startCol : 0)-1, row = startRow >= 0 ? startRow : 0;
		for (final Node node : nodes) {
			if (gaugeStyle) {
				node.getStyleClass().add("gauge");
			}
			grid.add(node, ++col, row);
			row = col == (numItemsPerRow - 1) ? row + 1 : row;
			col = col == (numItemsPerRow - 1) ? -1 : col;
		}
//		grid.setAlignment(Pos.CENTER_LEFT);
		grid.getStyleClass().add("background-display");
		return grid;
	}
	
	/**
	 * Checks the {@linkplain MouseEvent} to see if it's a normal primary press
	 * of the mouse without any other keys held down
	 * 
	 * @param event
	 *            the pressed {@linkplain MouseEvent}
	 * @return true when just the primary is pressed
	 */
	public static boolean isPrimaryPress(final MouseEvent event) {
		return !(event.isMetaDown() || event.isControlDown() || event.isAltDown() || 
				event.isShiftDown() || event.isShortcutDown() || !event.isPrimaryButtonDown());
	}
	
	/**
	 * A {@linkplain Service} for showing and hiding a {@linkplain Stage}
	 */
	public static class DialogService extends Service<Void> {

		private final Dialog dialog;
		private final Effect origEffect;
		private final Service<Void> submitService;
		
		/**
		 * Creates a dialog service for showing and hiding a {@linkplain Stage}
		 * 
		 * @param parent
		 *            the parent {@linkplain Stage}
		 * @param window
		 *            the window {@linkplain Stage} that will be shown/hidden
		 * @param messageHeader
		 *            the messageHeader {@linkplain Text} used for the service
		 *            that will be updated with exception information as the
		 *            submitService informs the {@linkplain DialogService} of
		 * @param submitService
		 *            the {@linkplain Service} that will be listened to for
		 *            {@linkplain State#SUCCEEDED} at which point the
		 *            {@linkplain DialogService} window {@linkplain Stage} will
		 *            be hidden
		 */
		protected DialogService(final Dialog dialog, final Service<Void> submitService) {
			this.dialog = dialog;
			this.origEffect = hasParentSceneRoot() ? dialog.getParent().getScene().getRoot().getEffect() : null;
			this.submitService = submitService;
			this.submitService.stateProperty().addListener(new ChangeListener<State>() {
				@Override
				public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
					if (submitService.getException() != null) {
						// service indicated that an error occurred
						dialog.getMessageHeader().setText(submitService.getException().getMessage());
					} else if (newValue == State.SUCCEEDED) {
						dialog.getStage().getScene().getRoot().setEffect(ColorAdjustBuilder.create().brightness(-0.5d).build());
						Platform.runLater(createHideTask());
					}
				}
			});
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		protected Task<Void> createTask() {
			return dialog.getStage().isShowing() ? createHideTask() : createShowTask();
		}
		
		/**
		 * @return a task that will show the service {@linkplain Stage}
		 */
		protected Task<Void> createShowTask() {
			final Task<Void> showTask = new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					Platform.runLater(new Runnable() {
						public void run() {
							if (hasParentSceneRoot()) {
								dialog.getParent().getScene().getRoot().setEffect(
										ColorAdjustBuilder.create().brightness(-0.5d).build());
							}
							dialog.getStage().show();
							dialog.getStage().centerOnScreen();
						}
					});
					return null;
				}
			};
			showTask.stateProperty().addListener(new ChangeListener<State>() {
				@Override
				public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
					if (newValue == State.FAILED || newValue == State.CANCELLED) {
						Platform.runLater(createHideTask());
					}
				}
			});
			return showTask;
		}
		
		/**
		 * @return a task that will hide the service {@linkplain Stage}
		 */
		protected Task<Void> createHideTask() {
			final Task<Void> closeTask = new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					dialog.getStage().hide();
					if (hasParentSceneRoot()) {
						dialog.getParent().getScene().getRoot().setEffect(origEffect);
					}
					dialog.getStage().getScene().getRoot().setDisable(false);
					return null;
				}
			};
			return closeTask;
		}
		
		/**
		 * @return true when the parent {@linkplain Stage#getScene()} has a valid {@linkplain Scene#getRoot()}
		 */
		private boolean hasParentSceneRoot() {
			return dialog.getParent() != null && dialog.getParent().getScene() != null && 
					dialog.getParent().getScene().getRoot() != null;
		}
		
		/**
		 * Hides the dialog used in the {@linkplain Service}
		 */
		public void hide() {
			Platform.runLater(createHideTask());
		}
	}
}
