package org.ugate.service.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.ugate.service.entity.jpa.Message;

/**
 * Settings DAO
 */
@Repository
public class SettingsDao {
    
	@PersistenceContext
	private EntityManager em;

	public void persistMessage(final Message msg) {
		//new org.springframework.orm.jpa.JpaTransactionManager().se
		//new org.springframework.orm.jpa.
		//em = (TransactionManager) new InitialContext().lookup("jdbc/ugateDS");new 
		em.persist(msg);
	}

	@SuppressWarnings("unchecked")
	public List<Message> getAllMessages() {
        final Query q = em.createQuery("select m from Message m");
        return (List<Message>) q.getResultList();
	}
}
