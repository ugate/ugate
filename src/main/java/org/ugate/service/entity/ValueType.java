package org.ugate.service.entity;

import java.util.Map;

import org.ugate.resources.RS.KEY;

/**
 * A {@link IModelType} {@link #getValue()} holder
 * 
 * @param <T>
 *            the {@link Model} type
 * @param <V>
 *            the {@link Model} {@link #getValue()} type
 */
public class ValueType<T extends Model, V> {
	private final IModelType<T> type;
	private final V value;
	private final Map<KEY, V> optionValues;

	/**
	 * Constructor
	 * 
	 * @param type
	 *            the {@link #getType()}
	 * @param value
	 *            the {@link #getValue()}
	 */
	public ValueType(final IModelType<T> type, final V value) {
		this.type = type;
		this.value = value;
		this.optionValues = null;
	}

	/**
	 * Constructor
	 * 
	 * @param type
	 *            the {@link #getType()}
	 * @param value
	 *            the {@link #getValue()}
	 * @param the
	 *            {@link #getOptionValues()}
	 */
	public ValueType(final IModelType<T> type, final V value,
			final Map<KEY, V> optionValues) {
		this.type = type;
		this.value = value;
		this.optionValues = optionValues;
	}

	/**
	 * @return the type of {@link #getValue()}
	 */
	public IModelType<T> getType() {
		return type;
	}

	/**
	 * @return the value of the {@link #getType()}
	 */
	public V getValue() {
		return value;
	}

	/**
	 * @return the {@link #getValue()} options (null/empty when no options are
	 *         designated for the {@link ValueType})
	 */
	public Map<KEY, V> getOptionValues() {
		return optionValues;
	}
}
