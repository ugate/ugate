package org.ugate.service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.ugate.ByteUtils;
import org.ugate.Command;
import org.ugate.UGateKeeper;
import org.ugate.UGateKeeperEvent;
import org.ugate.UGateUtil;
import org.ugate.UGateXBeePacketListener;
import org.ugate.resources.RS;
import org.ugate.resources.RS.KEYS;
import org.ugate.service.dao.RemoteNodeDao;
import org.ugate.service.entity.RemoteNodeType;
import org.ugate.service.entity.jpa.Host;
import org.ugate.service.entity.jpa.RemoteNode;
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
 * Wireless service
 */
@Service
@Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
public class WirelessService {

	private final Logger log = UGateUtil.getLogger(WirelessService.class);
	private XBee xbee;
	private UGateXBeePacketListener packetListener;
	
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
	 * @return true if connected
	 */
	public boolean init() {
		if (xbee != null) {
			return true;
		}
		log.info("Initializing local XBee");
		// ensure that the needed RXTX is installed (if not install it)
		final boolean commInitialized = RS.initComm();
		if (commInitialized) {
			xbee = new XBee();
			return true;
		}
		// test the serial ports
		getSerialPorts();
		return commInitialized;
	}
	
	/**
	 * Connects to the wireless network
	 * 
	 * @param host
	 *            the {@linkplain Host} to connect to
	 */
	public boolean connect(final Host host, final RemoteNode remoteNode) {
		if (xbee == null) {
			init();
		} else {
			disconnect();
		}
		log.info("Connecting to local XBee");
		UGateKeeper.DEFAULT.notifyListeners(new UGateKeeperEvent<Void>(UGateKeeper.DEFAULT, UGateKeeperEvent.Type.WIRELESS_HOST_CONNECTING, false));
		try {
			xbee.open(host.getComAddress(), host.getComBaud());
			packetListener = new UGateXBeePacketListener() {
				@Override
				protected <V extends RxData> void handleEvent(final UGateKeeperEvent<V> event) {
					// TODO : update the remote nodes history for incoming data
//					if (event.getType() == UGateKeeperEvent.Type.WIRELESS_DATA_RX_SUCCESS || 
//							event.getType() == UGateKeeperEvent.Type.WIRELESS_DATA_TX_STATUS_RESPONSE_SUCCESS) {
//						for (final Map.Entry<Integer, String> ea : event.getNodeAddresses().entrySet()) {
//							
//						}
//					}
					UGateKeeper.DEFAULT.notifyListeners(event);
				}
				@Override
				public int getCamImgCaptureRetryCnt() {
					return remoteNode.getCamImgCaptureRetryCnt();
				}
			};
			xbee.addPacketListener(packetListener);
			log.info(String.format("Connected to local XBee using port %1$s and baud rate %2$s", 
					host.getComAddress(), host.getComBaud()));
			// XBee connection is blocking so notification can be sent here
			UGateKeeper.DEFAULT.notifyListeners(new UGateKeeperEvent<Void>(UGateKeeper.DEFAULT, UGateKeeperEvent.Type.WIRELESS_HOST_CONNECTED, false));
			return true;
		} catch (final Throwable t) {
			final String errorMsg = String.format("Unable to establish a connection to the local XBee using port %1$s and baud rate %2$s", 
					host.getComAddress(), host.getComBaud());
			log.error(errorMsg, t);
			UGateKeeper.DEFAULT.notifyListeners(new UGateKeeperEvent<Void>(UGateKeeper.DEFAULT, UGateKeeperEvent.Type.WIRELESS_HOST_CONNECT_FAILED, false, errorMsg, t.getMessage()));
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
				UGateKeeper.DEFAULT.notifyListeners(new UGateKeeperEvent<Void>(UGateKeeper.DEFAULT, UGateKeeperEvent.Type.WIRELESS_HOST_DISCONNECTING, false, msg));	
			}
			try {
				xbee.close();
				msg = "Disconnected from XBee";
				log.info(msg);
				if (notify) {
					// XBee close is blocking so notification can be sent here
					UGateKeeper.DEFAULT.notifyListeners(new UGateKeeperEvent<Void>(UGateKeeper.DEFAULT, UGateKeeperEvent.Type.WIRELESS_HOST_DISCONNECTED, false, msg));
				}
			} catch (final Throwable t) {
				msg = "Unable to close wireless connection";
				log.error(msg, t);
				if (notify) {
					UGateKeeper.DEFAULT.notifyListeners(new UGateKeeperEvent<Void>(UGateKeeper.DEFAULT, UGateKeeperEvent.Type.WIRELESS_HOST_DISCONNECT_FAILED, false, 
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
	public List<RemoteNode> getRemoteNodes(final Host host) {
		if (host != null && host.getId() > 0) {
			return remoteNodeDao.getRemoteNodesForHost(host.getId());
		}
		return new ArrayList<>();
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
	 * @param data
	 *            the data string to send
	 * @return true when successful
	 */
	public boolean sendData(final RemoteNode remoteNode, final Command command, final String data) {
		return sendData(remoteNode, command, ByteUtils.stringToIntArray(data));
	}
	
	/**
	 * Sends the data string to the {@linkplain RemoteNode#getAddress()} in
	 * ASCII format array
	 * 
	 * @param remoteNode
	 *            the {@linkplain RemoteNode} to send the data to
	 * @param command
	 *            the executing {@linkplain Command}
	 * @param data
	 *            the data string to send
	 * @return true when successful
	 */
	public boolean sendData(final RemoteNode remoteNode, final Command command, final List<Integer> data) {
		int[] dataInts = new int[data.size()];
		for(int i=0; i<data.size(); i++) {
			dataInts[i] = data.get(i);
		}
		return sendData(remoteNode, command, dataInts);
	}
	
	/**
	 * Sends the data array to the {@linkplain RemoteNode#getAddress()}
	 * 
	 * @param remoteNode
	 *            the {@linkplain RemoteNode} to send the data to
	 * @param command
	 *            the executing {@linkplain Command}
	 * @param data
	 *            the data to send
	 * @return true when successful
	 */
	public boolean sendData(final RemoteNode remoteNode, final Command command, final int... data) {
		final LinkedHashSet<String> addys = new LinkedHashSet<>(1);
		addys.add(remoteNode.getAddress());
		return sendData(new UGateKeeperEvent<int[]>(this, UGateKeeperEvent.Type.INITIALIZE, false, 
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
	private boolean sendData(final UGateKeeperEvent<int[]> event) {
		if (event.getNodeAddresseCount() <= 0) {
			throw new NullPointerException("No wireless node addresses to send data to");
		}
		int i = 0;
		int successCount = 0;
		int failureCount = 0;
		UGateKeeper.DEFAULT.notifyListeners(event.clone(UGateKeeperEvent.Type.WIRELESS_DATA_ALL_TX, i));
		String message;
		for (final String addy : event.getNodeAddresses()) {
			try {
				// bytes header command and status/failure code
				final int[] bytesHeader = new int[] { event.getCommand().id, RxData.Status.NORMAL.ordinal() };
				final int[] bytes = event.getNewValue() != null && event.getNewValue().length > 0 ? 
						UGateUtil.arrayConcatInt(bytesHeader, event.getNewValue()) : bytesHeader;
				final XBeeAddress16 xbeeAddress = getXbeeAddress(addy);
				// create a unicast packet to be delivered to the supplied address, with the pay load
				final TxRequest16 request = new TxRequest16(xbeeAddress, bytes);
				message = RS.rbLabel(KEYS.SERVICE_WIRELESS_SENDING, bytes, addy);
				log.info(message);
				UGateKeeper.DEFAULT.notifyListeners(event.clone(UGateKeeperEvent.Type.WIRELESS_DATA_TX, i, message));
				// send the packet and wait up to 10 seconds for the transmit status reply
				final TxStatusResponse response = (TxStatusResponse) xbee.sendSynchronous(request, 12000);
				if (response.isSuccess()) {
					// packet was delivered successfully
					successCount++;
					message = RS.rbLabel(KEYS.SERVICE_WIRELESS_ACK_SUCCESS, bytes, addy, response.getStatus());
					log.info(message);
					UGateKeeper.DEFAULT.notifyListeners(event.clone(UGateKeeperEvent.Type.WIRELESS_DATA_TX_ACK, i, message));
				} else {
					// packet was not delivered
					failureCount++;
					message = RS.rbLabel(KEYS.SERVICE_WIRELESS_ACK_FAILED, bytes, addy, response.getStatus());
					log.error(message);
					UGateKeeper.DEFAULT.notifyListeners(event.clone(UGateKeeperEvent.Type.WIRELESS_DATA_TX_ACK_FAILED, i, message));
				}
			} catch (XBeeTimeoutException e) {
				failureCount++;
				message = RS.rbLabel(KEYS.SERVICE_WIRELESS_TX_TIMEOUT, addy);
				log.error(message, e);
				UGateKeeper.DEFAULT.notifyListeners(event.clone(UGateKeeperEvent.Type.WIRELESS_DATA_TX_FAILED, i, message));
			} catch (final Throwable t) {
				failureCount++;
				message = RS.rbLabel(KEYS.SERVICE_WIRELESS_TX_FAILED, addy);
				log.error(message, t);
				UGateKeeper.DEFAULT.notifyListeners(event.clone(UGateKeeperEvent.Type.WIRELESS_DATA_TX_FAILED, i, message));
			}
		}
		if (failureCount <= 0) {
			message = RS.rbLabel(KEYS.SERVICE_WIRELESS_SUCCESS, successCount);
			log.info(message);
			UGateKeeper.DEFAULT.notifyListeners(event.clone(UGateKeeperEvent.Type.WIRELESS_DATA_ALL_TX_SUCCESS, i, message));
		} else {
			message = RS.rbLabel(KEYS.SERVICE_WIRELESS_TX_BATCH_FAILED, failureCount);
			log.error(message);
			UGateKeeper.DEFAULT.notifyListeners(event.clone(UGateKeeperEvent.Type.WIRELESS_DATA_ALL_TX_FAILED, i, message));
		}
		return failureCount == 0;
	}

	/**
	 * Tests the address of a remote device within the wireless network
	 * 
	 * @param remoteNode
	 *            the {@linkplain RemoteNode} to test a connection for (
	 *            {@linkplain RemoteSettings#WIRELESS_ADDRESS_START_INDEX} - 1
	 *            when testing the local address)
	 * @return the address of the device (if found and returns its address)
	 */
	public String testRemoteConnection(final RemoteNode remoteNode) {
		String address = null;
		try {
			AtCommandResponse response;
			int[] responseValue;
			final XBeeAddress16 remoteAddress = getXbeeAddress(remoteNode
					.getAddress());
			if (remoteAddress != null) {
				final RemoteAtRequest request = new RemoteAtRequest(
						remoteAddress, "MY");
				final RemoteAtResponse remoteResponse = (RemoteAtResponse) xbee
						.sendSynchronous(request, 10000);
				response = remoteResponse;
			} else {
				response = (AtCommandResponse) xbee
						.sendSynchronous(new AtCommand("MY"));
			}
			if (response.isOk()) {
				responseValue = response.getValue();
				address = ByteUtils.toBase16(responseValue);
				log.info(String.format("Successfully got %1$s address %2$s",
						(remoteAddress != null ? "remote" : "local"), address));
			} else {
				throw new XBeeException(
						"Failed to get remote address response. Status is "
								+ response.getStatus());
			}
		} catch (XBeeTimeoutException e) {
			log.warn("Timed out getting remote XBee address", e);
		} catch (XBeeException e) {
			log.warn("Error getting remote XBee address", e);
		}
		return address;
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
		// final int xbeeRawAddress = Integer.parseInt(preferences.get(wirelessAddressHexKey), 16);
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
	 * Removes a wireless node by address and sets the new wireless node address/index with the next one in the list
	 * 
	 * @param nodeAddress the wireless node address to remove
	 * @return the new wireless node address (the same node address passed if it cannot be removed)
	 */
	public String removeNode(final RemoteNode remoteNode) {
			final String msg = RS.rbLabel(KEYS.WIRELESS_NODE_REMOTE_REMOVE,
					remoteNode.getAddress(), remoteNode.getAddress());
			log.info(msg);
			// send notification(s)
			final LinkedHashSet<String> removed = new LinkedHashSet<>(1);
			removed.put(wakRemove.getKey(), oldAddy);
			UGateKeeper.DEFAULT.notifyListeners(new UGateKeeperEvent<Integer>(UGateKeeper.DEFAULT, UGateKeeperEvent.Type.SETTINGS_REMOTE_NODE_CHANGED_FROM_REMOVE, false,
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
	public void setRemoteNode(final String nodeAddress) {
		if (nodeAddress != null && !nodeAddress.isEmpty()) {
			setCurrentRemoteNodeIndex(getNodeAddressIndex(nodeAddress), nodeAddress, true);
		}
	}
	
	/**
	 * Gets the index for a wireless node address
	 * 
	 * @param nodeAddress the wireless node address
	 * @return the index of the wireless node address (negative one when the address value is not found)
	 */
	public int getNodeAddressIndex(final String nodeAddress) {
		for (final Map.Entry<Integer, RemoteNodeStorage> wak : remoteNodes.entrySet()) {
			if (wak.getValue().settings.hasKey(RemoteSettings.WIRELESS_ADDRESS_NODE.getKey()) && 
					wak.getValue().settings.get(RemoteSettings.WIRELESS_ADDRESS_NODE.getKey()).equals(nodeAddress)) {
				return wak.getKey();
			}
		}
		return -1; 
	}
	
	/**
	 * Synchronizes the locally hosted settings with the remote wireless node(s)
	 * 
	 * @return true when all node(s) have been updated successfully
	 */
	public boolean sendSettings(final RemoteNode... remoteNode) {
		boolean allSuccess = false;
		if (!isConnected()) {
			return allSuccess;
		}
		try {
			final RxTxRemoteSettingsData sd = new RxTxRemoteSettingsData(nodeIndex);
			final int[] sendData = sd.getAllData();
			log.info(String.format("Attempting to send: %s", sd));
			final Map<Integer, String> was = new HashMap<>();
			was.put(0, remoteNode);
			final UGateKeeperEvent<int[]> event = new UGateKeeperEvent<int[]>(this, UGateKeeperEvent.Type.INITIALIZE, false, 
					was, null, Command.SENSOR_SET_SETTINGS, null, sendData);
			if (sendData(event)) {
				log.info(String.format("Settings sent to %1$s node(s)", was.size()));
				allSuccess = true;
			}
		} catch (final Throwable t) {
			log.error("Error while sending settings", t);
		}
		return allSuccess;
	}
	
	/**
	 * Sets the current wireless remote node index
	 * 
	 * @param newNodeIndex
	 *            the remote index of the device node for which the controls
	 *            represent
	 * @param nodeAddy
	 *            the new node address to set (when adding a new node and
	 *            setting it's index)
	 * @param notifyListeners
	 *            true to notify any listeners of the change
	 */
	private void setCurrentRemoteNodeIndex(final int newNodeIndex, final String nodeAddy, final boolean notifyListeners) {
		int newAdjNodeIndex = newNodeIndex < 0 ? wirelessNextRemoteNodeIndex : newNodeIndex;
		if (oldIndex != newAdjNodeIndex) {
			if (notifyListeners) {
				UGateKeeper.DEFAULT.notifyListeners(new UGateKeeperEvent<Integer>(UGateKeeper.DEFAULT, noAdd ? UGateKeeperEvent.Type.SETTINGS_REMOTE_NODE_CHANGED_FROM_SELECT : 
					UGateKeeperEvent.Type.SETTINGS_REMOTE_NODE_CHANGED_FROM_ADD, false,
						getRemoteNodeAddressMap(this.wirelessCurrentRemoteNodeIndex),null, null, oldIndex, 
						this.wirelessCurrentRemoteNodeIndex, msg));
			}
		}
	}
	
	/**
	 * This method is used to get a list of all the available Serial ports (note: only Serial ports are considered). 
	 * Any one of the elements contained in the returned {@link List} can be used as a parameter in 
	 * {@link #connect(String)} or {@link #connect(String, int)} to open a Serial connection.
	 * 
	 * @return A {@link List} containing {@link String}s showing all available Serial ports.
	 */
	public List<String> getSerialPorts() {
		return RS.getSerialPorts();
	}
}
