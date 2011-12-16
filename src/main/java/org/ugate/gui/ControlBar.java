package org.ugate.gui;

import org.apache.log4j.Logger;
import org.ugate.Command;
import org.ugate.IGateKeeperListener;
import org.ugate.Settings;
import org.ugate.UGateKeeper;
import org.ugate.UGateUtil;
import org.ugate.gui.components.Digits;
import org.ugate.gui.components.UGateToggleSwitchPreferenceView;
import org.ugate.resources.RS;

import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Main menu control bar
 */
public class ControlBar extends ToolBar {
	
	private final ScrollPane helpTextPane;
	private final Label helpText;
	
	private static final Logger log = Logger.getLogger(ControlBar.class);
	public static final int HELP_TEXT_COLOR_CHANGE_CYCLE_COUNT = 8;
	public static final double CHILD_SPACING = 10d;
	public static final double CHILD_PADDING = 5d;
	public static final Insets PADDING_INSETS = new Insets(CHILD_PADDING, CHILD_PADDING, 0, CHILD_PADDING);

	private final Stage stage;
	private int remoteNodeIndex = 1;
	private boolean propagateSettingsToAllRemoteNodes = false;

	public ControlBar(final Stage stage) {
		this.stage = stage;
		// help view
		final DropShadow helpTextDropShadow = new DropShadow();
		final Timeline helpTextTimeline = GuiUtil.createDropShadowColorIndicatorTimline(
				helpTextDropShadow, Color.RED, Color.BLACK, HELP_TEXT_COLOR_CHANGE_CYCLE_COUNT);
		helpTextPane = new ScrollPane();
		helpTextPane.setStyle("-fx-background-color: #ffffff;");
		helpTextPane.setPrefHeight(40d);
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
					createCommandService(Command.SENSOR_SET_SETTINGS).start();
				}
			}
	    });
		addHelpTextTrigger(settingsSet, RS.rbLabel("settings.send"));
		final DropShadow settingsDS = new DropShadow();
		settingsSet.setEffect(settingsDS);
		final Timeline settingsSetTimeline = GuiUtil.createDropShadowColorIndicatorTimline(
				settingsDS, Color.RED, Color.BLACK, Timeline.INDEFINITE);
		// show a visual indication that the settings need updated
		UGateKeeper.DEFAULT.preferencesAddListener(new IGateKeeperListener() {
			@Override
			public void handle(final IGateKeeperListener.Event type, final String node,
					final Settings key, final Command command, 
					final String oldValue, final String newValue) {
				if (type == IGateKeeperListener.Event.SETTINGS_SAVE_LOCAL) {
					if (key != null && key.canRemote) {
						settingsSetTimeline.play();
					}
				} else if (type == IGateKeeperListener.Event.SETTINGS_SEND_SUCCESS) {
					settingsSetTimeline.stop();
				} else if (type == IGateKeeperListener.Event.SETTINGS_SEND_FAILED) {
				}
			}
		});
		final ImageView readingsGet = RS.imgView(RS.IMG_READINGS_GET);
		addHelpTextTrigger(readingsGet, RS.rbLabel("sensors.readings.get"));
		readingsGet.setCursor(Cursor.HAND);
		readingsGet.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>(){
			@Override
			public void handle(final MouseEvent event) {
				if (GuiUtil.isPrimaryPress(event)) {
					createCommandService(Command.SENSOR_GET_READINGS).start();
				}
			}
	    });
		
		// add the readings view
		final ImageView sonarReadingLabel = RS.imgView(RS.IMG_SONAR);
		final Digits sonarReading = new Digits(String.format(SensorControl.FORMAT_SONAR, 0.0f),
				0.15f, SensorControl.COLOR_SONAR, null);
		final ImageView pirReadingLabel = RS.imgView(RS.IMG_PIR);
		final Digits pirReading = new Digits(String.format(SensorControl.FORMAT_PIR, 0.0f), 
				0.15f, SensorControl.COLOR_PIR, null);
		final ImageView mwReadingLabel = RS.imgView(RS.IMG_MICROWAVE);
		final Digits mwReading = new Digits(String.format(SensorControl.FORMAT_MW, 0), 0.15f, 
				SensorControl.COLOR_MW, null);
		final Group readingsGroup = GuiUtil.createBackgroundDisplay(PADDING_INSETS, CHILD_SPACING, 10,
				sonarReadingLabel, sonarReading, pirReadingLabel, pirReading, mwReadingLabel, mwReading);
		addHelpTextTrigger(readingsGroup, "Current sensors readings display");
		
		
		// add the multi-alarm trip state
		final UGateToggleSwitchPreferenceView multiAlarmToggleSwitch = new UGateToggleSwitchPreferenceView(
				Settings.MULTI_ALARM_TRIP_STATE_KEY,
				new UGateToggleSwitchPreferenceView.ToggleItem(RS.IMG_SONAR_ALARM_ON, RS.IMG_SONAR_ALARM_OFF, false),
				new UGateToggleSwitchPreferenceView.ToggleItem(RS.IMG_IR_ALARM_ON, RS.IMG_IR_ALARM_OFF, false),
				new UGateToggleSwitchPreferenceView.ToggleItem(RS.IMG_MICROWAVE_ALARM_ON, RS.IMG_MICROWAVE_ALARM_OFF, false));
		final Group multiAlarmGroup = GuiUtil.createBackgroundDisplay(PADDING_INSETS, CHILD_SPACING, 0,
				multiAlarmToggleSwitch);
		addHelpTextTrigger(multiAlarmGroup, RS.rbLabel("sensors.trip.multi"));
		
		// add the menu items
		getItems().addAll(camTakeQvga, camTakeVga, settingsSet, readingsGet, 
				new Separator(Orientation.VERTICAL), readingsGroup, 
				new Separator(Orientation.VERTICAL), multiAlarmGroup,
				new Separator(Orientation.VERTICAL), helpTextPane);
	}
	
	/**
	 * Creates a service for the command
	 * 
	 * @param command the command
	 * @return the service
	 */
	public Service<Boolean> createCommandService(final Command command) {
		setHelpText(null);
		return GuiUtil.alertProgress(stage, new Task<Boolean>() {
			@Override
			protected Boolean call() throws Exception {
				try {
					if (command == Command.SENSOR_GET_READINGS) {
						if (!UGateKeeper.DEFAULT.wirelessSendData(getRemoteNodeAddress(), 
								Command.SENSOR_GET_READINGS)) {
							setHelpText(RS.rbLabel("sensors.readings.failed",
									(isPropagateSettingsToAllRemoteNodes() ? RS.rbLabel("all") : 
										getRemoteNodeAddress())));
							return false;
						}
					} else if (command == Command.SENSOR_SET_SETTINGS) {
						if (!UGateKeeper.DEFAULT.wirelessSendSettings(
								(isPropagateSettingsToAllRemoteNodes() ? null : getRemoteNodeAddress()))) {
							setHelpText(RS.rbLabel("settings.send.failed",
									(isPropagateSettingsToAllRemoteNodes() ? RS.rbLabel("all") : 
										getRemoteNodeAddress())));
							return false;
						}
					} else if (command == Command.GATE_TOGGLE_OPEN_CLOSE) {
						if (!UGateKeeper.DEFAULT.wirelessSendData(getRemoteNodeAddress(), 
								Command.GATE_TOGGLE_OPEN_CLOSE)) {
							setHelpText(RS.rbLabel("gate.toggle.failed",
									(isPropagateSettingsToAllRemoteNodes() ? RS.rbLabel("all") : 
										getRemoteNodeAddress())));
							return false;
						}
					} else {
						log.warn(String.format("%1$s is not a valid command for %2$s", 
								command, Controls.class.getName()));
						return false;
					}
				} catch (final Throwable t) {
					setHelpText(
							"An error occurred while executing command. See log for more details.");
					log.error("Unable to execute " + command, t);
					return false;
				}
				return true;
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
		helpText.setText(text == null || text.length() == 0 ? RS.rbLabel(UGateUtil.HELP_TEXT_DEFAULT_KEY) : text);
	}
	
	/**
	 * @return the remote node address of the device node for which the controls represent
	 */
	public String getRemoteNodeAddress() {
		return Settings.WIRELESS_ADDRESS_NODE_PREFIX_KEY.key + getRemoteNodeIndex();
	}

	/**
	 * @return the remote index of the device node for which the controls represent
	 */
	public int getRemoteNodeIndex() {
		return this.remoteNodeIndex;
	}

	/**
	 * Sets the remote index of the device node for which the controls represent
	 * 
	 * @param remoteNodeIndex the remote node index
	 */
	public void setRemoteNodeIndex(final int remoteNodeIndex) {
		this.remoteNodeIndex = remoteNodeIndex;
	}

	/**
	 * @return true when settings updates should be propagated to all the remote nodes when
	 * 		the user chooses to save the settings
	 */
	public boolean isPropagateSettingsToAllRemoteNodes() {
		return propagateSettingsToAllRemoteNodes;
	}

	/**
	 * @param propagateSettingsToAllRemoteNodes true when settings updates should be propagated 
	 * 		to all the remote nodes when the user chooses to save the settings
	 */
	public void setPropagateSettingsToAllRemoteNodes(final boolean propagateSettingsToAllRemoteNodes) {
		this.propagateSettingsToAllRemoteNodes = propagateSettingsToAllRemoteNodes;
	}
}
