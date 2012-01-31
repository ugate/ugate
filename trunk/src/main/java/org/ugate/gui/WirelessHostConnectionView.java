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
import javafx.scene.layout.VBox;

import org.apache.log4j.Logger;
import org.ugate.Command;
import org.ugate.HostSettings;
import org.ugate.IGateKeeperListener;
import org.ugate.UGateKeeper;
import org.ugate.UGateKeeperEvent;
import org.ugate.UGateUtil;
import org.ugate.gui.components.UGateChoiceBox;
import org.ugate.gui.components.UGateTextFieldPreferenceView;
import org.ugate.resources.RS;

/**
 * Wireless connection GUI responsible for connecting to the wireless service
 */
public class WirelessHostConnectionView extends StatusView {
	
	private static final Logger log = Logger.getLogger(WirelessHostConnectionView.class);
	public final UGateChoiceBox<String> port;
	public final UGateChoiceBox<Integer> baud;
	public final UGateTextFieldPreferenceView hostAddress;
	public final Button connect;

	/**
	 * Creates the wireless connection view
	 */
	public WirelessHostConnectionView(final ControlBar controlBar) {
		super(controlBar, 20);
		
		final ImageView icon = RS.imgView(RS.IMG_WIRELESS_ICON);
		
		port = new UGateChoiceBox<String>(RS.rbLabel("wireless.port"), new String[]{});
		controlBar.addHelpTextTrigger(port, RS.rbLabel("wireless.port.desc"));
		configComPorts();
	    baud = new UGateChoiceBox<Integer>(RS.rbLabel("wireless.speed"), new Integer[]{});
	    controlBar.addHelpTextTrigger(baud, RS.rbLabel("wireless.speed.desc"));
	    configBaudRates();
	    hostAddress = new UGateTextFieldPreferenceView(HostSettings.WIRELESS_ADDRESS_HOST, null,
				UGateTextFieldPreferenceView.Type.TYPE_TEXT, RS.rbLabel("wireless.host"), null);
	    controlBar.addHelpTextTrigger(hostAddress, RS.rbLabel("wireless.host.desc"));

		UGateKeeper.DEFAULT.addListener(new IGateKeeperListener() {
			@Override
			public void handle(final UGateKeeperEvent<?> event) {
				if (event.getType() == UGateKeeperEvent.Type.WIRELESS_HOST_CONNECTING) {
					connect.setDisable(true);
					connect.setText(RS.rbLabel("wireless.connecting"));
				} else if (event.getType() == UGateKeeperEvent.Type.WIRELESS_HOST_CONNECTED) {
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
					connect.setDisable(false);
					connect.setText(RS.rbLabel("wireless.connect"));
					log.debug("Turning OFF email connection icon");
					setStatusFill(statusIcon, false);
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
	    main.getChildren().addAll(iconGrid, hostAddress);
	    getChildren().addAll(main, connect);
	}
	
	/**
	 * Configures the COM ports
	 */
	public void configComPorts() {
		log.debug("Loading available serial ports");
		port.choice.getItems().addAll(UGateKeeper.DEFAULT.getSerialPorts());
		final String xbeeComPort = UGateKeeper.DEFAULT.settingsGet(HostSettings.WIRELESS_COM_PORT, null);
		final boolean hasItem = xbeeComPort != null && xbeeComPort.length() > 0 && port.choice.getItems().contains(xbeeComPort);
		if (hasItem) {
			port.choice.getSelectionModel().select(xbeeComPort);
		}
		port.choice.selectionModelProperty().addListener(new ChangeListener<SingleSelectionModel<String>>() {
			@Override
			public void changed(final ObservableValue<? extends SingleSelectionModel<String>> observable, 
					final SingleSelectionModel<String> oldValue, final SingleSelectionModel<String> newValue) {
				UGateKeeper.DEFAULT.settingsSet(HostSettings.WIRELESS_COM_PORT, null, newValue.getSelectedItem());
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
		final String xbeeBaudRateStr = UGateKeeper.DEFAULT.settingsGet(HostSettings.WIRELESS_BAUD_RATE, null);
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
				UGateKeeper.DEFAULT.settingsSet(HostSettings.WIRELESS_BAUD_RATE, null, newValue.getSelectedItem());
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
				!baud.choice.getSelectionModel().isEmpty() && baud.choice.getSelectionModel().getSelectedItem() != null) {
			UGateKeeper.DEFAULT.settingsSet(HostSettings.WIRELESS_ADDRESS_HOST, 
					UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex(), 
					hostAddress.getValue().toString());
			controlBar.createWirelessConnectionService(port.choice.getSelectionModel().getSelectedItem(), 
					baud.choice.getSelectionModel().getSelectedItem()).start();
		}
	}
}
