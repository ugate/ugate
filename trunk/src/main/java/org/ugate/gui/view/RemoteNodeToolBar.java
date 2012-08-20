package org.ugate.gui.view;

import java.util.Collection;
import java.util.LinkedHashSet;

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
import org.ugate.service.entity.RemoteNodeType;
import org.ugate.service.entity.jpa.Host;
import org.ugate.service.entity.jpa.RemoteNode;

/**
 * {@linkplain ToolBar} for displaying the status of each of the {@linkplain RemoteNode}s
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
		initNodeItems();
		UGateKeeper.DEFAULT.addListener(new IGateKeeperListener() {
			@Override
			public void handle(final UGateKeeperEvent<?> event) {
				final boolean isNodeChange = event.getType() == UGateKeeperEvent.Type.SETTINGS_REMOTE_NODE_CHANGED_FROM_SELECT || 
					event.getType() == UGateKeeperEvent.Type.SETTINGS_REMOTE_NODE_CHANGED_FROM_ADD || 
					event.getType() == UGateKeeperEvent.Type.SETTINGS_REMOTE_NODE_CHANGED_FROM_REMOVE;
				final boolean isRemoteCommand = event.isFromRemote() && event.getCommand() != null;
				if (isNodeChange || isRemoteCommand) {
					final LinkedHashSet<String> adds = new LinkedHashSet<>();
					final LinkedHashSet<String> removes = new LinkedHashSet<>();
					boolean isSameAddy = false;
					for (final String me : event.getNodeAddresses()) {
						if (event.getType() == UGateKeeperEvent.Type.SETTINGS_REMOTE_NODE_CHANGED_FROM_ADD) {
							adds.add(me);
						}
						for (final Node node : getItems()) {
							if (node instanceof NodeStatusView) {
								isSameAddy = ((NodeStatusView) node).getRemoteNode().getAddress().equalsIgnoreCase(
										controlBar.getRemoteNode().getAddress());
								if (isSameAddy) {
									if (isRemoteCommand) {
										// blink status to indicate the a remote command has been received
										((NodeStatusView) node).updateLastCommand(event.getCommand());
										((NodeStatusView) node).blinkStart();
										break;
									} else if (event.getType() == UGateKeeperEvent.Type.SETTINGS_REMOTE_NODE_CHANGED_FROM_REMOVE) {
										removes.add(((NodeStatusView) node).getRemoteNode().getAddress());
									} else {
										((NodeStatusView) node).setStatusFill(true);
									}
								} else if (!isRemoteCommand && isSameAddy) {
									((NodeStatusView) node).setStatusFill(true);
								} else if (!isRemoteCommand) {
									((NodeStatusView) node).setStatusFill(false);
								}
							}
						}
					}
					// TODO : create batch transaction for all add/removes
					addOrRemoveNodeAddresses(adds, true);
					addOrRemoveNodeAddresses(removes, false);
				}
			}
		});
	}
	
	/**
	 * Creates all the available {@linkplain NodeStatusView}s from the
	 * {@linkplain ControlBar}'s {@linkplain Host#getRemoteNodes()}
	 */
	protected void initNodeItems() {
		if (controlBar.getActor().getHost().getRemoteNodes() != null) {
			for (final RemoteNode remoteNode : controlBar.getActor().getHost()
					.getRemoteNodes()) {
				addNodeItem(remoteNode);
			}
		}
	}

	/**
	 * Adds a {@linkplain NodeStatusView} for a {@linkplain RemoteNode}
	 * 
	 * @param remoteNode
	 *            the {@linkplain RemoteNode} to add a
	 *            {@linkplain NodeStatusView} for
	 */
	protected void addNodeItem(final RemoteNode remoteNode) {
		final NodeStatusView wnav = new NodeStatusView(controlBar, remoteNode);
		if (controlBar.getRemoteNode().getAddress()
				.equalsIgnoreCase(remoteNode.getAddress())) {
			wnav.setStatusFill(true);
		}
		getItems().add(wnav);
	}

	/**
	 * Adds the user interaction controls for adding/removing nodes
	 */
	protected void addGlobalNodeControls() {
		textField.setPromptText(RS.rbLabel(KEYS.WIRELESS_NODE_REMOTE_PROMPT));
		// textField.setTooltip(new
		// Tooltip(RS.rbLabel("wireless.node.remote")));
		textField.setMaxWidth(100d);
		controlBar.addHelpTextTrigger(textField,
				RS.rbLabel(KEYS.WIRELESS_NODE_REMOTE_ADDY_DESC));
		final Button addNodeButton = new FunctionButton(
				FunctionButton.Function.ADD, new Runnable() {
					@Override
					public void run() {
						addOrRemoveNodeAddress(textField.getText(), true);
					}
				});
		controlBar.addHelpTextTrigger(addNodeButton,
				RS.rbLabel(KEYS.WIRELESS_NODE_REMOTE_ADD_DESC));
		final Button removeNodeButton = new FunctionButton(
				FunctionButton.Function.REMOVE, new Runnable() {
					@Override
					public void run() {
						addOrRemoveNodeAddress(textField.getText(), false);
					}
				});
		controlBar.addHelpTextTrigger(removeNodeButton,
				RS.rbLabel(KEYS.WIRELESS_NODE_REMOTE_REMOVE_DESC));
		getItems().addAll(textField, addNodeButton, removeNodeButton,
				new Separator(Orientation.VERTICAL));
	}

	/**
	 * Adds/Removes a {@linkplain Collection} of node addresses. Validates that
	 * an address doesn't already exist when adding and validates that an
	 * address does exist when removing.
	 * 
	 * @param nodeAddress
	 *            the {@linkplain Collection} of node addresses to add or remove
	 * @param add
	 *            true to add, false to remove
	 */
	protected void addOrRemoveNodeAddresses(final Collection<String> nodeAddresses, final boolean add) {
		if (nodeAddresses != null) {
			for (final String na : nodeAddresses) {
				addOrRemoveNodeAddress(na, add);
			}
		}
	}

	/**
	 * Adds/Removes a nodes address. Validates that an address doesn't already
	 * exist when adding and validates that an address does exist when removing.
	 * 
	 * @param nodeAddress
	 *            the node address to add or remove
	 * @param add
	 *            true to add, false to remove
	 */
	protected void addOrRemoveNodeAddress(final String nodeAddress, final boolean add) {
		if (nodeAddress != null && !nodeAddress.isEmpty()) {
			RemoteNode rns = null;
			for (final Node node : getItems()) {
				rns = node instanceof NodeStatusView ? ((NodeStatusView) node).getRemoteNode() : null;
				if (rns != null && rns.getAddress().equalsIgnoreCase(nodeAddress)) {
					if (add) {
						log.warn(String.format("Cannot add a remote node %1$s because it already exists", 
								rns.getAddress()));
						return;
					} else {
						for (final RemoteNode rn : controlBar.getActor().getHost().getRemoteNodes()) {
							if (rns.getAddress().equalsIgnoreCase(rn.getAddress())) {
								controlBar.getActor().getHost().getRemoteNodes().remove(rn);
								try {
									ServiceProvider.IMPL.getCredentialService().mergeHost(
											controlBar.getActor().getHost());
									// select first available node
									controlBar.getRemoteNodePA().setBean(
											controlBar.getActor().getHost().getRemoteNodes()
											.iterator().next());
									getItems().remove(node);
								} catch (final Throwable t) {
									log.error(String.format("Unable to remove %1$s", 
											rn.getAddress()), t);
									controlBar.setHelpText(RS.rbLabel(
											KEYS.WIRELESS_NODE_REMOVE_FAILED, 
											rns.getAddress()));
								}
								return;
							}
						}
						return;
					}
				}
			}
			if (add && rns == null) {
				rns = RemoteNodeType.newDefaultRemoteNode(controlBar.getActor().getHost());
				rns.setAddress(nodeAddress);
				controlBar.getActor().getHost().getRemoteNodes().add(rns);
				try {
					ServiceProvider.IMPL.getCredentialService().mergeHost(
							controlBar.getActor().getHost());
					controlBar.getRemoteNodePA().setBean(rns);
					addNodeItem(rns);
				} catch (final Throwable t) {
					log.error(String.format("Unable to add %1$s", 
							rns.getAddress()), t);
					controlBar.setHelpText(RS.rbLabel(
							KEYS.WIRELESS_NODE_ADD_FAILED, rns.getAddress()));
				}
			}
		}
	}
	
	/**
	 * Node status view 
	 */
	protected static final class NodeStatusView extends StatusView {

		private final RemoteNode remoteNode;
		private final StringProperty helpTextStringProperty;
		
		/**
		 * Constructor
		 * 
		 * @param controlBar
		 *            the {@linkplain ControlBar}
		 * @param remoteNode
		 *            the {@linkplain RemoteNode} the
		 *            {@linkplain NodeStatusView} is for
		 */
		public NodeStatusView(final ControlBar controlBar, final RemoteNode remoteNode) {
			super(controlBar, false, GuiUtil.COLOR_UNSELECTED, null, GuiUtil.COLOR_SELECTING);
			this.remoteNode = remoteNode;
			this.helpTextStringProperty = new SimpleStringProperty();
			setHelpText("");
			setCursor(Cursor.HAND);
			addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(final MouseEvent event) {
					if (GuiUtil.isPrimaryPress(event)) {
						final RemoteNode rn = ServiceProvider.IMPL.getWirelessService()
								.findRemoteNodeByAddress(getRemoteNode().getAddress());
						if (rn == null) {
							controlBar.setHelpText(RS.rbLabel(
									KEYS.WIRELESS_NODE_REMOTE_SELECT_FAILED, 
									getRemoteNode().getAddress()));
						} else {
							controlBar.getRemoteNodePA().setBean(rn);
						}
						blinkStop(true);
					}
				}
			});
			controlBar.addHelpTextTrigger(this, this.helpTextStringProperty);
			final HBox hbox = new HBox();
			hbox.setAlignment(Pos.CENTER);
			final Label addressLabel = new Label(this.remoteNode.getAddress());
			hbox.getChildren().addAll(addressLabel, statusIcon);
			getChildren().add(hbox);
		}
		
		/**
		 * Sets the help text for the status view
		 * 
		 * @param commandDesc
		 *            the node command description
		 */
		protected void setHelpText(final String commandDesc) {
			this.helpTextStringProperty.set(RS.rbLabel(
					KEYS.WIRELESS_NODE_REMOTE_STATUS, getRemoteNode()
							.getAddress(), commandDesc));
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
		 * @return the {@linkplain RemoteNode}
		 */
		public RemoteNode getRemoteNode() {
			return remoteNode;
		}
	}
}
