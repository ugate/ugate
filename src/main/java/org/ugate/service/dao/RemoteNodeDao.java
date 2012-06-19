package org.ugate.service.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.ugate.service.entity.jpa.RemoteNode;

/**
 * {@linkplain RemoteNode} DAO
 */
@Repository
public class RemoteNodeDao extends Dao {

	@PersistenceContext
	private EntityManager em;

	public List<RemoteNode> getRemoteNodesForHost(final int hostId) {
		final TypedQuery<RemoteNode> q = em.createQuery(
				"select rn from RemoteNode rn where rn.host.id = :hostId", RemoteNode.class);
		q.setParameter("hostId", hostId);
		return q.getResultList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected EntityManager getEntityManager() {
		return em;
	}
}
