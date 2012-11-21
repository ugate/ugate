package org.ugate.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.ugate.ByteUtils;
import org.ugate.UGateKeeper;
import org.ugate.UGateEvent;
import org.ugate.UGateUtil;
import org.ugate.resources.RS;
import org.ugate.resources.RS.KEY;
import org.ugate.service.dao.RemoteNodeDao;
import org.ugate.service.entity.Command;
import org.ugate.service.entity.RemoteNodeType;
import org.ugate.service.entity.jpa.Host;
import org.ugate.service.entity.jpa.RemoteNode;
import org.ugate.wireless.data.RxData;
import org.ugate.wireless.data.RxTxRemoteNodeDTO;
import org.ugate.wireless.data.UGateXBeePacketListener;

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
 * Wireless service
 */
@Service
@Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
public class WirelessService {

	private final Logger log = UGateUtil.getLogger(WirelessService.class);
	public static final int DEFAULT_WAIT_MILISECONDS = 12000;
	private XBee xbee;
	private UGateXBeePacketListener packetListener;
	private boolean requiresRestart;
	private boolean isListening;
	
	@Resource
	private RemoteNodeDao remoteNodeDao;

	/**
	 * Only {@linkplain ServiceProvider} constructor
	 */
	WirelessService() {
	}

	/**
	 * Connects to the local wireless device
	 * 
	 * @return true if the connection has been established
	 */
	public boolean init() {
		if (xbee != null) {
			return true;
		}
		log.info("Initializing local XBee");
		// ensure that the needed RXTX is installed (if not install it)
		requiresRestart = RS.initComm();
		if (requiresRestart) {
			return false;
		}
		xbee = new XBee();
		packetListener = new UGateXBeePacketListener() {
			@Override
			protected <V extends RxData> void handleEvent(final UGateEvent<RemoteNode, V> event) {
				UGateKeeper.DEFAULT.notifyListeners(event);
			}
		};
		// test the serial ports
		getSerialPorts();
		return true;
	}
	
	/**
	 * Connects to the wireless network
	 * 
	 * @param host
	 *            the {@linkplain Host} to wirelessBtn to
	 */
	public boolean connect(final Host host, final RemoteNode remoteNode) {
		if (xbee == null) {
			init();
		}
		if (isListening()) {
			disconnect();
		}
		log.info("Connecting to local XBee");
		UGateKeeper.DEFAULT.notifyListeners(new UGateEvent<WirelessService, Void>(
				this, UGateEvent.Type.WIRELESS_HOST_CONNECTING, false));
		try {
			xbee.open(host.getComPort(), host.getComBaud());
			if (!isListening()) {
				xbee.addPacketListener(packetListener);
				isListening = true;
			}
			log.info(String.format("Connected to local XBee using port %1$s and baud rate %2$s", 
					host.getComAddress(), host.getComBaud()));
			// XBee connection is blocking so notification can be sent here
			UGateKeeper.DEFAULT.notifyListeners(new UGateEvent<WirelessService, Void>(
					this, UGateEvent.Type.WIRELESS_HOST_CONNECTED, false));
			return true;
		} catch (final Throwable t) {
			final String errorMsg = String.format("Unable to establish a connection to the local XBee at address %1$s using port %2$s and baud rate %3$s", 
					host.getComAddress(), host.getComPort(), host.getComBaud());
			log.error(errorMsg, t);
			UGateKeeper.DEFAULT.notifyListeners(new UGateEvent<WirelessService, Void>(
					this, UGateEvent.Type.WIRELESS_HOST_CONNECT_FAILED, false, errorMsg, t.getMessage()));
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
	public void disconnect() {
		wirelessDisconnectInternal(true);
	}
	
	/**
	 * Disconnects from the wireless network
	 * 
	 * @param notify true to notify listeners
	 */
	private void wirelessDisconnectInternal(final boolean notify) {
		if (isConnected()) {
			String msg = "Disconnecting from XBee";
			log.info(msg);
			if (notify) {
				UGateKeeper.DEFAULT.notifyListeners(new UGateEvent<WirelessService, Void>(
						WirelessService.this, UGateEvent.Type.WIRELESS_HOST_DISCONNECTING, false, msg));	
			}
			try {
				xbee.close();
				msg = "Disconnected from XBee";
				log.info(msg);
				if (notify) {
					// XBee close is blocking so notification can be sent here
					UGateKeeper.DEFAULT.notifyListeners(new UGateEvent<WirelessService, Void>(
							WirelessService.this, UGateEvent.Type.WIRELESS_HOST_DISCONNECTED, false, msg));
				}
			} catch (final Throwable t) {
				msg = "Unable to close wireless connection";
				log.error(msg, t);
				if (notify) {
					UGateKeeper.DEFAULT.notifyListeners(new UGateEvent<WirelessService, Void>(
							WirelessService.this, UGateEvent.Type.WIRELESS_HOST_DISCONNECT_FAILED, false, 
							msg, t.getMessage()));
				}
			}
			log.info("Disconnected from XBee");
		}
	}

	/**
	 * Gets {@linkplain RemoteNode}s for a given {@linkplain Host}
	 * 
	 * @param host
	 *            the {@linkplain Host}
	 * @return a {@linkplain List} for the service {@linkplain Host}
	 */
	public List<RemoteNode> findRemoteNodesByHost(final Host host) {
		if (host != null && host.getId() > 0) {
			return remoteNodeDao.findByHostId(host.getId());
		}
		return new ArrayList<>();
	}

	/**
	 * Gets {@linkplain RemoteNode} for a given {@linkplain RemoteNode#getAddress()}
	 * 
	 * @param address
	 *            the {@linkplain RemoteNode#getAddress()}
	 * @return a {@linkplain RemoteNode}
	 */
	public RemoteNode findRemoteNodeByAddress(final String address) {
		if (address != null && !address.isEmpty()) {
			return remoteNodeDao.findByAddress(address);
		}
		return null;
	}

	/**
	 * @return true when connected to the wireless network
	 */
	public boolean isConnected() {
		return xbee != null && xbee.isConnected();
	}

	/**
	 * Sends the data string to the {@linkplain RemoteNode#getAddress()} in
	 * ASCII format array
	 * 
	 * @param remoteNode
	 *            the {@linkplain RemoteNode} to send the data to
	 * @param command
	 *            the executing {@linkplain Command}
	 * @param timeout
	 *            the number of milliseconds that will be used to wait for a
	 *            response before timing out
	 * @param throwRuntimeException
	 *            true to throw any {@linkplain Throwable} as a
	 *            {@linkplain RuntimeException}
	 * @param data
	 *            the data string to send
	 * @return true when successful
	 */
	public boolean sendData(final RemoteNode remoteNode, final Command command,
			final int timeout, final boolean throwRuntimeException,
			final String data) {
		return sendData(remoteNode, command, timeout, throwRuntimeException,
				ByteUtils.stringToIntArray(data));
	}

	/**
	 * Sends the data string to the {@linkplain RemoteNode#getAddress()} in
	 * ASCII format array
	 * 
	 * @param remoteNode
	 *            the {@linkplain RemoteNode} to send the data to
	 * @param command
	 *            the executing {@linkplain Command}
	 * @param timeout
	 *            the number of milliseconds that will be used to wait for a
	 *            response before timing out
	 * @param throwRuntimeException
	 *            true to throw any {@linkplain Throwable} as a
	 *            {@linkplain RuntimeException}
	 * @param data
	 *            the data string to send
	 * @return true when successful
	 */
	public boolean sendData(final RemoteNode remoteNode, final Command command,
			final int timeout, final boolean throwRuntimeException,
			final List<Integer> data) {
		int[] dataInts = new int[data.size()];
		for (int i = 0; i < data.size(); i++) {
			dataInts[i] = data.get(i);
		}
		return sendData(remoteNode, command, timeout, throwRuntimeException,
				dataInts);
	}

	/**
	 * Sends the data array to the {@linkplain RemoteNode#getAddress()}
	 * 
	 * @param remoteNode
	 *            the {@linkplain RemoteNode} to send the data to
	 * @param command
	 *            the executing {@linkplain Command}
	 * @param timeout
	 *            the number of milliseconds that will be used to wait for a
	 *            response before timing out
	 * @param throwRuntimeException
	 *            true to throw any {@linkplain Throwable} as a
	 *            {@linkplain RuntimeException}
	 * @param data
	 *            the data to send
	 * @return true when successful
	 */
	public boolean sendData(final RemoteNode remoteNode, final Command command,
			final int timeout, final boolean throwRuntimeException,
			final int... data) {
		return sendData(new UGateEvent<RemoteNode, int[]>(remoteNode,
				UGateEvent.Type.INITIALIZE, false, null, command, null, data),
				timeout, throwRuntimeException);
	}
	
	/**
	 * Sends the data array to the {@linkplain RemoteNode#getAddress()}
	 * 
	 * @param event
	 *            the event that contains the <code>int</code> array of data to
	 *            send {@linkplain UGateEvent#getNewValue()}, the
	 *            {@linkplain UGateEvent#getCommand()}, and
	 *            {@linkplain UGateEvent#getSource()} to send the data to
	 * @param timeout
	 *            the number of milliseconds that will be used to wait for a
	 *            response before timing out
	 * @param throwRuntimeException
	 *            true to throw any {@linkplain Throwable} as a
	 *            {@linkplain RuntimeException}
	 * @return true when successful
	 */
	private boolean sendData(final UGateEvent<RemoteNode, int[]> event,
			final int timeout, final boolean throwRuntimeException) {
		if (event.getSource() == null) {
			throw new NullPointerException("No wireless node addresses to send data to");
		}
		int i = 0;
		int successCount = 0;
		int failureCount = 0;
		UGateKeeper.DEFAULT.notifyListeners(event.clone(UGateEvent.Type.WIRELESS_DATA_ALL_TX, i));
		String message;
		try {
			// bytes header command and status/failure code
			final int[] bytesHeader = new int[] { event.getCommand().getKey(), RxData.Status.NORMAL.ordinal() };
			final int[] bytes = event.getNewValue() != null && event.getNewValue().length > 0 ? 
					UGateUtil.arrayConcatInt(bytesHeader, event.getNewValue()) : bytesHeader;
			final XBeeAddress16 xbeeAddress = getXbeeAddress(event.getSource().getAddress());
			// create a unicast packet to be delivered to the supplied address, with the pay load
			final TxRequest16 request = new TxRequest16(xbeeAddress, bytes);
			message = RS.rbLabel(KEY.SERVICE_WIRELESS_SENDING, bytes, event.getSource().getAddress());
			log.info(message);
			UGateKeeper.DEFAULT.notifyListeners(event.clone(UGateEvent.Type.WIRELESS_DATA_TX, i, message));
			// send the packet and wait up to 12 seconds for the transmit status reply
			final TxStatusResponse response = (TxStatusResponse) xbee.sendSynchronous(request, 
					timeout <= 0 ? DEFAULT_WAIT_MILISECONDS : timeout);
			if (response.isSuccess()) {
				// packet was delivered successfully
				successCount++;
				message = RS.rbLabel(KEY.SERVICE_WIRELESS_ACK_SUCCESS, bytes, event.getSource().getAddress(), response.getStatus());
				log.info(message);
				UGateKeeper.DEFAULT.notifyListeners(event.clone(UGateEvent.Type.WIRELESS_DATA_TX_ACK, i, message));
			} else {
				// packet was not delivered
				failureCount++;
				message = RS.rbLabel(KEY.SERVICE_WIRELESS_ACK_FAILED, bytes, event.getSource().getAddress(), response.getStatus());
				if (throwRuntimeException) {
					throw new RuntimeException(message);
				} else {
					log.error(message);
				}
				UGateKeeper.DEFAULT.notifyListeners(event.clone(UGateEvent.Type.WIRELESS_DATA_TX_ACK_FAILED, i, message));
			}
		} catch (XBeeTimeoutException e) {
			failureCount++;
			message = RS.rbLabel(KEY.SERVICE_WIRELESS_TX_TIMEOUT, event.getSource().getAddress());
			if (throwRuntimeException) {
				throw new RuntimeException(message, e);
			} else {
				log.error(message, e);
			}
			UGateKeeper.DEFAULT.notifyListeners(event.clone(UGateEvent.Type.WIRELESS_DATA_TX_FAILED, i, message));
		} catch (final Throwable t) {
			failureCount++;
			message = RS.rbLabel(KEY.SERVICE_WIRELESS_TX_FAILED, event.getSource().getAddress());
			if (throwRuntimeException) {
				throw new RuntimeException(message, t);
			} else {
				log.error(message, t);
			}
			UGateKeeper.DEFAULT.notifyListeners(event.clone(UGateEvent.Type.WIRELESS_DATA_TX_FAILED, i, message));
		}
		if (failureCount <= 0) {
			message = RS.rbLabel(KEY.SERVICE_WIRELESS_SUCCESS, successCount);
			log.info(message);
			UGateKeeper.DEFAULT.notifyListeners(event.clone(UGateEvent.Type.WIRELESS_DATA_ALL_TX_SUCCESS, i, message));
		} else {
			message = RS.rbLabel(KEY.SERVICE_WIRELESS_TX_BATCH_FAILED, failureCount);
			log.error(message);
			UGateKeeper.DEFAULT.notifyListeners(event.clone(UGateEvent.Type.WIRELESS_DATA_ALL_TX_FAILED, i, message));
		}
		return failureCount == 0;
	}

	/**
	 * Tests the {@link RemoteNode#getAddress()} of a remote device within the
	 * wireless network
	 * 
	 * @param remoteNode
	 *            the {@linkplain RemoteNode} to test a connection for
	 * @param timeout
	 *            the number of milliseconds that will be used to wait for a
	 *            response before timing out
	 * @return true when the connection was successful
	 */
	public boolean testRemoteConnection(final RemoteNode remoteNode,
			final int timeout) {
		if (!isConnected()) {
			return false;
		}
		try {
			AtCommandResponse response;
			int[] responseValue;
			final XBeeAddress16 remoteAddress = getXbeeAddress(remoteNode
					.getAddress());
			if (remoteAddress != null) {
				final RemoteAtRequest request = new RemoteAtRequest(
						remoteAddress, "MY");
				final RemoteAtResponse remoteResponse = (RemoteAtResponse) xbee
						.sendSynchronous(request,
								timeout <= 0 ? DEFAULT_WAIT_MILISECONDS
										: timeout);
				response = remoteResponse;
			} else {
				response = (AtCommandResponse) xbee
						.sendSynchronous(new AtCommand("MY"));
			}
			if (response.isOk()) {
				responseValue = response.getValue();
				final String address = ByteUtils.toBase16(responseValue);
				log.info(String.format("Successfully got %1$s address %2$s",
						(remoteAddress != null ? "remote" : "local"), address));
				if (address != null && !address.isEmpty()) {
					return true;
				}
			} else {
				throw new XBeeException(
						"Failed to get remote address response. Status is "
								+ response.getStatus());
			}
		} catch (final XBeeTimeoutException e) {
			log.warn("Timed out getting remote XBee address", e);
		} catch (final XBeeException e) {
			log.warn("Error getting remote XBee address", e);
		}
		return false;
	}
	
	/**
	 * Gets a wireless {@linkplain XBeeAddress16} for a wireless
	 * {@linkplain RemoteNode#getAddress()}
	 * 
	 * @param rawAddress
	 *            the {@linkplain RemoteNode#getAddress()} of the wireless node
	 * @return the {@linkplain XBeeAddress16}
	 */
	private XBeeAddress16 getXbeeAddress(final String rawAddress) {
		// final int xbeeRawAddress =
		// Integer.parseInt(preferences.get(wirelessAddressHexKey), 16);
		if (rawAddress.length() > RemoteNodeType.WIRELESS_ADDRESS_MAX_DIGITS) {
			throw new IllegalArgumentException(
					"Wireless address cannot be more than "
							+ RemoteNodeType.WIRELESS_ADDRESS_MAX_DIGITS
							+ " hex digits long");
		}
		final int msb = Integer.parseInt(rawAddress.substring(0, 2), 16);
		final int lsb = Integer.parseInt(rawAddress.substring(2, 4), 16);
		final XBeeAddress16 xbeeAddress = new XBeeAddress16(msb, lsb);
		return xbeeAddress;
	}
	
	/**
	 * Synchronizes the locally hosted settings with the remote wireless node(s)
	 * 
	 * @param timeout
	 *            the number of milliseconds that will be used to wait for a
	 *            response before timing out
	 * @param throwRuntimeException
	 *            true to throw any {@linkplain Throwable} as a
	 *            {@linkplain RuntimeException}
	 * @param remoteNode
	 *            the {@linkplain RemoteNode}(s) to send the settings to
	 * @return true when all node(s) have been updated successfully
	 */
	public boolean sendSettings(final int timeout,
			final boolean throwRuntimeException, final RemoteNode... remoteNode) {
		boolean allSuccess = false;
		if (!isConnected()) {
			return allSuccess;
		}
		try {
			for (final RemoteNode rn : remoteNode) {
				final RxTxRemoteNodeDTO sd = new RxTxRemoteNodeDTO(rn);
				final int[] sendData = sd.getData();
				log.info(String.format("Attempting to send: %s", sd));
				final UGateEvent<RemoteNode, int[]> event = new UGateEvent<>(
						rn, UGateEvent.Type.INITIALIZE, false, null,
						Command.SENSOR_SEND_SETTINGS, null, sendData);
				if (sendData(event, timeout, throwRuntimeException)) {
					log.info(String.format("Settings sent to %1$s",
							rn.getAddress()));
					allSuccess = true;
				}
			}
		} catch (final Throwable t) {
			if (throwRuntimeException && t instanceof RuntimeException) {
				throw (RuntimeException) t;
			} else if (throwRuntimeException) {
				throw new RuntimeException("Error while sending settings", t);
			} else {
				log.error("Error while sending settings", t);
			}
		}
		return allSuccess;
	}
	
	/**
	 * This method is used to get a list of all the available Serial ports
	 * (note: only Serial ports are considered). Any one of the elements
	 * contained in the returned {@link List} can be used as a parameter in
	 * {@link #wirelessBtn(String)} or {@link #wirelessBtn(String, int)} to open
	 * a Serial connection.
	 * 
	 * @return A {@link List} containing {@link String}s showing all available
	 *         Serial ports.
	 */
	public List<String> getSerialPorts() {
		return RS.getSerialPorts();
	}

	/**
	 * @return true if the application needs to be restarted before a wireless
	 *         connection can be established
	 */
	public boolean isRequiresRestart() {
		return requiresRestart;
	}

	/**
	 * @return true when the service is listening to incoming wireless data
	 */
	public boolean isListening() {
		return isListening;
	}
}
