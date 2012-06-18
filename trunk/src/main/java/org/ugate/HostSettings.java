package org.ugate;

import org.ugate.service.IModelType;

/**
 * Settings related to the global host computer that will span all nodes across the device network
 */
public enum HostSettings implements IModelType {
	USE_METRIC("metric.on"),
	WIRELESS_ADDRESS_HOST("wireless.address.host"),
	WIRELESS_COM_PORT("wireless.com.port"),
	WIRELESS_BAUD_RATE("wireless.baud.rate"),
	MAIL_INBOX_NAME("mail.inbox.name"),
	MAIL_RECIPIENTS("mail.recipients"),
	MAIL_SMTP_HOST("mail.smtp.host"),
	MAIL_SMTP_PORT("mail.smtp.port"),
	MAIL_IMAP_HOST("mail.imap.host"),
	MAIL_IMAP_PORT("mail.imap.port"),
	MAIL_USERNAME("mail.username"),
	MAIL_PASSWORD("mail.password");

	public static final String MAIL_COMMAND_DELIMITER = ";";
	public static final String MAIL_RECIPIENTS_DELIMITER = ";";
	public final String key;
	
	/**
	 * Constructor
	 * 
	 * @param key the key
	 */
	private HostSettings(final String key) {
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
}
