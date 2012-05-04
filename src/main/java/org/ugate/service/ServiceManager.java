package org.ugate.service;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.ugate.resources.RS;
import org.ugate.service.entity.jpa.Message;
import org.ugate.service.web.WebServer;

/**
 * Service manager
 */
public class ServiceManager {

	private static final Logger log = LoggerFactory.getLogger(ServiceManager.class);
	private int webServerPortNumber = 9080;
	private final ClassPathXmlApplicationContext appContext;
	private WebServer webServer;
	private static EntityManagerFactory factory;
	
	/**
	 * Creates/Initializes a new {@linkplain ServiceManager}
	 */
	public ServiceManager() {
		try {
			appContext = new ClassPathXmlApplicationContext(new String[] { "spring-all.xml" });
			appContext.start();
			//TransactionManager tm = com.atomikos.icatch.jta.TransactionManagerImp.getTransactionManager();
			getSettingsService().saveMessage(new Message(""));
		} finally {
			// TODO : get port number from DB
			startWebServer();
		}
	}
	
	/**
	 * Stops all underlying managed services
	 */
	public void close() {
		try {
			stopWebServer();
		} catch (final Exception e) {
			log.error("Unable to stop web server", e);
		}
		try {
			appContext.close();
		} catch (final Exception e) {
			log.error("Unable to close application service context", e);
		}
		try {
			if (factory != null && factory.isOpen()) {
				factory.close();
			}
			factory = null;
		} catch (final Exception e) {
			log.error("Unable to close service factory", e);
		}
	}
	
	/**
	 * Starts a {@linkplain WebServer}. If it has already been started it will be stopped and restarted
	 */
	public void startWebServer() {
		webServer = WebServer.start(webServerPortNumber);
	}
	
	/**
	 * Stops a {@linkplain WebServer} if it has been previously started
	 */
	public void stopWebServer() {
		if (webServer != null) {
			webServer.stop();
		}
	}
	
	/**
	 * @return the {@linkplain SettingsService}
	 */
	public SettingsService getSettingsService() {
		return (SettingsService) appContext.getBean(SettingsService.class.getSimpleName());
	}
	
	/**
	 * @return the {@linkplain ClassPathXmlApplicationContext}
	 */
	protected ClassPathXmlApplicationContext getApplicationContext() {
		return appContext;
	}
	
	protected static final EntityManagerFactory getEmFactory() {
		return factory;
	}
	
	protected static final void setEmFactory(final EntityManagerFactory factory) {
		if (ServiceManager.factory != null && ServiceManager.factory.isOpen()) {
			ServiceManager.factory.close();
		}
		if (factory == null || !factory.isOpen()) {
			//new javax.naming.InitialContext().lookup("jdbc/ugateDS");
			ServiceManager.factory = Persistence.
		            createEntityManagerFactory(RS.rbLabel("persistent.unit"), 
		            		System.getProperties());
			return;
		}
		ServiceManager.factory = factory;
	}
}
