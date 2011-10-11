package org.ugate.mail;

import java.util.EventListener;

public interface IEmailListener extends EventListener {

	/**
	 * Called when incoming mail events occur
	 * 
	 * @param event the mail command event
	 */
	void handle(EmailEvent event);
}
