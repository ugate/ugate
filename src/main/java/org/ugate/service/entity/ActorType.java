package org.ugate.service.entity;

import java.util.LinkedHashSet;

import org.ugate.service.entity.jpa.Actor;
import org.ugate.service.entity.jpa.Host;
import org.ugate.service.entity.jpa.MailRecipient;
import org.ugate.service.entity.jpa.RemoteNode;

/**
 * {@linkplain Actor} types that contain {@linkplain #getKey()}s that point to
 * field paths within an {@linkplain Actor}
 */
public enum ActorType implements IModelType<Actor> {
	USERNAME("login"),
	PASSWORD("pwd"),
	USE_METRIC("host.useMetric"),
	HOST_COM_ADDY("host.comAddress"),
	HOST_COM_PORT("host.comPort"),
	HOST_BAUD_RATE("host.comBaud"),
	MAIL_RECIPIENTS("host.mailRecipients"),
	MAIL_SMTP_HOST("host.mailSmtpHost"),
	MAIL_SMTP_PORT("host.mailSmtpPort"),
	MAIL_IMAP_HOST("host.mailImapHost"),
	MAIL_IMAP_PORT("host.mailImapPort"),
	MAIL_INBOX_NAME("host.mailInboxName"),
	MAIL_USERNAME("host.mailUserName"),
	MAIL_PASSWORD("host.mailPassword"),
	WEB_HOST("host.webHost"),
	WEB_PORT("host.webPort");
	
	public static final String MAIL_COMMAND_DELIMITER = ";";
	public static final String MAIL_RECIPIENTS_DELIMITER = ";";
	/**
	 * Available XBee baud rates
	 */
	public static final Integer[] HOST_BAUD_RATES = {1200, 2400, 4800, 9600, 19200, 
		38400, 57600, 115200, 230400};
	public final String key;
	
	/**
	 * Constructor
	 * 
	 * @param key
	 *            the key
	 */
	private ActorType(final String key) {
		this.key = key;
	}

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return String.format("%1$s (key = %2$s)", super.toString(), key);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getKey() {
		return this.key;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canRemote() {
		return false;
	}

	/**
	 * @return a new default {@linkplain Actor}
	 */
	public static Actor newDefaultActor() {
		final Actor actor = new Actor();
		actor.setHost(newDefaultHost());
		return actor;
	}

	/**
	 * @return a new default {@linkplain Host}
	 */
	public static Host newDefaultHost() {
		final Host host = new Host();
		final LinkedHashSet<RemoteNode> remoteNodes = new LinkedHashSet<>();
		remoteNodes.add(RemoteNodeType.newDefaultRemoteNode(host));
		final LinkedHashSet<MailRecipient> mailRecipients = new LinkedHashSet<>();
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
		host.setWebHost("localhost");
		host.setWebPort(443);
		host.setMailRecipients(mailRecipients);
		host.setRemoteNodes(remoteNodes);
		return host;
	}
}
