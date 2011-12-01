package org.ugate;

import java.util.EventListener;

/**
 * Gate keeper listener
 */
public interface IGateKeeperListener extends EventListener {
	
	/**
	 * Handles the gate keeper event
	 * 
	 * @param type the {@linkplain IGateKeeperListener.Event} type
	 * @param key the key (null when all are changing)
	 * @param oldValue the old value (null when all are changing)
	 * @param newValue the new value (null when all are changing)
	 */
	public abstract void handle(final Event type, final String key, final String oldValue, final String newValue);
	
	/**
	 * The change types
	 */
	public enum Event {
		/** Event when preferences are being set on the host */
		PREFERENCES_SET, 
		/** Event when sending preferences to a remote node */
		SETTINGS_SENDING, 
		/** Event when preferences have successfully sent to a remote node */
		SETTINGS_SEND_SUCCESS, 
		/** Event when preferences have failed to be sent to a remote node */
		SETTINGS_SEND_FAILED;
	}
}
