package org.ugate.service.entity;

import java.lang.reflect.Method;

/**
 * {@linkplain Model} coupler used for pseudo binding
 * 
 * @param <M>
 *            the {@linkplain Model} type
 */
public class ModelCoupler<M extends Model, R> {

	private M model;
	private final String fieldName;
	private Method fieldGetter;
	private Method fieldSetter;
	
	/**
	 * {@linkplain ModelCoupler} constructor
	 * 
	 * @param model
	 *            the {@linkplain Model} to couple
	 */
	public ModelCoupler(final M model, final String fieldName, final Class<R> type) {
		this.model = model;
		this.fieldName = fieldName.substring(0, 1).toUpperCase() + 
				fieldName.substring(1);
		try {
			this.fieldSetter = this.model.getClass().getMethod("set" + this.fieldName,
					type);
			try {
				this.fieldGetter = this.model.getClass().getMethod("get" + this.fieldName);
			} catch (final NoSuchMethodException e) {
				try {
					this.fieldGetter = this.model.getClass().getMethod("is" + this.fieldName);
				} catch (final NoSuchMethodException e2) {
					this.fieldGetter = this.model.getClass().getMethod("has" + this.fieldName);
				}
			}
		} catch (final Throwable t) {
			throw new IllegalArgumentException("Unable to couple to model: " + model, t);
		}
	}
	
	public void set(final R value) {
		try {
			getFieldSetter().invoke(getModel(), value);
		} catch (final Throwable t) {
			throw new RuntimeException(
					String.format("Unable to set %1$s on %2$s using %3$s", 
							value, getModel(), getFieldSetter().getName()));
		}
	}

	/**
	 * @return the fieldSetter
	 */
	protected Method getFieldSetter() {
		return fieldSetter;
	}

	/**
	 * @return the fieldGetter
	 */
	protected Method getFieldGetter() {
		return fieldGetter;
	}

	/**
	 * @return gets the {@linkplain Model} to couple
	 */
	public M getModel() {
		return model;
	}

	/**
	 * Sets a new model
	 * 
	 * @param model the model to set
	 */
	public void setModel(final M model) {
		this.model = model;
	}

	/**
	 * @return the field name of the {@linkplain Model} to couple to
	 */
	public String getFieldName() {
		return fieldName;
	}
}
