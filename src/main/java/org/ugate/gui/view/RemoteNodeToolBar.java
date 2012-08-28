package org.ugate.gui.view;

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
import org.ugate.UGateListener;
import org.ugate.UGateKeeper;
import org.ugate.UGateEvent;
import org.ugate.gui.ControlBar;
import org.ugate.gui.GuiUtil;
import org.ugate.gui.components.FunctionButton;
import org.ugate.resources.RS;
import org.ugate.resources.RS.KEYS;
import org.ugate.service.ServiceProvider;
import org.ugate.service.entity.RemoteNodeType;
import org.ugate.service.entity.jpa.Host;
import org.ugate.service.entity.jpa.RemoteNode;
import org.ugate.wireless.data.RxTxRemoteNodeDTO;

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
		registerListeners();
	}

	/**
	 * Registers any {@linkplain UGateListener}s
	 */
	protected final void registerListeners() {
		UGateKeeper.DEFAULT.addListener(new UGateListener() {
			@Override
			public void handle(final UGateEvent<?, ?> event) {
				final boolean isNodeChange = event.getType() == UGateEvent.Type.WIRELESS_REMOTE_NODE_CHANGING_FROM_SELECT || 
					event.getType() == UGateEvent.Type.WIRELESS_REMOTE_NODE_CHANGING_FROM_ADD || 
					event.getType() == UGateEvent.Type.WIRELESS_REMOTE_NODE_CHANGING_FROM_REMOVE;
				final boolean isRemoteCommand = event.isFromRemote() && event.getCommand() != null;
				if (isNodeChange || isRemoteCommand) {
					// commit selection/add/remove changes
					boolean isSameAddy = false;
					boolean isRemoteNodeChanged = false;
					for (final Node node : getItems()) {
						if (node instanceof NodeStatusView) {
							isSameAddy = ((NodeStatusView) node).getRemoteNode().getAddress().equalsIgnoreCase(
									controlBar.getRemoteNode().getAddress());
							if (isSameAddy) {
								if (isRemoteCommand) {
									// blink status to indicate the a remote command has been received
									((NodeStatusView) node).updateLastCommand(event.getCommand());
								} else if (event.getType() == UGateEvent.Type.WIRELESS_REMOTE_NODE_CHANGING_FROM_REMOVE) {
									addOrRemoveNodeAddress(((RemoteNode) event.getSource()).getAddress(), false, true);
									isRemoteNodeChanged = true;
								} else if (event.getType() == UGateEvent.Type.WIRELESS_REMOTE_NODE_CHANGING_FROM_ADD) {
									addOrRemoveNodeAddress(((RemoteNode) event.getSource()).getAddress(), true, true);
									isRemoteNodeChanged = true;
								} else if (event.getType() == UGateEvent.Type.WIRELESS_REMOTE_NODE_CHANGING_FROM_SELECT) {
									controlBar.getRemoteNodePA().setBean((RemoteNode) event.getSource());
									isRemoteNodeChanged = true;
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
					if (!isRemoteNodeChanged
							&& event.getType() == UGateEvent.Type.WIRELESS_REMOTE_NODE_CHANGING_FROM_SELECT) {
						controlBar.setHelpText(RS.rbLabel(
								KEYS.WIRELESS_NODE_REMOTE_SELECT_FAILED,
								((RemoteNode) event.getSource()).getAddress()));
					} else if (isRemoteNodeChanged) {
						UGateKeeper.DEFAULT
								.notifyListeners(new UGateEvent<RemoteNode, RemoteNode>(
										(RemoteNode) event.getSource(),
										UGateEvent.Type.WIRELESS_REMOTE_NODE_CHANGED,
										false, null, null, (RemoteNode) event
												.getSource(), controlBar
												.getRemoteNode()));
					}
				} else if (isRemoteCommand && event.getType() == UGateEvent.Type.WIRELESS_REMOTE_NODE_COMMITTED) {
					// notify the user that the remote node has successfully been committed locally
					final RemoteNode rn = (RemoteNode) event.getSource();
					final NodeStatusView nsv = getNodeStatusView(rn);
					if (nsv != null) {
						// blink status to indicate the a remote command has been received
						nsv.updateLastCommand(event.getCommand());
						controlBar.setHelpText(RS.rbLabel(
								KEYS.WIRELESS_NODE_REMOTE_SAVED_LOCAL,
								((RemoteNode) event.getSource()).getAddress()));
					}
				} else if (event.getType() == UGateEvent.Type.WIRELESS_DATA_RX_SUCCESS) {
					final RemoteNode rn = (RemoteNode) event.getSource();
					if (event.getNewValue() instanceof RxTxRemoteNodeDTO) {
						final RxTxRemoteNodeDTO ndto = (RxTxRemoteNodeDTO) event.getNewValue();
						if (!RemoteNodeType.remoteEquivalent(rn, ndto.getRemoteNode())) {
							// remote device values do not match the local device values
							rn.setDeviceSynchronized(false);
							ndto.getRemoteNode().setDeviceSynchronized(false);
							final NodeStatusView nsv = getNodeStatusView(rn);
							// blink status to indicate the a remote device is out of sync
							nsv.updateLastCommand(event.getCommand());
							controlBar.setHelpText(RS.rbLabel(
									KEYS.WIRELESS_NODE_REMOTE_SAVED_LOCAL,
									((RemoteNode) event.getSource()).getAddress()));
						}
					}
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
						if (textField.getText().isEmpty()) {
							return;
						}
						final RemoteNode rn = RemoteNodeType.newDefaultRemoteNode(
								controlBar.getActor().getHost());
						rn.setAddress(textField.getText());
						UGateKeeper.DEFAULT.notifyListeners(
								new UGateEvent<RemoteNode, Void>(
										rn,
										UGateEvent.Type.WIRELESS_REMOTE_NODE_CHANGING_FROM_ADD,
										false));
					}
				});
		controlBar.addHelpTextTrigger(addNodeButton,
				RS.rbLabel(KEYS.WIRELESS_NODE_REMOTE_ADD_DESC));
		final Button removeNodeButton = new FunctionButton(
				FunctionButton.Function.REMOVE, new Runnable() {
					@Override
					public void run() {
						if (textField.getText().isEmpty()) {
							return;
						}
						final RemoteNode rn = RemoteNodeType.newDefaultRemoteNode(
								controlBar.getActor().getHost());
						rn.setAddress(textField.getText());
						UGateKeeper.DEFAULT.notifyListeners(
								new UGateEvent<RemoteNode, Void>(
										rn,
										UGateEvent.Type.WIRELESS_REMOTE_NODE_CHANGING_FROM_REMOVE,
										false));
					}
				});
		controlBar.addHelpTextTrigger(removeNodeButton,
				RS.rbLabel(KEYS.WIRELESS_NODE_REMOTE_REMOVE_DESC));
		getItems().addAll(textField, addNodeButton, removeNodeButton,
				new Separator(Orientation.VERTICAL));
	}

	/**
	 * Adds/Removes a nodes address. Validates that an address doesn't already
	 * exist when adding and validates that an address does exist when removing.
	 * 
	 * @param nodeAddress
	 *            the node address to add or remove
	 * @param add
	 *            true to add, false to remove
	 * @param commit
	 *            true to commit the changes to the {@linkplain RemoteNode}
	 * @return true when successful
	 */
	protected boolean addOrRemoveNodeAddress(final String nodeAddress,
			final boolean add, final boolean commit) {
		if (addOrRemoveNodeAddress(nodeAddress, add) && commit) {
			try {
				ServiceProvider.IMPL.getCredentialService().mergeHost(
						controlBar.getActor().getHost());
				return true;
			} catch (final Throwable t) {
				log.error(String.format("Unable to commit changes to %1$s", 
						nodeAddress), t);
			}
		}
		return false;
	}

	/**
	 * Adds/Removes a nodes address. Validates that an address doesn't already
	 * exist when adding and validates that an address does exist when removing.
	 * 
	 * @param nodeAddress
	 *            the node address to add or remove
	 * @param add
	 *            true to add, false to remove
	 * @return true when successful
	 */
	private boolean addOrRemoveNodeAddress(final String nodeAddress, final boolean add) {
		if (nodeAddress != null && !nodeAddress.isEmpty()) {
			RemoteNode rns = null;
			for (final Node node : getItems()) {
				rns = node instanceof NodeStatusView ? ((NodeStatusView) node).getRemoteNode() : null;
				if (rns != null && rns.getAddress().equalsIgnoreCase(nodeAddress)) {
					if (add) {
						log.warn(String.format("Cannot add a remote node %1$s because it already exists", 
								rns.getAddress()));
						return false;
					} else {
						for (final RemoteNode rn : controlBar.getActor().getHost().getRemoteNodes()) {
							if (rns.getAddress().equalsIgnoreCase(rn.getAddress())) {
								controlBar.getActor().getHost().getRemoteNodes().remove(rn);
								try {
									// select first available node
									controlBar.getRemoteNodePA().setBean(
											controlBar.getActor().getHost().getRemoteNodes()
											.iterator().next());
									getItems().remove(node);
									return true;
								} catch (final Throwable t) {
									log.error(String.format("Unable to remove %1$s", 
											rn.getAddress()), t);
									controlBar.setHelpText(RS.rbLabel(
											KEYS.WIRELESS_NODE_REMOVE_FAILED, 
											rns.getAddress()));
									return false;
								}
							}
						}
						return false;
					}
				}
			}
			if (add && rns == null) {
				rns = RemoteNodeType.newDefaultRemoteNode(controlBar.getActor().getHost());
				rns.setAddress(nodeAddress);
				controlBar.getActor().getHost().getRemoteNodes().add(rns);
				try {
					controlBar.getRemoteNodePA().setBean(rns);
					addNodeItem(rns);
					return true;
				} catch (final Throwable t) {
					log.error(String.format("Unable to add %1$s", 
							rns.getAddress()), t);
					controlBar.setHelpText(RS.rbLabel(
							KEYS.WIRELESS_NODE_ADD_FAILED, rns.getAddress()));
					return false;
				}
			}
		}
		return false;
	}

	/**
	 * Gets a {@linkplain NodeStatusView} for a specified
	 * {@linkplain RemoteNode}
	 * 
	 * @param remoteNode
	 *            the {@linkplain RemoteNode}
	 * @return the {@linkplain NodeStatusView}
	 */
	public NodeStatusView getNodeStatusView(final RemoteNode remoteNode) {
		for (final Node node : getItems()) {
			if (node instanceof NodeStatusView
					&& ((NodeStatusView) node).getRemoteNode().getAddress()
							.equalsIgnoreCase(remoteNode.getAddress())) {
				return (NodeStatusView) node;
			}
		}
		return null;
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
						selectNotify();
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
		 * Send notification of selection
		 */
		public void selectNotify() {
			UGateKeeper.DEFAULT.notifyListeners(
					new UGateEvent<RemoteNode, Void>(
							getRemoteNode(),
							UGateEvent.Type.WIRELESS_REMOTE_NODE_CHANGING_FROM_SELECT,
							false));
			blinkStop(true);
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
		 * Updates the help text of the node status using a reference to the
		 * supplied {@linkplain Command}. Also blinks the
		 * {@linkplain NodeStatusView} for about 10 seconds when the
		 * {@linkplain ControlBar#getRemoteNode()} has the same
		 * {@linkplain RemoteNode#getAddress()} as the the
		 * {@linkplain #getRemoteNode()} or indefinitely when it is different.
		 * 
		 * @param command
		 *            the {@linkplain Command} to use in the help text
		 */
		public void updateLastCommand(final Command command) {
			setHelpText(command.toString());
			blinkStart(remoteNode.getAddress().equalsIgnoreCase(
					cb.getRemoteNode().getAddress()) ? 0 : 0);
		}
		
		/**
		 * @return the {@linkplain RemoteNode}
		 */
		public RemoteNode getRemoteNode() {
			return remoteNode;
		}
	}
}
