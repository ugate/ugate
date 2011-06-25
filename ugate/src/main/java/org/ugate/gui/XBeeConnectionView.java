package org.ugate.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import org.apache.log4j.Logger;
import org.ugate.UGateKeeper;

/**
 * XBee connection GUI responsible for connecting to the XBee service
 */
public abstract class XBeeConnectionView extends StatusView {
	
	private static final Logger log = Logger.getLogger(XBeeConnectionView.class);
	public static final String LABEL_CONNECT = "Connect To Local XBee";
	public static final String LABEL_CONNECTING = "Connecting To Local XBee...";
	public static final String LABEL_RECONNECT = "Reconnect To Local XBee";
	public final UGateChoiceBoxControl<String> port;
	public final UGateChoiceBoxControl<Integer> baud;
	public final Button connect;

	public XBeeConnectionView() {
		super(20);
		
		port = new UGateChoiceBoxControl<String>("Serial Port", new String[]{});
		port.choice.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> ov, String value, String newValue) {
				
			}
		});
		
	    baud = new UGateChoiceBoxControl<Integer>("Baud Rate", new Integer[]{});
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
	
	public void loadComPorts() {
		port.choice.getItems().addAll(UGateKeeper.DEFAULT.getSerialPorts());
		final String xbeeComPort = UGateKeeper.DEFAULT.preferences.get(UGateKeeper.XBEE_COM_PORT_KEY);
		if (xbeeComPort != null && xbeeComPort.length() > 0 && 
				port.choice.getItems().contains(xbeeComPort)) {
			port.choice.getSelectionModel().select(xbeeComPort);
		} else if (!port.choice.getItems().isEmpty()) {
			port.choice.getSelectionModel().select(0);
		}
	}
	
	public void loadBaudRates() {
		log.debug("Loading available baud rates");
		baud.choice.getItems().addAll(UGateKeeper.XBEE_BAUD_RATES);
		final String xbeeBaudRateStr = UGateKeeper.DEFAULT.preferences.get(UGateKeeper.XBEE_BAUD_RATE_KEY);
		if (xbeeBaudRateStr != null && xbeeBaudRateStr.length() > 0) {
			final Integer xbeeBaudRate = Integer.parseInt(xbeeBaudRateStr);
			if (baud.choice.getItems().contains(xbeeBaudRate)) {
				baud.choice.getSelectionModel().select(xbeeBaudRate);
			}
		}
	}
	
	public void connect(String comPort, int baudRate) {
		disconnect();
		connect.setDisable(true);
		connect.setText(LABEL_CONNECTING);
		if (UGateKeeper.DEFAULT.xbeeConnect(comPort, baudRate)) {
			setStatusFill(statusIcon, true);
			connect.setText(LABEL_RECONNECT);
		} else {
			connect.setText(LABEL_CONNECT);
		}
		connect.setDisable(false);
		UGateKeeper.DEFAULT.preferences.set(UGateKeeper.XBEE_COM_PORT_KEY, comPort);
		UGateKeeper.DEFAULT.preferences.set(UGateKeeper.XBEE_BAUD_RATE_KEY, String.valueOf(baudRate));
	}

	public void disconnect() {
		if (UGateKeeper.DEFAULT.xbeeIsConnected()) {
			UGateKeeper.DEFAULT.xbeeDisconnect();
			setStatusFill(statusIcon, false);
			connect.setDisable(false);
			connect.setText(LABEL_CONNECT);
		}
	}
}
