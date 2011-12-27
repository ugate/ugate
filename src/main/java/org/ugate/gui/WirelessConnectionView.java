package org.ugate.gui;

import java.util.Timer;
import java.util.TimerTask;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import org.apache.log4j.Logger;
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
public abstract class WirelessConnectionView extends StatusView {
	
	private static final Logger log = Logger.getLogger(WirelessConnectionView.class);
	public static final String ACCESS_KEY_CODE_FORMAT = "%01d";
	public final UGateChoiceBox<String> port;
	public final UGateChoiceBox<Integer> baud;
	public final UGateToggleSwitchPreferenceView universalRemoteAccessToggleSwitch;
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
		
		universalRemoteAccessToggleSwitch = new UGateToggleSwitchPreferenceView(
				Settings.UNIVERSAL_REMOTE_ACCESS_ON, RS.IMG_UNIVERSAL_REMOTE_ON, RS.IMG_UNIVERSAL_REMOTE_OFF);
	    accessKey1 = new UGateTextFieldPreferenceView(Settings.ACCESS_CODE_1_KEY, 
	    		ACCESS_KEY_CODE_FORMAT, null, null, RS.rbLabel("wireless.access.key", 1), 
				RS.rbLabel("wireless.access.key.desc", 1));
	    accessKey2 = new UGateTextFieldPreferenceView(Settings.ACCESS_CODE_2_KEY, 
	    		ACCESS_KEY_CODE_FORMAT, null, null, RS.rbLabel("wireless.access.key", 2), 
				RS.rbLabel("wireless.access.key.desc", 2));
	    accessKey3 = new UGateTextFieldPreferenceView(Settings.ACCESS_CODE_3_KEY, 
	    		ACCESS_KEY_CODE_FORMAT, null, null, RS.rbLabel("wireless.access.key", 3), 
				RS.rbLabel("wireless.access.key.desc", 3));
		
		port = new UGateChoiceBox<String>(RS.rbLabel("wireless.port"), new String[]{});
		configComPorts();
	    baud = new UGateChoiceBox<Integer>(RS.rbLabel("wireless.speed"), new Integer[]{});
	    configBaudRates();
	    
	    // update the status when wireless connections are made/lost
		UGateKeeper.DEFAULT.addListener(new IGateKeeperListener() {
			@Override
			public void handle(final UGateKeeperEvent<?> event) {
				if (event.getType() == UGateKeeperEvent.Type.WIRELESS_DATA_ALL_TX_SUCCESS) {
					if (event.getKey() != null && event.getKey().canRemote) {
						// TODO :
					}
				}
			}
		});
	    
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
	    
	    final HBox accessKeysContainer = new HBox(5);
	    accessKeysContainer.getChildren().addAll(accessKey1, accessKey2, accessKey3);
	    
	    final GridPane grid = new GridPane();
	    grid.setHgap(10d);
	    grid.setVgap(30d);
	    
	    final VBox toggleView = new VBox(10d);
	    toggleView.getChildren().addAll(icon, universalRemoteAccessToggleSwitch);
	    
	    final GridPane connectionGrid = new GridPane();
	    connectionGrid.setPadding(new Insets(20d, 0, 0, 0));
		connectionGrid.setHgap(5d);
		connectionGrid.setVgap(15d);
	    connectionGrid.add(port, 0, 0);
	    connectionGrid.add(baud, 0, 1);
	    
	    grid.add(toggleView, 0, 0);
	    grid.add(connectionGrid, 1, 0);
	    grid.add(accessKeysContainer, 0, 1, 2, 1);
	    grid.add(connect, 0, 2, 2, 1);
	    getChildren().add(grid);
	}
	
	/**
	 * Configures the COM ports
	 */
	public void configComPorts() {
		log.debug("Loading available serial ports");
		port.choice.getItems().addAll(UGateKeeper.DEFAULT.getSerialPorts());
		final String xbeeComPort = UGateKeeper.DEFAULT.preferencesGet(Settings.WIRELESS_COM_PORT_KEY);
		final boolean hasItem = xbeeComPort != null && xbeeComPort.length() > 0 && port.choice.getItems().contains(xbeeComPort);
		if (hasItem) {
			port.choice.getSelectionModel().select(xbeeComPort);
		}
		port.choice.selectionModelProperty().addListener(new ChangeListener<SingleSelectionModel<String>>() {
			@Override
			public void changed(final ObservableValue<? extends SingleSelectionModel<String>> observable, 
					final SingleSelectionModel<String> oldValue, final SingleSelectionModel<String> newValue) {
				UGateKeeper.DEFAULT.preferencesSet(Settings.WIRELESS_COM_PORT_KEY, newValue.getSelectedItem());
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
		final String xbeeBaudRateStr = UGateKeeper.DEFAULT.preferencesGet(Settings.WIRELESS_BAUD_RATE_KEY);
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
				UGateKeeper.DEFAULT.preferencesSet(Settings.WIRELESS_BAUD_RATE_KEY, newValue.getSelectedItem());
			}
		});
		if (!hasItem && !baud.choice.getItems().isEmpty()) {
			baud.choice.getSelectionModel().select(0);
		}
		baud.choice.autosize();
	}
	
	/**
	 * Establishes a wireless connection
	 * 
	 * @param comPort the COM port to connect to
	 * @param baudRate the baud rate to connect at
	 */
	public void connect(final String comPort, final int baudRate) {
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
