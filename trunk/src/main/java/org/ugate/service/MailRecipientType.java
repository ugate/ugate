package org.ugate.service;

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
