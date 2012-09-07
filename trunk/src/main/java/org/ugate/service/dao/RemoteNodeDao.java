package org.ugate.service.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.ugate.service.entity.jpa.RemoteNode;
import org.ugate.service.entity.jpa.RemoteNodeReading;

/**
 * {@linkplain RemoteNode} DAO
 */
@Repository
public class RemoteNodeDao extends Dao {

	@PersistenceContext
	private EntityManager em;

	public RemoteNode findByAddress(final String address) {
		final TypedQuery<RemoteNode> q = em.createQuery(
				"select rn from RemoteNode rn where rn.address = :addy", RemoteNode.class);
		q.setParameter("addy", address);
		return q.getSingleResult();
	}

	public List<RemoteNode> findByHostId(final int hostId) {
		final TypedQuery<RemoteNode> q = em.createQuery(
				"select rn from RemoteNode rn where rn.host.id = :hostId", RemoteNode.class);
		q.setParameter("hostId", hostId);
		return q.getResultList();
	}

	public List<RemoteNodeReading> findReadingsById(final RemoteNode remoteNode,
			final int startPosition, final int maxResults) {
		final TypedQuery<RemoteNodeReading> q = em
				.createQuery(
						"select rnr from RemoteNodeReading rnr where rnr.remoteNode.id = :id order by rnr.readDate desc",
						RemoteNodeReading.class);
		q.setParameter("id", remoteNode.getId());
		return q.setFirstResult(startPosition).setMaxResults(maxResults).getResultList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected EntityManager getEntityManager() {
		return em;
	}
}
