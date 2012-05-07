package org.ugate.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.ugate.service.dao.SettingsDao;
import org.ugate.service.entity.jpa.Message;

/**
 * Settings service
 */
@Service
@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
public class SettingsService {
	
    @Resource
    private SettingsDao settingsDao;

	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public void saveMessage(final Message msg) {
		settingsDao.persistMessage(msg);
	}
	
	public List<Message> getAllMessages() {
		return settingsDao.getAllMessages();
	}
}
