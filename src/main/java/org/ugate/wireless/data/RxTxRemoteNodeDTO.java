package org.ugate.wireless.data;

import java.lang.reflect.Field;
import java.util.Arrays;

import org.ugate.service.entity.RemoteNodeType;
import org.ugate.service.entity.jpa.RemoteNode;


/**
 * Settings data
 */
public class RxTxRemoteNodeDTO extends RxData {

	private int[] data = new int[RemoteNodeType.canRemoteCount()];
	
	/**
	 * Constructs settings data with the current settings data values set {@linkplain #setRemoteSettingsDataFromHostValues()}
	 * 
	 * @param remoteNode the remote node index
	 */
	public RxTxRemoteNodeDTO(final RemoteNode remoteNode) {
		super(remoteNode,Status.NORMAL, 0);
		transferData(true);
	}


	/**
	 * Full constructor
	 * 
	 * @param remoteNode
	 *            the {@linkplain RemoteNode}
	 * @param status the {@linkplain Status} of the wireless transfer
	 * @param signalStrength the signal strength of the remote wireless connection at the time of the transfer
	 * @param rawRemoteNodeData the {@linkplain RemoteNodeType} values in the order they exist in the {@linkplain RemoteNodeType}
	 */
	public RxTxRemoteNodeDTO(final RemoteNode remoteNode, final Status status, final Integer signalStrength, final int... rawRemoteNodeData) {
		super(remoteNode, status, signalStrength);
		if (rawRemoteNodeData.length != RemoteNodeType.canRemoteCount()) {
			throw new IllegalArgumentException(String.format(
					"%1$s should have %2$s arguments, found %3$s",
					RemoteNodeType.class.getSimpleName(),
					RemoteNodeType.values().length, rawRemoteNodeData.length));
		}
		this.data = rawRemoteNodeData;
		transferData(false);
	}


	/**
	 * Transfers data from/to {@linkplain #getData()} and
	 * {@linkplain #getRemoteNode()}
	 * 
	 * @param toData
	 *            true when transferring the from the {@linkplain RemoteNode} to
	 *            the {@linkplain #getData()} (false for the reverse)
	 */
	protected void transferData(final boolean toData) {
		if (!toData && data.length == 0) {
			return;
		}
		int i = 0;
		for (final RemoteNodeType rnt : RemoteNodeType.values()) {
			if (rnt.canRemote()) {
				try {
					if (toData) {
						data[i] = rnt.getRemoteValue(getRemoteNode());
					} else {
						rnt.setRemoteValue(getRemoteNode(), data[i]);
					}
				} catch (final Throwable t) {
					throw new RuntimeException(
							String.format(
									"Unable to transfer data at index %1$s to/from %2$s.%3$s",
									RemoteNode.class.getName(), rnt.getKey(), i),
							t);
				}
			}
		}
	}
	
	/**
	 * @return all of the raw data values
	 */
	public int[] getData() {
		return Arrays.copyOf(data, RemoteNodeType.canRemoteCount());
	}

	/**
	 * {@inheritDoc}
	 */
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
				if (field.getType() == int[].class) {
					sb.append('[');
					final int[] dm = (int[]) field.get(this);
					if (dm != null) {
						for (final int me : dm) {
							sb.append(me);
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
