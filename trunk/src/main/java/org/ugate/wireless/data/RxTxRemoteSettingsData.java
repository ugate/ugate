package org.ugate.wireless.data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.ugate.RemoteSettings;
import org.ugate.RemoteSettingsData;
import org.ugate.service.ServiceProvider;


/**
 * Settings data
 */
public class RxTxRemoteSettingsData extends RxData {
	
	private List<RemoteSettingsData> data = new ArrayList<RemoteSettingsData>(RemoteSettings.canRemoteCount());
	
	/**
	 * Constructs settings data with the current settings data values set {@linkplain #setRemoteSettingsDataFromHostValues()}
	 * 
	 * @param nodeIndex the remote node index
	 * @param nodeIndex the index of the remote node
	 */
	public RxTxRemoteSettingsData(final int nodeIndex) {
		super(nodeIndex,Status.NORMAL, 0);
		setRemoteSettingsDataFromHostValues();
	}


	/**
	 * Full constructor
	 * 
	 * @param nodeIndex the index of the remote wireless node
	 * @param status the {@linkplain Status} of the wireless transfer
	 * @param signalStrength the signal strength of the remote wireless connection at the time of the transfer
	 * @param remoteSettingsValues the {@linkplain RemoteSettings} values in the order they exist in the {@linkplain RemoteSettings}
	 */
	public RxTxRemoteSettingsData(final Integer nodeIndex, final Status status, final Integer signalStrength, final int... remoteSettingsValues) {
		super(nodeIndex, status, signalStrength);
		if (remoteSettingsValues.length != RemoteSettings.canRemoteCount()) {
			throw new IllegalArgumentException(String.format("%1$s should have %2$s arguments, found %3$s", 
					RemoteSettings.class.getSimpleName(), RemoteSettings.values().length, remoteSettingsValues.length));
		}
		int i = -1;
		for (final RemoteSettings rs : RemoteSettings.values()) {
			if (rs.canRemote()) {
				this.data.add(new RemoteSettingsData(rs, remoteSettingsValues[++i]));
			}
		}
	}


	/**
	 * Sets the {@linkplain RemoteSettingsData} values from data stored on the host
	 */
	protected void setRemoteSettingsDataFromHostValues() {
		if (!data.isEmpty()) {
			return;
		}
		for (final RemoteSettings rs : RemoteSettings.values()) {
			if (rs.canRemote()) {
				data.add(new RemoteSettingsData(rs, Integer.parseInt(ServiceProvider.IMPL.getWirelessService().settingsGet(rs, getNodeAddress()))));
			}
		}
	}
	
	/**
	 * Gets a settings data value for a remote setting
	 * 
	 * @param remoteSettings the remote setting to get
	 * @return the remote settings data value
	 */
	protected RemoteSettingsData get(final RemoteSettings remoteSettings) {
		for (final RemoteSettingsData rs : data) {
			if (rs.getSettings() == remoteSettings) {
				return rs;
			}
		}
		return null;
	}
	
	/**
	 * @return all of the {@linkplain RemoteSettingsData}
	 */
	protected List<RemoteSettingsData> getAll() {
		return Collections.unmodifiableList(data);
	}
	
	/**
	 * @return all the {@linkplain RemoteSettingsData#getValue()}
	 */
	public int[] getAllData() {
		final int[] dataValues = new int[RemoteSettings.canRemoteCount()];
		int i = 0;
		for (final RemoteSettingsData rs : data) {
			dataValues[i++] = rs.getValue();
		}
		return Arrays.copyOf(dataValues, RemoteSettings.canRemoteCount());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String toString() {
		final Field[] fields = getClass().getDeclaredFields();
		final StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append(" [");
		for (final Field field : fields) {
			try {
				sb.append(field.getName());
				sb.append('=');
				if (field.getType() == List.class) {
					sb.append('[');
					final List<RemoteSettingsData> dm = 
						(List<RemoteSettingsData>) field.get(this);
					if (dm != null) {
						for (final RemoteSettingsData me : dm) {
							sb.append(me.toString());
							sb.append(',');
						}
					}
					sb.append(']');
				} else {
					sb.append(field.getInt(this));
				}
				sb.append(", ");
			} catch (final Throwable t) {
				t.printStackTrace();
			}
		}
		sb.append(']');
		return sb.toString();
	}
}
