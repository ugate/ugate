package org.ugate.gui.view;

import java.util.HashMap;
import java.util.Map;

import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.util.Callback;
import javafx.util.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ugate.Command;
import org.ugate.UGateEvent;
import org.ugate.UGateKeeper;
import org.ugate.UGateListener;
import org.ugate.gui.ControlBar;
import org.ugate.gui.GuiUtil;
import org.ugate.gui.components.FunctionButton;
import org.ugate.gui.components.StatusIcon;
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
public class RemoteNodes extends ToolBar {

	private static final Logger log = LoggerFactory.getLogger(RemoteNodes.class);
	protected final ControlBar controlBar;
	private final TextField textField = new TextField();
	private final ListView<String> rnListView = new ListView<>();
	
	/**
	 * Constructor
	 * 
	 * @param controlBar the control bar
	 * @param orientation the {@linkplain Orientation}
	 */
	public RemoteNodes(final ControlBar controlBar, final Orientation orientation) {
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
						nsv.updateLastCommand(event.getCommand(), false);
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
							if (nsv != null) {
								// blink status to indicate the a remote device is out of sync
								nsv.updateLastCommand(event.getCommand(), true);
								controlBar.setHelpText(RS.rbLabel(
										KEYS.WIRELESS_NODE_REMOTE_SAVED_LOCAL,
										((RemoteNode) event.getSource()).getAddress()));
							}
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
						add(textField.getText());
					}
				});
		controlBar.addHelpTextTrigger(addNodeButton,
				RS.rbLabel(KEYS.WIRELESS_NODE_REMOTE_ADD_DESC));
		final Button removeNodeButton = new FunctionButton(
				FunctionButton.Function.REMOVE, new Runnable() {
					@Override
					public void run() {
						removeSelected();
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
		//VBox.setVgrow(rnListView, Priority.ALWAYS);
		rnListView.getStyleClass().add("remote-node-listview");
		rnListView.setPrefWidth(USE_PREF_SIZE);
		rnListView.setPrefHeight(30d);
		rnListView.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<String>() {
					@Override
					public void changed(
							final ObservableValue<? extends String> ov,
							final String oldAddress, final String newAddress) {
						selectFromChange(newAddress);
					}
				});
		controlBar.getActorPA().bindContentBidirectional(
				ActorType.REMOTE_NODES.getKey(),
				RemoteNodeType.WIRELESS_ADDRESS.getKey(), RemoteNode.class,
				rnListView.getItems(), String.class, null, null);
		getItems().addAll(textField, addNodeButton, removeNodeButton,
				new Separator(Orientation.VERTICAL), rnListView);
		selectFromChange(null);
	}

	/**
	 * Selects a {@linkplain RemoteNode#getAddress()}
	 * 
	 * @param address
	 *            the {@linkplain RemoteNode#getAddress()} to select
	 * @return true if selection is successful
	 */
	private boolean selectFromChange(final String address) {
		if (address == null || address.isEmpty()) {
			rnListView.getSelectionModel().select(
					controlBar.getRemoteNode().getAddress());
			return false;
		} else if (controlBar.getRemoteNode().getAddress()
				.equalsIgnoreCase(address)) {
			return true;
		}
		for (final RemoteNode rn : controlBar.getActor().getHost()
				.getRemoteNodes()) {
			if (rn.getAddress().equalsIgnoreCase(address)) {
				controlBar.getRemoteNodePA().setBean(rn);
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						UGateKeeper.DEFAULT
								.notifyListeners(new UGateEvent<RemoteNode, RemoteNode>(
										rn,
										UGateEvent.Type.WIRELESS_REMOTE_NODE_CHANGED,
										false, null, null, rn, controlBar
												.getRemoteNode()));
					}
				});
				return true;
			}
		}
		return false;
	}

	/**
	 * Adds a {@linkplain RemoteNode} based upon the given
	 * {@linkplain RemoteNode#getAddress()}. The currently selected
	 * {@linkplain RemoteNode} values will be used to initialize the new
	 * {@linkplain RemoteNode}
	 * 
	 * @param address
	 *            the {@linkplain RemoteNode#getAddress()} to add
	 */
	public void add(final String address) {
		if (address == null || address.isEmpty()) {
			return;
		}
		log.info("Attempting to add remote node at address: " + address);
		// node may have been added, but the save failed
		RemoteNode rnm = null;
		for (final RemoteNode rn : controlBar.getActor().getHost().getRemoteNodes()) {
			if (rn.getAddress().equalsIgnoreCase(address)) {
				rnm = rn;
				break;
			}
		}
		if (rnm == null) {
			final RemoteNode rn = RemoteNodeType.newDefaultRemoteNode(controlBar
					.getActor().getHost(), controlBar.getRemoteNode());
			rn.setAddress(address);
			controlBar.getActor().getHost().getRemoteNodes().add(rn);
		}
		ServiceProvider.IMPL.getCredentialService().mergeHost(
				controlBar.getActor().getHost());
	}

	/**
	 * Removes the selected {@linkplain RemoteNode}
	 */
	public void removeSelected() {
		// TODO : there may be an issue with removal when persistence fails the item will already be removed
		final String address = rnListView.getSelectionModel().getSelectedItem();
		if (address == null || address.isEmpty()) {
			return;
		}
		final Button closeBtn = ButtonBuilder.create().text(RS.rbLabel(KEYS.CLOSE)).build();
		final GuiUtil.DialogService dialogService = GuiUtil.dialogService(null, KEYS.APP_TITLE, 
				RS.rbLabel(KEYS.WIRELESS_NODE_REMOTE_REMOVE), 
				KEYS.SUBMIT, 550d, 300d, new Service<Void>() {
			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						// if the dialog shouldn't be closed call super.cancel()
						try {
							log.info("Attempting to remove remote node at address: "
									+ address);
							if (controlBar.getActor().getHost().getRemoteNodes().size() > 1) {
								RemoteNode rnr = null;
								for (final RemoteNode rn : controlBar.getActor().getHost().getRemoteNodes()) {
									if (address.equalsIgnoreCase(rn.getAddress())) {
										rnr = rn;
										break;
									}
								}
								if (rnr != null) {
									controlBar.getActor().getHost().getRemoteNodes().remove(rnr);
									ServiceProvider.IMPL.getCredentialService().mergeHost(
											controlBar.getActor().getHost(), rnr);
									controlBar.getRemoteNodePA().setBean(
											controlBar.getActor().getHost().getRemoteNodes().iterator().next());
									NodeStatusView.remove(address);
									selectFromChange(null);
									log.info("Removed remote node at address: "
											+ address);
								}
							}
						} catch (final Throwable t) {
							throw new RuntimeException();
						}
						return null;
					}
				};
			}
		}, Modality.APPLICATION_MODAL, closeBtn);
		if (closeBtn != null) {
			closeBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(final MouseEvent event) {
					dialogService.hide();
				}
			});	
		}
		dialogService.start();
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
		if (address == null || address.isEmpty()) {
			return null;
		}
		return NodeStatusView.get(address);
	}

	/**
	 * Node status view 
	 */
	protected static final class NodeStatusView extends ListCell<String> {

		// TODO : there is no public API to access the ListCell from a ListView
		// so ALL of them are maintained globally
		private static final Map<String, NodeStatusView> ALL = new HashMap<>();
		private final ControlBar controlBar;
		private String address;
		private final StatusIcon statusIcon;
		private final StringProperty helpTextStringProperty;
		private boolean isSet;
		
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
			getStyleClass().add("remote-node-listcell");
			this.controlBar = controlBar;
			this.statusIcon = new StatusIcon(16d, 16d, GuiUtil.COLOR_CLOSED);
			this.helpTextStringProperty = new SimpleStringProperty();
			setHelpText("LOADING...");
			setCursor(Cursor.HAND);
			controlBar.addHelpTextTrigger(this, this.helpTextStringProperty);
		}

		/**
		 * {@inheritDoc}
		 */
        @Override
        public void updateItem(final String item, final boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
				if (!isSelected()
						&& item.equalsIgnoreCase(getListView()
								.getSelectionModel().getSelectedItem())) {
					// bug where selection is not made on the cell, but exists
					// on the list (isSelected() will still be false immediately
					// following the selection on the list
					getListView().getSelectionModel().select(item);
				}
            	this.address = item;
    			setText(this.address);
                setGraphic(this.statusIcon);
                ALL.put(this.address, this);
                isSet = true;
            } else if (isSet) {
            	ALL.remove(this.address);
            }
        }

		/**
		 * Removes an {@linkplain RemoteNode#getAddress()} from the global cache
		 * 
		 * @param address
		 *            the {@linkplain RemoteNode#getAddress()} key
		 */
        public static void remove(final String address) {
        	ALL.remove(address);
        }

		/**
		 * Gets a {@linkplain NodeStatusView} from the global cache using it's
		 * {@linkplain RemoteNode#getAddress()}
		 * 
		 * @param address
		 *            the {@linkplain RemoteNode#getAddress()} key
		 * @return the {@linkplain NodeStatusView}
		 */
        public static NodeStatusView get(final String address) {
        	return ALL.get(address);
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
		 * @param hasIssue
		 *            true when there was an issue with the command or the
		 *            {@linkplain RemoteNode} is out-of-sync
		 */
		public void updateLastCommand(final Command command, final boolean hasIssue) {
			setHelpText(command.toString());
			final int cycleCount = getAddress().equalsIgnoreCase(
					controlBar.getRemoteNode().getAddress()) ? 40
					: Timeline.INDEFINITE;
			this.statusIcon.setStatusFill(Duration.seconds(1),
					hasIssue ? GuiUtil.COLOR_OPEN : GuiUtil.COLOR_ON,
					GuiUtil.COLOR_CLOSED, cycleCount);
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
		public StatusIcon getStatusIcon() {
			return statusIcon;
		}
	}
}
