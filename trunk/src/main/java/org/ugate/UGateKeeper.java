package org.ugate;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;

import org.slf4j.Logger;

/**
 * Central gate keeper hub
 */
public enum UGateKeeper {
	
	DEFAULT;
	
	private final Logger log = UGateUtil.getLogger(UGateKeeper.class);
	private final List<IGateKeeperListener> listeners = new ArrayList<IGateKeeperListener>();
	
	/**
	 * Constructor
	 */
	private UGateKeeper() {
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
	public <V> void notifyListeners(final UGateKeeperEvent<V> event) {
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
}
