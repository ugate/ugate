package org.ugate.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

import org.ugate.Command;
import org.ugate.RemoteSettings;
import org.ugate.UGateKeeper;
import org.ugate.gui.components.UGateCtrlView;
import org.ugate.gui.components.UGateToggleSwitchView;
import org.ugate.resources.RS;
import org.ugate.service.RemoteNodeType;
import org.ugate.service.entity.jpa.RemoteNode;
import org.ugate.wireless.data.RxTxSensorReadings;

/**
 * Node configuration
 */
public class AccessSettings extends ControlPane {

	private UGateToggleSwitchView<RemoteNode> universalRemoteAccessToggleSwitch;
	private UGateCtrlView<RemoteNode, Void> remoteAddress;
	private UGateCtrlView<RemoteNode, Void> workingDir;
	private UGateCtrlView<RemoteNode, Void> accessKey1;
	private UGateCtrlView<RemoteNode, Void> accessKey2;
	private UGateCtrlView<RemoteNode, Void> accessKey3;
	private UGateToggleSwitchView<RemoteNode> gateToggleSwitchView;

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
		final Label nodeLabel = createLabel("wireless.node.remote");

		remoteAddress = new UGateCtrlView<>(controlBar.getRemoteNodePA(),
				RemoteNodeType.WIRELESS_ADDRESS, UGateCtrlView.Type.TYPE_TEXT,
				RS.rbLabel("wireless.remote"), null);
		controlBar.addHelpTextTrigger(remoteAddress, RS.rbLabel(
				"wireless.remote.desc",
				UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex()));
		workingDir = new UGateCtrlView<>(controlBar.getRemoteNodePA(),
				RemoteNodeType.WIRELESS_WORKING_DIR_PATH,
				UGateCtrlView.Type.TYPE_TEXT,
				RS.rbLabel("wireless.workingdir"), null);
		controlBar.addHelpTextTrigger(workingDir,
				RS.rbLabel("wireless.workingdir.desc"));
	    final Button update = new Button(RS.rbLabel("update"));
	    update.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(final MouseEvent event) {
				if (GuiUtil.isPrimaryPress(event)) {
					
					UGateKeeper.DEFAULT.settingsSet(RemoteSettings.WIRELESS_ADDRESS_NODE, 
							UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex(), 
							remoteAddress.getValue().toString());
					UGateKeeper.DEFAULT.settingsSet(RemoteSettings.WIRELESS_WORKING_DIR_PATH, 
							UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex(), 
							workingDir.getValue().toString());
				}
			}
		});
		
	    final Group nodeConnectionCell = createCell(false, true, nodeLabel, remoteAddress, workingDir, update);
		add(nodeConnectionCell, 0, 0);
	}
	
	protected void addUniveralRemoteChildren() {
		final Label univRemoteLabel = createLabel("wireless.remote.universal");
		universalRemoteAccessToggleSwitch = new UGateToggleSwitchView<>(
				controlBar.getRemoteNodePA(),
				RemoteNodeType.UNIVERSAL_REMOTE_ACCESS_ON,
				RS.IMG_UNIVERSAL_REMOTE_ON, RS.IMG_UNIVERSAL_REMOTE_OFF);
		controlBar.addHelpTextTrigger(universalRemoteAccessToggleSwitch, 
				RS.rbLabel("wireless.remote.universal.desc", 
						UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex()));
		accessKey1 = new UGateCtrlView<>(controlBar.getRemoteNodePA(),
				RemoteNodeType.UNIVERSAL_REMOTE_ACCESS_CODE_1,
				ACCESS_KEY_CODE_FORMAT, null, null, null, RS.rbLabel(
						"wireless.access.key", 1), null);
	    controlBar.addHelpTextTrigger(accessKey1, RS.rbLabel("wireless.access.key.desc", 1));
		accessKey2 = new UGateCtrlView<>(controlBar.getRemoteNodePA(),
				RemoteNodeType.UNIVERSAL_REMOTE_ACCESS_CODE_2,
				ACCESS_KEY_CODE_FORMAT, null, null, null, RS.rbLabel(
						"wireless.access.key", 2), null);
	    controlBar.addHelpTextTrigger(accessKey2, RS.rbLabel("wireless.access.key.desc", 2));
		accessKey3 = new UGateCtrlView<>(controlBar.getRemoteNodePA(),
				RemoteNodeType.UNIVERSAL_REMOTE_ACCESS_CODE_3,
				ACCESS_KEY_CODE_FORMAT, null, null, null, RS.rbLabel(
						"wireless.access.key", 3), null);
	    controlBar.addHelpTextTrigger(accessKey3, RS.rbLabel("wireless.access.key.desc", 3));
	    
	    final HBox accessKeysContainer = new HBox(5);
	    accessKeysContainer.getChildren().addAll(accessKey1, accessKey2, accessKey3);
	    
		final Group univRemoteCell = createCell(false, true, univRemoteLabel, universalRemoteAccessToggleSwitch, accessKeysContainer);
		add(univRemoteCell, 1, 0);
	}
	
	protected void addGateChildren() {
		final Label gateHeader = createLabel("gate.conf");
		gateToggleSwitchView = new UGateToggleSwitchView<>(
				controlBar.getRemoteNodePA(), RemoteNodeType.GATE_ACCESS_ON,
				RS.IMG_GATE_ON, RS.IMG_GATE_OFF);
		controlBar.addHelpTextTrigger(gateToggleSwitchView, RS.rbLabel("gate.toggle"));
		final Label gateCtrlHeader = createLabel("gate.state");
		final ImageView gateToggleButton = RS.imgView(RS.IMG_GATE_CLOSED);
		final Region gateGroup = GuiUtil.createBackgroundDisplay(PADDING_INSETS, CHILD_SPACING, 
				1, false, gateToggleButton);
		gateGroup.setCursor(Cursor.HAND);
		controlBar.addHelpTextTrigger(gateGroup, RS.rbLabel("gate.toggle.desc"));
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
