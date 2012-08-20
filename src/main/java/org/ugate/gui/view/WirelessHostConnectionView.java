package org.ugate.gui.view;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ugate.Command;
import org.ugate.IGateKeeperListener;
import org.ugate.UGateKeeper;
import org.ugate.UGateKeeperEvent;
import org.ugate.gui.ControlBar;
import org.ugate.gui.components.UGateComboBox;
import org.ugate.gui.components.UGateCtrlBox;
import org.ugate.resources.RS;
import org.ugate.resources.RS.KEYS;
import org.ugate.service.ServiceProvider;
import org.ugate.service.entity.ActorType;
import org.ugate.service.entity.jpa.Actor;

public class WirelessHostConnectionView extends StatusView {
	
	private static final Logger log = LoggerFactory.getLogger(WirelessHostConnectionView.class);
	public final UGateComboBox<String> port;
	public final UGateComboBox<Integer> baud;
	public final UGateCtrlBox<Actor, Void, Void> hostAddress;
	public final Button connect;

	/**
	 * Creates the wireless connection view
	 */
	public WirelessHostConnectionView(final ControlBar controlBar) {
		super(controlBar, false, 20);
		
		final ImageView icon = RS.imgView(RS.IMG_WIRELESS_ICON);
		
		port = createComPortBox();
	    baud = createBaudRateBox();
	    hostAddress = new UGateCtrlBox<>(cb.getActorPA(), ActorType.HOST_COM_ADDY, 
				UGateCtrlBox.Type.TYPE_TEXT, RS.rbLabel(KEYS.WIRELESS_HOST_ADDY), null);
	    controlBar.addHelpTextTrigger(hostAddress, RS.rbLabel(KEYS.WIRELESS_HOST_ADDY_DESC));

		UGateKeeper.DEFAULT.addListener(new IGateKeeperListener() {
			@Override
			public void handle(final UGateKeeperEvent<?> event) {
				if (event.getType() == UGateKeeperEvent.Type.WIRELESS_HOST_CONNECTING) {
					connect.setDisable(true);
					connect.setText(RS.rbLabel(KEYS.WIRELESS_CONNECTING));
				} else if (event.getType() == UGateKeeperEvent.Type.WIRELESS_HOST_CONNECTED) {
					connect.setDisable(false);
					connect.setText(RS.rbLabel(KEYS.WIRELESS_RECONNECT));
					log.debug("Turning ON email connection icon");
					setStatusFill(true);
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							log.info("Synchronizing the host settings to remote node(s)");
							controlBar.createCommandService(Command.SENSOR_SET_SETTINGS, true);
						}
					});
				} else if (event.getType() == UGateKeeperEvent.Type.WIRELESS_HOST_CONNECT_FAILED) {
					connect.setDisable(false);
					connect.setText(RS.rbLabel(KEYS.WIRELESS_CONNECT));
				} else if (event.getType() == UGateKeeperEvent.Type.WIRELESS_HOST_DISCONNECTING) {
					connect.setDisable(true);
					connect.setText(RS.rbLabel(KEYS.WIRELESS_DISCONNECTING));
				} else if (event.getType() == UGateKeeperEvent.Type.WIRELESS_HOST_DISCONNECTED) {
					connect.setDisable(false);
					connect.setText(RS.rbLabel(KEYS.WIRELESS_CONNECT));
					log.debug("Turning OFF email connection icon");
					setStatusFill(false);
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
	    connect.setText(RS.rbLabel(KEYS.WIRELESS_CONNECT));
	    
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
	 * @return {@linkplain ActorType#HOST_COM_PORT} {@linkplain UGateComboBox}
	 */
	public UGateComboBox<String> createComPortBox() {
		log.debug("Loading available serial ports");
		final UGateComboBox<String> port = new UGateComboBox<>(RS.rbLabel(KEYS.WIRELESS_PORT), 
				ServiceProvider.IMPL.getWirelessService().getSerialPorts());
		cb.addHelpTextTrigger(port, RS.rbLabel(KEYS.WIRELESS_PORT_DESC));
		cb.bindTo(ActorType.HOST_COM_PORT, port.getComboBox().valueProperty(),
				String.class);
		return port;
	}
	
	/**
	 * @return {@linkplain ActorType#HOST_BAUD_RATE} {@linkplain UGateComboBox}
	 */
	public UGateComboBox<Integer> createBaudRateBox() {
		log.debug("Loading available baud rates");
	    final UGateComboBox<Integer> baud = new UGateComboBox<>(
	    		RS.rbLabel(KEYS.WIRELESS_SPEED), ActorType.HOST_BAUD_RATES);
	    cb.addHelpTextTrigger(baud, RS.rbLabel(KEYS.WIRELESS_SPEED_DESC));
		cb.bindTo(ActorType.HOST_BAUD_RATE, baud.getComboBox().valueProperty(),
				Integer.class);
		return baud;
	}
	
	/**
	 * Establishes a wireless connection using the internal parameters.
	 */
	public void connect() {
		if (!cb.getActor().getHost().getComAddress().isEmpty() && 
				cb.getActor().getHost().getComBaud() > 0) {
			ServiceProvider.IMPL.getCredentialService().mergeHost(cb.getActor().getHost());
			cb.createWirelessConnectionService().start();
		}
	}
}
