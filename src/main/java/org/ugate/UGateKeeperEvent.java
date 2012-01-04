package org.ugate;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EventObject;
import java.util.List;
import java.util.Map;

import org.ugate.wireless.data.RxData;

/**
 * Gate keeper event
 * 
 * @param <V> the old/new value type
 */
public class UGateKeeperEvent<V> extends EventObject implements Cloneable {
	
	private static final long serialVersionUID = 7451746276275099724L;
	private Type type;
	private final Map<Integer, String> nodeAddresses;
	private final ISettings key;
	private final Command command;
	private final V oldValue;
	private final V newValue;
	private int nodeIndex;
	private List<String> messages;
	
	/**
	 * Creates a gate keeper event
	 * 
	 * @param source the {@linkplain UGateKeeper} the event is for
	 * @param type the {@linkplain Type} type
	 * @param messages messages (if any)
	 */
	UGateKeeperEvent(final Object source, final Type type, final String... messages) {
		this(source, type, null, 0, null, null, null, null, messages);
	}
	
	/**
	 * Creates a gate keeper event
	 * 
	 * @param source the {@linkplain UGateKeeper} the event is for
	 * @param type the {@linkplain Type} type
	 * @param nodeAddress the remote node address the event is for (null when event is for all nodes)
	 * @param nodeIndex the index of the node (starting at index zero up to the length of the {@linkplain #nodeAddresses})
	 * @param key the {@linkplain ISettings} (null when event is for all nodes)
	 * @param command the executing {@linkplain Command} (null when not applicable)
	 * @param oldValue the old value (null when event is for all nodes)
	 * @param newValue the new value (null when event is for all nodes)
	 * @param messages messages (if any)
	 */
	UGateKeeperEvent(final Object source, final Type type, final Map<Integer, String> nodeAddresses, final int nodeIndex, 
			final ISettings key, final Command command, final V oldValue, final V newValue, final String... messages) {
		this(source, type, nodeAddresses, nodeIndex, key, command, oldValue, newValue, 
				messages != null && messages.length > 0 ? new ArrayList<String>(Arrays.asList(messages)) : null);
	}

	/**
	 * Creates a gate keeper event
	 * 
	 * @param source the {@linkplain UGateKeeper} the event is for
	 * @param type the {@linkplain Type} type
	 * @param nodeAddress the remote node address the event is for (null when event is for all nodes)
	 * @param nodeIndex the index of the node (starting at index zero up to the length of the {@linkplain #nodeAddresses})
	 * @param key the {@linkplain ISettings} (null when event is for all nodes)
	 * @param command the executing {@linkplain Command} (null when not applicable)
	 * @param oldValue the old value (null when event is for all nodes)
	 * @param newValue the new value (null when event is for all nodes)
	 * @param messages messages (if any)
	 */
	UGateKeeperEvent(final Object source, final Type type, final Map<Integer, String> nodeAddresses, final int nodeIndex, 
			final ISettings key, final Command command, final V oldValue, final V newValue, final List<String> messages) {
		super(source);
		this.type = type;
		this.nodeAddresses = nodeAddresses;
		this.nodeIndex = this.nodeAddresses == null ? RemoteSettings.WIRELESS_ADDRESS_START_INDEX : 
			!this.nodeAddresses.containsKey(nodeIndex) ? 
				this.nodeAddresses.keySet().iterator().next() : nodeIndex;
		this.key = key;
		this.command = command;
		this.oldValue = oldValue;
		this.newValue = newValue;
		this.messages = messages;
	}
	
	/**
	 * Clones the event changing the type and adding any message(s) (if applicable)
	 * 
	 * @param type the type of the event
	 * @param messages any messages to add
	 * @return the event clone
	 */
	@SuppressWarnings("unchecked")
	UGateKeeperEvent<V> clone(final Type type, final int nodeIndex, final String... messages) {
		UGateKeeperEvent<V> event = null;
		try {
			event = (UGateKeeperEvent<V>) super.clone();
			event.type = type;
			event.nodeIndex = nodeIndex;
			if (messages != null) {
				for (final String error : messages) {
					event.addMessage(error);
				}
			}
		} catch (final CloneNotSupportedException e) {
			// should never happen
			e.printStackTrace();
		}
		return event;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final Field[] fields = getClass().getDeclaredFields();
		final StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append(" [");
		Map<?, ?> map;
		Collection<?> col;
		for (final Field field : fields) {
			try {
				sb.append(field.getName());
				sb.append('=');
				if (field.getDeclaringClass().isAssignableFrom(Map.class)) {
					map = (Map<?, ?>) field.get(this);
					sb.append('{');
					if (map != null) {
						for (final Map.Entry<?, ?> me : map.entrySet()) {
							sb.append(" key: ");
							sb.append(me.getKey());
							sb.append(" value: ");
							sb.append(me.getValue());
						}
					}
					sb.append('}');
				} else if (field.getDeclaringClass().isAssignableFrom(Collection.class)) {
					col = (Collection<?>) field.get(this);
					sb.append('{');
					if (col != null) {
						for (final Object c : col) {
							sb.append(c);
							sb.append(' ');
						}
					}
					sb.append('}');
				} else {
					sb.append(field.get(this));
				}
				sb.append(", ");
			} catch (final Throwable t) {
				t.printStackTrace();
			}
		}
		sb.append(']');
		return sb.toString();
	}
	
	/**
	 * @return the total number of node addresses
	 */
	public int getTotalNodeAddresses() {
		return nodeAddresses == null ? 0 : nodeAddresses.size();
	}
	
	/**
	 * @return the node address for which the event is for
	 */
	public String getEventNodeAddress() {
		return nodeAddresses != null ? nodeAddresses.get(nodeIndex) : null;
	}
	
	/**
	 * Gets a node address at the specified address
	 * 
	 * @param nodeIndex the node address index to get an address for
	 * @return the node address (or null when no address exists at the specified index)
	 */
	public String getNodeAddress(final int nodeIndex) {
		return nodeAddresses != null && nodeAddresses.containsKey(nodeIndex) ? 
				nodeAddresses.get(nodeIndex) : null;
	}

	/**
	 * @return the nodeAddresses
	 */
	public Collection<String> getNodeAddresses() {
		return nodeAddresses != null ? nodeAddresses.values() : null;
	}
	
	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @return the key
	 */
	public ISettings getKey() {
		return key;
	}

	/**
	 * @return the command
	 */
	public Command getCommand() {
		return command;
	}

	/**
	 * @return the oldValue
	 */
	public V getOldValue() {
		return oldValue;
	}

	/**
	 * @return the newValue
	 */
	public V getNewValue() {
		return newValue;
	}

	/**
	 * @return the nodeIndex
	 */
	public int getNodeIndex() {
		return nodeIndex;
	}

	/**
	 * @param messages the messages to set
	 */
	public void setMessages(final List<String> messages) {
		this.messages = messages;
	}
	
	/**
	 * @return the errors
	 */
	public List<String> getMessages() {
		if (messages == null) {
			messages = new ArrayList<String>();
		}
		return messages;
	}
	
	/**
	 * Adds a message
	 * 
	 * @param message the message to add
	 */
	public void addMessage(final String message) {
		final List<String> msgs = getMessages();
		msgs.add(message);
	}
	
	/**
	 * @return a next-line delimited message string
	 */
	public String getMessageString() {
		final List<String> messageArray = getMessages();
		if (messageArray.isEmpty()) {
			return "";
		}
	    final StringBuilder msgs = new StringBuilder();
	    int i = 0;
	    for (final String msg : messageArray) {
	    	msgs.append(msg);
	    	msgs.append('\n');
	    	i++;
	    }
	    return msgs.toString();
	}

	/**
	 * The event types
	 */
	public enum Type {
		/** Event when the event is initializing */
		INITIALIZE, 
		/** Event when preferences/settings are being set on the host */
		SETTINGS_SAVE_LOCAL, 
		/** Event when the remote node for the preferences/settings has changed */
		SETTINGS_REMOTE_NODE_CHANGED, 
		/** Event when connecting to the local host wireless device */
		WIRELESS_HOST_CONNECTING,
		/** Event when a connection has been established with the local host wireless device */
		WIRELESS_HOST_CONNECTED,
		/** Event when a connection attempt has been made, but failed to connect */
		WIRELESS_HOST_CONNECT_FAILED,
		/** Event when disconnecting from the local host wireless device */
		WIRELESS_HOST_DISCONNECTING,
		/** Event when a connection with the local host wireless device has been terminated */
		WIRELESS_HOST_DISCONNECTED,
		/** Event when a disconnection attempt has been made, but failed to be completed */
		WIRELESS_HOST_DISCONNECT_FAILED,
		/** Event when wireless data is being sent to ALL THE SPECIFIED remote node(s) */
		WIRELESS_DATA_ALL_TX,
		/** Event when wireless data is being sent to a SINGLE remote node */
		WIRELESS_DATA_TX,
		/** Event when wireless data has failed to be sent to a SINGLE remote node */
		WIRELESS_DATA_TX_FAILED,
		/** Event when wireless data has been sent to a SINGLE remote node and a sent acknowledgment has been received from the LOCAL node */
		WIRELESS_DATA_TX_ACK,
		/** Event when wireless data has been sent to a SINGLE remote node and an acknowledgment from the LOCAL node was never received */
		WIRELESS_DATA_TX_ACK_FAILED,
		/** Event when wireless data has been sent to a SINGLE remote node and the REMOTE node successfully responded to the sent data */
		WIRELESS_DATA_TX_SUCCESS,
		/** Event when wireless data has been sent and an unrecognized response has been received by a remote node. {@linkplain UGateKeeperEvent#getNewValue()} will contain {@linkplain RxRawData} */
		WIRELESS_DATA_TX_STATUS_RESPONSE_UNRECOGNIZED,
		/** Event when wireless data has been sent and a response has been received by a remote node. {@linkplain UGateKeeperEvent#getNewValue()} will contain {@linkplain RxRawData} */
		WIRELESS_DATA_TX_STATUS_RESPONSE_SUCCESS,
		/** Event when wireless data has been sent and an error response has been received by a remote node(s). {@linkplain UGateKeeperEvent#getNewValue()} will contain {@linkplain RxRawData} */
		WIRELESS_DATA_TX_STATUS_RESPONSE_FAILED,
		/** Event when wireless data has been sent to ALL THE SPECIFIED remote node(s) and a sent acknowledgment has been received for each REMOTE node from the LOCAL node */
		WIRELESS_DATA_ALL_TX_ACK,
		/** Event when wireless data has been sent to ALL THE SPECIFIED remote node(s) and an acknowledgment from ANY number of REMOTE node(s) were never received by the LOCAL node */
		WIRELESS_DATA_ALL_TX_ACK_FAILED,
		/** Event when wireless data has been sent to remote node(s) and ALL THE SPECIFIED of the REMOTE node successfully responded to the sent data */
		WIRELESS_DATA_ALL_TX_SUCCESS,
		/** Event when wireless data has been sent to ALL THE SPECIFIED remote node(s) and at least one of the REMOTE node(s) responded with a failure to the sent data */
		WIRELESS_DATA_ALL_TX_FAILED,
		/** Event when wireless data has been received by a remote node that requires multiple transmissions. {@linkplain UGateKeeperEvent#getNewValue()} will contain a partial {@linkplain RxData} */
		WIRELESS_DATA_RX_MULTIPART,
		/** Event when wireless data has been received by a remote node without any failures. {@linkplain UGateKeeperEvent#getNewValue()} will contain {@linkplain RxData} */
		WIRELESS_DATA_RX_SUCCESS,
		/** Event when wireless data has been received by a remote node, but failures exist- retrying attempt */
		WIRELESS_DATA_RX_FAILED_RETRYING,
		/** Event when wireless data has been received by a remote node, but failures exist */
		WIRELESS_DATA_RX_FAILED,
		/** Event triggered when executed command(s) from email {@linkplain UGateKeeperEvent#getNewValue()} will contain a {@linkplain List} of remote nodes address(es) */
		EMAIL_EXECUTED_COMMANDS,
		/** Event triggered when failure occurs while executing command(s) from email {@linkplain UGateKeeperEvent#getNewValue()} will contain a {@linkplain List} of remote nodes address(es) */
		EMAIL_EXECUTE_COMMANDS_FAILED,
		/** Event triggered when connecting to email */
		EMAIL_CONNECTING,
		/** Event triggered when connected to email */
		EMAIL_CONNECTED,
		/** Event triggered when connection to email failed */
		EMAIL_CONNECT_FAILED,
		/** Event triggered when disconnecting from email */
		EMAIL_DISCONNECTING,
		/** Event triggered when disconnected from email */
		EMAIL_DISCONNECTED,
		/** Event triggered when the connection to email has been closed */
		EMAIL_CLOSED;
	}

}
