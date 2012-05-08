package org.ugate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.ugate.service.web.WebServer;

/**
 * Service manager
 */
public enum ServiceManager {
	
	IMPL;

	private final Logger log = LoggerFactory.getLogger(ServiceManager.class);
	private int webServerPortNumber = 9080;
	private ClassPathXmlApplicationContext appContext;
	private WebServer webServer;
	
	/**
	 * Creates/Initializes a new {@linkplain ServiceManager}
	 */
	private ServiceManager() {
	}
	
	/**
	 * Opens all underlying managed services
	 */
	public void open() {
		try {
			appContext = new ClassPathXmlApplicationContext(new String[] { "spring-all.xml" });
			appContext.start();
			//getSettingsService().saveMessage(new Message("Initialization Message!"));
			// TODO : get port number from DB
			startWebServer();
		} catch (final Throwable t) {
			log.error(String.format("Unable to initialize/start %1$s", 
					ServiceManager.class.getSimpleName()), t);
			close();
			throw new RuntimeException(t);
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
		log.info(String.format("%1$s closed", 
				ServiceManager.class.getSimpleName()));
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
}
