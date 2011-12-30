package org.ugate.gui;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import org.apache.log4j.Logger;
import org.ugate.Command;
import org.ugate.IGateKeeperListener;
import org.ugate.Settings;
import org.ugate.UGateKeeper;
import org.ugate.UGateKeeperEvent;
import org.ugate.UGateUtil;
import org.ugate.gui.components.UGateChoiceBox;
import org.ugate.gui.components.UGateTextFieldPreferenceView;
import org.ugate.gui.components.UGateToggleSwitchPreferenceView;
import org.ugate.resources.RS;

/**
 * Wireless connection GUI responsible for connecting to the wireless service
 */
public class WirelessConnectionView extends StatusView {
	
	private static final Logger log = Logger.getLogger(WirelessConnectionView.class);
	public static final String ACCESS_KEY_CODE_FORMAT = "%01d";
	public final UGateChoiceBox<String> port;
	public final UGateChoiceBox<Integer> baud;
	public final UGateToggleSwitchPreferenceView universalRemoteAccessToggleSwitch;
	public final UGateTextFieldPreferenceView hostAddress;
	public final UGateTextFieldPreferenceView remoteAddress;
	public final UGateTextFieldPreferenceView accessKey1;
	public final UGateTextFieldPreferenceView accessKey2;
	public final UGateTextFieldPreferenceView accessKey3;
	public final Button connect;

	/**
	 * Creates the wireless connection view
	 */
	public WirelessConnectionView(final ControlBar controlBar) {
		super(controlBar, 20);
		
		final ImageView icon = RS.imgView(RS.IMG_WIRELESS_ICON);
		
	    accessKey1 = new UGateTextFieldPreferenceView(Settings.ACCESS_CODE_1, 
	    		ACCESS_KEY_CODE_FORMAT, null, null, RS.rbLabel("wireless.access.key", 1), null);
	    controlBar.addHelpTextTrigger(accessKey1, RS.rbLabel("wireless.access.key.desc", 1));
	    accessKey2 = new UGateTextFieldPreferenceView(Settings.ACCESS_CODE_2, 
	    		ACCESS_KEY_CODE_FORMAT, null, null, RS.rbLabel("wireless.access.key", 2), null);
	    controlBar.addHelpTextTrigger(accessKey2, RS.rbLabel("wireless.access.key.desc", 2));
	    accessKey3 = new UGateTextFieldPreferenceView(Settings.ACCESS_CODE_3, 
	    		ACCESS_KEY_CODE_FORMAT, null, null, RS.rbLabel("wireless.access.key", 3), null);
	    controlBar.addHelpTextTrigger(accessKey3, RS.rbLabel("wireless.access.key.desc", 3));
	    hostAddress = new UGateTextFieldPreferenceView(Settings.WIRELESS_ADDRESS_HOST, 
				UGateTextFieldPreferenceView.Type.TYPE_TEXT, RS.rbLabel("wireless.host"), null);
	    controlBar.addHelpTextTrigger(hostAddress, RS.rbLabel("wireless.host.desc"));
	    // TODO : add GUI support for multiple remote wireless nodes
	    remoteAddress = new UGateTextFieldPreferenceView(Settings.WIRELESS_ADDRESS_NODE_PREFIX, 
				UGateUtil.WIRELESS_ADDRESS_START_INDEX, 
				UGateTextFieldPreferenceView.Type.TYPE_TEXT, 
				RS.rbLabel("wireless.remote", UGateUtil.WIRELESS_ADDRESS_START_INDEX), null);
	    controlBar.addHelpTextTrigger(remoteAddress, 
	    		RS.rbLabel("wireless.remote.desc", UGateUtil.WIRELESS_ADDRESS_START_INDEX));
		universalRemoteAccessToggleSwitch = new UGateToggleSwitchPreferenceView(
				Settings.UNIVERSAL_REMOTE_ACCESS_ON, RS.IMG_UNIVERSAL_REMOTE_ON, RS.IMG_UNIVERSAL_REMOTE_OFF);
		controlBar.addHelpTextTrigger(universalRemoteAccessToggleSwitch, RS.rbLabel("wireless.remote.universal.desc", 
				UGateUtil.WIRELESS_ADDRESS_START_INDEX));
		
		port = new UGateChoiceBox<String>(RS.rbLabel("wireless.port"), new String[]{});
		controlBar.addHelpTextTrigger(port, RS.rbLabel("wireless.port.desc"));
		configComPorts();
	    baud = new UGateChoiceBox<Integer>(RS.rbLabel("wireless.speed"), new Integer[]{});
	    controlBar.addHelpTextTrigger(baud, RS.rbLabel("wireless.speed.desc"));
	    configBaudRates();

		UGateKeeper.DEFAULT.addListener(new IGateKeeperListener() {
			@Override
			public void handle(final UGateKeeperEvent<?> event) {
				if (event.getType() == UGateKeeperEvent.Type.WIRELESS_HOST_CONNECTING) {
					connect.setDisable(true);
					connect.setText(RS.rbLabel("wireless.connecting"));
				} else if (event.getType() == UGateKeeperEvent.Type.WIRELESS_HOST_CONNECTED) {
					// save the connected parameters (done here instead of automatic in case a connection cannot be made)
					UGateKeeper.DEFAULT.preferencesSet(Settings.WIRELESS_ADDRESS_HOST, hostAddress.textField.getText());
					UGateKeeper.DEFAULT.preferencesSet(Settings.WIRELESS_ADDRESS_NODE_PREFIX, 
							UGateUtil.WIRELESS_ADDRESS_START_INDEX, remoteAddress.textField.getText());
					connect.setDisable(false);
					connect.setText(RS.rbLabel("wireless.reconnect"));
					log.debug("Turning ON email connection icon");
					setStatusFill(statusIcon, true);
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							log.info("Synchronizing the host settings to remote node(s)");
							controlBar.createCommandService(Command.SENSOR_SET_SETTINGS, true);
						}
					});
				} else if (event.getType() == UGateKeeperEvent.Type.WIRELESS_HOST_CONNECT_FAILED) {
					connect.setDisable(false);
					connect.setText(RS.rbLabel("wireless.connect"));
				} else if (event.getType() == UGateKeeperEvent.Type.WIRELESS_HOST_DISCONNECTING) {
					connect.setDisable(true);
					connect.setText(RS.rbLabel("wireless.disconnecting"));
				} else if (event.getType() == UGateKeeperEvent.Type.WIRELESS_HOST_DISCONNECTED) {
					// run later in case the application is going to exit which will cause an issue with FX thread
					try {
						connect.setDisable(false);
						connect.setText(RS.rbLabel("wireless.connect"));
						log.debug("Turning OFF email connection icon");
						setStatusFill(statusIcon, false);
					} catch (final Throwable t) {
						
					}
				}
			}
		});
	    
	    connect = new Button();
	    connect.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				connect();
			}
	    });
	    connect.setText(RS.rbLabel("wireless.connect"));
	    
	    final HBox accessKeysContainer = new HBox(5);
	    accessKeysContainer.getChildren().addAll(accessKey1, accessKey2, accessKey3);
	    
	    final VBox portBaudView = new VBox(10d);
	    portBaudView.setPadding(new Insets(30d, 0, 0, 0));
	    portBaudView.getChildren().addAll(port, baud);
	    
	    final GridPane iconGrid = new GridPane();
	    //iconGrid.setPadding(new Insets(20d, 0, 0, 0));
		iconGrid.setHgap(5d);
		iconGrid.setVgap(15d);
		iconGrid.add(icon, 0, 0);
	    iconGrid.add(portBaudView, 1, 0);
	    
	    final VBox main = new VBox(10d);
	    main.getChildren().addAll(iconGrid, hostAddress, remoteAddress, 
	    		universalRemoteAccessToggleSwitch, accessKeysContainer);
	    getChildren().addAll(main, connect);
	}
	
	/**
	 * Configures the COM ports
	 */
	public void configComPorts() {
		log.debug("Loading available serial ports");
		port.choice.getItems().addAll(UGateKeeper.DEFAULT.getSerialPorts());
		final String xbeeComPort = UGateKeeper.DEFAULT.preferencesGet(Settings.WIRELESS_COM_PORT);
		final boolean hasItem = xbeeComPort != null && xbeeComPort.length() > 0 && port.choice.getItems().contains(xbeeComPort);
		if (hasItem) {
			port.choice.getSelectionModel().select(xbeeComPort);
		}
		port.choice.selectionModelProperty().addListener(new ChangeListener<SingleSelectionModel<String>>() {
			@Override
			public void changed(final ObservableValue<? extends SingleSelectionModel<String>> observable, 
					final SingleSelectionModel<String> oldValue, final SingleSelectionModel<String> newValue) {
				UGateKeeper.DEFAULT.preferencesSet(Settings.WIRELESS_COM_PORT, newValue.getSelectedItem());
			}
		});
		if (!hasItem && !port.choice.getItems().isEmpty()) {
			port.choice.getSelectionModel().select(0);
		}
		port.choice.autosize();
	}
	
	/**
	 * Configures the Baud rates
	 */
	public void configBaudRates() {
		log.debug("Loading available baud rates");
		baud.choice.getItems().addAll(UGateUtil.XBEE_BAUD_RATES);
		final String xbeeBaudRateStr = UGateKeeper.DEFAULT.preferencesGet(Settings.WIRELESS_BAUD_RATE);
		boolean hasItem = xbeeBaudRateStr != null && xbeeBaudRateStr.length() > 0;
		if (hasItem) {
			final Integer xbeeBaudRate = Integer.parseInt(xbeeBaudRateStr);
			if (baud.choice.getItems().contains(xbeeBaudRate)) {
				baud.choice.getSelectionModel().select(xbeeBaudRate);
			} else {
				hasItem = false;
			}
		}
		port.choice.selectionModelProperty().addListener(new ChangeListener<SingleSelectionModel<String>>() {
			@Override
			public void changed(final ObservableValue<? extends SingleSelectionModel<String>> observable, 
					final SingleSelectionModel<String> oldValue, final SingleSelectionModel<String> newValue) {
				UGateKeeper.DEFAULT.preferencesSet(Settings.WIRELESS_BAUD_RATE, newValue.getSelectedItem());
			}
		});
		if (!hasItem && !baud.choice.getItems().isEmpty()) {
			baud.choice.getSelectionModel().select(0);
		}
		baud.choice.autosize();
	}
	
	/**
	 * Establishes a wireless connection using the internal parameters.
	 */
	@Override
	public void connect() {
		if (!port.choice.getSelectionModel().isEmpty() && port.choice.getSelectionModel().getSelectedItem() != null && 
				baud.choice.getSelectionModel().isEmpty() && baud.choice.getSelectionModel().getSelectedItem() != null) {
			controlBar.createWirelessConnectionService(port.choice.getSelectionModel().getSelectedItem(), 
					baud.choice.getSelectionModel().getSelectedItem()).start();
		}
//		disconnect();
//		connect.setDisable(true);
//		connect.setText(RS.rbLabel("wireless.connecting"));
//		try {
//			if (UGateKeeper.DEFAULT.wirelessConnect(comPort, baudRate)) {
//				setStatusFill(statusIcon, true);
//				connect.setText(RS.rbLabel("wireless.synchronizing"));
//				new Timer().schedule(new TimerTask() {
//					@Override
//					public void run() {
//						try {
//							UGateKeeper.DEFAULT.wirelessSendSettings();
//						} catch (final Throwable t) {
//							log.warn("Unable to sync local settings to remote wireless nodes", t);
//						}
//						connect.setText(RS.rbLabel("wireless.reconnect"));
//						connect.setDisable(false);
//					}
//				}, 1000);
//			} else {
//				connect.setText(RS.rbLabel("wireless.connect"));
//				connect.setDisable(false);
//			}
//		} catch (final Throwable t) {
//			log.warn(String.format("Unable to connect to COM port: {0} @ {1}", comPort, baudRate), t);
//			connect.setText(RS.rbLabel("wireless.connect"));
//			connect.setDisable(false);
//		}
	}
}
