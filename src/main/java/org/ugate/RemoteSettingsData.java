package org.ugate;

import java.io.Serializable;

/**
 * Data instances of {@linkplain RemoteSettings}
 */
public class RemoteSettingsData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5670857089945678850L;
	
	private final RemoteSettings settings;
	private final int value;
	
	/**
	 * Constructor
	 * 
	 * @param settings the {@linkplain RemoteSettingsData}
	 * @param value the data value
	 */
	public RemoteSettingsData(final RemoteSettings settings, final int value) {
		super();
		this.settings = settings;
		this.value = value;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return String.format("%1$s (%2$s, data = %3$s)", super.toString(), settings, value);
	}

	/**
	 * @return the settings
	 */
	public RemoteSettings getSettings() {
		return settings;
	}

	/**
	 * @return the data value
	 */
	public int getValue() {
		return value;
	}
}
