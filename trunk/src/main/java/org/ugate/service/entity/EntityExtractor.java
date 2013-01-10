package org.ugate.service.entity;


/**
 * Provides a means to dynamically extract a {@link Model}
 * 
 * @param <T>
 *            the {@link Model} type
 */
public interface EntityExtractor<T extends Model> {

	/**
	 * @return the {@link EntityExtractor} type
	 */
	public T extract();
}
