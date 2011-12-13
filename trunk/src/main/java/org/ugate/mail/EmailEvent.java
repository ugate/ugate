package org.ugate.mail;

import java.util.List;

import javax.mail.Address;

import org.ugate.Command;

public class EmailEvent {

	public static final String TYPE_EXECUTE_COMMAND = "executeCommand";
	public static final String TYPE_CONNECT = "connect";
	public static final String TYPE_DISCONNECT = "disconnect";
	public static final String TYPE_CLOSED = "closed";
	public final String type;
	public final List<Command> commands;
	public final Address[] from;
	public final int[] toNodes;
	
	public EmailEvent(String type, List<Command> commands, Address[] from, int... toNodes) {
		this.type = type;
		this.commands = commands;
		this.from = from;
		this.toNodes = toNodes;
	}
}
