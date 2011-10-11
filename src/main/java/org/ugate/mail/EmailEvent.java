package org.ugate.mail;

import java.util.List;

import javax.mail.Address;

public class EmailEvent {

	public static final String TYPE_EXECUTE_COMMAND = "executeCommand";
	public static final String TYPE_CONNECT = "connect";
	public static final String TYPE_DISCONNECT = "disconnect";
	public static final String TYPE_CLOSED = "closed";
	public final String type;
	public final List<String> commands;
	public final Address[] from;
	
	public EmailEvent(String type, List<String> commands, Address... from) {
		this.type = type;
		this.commands = commands;
		this.from = from;
	}
}
