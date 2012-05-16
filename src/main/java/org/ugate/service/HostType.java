package org.ugate.service;

import org.ugate.service.entity.jpa.Host;

/**
 * {@linkplain Host} types
 */
public enum HostType {
	DEFAULT(new Host());
	
	private final Host host;
	
	/**
	 * Constructor
	 * 
	 * @param host
	 */
	private HostType(final Host host) {
		this.host = host;
	}
	
	/**
	 * @return a new or existing {@linkplain Host} (depending upon the type)
	 */
	public Host getHost() {
		if (host != null) {
			host.setComAddress("7777");
			host.setComBaud(19200);
			host.setComPort("COM1");
			host.setMailSmtpHost("smtp.gmail.com");
			host.setMailSmtpPort(465);
			host.setMailImapHost("imap.gmail.com");
			host.setMailImapPort(993);
			host.setMailUserName("myemail@gmail.com");
			// TODO : host.setMailRecipients("user1@example.com", "user2@example.com", "user3@example.com");
			return host;
		}
		return new Host();
	}
}
