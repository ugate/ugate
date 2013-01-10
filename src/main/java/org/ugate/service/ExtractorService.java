package org.ugate.service;

import org.ugate.service.entity.EntityExtractor;
import org.ugate.service.entity.Model;

/**
 * Extractor service
 *
 * @param <T> the {@link Model} used by the {@link #getExtractor()}
 */
public abstract class ExtractorService<T extends Model> {

	private final EntityExtractor<T> extractor;

	/**
	 * Constructor
	 * 
	 * @param extractor
	 *            the {@link EntityExtractor} for the {@link Model} used by the
	 *            {@link ExtractorService}
	 */
	ExtractorService(final EntityExtractor<T> extractor) {
		this.extractor = extractor;
	}

	/**
	 * @return the {@link EntityExtractor} used for the {@link ExtractorService}
	 */
	public EntityExtractor<T> getExtractor() {
		return extractor;
	}

	/**
	 * @return convenience for {@link EntityExtractor#extract()}
	 */
	public T extract() {
		return getExtractor() == null ? null : getExtractor().extract();
	}
}
