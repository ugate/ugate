package org.ugate.gui;

import java.util.Set;

import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.DropShadowBuilder;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ugate.Command;
import org.ugate.UGateEvent;
import org.ugate.UGateEvent.Type;
import org.ugate.UGateKeeper;
import org.ugate.UGateListener;
import org.ugate.UGateUtil;
import org.ugate.gui.components.BeanPathAdapter;
import org.ugate.gui.components.StatusIcon;
import org.ugate.gui.view.SensorReading;
import org.ugate.resources.RS;
import org.ugate.resources.RS.KEYS;
import org.ugate.service.ServiceProvider;
import org.ugate.service.entity.ActorType;
import org.ugate.service.entity.Model;
import org.ugate.service.entity.jpa.Actor;
import org.ugate.service.entity.jpa.Host;
import org.ugate.service.entity.jpa.RemoteNode;

/**
 * Main menu control bar
 */
public class ControlBar extends ToolBar {

	private final ValidatorFactory validationFactory;
	private final BeanPathAdapter<Actor> actorPA;
	private final BeanPathAdapter<RemoteNode> remoteNodePA;
	private final ScrollPane helpTextPane;
	private final ImageView defaultUserImg;
	private final StatusIcon cnctStatusWireless;
	private final StatusIcon cnctStatusWeb;
	private final StatusIcon cnctStatusEmail;
	private final Label helpText;
	private final Timeline settingsSetTimeline;
	private final SensorReading sensorReading;
	
	private static final Logger log = LoggerFactory.getLogger(ControlBar.class);
	private static final double CONNECTION_STATUS_SIZE = 32d;
	public static final Color ATTENTION_COLOR = Color.YELLOW;
	public static final int HELP_TEXT_COLOR_CHANGE_CYCLE_COUNT = 8;
	public static final double CHILD_SPACING = 10d;
	public static final double CHILD_PADDING = 5d;
	public static final Insets PADDING_INSETS = new Insets(CHILD_PADDING, CHILD_PADDING, 0, CHILD_PADDING);

	private final Stage stage;

	/**
	 * Constructor
	 * 
	 * @param stage
	 *            the {@linkplain Stage}
	 * @param actorPA
	 *            the {@linkplain BeanPathAdapter} for the {@linkplain Actor}
	 * @param remoteNodePA
	 *            the {@linkplain BeanPathAdapter} for the
	 *            {@linkplain RemoteNode}
	 */
	public ControlBar(final Stage stage, final BeanPathAdapter<Actor> actorPA, 
			final BeanPathAdapter<RemoteNode> remoteNodePA) {
		validationFactory = Validation.buildDefaultValidatorFactory();
		setId("control-bar");
		this.stage = stage;
		this.actorPA = actorPA;
		this.remoteNodePA = remoteNodePA;
		final DropShadow dbDS = new DropShadow();
		final DropShadow settingsDS = new DropShadow();
		this.settingsSetTimeline = GuiUtil.createDropShadowColorIndicatorTimeline(
				settingsDS, ATTENTION_COLOR, Color.BLACK, Timeline.INDEFINITE);
		// help view
		final DropShadow helpTextDropShadow = new DropShadow();
		helpTextDropShadow.setRadius(50d);
		final Timeline helpTextTimeline = GuiUtil.createDropShadowColorIndicatorTimeline(
				helpTextDropShadow, ATTENTION_COLOR, Color.BLACK.brighter(), HELP_TEXT_COLOR_CHANGE_CYCLE_COUNT);
		helpTextPane = new ScrollPane();
		helpTextPane.getStyleClass().add("text-area-help");
		//helpTextPane.setPrefHeight(40d);
		helpTextPane.setPrefWidth(300d);
		helpTextPane.setEffect(helpTextDropShadow);
		helpText = new Label(RS.rbLabel(UGateUtil.HELP_TEXT_DEFAULT_KEY));
		helpText.setWrapText(true);
		helpText.setPrefWidth(helpTextPane.getPrefWidth() - 35d);
		helpText.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, 
					String newValue) {
				helpTextTimeline.stop();
				if (newValue != null && newValue.length() > 0 && 
						!newValue.equals(RS.rbLabel(UGateUtil.HELP_TEXT_DEFAULT_KEY))) {
					helpTextTimeline.play();
				}
			}
		});
		helpTextPane.setContent(helpText);
		defaultUserImg = RS.imgView(RS.IMG_LOCK);
		defaultUserImg.setCursor(Cursor.HAND);
		defaultUserImg.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(final MouseEvent event) {
				if (GuiUtil.isPrimaryPress(event)) {
					setDefaultActor(defaultUserImg.getImage() == RS
							.img(RS.IMG_LOCK) ? true : false, true);
				}
			}
		});
		addHelpTextTrigger(defaultUserImg, RS.rbLabel(KEYS.APP_DIALOG_DEFAULT_USER));
	
		cnctStatusWireless = new StatusIcon(RS.imgView(RS.IMG_WIRELESS_ICON,
				CONNECTION_STATUS_SIZE, CONNECTION_STATUS_SIZE, true, true), GuiUtil.COLOR_OFF);
		addServiceBehavior(cnctStatusWireless, null, ServiceProvider.Type.WIRELESS, null);
		cnctStatusWeb = new StatusIcon(RS.imgView(RS.IMG_WEB_ICON,
				CONNECTION_STATUS_SIZE, CONNECTION_STATUS_SIZE, true, true), GuiUtil.COLOR_OFF);
		addServiceBehavior(cnctStatusWeb, null, ServiceProvider.Type.WEB, null);
		cnctStatusEmail = new StatusIcon(RS.imgView(RS.IMG_EMAIL_ICON, CONNECTION_STATUS_SIZE,
				CONNECTION_STATUS_SIZE, true, true), GuiUtil.COLOR_OFF);
		addServiceBehavior(cnctStatusEmail, null, ServiceProvider.Type.EMAIL, null);
		
		// TODO : need to set camera resolution before calling Command.CAM_TAKE_PIC?
		final DropShadow ds = new DropShadow();
		final ImageView camTakeQvga = RS.imgView(RS.IMG_CAM_QVGA);
		addServiceBehavior(camTakeQvga, Command.CAM_TAKE_PIC, null, KEYS.CAM_ACTION_QVGA);
		camTakeQvga.setEffect(ds);
		final ImageView camTakeVga = RS.imgView(RS.IMG_CAM_VGA);
		addServiceBehavior(camTakeVga, Command.CAM_TAKE_PIC, null, KEYS.CAM_ACTION_VGA);
		camTakeVga.setEffect(ds);
		final ImageView settingsSave = RS.imgView(RS.IMG_DB_SAVE);
		settingsSave.setCursor(Cursor.HAND);
		settingsSave.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(final MouseEvent event) {
				if (GuiUtil.isPrimaryPress(event)) {
					ServiceProvider.IMPL.getCredentialService().mergeActor(getActor());
				}
			}
	    });
		addHelpTextTrigger(settingsSave, RS.rbLabel(KEYS.SETTINGS_SAVE));
		settingsSave.setEffect(dbDS);
		final ImageView settingsSet = RS.imgView(RS.IMG_SETTINGS_SET);
		addServiceBehavior(settingsSet, Command.SENSOR_SET_SETTINGS, null, KEYS.SETTINGS_SEND);
		settingsSet.setEffect(settingsDS);
		final ImageView settingsGet = RS.imgView(RS.IMG_SETTINGS_GET);
		addServiceBehavior(settingsGet, Command.SENSOR_GET_SETTINGS, null, KEYS.SETTINGS_RECEIVE);
		settingsGet.setEffect(ds);
		final ImageView readingsGet = RS.imgView(RS.IMG_READINGS_GET);
		addServiceBehavior(readingsGet, Command.SENSOR_GET_READINGS, null, KEYS.SENSOR_READINGS_GET);
		readingsGet.setEffect(ds);

		sensorReading = new SensorReading(this, Orientation.HORIZONTAL);
		
		// add the menu items
		getItems().addAll(camTakeQvga, camTakeVga, settingsSave, settingsSet,
				settingsGet, readingsGet, new Separator(Orientation.VERTICAL), 
				sensorReading);
		// show a visual indication that the settings need updated
		UGateKeeper.DEFAULT.addListener(new UGateListener() {
			@Override
			public void handle(final UGateEvent<?, ?> event) {
				setHelpText(event.getMessageString());
				if (event.getType() == UGateEvent.Type.ACTOR_COMMIT) {
					validate(getActor());
				} else if (event.getType() == UGateEvent.Type.HOST_COMMIT) {
					validate(getActor().getHost());
				} else if (event.getType() == UGateEvent.Type.WIRELESS_REMOTE_NODE_COMMIT) {
					validate(getRemoteNode());
				} else if (event.getType() == UGateEvent.Type.ACTOR_COMMITTED) {
					// need to update the existing dirty actor
					getActorPA().setBean((Actor) event.getSource());
				} else if (event.getType() == UGateEvent.Type.HOST_COMMITTED) {
					// need to update the existing dirty host
					getActor().setHost((Host) event.getSource());
					getActorPA().setBean(getActor());
				} else if (event.getType() == UGateEvent.Type.WIRELESS_REMOTE_NODE_COMMITTED) {
					final RemoteNode rn = (RemoteNode) event.getSource();
					if (!rn.isDeviceSynchronized() && rn.isDeviceAutoSynchronize()) {
						// automatically send the changes to the remote node
						// (consume event so no other notifications for the
						// event will be processed)
						event.setConsumed(true);
						createCommandService(Command.SENSOR_SET_SETTINGS, true);
					} else if (!rn.isDeviceSynchronized() && 
							rn.getAddress().equalsIgnoreCase(getRemoteNode().getAddress())) {
						validateRemoteNodeSynchronization();
					}
				} else if (event.getType() == UGateEvent.Type.WIRELESS_DATA_ALL_TX_SUCCESS) {
					final RemoteNode rn = (RemoteNode) event.getSource();
					if (rn.getAddress().equalsIgnoreCase(getRemoteNode().getAddress())) {
						settingsSetTimeline.stop();
					}
				} else if (event.getType() == UGateEvent.Type.WIRELESS_HOST_CONNECTING) {
					cnctStatusWireless.setStatusFill(Duration.seconds(1), 
							GuiUtil.COLOR_OPEN, GuiUtil.COLOR_CLOSED, 
							Timeline.INDEFINITE);
				} else if (event.getType() == UGateEvent.Type.WIRELESS_HOST_CONNECTED) {
					cnctStatusWireless.setStatusFill(GuiUtil.COLOR_ON);
					ServiceProvider.IMPL.getCredentialService().mergeHost(getActor().getHost());
				} else if (event.getType() == UGateEvent.Type.WIRELESS_HOST_CONNECT_FAILED) {
					cnctStatusWireless.setStatusFill(GuiUtil.COLOR_OFF);
				} else if (event.getType() == UGateEvent.Type.WIRELESS_HOST_DISCONNECTING) {
					cnctStatusWireless.setStatusFill(Duration.seconds(1), 
							GuiUtil.COLOR_OFF, GuiUtil.COLOR_CLOSED, 
							Timeline.INDEFINITE);
				} else if (event.getType() == UGateEvent.Type.WIRELESS_HOST_DISCONNECTED) {
					cnctStatusWireless.setStatusFill(GuiUtil.COLOR_OFF);
				} else if (event.getType() == UGateEvent.Type.EMAIL_CONNECTING) {
					cnctStatusEmail.setStatusFill(Duration.seconds(1), 
							GuiUtil.COLOR_OPEN, GuiUtil.COLOR_CLOSED, 
							Timeline.INDEFINITE);
				} else if (event.getType() == UGateEvent.Type.EMAIL_CONNECTED) {
					cnctStatusEmail.setStatusFill(GuiUtil.COLOR_ON);
				} else if (event.getType() == UGateEvent.Type.EMAIL_CONNECT_FAILED) {
					cnctStatusEmail.setStatusFill(GuiUtil.COLOR_OFF);
				} else if (event.getType() == UGateEvent.Type.EMAIL_DISCONNECTING) {
					cnctStatusEmail.setStatusFill(Duration.seconds(1), 
							GuiUtil.COLOR_OFF, GuiUtil.COLOR_CLOSED, 
							Timeline.INDEFINITE);
				} else if (event.getType() == UGateEvent.Type.EMAIL_DISCONNECTED || 
						event.getType() == UGateEvent.Type.EMAIL_CLOSED || 
						event.getType() == UGateEvent.Type.EMAIL_AUTH_FAILED) {
					cnctStatusEmail.setStatusFill(GuiUtil.COLOR_OFF);
				} else if (event.getType() == UGateEvent.Type.WEB_INITIALIZE) {
					cnctStatusWeb.setStatusFill(Duration.seconds(1), 
							GuiUtil.COLOR_SELECTED, GuiUtil.COLOR_CLOSED, 
							Timeline.INDEFINITE);
				} else if (event.getType() == UGateEvent.Type.WEB_CONNECTING) {
					cnctStatusWeb.setStatusFill(Duration.seconds(1), 
							GuiUtil.COLOR_OPEN, GuiUtil.COLOR_CLOSED, 
							Timeline.INDEFINITE);
				} else if (event.getType() == UGateEvent.Type.WEB_CONNECTED) {
					cnctStatusWeb.setStatusFill(GuiUtil.COLOR_ON);
				} else if (event.getType() == UGateEvent.Type.WEB_CONNECT_FAILED || 
						event.getType() == UGateEvent.Type.WEB_INITIALIZE_FAILED) {
					cnctStatusWeb.setStatusFill(GuiUtil.COLOR_OFF);
				} else if (event.getType() == UGateEvent.Type.WEB_DISCONNECTING) {
					cnctStatusWeb.setStatusFill(Duration.seconds(1), 
							GuiUtil.COLOR_OFF, GuiUtil.COLOR_CLOSED, 
							Timeline.INDEFINITE);
				} else if (event.getType() == UGateEvent.Type.WEB_DISCONNECTED) {
					cnctStatusWeb.setStatusFill(GuiUtil.COLOR_OFF);
				}
			}
		});
		validateRemoteNodeSynchronization();
	}

	/**
	 * Adds a {@linkplain MouseEvent.MOUSE_PRESSED} listener to the
	 * {@linkplain Node} that will execute a {@linkplain Command} and/or
	 * {@linkplain ServiceProvider.Type}
	 * 
	 * @param node
	 *            the {@linkplain Node} to add the listener to
	 * @param command
	 *            the optional {@linkplain Command} to execute
	 * @param serviceType
	 *            the optional {@linkplain ServiceProvider.Type} to execute
	 * @param helpKey
	 *            the optional {@linkplain KEYS} for the
	 *            {@linkplain ControlBar#setHelpText(String)}
	 */
	public void addServiceBehavior(final Node node, final Command command,
			final ServiceProvider.Type serviceType, final KEYS helpKey) {
		if (command == null && serviceType == null) {
			return;
		}
		node.setCursor(Cursor.HAND);
		node.addEventHandler(MouseEvent.MOUSE_PRESSED,
				new EventHandler<MouseEvent>() {
					@Override
					public void handle(final MouseEvent event) {
						if (GuiUtil.isPrimaryPress(event)) {
							if (command != null) {
								createCommandService(command, true);
							}
							if (serviceType == ServiceProvider.Type.WIRELESS) {
								createWirelessConnectionService().start();
							} else if (serviceType == ServiceProvider.Type.WEB) {
								createWebConnectionService().start();
							} else if (serviceType == ServiceProvider.Type.EMAIL) {
								createEmailConnectionService().start();
							}
						}
					}
				});
		if (helpKey != null) {
			addHelpTextTrigger(node, RS.rbLabel(helpKey));
		}
	}

	/**
	 * Validates a {@linkplain Model} and displays any validation error messages
	 * 
	 * @param model
	 *            the {@linkplain Model}
	 * @return any validation error messages
	 */
	public <T extends Model> String validate(final T model) {
		final Set<ConstraintViolation<T>> cvs = validationFactory
				.getValidator().validate(model);
		String s = "";
		if (!cvs.isEmpty()) {
			for (final ConstraintViolation<T> cv : cvs) {
				s += cv.getPropertyPath().toString()
						+ ": "
						+ (cv.getMessage() != null ? cv.getMessage() : cv
								.getInvalidValue()) + '\n';
			}
			setHelpText(s);
			UGateKeeper.DEFAULT.notifyListeners(new UGateEvent<>(cvs,
					Type.VALIDATION_FAILED, false));
		}
		return s;
	}

	/**
	 * Notifies the user that the {@linkplain #getRemoteNode()} needs to be
	 * sent/synchronized with the remote device (if needed)
	 */
	public void validateRemoteNodeSynchronization() {
		if (getRemoteNode().isDeviceSynchronized()) {
			settingsSetTimeline.stop();
		} else {
			settingsSetTimeline.play();
		}
	}

	/**
	 * @return the menu bar items related to the {@linkplain ControlBar}
	 */
	public Region createTitleBarItems() {
		final HBox menu = new HBox(10d);
		menu.getStyleClass().add("title-bar-menu");
		menu.setAlignment(Pos.CENTER);
		menu.setPadding(new Insets(0, 50d, 0, 50d));
		final ImageView helpButton = RS.imgView(RS.IMG_HELP);
		final DropShadow effect = DropShadowBuilder.create().color(GuiUtil.COLOR_SELECTED).build();
		helpButton.setCursor(Cursor.HAND);
		helpButton.setEffect(effect);
		helpButton.addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>() {
			@Override
			public void handle(final MouseEvent event) {
				if (event.getEventType() == MouseEvent.MOUSE_ENTERED) {
					effect.setColor(GuiUtil.COLOR_SELECTING);
				} else if (event.getEventType() == MouseEvent.MOUSE_EXITED) {
					effect.setColor(GuiUtil.COLOR_SELECTED);
				} else if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
					setHelpText(null);
				}
			}
		});
		menu.getChildren().addAll(helpButton, helpTextPane, cnctStatusWireless, 
				cnctStatusWeb, cnctStatusEmail, defaultUserImg);
	    return menu;
	}
	
	/**
	 * Creates a {@linkplain Service} for the {@linkplain Command} that will
	 * show a progress indicator preventing further action until the command
	 * execution has completed.
	 * 
	 * @param command
	 *            the {@linkplain Command}
	 * @param start
	 *            true to start the {@linkplain Service} immediately after
	 *            creation
	 * @return the {@linkplain Service}
	 */
	public Service<Boolean> createCommandService(final Command command, final boolean start) {
		if (!ServiceProvider.IMPL.getWirelessService().isConnected()) {
			setHelpText(RS.rbLabel(KEYS.SERVICE_WIRELESS_CONNECTION_REQUIRED));
			return null;
		}
		setHelpText(null);
		final Service<Boolean> service = GuiUtil.alertProgress(stage, new Task<Boolean>() {
			@Override
			protected Boolean call() throws Exception {
				try {
					if (command == Command.SENSOR_GET_READINGS) {
						if (!ServiceProvider.IMPL.getWirelessService().sendData(
								getRemoteNode(), 
								Command.SENSOR_GET_READINGS)) {
							setHelpText(RS.rbLabel(KEYS.SENSOR_READINGS_FAILED,
									getRemoteNode().getAddress()));
							return false;
						}
					} else if (command == Command.SENSOR_SET_SETTINGS) {
						if (!ServiceProvider.IMPL.getWirelessService().sendSettings(
								getRemoteNode())) {
							setHelpText(RS.rbLabel(KEYS.SETTINGS_SEND_FAILED,
									getRemoteNode().getAddress()));
							return false;
						} else if (!getRemoteNode().isDeviceSynchronized()) {
							getRemoteNode().setDeviceSynchronized(true);
						}
					} else if (command == Command.GATE_TOGGLE_OPEN_CLOSE) {
						if (!ServiceProvider.IMPL.getWirelessService().sendData(
								getRemoteNode(), 
								Command.GATE_TOGGLE_OPEN_CLOSE)) {
							setHelpText(RS.rbLabel(KEYS.GATE_TOGGLE_FAILED,
									getRemoteNode().getAddress()));
							return false;
						}
					} else {
						log.warn(String.format("%1$s is not a valid command for %2$s", 
								command, Controls.class.getName()));
						return false;
					}
				} catch (final Throwable t) {
					setHelpText(RS.rbLabel(KEYS.SERVICE_CMD_FAILED));
					log.error("Unable to execute " + command, t);
					return false;
				}
				return true;
			}
		});
		if (start) {
			service.start();
		}
		return service;
	}

	/**
	 * Creates a web server connection {@linkplain Service} that will start or
	 * stop the web server in a background process.
	 * 
	 * @return the {@linkplain Service}
	 */
	public Service<Boolean> createWebConnectionService() {
		setHelpText(null);
		return new Service<Boolean>() {
			@Override
			protected Task<Boolean> createTask() {
				return new Task<Boolean>() {
					@Override
					protected Boolean call() throws Exception {
						try {
							if (ServiceProvider.IMPL.getWebService()
									.isRunning()) {
								ServiceProvider.IMPL.getWebService().stop();
							} else {
								ServiceProvider.IMPL.getWebService().start(
										getActor().getHost(), null);
							}
						} catch (final Throwable t) {
							setHelpText(RS.rbLabel(KEYS.SERVICE_WIRELESS_FAILED));
							log.error("Unable to establish a wireless connection", t);
						}
						return false;
					}
				};
			}
		};
	}

	/**
	 * Creates a wireless connection {@linkplain Service} that will show a
	 * progress indicator preventing further action until the wireless
	 * connection has been established. When the wireless connection is already
	 * connected an attempt will be made to reconnect.
	 * 
	 * @return the {@linkplain Service}
	 */
	public Service<Boolean> createWirelessConnectionService() {
		setHelpText(null);
		return GuiUtil.alertProgress(stage, new Task<Boolean>() {
			@Override
			protected Boolean call() throws Exception {
				try {
					// establish wireless connection (blocking)
					ServiceProvider.IMPL.getWirelessService().connect(
							getActor().getHost(), getRemoteNode());
				} catch (final Throwable t) {
					setHelpText(RS.rbLabel(KEYS.SERVICE_WIRELESS_FAILED));
					log.error("Unable to establish a wireless connection", t);
				}
				return false;
			}
		});
	}
	
	/**
	 * Creates an email connection {@linkplain Service} that will connect to an
	 * email server in a background process.
	 * 
	 * @return the {@linkplain Service}
	 */
	public Service<Boolean> createEmailConnectionService() {
		setHelpText(null);
		return new Service<Boolean>() {
			@Override
			protected Task<Boolean> createTask() {
				return new Task<Boolean>() {
					@Override
					protected Boolean call() throws Exception {
						try {
							// establish wireless connection (blocking)
							ServiceProvider.IMPL.getEmailService().connect(getActor().getHost());
						} catch (final Throwable t) {
							setHelpText(RS.rbLabel(KEYS.SERVICE_EMAIL_FAILED));
							log.error("Unable to establish a wireless connection", t);
						}
						return false;
					}
				};
			}
		};
	}
	
	/**
	 * Adds the help from the {@linkplain StringProperty} text when the
	 * {@linkplain Node} is right clicked
	 * 
	 * @param node
	 *            the {@linkplain Node} to trigger the text
	 * @param stringProperty
	 *            the {@linkplain StringProperty} to show as the text value
	 */
	public void addHelpTextTrigger(final Node node, final StringProperty stringProperty) {
		node.setOnMousePressed(new HelpTextMouseHandler(stringProperty, null));
	}
	
	/**
	 * Adds the help text when the {@linkplain Node} is right clicked
	 * 
	 * @param node
	 *            the {@linkplain Node} to trigger the text
	 * @param text
	 *            the text to show
	 */
	public void addHelpTextTrigger(final Node node, final String text) {
		node.setOnMousePressed(new HelpTextMouseHandler(null, text));
	}
	
	/**
	 * @return the current help text
	 */
	public String getHelpText() {
		return helpText.getText();
	}
	
	/**
	 * Sets the help text
	 * 
	 * @param text
	 *            the help text to set
	 */
	public void setHelpText(final String text) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				helpText.setText(text == null || text.length() == 0 ? RS.rbLabel(UGateUtil.HELP_TEXT_DEFAULT_KEY) : text);
			}
		});
	}

	/**
	 * @return the {@linkplain SensorReading}
	 */
	public SensorReading getSensorReadingsView() {
		return sensorReading;
	}

	/**
	 * @return the {@linkplain RemoteNode} {@linkplain BeanPathAdapter}
	 */
	public BeanPathAdapter<RemoteNode> getRemoteNodePA() {
		return remoteNodePA;
	}
	
	/**
	 * Shortcut to {@linkplain BeanPathAdapter#getBean()} for the
	 * {@linkplain #getRemoteNodePA()}
	 * 
	 * @return the {@linkplain RemoteNode}
	 */
	public RemoteNode getRemoteNode() {
		return getRemoteNodePA().getBean();
	}

	/**
	 * @return the {@linkplain Actor} {@linkplain BeanPathAdapter}
	 */
	public BeanPathAdapter<Actor> getActorPA() {
		return actorPA;
	}

	/**
	 * Sets the current {@linkplain Actor} as the default one (bypassing the
	 * need for authentication when the application starts)
	 * 
	 * @param isDefault
	 *            true when the current {@linkplain Actor} is the default one
	 * @param persist
	 *            true to persist the default option
	 */
	public void setDefaultActor(final boolean isDefault, final boolean persist) {
		try {
			if (persist) {
				ServiceProvider.IMPL.getCredentialService().setDefaultActor(
						isDefault ? getActor() : null,
						RS.rbLabel(KEYS.APP_VERSION));
			}
			defaultUserImg.setImage(RS.img(isDefault ? RS.IMG_UNLOCK
					: RS.IMG_LOCK));
		} catch (final Throwable t) {
			log.warn("Unable to set the default " + Actor.class.getName(), t);
		}
	}

	/**
	 * Shortcut to {@linkplain BeanPathAdapter#getBean()} for the
	 * {@linkplain #getActorPA()}
	 * 
	 * @return the {@linkplain Actor}
	 */
	public Actor getActor() {
		return getActorPA().getBean();
	}

	/**
	 * @see #bindTo(ActorType, Property, Class)
	 */
	public void bindBidirectional(final ActorType actorType, 
			final BooleanProperty property) {
		bindTo(actorType, property, Boolean.class);
	}

	/**
	 * @see #bindTo(ActorType, Property, Class)
	 */
	public void bindTo(final ActorType actorType, 
			final StringProperty property) {
		bindTo(actorType, property, String.class);
	}

	/**
	 * @see #bindTo(ActorType, Property, Class)
	 */
	public void bindTo(final ActorType actorType,
			final Property<Number> property) {
		bindTo(actorType, property, null);
	}

	/**
	 * Shortcut to
	 * {@linkplain BeanPathAdapter#bindBidirectional(String, Property, Class)}
	 * for the {@linkplain #getActorPA()}
	 * 
	 * @param actorType
	 *            the {@linkplain ActorType} that contains the field path key
	 * @param property
	 *            the {@linkplain Property}
	 * @param propertyValueType
	 *            the class type of the {@linkplain Property#getValue()}
	 */
	public <T> void bindTo(final ActorType actorType, final Property<T> property,
			final Class<T> propertyValueType) {
		getActorPA().bindBidirectional(actorType.getKey(), property,
				propertyValueType);
	}
	
	/**
	 * Help text {@linkplain EventHandler} {@linkplain MouseEvent} that shows
	 * either the {@linkplain StringProperty} or text
	 */
	private final class HelpTextMouseHandler implements EventHandler<MouseEvent> {
		
		private final StringProperty stringProperty;
		private final String text;
		
		/**
		 * Constructor
		 * 
		 * @param stringProperty
		 *            the {@linkplain StringProperty} to show as the text value
		 * @param text
		 *            the text to show
		 */
		private HelpTextMouseHandler(final StringProperty stringProperty, final String text) {
			this.stringProperty = stringProperty;
			this.text = text;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void handle(final MouseEvent event) {
			if (event.isSecondaryButtonDown()) {
				helpTextPane.setVvalue(helpTextPane.getVmin());
				if (stringProperty != null) {
					helpText.setText(stringProperty.get());
				} else {
					helpText.setText(text);
				}
				event.consume();
			}
		}
	}
}
