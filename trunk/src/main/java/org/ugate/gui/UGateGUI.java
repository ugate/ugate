package org.ugate.gui;

import java.io.File;
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
import org.ugate.UGateListener;
import org.ugate.UGateKeeper;
import org.ugate.UGateEvent;
import org.ugate.UGateUtil;
import org.ugate.gui.components.AppFrame;
import org.ugate.gui.components.BeanPathAdapter;
import org.ugate.gui.components.DisplayShelf;
import org.ugate.gui.components.SimpleCalendar;
import org.ugate.gui.components.UGateDirectory;
import org.ugate.gui.view.EmailHostConnectionView;
import org.ugate.gui.view.RemoteNodeToolBar;
import org.ugate.gui.view.WirelessHostConnectionView;
import org.ugate.resources.RS;
import org.ugate.resources.RS.KEYS;
import org.ugate.service.ServiceProvider;
import org.ugate.service.entity.ActorType;
import org.ugate.service.entity.RemoteNodeType;
import org.ugate.service.entity.RoleType;
import org.ugate.service.entity.jpa.Actor;
import org.ugate.service.entity.jpa.Host;
import org.ugate.service.entity.jpa.RemoteNode;
import org.ugate.wireless.data.ImageCapture;

/**
 * The main GUI application entry point
 */
public class UGateGUI extends Application {

	private static final Logger log = UGateUtil.getLogger(UGateGUI.class);

	public static final double APPLICATION_WIDTH = 920d;
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
	private String initErrorHeader;
	private static StringBuffer initErrors;
	private final BeanPathAdapter<Actor> actorPA;
	private final BeanPathAdapter<RemoteNode> remoteNodePA;

	/**
	 * Constructor
	 */
	public UGateGUI() {
		// TextAreaAppender.setTextArea(loggingView);
		actorPA = new BeanPathAdapter<Actor>(ActorType.newDefaultActor());
		remoteNodePA = new BeanPathAdapter<RemoteNode>(
				RemoteNodeType
						.newDefaultRemoteNode(actorPA.getBean().getHost()));
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
			logStartStop(true);
			log.debug("Iniitializing Service Provider...");
			notifyPreloader(new ProgressNotification(0.3d));
			if (!ServiceProvider.IMPL.init() && 
					ServiceProvider.IMPL.getWirelessService().isRequiresRestart()) {
				initErrorHeader = RS.rbLabel(KEYS.APP_TITLE_ACTION_REQUIRED);
				addInitError(RS.rbLabel(KEYS.APP_SERVICE_COM_RESTART_REQUIRED));
			}
			// Text.setFontSmoothingType(FontSmoothingType.LCD);
		} catch (final Throwable t) {
			log.error("Unable to start services", t);
			try {
				addInitError(RS.rbLabel(KEYS.APP_SERVICE_INIT_ERROR), t.getMessage());
			} catch (final Throwable t2) {
				log.error("Unable notify user that services failed to start", t2);
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
				if (initErrorHeader == null) {
					initErrorHeader = RS.rbLabel(KEYS.APP_TITLE_ERROR);
				}
				final GuiUtil.DialogService dialogService = GuiUtil.dialogService(stage, KEYS.APP_TITLE, initErrorHeader, 
						KEYS.CLOSE, 550d, 300d, new Service<Void>() {
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
		stage.setTitle(RS.rbLabel(KEYS.APP_TITLE));

		controlBar = new ControlBar(stage, actorPA, remoteNodePA);
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
		UGateKeeper.DEFAULT.addListener(new UGateListener() {
			@Override
			public void handle(final UGateEvent<?, ?> event) {
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

		taskbar.getChildren().add(createConnectionStatusView(genTaskbarItem(RS.IMG_CONNECT, RS.rbLabel(KEYS.APP_CONNECTION_DESC), 0, new Runnable() {
			@Override
			public void run() {
				changeCenterView(connectionView, 0);
			}
		})));
		taskbar.getChildren().add(genTaskbarItem(RS.IMG_WIRELESS, RS.rbLabel(KEYS.APP_CONTROLS_DESC), 1, new Runnable() {
			@Override
			public void run() {
				changeCenterView(controls, 1);
			}
		}));
		taskbar.getChildren().add(genTaskbarItem(RS.IMG_PICS, RS.rbLabel(KEYS.APP_CAPTURE_DESC), 2, new Runnable() {
			@Override
			public void run() {
				changeCenterView(new DisplayShelf(new File(controlBar.getRemoteNode().getWorkingDir()), 350, 350, 0.25, 45, 80, 
						DisplayShelf.TOOLBAR_POSITION_TOP, RS.rbLabel(KEYS.LABEL_DISPLAYSHELF_FULLSIZE_DESC)), 2);
			}
		}));
		taskbar.getChildren().add(genTaskbarItem(RS.IMG_GRAPH, RS.rbLabel(KEYS.LABEL_GRAPH_DESC), 3, new Runnable() {
			@Override
			public void run() {
				NumberAxis xAxis = new NumberAxis();
				NumberAxis yAxis = new NumberAxis();
				LineChart<Number, Number> chart = new LineChart<Number, Number>(xAxis, yAxis);
				chart.setTitle(RS.rbLabel(KEYS.LABEL_GRAPH_ALARM_NOTIFY));
				xAxis.setLabel(RS.rbLabel(KEYS.LABEL_GRAPH_AXIS_X));
				yAxis.setLabel(RS.rbLabel(KEYS.LABEL_GRAPH_AXIS_Y));
				XYChart.Series<Number, Number> sonarTrippedSeries = new XYChart.Series<Number, Number>();
				sonarTrippedSeries.setName(RS.rbLabel(KEYS.LABEL_GRAPH_SERIES_ALARM));
				Random random = new Random();
				for (int i = 0; i < 10 + random.nextInt(20); i++) {
					sonarTrippedSeries.getData().add(new XYChart.Data<Number, Number>(10 * i + 10, random.nextDouble() * 150));
				}
				chart.getData().add(sonarTrippedSeries);
				XYChart.Series<Number, Number> manualImageSeries = new XYChart.Series<Number, Number>();
				manualImageSeries.setName(RS.rbLabel(KEYS.LABEL_GRAPH_SERIES_ACTIVITY_MANUAL));
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
		taskbar.getChildren().add(genTaskbarItem(RS.IMG_LOGS, RS.rbLabel(KEYS.APP_LOGS_DESC), 4, new Runnable() {
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
		final String appVersion = RS.rbLabel(KEYS.APP_VERSION);
		if (ServiceProvider.IMPL.getCredentialService().addAppInfoIfNeeded(RS.rbLabel(KEYS.APP_VERSION))) {
			log.info("Persisted new application information for version " + appVersion);
		}
		// if there are no users then the user needs to be prompted for a username/password
		final boolean isAuth = ServiceProvider.IMPL.getCredentialService().getActorCount() > 0;
		String dialogHeader;
		if (isAuth) {
			dialogHeader = RS.rbLabel(KEYS.APP_DIALOG_AUTH);
			log.debug("Presenting authentication dialog prompt");
		} else {
			dialogHeader = RS.rbLabel(KEYS.APP_DIALOG_SETUP);
			log.info("Initializing post installation dialog prompt");
		}
		final UGateDirectory wirelessRemoteNodeDirBox = isAuth ? null : new UGateDirectory(stage);
		if (!isAuth) {
			wirelessRemoteNodeDirBox.getTextField().setPromptText(RS.rbLabel(KEYS.WIRELESS_WORKING_DIR));
		}
		final TextField wirelessHostAddy = isAuth ? null : TextFieldBuilder.create().promptText(RS.rbLabel(KEYS.WIRELESS_HOST_ADDY)).build();
		final TextField wirelessRemoteNodeAddy = isAuth ? null : TextFieldBuilder.create().promptText(RS.rbLabel(KEYS.WIRELESS_NODE_REMOTE_ADDY)).build();
		//final FileChooser wirelessRemoteNodeDir = new FileChooser();
		//wirelessRemoteNodeDir.setTitle(RS.rbLabel(KEYS.WIRELESS_WORKING_DIR));
		final TextField username = TextFieldBuilder.create().promptText(RS.rbLabel(KEYS.APP_DIALOG_USERNAME)).build();
		final PasswordField password = PasswordFieldBuilder.create().promptText(RS.rbLabel(KEYS.APP_DIALOG_PWD)).build();
		final PasswordField passwordVerify = isAuth ? null : PasswordFieldBuilder.create().promptText(
				RS.rbLabel(KEYS.APP_DIALOG_PWD_VERIFY)).build();
		final Button closeBtn = ButtonBuilder.create().text(RS.rbLabel(KEYS.CLOSE)).build();
		final GuiUtil.DialogService dialogService = GuiUtil.dialogService(stage, KEYS.APP_TITLE, dialogHeader, null, 550d, isAuth ? 200d : 400d, new Service<Void>() {
			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {
					private Actor actor;
					@Override
					protected Void call() throws Exception {
						final boolean hasWirelessHostAddy = isAuth ? true : !wirelessHostAddy.getText().isEmpty();
						final boolean hasWirelessRemoteNodeAddy = isAuth ? true : !wirelessRemoteNodeAddy.getText().isEmpty();
						final boolean hasWirelessRemoteNodeDir = isAuth ? true : !wirelessRemoteNodeDirBox.getTextField().getText().isEmpty();
						final boolean hasUsername = !username.getText().isEmpty();
						final boolean hasPassword = !password.getText().isEmpty();
						final boolean hasPasswordVerify = isAuth ? true : !passwordVerify.getText().isEmpty();
						if (hasWirelessRemoteNodeAddy && hasWirelessRemoteNodeDir && hasUsername && hasPassword && hasPasswordVerify) {
							try {
								if (isAuth) {
									actor = ServiceProvider.IMPL.getCredentialService().authenticate(
											username.getText(), password.getText());
									if (actor == null) {
										throw new AuthenticationException(RS.rbLabel(KEYS.APP_DIALOG_AUTH_ERROR, 
												username.getText()));
									}
								} else {
									if (!password.getText().equals(passwordVerify.getText())) {
										throw new InputMismatchException(RS.rbLabel(
												KEYS.APP_DIALOG_SETUP_ERROR_PWD_MISMATCH));
									}
									final Host host = ActorType.newDefaultHost();
									host.setComAddress(wirelessHostAddy.getText());
									host.getRemoteNodes().iterator().next().setAddress(wirelessRemoteNodeAddy.getText());
									host.getRemoteNodes().iterator().next().setWorkingDir(
											wirelessRemoteNodeDirBox.getTextField().getText());
									actor = ServiceProvider.IMPL.getCredentialService().addUser(
													username.getText(), password.getText(), host, 
													RoleType.ADMIN.newRole());
									if (actor == null) {
										throw new IllegalArgumentException("Unable to add user " + username.getText());
									}
								}
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
											KEYS.APP_DIALOG_AUTH_ERROR : KEYS.APP_DIALOG_SETUP_ERROR, username.getText());
									log.warn(errorMsg, t);
								}
								throw new RuntimeException(errorMsg, t);
							}
						} else {
							final String invalidFields = (!hasWirelessHostAddy ? wirelessHostAddy.getPromptText() : "") + ' ' +
									(!hasWirelessRemoteNodeAddy ? wirelessRemoteNodeAddy.getPromptText() : "") +
									(!hasWirelessRemoteNodeDir ? wirelessRemoteNodeDirBox.getTextField().getPromptText() : "") +
									(!hasUsername ? username.getPromptText() : "") + 
									(!hasPassword ? password.getPromptText() : "") + 
									(!hasPasswordVerify ? passwordVerify.getPromptText() : "");
							throw new RuntimeException(RS.rbLabel(KEYS.APP_DIALOG_REQUIRED, invalidFields));
						}
						return null;
					}
				};
			}
		}, closeBtn, wirelessHostAddy, wirelessRemoteNodeAddy, wirelessRemoteNodeDirBox, username, password, passwordVerify);
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
				remoteNodePA.setBean(authActor.getHost().getRemoteNodes()
						.iterator().next());
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
			ServiceProvider.IMPL.disconnect();
		} catch (final Throwable t) {
			log.error(
					"Unable to disconnect from "
							+ ServiceProvider.class.getName(), t);
		}
		try {
			SystemTray.exit();
			logStartStop(false);
		} catch (final Throwable t) {
			log.error("Unable to exit the system tray", t);
		}
		Platform.exit();
		// System.exit(0);
	}

	private static void logStartStop(final boolean start) {
		final String msg = start ? "Application Starting" : "Application Exited";
		UGateUtil.PLAIN_LOGGER
				.info("=============================================" + msg
						+ "=============================================");
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
	 * Plays a sound for predefined events if the
	 * {@linkplain RemoteNode#getDeviceSoundsOn()} for the
	 * {@linkplain UGateEvent#getSource()} (i.e. {@linkplain RemoteNode})
	 * 
	 * @param event
	 *            the {@linkplain UGateEvent}
	 */
	protected void playSound(final UGateEvent<?, ?> event) {
		if (!(event.getSource() instanceof RemoteNode)) {
			return;
		}
		final RemoteNode rn = (RemoteNode) event.getSource();
		if (rn == null || rn.getDeviceSoundsOn() != 1) {
			return;
		}
		if (event.getType() == UGateEvent.Type.WIRELESS_DATA_TX_STATUS_RESPONSE_UNRECOGNIZED) {
			RS.mediaPlayerConfirm.play();
		} else if (event.getType() == UGateEvent.Type.WIRELESS_DATA_TX_STATUS_RESPONSE_SUCCESS) {
			RS.mediaPlayerBlip.play();
		} else if (event.getType() == UGateEvent.Type.WIRELESS_DATA_TX_STATUS_RESPONSE_FAILED) {
			RS.mediaPlayerError.play();
		} else if (event.getType() == UGateEvent.Type.WIRELESS_DATA_RX_SUCCESS && event.getNewValue() instanceof ImageCapture) {
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
		} else if (event.getType() == UGateEvent.Type.WIRELESS_DATA_RX_MULTIPART && event.getNewValue() instanceof ImageCapture) {
			RS.mediaPlayerCam.play();
		}
	}
}
