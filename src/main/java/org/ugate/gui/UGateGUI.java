package org.ugate.gui;

import java.util.Random;

import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.effect.Reflection;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import org.apache.log4j.Logger;
import org.ugate.UGateKeeper;
import org.ugate.UGateUtil;
import org.ugate.gui.components.AppFrame;
import org.ugate.gui.components.DisplayShelf;
import org.ugate.gui.components.TextAreaAppender;
import org.ugate.resources.RS;

/**
 * The main GUI application entry point
 */
public class UGateGUI extends Application {

	private static final Logger log = Logger.getLogger(UGateGUI.class);

	public static final double APPLICATION_WIDTH = 900d;
	public static final double APPLICATION_HEIGHT = 800d;
	
	private static final double TASKBAR_BUTTON_SCALE = 1.3;
	private static final String TASKBAR_BUTTON_DURATION = "300ms";
	private static final double TASKBAR_BUTTON_WIDTH = 100;
	private static final double TASKBAR_BUTTON_HEIGHT = 100;

	public static final AudioClip mediaPlayerConfirm = RS.newAudioClip("x_confirm.wav");
	public static final AudioClip mediaPlayerDoorBell = RS.newAudioClip("x_doorbell.wav");
	public static final AudioClip mediaPlayerCam = RS.newAudioClip("x_cam.wav");
	public static final AudioClip mediaPlayerComplete = RS.newAudioClip("x_complete.wav");
	public static final AudioClip mediaPlayerError = RS.newAudioClip("x_error.wav");
	public static final AudioClip mediaPlayerBlip = RS.newAudioClip("x_blip.wav");
	protected final StackPane centerView = new StackPane();
	protected final HBox taskbar = new HBox(10);
	protected final HBox connectionView = new HBox(10);
	protected final TextArea loggingView = new TextArea();
	protected MailConnectionView mailConnectionView;
	protected WirelessConnectionView wirelessConnectionView;
	protected Controls controls;
	protected AppFrame applicationFrame;

	/**
	 * Constructor
	 */
	public UGateGUI() {
		TextAreaAppender.setTextArea(loggingView);
	}

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			// Set up a simple configuration that logs on the console.
			// BasicConfigurator.configure();
			// PropertyConfigurator.configure(RS.class.getResource("log4j.xml").getPath());
			Application.launch(UGateGUI.class, args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() {
		log.debug("Iniitializing GUI...");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start(final Stage stage) throws Exception {
		log.debug("Starting GUI...");
		try {
			stage.getIcons().add(RS.img(RS.IMG_LOGO_16));
			// public final AudioClip mediaPlayerBeep = new
			// AudioClip(RS.class.getResource("x_beep.wav").getPath());
			wirelessConnectionView = new WirelessConnectionView() {
	
				@Override
				public void handleStatusChange(Boolean on) {
					if (!on) {
						changeCenterView(connectionView, false);
					}
				}
			};
			mailConnectionView = new MailConnectionView() {
	
				@Override
				public void handleStatusChange(Boolean on) {
					if (!on) {
						changeCenterView(connectionView, false);
					}
				}
			};
			controls = new Controls(stage);
	
			final BorderPane content = new BorderPane();
			content.setId("content");
			applicationFrame = new AppFrame(stage, content, APPLICATION_WIDTH, APPLICATION_HEIGHT, 
					APPLICATION_WIDTH + 10d, APPLICATION_HEIGHT + 10d);
			stage.getScene().getStylesheets().add(RS.path(RS.CSS_MAIN));
			stage.getScene().getStylesheets().add(RS.path(RS.CSS_DISPLAY_SHELF));
			stage.setTitle("UGate Application Interface");
	
			taskbar.setId("taskbar");
			taskbar.setCache(true);
			taskbar.setAlignment(Pos.CENTER);
			taskbar.setPadding(new Insets(10, 10, 50, 10));
			taskbar.setPrefHeight(100);
	
			centerView.setPrefHeight(300);
			//VBox.getVgrow(centerView);
			centerView.setId("center-view");
			centerView.setPadding(new Insets(10, 10, 70, 10));
			connectionView.setAlignment(Pos.CENTER);
			changeCenterView(connectionView, false);
	
			connectionView.getChildren().addAll(wirelessConnectionView,
					createSeparator(Orientation.VERTICAL), mailConnectionView);
	
			content.setCenter(centerView);
			content.setBottom(taskbar);
	
			taskbar.getChildren().add(
					createConnectionStatusView(genFisheyeTaskbar(RS.IMG_CONNECT,
							new Runnable() {
	
								public void run() {
									changeCenterView(connectionView, true);
								}
							})));
			taskbar.getChildren().add(
					genFisheyeTaskbar(RS.IMG_WIRELESS, new Runnable() {
	
						public void run() {
							changeCenterView(controls, true);
						}
					}));
			taskbar.getChildren().add(
					genFisheyeTaskbar(RS.IMG_PICS, new Runnable() {
						public void run() {
							changeCenterView(
									new DisplayShelf(UGateUtil.imagePath(),
											350, 350, 0.25, 45, 80,
											DisplayShelf.TOOLBAR_POSITION_TOP),
									true);
						}
					}));
			taskbar.getChildren().add(
					genFisheyeTaskbar(RS.IMG_GRAPH, new Runnable() {
	
						public void run() {
							NumberAxis xAxis = new NumberAxis();
							NumberAxis yAxis = new NumberAxis();
							LineChart<Number, Number> chart = new LineChart<Number, Number>(
									xAxis, yAxis);
							chart.setTitle("XBee Notifications");
							xAxis.setLabel("Hour");
							yAxis.setLabel("Occurrences");
							XYChart.Series<Number, Number> sonarTrippedSeries = new XYChart.Series<Number, Number>();
							sonarTrippedSeries.setName("Tripped Image Snapshot");
							Random random = new Random();
							for (int i = 0; i < 10 + random.nextInt(20); i++) {
								sonarTrippedSeries.getData().add(
										new XYChart.Data<Number, Number>(
												10 * i + 10,
												random.nextDouble() * 150));
							}
							chart.getData().add(sonarTrippedSeries);
							XYChart.Series<Number, Number> manualImageSeries = new XYChart.Series<Number, Number>();
							manualImageSeries
									.setName("Manually Initiated Image Snapshots");
							for (int i = 0; i < 10 + random.nextInt(20); i++) {
								manualImageSeries.getData().add(
										new XYChart.Data<Number, Number>(
												10 * i + 10,
												random.nextDouble() * 150));
							}
							chart.getData().add(manualImageSeries);
							changeCenterView(chart, true);
						}
					}));
			taskbar.getChildren().add(genFisheyeTaskbar(RS.IMG_LOGS, new Runnable() {
	
				public void run() {
					loggingView.setEditable(false);
					changeCenterView(loggingView, true);
				}
			}));
	
			stage.addEventHandler(WindowEvent.ANY,
					new EventHandler<WindowEvent>() {
	
						@Override
						public void handle(WindowEvent event) {
							if (event.getEventType() == WindowEvent.WINDOW_SHOWN) {
								Platform.runLater(new Runnable() {
									public void run() {
										// attempt connections
										wirelessConnectionView.getStatusHandler()
												.handle(null);
										mailConnectionView.getStatusHandler()
												.handle(null);
									}
								});
							} else if (event.getEventType() == WindowEvent.WINDOW_CLOSE_REQUEST) {
								log.debug("Window close requested");
							}
						}
					});
			stage.show();
		} catch (final Throwable t) {
			log.error("Unable to start GUI", t);
			throw new RuntimeException("Unable to start GUI", t);
		}
		log.debug("GUI started");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop() throws Exception {
		super.stop();
		wirelessConnectionView.disconnect();
		mailConnectionView.disconnect();
		log.info("Exiting application");
		System.exit(0);
	}

	private Separator createSeparator(Orientation orientation) {
		Separator topSeparator = new Separator();
		topSeparator.setOrientation(orientation);
		topSeparator.setHalignment(HPos.CENTER);
		topSeparator.setValignment(VPos.CENTER);
		return topSeparator;
	}

	private Node createConnectionStatusView(Node connectionButton) {
		final VBox node = new VBox();
		node.setCache(true);
		final HBox statusNode = new HBox(10);
		statusNode.setCache(true);
		statusNode.setAlignment(Pos.CENTER);
		statusNode.getChildren().add(wirelessConnectionView.statusIcon);
		statusNode.getChildren().add(mailConnectionView.statusIcon);
		node.getChildren().add(connectionButton);
		node.getChildren().add(statusNode);
		return node;
	}

	public static ImageView genFisheyeTaskbar(final String iconName,
			final Runnable action) {
		return genFisheye(iconName, TASKBAR_BUTTON_WIDTH,
				TASKBAR_BUTTON_HEIGHT, TASKBAR_BUTTON_SCALE,
				TASKBAR_BUTTON_SCALE, true, action);
	}

	public static ImageView genFisheye(final String iconName,
			final double width, final double height, final double scaleX,
			final double scaleY, final boolean showReflection,
			final Runnable action) {
		final ImageView node = RS.imgView(iconName);
		node.setCacheHint(CacheHint.SCALE);
		node.setFitWidth(width);
		node.setFitHeight(height);

		final ScaleTransition animationGrow = new ScaleTransition(
				Duration.valueOf(TASKBAR_BUTTON_DURATION), node);
		animationGrow.setToX(scaleX);
		animationGrow.setToY(scaleY);

		final ScaleTransition animationShrink = new ScaleTransition(
				Duration.valueOf(TASKBAR_BUTTON_DURATION), node);
		animationShrink.setToX(1);
		animationShrink.setToY(1);

		final Reflection effect = showReflection ? new Reflection() : null;
		if (showReflection) {
			node.setEffect(effect);
		}

		node.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				action.run();
			}
		});
		node.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				node.toFront();
				animationShrink.stop();
				animationGrow.playFromStart();
			}
		});
		node.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				animationGrow.stop();
				animationShrink.playFromStart();
			}
		});
		return node;
	}

	/**
	 * Changes the center view above the task bar menu
	 * 
	 * @param node
	 *            the node to change the center view to
	 * @param checkConnection
	 *            true to check for the connection when changing the view, false
	 *            will change the center view only when both the XBee and email
	 *            agent are connected
	 */
	private void changeCenterView(Node node, boolean checkConnection) {
		if (!checkConnection
				|| (UGateKeeper.DEFAULT.wirelessIsConnected() && UGateKeeper.DEFAULT
						.isEmailConnected())) {
			centerView.getChildren().clear();
			centerView.getChildren().add(node);
		}
	}
}
