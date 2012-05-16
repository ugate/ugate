package org.ugate.service;

import java.util.HashSet;
import java.util.Set;

import org.ugate.service.entity.jpa.Host;
import org.ugate.service.entity.jpa.MailRecipient;

/**
 * {@linkplain Host} types
 */
public enum HostType {
	DEFAULT;
	
	/**
	 * Constructor
	 */
	private HostType() {
	}
	
	/**
	 * @return a new {@linkplain Host} (depending upon the type some fields may
	 *         be populated)
	 */
	public Host newHost() {
		final Host host = new Host();
		if (this == DEFAULT) {
			final Set<MailRecipient> mailRecipients = new HashSet<MailRecipient>();
			final MailRecipient mr1 = new MailRecipient();
			mr1.setEmail("user1@example.com");
			mailRecipients.add(mr1);
			final MailRecipient mr2 = new MailRecipient();
			mr2.setEmail("user2@example.com");
			mailRecipients.add(mr2);
			final MailRecipient mr3 = new MailRecipient();
			mr3.setEmail("user3@example.com");
			mailRecipients.add(mr3);
			host.setComAddress("7777");
			host.setComBaud(19200);
			host.setComPort("COM1");
			host.setMailSmtpHost("smtp.gmail.com");
			host.setMailSmtpPort(465);
			host.setMailImapHost("imap.gmail.com");
			host.setMailImapPort(993);
			host.setMailUserName("myemail@gmail.com");
			host.setMailInboxName("Inbox");
			host.setMailRecipients(mailRecipients);
		}
		return host;
	}
}
