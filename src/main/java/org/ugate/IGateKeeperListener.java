package org.ugate;

import java.util.EventListener;

/**
 * Gate keeper listener
 */
public interface IGateKeeperListener extends EventListener {
	
	/**
	 * Handles any {@linkplain UGateKeeperEvent}s
	 *
	 * @param event the {@linkplain UGateKeeperEvent} to handle
	 */
	public abstract void handle(final UGateKeeperEvent<?, ?> event);
}
