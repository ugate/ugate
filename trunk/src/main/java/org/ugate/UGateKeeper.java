package org.ugate;

import gnu.io.CommPortIdentifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;
import org.ugate.mail.EmailAgent;
import org.ugate.mail.EmailEvent;
import org.ugate.mail.IEmailListener;
import org.ugate.xbee.UGateXBeePacketListener;

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
import com.rapplogic.xbee.util.ByteUtils;

/**
 * Gate keeper that keeps track of shared XBee, Mail, and other provider implementations
 */
public enum UGateKeeper {
	
	DEFAULT;

	private static final Logger log = Logger.getLogger(UGateKeeper.class);
	private static final List<IEmailListener> REQUESTS = new CopyOnWriteArrayList<IEmailListener>();

	/**
	 * Remote XBee radio used for gate operations using a 16-bit address: 3333 
	 * (XBee must NOT be configured with "MY" set to FFFF)
	 */
	public final XBeeAddress16 GATE_XBEE_ADDRESS = new XBeeAddress16(0x33, 0x33);
	public final Preferences preferences;
	public final List<IEmailListener> emailListeners = new ArrayList<IEmailListener>();
	private final XBee xbee;
	private EmailAgent emailAgent;
	private boolean isEmailConnected;
	
	private UGateKeeper() {
		preferences = new Preferences("ugate");
		xbee = new XBee();
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
	public void emailConnect(String smtpHost, String smtpPort, String imapHost, String imapPort, String username, 
			String password, String mainFolderName, boolean debug, IEmailListener... listener) {
		// test email connection
		isEmailConnected = true;
		listener[0].handle(new EmailEvent(EmailEvent.TYPE_CONNECT, null));
		if (true) {
			return;
		}
		// connect to email
		emailDisconnect();
		log.info("Connecting to email");
		final List<IEmailListener> listeners = new ArrayList<IEmailListener>();
		listeners.add(new IEmailListener() {
			
			@Override
			public void handle(EmailEvent event) {
				if (event.type == EmailEvent.TYPE_EXECUTE_COMMAND) {
					if (isXbeeConnected()) {
						for (String command : event.commands) {
							xbeeSendData(GATE_XBEE_ADDRESS, command);
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
	
	/* ======= Serial Communications ======= */
	
	/**
	 * Connects to the local XBee
	 * 
	 * @param comPort the COM port to connect to {@link #getSerialPorts()}
	 * @param baudRate the baud rate to connect at {@link #XBEE_BAUD_RATES}
	 * @return true when on successful connection
	 */
	public boolean xbeeConnect(String comPort, int baudRate) {
		xbeeDisconnect();
		log.info("Connecting to local XBee");
		try {
			xbee.open(comPort, baudRate);
			xbee.addPacketListener(new UGateXBeePacketListener());
			log.info("Connected to local XBee: \"" + comPort + "\" at " + baudRate);
			return true;
		} catch (XBeeException e) {
			log.error("Unable to connect to local XBee", e);
			return false;
		}
	}
	
	/**
	 * Disconnects from the local XBee
	 */
	public void xbeeDisconnect() {
		if (isXbeeConnected()) {
			log.info("Disconnecting from XBee");
			xbee.close();
			log.info("Disconnected from XBee");
		}
	}
	
	/**
	 * @return true if the local XBee is connected
	 */
	public boolean isXbeeConnected() {
		return xbee != null && xbee.isConnected();
	}

	/**
	 * Sends the data string to the remote address in ASCII format array
	 * 
	 * @param address the address to send to
	 * @param data the data string to send
	 * @return the TX status response
	 */
	public TxStatusResponse xbeeSendData(XBeeAddress16 address, String data) {
		return xbeeSendData(address, ByteUtils.stringToIntArray(data));
	}
	
	/**
	 * Sends the data string to the remote address in ASCII format array
	 * 
	 * @param address the address to send to
	 * @param data the data string to send
	 * @return the TX status response
	 */
	public TxStatusResponse xbeeSendData(XBeeAddress16 address, List<Integer> data) {
		int[] dataInts = new int[data.size()];
		for(int i=0; i<data.size(); i++) {
			dataInts[i] = data.get(i);
		}
		return xbeeSendData(address, dataInts);
	}
	
	/**
	 * Sends the data array to the remote XBee address
	 * 
	 * @param address the address to send to
	 * @param data the data to send
	 * @return the TX status response
	 */
	public TxStatusResponse xbeeSendData(XBeeAddress16 address, int[] data) {
		try {
			// create a unicast packet to be delivered to the supplied address, with the pay load
			TxRequest16 request = new TxRequest16(address, data);
			// send the packet and wait up to 10 seconds for the transmit status reply
			TxStatusResponse response = (TxStatusResponse) xbee.sendSynchronous(request, 12000);
			if (response.isSuccess()) {
				// packet was delivered successfully
				log.info("Data successfully sent");
			} else {
				// packet was not delivered
				throw new XBeeException("Packet was not delivered. status: " + response.getStatus());
			}
		} catch (XBeeTimeoutException e) {
			log.error("No response was received in the allotted time", e);
		} catch (XBeeException e) {
			log.error("Unexpected error occurred when sending data", e);
		}
		return null;
	}

	/**
	 * Sends a wireless command to the specified address
	 * 
	 * @param address the address of the device to send the command to
	 * @param commands the commands to send
	 */
	public void wirelessSendCommands(final String address, final int[] commands) {
		
	}

	/**
	 * Gets the address of a local or remote device
	 * 
	 * @param isRemote true when getting an address for a remote device
	 * @return the address of the device (if found)
	 */
	public String wirelessGetAddress(final Boolean isRemote) {
		String address = null;
		try {
			AtCommandResponse response;
			int[] responseValue;
			if (isRemote) {
				RemoteAtRequest request = new RemoteAtRequest(new XBeeAddress16(0x33, 0x33), "MY");
				RemoteAtResponse remoteResponse = (RemoteAtResponse) xbee.sendSynchronous(request, 10000);
				response = remoteResponse;
			} else {
				response = (AtCommandResponse) xbee.sendSynchronous(new AtCommand("MY"));
			}
			if (response.isOk()) {
				responseValue = response.getValue();
				address = ByteUtils.toBase16(responseValue);
				log.info("Successfully got " + (isRemote ? "remote" : "local") + " MY String: " + address);
			} else {
				throw new XBeeException("Failed to get remote MY. Status is " + response.getStatus());
			}
		} catch (XBeeTimeoutException e) {
			log.warn("Timed out getting remote XBee address", e);
		} catch (XBeeException e) {
			log.warn("Error getting remote XBee address", e);
		}
		return address;
	}

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

	/**
	 * @return true if the email agent is connected
	 */
	public boolean isEmailConnected() {
		return isEmailConnected;
	}
}
