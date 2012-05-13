package org.ugate.service.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.ugate.service.entity.jpa.Actor;
import org.ugate.service.entity.jpa.Role;

/**
 * {@linkplain Actor}/{@linkplain Role} DAO
 */
@Repository
public class CredentialDao extends Dao {
	
	@PersistenceContext
	private EntityManager em;
	
	public void persistActor(final Actor actor) {
		em.persist(actor);
	}
	
	public Actor getActor(final String login) {
		final TypedQuery<Actor> q = em.createQuery("select a from Actor a where a.login = ?1", Actor.class);
		q.setParameter(1, login);
		return q.getSingleResult();
	}

	public Actor getActorByPassword(final String password) {
		final TypedQuery<Actor> q = em.createQuery("select a from Actor a where a.pwd = ?1", Actor.class);
		q.setParameter(1, password);
		return q.getSingleResult();
	}
	
	public long getActorCount() {
		return getTotalCount(Actor.class);
	}

	public long getRoleCount() {
		return getTotalCount(Role.class);
	}

	public List<Actor> getAllActors() {
        final TypedQuery<Actor> q = em.createQuery("select a from Actor a", Actor.class);
        return q.getResultList();
	}

	public List<Actor> getAllRoles() {
        final TypedQuery<Actor> q = em.createQuery("select a from Actor a", Actor.class);
        return q.getResultList();
	}

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}
}
