package org.ugate.gui.view;

import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ugate.Command;
import org.ugate.UGateEvent;
import org.ugate.UGateKeeper;
import org.ugate.UGateListener;
import org.ugate.gui.ControlBar;
import org.ugate.gui.GuiUtil;
import org.ugate.gui.components.StatusIcon;
import org.ugate.gui.components.UGateComboBox;
import org.ugate.gui.components.UGateCtrlBox;
import org.ugate.resources.RS;
import org.ugate.resources.RS.KEYS;
import org.ugate.service.ServiceProvider;
import org.ugate.service.entity.ActorType;
import org.ugate.service.entity.jpa.Actor;

public class WirelessHostConnection extends VBox {
	
	private static final Logger log = LoggerFactory.getLogger(WirelessHostConnection.class);
	public final UGateComboBox<String> port;
	public final UGateComboBox<Integer> baud;
	public final UGateCtrlBox<Actor, Void, Void> hostAddress;
	public final Button wirelessBtn;
	public final Button webBtn;
	public final ControlBar cb;

	/**
	 * Creates the wireless connection view
	 */
	public WirelessHostConnection(final ControlBar controlBar) {
		super(20);
		this.cb = controlBar;
		
		final StatusIcon wirelessIcon = new StatusIcon(
				RS.imgView(RS.IMG_WIRELESS_ICON), GuiUtil.COLOR_OFF);
		final StatusIcon webIcon = new StatusIcon(
				RS.imgView(RS.IMG_WEB_ICON), GuiUtil.COLOR_OFF);
		port = createComPortBox();
	    baud = createBaudRateBox();
	    hostAddress = new UGateCtrlBox<>(cb.getActorPA(), ActorType.HOST_COM_ADDY, 
				UGateCtrlBox.Type.TEXT, RS.rbLabel(KEYS.WIRELESS_HOST_ADDY), null);
	    controlBar.addHelpTextTrigger(hostAddress, RS.rbLabel(KEYS.WIRELESS_HOST_ADDY_DESC));

		UGateKeeper.DEFAULT.addListener(new UGateListener() {
			@Override
			public void handle(final UGateEvent<?, ?> event) {
				if (event.getType() == UGateEvent.Type.WIRELESS_HOST_CONNECTING) {
					wirelessBtn.setDisable(true);
					wirelessBtn.setText(RS.rbLabel(KEYS.WIRELESS_CONNECTING));
					wirelessIcon.setStatusFill(Duration.seconds(1), 
							GuiUtil.COLOR_OPEN, GuiUtil.COLOR_CLOSED, 
							Timeline.INDEFINITE);
				} else if (event.getType() == UGateEvent.Type.WIRELESS_HOST_CONNECTED) {
					wirelessBtn.setDisable(false);
					wirelessBtn.setText(RS.rbLabel(KEYS.WIRELESS_RECONNECT));
					log.debug("Turning ON email connection icon");
					wirelessIcon.setStatusFill(GuiUtil.COLOR_ON);
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							log.info("Synchronizing the host settings to remote node(s)");
							controlBar.createCommandService(Command.SENSOR_SET_SETTINGS, true);
						}
					});
				} else if (event.getType() == UGateEvent.Type.WIRELESS_HOST_CONNECT_FAILED) {
					wirelessBtn.setDisable(false);
					wirelessBtn.setText(RS.rbLabel(KEYS.WIRELESS_CONNECT));
					wirelessIcon.setStatusFill(GuiUtil.COLOR_OFF);
				} else if (event.getType() == UGateEvent.Type.WIRELESS_HOST_DISCONNECTING) {
					wirelessBtn.setDisable(true);
					wirelessBtn.setText(RS.rbLabel(KEYS.WIRELESS_DISCONNECTING));
					wirelessIcon.setStatusFill(Duration.seconds(1), 
							GuiUtil.COLOR_OFF, GuiUtil.COLOR_CLOSED, 
							Timeline.INDEFINITE);
				} else if (event.getType() == UGateEvent.Type.WIRELESS_HOST_DISCONNECTED) {
					wirelessBtn.setDisable(false);
					wirelessBtn.setText(RS.rbLabel(KEYS.WIRELESS_CONNECT));
					log.debug("Turning FILL_OFF email connection icon");
					wirelessIcon.setStatusFill(GuiUtil.COLOR_OFF);
				} else if (event.getType() == UGateEvent.Type.WEB_INITIALIZE) {
					webBtn.setDisable(true);
					webIcon.setStatusFill(Duration.seconds(1), 
							GuiUtil.COLOR_SELECTED, GuiUtil.COLOR_CLOSED, 
							Timeline.INDEFINITE);
				} else if (event.getType() == UGateEvent.Type.WEB_CONNECTING) {
					webBtn.setDisable(true);
					webIcon.setStatusFill(Duration.seconds(1), 
							GuiUtil.COLOR_OPEN, GuiUtil.COLOR_CLOSED, 
							Timeline.INDEFINITE);
				} else if (event.getType() == UGateEvent.Type.WEB_CONNECTED) {
					webBtn.setDisable(false);
					webIcon.setStatusFill(GuiUtil.COLOR_ON);
				} else if (event.getType() == UGateEvent.Type.WEB_CONNECT_FAILED || 
						event.getType() == UGateEvent.Type.WEB_INITIALIZE_FAILED) {
					webBtn.setDisable(false);
					webIcon.setStatusFill(GuiUtil.COLOR_OFF);
				} else if (event.getType() == UGateEvent.Type.WEB_DISCONNECTING) {
					webBtn.setDisable(true);
					webIcon.setStatusFill(Duration.seconds(1), 
							GuiUtil.COLOR_OFF, GuiUtil.COLOR_CLOSED, 
							Timeline.INDEFINITE);
				} else if (event.getType() == UGateEvent.Type.WEB_DISCONNECTED) {
					webBtn.setDisable(false);
					webIcon.setStatusFill(GuiUtil.COLOR_OFF);
				}
			}
		});
	    
		wirelessBtn = new Button(RS.rbLabel(KEYS.WIRELESS_CONNECT));
		cb.addServiceBehavior(wirelessBtn, null, ServiceProvider.Type.WIRELESS,
				null);
		webBtn = new Button(RS.rbLabel(KEYS.WIRELESS_WEB_START_STOP));
		cb.addServiceBehavior(webBtn, null, ServiceProvider.Type.WEB,
				KEYS.WIRELESS_WEB_START_STOP_DESC);
	    
	    final VBox portBaudView = new VBox(10d);
	    portBaudView.setPadding(new Insets(30d, 0, 0, 0));
	    portBaudView.getChildren().addAll(port, baud);
	    
	    final GridPane iconGrid = new GridPane();
	    //iconGrid.setPadding(new Insets(20d, 0, 0, 0));
		iconGrid.setHgap(5d);
		iconGrid.setVgap(15d);
		iconGrid.add(wirelessIcon, 0, 0);
	    iconGrid.add(portBaudView, 1, 0);
	    
	    final VBox main = new VBox(10d);
	    main.getChildren().addAll(iconGrid, hostAddress);
	    getChildren().addAll(main, wirelessBtn, webIcon, webBtn);
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
}
