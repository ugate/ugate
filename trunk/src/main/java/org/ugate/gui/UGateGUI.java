package org.ugate.gui;

import java.io.File;
import java.util.Random;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Control;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.DropShadowBuilder;
import javafx.scene.effect.InnerShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

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
	
	private static final double TASKBAR_HEIGHT = 180d;
	private static final double TASKBAR_BUTTON_WIDTH = 100d;
	private static final double TASKBAR_BUTTON_HEIGHT = 100d;

	private static final AudioClip mediaPlayerConfirm = RS.audioClip("x_confirm.wav");
	private static final AudioClip mediaPlayerDoorBell = RS.audioClip("x_doorbell.wav");
	private static final AudioClip mediaPlayerCam = RS.audioClip("x_cam.wav");
	private static final AudioClip mediaPlayerComplete = RS.audioClip("x_complete.wav");
	private static final AudioClip mediaPlayerError = RS.audioClip("x_error.wav");
	private static final AudioClip mediaPlayerBlip = RS.audioClip("x_blip.wav");
	protected final HBox taskbar = new HBox(10d);
	protected final HBox connectionView = new HBox(10d);
	protected final TextArea loggingView = new TextArea();
	protected ControlBar controlBar;
	protected EmailHostConnectionView mailConnectionView;
	protected WirelessHostConnectionView wirelessConnectionView;
	protected StackPane centerView;
	protected AppFrame applicationFrame;
	protected final IntegerProperty taskBarSelectProperty = new SimpleIntegerProperty(0);

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
			stage.setTitle(RS.rbLabel("app.title"));
	
			controlBar = new ControlBar(stage);
			final BorderPane content = new BorderPane();
			content.setId("main-content");
			content.setEffect(new InnerShadow());
			applicationFrame = new AppFrame(stage, content, APPLICATION_WIDTH, APPLICATION_HEIGHT, 
					APPLICATION_WIDTH + 10d, APPLICATION_HEIGHT + 10d, false, 
					new String[]{RS.path(RS.CSS_MAIN), RS.path(RS.CSS_DISPLAY_SHELF)},
					controlBar.createTitleBarItems());
	
			taskbar.setId("taskbar");
			taskbar.setCache(true);
			taskbar.setPrefHeight(TASKBAR_HEIGHT);
			
			centerView = new StackPane();
			centerView.setId("center-view");

			final Controls controls = new Controls(controlBar);
			
			wirelessConnectionView = new WirelessHostConnectionView(controlBar);
			mailConnectionView = new EmailHostConnectionView(controlBar);
	
			// change the center view back to the connection view when connections are lost
			UGateKeeper.DEFAULT.addListener(new IGateKeeperListener() {
				@Override
				public void handle(final UGateKeeperEvent<?> event) {
					playSound(event);
				}
			});
			connectionView.setId("connection-view");
			connectionView.getChildren().addAll(wirelessConnectionView,
					createSeparator(Orientation.VERTICAL), mailConnectionView);
			
			content.setTop(controlBar);
			content.setCenter(centerView);
			content.setBottom(taskbar);
	
			taskbar.getChildren().add(
					createConnectionStatusView(genTaskbarItem(RS.IMG_CONNECT, RS.rbLabel("app.connection.desc"), 0,
							new Runnable() {
								@Override
								public void run() {
									changeCenterView(connectionView, 0);
								}
							})));
			taskbar.getChildren().add(
					genTaskbarItem(RS.IMG_WIRELESS, RS.rbLabel("app.controls.desc"), 1, 
					new Runnable() {
						@Override			
						public void run() {
							changeCenterView(controls, 1);
						}
					}));
			taskbar.getChildren().add(
					genTaskbarItem(RS.IMG_PICS, RS.rbLabel("app.capture.desc"), 2, 
					new Runnable() {
						@Override
						public void run() {
							changeCenterView(
									new DisplayShelf(new File(UGateKeeper.DEFAULT.wirelessWorkingDirectory(
											UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex())),
											350, 350, 0.25, 45, 80,
											DisplayShelf.TOOLBAR_POSITION_TOP, 
											RS.rbLabel("displayshelf.fullsize.tooltip")), 2);
						}
					}));
			taskbar.getChildren().add(
					genTaskbarItem(RS.IMG_GRAPH, RS.rbLabel("app.graph.desc"), 3, 
					new Runnable() {
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
							changeCenterView(chart, 3);
						}
					}));
			taskbar.getChildren().add(genTaskbarItem(RS.IMG_LOGS, RS.rbLabel("app.logs.desc"), 4, 
					new Runnable() {
						@Override
						public void run() {
							loggingView.setEditable(false);
							changeCenterView(loggingView, 4);
						}
					}));
			changeCenterView(connectionView, 0);
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
		Platform.exit();
		//System.exit(0);
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
		node.setPrefHeight(TASKBAR_BUTTON_HEIGHT);
		node.setMaxHeight(Control.USE_PREF_SIZE);
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

	protected ImageView genTaskbarItem(final String iconName,
			final String helpText, final int index, final Runnable action) {
		return genFisheye(iconName, TASKBAR_BUTTON_WIDTH,
				TASKBAR_BUTTON_HEIGHT, helpText, index, taskBarSelectProperty, action);
	}

	public ImageView genFisheye(final String iconName,
			final double width, final double height, 
			final String helpText, final int index, 
			final IntegerProperty selectProperty, final Runnable action) {
		final ImageView node = RS.imgView(iconName);
		node.setCache(true);
		node.setCacheHint(CacheHint.SPEED);
		node.setFitWidth(width);
		node.setFitHeight(height);

		final DropShadow effect = DropShadowBuilder.create().color(
				selectProperty != null && selectProperty.get() == index ? Color.DEEPSKYBLUE :
					Color.TRANSPARENT).build();
		final Reflection effect2 = new Reflection();
		effect.setInput(effect2);
		node.setEffect(effect);
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
				if (selectProperty == null || selectProperty.get() != index) {
					effect.setColor(Color.YELLOW);
				}
			}
		});
		node.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (selectProperty == null || selectProperty.get() != index) {
					effect.setColor(Color.TRANSPARENT);
				} else if (selectProperty.get() == index) {
					effect.setColor(Color.DEEPSKYBLUE);
				}
			}
		});
		if (selectProperty != null) {
			selectProperty.addListener(new ChangeListener<Number>() {
				@Override
				public void changed(final ObservableValue<? extends Number> observable,
						final Number oldValue, final Number newValue) {
					if (newValue.intValue() == index) {
						effect.setColor(Color.DEEPSKYBLUE);
					} else {
						effect.setColor(Color.TRANSPARENT);
					}
				}
			});
		}
		return node;
	}

	/**
	 * Changes the center view above the task bar menu
	 * 
	 * @param node
	 *            the node to change the center view to
	 * @param index the index of the task bar item
	 */
	protected void changeCenterView(final Node node, final int index) {
		centerView.getChildren().setAll(node);
		taskBarSelectProperty.set(index);
	}
	
	/**
	 * Plays a sound for predefined events if preferences is set to do so
	 * 
	 * @param event the event
	 */
	protected void playSound(final UGateKeeperEvent<?> event) {
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
