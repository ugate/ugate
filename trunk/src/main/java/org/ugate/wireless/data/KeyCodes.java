package org.ugate.wireless.data;

import java.lang.reflect.Field;

/**
 * Key codes used for universal remote access to device nodes
 */
public class KeyCodes extends RxData {
	
	private int keyCode1;
	private int keyCode2;
	private int keyCode3;

	/**
	 * Constructor
	 * 
	 * @param nodeIndex the remote node index
	 * @param status the {@linkplain Status}
	 * @param signalStrength the signal strength
	 * @param keyCode1 the first key code
	 * @param keyCode2 the second key code
	 * @param keyCode3 the third key code
	 */
	public KeyCodes(final Integer nodeIndex, final Status status, final int signalStrength, final int keyCode1, 
			final int keyCode2, final int keyCode3) {
		super(nodeIndex, status, signalStrength);
		this.keyCode1 = keyCode1;
		this.keyCode2 = keyCode2;
		this.keyCode3 = keyCode3;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final Field[] fields = getClass().getDeclaredFields();
		final StringBuffer sb = new StringBuffer(RxTxRemoteSettingsData.class.getSimpleName() + " [");
		for (final Field field : fields) {
			try {
				sb.append(field.getName());
				sb.append('=');
				sb.append(field.getInt(this));
				sb.append(", ");
			} catch (final Throwable t) {
				t.printStackTrace();
			}
		}
		sb.append(']');
		return sb.toString();
	}

	/**
	 * @return the keyCode1
	 */
	public int getKeyCode1() {
		return keyCode1;
	}

	/**
	 * @param keyCode1 the keyCode1 to set
	 */
	public void setKeyCode1(int keyCode1) {
		this.keyCode1 = keyCode1;
	}

	/**
	 * @return the keyCode2
	 */
	public int getKeyCode2() {
		return keyCode2;
	}

	/**
	 * @param keyCode2 the keyCode2 to set
	 */
	public void setKeyCode2(int keyCode2) {
		this.keyCode2 = keyCode2;
	}

	/**
	 * @return the keyCode3
	 */
	public int getKeyCode3() {
		return keyCode3;
	}

	/**
	 * @param keyCode3 the keyCode3 to set
	 */
	public void setKeyCode3(int keyCode3) {
		this.keyCode3 = keyCode3;
	}

}
