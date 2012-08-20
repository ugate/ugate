package org.ugate.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.ugate.service.dao.RemoteNodeDao;
import org.ugate.service.entity.jpa.Host;
import org.ugate.service.entity.jpa.RemoteNode;

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
}
