package org.ugate.gui;

import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import org.apache.log4j.Logger;
import org.ugate.Command;
import org.ugate.IGateKeeperListener;
import org.ugate.RemoteSettings;
import org.ugate.UGateKeeper;
import org.ugate.UGateKeeperEvent;
import org.ugate.UGateUtil;
import org.ugate.gui.components.Digits;
import org.ugate.gui.components.TextFieldMenu;
import org.ugate.gui.components.UGateToggleSwitchPreferenceView;
import org.ugate.resources.RS;
import org.ugate.wireless.data.SensorReadings;

/**
 * Main menu control bar
 */
public class ControlBar extends ToolBar {
	
	private final ScrollPane helpTextPane;
	private final Label helpText;
	private final ReadOnlyObjectWrapper<SensorReadings> sensorReadingsPropertyWrapper = new ReadOnlyObjectWrapper<SensorReadings>();
	
	private static final Logger log = Logger.getLogger(ControlBar.class);
	public static final int HELP_TEXT_COLOR_CHANGE_CYCLE_COUNT = 8;
	public static final double CHILD_SPACING = 10d;
	public static final double CHILD_PADDING = 5d;
	public static final Insets PADDING_INSETS = new Insets(CHILD_PADDING, CHILD_PADDING, 0, CHILD_PADDING);

	private final Stage stage;

	public ControlBar(final Stage stage) {
		this.stage = stage;
		// help view
		final DropShadow helpTextDropShadow = new DropShadow();
		final Timeline helpTextTimeline = GuiUtil.createDropShadowColorIndicatorTimline(
				helpTextDropShadow, Color.RED, Color.BLACK, HELP_TEXT_COLOR_CHANGE_CYCLE_COUNT);
		helpTextPane = new ScrollPane();
		helpTextPane.getStyleClass().add("text-area-help");
		//helpTextPane.setPrefHeight(40d);
		helpTextPane.setPrefWidth(200d);
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
		final ImageView readingsGet = RS.imgView(RS.IMG_READINGS_GET);
		addHelpTextTrigger(readingsGet, RS.rbLabel("sensors.readings.get"));
		readingsGet.setCursor(Cursor.HAND);
		readingsGet.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>(){
			@Override
			public void handle(final MouseEvent event) {
				if (GuiUtil.isPrimaryPress(event)) {
					createCommandService(Command.SENSOR_GET_READINGS, true);
				}
			}
	    });
		
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
				new UGateToggleSwitchPreferenceView.ToggleItem(RS.IMG_SONAR_ALARM_ON, 
						RS.IMG_SONAR_ALARM_OFF, RS.IMG_SONAR_ALARM_ANY, null, false),
				new UGateToggleSwitchPreferenceView.ToggleItem(RS.IMG_PIR_ALARM_ON, 
						RS.IMG_PIR_ALARM_OFF, RS.IMG_PIR_ALARM_ANY, null, false),
				new UGateToggleSwitchPreferenceView.ToggleItem(RS.IMG_MICROWAVE_ALARM_ON,
						RS.IMG_MICROWAVE_ALARM_OFF, RS.IMG_MICROWAVE_ALARM_ANY, null, false),
				new UGateToggleSwitchPreferenceView.ToggleItem(RS.IMG_LASER_ALARM_ON, 
						RS.IMG_LASER_ALARM_OFF, RS.IMG_LASER_ALARM_ANY, null, false));
		final Region multiAlarmGroup = GuiUtil.createBackgroundDisplay(PADDING_INSETS, CHILD_SPACING, 0,
				false, multiAlarmToggleSwitch);
		addHelpTextTrigger(multiAlarmGroup, RS.rbLabel("sensors.trip.multi"));
		
		// add the menu items
		getItems().addAll(camTakeQvga, camTakeVga, settingsSet, readingsGet, 
				new Separator(Orientation.VERTICAL), readingsGroup, 
				new Separator(Orientation.VERTICAL), multiAlarmGroup);
		
		final Timeline settingsSetTimeline = GuiUtil.createDropShadowColorIndicatorTimline(
				settingsDS, Color.RED, Color.BLACK, Timeline.INDEFINITE);
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
					if (event.getNewValue() instanceof SensorReadings) {
						final SensorReadings sr = (SensorReadings) event.getNewValue();
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
		final HBox menu = new HBox(10);
	    final TextFieldMenu raddy = new TextFieldMenu(RS.rbLabel("wireless.node.remote"), 
	    		RS.rbLabel("wireless.node.remote.prompt"));
	    menu.getStyleClass().add("title-bar-menu");
	    final Object[] raddys = UGateKeeper.DEFAULT.wirelessGetRemoteAddressMap().values().toArray();
	    raddy.addMenuItems(raddys);
	    raddy.select(UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeAddress());
	    menu.getChildren().addAll(raddy, helpTextPane);
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
	 * Adds the help text when the node is right clicked
	 * 
	 * @param node the node to trigger the text
	 * @param text the text to show
	 */
	public void addHelpTextTrigger(final Node node, final String text) {
		node.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(final MouseEvent event) {
				if (event.isSecondaryButtonDown()) {
					helpTextPane.setVvalue(helpTextPane.getVmin());
					helpText.setText(text);
					event.consume();
				}
			}
		});
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
	public ReadOnlyObjectProperty<SensorReadings> sensorReadingsProperty() {
		return sensorReadingsPropertyWrapper.getReadOnlyProperty();
	}
}
