package org.ugate.gui.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import org.ugate.Command;
import org.ugate.gui.ControlBar;
import org.ugate.gui.ControlPane;
import org.ugate.gui.GuiUtil;
import org.ugate.gui.components.UGateCtrlBox;
import org.ugate.gui.components.UGateToggleSwitchBox;
import org.ugate.resources.RS;
import org.ugate.resources.RS.KEYS;
import org.ugate.service.entity.Model;
import org.ugate.service.entity.RemoteNodeType;
import org.ugate.service.entity.jpa.RemoteNode;
import org.ugate.service.entity.jpa.RemoteNodeReading;

/**
 * Node configuration
 */
public class AlarmSettings extends ControlPane {

	private UGateToggleSwitchBox<RemoteNode> syncToggleSwitch;
	private UGateToggleSwitchBox<RemoteNode> soundsToggleSwitch;
	private UGateToggleSwitchBox<RemoteNode> emailToggleSwitch;
	private UGateToggleSwitchBox<RemoteNode> imgResToggleSwitch;
	private UGateToggleSwitchBox<RemoteNode> universalRemoteAccessToggleSwitch;
	private UGateCtrlBox<RemoteNode, Model, Void> remoteAddress;
	private UGateCtrlBox<RemoteNode, Model, Void> workingDir;
	private UGateCtrlBox<RemoteNode, Model, Void> accessKey1;
	private UGateCtrlBox<RemoteNode, Model, Void> accessKey2;
	private UGateCtrlBox<RemoteNode, Model, Void> accessKey3;
	private UGateToggleSwitchBox<RemoteNode> gateToggleSwitchView;
	private AlarmMultistates alarmMultistates;

	/**
	 * Constructor
	 * 
	 * @param controlBar
	 *            the {@linkplain ControlBar}
	 * @param the
	 *            {@linkplain SensorReading}
	 */
	public AlarmSettings(final ControlBar controlBar) {
		super(controlBar);
		int ci = -1;
		addRemoteNodeSetupChildren(++ci, 0);
		addNotificationOptionChildren(++ci, 0);
		addGateChildren(++ci, 0);
	}

	protected void addNotificationOptionChildren(final int columnIndex, final int rowIndex) {
		final Label soundLabel = createLabel(KEYS.SERVICE_CMD_SOUNDS);
		final Label emailLabel = createLabel(KEYS.MAIL_ALARM_NOTIFY);
		final Label imgResLabel = createLabel(KEYS.CAM_RES);
		final Label syncResLabel = createLabel(KEYS.WIRELESS_REMOTE_SYNC);

		soundsToggleSwitch = new UGateToggleSwitchBox<>(
				controlBar.getRemoteNodePA(), RemoteNodeType.DEVICE_SOUNDS_ON,
				RS.IMG_SOUND_ON, RS.IMG_SOUND_OFF);
		controlBar.addHelpTextTrigger(soundsToggleSwitch, RS.rbLabel(KEYS.SERVICE_CMD_SOUNDS_TOGGLE));
		emailToggleSwitch = new UGateToggleSwitchBox<>(
				controlBar.getRemoteNodePA(), RemoteNodeType.MAIL_ALERT_ON,
				RS.IMG_EMAIL_NOTIFY_ON, RS.IMG_EMAIL_NOTIFY_OFF);
		controlBar.addHelpTextTrigger(emailToggleSwitch, RS.rbLabel(KEYS.MAIL_ALARM_NOTIFY_DESC));
		imgResToggleSwitch = new UGateToggleSwitchBox<>(
				controlBar.getRemoteNodePA(), RemoteNodeType.CAM_RESOLUTION,
				RS.IMG_CAM_TOGGLE_VGA, RS.IMG_CAM_TOGGLE_QVGA,
				RS.rbLabel(KEYS.CAM_RES_VGA),
				RS.rbLabel(KEYS.CAM_RES_QVGA));
		imgResToggleSwitch.getToggleItem().toggleSwitchImageView.setEffect(new DropShadow());
		controlBar.addHelpTextTrigger(imgResToggleSwitch, RS.rbLabel(KEYS.CAM_RES_DESC));
		syncToggleSwitch = new UGateToggleSwitchBox<>(
				controlBar.getRemoteNodePA(), RemoteNodeType.DEVICE_AUTO_SYNCHRONIZE,
				RS.IMG_SYNC_ON, RS.IMG_SYNC_OFF);
		controlBar.addHelpTextTrigger(syncToggleSwitch, RS.rbLabel(KEYS.WIRELESS_REMOTE_SYNC_DESC));

		final Parent generalCell = createCell(soundLabel, soundsToggleSwitch, emailLabel, emailToggleSwitch,
				imgResLabel, imgResToggleSwitch, syncResLabel, syncToggleSwitch);
		add(generalCell, columnIndex, rowIndex);
	}
	
	protected void addRemoteNodeSetupChildren(final int columnIndex, final int rowIndex) {
		final Label univRemoteLabel = createLabel(KEYS.WIRELESS_REMOTE_UNIVERSAL);
		final Label nodeLabel = createLabel(KEYS.WIRELESS_NODE_REMOTE_ADDY);
		nodeLabel.setPrefWidth(350d);
		
		remoteAddress = new UGateCtrlBox<>(controlBar.getRemoteNodePA(),
				RemoteNodeType.WIRELESS_ADDRESS, UGateCtrlBox.Type.TEXT,
				RS.rbLabel(KEYS.WIRELESS_NODE_REMOTE_ADDY), null);
		remoteAddress.label.getStyleClass().add("dialog-normal");
		controlBar.addHelpTextTrigger(remoteAddress, RS.rbLabel(
				KEYS.WIRELESS_NODE_REMOTE_ADDY_DESC,
				controlBar.getRemoteNode().getAddress()));
		workingDir = new UGateCtrlBox<>(controlBar.getRemoteNodePA(),
				RemoteNodeType.WIRELESS_WORKING_DIR_PATH,
				UGateCtrlBox.Type.DIR_CHOOSER,
				RS.rbLabel(KEYS.WIRELESS_WORKING_DIR), null);
		workingDir.label.getStyleClass().add("dialog-normal");
		controlBar.addHelpTextTrigger(workingDir,
				RS.rbLabel(KEYS.WIRELESS_WORKING_DIR_DESC));
		alarmMultistates = new AlarmMultistates(controlBar, Orientation.HORIZONTAL);

		universalRemoteAccessToggleSwitch = new UGateToggleSwitchBox<>(
				controlBar.getRemoteNodePA(),
				RemoteNodeType.UNIVERSAL_REMOTE_ACCESS_ON,
				RS.IMG_UNIVERSAL_REMOTE_ON, RS.IMG_UNIVERSAL_REMOTE_OFF);
		controlBar.addHelpTextTrigger(universalRemoteAccessToggleSwitch, 
				RS.rbLabel(KEYS.WIRELESS_REMOTE_UNIVERSAL_DESC, 
						controlBar.getRemoteNode().getAddress()));
		accessKey1 = new UGateCtrlBox<>(controlBar.getRemoteNodePA(),
				RemoteNodeType.UNIVERSAL_REMOTE_ACCESS_CODE_1,
				ACCESS_KEY_CODE_FORMAT, null, null, null, RS.rbLabel(
						KEYS.WIRELESS_ACCESS_KEY, 1), null);
	    controlBar.addHelpTextTrigger(accessKey1, RS.rbLabel(KEYS.WIRELESS_ACCESS_KEY_DESC, 1));
		accessKey2 = new UGateCtrlBox<>(controlBar.getRemoteNodePA(),
				RemoteNodeType.UNIVERSAL_REMOTE_ACCESS_CODE_2,
				ACCESS_KEY_CODE_FORMAT, null, null, null, RS.rbLabel(
						KEYS.WIRELESS_ACCESS_KEY, 2), null);
	    controlBar.addHelpTextTrigger(accessKey2, RS.rbLabel(KEYS.WIRELESS_ACCESS_KEY_DESC, 2));
		accessKey3 = new UGateCtrlBox<>(controlBar.getRemoteNodePA(),
				RemoteNodeType.UNIVERSAL_REMOTE_ACCESS_CODE_3,
				ACCESS_KEY_CODE_FORMAT, null, null, null, RS.rbLabel(
						KEYS.WIRELESS_ACCESS_KEY, 3), null);
	    controlBar.addHelpTextTrigger(accessKey3, RS.rbLabel(KEYS.WIRELESS_ACCESS_KEY_DESC, 3));
	    
	    final HBox accessKeysContainer = new HBox(5);
	    accessKeysContainer.getChildren().addAll(accessKey1, accessKey2, accessKey3);
	    
		final Parent setupCell = createCell(nodeLabel, remoteAddress, workingDir, alarmMultistates,
				univRemoteLabel, universalRemoteAccessToggleSwitch, accessKeysContainer);
		add(setupCell, columnIndex, rowIndex);
	}
	
	protected void addGateChildren(final int columnIndex, final int rowIndex) {
		final Label gateHeader = createLabel(KEYS.GATE_CONFIG);
		gateToggleSwitchView = new UGateToggleSwitchBox<>(
				controlBar.getRemoteNodePA(), RemoteNodeType.GATE_ACCESS_ON,
				RS.IMG_GATE_ON, RS.IMG_GATE_OFF);
		controlBar.addHelpTextTrigger(gateToggleSwitchView, RS.rbLabel(KEYS.GATE_TOGGLE));
		final Label gateCtrlHeader = createLabel(KEYS.GATE_STATE);
		final ImageView gateToggleImgView = RS.imgView(RS.IMG_GATE_CLOSED);
		final Button gateToggleBtn = new Button();
		gateToggleBtn.setCursor(Cursor.HAND);
		gateToggleBtn.setGraphic(gateToggleImgView);
		controlBar.addHelpTextTrigger(gateToggleBtn, RS.rbLabel(KEYS.GATE_TOGGLE_DESC));
		gateToggleBtn.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(final MouseEvent event) {
				if (GuiUtil.isPrimaryPress(event)) {
					gateToggleBtn.setDisable(true);
					if (controlBar.createCommandService(Command.GATE_TOGGLE_OPEN_CLOSE, true) == null) {
						gateToggleBtn.setDisable(false);
					}
				}
			}
		});
		controlBar.getSensorReadingsView().sensorReadingsProperty().addListener(
				new ChangeListener<RemoteNodeReading>() {
					@Override
					public void changed(final ObservableValue<? extends RemoteNodeReading> observable, 
							final RemoteNodeReading oldValue, final RemoteNodeReading newValue) {
						// when a command is sent to a remote node to open/close a gate a response for
						// sensor readings will be sent to the host where the gate state update is captured
						gateToggleImgView.setImage(newValue != null && newValue.getGateState() == 1 ? 
								RS.img(RS.IMG_GATE_OPENED) : RS.img(RS.IMG_GATE_CLOSED));
						gateToggleBtn.setDisable(false);
					}
				});
		
		final Parent cell = createCell(gateHeader, gateToggleSwitchView, 
				gateCtrlHeader, gateToggleBtn);
		add(cell, columnIndex, rowIndex);
	}
}
