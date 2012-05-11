package org.ugate.service.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.ugate.service.entity.jpa.Message;

/**
 * Settings DAO
 */
@Repository
public class SettingsDao extends Dao {
    
	@PersistenceContext
	private EntityManager em;

	public void persistMessage(final Message msg) {
		em.persist(msg);
	}

	public List<Message> getAllMessages() {
        final TypedQuery<Message> q = em.createQuery("select m from Message m", Message.class);
        return q.getResultList();
	}

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}
}
