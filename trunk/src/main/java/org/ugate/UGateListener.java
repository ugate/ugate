package org.ugate;

import java.util.EventListener;

/**
 * Gate keeper listener
 */
public interface UGateListener extends EventListener {
	
	/**
	 * Handles any {@linkplain UGateEvent}s
	 *
	 * @param event the {@linkplain UGateEvent} to handle
	 */
	public abstract void handle(final UGateEvent<?, ?> event);
}
