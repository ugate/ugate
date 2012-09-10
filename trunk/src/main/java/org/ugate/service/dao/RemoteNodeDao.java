package org.ugate.service.dao;

import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TemporalType;
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

	public List<RemoteNodeReading> findReadingsByIdAndDate(final RemoteNode remoteNode,
			final Calendar startInclusive, final Calendar endExclusive,
			final boolean asc) {
		final String orderBy = (asc ? "asc" : "desc");
		final TypedQuery<RemoteNodeReading> q = em
				.createQuery(
						"select rnr from RemoteNodeReading rnr where rnr.remoteNode.id = :id and rnr.readDate >= :sd and rnr.readDate < :ed order by rnr.readDate "
								+ orderBy + ", rnr.fromMultiState " + orderBy,
						RemoteNodeReading.class);
		q.setParameter("id", remoteNode.getId());
		q.setParameter("sd", startInclusive.getTime(), TemporalType.TIMESTAMP);
		q.setParameter("ed", endExclusive.getTime(), TemporalType.TIMESTAMP);
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
