package org.ugate.service;

import org.ugate.service.entity.IModelType;
import org.ugate.service.entity.jpa.MailRecipient;

public enum MailRecipientType implements IModelType<MailRecipient> {
	EMAIL("email");
	
	public final String key;

	/**
	 * Constructor
	 * 
	 * @param key
	 *            the key
	 */
	private MailRecipientType(final String key) {
		this.key = key;
	}

	/**
	 * Gets a {@linkplain MailRecipient} value for a {@linkplain MailRecipientType}
	 * 
	 * @param mailRecipient
	 *            the {@linkplain MailRecipient} to get the value from
	 * @return the extracted {@linkplain MailRecipient} value
	 * @throws Throwable
	 *             any errors during extraction
	 */
	public Object getValue(final MailRecipient mailRecipient) throws Throwable {
		return IModelType.ValueHelper.getValue(mailRecipient, this);
	}

	/**
	 * Sets a {@linkplain MailRecipient} value for a {@linkplain MailRecipientType}.
	 * 
	 * @param mailRecipient
	 *            the {@linkplain MailRecipient} to get the value from
	 * @param value
	 *            the value to set
	 * @return the extracted {@linkplain MailRecipient} value
	 * @throws Throwable
	 *             any errors during extraction
	 */
	public void setValue(final MailRecipient mailRecipient, final Object value)
			throws Throwable {
		IModelType.ValueHelper.setValue(mailRecipient, this, value);
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
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return String.format("%1$s (key = %2$s)", super.toString(), key);
	}
}
