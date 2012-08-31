package org.ugate;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EventObject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.ugate.service.entity.IModelType;
import org.ugate.service.entity.jpa.Host;
import org.ugate.service.entity.jpa.RemoteNode;
import org.ugate.wireless.data.RxData;
import org.ugate.wireless.data.RxRawData;

/**
 * Gate keeper event
 * 
 * @param <S>
 *            the source type
 * @param <V>
 *            the old/new value type
 */
public class UGateEvent<S, V> extends EventObject implements Cloneable {
	
	private static final long serialVersionUID = 7451746276275099724L;
	private Type type;
	private final IModelType<?> key;
	private final Command command;
	private final V oldValue;
	private final V newValue;
	private boolean fromRemote;
	private List<String> messages;
	private AtomicBoolean consumed = new AtomicBoolean();
	
	/**
	 * Constructor
	 * 
	 * @param source
	 *            the source of the event
	 * @param type
	 *            the {@linkplain Type} type
	 * @param fromRemote
	 *            true when the event is initialized from a remote source
	 * @param messages
	 *            messages (if any)
	 */
	public UGateEvent(final S source, final Type type, final boolean fromRemote, final String... messages) {
		this(source, type, fromRemote, null, null, null, null, messages);
	}
	
	/**
	 * Constructor
	 * 
	 * @param source
	 *            the source of the event
	 * @param type
	 *            the {@linkplain Type} type
	 * @param fromRemote
	 *            true when the event originated from a {@linkplain RemoteNode}
	 * @param key
	 *            the {@linkplain IModelType} (null when event is for ALL nodes)
	 * @param command
	 *            the executing {@linkplain Command} (null when not applicable)
	 * @param oldValue
	 *            the old value (null when event is for ALL nodes)
	 * @param newValue
	 *            the new value (null when event is for ALL nodes)
	 * @param messages
	 *            messages (if any)
	 */
	public UGateEvent(final S source, final Type type, final boolean fromRemote, 
			final IModelType<?> key, final Command command, final V oldValue, final V newValue, final String... messages) {
		this(source, type, fromRemote, key, command, oldValue, newValue, 
				messages != null && messages.length > 0 ? new ArrayList<String>(Arrays.asList(messages)) : null);
	}

	/**
	 * Constructor
	 * 
	 * @param source
	 *            the source of the event
	 * @param type
	 *            the {@linkplain Type} type
	 * @param fromRemote
	 *            true when the event originated from a {@linkplain RemoteNode}
	 * @param key
	 *            the {@linkplain IModelType} (null when event is for ALL nodes)
	 * @param command
	 *            the executing {@linkplain Command} (null when not applicable)
	 * @param oldValue
	 *            the old value (null when event is for ALL nodes)
	 * @param newValue
	 *            the new value (null when event is for ALL nodes)
	 * @param messages
	 *            messages (if any)
	 */
	public UGateEvent(final S source, final Type type, final boolean fromRemote, 
			final IModelType<?> key, final Command command, final V oldValue, final V newValue, final List<String> messages) {
		super(source);
		this.type = type;
		this.key = key;
		this.command = command;
		this.oldValue = oldValue;
		this.newValue = newValue;
		this.fromRemote = fromRemote;
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
	public UGateEvent<S, V> clone(final Type type, final int nodeIndex, final String... messages) {
		UGateEvent<S, V> event = null;
		try {
			event = (UGateEvent<S, V>) super.clone();
			event.type = type;
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
	@SuppressWarnings("unchecked")
	@Override
	public S getSource() {
		return (S) super.getSource();
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
	 * @return the {@linkplain Type}
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @return the key
	 */
	public IModelType<?> getKey() {
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
	 * @return true when the event originated from a {@linkplain RemoteNode}
	 */
	public boolean isFromRemote() {
		return fromRemote;
	}

	/**
	 * @param messages the messages to set
	 */
	public void setMessages(final List<String> messages) {
		this.messages = messages;
	}
	
	/**
	 * @return the messages
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
	    for (final String msg : messageArray) {
	    	msgs.append(msg);
	    	msgs.append('\n');
	    }
	    return msgs.toString();
	}

	/**
	 * @return true when the event is consumed (no other listeners will be
	 *         notified)
	 */
	public boolean isConsumed() {
		return consumed.get();
	}

	/**
	 * @param consumed
	 *            true to set that event as consumed (no other listeners will be
	 *            notified)
	 */
	public void setConsumed(boolean consumed) {
		this.consumed.set(consumed);
	}

	/**
	 * The {@linkplain UGateEvent} types
	 */
	public enum Type {
		/** Event when the event is initializing */
		INITIALIZE, 
		/** Event when a {@linkplain Host} has updated values that have been committed */
		ACTOR_COMMITTED,
		/** Event when a {@linkplain Host} has updated values that have been committed */
		HOST_COMMITTED,
		/** Event when a {@linkplain RemoteNode} has updated values that have been committed, but not yet sent the {@linkplain RemoteNode}'s device */
		WIRELESS_REMOTE_NODE_COMMITTED, 
		/** Event when a {@linkplain RemoteNode} has changed {@linkplain UGateEvent#getOldValue()} will contain the old {@linkplain RemoteNode} and {@linkplain UGateEvent#getNewValue()} will contain the new {@linkplain RemoteNode} */
		WIRELESS_REMOTE_NODE_CHANGED, 
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
		/** Event when wireless data is being sent to ALL THE SPECIFIED {@linkplain RemoteNode}(s) */
		WIRELESS_DATA_ALL_TX,
		/** Event when wireless data is being sent to a SINGLE {@linkplain RemoteNode} */
		WIRELESS_DATA_TX,
		/** Event when wireless data has failed to be sent to a SINGLE {@linkplain RemoteNode} */
		WIRELESS_DATA_TX_FAILED,
		/** Event when wireless data has been sent to a SINGLE {@linkplain RemoteNode} and a sent acknowledgment has been received from the LOCAL node */
		WIRELESS_DATA_TX_ACK,
		/** Event when wireless data has been sent to a SINGLE {@linkplain RemoteNode} and an acknowledgment from the LOCAL node was never received */
		WIRELESS_DATA_TX_ACK_FAILED,
		/** Event when wireless data has been sent to a SINGLE {@linkplain RemoteNode} and the {@linkplain RemoteNode} successfully responded to the sent data */
		WIRELESS_DATA_TX_SUCCESS,
		/** Event when wireless data has been sent and an unrecognized response has been received by a {@linkplain RemoteNode}. {@linkplain UGateEvent#getNewValue()} will contain {@linkplain RxRawData} */
		WIRELESS_DATA_TX_STATUS_RESPONSE_UNRECOGNIZED,
		/** Event when wireless data has been sent and a response has been received by a {@linkplain RemoteNode}. {@linkplain UGateEvent#getNewValue()} will contain {@linkplain RxRawData} */
		WIRELESS_DATA_TX_STATUS_RESPONSE_SUCCESS,
		/** Event when wireless data has been sent and an error response has been received by a {@linkplain RemoteNode}(s). {@linkplain UGateEvent#getNewValue()} will contain {@linkplain RxRawData} */
		WIRELESS_DATA_TX_STATUS_RESPONSE_FAILED,
		/** Event when wireless data has been sent to ALL THE SPECIFIED {@linkplain RemoteNode}(s) and a sent acknowledgment has been received for each {@linkplain RemoteNode} from the LOCAL node */
		WIRELESS_DATA_ALL_TX_ACK,
		/** Event when wireless data has been sent to ALL THE SPECIFIED {@linkplain RemoteNode}(s) and an acknowledgment from ANY number of {@linkplain RemoteNode}(s) were never received by the LOCAL node */
		WIRELESS_DATA_ALL_TX_ACK_FAILED,
		/** Event when wireless data has been sent to {@linkplain RemoteNode}(s) and ALL THE SPECIFIED of the {@linkplain RemoteNode} successfully responded to the sent data */
		WIRELESS_DATA_ALL_TX_SUCCESS,
		/** Event when wireless data has been sent to ALL THE SPECIFIED {@linkplain RemoteNode}(s) and at least one of the {@linkplain RemoteNode}(s) responded with a failure to the sent data */
		WIRELESS_DATA_ALL_TX_FAILED,
		/** Event when wireless data has been received by a {@linkplain RemoteNode} that requires multiple transmissions. {@linkplain UGateEvent#getNewValue()} will contain a partial {@linkplain RxData} */
		WIRELESS_DATA_RX_MULTIPART,
		/** Event when wireless data has been received by a {@linkplain RemoteNode} without any failures. {@linkplain UGateEvent#getNewValue()} will contain {@linkplain RxData} */
		WIRELESS_DATA_RX_SUCCESS,
		/** Event when wireless data has been received by a {@linkplain RemoteNode}, but failures exist- retrying attempt */
		WIRELESS_DATA_RX_FAILED_RETRYING,
		/** Event when wireless data has been received by a {@linkplain RemoteNode}, but failures exist */
		WIRELESS_DATA_RX_FAILED,
		/** Event triggered when executed command(s) from email {@linkplain UGateEvent#getNewValue()} will contain a {@linkplain List} of {@linkplain RemoteNode}s address(es) */
		EMAIL_EXECUTED_COMMANDS,
		/** Event triggered when failure occurs while executing command(s) from email {@linkplain UGateEvent#getNewValue()} will contain a {@linkplain List} of {@linkplain RemoteNode}s address(es) */
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
