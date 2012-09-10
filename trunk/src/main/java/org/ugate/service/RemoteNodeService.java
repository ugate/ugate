package org.ugate.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.ugate.service.dao.RemoteNodeDao;
import org.ugate.service.entity.jpa.Host;
import org.ugate.service.entity.jpa.RemoteNode;
import org.ugate.service.entity.jpa.RemoteNodeReading;

/**
 * {@linkplain RemoteNode} service
 */
@Service
@Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
public class RemoteNodeService {

	@Resource
	private RemoteNodeDao remoteNodeDao;

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void save(final RemoteNode remoteNode) {
		remoteNodeDao.persistEntity(remoteNode);
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void merge(final RemoteNode remoteNode) {
		remoteNodeDao.mergeEntity(remoteNode);
	}

	/**
	 * Gets the {@linkplain RemoteNode}(s) for a given {@linkplain Host#getId()}
	 * 
	 * @param hostId
	 *            the {@linkplain Host#getId()} to get the
	 *            {@linkplain RemoteNode}(s) for
	 * @return the {@linkplain RemoteNode}(s)
	 */
	public List<RemoteNode> findForHost(final int hostId) {
		return remoteNodeDao.findByHostId(hostId);
	}

	/**
	 * Gets the {@linkplain RemoteNodeReading}(s) for a given
	 * {@linkplain RemoteNode#getId()}
	 * 
	 * @param remoteNode
	 *            the {@linkplain RemoteNode} to get the
	 *            {@linkplain RemoteNodeReading}(s) for
	 * @param startPosition
	 *            the pagination starting position
	 * @param maxResults
	 *            the maximum number of {@linkplain RemoteNodeReading}(s) to
	 *            return
	 * @return the {@linkplain RemoteNodeReading}(s)
	 */
	public List<RemoteNodeReading> findReadingsById(final RemoteNode remoteNode, 
			final int startPosition, final int maxResults) {
		return remoteNodeDao.findReadingsById(remoteNode, startPosition, maxResults);
	}

	/**
	 * Gets the {@linkplain RemoteNodeReading}(s) for a given {@linkplain Date}
	 * 
	 * @param remoteNode
	 *            the {@linkplain RemoteNode} to get the
	 *            {@linkplain RemoteNodeReading}(s) for
	 * @param date
	 *            the {@linkplain Date} to get the
	 *            {@linkplain RemoteNodeReading}(s) for
	 * @param asc
	 *            true for results in ascending order, false for descending
	 * @return the {@linkplain RemoteNodeReading}(s)
	 */
	public List<RemoteNodeReading> findReadingsByDate(
			final RemoteNode remoteNode, final Calendar cal, 
			final boolean asc) {
		final Calendar sc = Calendar.getInstance();
		sc.setTime(cal.getTime());
		sc.set(Calendar.HOUR_OF_DAY, 0);
		sc.set(Calendar.MINUTE, 0);
		sc.set(Calendar.SECOND, 0);
		sc.set(Calendar.MILLISECOND, 0);
		final Calendar ec = Calendar.getInstance();
		ec.setTime(sc.getTime());
		ec.add(Calendar.DAY_OF_MONTH, 1);
		//UGateUtil.PLAIN_LOGGER.info("Start : " + UGateUtil.calFormat(sc) + " End: " + UGateUtil.calFormat(ec));
		return remoteNodeDao.findReadingsByIdAndDate(remoteNode, sc, ec, asc);
	}
}
