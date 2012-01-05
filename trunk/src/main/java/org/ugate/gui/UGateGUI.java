package org.ugate.gui;

import java.io.File;
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
import javafx.scene.effect.InnerShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import org.apache.log4j.Logger;
import org.ugate.IGateKeeperListener;
import org.ugate.RemoteSettings;
import org.ugate.UGateKeeper;
import org.ugate.UGateKeeperEvent;
import org.ugate.gui.components.AppFrame;
import org.ugate.gui.components.DisplayShelf;
import org.ugate.gui.components.TextAreaAppender;
import org.ugate.resources.RS;
import org.ugate.wireless.data.ImageCapture;

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

	private static final AudioClip mediaPlayerConfirm = RS.audioClip("x_confirm.wav");
	private static final AudioClip mediaPlayerDoorBell = RS.audioClip("x_doorbell.wav");
	private static final AudioClip mediaPlayerCam = RS.audioClip("x_cam.wav");
	private static final AudioClip mediaPlayerComplete = RS.audioClip("x_complete.wav");
	private static final AudioClip mediaPlayerError = RS.audioClip("x_error.wav");
	private static final AudioClip mediaPlayerBlip = RS.audioClip("x_blip.wav");
	protected final HBox taskbar = new HBox(10);
	protected final HBox connectionView = new HBox(10);
	protected final TextArea loggingView = new TextArea();
	protected ControlBar controlBar;
	protected EmailHostConnectionView mailConnectionView;
	protected WirelessHostConnectionView wirelessConnectionView;
	protected StackPane centerView;
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
	public static void main(final String[] args) {
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
			// TODO : add GUI support for multiple remote wireless nodes
			stage.getIcons().add(RS.img(RS.IMG_LOGO_16));
	
			final BorderPane content = new BorderPane();
			content.setId("content");
			content.setEffect(new InnerShadow());
			applicationFrame = new AppFrame(stage, content, APPLICATION_WIDTH, APPLICATION_HEIGHT, 
					APPLICATION_WIDTH + 10d, APPLICATION_HEIGHT + 10d, false);
			stage.getScene().getStylesheets().add(RS.path(RS.CSS_MAIN));
			stage.getScene().getStylesheets().add(RS.path(RS.CSS_DISPLAY_SHELF));
			stage.setTitle(RS.rbLabel("app.title"));
	
			taskbar.setId("taskbar");
			taskbar.setCache(true);
			taskbar.setAlignment(Pos.CENTER);
			taskbar.setPadding(new Insets(10, 10, 50, 10));
			taskbar.setPrefHeight(100);
			
			centerView = new StackPane();
			//centerView.setPrefHeight(300);
			//VBox.getVgrow(centerView);
			centerView.setId("center-view");
			centerView.setPadding(new Insets(0, 0, 70d, 0));
			HBox.setHgrow(centerView, Priority.ALWAYS);
			VBox.setVgrow(centerView, Priority.ALWAYS);
			
			controlBar = new ControlBar(stage);
			final Controls controls = new Controls(controlBar);
			final VBox main = new VBox(0);
			main.setStyle("-fx-background-color: #000000;");
			HBox.setHgrow(main, Priority.ALWAYS);
			VBox.setVgrow(main, Priority.ALWAYS);
			main.getChildren().addAll(controlBar, centerView);
			
			wirelessConnectionView = new WirelessHostConnectionView(controlBar);
			mailConnectionView = new EmailHostConnectionView(controlBar);
	
			// change the center view back to the connection view when connections are lost
			UGateKeeper.DEFAULT.addListener(new IGateKeeperListener() {
				@Override
				public void handle(final UGateKeeperEvent<?> event) {
					playSound(event);
					if (event.getType() == UGateKeeperEvent.Type.WIRELESS_HOST_DISCONNECTED ||
							event.getType() == UGateKeeperEvent.Type.EMAIL_DISCONNECTED) {
						changeCenterView(connectionView);
					}
				}
			});
			connectionView.setAlignment(Pos.CENTER);
			connectionView.getChildren().addAll(wirelessConnectionView,
					createSeparator(Orientation.VERTICAL), mailConnectionView);
	
			content.setCenter(main);
			content.setBottom(taskbar);
	
			taskbar.getChildren().add(
					createConnectionStatusView(genFisheyeTaskbar(RS.IMG_CONNECT, RS.rbLabel("app.connection.desc"),
							new Runnable() {
								@Override
								public void run() {
									changeCenterView(connectionView);
								}
							})));
			taskbar.getChildren().add(
					genFisheyeTaskbar(RS.IMG_WIRELESS, RS.rbLabel("app.controls.desc"), new Runnable() {
						@Override			
						public void run() {
							changeCenterView(controls);
						}
					}));
			taskbar.getChildren().add(
					genFisheyeTaskbar(RS.IMG_PICS, RS.rbLabel("app.capture.desc"), new Runnable() {
						@Override
						public void run() {
							changeCenterView(
									new DisplayShelf(new File(UGateKeeper.DEFAULT.wirelessWorkingDirectory(
											UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex())),
											350, 350, 0.25, 45, 80,
											DisplayShelf.TOOLBAR_POSITION_TOP, 
											RS.rbLabel("displayshelf.fullsize.tooltip")));
						}
					}));
			taskbar.getChildren().add(
					genFisheyeTaskbar(RS.IMG_GRAPH, RS.rbLabel("app.graph.desc"), new Runnable() {
						@Override
						public void run() {
							NumberAxis xAxis = new NumberAxis();
							NumberAxis yAxis = new NumberAxis();
							LineChart<Number, Number> chart = new LineChart<Number, Number>(
									xAxis, yAxis);
							chart.setTitle(RS.rbLabel("graph.alarm.notify"));
							xAxis.setLabel(RS.rbLabel("graph.axis.x"));
							yAxis.setLabel(RS.rbLabel("graph.axis.y"));
							XYChart.Series<Number, Number> sonarTrippedSeries = new XYChart.Series<Number, Number>();
							sonarTrippedSeries.setName(RS.rbLabel("graph.series.alarm"));
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
									.setName(RS.rbLabel("graph.series.activity.manual"));
							for (int i = 0; i < 10 + random.nextInt(20); i++) {
								manualImageSeries.getData().add(
										new XYChart.Data<Number, Number>(
												10 * i + 10,
												random.nextDouble() * 150));
							}
							chart.getData().add(manualImageSeries);
							changeCenterView(chart);
						}
					}));
			taskbar.getChildren().add(genFisheyeTaskbar(RS.IMG_LOGS, RS.rbLabel("app.logs.desc"), new Runnable() {
				@Override
				public void run() {
					loggingView.setEditable(false);
					changeCenterView(loggingView);
				}
			}));
	
			stage.addEventHandler(WindowEvent.WINDOW_SHOWN,
					new EventHandler<WindowEvent>() {
						@Override
						public void handle(final WindowEvent event) {
							if (UGateKeeper.DEFAULT.emailIsConnected() && 
									UGateKeeper.DEFAULT.wirelessIsConnected()) {
								return;
							}
							Platform.runLater(new Runnable() {
								public void run() {
									// attempt connections
									wirelessConnectionView.connect();
									mailConnectionView.connect();
								}
							});
						}
					});
			changeCenterView(connectionView);
			stage.show();
			SystemTray.initSystemTray(stage);
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
		log.info("Exiting application");
		UGateKeeper.DEFAULT.exit();
		SystemTray.exit();
		// TODO : remove dependency on System#exit
		System.exit(0);
	}

	private Separator createSeparator(final Orientation orientation) {
		Separator topSeparator = new Separator();
		topSeparator.setOrientation(orientation);
		topSeparator.setHalignment(HPos.CENTER);
		topSeparator.setValignment(VPos.CENTER);
		return topSeparator;
	}

	private Node createConnectionStatusView(final Node connectionButton) {
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

	public ImageView genFisheyeTaskbar(final String iconName,
			final String helpText, final Runnable action) {
		return genFisheye(iconName, TASKBAR_BUTTON_WIDTH,
				TASKBAR_BUTTON_HEIGHT, TASKBAR_BUTTON_SCALE,
				TASKBAR_BUTTON_SCALE, true, helpText, action);
	}

	public ImageView genFisheye(final String iconName,
			final double width, final double height, final double scaleX,
			final double scaleY, final boolean showReflection,
			final String helpText, final Runnable action) {
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
		node.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (GuiUtil.isPrimaryPress(event)) {
					action.run();
				} else {
					controlBar.setHelpText(helpText);
				}
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
	 */
	private void changeCenterView(final Node node) {
		centerView.getChildren().clear();
		centerView.getChildren().add(node);
	}
	
	/**
	 * Plays a sound for predefined events if preferences is set to do so
	 * 
	 * @param event the event
	 */
	private void playSound(final UGateKeeperEvent<?> event) {
		final String soundsOn = UGateKeeper.DEFAULT.settingsGet(RemoteSettings.SOUNDS_ON, 
				UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex());
		if (soundsOn == null || soundsOn.isEmpty() || Integer.parseInt(soundsOn) != 1) {
			return;
		}
		if (event.getType() == UGateKeeperEvent.Type.WIRELESS_DATA_TX_STATUS_RESPONSE_UNRECOGNIZED) {
			mediaPlayerConfirm.play();
		} else if (event.getType() == UGateKeeperEvent.Type.WIRELESS_DATA_TX_STATUS_RESPONSE_SUCCESS) {
			mediaPlayerBlip.play();
		} else if (event.getType() == UGateKeeperEvent.Type.WIRELESS_DATA_TX_STATUS_RESPONSE_FAILED) {
			mediaPlayerError.play();
		} else if (event.getType() == UGateKeeperEvent.Type.WIRELESS_DATA_RX_SUCCESS && 
				event.getNewValue() instanceof ImageCapture) {
			mediaPlayerDoorBell.play();
//			UGateKeeper.DEFAULT.emailSend("UGate Tripped", trippedImage.toString(), 
//					UGateKeeper.DEFAULT.preferences.get(UGateKeeper.MAIL_USERNAME_KEY), 
//					UGateKeeper.DEFAULT.preferences.get(UGateKeeper.MAIL_RECIPIENTS_KEY, UGateKeeper.MAIL_RECIPIENTS_DELIMITER).toArray(new String[]{}), 
//					imageFile.filePath);
			mediaPlayerComplete.play();
		} else if (event.getType() == UGateKeeperEvent.Type.WIRELESS_DATA_RX_MULTIPART && 
				event.getNewValue() instanceof ImageCapture) {
			mediaPlayerCam.play();
		}
	}
}
