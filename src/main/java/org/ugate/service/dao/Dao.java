package org.ugate.service.dao;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ugate.service.entity.Model;

/**
 * Base DAO
 */
public abstract class Dao {
	
	private static final Logger log = LoggerFactory.getLogger(Dao.class);

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
	 * Finds a model by ID
	 * 
	 * @param modelClass
	 *            the {@linkplain Model} class
	 * @param entityId
	 *            the {@linkplain Model} ID
	 * @return the {@linkplain Model}
	 */
	public <T, M extends Model> M findEntityById(final Class<M> modelClass, final T entityId) {
		return getEntityManager().find(modelClass, entityId);
	}

	public int deleteEntitiesById(final String matchField, final Model... entities) {
		try {
		if (entities != null && entities.length > 0) {
			final List<Object> ids = new ArrayList<>(entities.length);
			for (final Model e : entities) {
				ids.add(invokeGet(e, matchField));
			}
				final String q = String.format(
						"DELETE FROM %1$s AS m WHERE m.%2$s IN :inMatches",
						entities[0].getClass().getName(), matchField);
			return getEntityManager().createQuery(q)
					.setParameter("inMatches", ids)
					.executeUpdate();
		}
		} catch (final ConstraintViolationException e) {
			
			log.warn("Unable to delete entities", e);
		}
		return -1;
	}

	/**
	 * Invokes a getter method on a {@linkplain Model}
	 * 
	 * @param e
	 *            the {@linkplain Model}
	 * @param fieldName
	 *            the field name in the {@linkplain Model}
	 * @return the return value from the
	 *         {@linkplain MethodHandle#invoke(Object...)}
	 */
	protected static Object invokeGet(final Model e, final String fieldName) {
		try {
			return buildMethodHandle(e, fieldName).invoke();
		} catch (final Throwable t) {
			throw new RuntimeException("Unable to invoke get method", t);
		}
	}

	/**
	 * Builds an accessor {@linkplain MethodHandle}
	 * 
	 * @param e
	 *            the {@linkplain Model}
	 * @param fieldName
	 *            the field name in the {@linkplain Model}
	 * @return the {@linkplain MethodHandle}
	 */
	protected static MethodHandle buildMethodHandle(final Model e, final String fieldName) {
		try {
			final String accessorName = buildMethodName("get", fieldName);
			return MethodHandles
			.lookup()
			.findVirtual(
					e.getClass(),
					accessorName,
					MethodType.methodType(e.getClass()
							.getMethod(accessorName)
							.getReturnType())).bindTo(e);
		} catch (final Throwable t) {
			throw new RuntimeException("Unable to build method", t);
		}
	}

	/**
	 * Builds a method name
	 * 
	 * @param prefix
	 *            the prefix
	 * @param fieldName
	 *            the name of the field
	 * @return the method name
	 */
	protected static String buildMethodName(final String prefix,
			final String fieldName) {
		return (fieldName.startsWith(prefix) ? fieldName : prefix
				+ fieldName.substring(0, 1).toUpperCase()
				+ fieldName.substring(1));
	}

	/**
	 * @return the {@linkplain EntityManager}
	 */
	protected abstract EntityManager getEntityManager();
}
