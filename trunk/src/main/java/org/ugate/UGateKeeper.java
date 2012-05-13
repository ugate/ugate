package org.ugate;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Platform;

import org.slf4j.Logger;
import org.ugate.mail.EmailAgent;
import org.ugate.mail.EmailEvent;
import org.ugate.mail.IEmailListener;
import org.ugate.resources.RS;
import org.ugate.wireless.data.RxData;
import org.ugate.wireless.data.RxTxRemoteSettingsData;

import com.rapplogic.xbee.api.AtCommand;
import com.rapplogic.xbee.api.AtCommandResponse;
import com.rapplogic.xbee.api.RemoteAtRequest;
import com.rapplogic.xbee.api.RemoteAtResponse;
import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeAddress16;
import com.rapplogic.xbee.api.XBeeException;
import com.rapplogic.xbee.api.XBeeTimeoutException;
import com.rapplogic.xbee.api.wpan.TxRequest16;
import com.rapplogic.xbee.api.wpan.TxStatusResponse;

/**
 * Central gate keeper hub for wireless, mail, and other provider implementations
 */
public enum UGateKeeper {
	
	DEFAULT;
	
	private final Logger log = UGateUtil.getLogger(UGateKeeper.class);
	private final List<IGateKeeperListener> listeners = new ArrayList<IGateKeeperListener>();
	private XBee xbee;
	private EmailAgent emailAgent;
	private boolean isEmailConnected;
	private StorageFile hostSettings;
	private Map<Integer, RemoteNode> remoteNodes = new HashMap<Integer, RemoteNode>(1);
	private int wirelessCurrentRemoteNodeIndex = RemoteSettings.WIRELESS_ADDRESS_START_INDEX;
	private int wirelessNextRemoteNodeIndex = RemoteSettings.WIRELESS_ADDRESS_START_INDEX;
	
	/**
	 * Constructor
	 */
	private UGateKeeper() {
	}
	
	/**
	 * Initializes the {@linkplain UGateKeeper}
	 */
	public void init() {
		log.info("Iniitializing the gate keeper...");
		final Path hostPropPath = RS.hostPropertiesFilePath();
		final Path hostCpyFrmPropPath = RS.hostDefaultPropertiesPath();
		log.info(String.format("Loading %1$s (copies from %2$s when not present)", hostPropPath, hostCpyFrmPropPath));
		hostSettings = new StorageFile(hostPropPath, hostCpyFrmPropPath);
		wirelessInit();
		xbee = new XBee();
		// test the serial ports
		wirelessSerialPorts();
		log.info("...iniitialized the gate keeper");
	}
	
	/**
	 * Closes the gate keeper services
	 */
	public void close() {
		emailDisconnect();
		wirelessDisconnect();
	}
	
	/* ======= Preferences ======= */
	
	/**
	 * Determines if the key exists within the saved settings
	 * 
	 * @param key the key to get the value for
	 * @param index the index of the settings (if applicable)
	 * @return true when the key exists
	 */
	public boolean settingsHasKey(final ISettings key, final Integer index) {
		if (key instanceof RemoteSettings) {
			if (!remoteNodes.containsKey(index)) {
				return false;
			}
			return remoteNodes.get(index).settings.hasKey(key.getKey());
		} else if (key instanceof HostSettings) {
			return hostSettings.hasKey(key.getKey());
		}
		return false;
	}
	
	/**
	 * Sets a key/value settings
	 * 
	 * @param key the {@linkplain ISettings}
	 * @param index the index of the {@linkplain ISettings} (if applicable)
	 * @param value the value
	 */
	public void settingsSet(final ISettings key, final Integer index, final String value) {
		final String oldValue = settingsGet(key, index);
		if (!value.equals(oldValue)) {
			if (key instanceof RemoteSettings) {
				remoteNodes.get(index).settings.set(key.getKey(), value);
			} else if (key instanceof HostSettings) {
				hostSettings.set(key.getKey(), value);
			} else {
				throw new UnsupportedOperationException(String.format("Unhandled %1$s implementation for %2$s", 
						ISettings.class.getSimpleName(), key));
			}
			notifyListeners(new UGateKeeperEvent<String>(this, UGateKeeperEvent.Type.SETTINGS_SAVE_LOCAL, false, null,
					key, null, oldValue, value));
		}
	}
	
	/**
	 * Gets a settings value
	 * 
	 * @param key the key to get the value for
	 * @param index the index of the settings (if applicable)
	 * @return the settings value
	 */
	public String settingsGet(final ISettings key, final Integer index) {
		if (!settingsHasKey(key, index)) {
			return null;
		}
		return key instanceof RemoteSettings ? remoteNodes.get(index).settings.get(key.getKey()) : hostSettings.get(key.getKey());
	}
	
	/**
	 * Gets a settings value
	 * 
	 * @param key the key to get the value for
	 * @param index the index of the settings (if applicable)
	 * @param delimiter the delimiter used to split the value
	 * @return the settings values
	 */
	public List<String> settingsGet(final ISettings key, final Integer index, final String delimiter) {
		if (!settingsHasKey(key, index)) {
			return null;
		}
		return key instanceof RemoteSettings ? remoteNodes.get(index).settings.get(key.getKey(), delimiter) : 
			hostSettings.get(key.getKey(), delimiter);
	}
	
	/* ======= Listeners ======= */
	
	/**
	 * Removes a {@linkplain IGateKeeperListener}
	 * 
	 * @param listener the listener to remove
	 */
	public void removeListener(final IGateKeeperListener listener) {
		if (listener != null && listeners.contains(listener)) {
			listeners.remove(listener);
		}
	}
	
	/**
	 * Adds a {@linkplain IGateKeeperListener} that will be notified of preference/settings 
	 * and connection interactions.
	 * 
	 * @param listener the listener to add
	 */
	public void addListener(final IGateKeeperListener listener) {
		if (listener != null && !listeners.contains(listener)) {
			listeners.add(listener);
		}
	}
	
	/**
	 * Notifies the listeners of preference/settings and connection interactions.
	 * 
	 * @param <V> the type of event value
	 * @param events the event(s)
	 */
	private <V> void notifyListeners(final UGateKeeperEvent<V> event) {
		for (final IGateKeeperListener pl : listeners) {
			// TODO : remove reference to GUI implementation
			if (Platform.isFxApplicationThread()) {
				pl.handle(event);
			} else {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						try {
							pl.handle(event);
						} catch (final Throwable t) {
							log.warn("Unable to notify listener: " + pl, t);
						}
					}
				});
			}
		}
	}
	
	/* ======= Email Communications ======= */
	
	/**
	 * Connects to email
	 */
	public void emailConnect() {
		// test email connection
//		isEmailConnected = true;
//		if (true) {
//			return;
//		}
		// connect to email
		//emailDisconnect();
		if (isEmailConnected) {
			return;
		}
		String msg;
		UGateKeeperEvent<Void> event;
		final String smtpHost = settingsGet(HostSettings.MAIL_SMTP_HOST, null);
		final String smtpPort = settingsGet(HostSettings.MAIL_SMTP_PORT, null);
		final String imapHost = settingsGet(HostSettings.MAIL_IMAP_HOST, null);
		final String imapPort = settingsGet(HostSettings.MAIL_IMAP_PORT, null);
		final String username = settingsGet(HostSettings.MAIL_USERNAME, null);
		final String password = settingsGet(HostSettings.MAIL_PASSWORD, null);
		final String mainFolderName = settingsGet(HostSettings.MAIL_INBOX_NAME, null);
		try {
			msg = RS.rbLabel("mail.connecting");
			log.info(msg);
			event = new UGateKeeperEvent<Void>(this, UGateKeeperEvent.Type.EMAIL_CONNECTING, false);
			event.addMessage(msg);
			notifyListeners(event);
			final List<IEmailListener> listeners = new ArrayList<IEmailListener>();
			listeners.add(new IEmailListener() {
				@Override
				public void handle(final EmailEvent event) {
					String msg;
					if (event.type == EmailEvent.Type.EXECUTE_COMMAND) {
						if (wirelessIsConnected()) {
							int nodeIndex;
							final Map<Integer, String> addys = new HashMap<Integer, String>();
							final List<String> commandMsgs = new ArrayList<String>();
							for (final Command command : event.commands) {
								// send command to all the nodes defined in the email
								for (final String toAddress : event.toAddresses) {
									try {
										nodeIndex = wirelessGetAddressIndex(toAddress);
										addys.put(nodeIndex, toAddress);
										wirelessSendData(nodeIndex, command);
										msg = RS.rbLabel("service.email.commandexec", command, event.from, toAddress);
										log.info(msg);
										commandMsgs.add(msg);
									} catch (final Throwable t) {
										msg = RS.rbLabel("service.email.commandexec.failed", command, event.from, toAddress);
										log.error(msg, t);
										commandMsgs.add(msg);
									}
								}
							}
							if (!addys.isEmpty()) {
								notifyListeners(new UGateKeeperEvent<List<Command>>(this, UGateKeeperEvent.Type.EMAIL_EXECUTED_COMMANDS, 
										false, addys, RemoteSettings.WIRELESS_ADDRESS_NODE, null, null, event.commands, 
										commandMsgs.toArray(new String[]{})));
							} else {
								notifyListeners(new UGateKeeperEvent<List<Command>>(this, UGateKeeperEvent.Type.EMAIL_EXECUTE_COMMANDS_FAILED, 
										false, addys, RemoteSettings.WIRELESS_ADDRESS_NODE, null, null, event.commands, 
										commandMsgs.toArray(new String[]{})));
							}
						} else {
							msg = RS.rbLabel("service.email.commandexec.failed", UGateUtil.toString(event.commands), UGateUtil.toString(event.from), 
									UGateUtil.toString(event.toAddresses), RS.rbLabel("service.wireless.connection.required"));
							log.warn(msg);
							notifyListeners(new UGateKeeperEvent<List<Command>>(this, UGateKeeperEvent.Type.EMAIL_EXECUTE_COMMANDS_FAILED, false, null,
									RemoteSettings.WIRELESS_ADDRESS_NODE, null, null, event.commands, msg));
						}
					} else if (event.type == EmailEvent.Type.CONNECT) {
						isEmailConnected = true;
						msg = RS.rbLabel("mail.connected");
						log.info(msg);
						notifyListeners(new UGateKeeperEvent<List<Command>>(this, UGateKeeperEvent.Type.EMAIL_CONNECTED, false, null,
								null, null, null, event.commands, msg));
					} else if (event.type == EmailEvent.Type.DISCONNECT) {
						isEmailConnected = false;
						msg = RS.rbLabel("mail.disconnected");
						log.info(msg);
						notifyListeners(new UGateKeeperEvent<List<Command>>(this, UGateKeeperEvent.Type.EMAIL_DISCONNECTED, false, null,
								null, null, null, event.commands, msg));
					} else if (event.type == EmailEvent.Type.CLOSED) {
						isEmailConnected = false;
						msg = RS.rbLabel("mail.closed");
						log.info(msg);
						notifyListeners(new UGateKeeperEvent<List<Command>>(this, UGateKeeperEvent.Type.EMAIL_CLOSED, false, null, 
								null, null, null, event.commands, msg));
					}
				}
			});
			this.emailAgent = EmailAgent.start(smtpHost, smtpPort, imapHost, imapPort, 
					username, password, mainFolderName, listeners.toArray(new IEmailListener[0]));
		} catch (final Throwable t) {
			msg = RS.rbLabel("mail.connect.failed", 
					smtpHost, smtpPort, imapHost, imapPort, username, mainFolderName);
			log.error(msg, t);
			event = new UGateKeeperEvent<Void>(this, UGateKeeperEvent.Type.EMAIL_CONNECT_FAILED, false);
			event.addMessage(msg);
			event.addMessage(t.getMessage());
			notifyListeners(event);
		}
	}
	
	/**
	 * Disconnects from email
	 */
	public void emailDisconnect() {
		if (emailAgent != null) {
			final String msg = RS.rbLabel("mail.disconnecting");
			log.info(msg);
			notifyListeners(new UGateKeeperEvent<Void>(this, UGateKeeperEvent.Type.EMAIL_DISCONNECTING, false, msg));
			emailAgent.disconnect();
			emailAgent = null;
		}
	}
	
	/**
	 * Sends an email
	 * 
	 * @param subject the subject of the email
	 * @param message the email message
	 * @param from who the email is from
	 * @param to the recipients of the email
	 * @param fileNames file name paths to any attachments (optional)
	 */
	public void emailSend(String subject, String message, String from, String[] to, String... fileNames) {
		if (emailAgent != null) {
			emailAgent.send(subject, message, from, to, fileNames);
		} else {
			log.warn("Unable to send email... no connection established");
		}
	}
	
	/**
	 * @return true if the email agent is connected
	 */
	public boolean emailIsConnected() {
		return isEmailConnected;
	}
	
	/* ======= Wireless Communications ======= */
	
	/**
	 * Connects to the wireless network
	 * 
	 * @param comPort the COM port to connect to {@link #wirelessSerialPorts()}
	 * @param baudRate the baud rate to connect at (if applicable)
	 * @return true when on successful connection
	 */
	public boolean wirelessConnect(final String comPort, final int baudRate) {
		wirelessDisconnect();
		log.info("Connecting to local XBee");
		notifyListeners(new UGateKeeperEvent<Void>(this, UGateKeeperEvent.Type.WIRELESS_HOST_CONNECTING, false));
		try {
			xbee.open(comPort, baudRate);
			xbee.addPacketListener(new UGateXBeePacketListener(){
				@Override
				protected <V extends RxData> void handleEvent(final UGateKeeperEvent<V> event) {
					// TODO : update the remote nodes history for incoming data
//					if (event.getType() == UGateKeeperEvent.Type.WIRELESS_DATA_RX_SUCCESS || 
//							event.getType() == UGateKeeperEvent.Type.WIRELESS_DATA_TX_STATUS_RESPONSE_SUCCESS) {
//						for (final Map.Entry<Integer, String> ea : event.getNodeAddresses().entrySet()) {
//							
//						}
//					}
					notifyListeners(event);
				}
			});
			log.info(String.format("Connected to local XBee using port %1$s and baud rate %2$s", 
					comPort, baudRate));
			// XBee connection is blocking so notification can be sent here
			notifyListeners(new UGateKeeperEvent<Void>(this, UGateKeeperEvent.Type.WIRELESS_HOST_CONNECTED, false));
			return true;
		} catch (final Throwable t) {
			final String errorMsg = String.format("Unable to establish a connection to the local XBee using port %1$s and baud rate %2$s", 
					comPort, baudRate);
			log.error(errorMsg, t);
			notifyListeners(new UGateKeeperEvent<Void>(this, UGateKeeperEvent.Type.WIRELESS_HOST_CONNECT_FAILED, false, errorMsg, t.getMessage()));
			if (t instanceof XBeeException) {
				// bug in XBee connection that will show xbee.isConnected() as true after an XBeeException unless we close it here
				try {
					log.debug(String.format("Closing connection due to %1$s", XBeeException.class.getName()));
					wirelessDisconnectInternal(false);
				} catch (final Throwable t2) {
					log.warn(String.format("Unable to close %1$s connection (diconnecting because connection threw error", 
							XBee.class.getName()), t2);
				}
			}
			return false;
		}
	}
	
	/**
	 * Disconnects from the wireless network
	 */
	public void wirelessDisconnect() {
		wirelessDisconnectInternal(true);
	}
	
	/**
	 * Disconnects from the wireless network
	 * 
	 * @param notify true to notify listeners
	 */
	private void wirelessDisconnectInternal(final boolean notify) {
		if (wirelessIsConnected()) {
			String msg = "Disconnecting from XBee";
			log.info(msg);
			if (notify) {
				notifyListeners(new UGateKeeperEvent<Void>(this, UGateKeeperEvent.Type.WIRELESS_HOST_DISCONNECTING, false, msg));	
			}
			try {
				xbee.close();
				msg = "Disconnected from XBee";
				log.info(msg);
				if (notify) {
					// XBee close is blocking so notification can be sent here
					notifyListeners(new UGateKeeperEvent<Void>(this, UGateKeeperEvent.Type.WIRELESS_HOST_DISCONNECTED, false, msg));
				}
			} catch (final Throwable t) {
				msg = "Unable to close wireless connection";
				log.error(msg, t);
				if (notify) {
					notifyListeners(new UGateKeeperEvent<Void>(this, UGateKeeperEvent.Type.WIRELESS_HOST_DISCONNECT_FAILED, false, 
							msg, t.getMessage()));
				}
			}
			log.info("Disconnected from XBee");
		}
	}
	
	/**
	 * @return true when connected to the wireless network
	 */
	public boolean wirelessIsConnected() {
		return xbee != null && xbee.isConnected();
	}
	
	/**
	 * Gets the wireless working directory for a specified node index. 
	 * All required directories will be created and accounted for.
	 * 
	 * @param nodeIndex the node index
	 * @return the path
	 */
	public Path wirelessWorkingDirectory(final int nodeIndex) {
		if (!remoteNodes.containsKey(nodeIndex)) {
			return null;
		}
		return wirelessWorkingDirectory(remoteNodes.get(nodeIndex).settings, nodeIndex);
	}
	
	/**
	 * Gets the wireless working directory for a specified node index. 
	 * All required directories will be created and accounted for.
	 * 
	 * @param storageFile the storage file to get the working directory for
	 * @param nodeIndex the node index
	 * @return the path
	 */
	private Path wirelessWorkingDirectory(final StorageFile storageFile, final int nodeIndex) {
		String workingDir = storageFile.get(RemoteSettings.WIRELESS_WORKING_DIR_PATH.getKey());
		if (workingDir == null || workingDir.isEmpty()) {
			throw new NullPointerException(String.format("%1$s is null for remote node %2$s", 
					RemoteSettings.WIRELESS_WORKING_DIR_PATH, nodeIndex));
		}
		return RS.workingDirectoryPath(Paths.get(workingDir, String.valueOf(nodeIndex)), null);
	}

	/**
	 * Sends the data string to the remote address in ASCII format array
	 * 
	 * @param nodeIndex the index of the node to send the data to
	 * @param command the executing {@linkplain Command}
	 * @param data the data string to send
	 * @return true when successful
	 */
	public boolean wirelessSendData(final int nodeIndex, final Command command, final String data) {
		return wirelessSendData(nodeIndex, command, ByteUtils.stringToIntArray(data));
	}
	
	/**
	 * Sends the data string to the remote address in ASCII format array
	 * 
	 * @param nodeIndex the index of the node to send the data to
	 * @param command the executing {@linkplain Command}
	 * @param data the data string to send
	 * @return true when successful
	 */
	public boolean wirelessSendData(final int nodeIndex, final Command command, final List<Integer> data) {
		int[] dataInts = new int[data.size()];
		for(int i=0; i<data.size(); i++) {
			dataInts[i] = data.get(i);
		}
		return wirelessSendData(nodeIndex, command, dataInts);
	}
	
	/**
	 * Sends the data array to the remote address
	 * 
	 * @param nodeIndex the index of the node to send the data to (
	 * @param command the executing {@linkplain Command}
	 * @param data the data to send
	 * @return true when successful
	 */
	public boolean wirelessSendData(final int nodeIndex, final Command command, final int... data) {
		final Map<Integer, String> addys = wirelessGetRemoteAddressMap(nodeIndex);
		if (addys.isEmpty()) {
			throw new ArrayIndexOutOfBoundsException(nodeIndex);
		}
		return wirelessSendData(new UGateKeeperEvent<int[]>(this, UGateKeeperEvent.Type.INITIALIZE, false, 
				addys, null, command, null, data));
	}
	
	/**
	 * Sends the data array to the remote address
	 * 
	 * @param event the event that contains the <code>int</code> array of data to send 
	 * 	{@linkplain UGateKeeperEvent#newValue}, the {@linkplain UGateKeeperEvent#command}, 
	 * 	and {@linkplain UGateKeeperEvent#nodeAddresses} to send the data to
	 * @return true when successful
	 */
	private boolean wirelessSendData(final UGateKeeperEvent<int[]> event) {
		if (event.getTotalNodeAddresses() <= 0) {
			throw new NullPointerException("Wireless node addresses cannot be null");
		}
		int i = RemoteSettings.WIRELESS_ADDRESS_START_INDEX;
		int it = event.getTotalNodeAddresses();
		int successCount = 0;
		int failureCount = 0;
		notifyListeners(event.clone(UGateKeeperEvent.Type.WIRELESS_DATA_ALL_TX, i));
		String message;
		for (; i<=it; i++) {
			try {
				// bytes header command and status/failure code
				final int[] bytesHeader = new int[] { event.getCommand().id, RxData.Status.NORMAL.ordinal() };
				final int[] bytes = event.getNewValue() != null && event.getNewValue().length > 0 ? 
						UGateUtil.arrayConcatInt(bytesHeader, event.getNewValue()) : bytesHeader;
				final XBeeAddress16 xbeeAddress = wirelessGetXbeeAddress(i);
				// create a unicast packet to be delivered to the supplied address, with the pay load
				final TxRequest16 request = new TxRequest16(xbeeAddress, bytes);
				message = RS.rbLabel("service.wireless.sending", bytes, event.getNodeAddress(i));
				log.info(message);
				notifyListeners(event.clone(UGateKeeperEvent.Type.WIRELESS_DATA_TX, i, message));
				// send the packet and wait up to 10 seconds for the transmit status reply
				final TxStatusResponse response = (TxStatusResponse) xbee.sendSynchronous(request, 12000);
				if (response.isSuccess()) {
					// packet was delivered successfully
					successCount++;
					message = RS.rbLabel("service.wireless.ack.success", bytes, event.getNodeAddress(i), response.getStatus());
					log.info(message);
					notifyListeners(event.clone(UGateKeeperEvent.Type.WIRELESS_DATA_TX_ACK, i, message));
				} else {
					// packet was not delivered
					failureCount++;
					message = RS.rbLabel("service.wireless.ack.failed", bytes, event.getNodeAddress(i), response.getStatus());
					log.error(message);
					notifyListeners(event.clone(UGateKeeperEvent.Type.WIRELESS_DATA_TX_ACK_FAILED, i, message));
				}
			} catch (XBeeTimeoutException e) {
				failureCount++;
				message = RS.rbLabel("service.wireless.tx.timeout", event.getNodeAddress(i));
				log.error(message, e);
				notifyListeners(event.clone(UGateKeeperEvent.Type.WIRELESS_DATA_TX_FAILED, i, message));
			} catch (final Throwable t) {
				failureCount++;
				message = RS.rbLabel("service.wireless.tx.failed", event.getNodeAddress(i));
				log.error(message, t);
				notifyListeners(event.clone(UGateKeeperEvent.Type.WIRELESS_DATA_TX_FAILED, i, message));
			}
		}
		if (failureCount <= 0) {
			message = RS.rbLabel("service.wireless.success", successCount);
			log.info(message);
			notifyListeners(event.clone(UGateKeeperEvent.Type.WIRELESS_DATA_ALL_TX_SUCCESS, i, message));
		} else {
			message = RS.rbLabel("service.wireless.tx.batch.failed", failureCount);
			log.error(message);
			notifyListeners(event.clone(UGateKeeperEvent.Type.WIRELESS_DATA_ALL_TX_FAILED, i, message));
		}
		return failureCount == 0;
	}

	/**
	 * Tests the address of a remote device within the wireless network
	 * 
	 * @param nodeIndex the index of the remote node to test a connection for 
	 * 		({@linkplain RemoteSettings#WIRELESS_ADDRESS_START_INDEX} - 1 when testing the local address)
	 * @return the address of the device (if found and returns its address)
	 */
	public String wirelessTestRemoteConnection(final int nodeIndex) {
		String address = null;
		try {
			AtCommandResponse response;
			int[] responseValue;
			final XBeeAddress16 remoteAddress = wirelessGetXbeeAddress(nodeIndex);
			if (remoteAddress != null) {
				final RemoteAtRequest request = new RemoteAtRequest(remoteAddress, "MY");
				final RemoteAtResponse remoteResponse = (RemoteAtResponse) xbee.sendSynchronous(request, 10000);
				response = remoteResponse;
			} else {
				response = (AtCommandResponse) xbee.sendSynchronous(new AtCommand("MY"));
			}
			if (response.isOk()) {
				responseValue = response.getValue();
				address = ByteUtils.toBase16(responseValue);
				log.info(String.format("Successfully got %1$s address %2$s", (remoteAddress != null ? "remote" : "local"), address));
			} else {
				throw new XBeeException("Failed to get remote address response. Status is " + response.getStatus());
			}
		} catch (XBeeTimeoutException e) {
			log.warn("Timed out getting remote XBee address", e);
		} catch (XBeeException e) {
			log.warn("Error getting remote XBee address", e);
		}
		return address;
	}
	
	/**
	 * Gets a wireless XBee address for a wireless node index
	 * 
	 * @param nodeIndex the index of the wireless node
	 * @return the XBee address
	 */
	private XBeeAddress16 wirelessGetXbeeAddress(final int nodeIndex) {
		//final int xbeeRawAddress = Integer.parseInt(preferences.get(wirelessAddressHexKey), 16);
		final String rawAddress = wirelessGetAddress(nodeIndex);
		if (rawAddress.length() > RemoteSettings.WIRELESS_ADDRESS_MAX_DIGITS) {
			throw new IllegalArgumentException("Wireless address cannot be more than " + 
					RemoteSettings.WIRELESS_ADDRESS_MAX_DIGITS + " hex digits long");
		}
		final int msb = Integer.parseInt(rawAddress.substring(0, 2), 16);
		final int lsb = Integer.parseInt(rawAddress.substring(2, 4), 16);
		final XBeeAddress16 xbeeAddress = new XBeeAddress16(msb, lsb);
		return xbeeAddress;
	}
	
	/**
	 * Removes a wireless node by address and sets the new wireless node address/index with the next one in the list
	 * 
	 * @param nodeAddress the wireless node address to remove
	 * @return the new wireless node address (the same node address passed if it cannot be removed)
	 */
	public String wirelessRemoveNode(final String nodeAddress) {
		if (remoteNodes.entrySet().size() <= 1) {
			return nodeAddress;
		}
		Map.Entry<Integer, RemoteNode> wakRemove = null;
		Map.Entry<Integer, RemoteNode> wakSelect = null;
		for (final Map.Entry<Integer, RemoteNode> wak : remoteNodes.entrySet()) {
			if (wak.getValue().settings.hasKey(RemoteSettings.WIRELESS_ADDRESS_NODE.getKey())) {
				if (wak.getValue().settings.get(RemoteSettings.WIRELESS_ADDRESS_NODE.getKey()).equals(nodeAddress)) {
					wakRemove = wak;
					if (wakSelect != null) {
						break;
					}
				} else if (wakSelect == null) {
					wakSelect = wak;
					if (wakRemove != null) {
						break;
					}
				}
			}
		}
		if (wakRemove != null && wakSelect != null) {
			final String oldAddy = wirelessGetAddress(wakRemove.getKey());
			removeRemoteNode(wakRemove.getKey());
			wirelessSetCurrentRemoteNodeIndex(wakSelect.getKey(), null, false);
			
			final String msg = RS.rbLabel("wireless.node.remote.remove", 
					wakRemove.getValue().settings.get(RemoteSettings.WIRELESS_ADDRESS_NODE.getKey()), 
					wakSelect.getValue().settings.get(RemoteSettings.WIRELESS_ADDRESS_NODE.getKey()));
			log.info(msg);
			// send notification(s)
			final Map<Integer, String> removed = new HashMap<Integer, String>();
			removed.put(wakRemove.getKey(), oldAddy);
			notifyListeners(new UGateKeeperEvent<Integer>(this, UGateKeeperEvent.Type.SETTINGS_REMOTE_NODE_CHANGED_FROM_REMOVE, false,
					removed, null, null, wakRemove.getKey(), 
					this.wirelessCurrentRemoteNodeIndex, msg));
			return wakSelect.getValue().settings.get(RemoteSettings.WIRELESS_ADDRESS_NODE.getKey());
		}
		return nodeAddress;
	}
	
	/**
	 * 
	 * @param nodeAddress the existing or new node address
	 */
	public void wirelessSetRemoteNode(final String nodeAddress) {
		if (nodeAddress != null && !nodeAddress.isEmpty()) {
			wirelessSetCurrentRemoteNodeIndex(wirelessGetAddressIndex(nodeAddress), nodeAddress, true);
		}
	}
	
	/**
	 * Gets the index for a wireless node address
	 * 
	 * @param nodeAddress the wireless node address
	 * @return the index of the wireless node address (negative one when the address value is not found)
	 */
	public int wirelessGetAddressIndex(final String nodeAddress) {
		for (final Map.Entry<Integer, RemoteNode> wak : remoteNodes.entrySet()) {
			if (wak.getValue().settings.hasKey(RemoteSettings.WIRELESS_ADDRESS_NODE.getKey()) && 
					wak.getValue().settings.get(RemoteSettings.WIRELESS_ADDRESS_NODE.getKey()).equals(nodeAddress)) {
				return wak.getKey();
			}
		}
		return -1; 
	}
	
	/**
	 * Gets a wireless address for a specified node index
	 * 
	 * @param nodeIndex the index of the node to get the address for
	 * @return the wireless node address
	 */
	public String wirelessGetAddress(final int nodeIndex) {
		if (!remoteNodes.containsKey(nodeIndex)) {
			return null;
		}
		return remoteNodes.get(nodeIndex).settings.get(RemoteSettings.WIRELESS_ADDRESS_NODE.getKey());
	}
	
	/**
	 * Gets a remote node index/address map for all of the valid node indexes passed
	 * 
	 * @param nodeIndexes the node indexes (null to get all available addresses)
	 * @return the map of node indexes/addresses
	 */
	public Map<Integer, String> wirelessGetRemoteAddressMap(final Integer... nodeIndexes) {
		final Integer[] nis = nodeIndexes.length > 0 ? nodeIndexes : remoteNodes.keySet().toArray(new Integer[]{});
		final Map<Integer, String> was = new HashMap<Integer, String>(nis.length);
		for (final int nodeIndex : nis) {
			if (!remoteNodes.containsKey(nodeIndex)) {
				log.warn(String.format("%1$s remote node has not been created or does not exist", nodeIndex));
				continue;
			}
			was.put(nodeIndex, wirelessGetAddress(nodeIndex));
		}
		return was;
	}
	
	/**
	 * Synchronizes the locally hosted settings with the remote wireless node(s)
	 * 
	 * @return true when all node(s) have been updated successfully
	 */
	public boolean wirelessSendSettings(final int nodeIndex) {
		boolean allSuccess = false;
		if (!wirelessIsConnected()) {
			return allSuccess;
		}
		try {
			final RxTxRemoteSettingsData sd = new RxTxRemoteSettingsData(nodeIndex);
			final int[] sendData = sd.getAllData();
			log.info(String.format("Attempting to send: %s", sd));
			final Map<Integer, String> was = wirelessGetRemoteAddressMap(nodeIndex);
			final UGateKeeperEvent<int[]> event = new UGateKeeperEvent<int[]>(this, UGateKeeperEvent.Type.INITIALIZE, false, 
					was, null, Command.SENSOR_SET_SETTINGS, null, sendData);
			if (wirelessSendData(event)) {
				log.info(String.format("Settings sent to %1$s node(s)", was.size()));
				allSuccess = true;
			}
		} catch (final Throwable t) {
			log.error("Error while sending settings", t);
		}
		return allSuccess;
	}
	
	/**
	 * @return the remote address of the device node for which the controls represent
	 */
	public String wirelessGetCurrentRemoteNodeAddress() {
		return settingsGet(RemoteSettings.WIRELESS_ADDRESS_NODE, wirelessGetCurrentRemoteNodeIndex());
	}
	
	/**
	 * @return the remote index of the current device node for which the controls represent
	 */
	public int wirelessGetCurrentRemoteNodeIndex() {
		return this.wirelessCurrentRemoteNodeIndex;
	}
	
	/**
	 * Sets the current wireless remote node index
	 * 
	 * @param newNodeIndex the remote index of the device node for which the controls represent
	 * @param nodeAddy the new node address to set (when adding a new node and setting it's index)
	 * @param notifyListeners true to notify any listeners of the change
	 */
	private void wirelessSetCurrentRemoteNodeIndex(final int newNodeIndex, final String nodeAddy, final boolean notifyListeners) {
		int newAdjNodeIndex = newNodeIndex < 0 ? wirelessNextRemoteNodeIndex : newNodeIndex;
		if (newNodeIndex >= wirelessNextRemoteNodeIndex) {
			throw new ArrayIndexOutOfBoundsException(
					String.format("The node index cannot be set to %1$s because it must be less than the next node index: %2$s", 
							newAdjNodeIndex, wirelessNextRemoteNodeIndex));
		}
		if (newNodeIndex < 0) {
			if ((nodeAddy == null || nodeAddy.isEmpty())) {
				throw new IllegalArgumentException(
						String.format("The node index cannot be set to %1$s because the node address is required", 
								newAdjNodeIndex));
			} else if (wirelessGetAddressIndex(nodeAddy) > -1) {
				final int existingNodeIndex = wirelessGetAddressIndex(nodeAddy);
				if (existingNodeIndex > -1 && existingNodeIndex != newAdjNodeIndex) {
					log.warn(String.format("Found existing index %1$s for node address %2$s, but it does not match the specified index %3$s", 
							existingNodeIndex, nodeAddy, newAdjNodeIndex));
					newAdjNodeIndex = existingNodeIndex;
				}
			}
		}
		final int oldIndex = wirelessGetCurrentRemoteNodeIndex();
		final String oldAddy = wirelessGetCurrentRemoteNodeAddress();
		if (oldIndex != newAdjNodeIndex) {
			RemoteNode rn;
			// add the remote node (if it doesn't already exist)
			// otherwise, just set the 
			final boolean noAdd = remoteNodes.containsKey(newAdjNodeIndex);
			if (noAdd) {
				rn = remoteNodes.get(newAdjNodeIndex);
			} else if ((rn = addRemoteNode(newAdjNodeIndex, oldIndex, true, null)) == null) {
				this.wirelessCurrentRemoteNodeIndex = oldIndex;
				throw new NullPointerException(String.format("Unable to set new remote node from index %1$s to %2$s", 
						oldIndex, newAdjNodeIndex));
			}
			if (newNodeIndex < 0 && nodeAddy != null) {
				// need to set the node address to the newly created node
				rn.settings.set(RemoteSettings.WIRELESS_ADDRESS_NODE.getKey(), nodeAddy);
			}
			this.wirelessCurrentRemoteNodeIndex = newAdjNodeIndex;
			final String msg = RS.rbLabel(noAdd ? "wireless.node.remote.changing" : "wireless.node.remote.add", 
					oldAddy, nodeAddy);
			log.info(msg);
			if (notifyListeners) {
				notifyListeners(new UGateKeeperEvent<Integer>(this, noAdd ? UGateKeeperEvent.Type.SETTINGS_REMOTE_NODE_CHANGED_FROM_SELECT : 
					UGateKeeperEvent.Type.SETTINGS_REMOTE_NODE_CHANGED_FROM_ADD, false,
						wirelessGetRemoteAddressMap(this.wirelessCurrentRemoteNodeIndex),null, null, oldIndex, 
						this.wirelessCurrentRemoteNodeIndex, msg));
			}
		}
	}
	
	/**
	 * Performs the required wireless initialization
	 */
	private void wirelessInit() {
		// ensure that the needed RXTX is installed (if not install it)
		RS.initComm();
		// initialize all of the existing remote wireless node settings/preferences
		int i = RemoteSettings.WIRELESS_ADDRESS_START_INDEX;
		RemoteNode sfs = null;
		do {
			// should always have at least one remote node- thus the create clause
			sfs = addRemoteNode(i, null, i == RemoteSettings.WIRELESS_ADDRESS_START_INDEX, 
					i == RemoteSettings.WIRELESS_ADDRESS_START_INDEX ? RS.remoteDefaultPropertiesPath() : null);
			i++;
		} while (sfs != null);
	}
	
	/**
	 * This method is used to get a list of all the available Serial ports (note: only Serial ports are considered). 
	 * Any one of the elements contained in the returned {@link List} can be used as a parameter in 
	 * {@link #connect(String)} or {@link #connect(String, int)} to open a Serial connection.
	 * 
	 * @return A {@link List} containing {@link String}s showing all available Serial ports.
	 */
	public List<String> wirelessSerialPorts() {
		return RS.getSerialPorts();
	}
	
	/**
	 * Create a remote node with all of the required {@linkplain StorageFile}s
	 * 
	 * @param nodeIndex the node index of the remote node to add
	 * @param copyFromIndex the index of the {@linkplain RemoteNode} to copy the {@linkplain RemoteNode#settings} 
	 * 		from (null when a copy should not be made)
	 * @param createIfNotExists true to create the {@linkplain RemoteNode#settings} when it doesn't already exist 
	 * 		(not applicable when copying)
	 * @param copyFromFilePathIfNotExists the path to copy the storage file from when it doesn't exist
	 * 		(not applicable when copying from index)
	 * @return the added node (when successful)
	 */
	private RemoteNode addRemoteNode(final int nodeIndex, final Integer copyFromIndex, final boolean createIfNotExists,
			final Path copyFromFilePathIfNotExists) {
		StorageFile sf;
		if (copyFromIndex == null) {
			if (remoteNodes.containsKey(nodeIndex)) {
				log.warn(String.format("%1$s already exists for remote node index %2$s", 
						RemoteNode.class.getSimpleName(), nodeIndex));
				return null;
			}
			if (copyFromFilePathIfNotExists != null && !copyFromFilePathIfNotExists.toAbsolutePath().toString().isEmpty()) {
				sf = new StorageFile(RS.remotePropertiesFilePath(nodeIndex), copyFromFilePathIfNotExists);
			} else {
				sf = new StorageFile(RS.remotePropertiesFilePath(nodeIndex), createIfNotExists);
			}
		} else {
			if (!remoteNodes.containsKey(copyFromIndex)) {
				log.warn(String.format("No %1$s exists to copy from at remote node index %2$s", 
						RemoteNode.class.getSimpleName(), copyFromIndex));
				return null;
			}
			sf = remoteNodes.get(copyFromIndex).settings.createCopy(Paths.get(RS.remotePropertiesFileName(nodeIndex)));
		}
		if (!sf.isLoaded()) {
			return null;
		}
		final Path workingDir = wirelessWorkingDirectory(sf, nodeIndex);
		if (workingDir != null) {
			final StorageFile hs = new StorageFile(Paths.get(workingDir.toAbsolutePath().toString(), 
					RS.WIRELESS_PREFERENCE_HISTORY_FILE), true);
			if (hs.isLoaded()) {
				if (copyFromIndex != null) {
					// the new node shouldn't have the same address as the one that it was copied from
					sf.set(RemoteSettings.WIRELESS_ADDRESS_NODE.getKey(), "");
				}
				final RemoteNode remoteNode = new RemoteNode(sf, hs);
				remoteNodes.put(nodeIndex, remoteNode);
				log.info(String.format("Loaded remote node settings storage at %1$s \nand history storage at %2$s", 
						remoteNode.settings.getFilePath().toAbsolutePath().toString(), 
						remoteNode.history.getFilePath().toAbsolutePath().toString()));
				if ((nodeIndex + 1) > this.wirelessNextRemoteNodeIndex) {
					this.wirelessNextRemoteNodeIndex = nodeIndex + 1;
				}
				return remoteNode;
			}
		}
		return null;
	}
	
	/**
	 * Removes a remote node by index key
	 * 
	 * @param nodeIndex the node index to remove
	 */
	private void removeRemoteNode(final Integer nodeIndex) {
		remoteNodes.get(nodeIndex).dispose();
		remoteNodes.remove(nodeIndex);
		int maxNodeIndex = RemoteSettings.WIRELESS_ADDRESS_START_INDEX;
		for (final Map.Entry<Integer, RemoteNode> rn : remoteNodes.entrySet()) {
			if (rn.getKey() > maxNodeIndex) {
				maxNodeIndex = rn.getKey();
			}
		}
		wirelessNextRemoteNodeIndex = maxNodeIndex + 1;
	}
	
	/**
	 * Composite of required {@linkplain StorageFile}s for each remote node 
	 */
	private static class RemoteNode {
		
		public final StorageFile settings;
		public final StorageFile history;
		
		/**
		 * Constructor
		 * 
		 * @param settings the {@linkplain RemoteSettings} {@linkplain StorageFile}
		 * @param history the history {@linkplain StorageFile}
		 */
		public RemoteNode(final StorageFile settings, final StorageFile history) {
			this.settings = settings;
			this.history = history;
		}
		
		/**
		 * Disposes of the {@linkplain #settings} and {@linkplain #history} 
		 * by calling {@linkplain StorageFile#dispose()}
		 */
		public void dispose() {
			settings.dispose();
			history.dispose();
		}
	}
}
