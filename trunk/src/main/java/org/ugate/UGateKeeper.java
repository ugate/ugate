package org.ugate;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javafx.application.Platform;

import org.slf4j.Logger;

/**
 * Central gate keeper hub
 */
public enum UGateKeeper {
	
	DEFAULT;
	
	private final Logger log = UGateUtil.getLogger(UGateKeeper.class);
	private final List<IGateKeeperListener> listeners = new CopyOnWriteArrayList<IGateKeeperListener>();
	//private final int numOfProcessors;
	//private final ForkJoinPool pool;
	
	/**
	 * Constructor
	 */
	private UGateKeeper() {
		//numOfProcessors = Runtime.getRuntime().availableProcessors();
		//pool = new ForkJoinPool(numOfProcessors);
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
	 * Notifies the listeners of preference/settings and connection
	 * interactions.
	 * 
	 * @param <S>
	 *            the source of the event
	 * @param <V>
	 *            the type of event value
	 * @param events
	 *            the event(s)
	 */
	public <S, V> void notifyListeners(final UGateKeeperEvent<S, V> event) {
		if (Platform.isFxApplicationThread()) {
			notifyListenersExec(event);
		} else {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					notifyListenersExec(event);
				}
			});
		}
	}

	/**
	 * Notifies the listeners of preference/settings and connection
	 * interactions. TODO : remove reference to GUI implementation
	 * 
	 * @param <S>
	 *            the source of the event
	 * @param <V>
	 *            the type of event value
	 * @param events
	 *            the event(s)
	 */
	private <S, V> void notifyListenersExec(final UGateKeeperEvent<S, V> event) {
		try {
			for (final IGateKeeperListener pl : listeners) {
				try {
					if (event.isConsumed()) {
						return;
					}
					pl.handle(event);
				} catch (final Throwable t) {
					log.warn("Unable to handle listener: " + pl, t);
				}
			}
		} catch (final Throwable t) {
			log.warn("Unable to cycle listeners: ", t);
		}
	}
}
