package org.ugate;

import gnu.io.CommPortIdentifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.Logger;
import org.ugate.mail.EmailAgent;
import org.ugate.mail.EmailEvent;
import org.ugate.mail.IEmailListener;
import org.ugate.wireless.data.IWirelessListener;
import org.ugate.wireless.data.SettingsData;
import org.ugate.wireless.data.WirelessResponse;
import org.ugate.wireless.data.WirelessStatusCode;
import org.ugate.wireless.xbee.UGateXBeePacketListener;

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

	private static final Logger log = Logger.getLogger(UGateKeeper.class);
	private final List<IGateKeeperListener> prefListeners = new ArrayList<IGateKeeperListener>();
	private final Preferences preferences;
	private static final List<IWirelessListener> wirelessListeners = new ArrayList<IWirelessListener>();
	private final XBee xbee;
	private EmailAgent emailAgent;
	private boolean isEmailConnected;
	
	private UGateKeeper() {
		preferences = new Preferences("ugate");
		xbee = new XBee();
	}
	
	public void init() {
		
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
	 * Removes a preference listener
	 * 
	 * @param listener the listener to remove
	 */
	public void preferencesRemoveListener(final IGateKeeperListener listener) {
		if (listener != null && prefListeners.contains(listener)) {
			prefListeners.remove(listener);
		}
	}
	
	/**
	 * Adds a preference listener
	 * 
	 * @param listener the listener to add
	 */
	public void preferencesAddListener(final IGateKeeperListener listener) {
		if (listener != null && !prefListeners.contains(listener)) {
			prefListeners.add(listener);
		}
	}
	
	/**
	 * Sets a key/value preference
	 * 
	 * @param key the {@linkplain Settings}
	 * @param value the value
	 */
	public void preferencesSet(final Settings key, final String value) {
		final String oldValue = preferencesGet(key);
		if (!oldValue.equals(value)) {
			preferences.set(key.key, value);
			preferencesNotify(IGateKeeperListener.Event.SETTINGS_SAVE_LOCAL, null, 
					key, null, oldValue, value);
		}
	}
	
	/**
	 * Gets a preference value
	 * 
	 * @param key the key to get the value for
	 * @return the preference value
	 */
	public String preferencesGet(final Settings key) {
		return preferences.get(key.key);
	}
	
	/**
	 * Gets a preference value
	 * 
	 * @param key the key to get the value for
	 * @param delimiter the delimiter used to split the value
	 * @return the preference values
	 */
	public List<String> preferencesGet(final Settings key, final String delimiter) {
		return preferences.get(key.key, delimiter);
	}
	
	/**
	 * Notifies the listeners of the preference change
	 * 
	 * @param type the {@linkplain IGateKeeperListener.Event} type
	 * @param node the node the preference notification is for
	 * @param key the {@linkplain Settings}
	 * @param command the {@linkplain Command}
	 * @param oldValue the old value
	 * @param newValue the new value
	 */
	private void preferencesNotify(final IGateKeeperListener.Event type, 
			final String node, final Settings key, final Command command,
			final String oldValue, final String newValue) {
		for (final IGateKeeperListener pl : prefListeners) {
			pl.handle(type, node, key, command, oldValue, newValue);
		}
	}
	
	/* ======= Email Communications ======= */
	
	/**
	 * Connects to email
	 * 
	 * @param smtpHost the SMTP host for sending messages
	 * @param smtpPort the SMTP port for sending messages
	 * @param imapHost the IMAP host for receiving messages
	 * @param imapPort the IMAP port for receiving messages
	 * @param username the username to connect as
	 * @param password the password to connect as
	 * @param mainFolderName the email folder to listen for incoming messages (usually "Inbox"- case insensitive)
	 * @param debug true when the low level protocol calls made should be echoed to the console
	 * @param listener email listeners that can detect connections, disconnections, command executions, etc.
	 */
	public void emailConnect(final String smtpHost, final String smtpPort, final String imapHost, 
			final String imapPort, final String username, final String password, 
			final String mainFolderName, final boolean debug, final IEmailListener... listener) {
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
		log.info("Connecting to email");
		final List<IEmailListener> listeners = new ArrayList<IEmailListener>();
		listeners.add(new IEmailListener() {
			@Override
			public void handle(final EmailEvent event) {
				if (event.type == EmailEvent.TYPE_EXECUTE_COMMAND) {
					if (wirelessIsConnected()) {
						for (final Command command : event.commands) {
							// send command to all the nodes defined in the email
							for (int toNode : event.toNodes) {
								wirelessSendData(Settings.WIRELESS_ADDRESS_NODE_PREFIX_KEY.key + toNode, 
										command);
							}
						}
					} else {
						log.warn("Incoming mail command received, but an XBee connection has not been made");
					}
				} else if (event.type == EmailEvent.TYPE_CONNECT) {
					isEmailConnected = true;
					log.info("Connected to email");
				} else if (event.type == EmailEvent.TYPE_DISCONNECT) {
					isEmailConnected = false;
					log.info("Disconnected from email");
				}
			}
		});
		listeners.addAll(Arrays.asList(listener));
		this.emailAgent = new EmailAgent(smtpHost, smtpPort, imapHost, imapPort, 
				username, password, mainFolderName, debug, listeners.toArray(new IEmailListener[0]));
	}
	
	/**
	 * Disconnects from email
	 */
	public void emailDisconnect() {
		if (emailAgent != null) {
			emailAgent.disconnect();
			emailAgent = null;
		}
	}
	
	/**
	 * Adds an email listener to the currently connected agent
	 * 
	 * @param listener the listener to add
	 */
	public void emailAddListener(final IEmailListener listener){
		if (emailIsConnected()) {
			this.emailAgent.addListener(listener);
		} else {
			throw new IllegalStateException("Email is not connected");
		}
	}
	
	/**
	 * Removes an email listener to the currently connected agent
	 * 
	 * @param listener the listener to add
	 */
	public void emailRemoveListener(final IEmailListener listener){
		if (emailIsConnected()) {
			this.emailAgent.removeListener(listener);
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
		//wirelessDisconnect();
		if (xbee.isConnected()) {
			return true;
		}
		log.info("Connecting to local XBee");
		try {
			xbee.open(comPort, baudRate);
			xbee.addPacketListener(new UGateXBeePacketListener(){
				@Override
				protected <T, R extends WirelessResponse<T>> void handleWirelessResponse(
						R wirelessResponse) {
					//wirelessResponse;
				}
			});
			log.info("Connected to local XBee: \"" + comPort + "\" at " + baudRate);
			return true;
		} catch (XBeeException e) {
			log.error("Unable to connect to local XBee", e);
			return false;
		}
	}
	
	/**
	 * Disconnects from the wireless network
	 */
	public void wirelessDisconnect() {
		if (wirelessIsConnected()) {
			log.info("Disconnecting from XBee");
			xbee.close();
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
	 * Sends the data string to the remote address in ASCII format array
	 * 
	 * @param wirelessNodeAddressHexKey the wireless node address preferences key used to get the address to send to
	 * @param command the executing {@linkplain Command}
	 * @param data the data string to send
	 * @return true when successful
	 */
	public boolean wirelessSendData(final String wirelessNodeAddressHexKey, 
			final Command command, final String data) {
		return wirelessSendData(wirelessNodeAddressHexKey, command, ByteUtils.stringToIntArray(data));
	}
	
	/**
	 * Sends the data string to the remote address in ASCII format array
	 * 
	 * @param wirelessNodeAddressHexKey the wireless node address preferences key used to get the address to send to
	 * @param command the executing {@linkplain Command}
	 * @param data the data string to send
	 * @return true when successful
	 */
	public boolean wirelessSendData(final String wirelessNodeAddressHexKey, 
			final Command command, final List<Integer> data) {
		int[] dataInts = new int[data.size()];
		for(int i=0; i<data.size(); i++) {
			dataInts[i] = data.get(i);
		}
		return wirelessSendData(wirelessNodeAddressHexKey, command, dataInts);
	}
	
	/**
	 * Sends the data array to the remote address
	 * 
	 * @param wirelessNodeAddressHexKey the wireless node address preferences key used to get the address to send to
	 * @param command the executing {@linkplain Command}
	 * @param data the data to send
	 * @return true when successful
	 */
	public boolean wirelessSendData(final String wirelessNodeAddressHexKey, 
			final Command command, final int... data) {
		try {
			// bytes header command and status/failure code
			final int[] bytesHeader = new int[] { command.id, WirelessStatusCode.NONE.ordinal() };
			final int[] bytes = data != null && data.length > 0 ? 
					UGateUtil.arrayConcatInt(bytesHeader, data) : bytesHeader;
			// TODO : allow for commands to be sent to more than one wireless node?
			if (!preferences.hasKey(wirelessNodeAddressHexKey)) {
				log.error(String.format("Wireless node address \"%1$s\" has not been defined", 
						wirelessNodeAddressHexKey));
				return false;
			}
			final XBeeAddress16 xbeeAddress = wirelessGetXbeeAddress(wirelessNodeAddressHexKey);
			// create a unicast packet to be delivered to the supplied address, with the pay load
			final TxRequest16 request = new TxRequest16(xbeeAddress, bytes);
			// send the packet and wait up to 10 seconds for the transmit status reply
			final TxStatusResponse response = (TxStatusResponse) xbee.sendSynchronous(request, 12000);
			if (response.isSuccess()) {
				// packet was delivered successfully
				log.info("Data successfully sent");
			} else {
				// packet was not delivered
				throw new XBeeException("Packet was not delivered. status: " + response.getStatus());
			}
			return response.isSuccess();
		} catch (XBeeTimeoutException e) {
			log.error(String.format("Wireless transfer failed. No response from %1$s was received in the allotted time",
					preferences.hasKey(wirelessNodeAddressHexKey)), e);
		} catch (XBeeException e) {
			log.error(String.format("Unexpected error occurred when wirelessly transferring data to %1$s",
					wirelessNodeAddressHexKey), e);
		}
		return false;
	}

	/**
	 * Tests the address of a local or remote device within the wireless network
	 * 
	 * @param wirelessAddressHexKey the wireless address preferences key used to get the address 
	 * 		to test a connection for (null when testing the local address)
	 * @return the address of the device (if found and returns its address)
	 */
	public String wirelessTestAddressConnection(final String wirelessAddressHexKey) {
		String address = null;
		try {
			AtCommandResponse response;
			int[] responseValue;
			if (wirelessAddressHexKey != null) {
				final RemoteAtRequest request = new RemoteAtRequest(wirelessGetXbeeAddress(wirelessAddressHexKey), "MY");
				final RemoteAtResponse remoteResponse = (RemoteAtResponse) xbee.sendSynchronous(request, 10000);
				response = remoteResponse;
			} else {
				response = (AtCommandResponse) xbee.sendSynchronous(new AtCommand("MY"));
			}
			if (response.isOk()) {
				responseValue = response.getValue();
				address = ByteUtils.toBase16(responseValue);
				log.info("Successfully got " + (wirelessAddressHexKey != null ? "remote" : "local") + " address: " + address);
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
	 * Gets a wireless XBee address for a wireless node
	 * 
	 * @param wirelessAddressHexKey the wireless address preferences key used to create the address
	 * @return the XBee address
	 */
	private XBeeAddress16 wirelessGetXbeeAddress(final String wirelessAddressHexKey) {
		//final int xbeeRawAddress = Integer.parseInt(preferences.get(wirelessAddressHexKey), 16);
		final String rawAddress = preferences.get(wirelessAddressHexKey);
		if (rawAddress.length() > UGateUtil.WIRELESS_ADDRESS_MAX_DIGITS) {
			throw new IllegalArgumentException("Wireless address cannot be more than " + 
					UGateUtil.WIRELESS_ADDRESS_MAX_DIGITS + " hex digits long");
		}
		final int msb = Integer.parseInt(rawAddress.substring(0, 2), 16);
		final int lsb = Integer.parseInt(rawAddress.substring(2, 4), 16);
		final XBeeAddress16 xbeeAddress = new XBeeAddress16(msb, lsb);
		return xbeeAddress;
	}
	
	/**
	 * Gets all of the wireless node addresses from the preferences
	 * 
	 * @param testConnections true to test each address before adding them to the list
	 * @return the list of addresses
	 */
	public List<String> wirelessGetNodeAddresses(final boolean testConnections) {
		final List<String> was = new ArrayList<String>();
		final List<String> waks = wirelessGetNodeAddressKeys(false);
		for (String wak : waks) {
			if (testConnections) {
				if (wirelessTestAddressConnection(wak) != null) {
					was.add(preferences.get(wak));
				} else {
					log.info("Invalid wireless address preference key: " + wak);
				}
			} else {
				was.add(preferences.get(wak));
			}
		}
		return was;
	}
	
	/**
	 * Gets the wireless connection node address keys from the preferences
	 * @param testConnections true to test/validate the addresses by testing the connection 
	 * 		before adding to the list
	 * @return the wireless address preference keys
	 */
	public List<String> wirelessGetNodeAddressKeys(final boolean testConnections) {
		final List<String> waks = new ArrayList<String>();
		int i = 1;
		String addressKey;
		while (preferences.hasKey((addressKey = Settings.WIRELESS_ADDRESS_NODE_PREFIX_KEY.key + i))) {
			if (testConnections) {
				if (wirelessTestAddressConnection(addressKey) != null) {
					waks.add(addressKey);
				} else {
					log.info("Invalid wireless address preference key: " + addressKey);
				}
			} else {
				waks.add(addressKey);
			}
			i++;
		}
		return waks;
	}
	
	/**
	 * Synchronizes the locally stored settings with the remote wireless node(s)
	 * 
	 * @param wirelessNodes the wireless nodes to send to (null to send to all)
	 * @return true when all node(s) have been updated successfully
	 */
	public boolean wirelessSendSettings(final String... wirelessNodes) {
		boolean allSuccess = false;
		if (!wirelessIsConnected()) {
			return allSuccess;
		}

		// send the command, status code, and settings data
		try {
			final SettingsData sd = new SettingsData();
			final int[] sendData = sd.toArray();
			log.info(String.format("Attempting to send: %s", sd));
			final List<String> waks = wirelessNodes == null || wirelessNodes.length == 0 ? 
					wirelessGetNodeAddressKeys(false) : Arrays.asList(wirelessNodes);
			int scnt = 0;
			for (int i=0; i<waks.size(); i++) {
				preferencesNotify(IGateKeeperListener.Event.SETTINGS_SENDING, 
						waks.get(i), null, Command.SENSOR_SET_SETTINGS, null, null);
				log.debug("Sending settings to node for key " + waks.get(i));
				if (wirelessSendData(waks.get(i), Command.SENSOR_SET_SETTINGS, sendData)) {
					log.debug("Sent settings to node for key " + waks.get(i));
					preferencesNotify(IGateKeeperListener.Event.SETTINGS_SEND_SUCCESS, 
							waks.get(i), null, Command.SENSOR_SET_SETTINGS, null, null);
					scnt++;
				} else {
					log.warn(String.format("Failed to send settings to node %2$s (preference key: %1$s)", 
							waks.get(i), preferences.get(waks.get(i))));
					preferencesNotify(IGateKeeperListener.Event.SETTINGS_SEND_FAILED, 
							waks.get(i), null, Command.SENSOR_SET_SETTINGS, null, null);
				}
			}
			log.info("Settings sent to " + scnt + " node(s)");
			if (scnt == waks.size()) {
				allSuccess = true;
			}
		} catch (final Throwable t) {
			log.error("Error while sending settings", t);
		}
		return allSuccess;
	}
}
