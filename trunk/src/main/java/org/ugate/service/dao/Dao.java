package org.ugate.service.dao;

import javax.persistence.Entity;
import javax.persistence.EntityManager;

import org.ugate.service.entity.Model;

/**
 * Base DAO
 */
public abstract class Dao {

	/**
	 * Gets the total count of an entity
	 * 
	 * @param clazz the entity class
	 * @return the total count
	 */
	protected long getTotalCount(final Class<? extends Model> clazz) {
		return getEntityManager().createQuery(String.format("select count(x.id) from %1$s x", 
				clazz.getSimpleName()), Long.class).getSingleResult();
	}
	
	/**
	 * Persists an {@linkplain Entity}
	 * 
	 * @param entity
	 *            the entity to persist
	 */
	public void persistEntity(final Model entity) {
		getEntityManager().persist(entity);
	}
	
	/**
	 * Merges an {@linkplain Entity}
	 * 
	 * @param entity
	 *            the entity to persist
	 */
	public void mergeEntity(final Model entity) {
		getEntityManager().merge(entity);
	}

	/**
	 * @return the {@linkplain EntityManager}
	 */
	protected abstract EntityManager getEntityManager();
}
