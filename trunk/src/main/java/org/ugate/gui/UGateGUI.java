package org.ugate.gui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Random;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader.ProgressNotification;
import javafx.application.Preloader.StateChangeNotification;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
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
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.control.Control;
import javafx.scene.control.PasswordField;
import javafx.scene.control.PasswordFieldBuilder;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextAreaBuilder;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFieldBuilder;
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
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.security.sasl.AuthenticationException;

import org.slf4j.Logger;
import org.ugate.IGateKeeperListener;
import org.ugate.RemoteSettings;
import org.ugate.UGateKeeper;
import org.ugate.UGateKeeperEvent;
import org.ugate.UGateUtil;
import org.ugate.gui.components.AppFrame;
import org.ugate.gui.components.BeanPathAdapter;
import org.ugate.gui.components.DisplayShelf;
import org.ugate.gui.components.SimpleCalendar;
import org.ugate.resources.RS;
import org.ugate.service.ActorType;
import org.ugate.service.RoleType;
import org.ugate.service.ServiceManager;
import org.ugate.service.entity.jpa.Actor;
import org.ugate.service.web.SignatureAlgorithm;
import org.ugate.wireless.data.ImageCapture;

/**
 * The main GUI application entry point
 */
public class UGateGUI extends Application {

	private static final Logger log = UGateUtil.getLogger(UGateGUI.class);

	public static final double APPLICATION_WIDTH = 900d;
	public static final double APPLICATION_HEIGHT = 800d;

	private static final double TASKBAR_HEIGHT = 130d;
	private static final double TASKBAR_BUTTON_WIDTH = 100d;
	private static final double TASKBAR_BUTTON_HEIGHT = 100d;

	protected final HBox taskbar = new HBox(10d);
	protected final HBox connectionView = new HBox(10d);
	protected final TextArea loggingView = new TextArea();
	protected ControlBar controlBar;
	protected EmailHostConnectionView mailConnectionView;
	protected WirelessHostConnectionView wirelessConnectionView;
	protected StackPane centerView;
	protected AppFrame applicationFrame;
	protected final IntegerProperty taskBarSelectProperty = new SimpleIntegerProperty(0);
	private static String initErrorTitleRsKey = "app.title.error";
	private static StringBuffer initErrors;
	private final BeanPathAdapter<Actor> actorPA;

	/**
	 * Constructor
	 */
	public UGateGUI() {
		// TextAreaAppender.setTextArea(loggingView);
		actorPA = new BeanPathAdapter<Actor>(new Actor());
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
		} catch (final Throwable t) {
			log.error("Unable to launch GUI", t);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() {
		try {
			notifyPreloader(new ProgressNotification(0.3d));
			ServiceManager.IMPL.open();
		} catch (final Throwable t) {
			log.error("Unable to start services", t);
			try {
				addInitError(RS.rbLabel("app.service.init.error"), t.getMessage());
			} catch (final Throwable t2) {
				log.error("Unable notify user that services failed to start", t2);
			}
		}
		try {
			log.debug("Iniitializing GUI...");
			notifyPreloader(new ProgressNotification(0.7d));
			if (UGateKeeper.DEFAULT.init()) {
				initErrorTitleRsKey = "app.title.action.required";
				addInitError(RS.rbLabel("app.service.com.restart.required"));
			}
			// Text.setFontSmoothingType(FontSmoothingType.LCD);
		} catch (final Throwable t) {
			log.error("Unable to initialize the GUI", t);
			try {
				addInitError(RS.rbLabel("app.gatekeeper.init.error"), t.getMessage());
			} catch (final Throwable t2) {
				log.error("Unable notify user that the gate keeper failed to start", t2);
			}
		}
	}

	/**
	 * Adds initialization error message(s)
	 * 
	 * @param messages
	 *            the message(s) to add
	 */
	private void addInitError(final String... messages) {
		if (initErrors == null) {
			initErrors = new StringBuffer();
		}
		for (final String msg : messages) {
			initErrors.append(msg);
			initErrors.append('\n');
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start(final Stage stage) throws Exception {
		try {
			log.debug("Starting GUI...");
			
			if (initErrors != null && initErrors.length() > 0) {
				final TextArea errorDetails = TextAreaBuilder.create().text(initErrors.toString()).wrapText(true
						).focusTraversable(false).editable(false).opacity(0.7d).build();
				final GuiUtil.DialogService dialogService = GuiUtil.dialogService(stage, "app.title", initErrorTitleRsKey, 
						"close", 550d, 300d, new Service<Void>() {
					@Override
					protected Task<Void> createTask() {
						return new Task<Void>() {
							@Override
							protected Void call() throws Exception {
								// if the dialog shouldn't be closed call super.cancel()
								return null;
							}
						};
					}
				}, errorDetails);
				dialogService.start();
				notifyPreloader(new StateChangeNotification(
						StateChangeNotification.Type.BEFORE_START));
			} else {
				// start the main GUI
				mainStageStart(stage);
			}
			
			log.debug("GUI started");
		} catch (final Throwable t) {
			log.error("Unable to start GUI", t);
			throw new RuntimeException("Unable to start GUI", t);
		}
	}
	
	/**
	 * Starts the primary {@linkplain Stage} but does not call {@linkplain Stage#show()}
	 * 
	 * @param stage the primary {@linkplain Stage}
	 */
	protected void mainStageStart(final Stage stage) {
		notifyPreloader(new ProgressNotification(0.9d));
		stage.getIcons().add(RS.img(RS.IMG_LOGO_16));
		stage.setTitle(RS.rbLabel("app.title"));

		controlBar = new ControlBar(stage, actorPA);
		final BorderPane content = new BorderPane();
		content.setId("main-content");
		content.setEffect(new InnerShadow());
		applicationFrame = new AppFrame(stage, content, APPLICATION_WIDTH, APPLICATION_HEIGHT, APPLICATION_WIDTH + 10d, APPLICATION_HEIGHT + 10d, false, new String[] { RS.path(RS.CSS_MAIN), RS.path(RS.CSS_DISPLAY_SHELF) }, controlBar.createTitleBarItems());

		taskbar.setId("taskbar");
		taskbar.setCache(true);
		taskbar.setPrefHeight(TASKBAR_HEIGHT);

		centerView = new StackPane();
		centerView.setId("center-view");

		final Controls controls = new Controls(controlBar);

		wirelessConnectionView = new WirelessHostConnectionView(controlBar);
		mailConnectionView = new EmailHostConnectionView(controlBar);

		// change the center view back to the connection view when
		// connections are lost
		UGateKeeper.DEFAULT.addListener(new IGateKeeperListener() {
			@Override
			public void handle(final UGateKeeperEvent<?> event) {
				try {
					playSound(event);
				} catch (final Exception e) {
					log.warn("Unable to play sound", e);
				}
			}
		});
		connectionView.setId("connection-view");
		connectionView.getChildren().addAll(wirelessConnectionView, createSeparator(Orientation.VERTICAL), mailConnectionView);

		final VBox bottom = new VBox();
		bottom.setPadding(new Insets(0, 50d, 0, 50d));
		bottom.getChildren().addAll(taskbar, new RemoteNodeToolBar(controlBar, Orientation.HORIZONTAL));
		content.setTop(bottom);

		content.setTop(controlBar);
		// content.setLeft(new RemoteNodeToolBar(controlBar,
		// Orientation.VERTICAL));
		content.setCenter(centerView);
		// content.setBottom(taskbar);
		content.setBottom(bottom);

		taskbar.getChildren().add(createConnectionStatusView(genTaskbarItem(RS.IMG_CONNECT, RS.rbLabel("app.connection.desc"), 0, new Runnable() {
			@Override
			public void run() {
				changeCenterView(connectionView, 0);
			}
		})));
		taskbar.getChildren().add(genTaskbarItem(RS.IMG_WIRELESS, RS.rbLabel("app.controls.desc"), 1, new Runnable() {
			@Override
			public void run() {
				changeCenterView(controls, 1);
			}
		}));
		taskbar.getChildren().add(genTaskbarItem(RS.IMG_PICS, RS.rbLabel("app.capture.desc"), 2, new Runnable() {
			@Override
			public void run() {
				changeCenterView(new DisplayShelf(UGateKeeper.DEFAULT.wirelessWorkingDirectory(UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex()).toFile(), 350, 350, 0.25, 45, 80, DisplayShelf.TOOLBAR_POSITION_TOP, RS.rbLabel("displayshelf.fullsize.tooltip")), 2);
			}
		}));
		taskbar.getChildren().add(genTaskbarItem(RS.IMG_GRAPH, RS.rbLabel("app.graph.desc"), 3, new Runnable() {
			@Override
			public void run() {
				NumberAxis xAxis = new NumberAxis();
				NumberAxis yAxis = new NumberAxis();
				LineChart<Number, Number> chart = new LineChart<Number, Number>(xAxis, yAxis);
				chart.setTitle(RS.rbLabel("graph.alarm.notify"));
				xAxis.setLabel(RS.rbLabel("graph.axis.x"));
				yAxis.setLabel(RS.rbLabel("graph.axis.y"));
				XYChart.Series<Number, Number> sonarTrippedSeries = new XYChart.Series<Number, Number>();
				sonarTrippedSeries.setName(RS.rbLabel("graph.series.alarm"));
				Random random = new Random();
				for (int i = 0; i < 10 + random.nextInt(20); i++) {
					sonarTrippedSeries.getData().add(new XYChart.Data<Number, Number>(10 * i + 10, random.nextDouble() * 150));
				}
				chart.getData().add(sonarTrippedSeries);
				XYChart.Series<Number, Number> manualImageSeries = new XYChart.Series<Number, Number>();
				manualImageSeries.setName(RS.rbLabel("graph.series.activity.manual"));
				for (int i = 0; i < 10 + random.nextInt(20); i++) {
					manualImageSeries.getData().add(new XYChart.Data<Number, Number>(10 * i + 10, random.nextDouble() * 150));
				}
				chart.getData().add(manualImageSeries);

				SimpleCalendar simpleCalender = new SimpleCalendar();
				simpleCalender.setMaxSize(100d, 20d);
				final TextField dateField = new TextField(new SimpleDateFormat("MM/dd/yyyy").format(simpleCalender.dateProperty().get()));
				dateField.setMaxSize(simpleCalender.getMaxWidth(), simpleCalender.getMaxHeight());
				dateField.setEditable(false);
				dateField.setDisable(true);
				simpleCalender.dateProperty().addListener(new ChangeListener<Date>() {

					@Override
					public void changed(ObservableValue<? extends Date> ov, Date oldDate, Date newDate) {
						dateField.setText(new SimpleDateFormat("MM/dd/yyyy").format(newDate));

					}
				});

				final HBox dateBox = new HBox();
				dateBox.setAlignment(Pos.BOTTOM_RIGHT);
				dateBox.getChildren().addAll(dateField, simpleCalender);
				final StackPane history = new StackPane();
				history.setPadding(new Insets(10d));
				history.setAlignment(Pos.BOTTOM_RIGHT);
				history.getChildren().addAll(chart, dateBox);
				changeCenterView(history, 3);
			}
		}));
		taskbar.getChildren().add(genTaskbarItem(RS.IMG_LOGS, RS.rbLabel("app.logs.desc"), 4, new Runnable() {
			@Override
			public void run() {
				loggingView.setEditable(false);
				changeCenterView(loggingView, 4);
			}
		}));
		changeCenterView(connectionView, 0);
		afterStart(stage);
	}

	/**
	 * After {@linkplain Application#start(Stage)} and before {@linkplain Stage#show()}
	 * 
	 * @param stage the primary {@linkplain Stage}
	 */
	protected void afterStart(final Stage stage) {
		// if there are no users then the user needs to be prompted for a username/password
		final boolean isAuth = ServiceManager.IMPL.getCredentialService().getActorCount() > 0;
		String dialogHeaderKey;
		if (isAuth) {
			dialogHeaderKey = "app.dialog.auth";
			log.debug("Presenting authentication dialog prompt");
		} else {
			dialogHeaderKey = "app.dialog.setup";
			log.info("Initializing post installation dialog prompt");
		}
		final TextField username = TextFieldBuilder.create().promptText(RS.rbLabel("app.dialog.username")).build();
		final PasswordField password = PasswordFieldBuilder.create().promptText(RS.rbLabel("app.dialog.password")).build();
		final PasswordField passwordVerify = isAuth ? null : PasswordFieldBuilder.create().promptText(
				RS.rbLabel("app.dialog.password.verify")).build();
		final Button closeBtn = isAuth ? ButtonBuilder.create().text(RS.rbLabel("close")).build() : null;
		final GuiUtil.DialogService dialogService = GuiUtil.dialogService(stage, "app.title", dialogHeaderKey, null, 550d, 300d, new Service<Void>() {
			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {
					private Actor actor;
					@Override
					protected Void call() throws Exception {
						final boolean hasUsername = !username.getText().isEmpty();
						final boolean hasPassword = !password.getText().isEmpty();
						final boolean hasPasswordVerify = passwordVerify == null ? true : !passwordVerify.getText().isEmpty();
						if (hasUsername && hasPassword && hasPasswordVerify) {
							try {
								if (isAuth) {
									actor = ServiceManager.IMPL.getCredentialService().authenticate(
											username.getText(), password.getText());
									if (actor == null) {
										throw new AuthenticationException(RS.rbLabel("app.dialog.auth.error", 
												username.getText()));
									}
								} else {
									if (!password.getText().equals(passwordVerify.getText())) {
										throw new InputMismatchException(RS.rbLabel("app.dialog.setup.error.password.mismatch"));
									}
									actor = 
											ServiceManager.IMPL.getCredentialService().addUser(
													username.getText(), password.getText(), 
													ActorType.newDefaultHost(), 
													RoleType.ADMIN.newRole());
									if (actor == null) {
										throw new IllegalArgumentException("Unable to add user " + username.getText());
									}
								}
								ServiceManager.IMPL.startWebServer(actor.getHost(), SignatureAlgorithm.getDefault());
								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										showApplication(stage, actor);
									}
								});
							} catch (final Throwable t) {
								String errorMsg;
								if (t instanceof AuthenticationException || t instanceof InputMismatchException) {
									errorMsg = t.getMessage();
								} else {
									errorMsg = RS.rbLabel(isAuth ? 
											"app.dialog.auth.error" : "app.dialog.setup.error", username.getText());
									log.warn(errorMsg, t);
								}
								throw new RuntimeException(errorMsg, t);
							}
						} else {
							final String invalidFields = (!hasUsername ? username.getPromptText() : "") + ' ' +
									(!hasPassword ? password.getPromptText() : "") + 
									(!hasPasswordVerify ? passwordVerify.getPromptText() : "");
							throw new RuntimeException(RS.rbLabel("app.dialog.required", invalidFields));
						}
						return null;
					}
				};
			}
		}, closeBtn, username, password, passwordVerify);
		if (closeBtn != null) {
			closeBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(final MouseEvent event) {
					dialogService.hide();
				}
			});	
		}
		dialogService.start();
		notifyPreloader(new StateChangeNotification(
				StateChangeNotification.Type.BEFORE_START));
	}

	/**
	 * Shows the primary {@linkplain Stage} and shows the {@linkplain SystemTray}
	 * 
	 * @param stage the primary {@linkplain Stage}
	 * @param authActor the authorized {@linkplain Actor}
	 */
	protected void showApplication(final Stage stage, final Actor authActor) {
		log.info("Showing GUI");
		if (authActor == null) {
			throw new IllegalArgumentException(
					"Cannot show application without an authenticated user");
		}
		stage.show();
		SystemTray.initSystemTray(stage);
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				actorPA.setBean(authActor);
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop() throws Exception {
		log.info("Exiting application...");
		try {
			ServiceManager.IMPL.close();
		} catch (final Throwable t) {
			log.error("Unable to stop services", t);
		}
		try {
			UGateKeeper.DEFAULT.close();
		} catch (final Throwable t) {
			log.error("Unable to stop gate keeper", t);
		}
		try {
			SystemTray.exit();
			UGateUtil.PLAIN_LOGGER.info("=============================================Exit Complete=============================================");
		} catch (final Throwable t) {
			log.error("Unable to exit the system tray", t);
		}
		Platform.exit();
		// System.exit(0);
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

	protected ImageView genTaskbarItem(final String iconName, final String helpText, final int index, final Runnable action) {
		return genFisheye(iconName, TASKBAR_BUTTON_WIDTH, TASKBAR_BUTTON_HEIGHT, helpText, index, taskBarSelectProperty, action);
	}

	public ImageView genFisheye(final String iconName, final double width, final double height, final String helpText, final int index, final IntegerProperty selectProperty, final Runnable action) {
		final ImageView node = RS.imgView(iconName);
		node.setCache(true);
		node.setCacheHint(CacheHint.SPEED);
		node.setFitWidth(width);
		node.setFitHeight(height);

		final DropShadow effect = DropShadowBuilder.create().color(selectProperty != null && selectProperty.get() == index ? GuiUtil.COLOR_SELECTED : Color.TRANSPARENT).build();
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
					effect.setColor(GuiUtil.COLOR_SELECTING);
				}
			}
		});
		node.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (selectProperty == null || selectProperty.get() != index) {
					effect.setColor(Color.TRANSPARENT);
				} else if (selectProperty.get() == index) {
					effect.setColor(GuiUtil.COLOR_SELECTED);
				}
			}
		});
		if (selectProperty != null) {
			selectProperty.addListener(new ChangeListener<Number>() {
				@Override
				public void changed(final ObservableValue<? extends Number> observable, final Number oldValue, final Number newValue) {
					if (newValue.intValue() == index) {
						effect.setColor(GuiUtil.COLOR_SELECTED);
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
	 * @param index
	 *            the index of the task bar item
	 */
	protected void changeCenterView(final Node node, final int index) {
		centerView.getChildren().setAll(node);
		taskBarSelectProperty.set(index);
	}

	/**
	 * Plays a sound for predefined events if preferences is set to do so
	 * 
	 * @param event
	 *            the event
	 */
	protected void playSound(final UGateKeeperEvent<?> event) {
		final String soundsOn = UGateKeeper.DEFAULT.settingsGet(RemoteSettings.SOUNDS_ON, UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex());
		if (soundsOn == null || soundsOn.isEmpty() || Integer.parseInt(soundsOn) != 1) {
			return;
		}
		if (event.getType() == UGateKeeperEvent.Type.WIRELESS_DATA_TX_STATUS_RESPONSE_UNRECOGNIZED) {
			RS.mediaPlayerConfirm.play();
		} else if (event.getType() == UGateKeeperEvent.Type.WIRELESS_DATA_TX_STATUS_RESPONSE_SUCCESS) {
			RS.mediaPlayerBlip.play();
		} else if (event.getType() == UGateKeeperEvent.Type.WIRELESS_DATA_TX_STATUS_RESPONSE_FAILED) {
			RS.mediaPlayerError.play();
		} else if (event.getType() == UGateKeeperEvent.Type.WIRELESS_DATA_RX_SUCCESS && event.getNewValue() instanceof ImageCapture) {
			RS.mediaPlayerDoorBell.play();
			// TODO : send email with image as attachment (only when the image
			// is captured via alarm trip rather, but not from GUI)
			// UGateKeeper.DEFAULT.emailSend("UGate Tripped",
			// trippedImage.toString(),
			// UGateKeeper.DEFAULT.preferences.get(UGateKeeper.MAIL_USERNAME_KEY),
			// UGateKeeper.DEFAULT.preferences.get(UGateKeeper.MAIL_RECIPIENTS_KEY,
			// UGateKeeper.MAIL_RECIPIENTS_DELIMITER).toArray(new String[]{}),
			// imageFile.filePath);
			RS.mediaPlayerComplete.play();
		} else if (event.getType() == UGateKeeperEvent.Type.WIRELESS_DATA_RX_MULTIPART && event.getNewValue() instanceof ImageCapture) {
			RS.mediaPlayerCam.play();
		}
	}
}
