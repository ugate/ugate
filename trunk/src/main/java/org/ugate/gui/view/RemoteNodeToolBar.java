package org.ugate.gui.view;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ugate.Command;
import org.ugate.UGateEvent;
import org.ugate.UGateKeeper;
import org.ugate.UGateListener;
import org.ugate.gui.ControlBar;
import org.ugate.gui.GuiUtil;
import org.ugate.gui.components.FunctionButton;
import org.ugate.resources.RS;
import org.ugate.resources.RS.KEYS;
import org.ugate.service.ServiceProvider;
import org.ugate.service.entity.ActorType;
import org.ugate.service.entity.RemoteNodeType;
import org.ugate.service.entity.jpa.RemoteNode;
import org.ugate.wireless.data.RxTxRemoteNodeDTO;

/**
 * {@linkplain ToolBar} for displaying the status of each of the {@linkplain RemoteNode}s
 */
public class RemoteNodeToolBar extends ToolBar {

	private static final Logger log = LoggerFactory.getLogger(RemoteNodeToolBar.class);
	protected final ControlBar controlBar;
	private final TextField textField = new TextField();
	private final ListView<String> rnListView = new ListView<>();
	
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
		this.rnListView.setOrientation(orientation);
		addGlobalNodeControls();
		registerListeners();
	}

	/**
	 * Registers any {@linkplain UGateListener}s
	 */
	protected final void registerListeners() {
		UGateKeeper.DEFAULT.addListener(new UGateListener() {
			@Override
			public void handle(final UGateEvent<?, ?> event) {
				final boolean isRemoteCommand = event.isFromRemote() && event.getCommand() != null;
				if (isRemoteCommand && event.getType() == UGateEvent.Type.WIRELESS_REMOTE_NODE_COMMITTED) {
					// notify the user that the remote node has successfully been committed locally
					final RemoteNode rn = (RemoteNode) event.getSource();
					final NodeStatusView nsv = getNodeStatusView(rn.getAddress());
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
							final NodeStatusView nsv = getNodeStatusView(rn.getAddress());
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
						log.info("Attempting to add remote node at address: "
								+ textField.getText());
						final NodeStatusView nsv = getNodeStatusView(textField.getText());
						if (nsv == null) {
							final RemoteNode rn = RemoteNodeType.newDefaultRemoteNode(
									controlBar.getActor().getHost());
							rn.setAddress(textField.getText());
							controlBar.getActor().getHost().getRemoteNodes().add(rn);
							ServiceProvider.IMPL.getCredentialService().mergeHost(
									controlBar.getActor().getHost());
							log.info("Added remote node at address: "
									+ textField.getText());
						}
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
						log.info("Attempting to remove remote node at address: "
								+ textField.getText());
						if (controlBar.getActor().getHost().getRemoteNodes().size() > 1) {
							RemoteNode rnr = null;
							for (final RemoteNode rn : controlBar.getActor().getHost().getRemoteNodes()) {
								if (textField.getText().equalsIgnoreCase(rn.getAddress())) {
									rnr = rn;
									break;
								}
							}
							if (rnr != null) {
								controlBar.getActor().getHost().getRemoteNodes().remove(rnr);
								ServiceProvider.IMPL.getCredentialService().mergeHost(
										controlBar.getActor().getHost());
								log.info("Removed remote node at address: "
										+ textField.getText());
							}
						}
					}
				});
		controlBar.addHelpTextTrigger(removeNodeButton,
				RS.rbLabel(KEYS.WIRELESS_NODE_REMOTE_REMOVE_DESC));

		rnListView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override 
            public ListCell<String> call(final ListView<String> list) {
                return new NodeStatusView(controlBar);
            }
        });
		HBox.setHgrow(rnListView, Priority.ALWAYS);
		VBox.setVgrow(rnListView, Priority.ALWAYS);
		//rnListView.getStyleClass().add("remote-node-listview");
		rnListView.setPrefSize(USE_PREF_SIZE, USE_PREF_SIZE);
		rnListView.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<String>() {
					@Override
					public void changed(
							final ObservableValue<? extends String> ov,
							final String oldAddress, final String newAddress) {
						for (final RemoteNode rn : controlBar.getActor()
								.getHost().getRemoteNodes()) {
							if (rn.getAddress().equalsIgnoreCase(newAddress)) {
								controlBar.getRemoteNodePA().setBean(rn);
								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										UGateKeeper.DEFAULT
												.notifyListeners(new UGateEvent<RemoteNode, RemoteNode>(
														rn,
														UGateEvent.Type.WIRELESS_REMOTE_NODE_CHANGED,
														false,
														null,
														null,
														rn,
														controlBar
																.getRemoteNode()));
									}
								});
								break;
							}
						}
					}
				});
		controlBar.getActorPA().bindContentBidirectional(
				ActorType.REMOTE_NODES.getKey(),
				RemoteNodeType.WIRELESS_ADDRESS.getKey(), RemoteNode.class,
				rnListView.getItems(), String.class, null, null);
		getItems().addAll(textField, addNodeButton, removeNodeButton,
				new Separator(Orientation.VERTICAL), rnListView);
	}

	/**
	 * Gets a {@linkplain NodeStatusView} for a specified
	 * {@linkplain RemoteNode#getAddress()}
	 * 
	 * @param address
	 *            the {@linkplain RemoteNode#getAddress()}
	 * @return the {@linkplain NodeStatusView}
	 */
	public NodeStatusView getNodeStatusView(final String address) {
		for (final Node node : getItems()) {
			if (node instanceof NodeStatusView
					&& ((NodeStatusView) node).getAddress()
							.equalsIgnoreCase(address)) {
				return (NodeStatusView) node;
			}
		}
		return null;
	}

	/**
	 * Node status view 
	 */
	protected static final class NodeStatusView extends ListCell<String> {

		private final ControlBar controlBar;
		private String address;
		private final StatusView statusView;
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
		public NodeStatusView(final ControlBar controlBar) {
			super();
			this.controlBar = controlBar;
			this.statusView = new StatusView(controlBar, true, GuiUtil.COLOR_UNSELECTED, 
            		null, GuiUtil.COLOR_SELECTING);
			this.helpTextStringProperty = new SimpleStringProperty();
			setHelpText("");
			setCursor(Cursor.HAND);
			controlBar.addHelpTextTrigger(this, this.helpTextStringProperty);
		}

        @Override
        public void updateItem(final String item, final boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
            	this.address = item;
    			setText(this.address);
                setGraphic(this.statusView);
            }
        }

		/**
		 * Sets the help text for the status view
		 * 
		 * @param commandDesc
		 *            the node command description
		 */
		protected void setHelpText(final String commandDesc) {
			this.helpTextStringProperty.set(RS.rbLabel(
					KEYS.WIRELESS_NODE_REMOTE_STATUS, 
					getAddress(), commandDesc));
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
			this.statusView.blinkStart(getAddress().equalsIgnoreCase(
					controlBar.getRemoteNode().getAddress()) ? 0 : 0);
		}
		
		/**
		 * @return the {@linkplain RemoteNode#getAddress()}
		 */
		public String getAddress() {
			return address;
		}

		/**
		 * @return the {@linkplain StatusView}
		 */
		public StatusView getStatusView() {
			return statusView;
		}
	}
}
