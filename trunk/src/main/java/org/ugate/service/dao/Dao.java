package org.ugate.service.dao;

import javax.persistence.Entity;
import javax.persistence.EntityManager;

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
	protected long getTotalCount(final Class<?> clazz) {
		return getEntityManager().createQuery(String.format("select count(x.id) from %1$s x", 
				clazz.getSimpleName()), Long.class).getSingleResult();
	}
	
	/**
	 * Persists an {@linkplain Entity}
	 * 
	 * TODO : verify an actual entity is passed
	 * 
	 * @param entity
	 *            the entity to persist
	 */
	public void persistEntity(final Object entity) {
		getEntityManager().persist(entity);
	}

	/**
	 * @return the {@linkplain EntityManager}
	 */
	protected abstract EntityManager getEntityManager();
}
