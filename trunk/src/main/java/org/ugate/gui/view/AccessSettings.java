package org.ugate.gui.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

import org.ugate.Command;
import org.ugate.gui.ControlBar;
import org.ugate.gui.ControlPane;
import org.ugate.gui.GuiUtil;
import org.ugate.gui.components.UGateCtrlBox;
import org.ugate.gui.components.UGateToggleSwitchBox;
import org.ugate.resources.RS;
import org.ugate.resources.RS.KEYS;
import org.ugate.service.entity.RemoteNodeType;
import org.ugate.service.entity.jpa.RemoteNode;
import org.ugate.wireless.data.RxTxSensorReadings;

/**
 * Node configuration
 */
public class AccessSettings extends ControlPane {

	private UGateToggleSwitchBox<RemoteNode> universalRemoteAccessToggleSwitch;
	private UGateCtrlBox<RemoteNode, Void, Void> remoteAddress;
	private UGateCtrlBox<RemoteNode, Void, Void> workingDir;
	private UGateCtrlBox<RemoteNode, Void, Void> accessKey1;
	private UGateCtrlBox<RemoteNode, Void, Void> accessKey2;
	private UGateCtrlBox<RemoteNode, Void, Void> accessKey3;
	private UGateToggleSwitchBox<RemoteNode> gateToggleSwitchView;

	/**
	 * Constructor
	 * 
	 * @param controlBar the control bar
	 */
	public AccessSettings(final ControlBar controlBar) {
		super(controlBar);
		addConnectionChildren();
		addUniveralRemoteChildren();
		addGateChildren();
	}

	protected void addConnectionChildren() {
		final Label nodeLabel = createLabel(KEYS.WIRELESS_NODE_REMOTE_ADDY);

		remoteAddress = new UGateCtrlBox<>(controlBar.getRemoteNodePA(),
				RemoteNodeType.WIRELESS_ADDRESS, UGateCtrlBox.Type.TEXT,
				RS.rbLabel(KEYS.WIRELESS_NODE_REMOTE_ADDY), null);
		controlBar.addHelpTextTrigger(remoteAddress, RS.rbLabel(
				KEYS.WIRELESS_NODE_REMOTE_ADDY_DESC,
				controlBar.getRemoteNode().getAddress()));
		workingDir = new UGateCtrlBox<>(controlBar.getRemoteNodePA(),
				RemoteNodeType.WIRELESS_WORKING_DIR_PATH,
				UGateCtrlBox.Type.DIR_CHOOSER,
				RS.rbLabel(KEYS.WIRELESS_WORKING_DIR), null);
		controlBar.addHelpTextTrigger(workingDir,
				RS.rbLabel(KEYS.WIRELESS_WORKING_DIR_DESC));
	    final Group nodeConnectionCell = createCell(false, true, nodeLabel, remoteAddress, workingDir);
		add(nodeConnectionCell, 0, 0);
	}
	
	protected void addUniveralRemoteChildren() {
		final Label univRemoteLabel = createLabel(KEYS.WIRELESS_REMOTE_UNIVERSAL);
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
	    
		final Group univRemoteCell = createCell(false, true, univRemoteLabel, universalRemoteAccessToggleSwitch, accessKeysContainer);
		add(univRemoteCell, 1, 0);
	}
	
	protected void addGateChildren() {
		final Label gateHeader = createLabel(KEYS.GATE_CONFIG);
		gateToggleSwitchView = new UGateToggleSwitchBox<>(
				controlBar.getRemoteNodePA(), RemoteNodeType.GATE_ACCESS_ON,
				RS.IMG_GATE_ON, RS.IMG_GATE_OFF);
		controlBar.addHelpTextTrigger(gateToggleSwitchView, RS.rbLabel(KEYS.GATE_TOGGLE));
		final Label gateCtrlHeader = createLabel(KEYS.GATE_STATE);
		final ImageView gateToggleButton = RS.imgView(RS.IMG_GATE_CLOSED);
		final Region gateGroup = GuiUtil.createBackgroundDisplay(PADDING_INSETS, CHILD_SPACING, 
				1, false, gateToggleButton);
		gateGroup.setCursor(Cursor.HAND);
		controlBar.addHelpTextTrigger(gateGroup, RS.rbLabel(KEYS.GATE_TOGGLE_DESC));
		gateGroup.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(final MouseEvent event) {
				if (GuiUtil.isPrimaryPress(event)) {
					gateToggleButton.setDisable(true);
					if (controlBar.createCommandService(Command.GATE_TOGGLE_OPEN_CLOSE, true) == null) {
						gateToggleButton.setDisable(false);
					}
				}
			}
		});
		controlBar.sensorReadingsProperty().addListener(new ChangeListener<RxTxSensorReadings>() {
			@Override
			public void changed(final ObservableValue<? extends RxTxSensorReadings> observable, 
					final RxTxSensorReadings oldValue, final RxTxSensorReadings newValue) {
				// when a command is sent to a remote node to open/close a gate a response for
				// sensor readings will be sent to the host where the gate state update is captured
				gateToggleButton.setImage(newValue.getGateState() == 1 ? RS.img(RS.IMG_GATE_OPENED) : 
					RS.img(RS.IMG_GATE_CLOSED));
				gateToggleButton.setDisable(false);
			}
		});
		
		final Group cell = createCell(false, true, gateHeader, gateToggleSwitchView, 
				gateCtrlHeader, gateGroup);
		add(cell, 2, 0);
	}
}
