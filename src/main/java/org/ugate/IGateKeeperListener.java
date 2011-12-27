package org.ugate;

import java.util.EventListener;

/**
 * Gate keeper listener
 */
public interface IGateKeeperListener extends EventListener {
	
	/**
	 * Handles the gate keeper event
	 *
	 * @param event the event to handle
	 */
	public abstract void handle(final UGateKeeperEvent<?> event);
}
