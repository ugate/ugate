package org.ugate;

/**
 * Settings interface
 */
public interface ISettings {

	/**
	 * @return The preference key used to extract the settings value
	 */
	public String getKey();
	
	/**
	 * @return The flag that indicates if the settings is transferable to remote nodes
	 */
	public boolean canRemote();
}
