package org.ugate.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.ugate.service.dao.SettingsDao;
import org.ugate.service.entity.jpa.Message;

/**
 * Settings service
 */
@Repository
@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
public class SettingsService {
	
    @Resource(name="SettingsDao")   // inject it the way you like e.g. @Autowired / Setter / Constructor injection, etc..
    private SettingsDao settingsDao;

	@Transactional
	public void saveMessage(final Message msg) {
		settingsDao.persistMessage(msg);
	}
}
