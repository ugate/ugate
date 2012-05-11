package org.ugate.service.dao;

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
	 * @return the {@linkplain EntityManager}
	 */
	protected abstract EntityManager getEntityManager();
}
