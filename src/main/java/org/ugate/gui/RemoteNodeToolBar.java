package org.ugate.gui;

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
import javafx.scene.layout.Region;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ugate.Command;
import org.ugate.IGateKeeperListener;
import org.ugate.UGateKeeper;
import org.ugate.UGateKeeperEvent;
import org.ugate.resources.RS;

/**
 * {@linkplain ToolBar} for displaying the status of each of the remote nodes
 */
public class RemoteNodeToolBar extends ToolBar {

	private static final Logger log = LoggerFactory.getLogger(RemoteNodeToolBar.class);
	private static final double BUTTON_ADD_REMOVE_SIZE = 12d;
	protected final ControlBar controlBar;
	private final TextField textField = new TextField();
	private final Region addNodeGraphic = new Region();
	private final Region removeNodeGraphic = new Region();
	
	/**
	 * Constructor
	 * 
	 * @param controlBar the control bar
	 * @param orientation the {@linkplain Orientation}
	 */
	public RemoteNodeToolBar(final ControlBar controlBar, final Orientation orientation) {
		super();
		getStyleClass().add("map-toolbar");
		setOrientation(orientation);
		this.controlBar = controlBar;
		addGlobalNodeControls();
		addNodeIcons(UGateKeeper.DEFAULT.wirelessGetRemoteAddressMap());
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
					//final int currNodeIndex = UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex();
					final String currNodeAddesss = UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeAddress();
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
				if (wn.getKey() == UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex()) {
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
		textField.setPromptText(RS.rbLabel("wireless.node.remote.prompt"));
//		textField.setTooltip(new Tooltip(RS.rbLabel("wireless.node.remote")));
		textField.setMaxWidth(100d);
		controlBar.addHelpTextTrigger(textField, RS.rbLabel("wireless.node.remote.desc"));
		final Button addNodeButton = new Button();
		addNodeButton.getStyleClass().add("button-mini");
		addNodeButton.setCursor(Cursor.HAND);
		addNodeButton.setMaxSize(BUTTON_ADD_REMOVE_SIZE, BUTTON_ADD_REMOVE_SIZE);
		addNodeButton.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(final MouseEvent event) {
				if (GuiUtil.isPrimaryPress(event)) {
					addOrRemoveNodeAddress(textField.getText(), true);
				}
			}
		});
		controlBar.addHelpTextTrigger(addNodeButton, RS.rbLabel("wireless.node.remote.add.desc"));
		addNodeGraphic.getStyleClass().setAll(new String[] { "text-field-menu-add-choice" });
		addNodeButton.setGraphic(addNodeGraphic);
		final Button removeNodeButton = new Button();
		removeNodeButton.getStyleClass().add("button-mini");
		removeNodeButton.setCursor(Cursor.HAND);
		removeNodeButton.setMaxSize(BUTTON_ADD_REMOVE_SIZE, BUTTON_ADD_REMOVE_SIZE);
		removeNodeButton.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(final MouseEvent event) {
				if (GuiUtil.isPrimaryPress(event)) {
					addOrRemoveNodeAddress(textField.getText(), false);
				}
			}
		});
		controlBar.addHelpTextTrigger(removeNodeButton, RS.rbLabel("wireless.node.remote.remove.desc"));
		removeNodeGraphic.getStyleClass().setAll(new String[] { "text-field-menu-remove-choice" });
		removeNodeButton.setGraphic(removeNodeGraphic);
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
						UGateKeeper.DEFAULT.wirelessRemoveNode(((NodeStatusView) node).getNodeAddress());
						return;
					}
				}
			}
			if (add) {
				UGateKeeper.DEFAULT.wirelessSetRemoteNode(nodeAddress);
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
						UGateKeeper.DEFAULT.wirelessSetRemoteNode(getNodeAddress());
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
			this.helpTextStringProperty.set(RS.rbLabel("wireless.node.remote.status", getNodeAddress(), commandDesc));
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
