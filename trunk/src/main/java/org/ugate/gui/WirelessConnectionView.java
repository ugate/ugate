package org.ugate.gui;

import java.util.Timer;
import java.util.TimerTask;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import org.apache.log4j.Logger;
import org.ugate.Settings;
import org.ugate.UGateKeeper;
import org.ugate.UGateUtil;
import org.ugate.gui.components.UGateChoiceBox;
import org.ugate.gui.components.UGateTextFieldPreferenceView;
import org.ugate.resources.RS;

/**
 * Wireless connection GUI responsible for connecting to the wireless service
 */
public abstract class WirelessConnectionView extends StatusView {
	
	private static final Logger log = Logger.getLogger(WirelessConnectionView.class);
	public static final String ACCESS_KEY_CODE_FORMAT = "%01d";
	public final UGateChoiceBox<String> port;
	public final UGateChoiceBox<Integer> baud;
	public final UGateTextFieldPreferenceView accessKey1;
	public final UGateTextFieldPreferenceView accessKey2;
	public final UGateTextFieldPreferenceView accessKey3;
	public final Button connect;

	/**
	 * Creates the wireless connection view
	 */
	public WirelessConnectionView() {
		super(20);
		
		final ImageView icon = RS.imgView(RS.IMG_WIRELESS_ICON);
		port = new UGateChoiceBox<String>(RS.rbLabel("wireless.port"), new String[]{});
		loadComPorts();
	    baud = new UGateChoiceBox<Integer>(RS.rbLabel("wireless.speed"), new Integer[]{});
	    loadBaudRates();

	    accessKey1 = new UGateTextFieldPreferenceView(Settings.ACCESS_CODE_1_KEY, 
	    		ACCESS_KEY_CODE_FORMAT, null, null, RS.rbLabel("wireless.access.key", 1), 
				RS.rbLabel("wireless.access.key.desc", 1));
	    accessKey2 = new UGateTextFieldPreferenceView(Settings.ACCESS_CODE_2_KEY, 
	    		ACCESS_KEY_CODE_FORMAT, null, null, RS.rbLabel("wireless.access.key", 2), 
				RS.rbLabel("wireless.access.key.desc", 2));
	    accessKey3 = new UGateTextFieldPreferenceView(Settings.ACCESS_CODE_3_KEY, 
	    		ACCESS_KEY_CODE_FORMAT, null, null, RS.rbLabel("wireless.access.key", 3), 
				RS.rbLabel("wireless.access.key.desc", 3));
	    
	    connect = new Button();
	    connectionHandler = new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				if (!port.choice.getSelectionModel().isEmpty() && !baud.choice.getSelectionModel().isEmpty()) {
					connect(port.choice.getSelectionModel().getSelectedItem(), 
							baud.choice.getSelectionModel().getSelectedItem());
				}
			}
	    };
	    connect.addEventHandler(MouseEvent.MOUSE_CLICKED, connectionHandler);
	    connect.setText(RS.rbLabel("wireless.connect"));
	    connect.setTooltip(new Tooltip(connect.getText()));	    
	    
	    final HBox wirelessContainer = new HBox(10);
	    wirelessContainer.getChildren().addAll(port, baud, statusIcon);
	    final HBox accessKeysContainer = new HBox(5);
	    accessKeysContainer.getChildren().addAll(accessKey1, accessKey2, accessKey3);
	    getChildren().addAll(icon, wirelessContainer, accessKeysContainer, connect);
	}
	
	/**
	 * Loads the available COM ports
	 */
	public void loadComPorts() {
		port.choice.getItems().addAll(UGateKeeper.DEFAULT.getSerialPorts());
		final String xbeeComPort = UGateKeeper.DEFAULT.preferencesGet(Settings.WIRELESS_COM_PORT_KEY);
		if (xbeeComPort != null && xbeeComPort.length() > 0 && 
				port.choice.getItems().contains(xbeeComPort)) {
			port.choice.getSelectionModel().select(xbeeComPort);
		} else if (!port.choice.getItems().isEmpty()) {
			port.choice.getSelectionModel().select(0);
		}
	}
	
	/**
	 * Loads the available Baud rates
	 */
	public void loadBaudRates() {
		log.debug("Loading available baud rates");
		baud.choice.getItems().addAll(UGateUtil.XBEE_BAUD_RATES);
		final String xbeeBaudRateStr = UGateKeeper.DEFAULT.preferencesGet(Settings.WIRELESS_BAUD_RATE_KEY);
		if (xbeeBaudRateStr != null && xbeeBaudRateStr.length() > 0) {
			final Integer xbeeBaudRate = Integer.parseInt(xbeeBaudRateStr);
			if (baud.choice.getItems().contains(xbeeBaudRate)) {
				baud.choice.getSelectionModel().select(xbeeBaudRate);
			}
		}
	}
	
	/**
	 * Establishes a wireless connection
	 * 
	 * @param comPort the COM port to connect to
	 * @param baudRate the baud rate to connect at
	 */
	public void connect(String comPort, int baudRate) {
		disconnect();
		connect.setDisable(true);
		connect.setText(RS.rbLabel("wireless.connecting"));
		try {
			if (UGateKeeper.DEFAULT.wirelessConnect(comPort, baudRate)) {
				setStatusFill(statusIcon, true);
				connect.setText(RS.rbLabel("wireless.synchronizing"));
				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {
						try {
							UGateKeeper.DEFAULT.wirelessSendSettings();
						} catch (final Throwable t) {
							log.warn("Unable to sync local settings to remote wireless nodes", t);
						}
						connect.setText(RS.rbLabel("wireless.reconnect"));
						connect.setDisable(false);
					}
				}, 1000);
			} else {
				connect.setText(RS.rbLabel("wireless.connect"));
				connect.setDisable(false);
			}
		} catch (final Throwable t) {
			log.warn(String.format("Unable to connect to COM port: {0} @ {1}", comPort, baudRate), t);
			connect.setText(RS.rbLabel("wireless.connect"));
			connect.setDisable(false);
		}
		UGateKeeper.DEFAULT.preferencesSet(Settings.WIRELESS_COM_PORT_KEY, comPort);
		UGateKeeper.DEFAULT.preferencesSet(Settings.WIRELESS_BAUD_RATE_KEY, String.valueOf(baudRate));
		UGateKeeper.DEFAULT.preferencesSet(Settings.ACCESS_CODE_1_KEY, accessKey1.getValue().toString());
		UGateKeeper.DEFAULT.preferencesSet(Settings.ACCESS_CODE_2_KEY, accessKey2.getValue().toString());
		UGateKeeper.DEFAULT.preferencesSet(Settings.ACCESS_CODE_3_KEY, accessKey3.getValue().toString());
	}

	/**
	 * Disconnects wireless connection (if connected)
	 */
	public void disconnect() {
		if (UGateKeeper.DEFAULT.wirelessIsConnected()) {
			UGateKeeper.DEFAULT.wirelessDisconnect();
			setStatusFill(statusIcon, false);
			connect.setDisable(false);
			connect.setText(RS.rbLabel("wireless.connect"));
		}
	}
}
