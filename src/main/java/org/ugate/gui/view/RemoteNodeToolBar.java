package org.ugate.gui.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ugate.Command;
import org.ugate.IGateKeeperListener;
import org.ugate.UGateKeeper;
import org.ugate.UGateKeeperEvent;
import org.ugate.gui.ControlBar;
import org.ugate.gui.GuiUtil;
import org.ugate.gui.components.FunctionButton;
import org.ugate.resources.RS;
import org.ugate.resources.RS.KEYS;
import org.ugate.service.ServiceProvider;

/**
 * {@linkplain ToolBar} for displaying the status of each of the remote nodes
 */
public class RemoteNodeToolBar extends ToolBar {

	private static final Logger log = LoggerFactory.getLogger(RemoteNodeToolBar.class);
	protected final ControlBar controlBar;
	private final TextField textField = new TextField();
	
	/**
	 * Constructor
	 * 
	 * @param controlBar the control bar
	 * @param orientation the {@linkplain Orientation}
	 */
	public RemoteNodeToolBar(final ControlBar controlBar, final Orientation orientation) {
		super();
		getStyleClass().add("dialog-base");
		setOrientation(orientation);
		this.controlBar = controlBar;
		addGlobalNodeControls();
		addNodeIcons(ServiceProvider.IMPL.getWirelessService().getRemoteNodeAddressMap());
		UGateKeeper.DEFAULT.addListener(new IGateKeeperListener() {
			@Override
			public void handle(final UGateKeeperEvent<?> event) {
				final boolean isNodeChange = event.getType() == UGateKeeperEvent.Type.SETTINGS_REMOTE_NODE_CHANGED_FROM_SELECT || 
					event.getType() == UGateKeeperEvent.Type.SETTINGS_REMOTE_NODE_CHANGED_FROM_ADD || 
					event.getType() == UGateKeeperEvent.Type.SETTINGS_REMOTE_NODE_CHANGED_FROM_REMOVE;
				final boolean isRemoteCommand = event.isFromRemote() && event.getCommand() != null;
				if (isNodeChange || isRemoteCommand) {
					final Map<Integer, String> adds = new HashMap<Integer, String>();
					final List<NodeStatusView> removes = new ArrayList<NodeStatusView>();
					//final int currNodeIndex = ServiceProvider.IMPL.getWirelessService().wirelessGetCurrentRemoteNodeIndex();
					final String currNodeAddesss = ServiceProvider.IMPL.getWirelessService().getCurrentRemoteNodeAddress();
					for (final Map.Entry<Integer, String> me : event.getNodeAddresses().entrySet()) {
						if (event.getType() == UGateKeeperEvent.Type.SETTINGS_REMOTE_NODE_CHANGED_FROM_ADD) {
							adds.put(me.getKey(), me.getValue());
						}
						for (final Node node : getItems()) {
							if (node instanceof NodeStatusView) {
								if (((NodeStatusView) node).getNodeAddress().equalsIgnoreCase(me.getValue())) {
									if (isRemoteCommand) {
										// blink status to indicate the a remote command has been received
										((NodeStatusView) node).updateLastCommand(event.getCommand());
										((NodeStatusView) node).blinkStart();
										break;
									} else if (event.getType() == UGateKeeperEvent.Type.SETTINGS_REMOTE_NODE_CHANGED_FROM_REMOVE) {
										removes.add((NodeStatusView) node);
									} else {
										((NodeStatusView) node).setStatusFill(true);
									}
								} else if (!isRemoteCommand && ((NodeStatusView) node).getNodeAddress().equalsIgnoreCase(currNodeAddesss)) {
									((NodeStatusView) node).setStatusFill(true);
								} else if (!isRemoteCommand) {
									((NodeStatusView) node).setStatusFill(false);
								}
							}
						}
					}
					addNodeIcons(adds);
					if (removes != null && !removes.isEmpty()) {
						getItems().removeAll(removes);
					}
				}
			}
		});
	}
	
	/**
	 * Creates all the available remote nodes statuses
	 * 
	 * @param addressesToAdd the node addresses (key=node index, value=node address) to add
	 */
	protected void addNodeIcons(final Map<Integer, String> addressesToAdd) {
		if (addressesToAdd != null) {
			for (final Map.Entry<Integer, String> wn : addressesToAdd.entrySet()) {
				final NodeStatusView wnav = new NodeStatusView(controlBar, wn.getKey(), wn.getValue());
				if (wn.getKey() == ServiceProvider.IMPL.getWirelessService().getCurrentRemoteNodeIndex()) {
					wnav.setStatusFill(true);
				}
				getItems().add(wnav);
			}
		}
	}
	
	/**
	 * Adds the user interaction controls for adding/removing nodes
	 */
	protected void addGlobalNodeControls() {
		textField.setPromptText(RS.rbLabel(KEYS.WIRELESS_NODE_REMOTE_PROMPT));
//		textField.setTooltip(new Tooltip(RS.rbLabel("wireless.node.remote")));
		textField.setMaxWidth(100d);
		controlBar.addHelpTextTrigger(textField, RS.rbLabel(KEYS.WIRELESS_NODE_REMOTE_ADDY_DESC));
		final Button addNodeButton = new FunctionButton(FunctionButton.Function.ADD, 
				new Runnable() {
					@Override
					public void run() {
						addOrRemoveNodeAddress(textField.getText(), true);
					}
				});
		controlBar.addHelpTextTrigger(addNodeButton, RS.rbLabel(KEYS.WIRELESS_NODE_REMOTE_ADD_DESC));
		final Button removeNodeButton = new FunctionButton(
				FunctionButton.Function.REMOVE, new Runnable() {
					@Override
					public void run() {
						addOrRemoveNodeAddress(textField.getText(), false);
					}
				});
		controlBar.addHelpTextTrigger(removeNodeButton, RS.rbLabel(KEYS.WIRELESS_NODE_REMOTE_REMOVE_DESC));
		getItems().addAll(textField, addNodeButton, removeNodeButton, new Separator(Orientation.VERTICAL));
	}
	
	/**
	 * Adds/Removes a nodes address from the {@linkplain UGateKeeper#DEFAULT}. Validates that an address 
	 * doesn't already exist when adding and validates that an address does exist when removing.
	 * 
	 * @param nodeAddress the node address to add or remove
	 * @param add true to add, false to remove
	 */
	protected void addOrRemoveNodeAddress(final String nodeAddress, final boolean add) {
		if (nodeAddress != null && !nodeAddress.isEmpty()) {
			for (final Node node : getItems()) {
				if (node instanceof NodeStatusView && 
						((NodeStatusView) node).getNodeAddress().equalsIgnoreCase(nodeAddress)) {
					if (add) {
						log.warn("Cannot add a remote node address that already exists");
						return;
					} else {
						ServiceProvider.IMPL.getWirelessService().removeNode(((NodeStatusView) node).getNodeAddress());
						return;
					}
				}
			}
			if (add) {
				ServiceProvider.IMPL.getWirelessService().setRemoteNode(nodeAddress);
			}
		}
	}
	
	/**
	 * Node status view 
	 */
	protected static final class NodeStatusView extends StatusView {
		
		private final int nodeIndex;
		private final String nodeAddress;
		private final StringProperty helpTextStringProperty;
		
		/**
		 * Constructor
		 * 
		 * @param controlBar the control bar
		 * @param nodeIndex the node index
		 */
		public NodeStatusView(final ControlBar controlBar, final int nodeIndex, final String nodeAddress) {
			super(controlBar, false, GuiUtil.COLOR_UNSELECTED, null, GuiUtil.COLOR_SELECTING);
			this.nodeIndex = nodeIndex;
			this.nodeAddress = nodeAddress;
			this.helpTextStringProperty = new SimpleStringProperty();
			setHelpText("");
			setCursor(Cursor.HAND);
			addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(final MouseEvent event) {
					if (GuiUtil.isPrimaryPress(event)) {
						ServiceProvider.IMPL.getWirelessService().setRemoteNode(getNodeAddress());
						blinkStop(true);
					}
				}
			});
			controlBar.addHelpTextTrigger(this, this.helpTextStringProperty);
			final HBox hbox = new HBox();
			hbox.setAlignment(Pos.CENTER);
			final Label addressLabel = new Label(this.nodeAddress);
			hbox.getChildren().addAll(addressLabel, statusIcon);
			getChildren().add(hbox);
		}
		
		/**
		 * Sets the help text for the status view
		 * 
		 * @param commandDesc the node command description
		 */
		protected void setHelpText(final String commandDesc) {
			this.helpTextStringProperty.set(RS.rbLabel(KEYS.WIRELESS_NODE_REMOTE_STATUS, getNodeAddress(), commandDesc));
		}
		
		/**
		 * Updates the help text of the node status using a reference to the supplied {@linkplain Command}
		 * 
		 * @param command the {@linkplain Command} to use in the help text
		 */
		public void updateLastCommand(final Command command) {
			setHelpText(command.toString());
		}
		
		/**
		 * @return the address of the node
		 */
		public String getNodeAddress() {
			return nodeAddress;
		}
		
		/**
		 * @return the node index
		 */
		public int getNodeIndex() {
			return nodeIndex;
		}
	}
}
