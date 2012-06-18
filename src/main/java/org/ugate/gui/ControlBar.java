package org.ugate.gui;

import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ugate.Command;
import org.ugate.IGateKeeperListener;
import org.ugate.RemoteSettings;
import org.ugate.UGateKeeper;
import org.ugate.UGateKeeperEvent;
import org.ugate.UGateUtil;
import org.ugate.gui.components.BeanPathAdapter;
import org.ugate.gui.components.Digits;
import org.ugate.gui.components.UGateToggleSwitchPreferenceView;
import org.ugate.resources.RS;
import org.ugate.service.ActorType;
import org.ugate.service.entity.jpa.Actor;
import org.ugate.wireless.data.RxTxSensorReadings;

/**
 * Main menu control bar
 */
public class ControlBar extends ToolBar {

	private final BeanPathAdapter<Actor> actorPA;
	private final ScrollPane helpTextPane;
	private final Label helpText;
	private final ReadOnlyObjectWrapper<RxTxSensorReadings> sensorReadingsPropertyWrapper = new ReadOnlyObjectWrapper<RxTxSensorReadings>();
	
	private static final Logger log = LoggerFactory.getLogger(ControlBar.class);
	public static final Color ATTENTION_COLOR = Color.YELLOW;
	public static final int HELP_TEXT_COLOR_CHANGE_CYCLE_COUNT = 8;
	public static final double CHILD_SPACING = 10d;
	public static final double CHILD_PADDING = 5d;
	public static final Insets PADDING_INSETS = new Insets(CHILD_PADDING, CHILD_PADDING, 0, CHILD_PADDING);

	private final Stage stage;

	public ControlBar(final Stage stage, final BeanPathAdapter<Actor> actorPA) {
		this.stage = stage;
		this.actorPA = actorPA;
		// help view
		final DropShadow helpTextDropShadow = new DropShadow();
		helpTextDropShadow.setRadius(50d);
		final Timeline helpTextTimeline = GuiUtil.createDropShadowColorIndicatorTimline(
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
		
		final DropShadow ds = new DropShadow();
		final ImageView camTakeQvga = RS.imgView(RS.IMG_CAM_QVGA);
		addHelpTextTrigger(camTakeQvga, RS.rbLabel("cam.take.qvga"));
		camTakeQvga.setCursor(Cursor.HAND);
		camTakeQvga.setEffect(ds);
		final ImageView camTakeVga = RS.imgView(RS.IMG_CAM_VGA);
		addHelpTextTrigger(camTakeVga, RS.rbLabel("cam.take.vga"));
		camTakeVga.setCursor(Cursor.HAND);
		camTakeVga.setEffect(ds);
		final ImageView settingsSet = RS.imgView(RS.IMG_SETTINGS_SET);
		settingsSet.setCursor(Cursor.HAND);
		settingsSet.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>(){
			@Override
			public void handle(final MouseEvent event) {
				if (GuiUtil.isPrimaryPress(event)) {
					createCommandService(Command.SENSOR_SET_SETTINGS, true);
				}
			}
	    });
		addHelpTextTrigger(settingsSet, RS.rbLabel("settings.send"));
		final DropShadow settingsDS = new DropShadow();
		settingsSet.setEffect(settingsDS);
		final ImageView settingsGet = RS.imgView(RS.IMG_SETTINGS_GET);
		settingsGet.setCursor(Cursor.HAND);
		settingsGet.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>(){
			@Override
			public void handle(final MouseEvent event) {
				if (GuiUtil.isPrimaryPress(event)) {
					createCommandService(Command.SENSOR_GET_SETTINGS, true);
				}
			}
	    });
		addHelpTextTrigger(settingsGet, RS.rbLabel("settings.receive"));
		settingsGet.setEffect(ds);
		final ImageView readingsGet = RS.imgView(RS.IMG_READINGS_GET);
		readingsGet.setCursor(Cursor.HAND);
		readingsGet.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>(){
			@Override
			public void handle(final MouseEvent event) {
				if (GuiUtil.isPrimaryPress(event)) {
					createCommandService(Command.SENSOR_GET_READINGS, true);
				}
			}
	    });
		addHelpTextTrigger(readingsGet, RS.rbLabel("sensors.readings.get"));
		readingsGet.setEffect(ds);
		
		// add the readings view
		final ImageView sonarReadingLabel = RS.imgView(RS.IMG_SONAR);
		final Digits sonarReading = new Digits(String.format(AlarmSettings.FORMAT_SONAR, 0.0f),
				0.15f, AlarmSettings.COLOR_SONAR, null);
		final ImageView pirReadingLabel = RS.imgView(RS.IMG_PIR);
		final Digits pirReading = new Digits(String.format(AlarmSettings.FORMAT_PIR, 0.0f), 
				0.15f, AlarmSettings.COLOR_PIR, null);
		final ImageView mwReadingLabel = RS.imgView(RS.IMG_MICROWAVE);
		final Digits mwReading = new Digits(String.format(AlarmSettings.FORMAT_MW, 0), 0.15f, 
				AlarmSettings.COLOR_MW, null);
		final ImageView laserReadingLabel = RS.imgView(RS.IMG_LASER);
		final Digits laserReading = new Digits(String.format(AlarmSettings.FORMAT_LASER, 0.0f), 
				0.15f, AlarmSettings.COLOR_LASER, null);
		final Region readingsGroup = GuiUtil.createBackgroundDisplay(PADDING_INSETS, CHILD_SPACING, 10, true,
				sonarReadingLabel, sonarReading, pirReadingLabel, pirReading, mwReadingLabel, mwReading, 
				laserReadingLabel, laserReading);
		addHelpTextTrigger(readingsGroup, "Current sensors readings display");
		
		
		// add the multi-alarm trip state
		final UGateToggleSwitchPreferenceView multiAlarmToggleSwitch = new UGateToggleSwitchPreferenceView(
				RemoteSettings.MULTI_ALARM_TRIP_STATE, UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex(),
				new UGateToggleSwitchPreferenceView.ToggleItem(RS.IMG_SONAR_ALARM_MULTI, 
						RS.IMG_SONAR_ALARM_OFF, RS.IMG_SONAR_ALARM_ANY, null, false),
				new UGateToggleSwitchPreferenceView.ToggleItem(RS.IMG_PIR_ALARM_MULTI, 
						RS.IMG_PIR_ALARM_OFF, RS.IMG_PIR_ALARM_ANY, null, false),
				new UGateToggleSwitchPreferenceView.ToggleItem(RS.IMG_MICROWAVE_ALARM_MULTI,
						RS.IMG_MICROWAVE_ALARM_OFF, RS.IMG_MICROWAVE_ALARM_ANY, null, false),
				new UGateToggleSwitchPreferenceView.ToggleItem(RS.IMG_LASER_ALARM_MULTI, 
						RS.IMG_LASER_ALARM_OFF, RS.IMG_LASER_ALARM_ANY, null, false));
		final Region multiAlarmGroup = GuiUtil.createBackgroundDisplay(PADDING_INSETS, CHILD_SPACING, 0,
				false, multiAlarmToggleSwitch);
		addHelpTextTrigger(multiAlarmGroup, RS.rbLabel("sensors.trip.multi"));
		
		// add the menu items
		getItems().addAll(camTakeQvga, camTakeVga, settingsSet, settingsGet, readingsGet, 
				new Separator(Orientation.VERTICAL), readingsGroup, 
				new Separator(Orientation.VERTICAL), multiAlarmGroup);
		
		final Timeline settingsSetTimeline = GuiUtil.createDropShadowColorIndicatorTimline(
				settingsDS, ATTENTION_COLOR, Color.BLACK, Timeline.INDEFINITE);
		// show a visual indication that the settings need updated
		UGateKeeper.DEFAULT.addListener(new IGateKeeperListener() {
			@Override
			public void handle(final UGateKeeperEvent<?> event) {
				setHelpText(event.getMessageString());
				if (event.getType() == UGateKeeperEvent.Type.SETTINGS_SAVE_LOCAL) {
					if (event.getKey() != null && event.getKey().canRemote()) {
						settingsSetTimeline.play();
					}
				} else if (event.getType() == UGateKeeperEvent.Type.WIRELESS_DATA_ALL_TX_SUCCESS) {
					settingsSetTimeline.stop();
				} else if (event.getType() == UGateKeeperEvent.Type.WIRELESS_DATA_RX_SUCCESS) {
					if (event.getNewValue() instanceof RxTxSensorReadings) {
						final RxTxSensorReadings sr = (RxTxSensorReadings) event.getNewValue();
						sensorReadingsPropertyWrapper.set(sr);
						sonarReading.setValue(String.format(AlarmSettings.FORMAT_SONAR, 
								Double.parseDouble(sr.getSonarFeet() + "." + sr.getSonarInches())));
						pirReading.setValue(String.format(AlarmSettings.FORMAT_PIR, 
								Double.parseDouble(sr.getIrFeet() + "." + sr.getIrInches())));
						mwReading.setValue(String.format(AlarmSettings.FORMAT_MW, 
								Math.round(sr.getSpeedMPH())));
					}
				}
			}
		});
//		sonarReading.setValue(String.format(AlarmSettings.FORMAT_SONAR, 5.3f));
//		pirReading.setValue(String.format(AlarmSettings.FORMAT_PIR, 3.7f));
//		mwReading.setValue(String.format(AlarmSettings.FORMAT_MW, 24L));
	}
	
	/**
	 * @return the menu bar items related to the control bar
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
		menu.getChildren().addAll(helpButton, helpTextPane);
	    return menu;
	}
	
	/**
	 * Creates a service for the command that will show a progress indicator preventing
	 * further action until the command execution has completed.
	 * 
	 * @param command the command
	 * @param start true to start the service immediately after creating the service
	 * @return the service
	 */
	public Service<Boolean> createCommandService(final Command command, final boolean start) {
		if (!UGateKeeper.DEFAULT.wirelessIsConnected()) {
			setHelpText(RS.rbLabel("service.wireless.connection.required"));
			return null;
		}
		setHelpText(null);
		final Service<Boolean> service = GuiUtil.alertProgress(stage, new Task<Boolean>() {
			@Override
			protected Boolean call() throws Exception {
				try {
					if (command == Command.SENSOR_GET_READINGS) {
						if (!UGateKeeper.DEFAULT.wirelessSendData(
								UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex(), 
								Command.SENSOR_GET_READINGS)) {
							setHelpText(RS.rbLabel("sensors.readings.failed",
									UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeAddress()));
							return false;
						}
					} else if (command == Command.SENSOR_SET_SETTINGS) {
						if (!UGateKeeper.DEFAULT.wirelessSendSettings(
								UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex())) {
							setHelpText(RS.rbLabel("settings.send.failed",
									UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeAddress()));
							return false;
						}
					} else if (command == Command.GATE_TOGGLE_OPEN_CLOSE) {
						if (!UGateKeeper.DEFAULT.wirelessSendData(
								UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex(), 
								Command.GATE_TOGGLE_OPEN_CLOSE)) {
							setHelpText(RS.rbLabel("gate.toggle.failed",
									UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeAddress()));
							return false;
						}
					} else {
						log.warn(String.format("%1$s is not a valid command for %2$s", 
								command, Controls.class.getName()));
						return false;
					}
				} catch (final Throwable t) {
					setHelpText(RS.rbLabel("service.command.failed"));
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
	 * Creates a wireless connection service that will show a progress indicator preventing
	 * further action until the wireless connection has been established.
	 * 
	 * @param comPort the COM port to connect to
	 * @param baudRate the baud rate to connect at
	 * @return the service
	 */
	public Service<Boolean> createWirelessConnectionService(final String comPort, final int baudRate) {
		setHelpText(null);
		return GuiUtil.alertProgress(stage, new Task<Boolean>() {
			@Override
			protected Boolean call() throws Exception {
				try {
					// establish wireless connection (blocking)
					UGateKeeper.DEFAULT.wirelessConnect(comPort, baudRate);
				} catch (final Throwable t) {
					setHelpText(RS.rbLabel("service.wireless.failed"));
					log.error("Unable to establish a wireless connection", t);
				}
				return false;
			}
		});
	}
	
	/**
	 * Creates an email connection service that will show a progress indicator preventing
	 * further action until the email connection has been established.
	 * 
	 * @return the service
	 */
	public Service<Boolean> createEmailConnectionService() {
		setHelpText(null);
		return GuiUtil.alertProgress(stage, new Task<Boolean>() {
			@Override
			protected Boolean call() throws Exception {
				try {
					// establish wireless connection (blocking)
					UGateKeeper.DEFAULT.emailConnect();
				} catch (final Throwable t) {
					setHelpText(RS.rbLabel("service.email.failed"));
					log.error("Unable to establish a wireless connection", t);
				}
				return false;
			}
		});
	}
	
	/**
	 * Adds the help from the {@linkplain StringProperty} text when the node is right clicked
	 * 
	 * @param node the node to trigger the text
	 * @param stringProperty the {@linkplain StringProperty} to show as the text value
	 */
	public void addHelpTextTrigger(final Node node, final StringProperty stringProperty) {
		node.setOnMousePressed(new HelpTextMouseHandler(stringProperty, null));
	}
	
	/**
	 * Adds the help text when the node is right clicked
	 * 
	 * @param node the node to trigger the text
	 * @param text the text to show
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
	 * @param text the help text to set
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
	 * @return the sensor readings property
	 */
	public ReadOnlyObjectProperty<RxTxSensorReadings> sensorReadingsProperty() {
		return sensorReadingsPropertyWrapper.getReadOnlyProperty();
	}
	
	/**
	 * @return the actor {@linkplain BeanPathAdapter}
	 */
	public BeanPathAdapter<Actor> getActorPA() {
		return actorPA;
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
	 * Help text mouse handler that shows either the {@linkplain StringProperty} or text
	 */
	private final class HelpTextMouseHandler implements EventHandler<MouseEvent> {
		
		private final StringProperty stringProperty;
		private final String text;
		
		/**
		 * Constructor
		 * 
		 * @param stringProperty the {@linkplain StringProperty} to show as the text value
		 * @param text the text to show
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
