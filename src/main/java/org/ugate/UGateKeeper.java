package org.ugate;

import gnu.io.CommPortIdentifier;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
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

	private static final String WIRELESS_HOST_SETTINGS_FILE = "host";
	private static final String WIRELESS_PREFERENCE_FILE_PREFIX = "remote-node-";
	private static final String WIRELESS_PREFERENCE_HISTORY_FILE = "history";
	private final Logger log;
	private final List<IGateKeeperListener> listeners = new ArrayList<IGateKeeperListener>();
	private final XBee xbee;
	private EmailAgent emailAgent;
	private boolean isEmailConnected;
	private final StorageFile hostSettings;
	private Map<Integer, RemoteNode> remoteNodes = new HashMap<Integer, RemoteNode>(1);
	private int wirelessCurrentRemoteNodeIndex = RemoteSettings.WIRELESS_ADDRESS_START_INDEX;
	
	private UGateKeeper() {
		log = Logger.getLogger(UGateKeeper.class);
		hostSettings = new StorageFile(WIRELESS_HOST_SETTINGS_FILE, true);
		wirelessInitRemoteNodeStorage();
		xbee = new XBee();
	}
	
	/**
	 * Exit the gate keeper services
	 */
	public void exit() {
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
			notifyListeners(new UGateKeeperEvent<String>(this, UGateKeeperEvent.Type.SETTINGS_SAVE_LOCAL, null, 0,
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
			try {
				pl.handle(event);
			} catch (final Throwable t) {
				log.warn("Unable to notify listener: " + pl, t);
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
			event = new UGateKeeperEvent<Void>(this, UGateKeeperEvent.Type.EMAIL_CONNECTING);
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
								notifyListeners(new UGateKeeperEvent<List<Command>>(this, UGateKeeperEvent.Type.EMAIL_EXECUTED_COMMANDS, addys, 0, 
										RemoteSettings.WIRELESS_ADDRESS_NODE, null, null, event.commands, commandMsgs.toArray(new String[]{})));
							} else {
								notifyListeners(new UGateKeeperEvent<List<Command>>(this, UGateKeeperEvent.Type.EMAIL_EXECUTE_COMMANDS_FAILED, addys, 0, 
										RemoteSettings.WIRELESS_ADDRESS_NODE, null, null, event.commands, commandMsgs.toArray(new String[]{})));
							}
						} else {
							msg = RS.rbLabel("service.email.commandexec.failed", UGateUtil.toString(event.commands), UGateUtil.toString(event.from), 
									UGateUtil.toString(event.toAddresses), RS.rbLabel("service.wireless.connection.required"));
							log.warn(msg);
							notifyListeners(new UGateKeeperEvent<List<Command>>(this, UGateKeeperEvent.Type.EMAIL_EXECUTE_COMMANDS_FAILED, null, 0, 
									RemoteSettings.WIRELESS_ADDRESS_NODE, null, null, event.commands, msg));
						}
					} else if (event.type == EmailEvent.Type.CONNECT) {
						isEmailConnected = true;
						msg = RS.rbLabel("mail.connected");
						log.info(msg);
						notifyListeners(new UGateKeeperEvent<List<Command>>(this, UGateKeeperEvent.Type.EMAIL_CONNECTED, null, 0, 
								null, null, null, event.commands, msg));
					} else if (event.type == EmailEvent.Type.DISCONNECT) {
						isEmailConnected = false;
						msg = RS.rbLabel("mail.disconnected");
						log.info(msg);
						notifyListeners(new UGateKeeperEvent<List<Command>>(this, UGateKeeperEvent.Type.EMAIL_DISCONNECTED, null, 0, 
								null, null, null, event.commands, msg));
					} else if (event.type == EmailEvent.Type.CLOSED) {
						isEmailConnected = false;
						msg = RS.rbLabel("mail.closed");
						log.info(msg);
						notifyListeners(new UGateKeeperEvent<List<Command>>(this, UGateKeeperEvent.Type.EMAIL_CLOSED, null, 0, 
								null, null, null, event.commands, msg));
					}
				}
			});
			this.emailAgent = EmailAgent.start(smtpHost, smtpPort, imapHost, imapPort, 
					username, password, mainFolderName, listeners.toArray(new IEmailListener[0]));
			// TODO : remove reference to GUI impl
//			final Service<Void> emailService = new Service<Void>() {
//				@Override
//				protected Task<Void> createTask() {
//					return new Task<Void>() {
//						@Override
//						protected Void call() throws Exception {
//							UGateKeeper.this.emailAgent.run();
//							return null;
//						}
//					};
//				}
//			};
//			emailService.start();
		} catch (final Throwable t) {
			msg = RS.rbLabel("mail.connect.failed", 
					smtpHost, smtpPort, imapHost, imapPort, username, mainFolderName);
			log.error(msg, t);
			event = new UGateKeeperEvent<Void>(this, UGateKeeperEvent.Type.EMAIL_CONNECT_FAILED);
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
			notifyListeners(new UGateKeeperEvent<Void>(this, UGateKeeperEvent.Type.EMAIL_DISCONNECTING, msg));
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

	/* ======= Serial Ports ======= */
	
	/**
	 * This method is used to get a list of all the available Serial ports (note: only Serial ports are considered). 
	 * Any one of the elements contained in the returned {@link List} can be used as a parameter in 
	 * {@link #connect(String)} or {@link #connect(String, int)} to open a Serial connection.
	 * 
	 * @return A {@link List} containing {@link String}s showing all available Serial ports.
	 */
	@SuppressWarnings("unchecked")
	public List<String> getSerialPorts() {
		log.debug("Loading available COM ports");
		Enumeration<CommPortIdentifier> portList;
		List<String> ports = new ArrayList<String>();
		portList = CommPortIdentifier.getPortIdentifiers();
		CommPortIdentifier portId;
		while (portList.hasMoreElements()) {
			portId = portList.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				ports.add(portId.getName());
			}
		}
		if (log.isDebugEnabled()) {
			log.debug("Found the following ports:");
			for (int i = 0; i < ports.size(); i++) {
				log.debug("   " + ports.get(i));
			}
		}
		return ports;
	}
	
	/* ======= Wireless Communications ======= */
	
	/**
	 * Connects to the wireless network
	 * 
	 * @param comPort the COM port to connect to {@link #getSerialPorts()}
	 * @param baudRate the baud rate to connect at (if applicable)
	 * @return true when on successful connection
	 */
	public boolean wirelessConnect(final String comPort, final int baudRate) {
		wirelessDisconnect();
		log.info("Connecting to local XBee");
		notifyListeners(new UGateKeeperEvent<Void>(this, UGateKeeperEvent.Type.WIRELESS_HOST_CONNECTING));
		try {
			xbee.open(comPort, baudRate);
			xbee.addPacketListener(new UGateXBeePacketListener(){
				@Override
				protected <V extends RxData> void handleEvent(final UGateKeeperEvent<V> event) {
					notifyListeners(event);
				}
			});
			log.info(String.format("Connected to local XBee using port %1$s and baud rate %2$s", 
					comPort, baudRate));
			// XBee connection is blocking so notification can be sent here
			notifyListeners(new UGateKeeperEvent<Void>(this, UGateKeeperEvent.Type.WIRELESS_HOST_CONNECTED));
			return true;
		} catch (final Throwable t) {
			final String errorMsg = String.format("Unable to establish a connection to the local XBee using port %1$s and baud rate %2$s", 
					comPort, baudRate);
			log.error(errorMsg, t);
			notifyListeners(new UGateKeeperEvent<Void>(this, UGateKeeperEvent.Type.WIRELESS_HOST_CONNECT_FAILED, errorMsg, t.getMessage()));
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
				notifyListeners(new UGateKeeperEvent<Void>(this, UGateKeeperEvent.Type.WIRELESS_HOST_DISCONNECTING, msg));	
			}
			try {
				xbee.close();
				msg = "Disconnected from XBee";
				log.info(msg);
				if (notify) {
					// XBee close is blocking so notification can be sent here
					notifyListeners(new UGateKeeperEvent<Void>(this, UGateKeeperEvent.Type.WIRELESS_HOST_DISCONNECTED, msg));
				}
			} catch (final Throwable t) {
				msg = "Unable to close wireless connection";
				log.error(msg, t);
				if (notify) {
					notifyListeners(new UGateKeeperEvent<Void>(this, UGateKeeperEvent.Type.WIRELESS_HOST_DISCONNECT_FAILED, 
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
	public String wirelessWorkingDirectory(final int nodeIndex) {
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
	private String wirelessWorkingDirectory(final StorageFile storageFile, final int nodeIndex) {
		String workingDir = storageFile.get(RemoteSettings.WIRELESS_WORKING_DIR_PATH.getKey());
		workingDir += workingDir.charAt(workingDir.length() - 1) != '/' ? "/" + nodeIndex + '/' : nodeIndex + "/";
		final File filePath = new File(workingDir);
		if (!filePath.exists()) {
			try {
				filePath.mkdirs();
			} catch (final Exception e) {
				log.warn("Unable to initialize the working directory path at: " + workingDir, e);
				return null;
			}
		} else if(!filePath.isDirectory() || !filePath.canWrite()) {
			log.error(String.format("The %1$s path %2$s must be an accessible/writable directory", 
					RemoteSettings.WIRELESS_WORKING_DIR_PATH, filePath));
			return null;
		}
		return workingDir;
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
		return wirelessSendData(new UGateKeeperEvent<int[]>(this, UGateKeeperEvent.Type.INITIALIZE, 
				addys, 0, null, command, null, data));
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
			final UGateKeeperEvent<int[]> event = new UGateKeeperEvent<int[]>(this, UGateKeeperEvent.Type.INITIALIZE, 
					was, 0, null, Command.SENSOR_SET_SETTINGS, null, sendData);
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
	 * @return the remote index of the device node for which the controls represent
	 */
	public int wirelessGetCurrentRemoteNodeIndex() {
		return this.wirelessCurrentRemoteNodeIndex;
	}
	
	/**
	 * Sets the current wireless remote node index
	 * 
	 * @param wirelessCurrentRemoteNodeIndex the remote index of the device node for which the controls represent
	 */
	public void wirelessSetCurrentRemoteNodeIndex(final int wirelessCurrentRemoteNodeIndex) {
		if (wirelessCurrentRemoteNodeIndex <= 0) {
			throw new ArrayIndexOutOfBoundsException(wirelessCurrentRemoteNodeIndex);
		}
		final int oldIndex = wirelessGetCurrentRemoteNodeIndex();
		if (oldIndex != wirelessCurrentRemoteNodeIndex) {
			this.wirelessCurrentRemoteNodeIndex = wirelessCurrentRemoteNodeIndex;
			// add the remote node- if it doesn't already exist
			addRemoteNode(this.wirelessCurrentRemoteNodeIndex, oldIndex, true);
			final String msg = RS.rbLabel("wireless.node.remote.changing", oldIndex, this.wirelessCurrentRemoteNodeIndex);
			log.info(msg);
			notifyListeners(new UGateKeeperEvent<Integer>(this, UGateKeeperEvent.Type.SETTINGS_REMOTE_NODE_CHANGED, 
					null, 0, null, null, oldIndex, this.wirelessCurrentRemoteNodeIndex, msg));
		}
	}
	
	/**
	 * Initializes all of the existing remote wireless node settings/preferences
	 */
	private void wirelessInitRemoteNodeStorage() {
		int i = RemoteSettings.WIRELESS_ADDRESS_START_INDEX;
		RemoteNode sfs = null;
		do {
			// should always have at least one remote node- thus the create clause
			sfs = addRemoteNode(i, null, i == RemoteSettings.WIRELESS_ADDRESS_START_INDEX);
			i++;
		} while (sfs != null);
	}
	
	/**
	 * Create a remote node with all of the required {@linkplain StorageFile}s
	 * 
	 * @param nodeIndex the node index of the remote node to add
	 * @param copyFromIndex the index of the {@linkplain RemoteNode} to copy the {@linkplain RemoteNode#settings} 
	 * 		from (null when a copy should not be made)
	 * @param createIfNotExists true to create the {@linkplain RemoteNode#settings} when it doesn't already exist 
	 * 		(not applicable when copying)
	 * @return the added node (when successful)
	 */
	private RemoteNode addRemoteNode(final int nodeIndex, final Integer copyFromIndex, final boolean createIfNotExists) {
		StorageFile sf;
		if (copyFromIndex == null) {
			if (remoteNodes.containsKey(nodeIndex)) {
				log.warn(String.format("%1$s already exists for remote node index %2$s", 
						RemoteNode.class.getSimpleName(), nodeIndex));
				return null;
			}
			sf = new StorageFile(WIRELESS_PREFERENCE_FILE_PREFIX + nodeIndex, createIfNotExists);
		} else {
			if (!remoteNodes.containsKey(copyFromIndex)) {
				log.warn(String.format("No %1$s exists to copy from at remote node index %2$s", 
						RemoteNode.class.getSimpleName(), copyFromIndex));
				return null;
			}
			sf = remoteNodes.get(copyFromIndex).settings.createCopy(WIRELESS_PREFERENCE_FILE_PREFIX + nodeIndex);
		}
		if (!sf.isLoaded()) {
			return null;
		}
		final String workingDir = wirelessWorkingDirectory(sf, nodeIndex);
		if (workingDir != null) {
			final StorageFile hs = new StorageFile(workingDir + WIRELESS_PREFERENCE_HISTORY_FILE, true);
			if (hs.isLoaded()) {
				if (copyFromIndex != null) {
					// the new node shouldn't have the same address as the one that it was copied from
					sf.set(RemoteSettings.WIRELESS_ADDRESS_NODE.getKey(), "");
				}
				final RemoteNode remoteNode = new RemoteNode(sf, hs);
				remoteNodes.put(nodeIndex, remoteNode);
				log.info(String.format("Loaded remote node settings storage at %1$s and history storage at %2$s", 
						remoteNode.settings.getAbsoluteFilePath(), 
						remoteNode.history.getAbsoluteFilePath()));
				return remoteNode;
			}
		}
		return null;
	}
	
	/**
	 * Composite of required {@linkplain StorageFile}s for each remote node 
	 */
	private static class RemoteNode {
		
		public final StorageFile settings;
		public final StorageFile history;
		
		public RemoteNode(final StorageFile settings, final StorageFile history) {
			this.settings = settings;
			this.history = history;
		}
	}
}
