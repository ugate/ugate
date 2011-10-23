package org.ugate.gui;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import org.apache.log4j.Logger;
import org.ugate.UGateKeeper;
import org.ugate.UGateUtil;
import org.ugate.gui.components.UGateChoiceBox;

/**
 * Wireless connection GUI responsible for connecting to the wireless service
 */
public abstract class WirelessConnectionView extends StatusView {
	
	private static final Logger log = Logger.getLogger(WirelessConnectionView.class);
	public static final String LABEL_CONNECT = "Connect To Local XBee";
	public static final String LABEL_CONNECTING = "Connecting To Local XBee...";
	public static final String LABEL_SYNC = "Syncronizing Remote XBees...";
	public static final String LABEL_RECONNECT = "Reconnect To Local XBee";
	public final UGateChoiceBox<String> port;
	public final UGateChoiceBox<Integer> baud;
	public final Button connect;

	/**
	 * Creates the wireless connection view
	 */
	public WirelessConnectionView() {
		super(20);
		
		port = new UGateChoiceBox<String>("Serial Port", new String[]{});
		loadComPorts();
	    baud = new UGateChoiceBox<Integer>("Baud Rate", new Integer[]{});
	    loadBaudRates();
	    
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
	    connect.setText(LABEL_CONNECT);
	    connect.setTooltip(new Tooltip(connect.getText()));
	    
	    final HBox xbeeContainer = new HBox(10);
	    xbeeContainer.getChildren().addAll(port, baud, statusIcon);
	    getChildren().addAll(xbeeContainer, connect);
	}
	
	/**
	 * Loads the available COM ports
	 */
	public void loadComPorts() {
		port.choice.getItems().addAll(UGateKeeper.DEFAULT.getSerialPorts());
		final String xbeeComPort = UGateKeeper.DEFAULT.preferences.get(UGateUtil.SV_WIRELESS_COM_PORT_KEY);
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
		final String xbeeBaudRateStr = UGateKeeper.DEFAULT.preferences.get(UGateUtil.SV_WIRELESS_BAUD_RATE_KEY);
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
		connect.setText(LABEL_CONNECTING);
		try {
			if (UGateKeeper.DEFAULT.wirelessConnect(comPort, baudRate)) {
				setStatusFill(statusIcon, true);
				connect.setText(LABEL_SYNC);
				try {
					UGateKeeper.DEFAULT.wirelessSyncSettings();
				} catch (final Throwable t) {
					log.warn("Unable to sync local settings to remote wireless nodes", t);
				}
				connect.setText(LABEL_RECONNECT);
			} else {
				connect.setText(LABEL_CONNECT);
			}
		} catch (final Throwable t) {
			log.warn(String.format("Unable to connect to COM port: {0} @ {1}", comPort, baudRate), t);
			connect.setText(LABEL_CONNECT);
		}
		connect.setDisable(false);
		UGateKeeper.DEFAULT.preferences.set(UGateUtil.SV_WIRELESS_COM_PORT_KEY, comPort);
		UGateKeeper.DEFAULT.preferences.set(UGateUtil.SV_WIRELESS_BAUD_RATE_KEY, String.valueOf(baudRate));
	}

	/**
	 * Disconnects wireless connection (if connected)
	 */
	public void disconnect() {
		if (UGateKeeper.DEFAULT.wirelessIsConnected()) {
			UGateKeeper.DEFAULT.wirelessDisconnect();
			setStatusFill(statusIcon, false);
			connect.setDisable(false);
			connect.setText(LABEL_CONNECT);
		}
	}
}
