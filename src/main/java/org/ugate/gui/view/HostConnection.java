package org.ugate.gui.view;

import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ugate.UGateEvent;
import org.ugate.UGateKeeper;
import org.ugate.UGateListener;
import org.ugate.gui.ControlBar;
import org.ugate.gui.GuiUtil;
import org.ugate.gui.components.StatusIcon;
import org.ugate.gui.components.UGateComboBox;
import org.ugate.gui.components.UGateCtrlBox;
import org.ugate.gui.components.UGateToggleSwitchBox;
import org.ugate.resources.RS;
import org.ugate.resources.RS.KEY;
import org.ugate.service.ServiceProvider;
import org.ugate.service.entity.ActorType;
import org.ugate.service.entity.Command;
import org.ugate.service.entity.Model;
import org.ugate.service.entity.jpa.Actor;

public class HostConnection extends VBox {
	
	private static final Logger log = LoggerFactory.getLogger(HostConnection.class);
	public final UGateComboBox<String> port;
	public final UGateComboBox<Integer> baud;
	public final UGateCtrlBox<Actor, Model, Void> hostAddress;
	public final UGateToggleSwitchBox<Actor> wirelessOnAtStartSwitch;
	public final Button wirelessBtn;
	public final UGateCtrlBox<Actor, Model, Void> webHost;
	public final UGateCtrlBox<Actor, Model, Void> webPort;
	public final UGateCtrlBox<Actor, Model, Void> webHostLocal;
	public final UGateCtrlBox<Actor, Model, Void> webPortLocal;
	public final UGateToggleSwitchBox<Actor> webOnAtStartSwitch;
	public final Button webBtn;
	public final ControlBar cb;

	/**
	 * Creates the wireless connection view
	 */
	public HostConnection(final ControlBar controlBar) {
		super(20);
		this.cb = controlBar;
		
		final StatusIcon wirelessIcon = new StatusIcon(
				RS.imgView(RS.IMG_WIRELESS_ICON), GuiUtil.COLOR_OFF);
		cb.addServiceBehavior(wirelessIcon, null, ServiceProvider.Type.WIRELESS, KEY.WIRELESS_CONNECT_DESC);
		final StatusIcon webIcon = new StatusIcon(
				RS.imgView(RS.IMG_WEB_ICON), GuiUtil.COLOR_OFF);
		cb.addServiceBehavior(webIcon, null, ServiceProvider.Type.WEB, KEY.WIRELESS_WEB_START_STOP_DESC);
		port = createComPortBox();
	    baud = createBaudRateBox();
	    hostAddress = new UGateCtrlBox<>(cb.getActorPA(), ActorType.COM_ADDY, 
				UGateCtrlBox.Type.TEXT, RS.rbLabel(KEY.WIRELESS_HOST_ADDY), null);
	    controlBar.addHelpTextTrigger(hostAddress, RS.rbLabel(KEY.WIRELESS_HOST_ADDY_DESC));

	    listenForAppEvents(wirelessIcon, webIcon);
		
	    webHost = new UGateCtrlBox<>(cb.getActorPA(), ActorType.WEB_HOST, 
				UGateCtrlBox.Type.TEXT, RS.rbLabel(KEY.WEB_HOST), null);
	    controlBar.addHelpTextTrigger(webHost, RS.rbLabel(KEY.WEB_HOST_DESC));
	    webPort = new UGateCtrlBox<>(cb.getActorPA(), ActorType.WEB_PORT, 
				UGateCtrlBox.Type.TEXT, RS.rbLabel(KEY.WEB_PORT), null);
	    controlBar.addHelpTextTrigger(webPort, RS.rbLabel(KEY.WEB_PORT_DESC));
	    webHostLocal = new UGateCtrlBox<>(cb.getActorPA(), ActorType.WEB_HOST_LOCAL, 
				UGateCtrlBox.Type.TEXT, RS.rbLabel(KEY.WEB_HOST_LOCAL), null);
	    controlBar.addHelpTextTrigger(webHostLocal, RS.rbLabel(KEY.WEB_HOST_LOCAL_DESC));
	    webPortLocal = new UGateCtrlBox<>(cb.getActorPA(), ActorType.WEB_PORT_LOCAL, 
				UGateCtrlBox.Type.TEXT, RS.rbLabel(KEY.WEB_PORT_LOCAL), null);
	    controlBar.addHelpTextTrigger(webPortLocal, RS.rbLabel(KEY.WEB_PORT_LOCAL_DESC));
		
		final HBox wirelessBtnView = new HBox();
		wirelessOnAtStartSwitch = new UGateToggleSwitchBox<>(
				controlBar.getActorPA(), ActorType.COM_ON_AT_APP_STARTUP, null,
				null, RS.rbLabel(KEY.APP_SERVICE_STARTUP_AUTO),
				RS.rbLabel(KEY.APP_SERVICE_STARTUP_MANUAL));
		controlBar.addHelpTextTrigger(wirelessOnAtStartSwitch, RS.rbLabel(KEY.APP_SERVICE_STARTUP_DESC));
		wirelessBtn = new Button(RS.rbLabel(KEY.WIRELESS_CONNECT));
		controlBar.addHelpTextTrigger(wirelessBtn, RS.rbLabel(KEY.WIRELESS_WEB_START_STOP_DESC));
		cb.addServiceBehavior(wirelessBtn, null, ServiceProvider.Type.WIRELESS,
				KEY.WIRELESS_CONNECT_DESC);
		wirelessBtnView.getChildren().addAll(wirelessBtn, wirelessOnAtStartSwitch);
		
		final HBox webBtnView = new HBox();
		webOnAtStartSwitch = new UGateToggleSwitchBox<>(
				controlBar.getActorPA(), ActorType.WEB_ON_AT_COM_STARTUP, null,
				null, RS.rbLabel(KEY.APP_SERVICE_STARTUP_AUTO),
				RS.rbLabel(KEY.APP_SERVICE_STARTUP_MANUAL));
		controlBar.addHelpTextTrigger(webOnAtStartSwitch, RS.rbLabel(KEY.APP_SERVICE_HOST_STARTUP_DESC));
		webBtn = new Button(RS.rbLabel(KEY.WIRELESS_WEB_START_STOP));
		cb.addServiceBehavior(webBtn, null, ServiceProvider.Type.WEB,
				KEY.WIRELESS_WEB_START_STOP_DESC);
		webBtnView.getChildren().addAll(webBtn, webOnAtStartSwitch);

		getChildren().addAll(
				createIconGrid(wirelessIcon, port, baud, hostAddress),
				wirelessBtnView,
				createIconGrid(webIcon, webHost, webPort, webHostLocal,
						webPortLocal), webBtnView);
	}

	/**
	 * Listens for any incoming {@linkplain UGateEvent}s and handles
	 * {@linkplain HostConnection} related tasks
	 * 
	 * @param wirelessIcon
	 *            the {@linkplain StatusIcon} for wireless
	 * @param webIcon
	 *            the {@linkplain StatusIcon} for web
	 */
	private void listenForAppEvents(final StatusIcon wirelessIcon, final StatusIcon webIcon) {
		UGateKeeper.DEFAULT.addListener(new UGateListener() {
			@Override
			public void handle(final UGateEvent<?, ?> event) {
				if (event.getType() == UGateEvent.Type.WIRELESS_HOST_CONNECTING) {
					wirelessBtn.setDisable(true);
					wirelessBtn.setText(RS.rbLabel(KEY.WIRELESS_CONNECTING));
					wirelessIcon.setStatusFill(Duration.seconds(1), 
							GuiUtil.COLOR_OPEN, GuiUtil.COLOR_CLOSED, 
							Timeline.INDEFINITE);
				} else if (event.getType() == UGateEvent.Type.WIRELESS_HOST_CONNECTED) {
					wirelessBtn.setDisable(false);
					wirelessBtn.setText(RS.rbLabel(KEY.WIRELESS_RECONNECT));
					log.debug("Turning ON email connection icon");
					wirelessIcon.setStatusFill(GuiUtil.COLOR_ON);
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							log.info("Synchronizing the host settings to remote node(s)");
							cb.createCommandService(Command.SENSOR_SEND_SETTINGS, true);
						}
					});
				} else if (event.getType() == UGateEvent.Type.WIRELESS_HOST_CONNECT_FAILED) {
					wirelessBtn.setDisable(false);
					wirelessBtn.setText(RS.rbLabel(KEY.WIRELESS_CONNECT));
					wirelessIcon.setStatusFill(GuiUtil.COLOR_OFF);
				} else if (event.getType() == UGateEvent.Type.WIRELESS_HOST_DISCONNECTING) {
					wirelessBtn.setDisable(true);
					wirelessBtn.setText(RS.rbLabel(KEY.WIRELESS_DISCONNECTING));
					wirelessIcon.setStatusFill(Duration.seconds(1), 
							GuiUtil.COLOR_OFF, GuiUtil.COLOR_CLOSED, 
							Timeline.INDEFINITE);
				} else if (event.getType() == UGateEvent.Type.WIRELESS_HOST_DISCONNECTED) {
					wirelessBtn.setDisable(false);
					wirelessBtn.setText(RS.rbLabel(KEY.WIRELESS_CONNECT));
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
	}

	/**
	 * @return {@linkplain ActorType#COM_PORT} {@linkplain UGateComboBox}
	 */
	public UGateComboBox<String> createComPortBox() {
		log.debug("Loading available serial ports");
		final UGateComboBox<String> port = new UGateComboBox<>(RS.rbLabel(KEY.WIRELESS_PORT), 
				ServiceProvider.IMPL.getWirelessService().getSerialPorts());
		cb.addHelpTextTrigger(port, RS.rbLabel(KEY.WIRELESS_PORT_DESC));
		cb.bindTo(ActorType.COM_PORT, port.getComboBox().valueProperty(),
				String.class);
		return port;
	}
	
	/**
	 * @return {@linkplain ActorType#COM_BAUD_RATE} {@linkplain UGateComboBox}
	 */
	public UGateComboBox<Integer> createBaudRateBox() {
		log.debug("Loading available baud rates");
	    final UGateComboBox<Integer> baud = new UGateComboBox<>(
	    		RS.rbLabel(KEY.WIRELESS_SPEED), ActorType.HOST_BAUD_RATES);
	    cb.addHelpTextTrigger(baud, RS.rbLabel(KEY.WIRELESS_SPEED_DESC));
		cb.bindTo(ActorType.COM_BAUD_RATE, baud.getComboBox().valueProperty(),
				Integer.class);
		return baud;
	}

	/**
	 * Creates a {@linkplain StatusIcon} view in a {@linkplain GridPane} layout
	 * format
	 * 
	 * @param icon
	 *            the {@linkplain StatusIcon}
	 * @param nodes
	 *            the {@linkplain Node}s that will appear adjacent to the
	 *            {@linkplain StatusIcon}
	 * @return the {@linkplain GridPane}
	 */
	protected GridPane createIconGrid(final StatusIcon icon, final Node... nodes) {
	    final VBox iconNodes = new VBox(10d);
	    iconNodes.setPadding(new Insets(30d, 0, 0, 0));
	    iconNodes.getChildren().addAll(nodes);
	    
	    final GridPane iconGrid = new GridPane();
	    //iconGrid.setPadding(new Insets(20d, 0, 0, 0));
		iconGrid.setHgap(5d);
		iconGrid.setVgap(15d);
		iconGrid.add(icon, 0, 0);
	    iconGrid.add(iconNodes, 1, 0);
	    
	    return iconGrid;
	}
}
