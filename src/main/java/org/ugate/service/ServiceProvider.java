package org.ugate.service;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Renders application services
 */
public enum ServiceProvider {
	
	IMPL;

	private final Logger log = LoggerFactory.getLogger(ServiceProvider.class);
	private ClassPathXmlApplicationContext appContext;
	private final WirelessService wirelessService;
	private final EmailService emailService;
	private final WebService webService;
	
	/**
	 * Creates/Initializes a new {@linkplain ServiceProvider}
	 */
	private ServiceProvider() {
		wirelessService = new WirelessService();
		emailService = new EmailService();
		webService = new WebService();
	}

	/**
	 * Initializes the underlying services
	 * 
	 * @return true when the {@linkplain WirelessService} successfully connected
	 *         to the local device
	 */
	public boolean init() {
		if (appContext == null) {
			appContext = new ClassPathXmlApplicationContext(new String[] { "spring-all.xml" });
			appContext.start();
		}
		return getWirelessService().init();
	}

	/**
	 * Stops all underlying services
	 */
	public void disconnect() {
		try {
			getWebService().stop();
		} catch (final Exception e) {
			log.error("Unable to stop web server", e);
		}
		try {
			getEmailService().disconnect();
		} catch (final Exception e) {
			log.error("Unable to disconnect from email", e);
		}
		try {
			getWirelessService().disconnect();
		} catch (final Exception e) {
			log.error("Unable to disconnect wireless connection", e);
		}
		try {
			appContext.close();
		} catch (final Exception e) {
			log.error("Unable to close application service context", e);
		}
		log.info(String.format("%1$s closed", 
				ServiceProvider.class.getSimpleName()));
	}

	/**
	 * @return the {@linkplain WebService}
	 */
	public WebService getWebService() {
		return webService;
	}

	/**
	 * @return the {@linkplain WirelessService}
	 */
	public WirelessService getWirelessService() {
		return wirelessService;
	}

	/**
	 * @return the {@linkplain EmailService}
	 */
	public EmailService getEmailService() {
		return emailService;
	}
	
	/**
	 * @return the {@linkplain RemoteNodeService}
	 */
	public RemoteNodeService getRemoteNodeService() {
		return (RemoteNodeService) appContext.getBean(RemoteNodeService.class.getSimpleName());
	}
	
	/**
	 * @return the {@linkplain CredentialService}
	 */
	public CredentialService getCredentialService() {
		return (CredentialService) appContext.getBean(CredentialService.class.getSimpleName());
	}
	
	/**
	 * @return the database {@linkplain Properties}
	 */
	public Properties getDbProperties() {
		return (Properties) appContext.getBean("dbProperties");
	}
	
	/**
	 * @return the {@linkplain ClassPathXmlApplicationContext}
	 */
	protected ClassPathXmlApplicationContext getApplicationContext() {
		return appContext;
	}

	/**
	 * Types of services provided by the {@linkplain ServiceProvider}
	 */
	public static enum Type {
		WIRELESS, WEB, EMAIL;
	}
}
