package org.ugate.service.entity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;

import org.ugate.service.entity.jpa.Actor;
import org.ugate.service.entity.jpa.Host;
import org.ugate.service.entity.jpa.MailRecipient;
import org.ugate.service.entity.jpa.RemoteNode;
import org.ugate.service.entity.jpa.Role;

/**
 * {@linkplain Actor} types that contain {@linkplain #getKey()}s that point to
 * field paths within an {@linkplain Actor}
 */
public enum ActorType implements IModelType<Actor> {
	ID("id"),
	USERNAME("username"),
	PASSWORD("password"),
	USE_METRIC("host.useMetric"),
	COM_ON_AT_APP_STARTUP("host.comOnAtAppStartup"),
	COM_ADDY("host.comAddress"),
	COM_PORT("host.comPort"),
	COM_BAUD_RATE("host.comBaud"),
	REMOTE_NODES("host.remoteNodes"),
	MAIL_ON_AT_COM_STARTUP("host.mailOnAtComStartup"),
	MAIL_RECIPIENTS("host.mailRecipients"),
	MAIL_SMTP_HOST("host.mailSmtpHost"),
	MAIL_SMTP_PORT("host.mailSmtpPort"),
	MAIL_IMAP_HOST("host.mailImapHost"),
	MAIL_IMAP_PORT("host.mailImapPort"),
	MAIL_INBOX_NAME("host.mailInboxName"),
	MAIL_USERNAME("host.mailUserName"),
	MAIL_PASSWORD("host.mailPassword"),
	MAIL_USE_SSL("host.mailUseSSL"),
	MAIL_USE_TLS("host.mailUseTLS"),
	WEB_ON_AT_COM_STARTUP("host.webOnAtComStartup"),
	WEB_HOST("host.webHost"),
	WEB_PORT("host.webPort"),
	WEB_HOST_LOCAL("host.webHostLocal"),
	WEB_PORT_LOCAL("host.webPortLocal");

	public static final String MAIL_COMMAND_DELIMITER = ";";
	public static final String MAIL_RECIPIENTS_DELIMITER = ";";
	/**
	 * Available XBee baud rates
	 */
	public static final Integer[] HOST_BAUD_RATES = { 1200, 2400, 4800, 9600,
			19200, 38400, 57600, 115200, 230400 };
	private final String key;

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
	public Object getValue(final Actor actor) throws Throwable {
		return IModelType.ValueHelper.getValue(actor, this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setValue(final Actor actor, final Object value)
			throws Throwable {
		IModelType.ValueHelper.setValue(actor, this, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ValueType<Actor, Object> newValueType(final Actor actor)
			throws Throwable {
		return new ValueType<>(this, getValue(actor));
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
	 * Creates a new {@linkplain Actor} with minimal information
	 * 
	 * @param username
	 *            the {@linkplain Actor#getUsername()}
	 * @param password
	 *            the {@linkplain Actor#getPassword()}
	 * @param passPhrase
	 *            the {@link Actor#getPassPhrase()}
	 * @param host
	 *            the {@linkplain Host} that will be associated with the
	 *            {@linkplain Actor}
	 * @return a new {@linkplain Actor}
	 */
	public static Actor newActor(final String username, final String password,
			final String passPhrase, final Host host, final Role... roles) {
		final Actor actor = new Actor();
		actor.setHost(host == null ? newDefaultHost() : host);
		actor.setUsername(username);
		actor.setPassword(password);
		actor.setPassPhrase(passPhrase);
		actor.setRoles(new HashSet<Role>(Arrays.asList(roles)));
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
		host.setComOnAtAppStartup(1);
		host.setComAddress("7777");
		host.setComBaud(19200);
		// host.setComPort("COM1");
		host.setMailSmtpHost("smtp.gmail.com");
		host.setMailSmtpPort(465);
		host.setMailImapHost("imap.gmail.com");
		host.setMailImapPort(993);
		host.setMailUserName("myemail@gmail.com");
		host.setMailInboxName("Inbox");
		host.setMailUseSSL(1);
		host.setMailUseTLS(1);
		host.setWebHost("0.0.0.0");
		host.setWebPort(443);
		host.setWebHostLocal("127.0.0.1");
		host.setWebPortLocal(80);
		host.setMailRecipients(mailRecipients);
		host.setRemoteNodes(remoteNodes);
		return host;
	}
}
