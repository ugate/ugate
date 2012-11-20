package org.ugate.mail;

import java.util.List;
import java.util.Set;

import javax.mail.Address;

import org.ugate.service.entity.Command;

/**
 * Email event
 */
public class EmailEvent {

	public final Type type;
	public final List<Command> commands;
	public final Address[] from;
	public final Set<String> toAddresses;
	
	/**
	 * Email event constructor
	 * 
	 * @param type the {@linkplain Type} of email event
	 * @param commands the list of {@linkplain Command}s to execute
	 * @param from the list of {@linkplain Address}es that initiated the event
	 * @param toAddresses the remote node address(es) where the command(s) will be sent to
	 */
	public EmailEvent(final Type type, final List<Command> commands, final Address[] from, final Set<String> toAddresses) {
		this.type = type;
		this.commands = commands;
		this.from = from;
		this.toAddresses = toAddresses;
	}
	
	/**
	 * @return the semicolon delimited from addresses
	 */
	public String getFromAddressString() {
		final StringBuilder sb = new StringBuilder();
		if (from != null) {
			int i = 0;
			for (final Address addy : from) {
				sb.append(addy.toString());
				if (i < (from.length - 1)) {
					sb.append(';');
				}
				i++;
			}
		}
		return sb.toString();
	}
	
	/**
	 * Email event types
	 */
	public enum Type {
		/** Event triggered when authentication failed */
		AUTH_FAILED,
		/** Event triggered when executing a command from email */
		EXECUTE_COMMAND,
		/** Event triggered when connecting to email */
		CONNECT,
		/** Event triggered when disconnecting from email */
		DISCONNECT,
		/** Event triggered when the connection to email has been closed */
		CLOSED,
		/** Event triggered when the email folder that is being observed closed */
		FOLDER_CLOSED,
		/** Event triggered when the email agent unexpectedly threw an exception */
		GENERAL_EXCEPTION;
	}
}
