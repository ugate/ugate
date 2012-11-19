package org.ugate.service.entity;

/**
 * A {@link IModelType} {@link #getValue()} holder
 */
public class TypeValue<T extends Model> {
	private final IModelType<T> type;
	private final Object value;

	/**
	 * Constructor
	 * 
	 * @param type
	 *            the {@link #getType()}
	 * @param value
	 *            the {@link #getValue()}
	 */
	public TypeValue(final IModelType<T> type, final Object value) {
		this.type = type;
		this.value = value;
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
	public Object getValue() {
		return value;
	}
}
