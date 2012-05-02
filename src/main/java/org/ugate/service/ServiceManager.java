package org.ugate.service;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ugate.resources.RS;
import org.ugate.service.web.WebServer;

public class ServiceManager {
	
	private static final Logger log = LoggerFactory.getLogger(ServiceManager.class);
	private static EntityManagerFactory factory;
	private static WebServer webServer;

	public static void startServices() {
		stopServices();
		setEmFactory(null);
		webServer = WebServer.start(9080);
	}
	
	public static void stopServices() {
		if (webServer != null) {
			webServer.stop();
		}
		try {
			if (factory != null && factory.isOpen()) {
				factory.close();
			}
			factory = null;
		} catch (final Exception e) {
			log.error("Unable to close " + EntityManagerFactory.class.getSimpleName(), e);
		}
	}
	
	public static final EntityManagerFactory getEmFactory() {
		return factory;
	}
	
	protected static final void setEmFactory(final EntityManagerFactory factory) {
		if (ServiceManager.factory != null && ServiceManager.factory.isOpen()) {
			ServiceManager.factory.close();
		}
		if (factory == null || !factory.isOpen()) {
			ServiceManager.factory = Persistence.
		            createEntityManagerFactory(RS.rbLabel("persistent.unit"), 
		            		System.getProperties());
			return;
		}
		ServiceManager.factory = factory;
	}
}
