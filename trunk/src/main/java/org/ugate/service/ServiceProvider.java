package org.ugate.service;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.ugate.service.entity.EntityExtractor;
import org.ugate.service.entity.jpa.Actor;
import org.ugate.service.entity.jpa.Host;

/**
 * Renders application services
 */
public enum ServiceProvider {
	
	IMPL;

	private final Logger log = LoggerFactory.getLogger(ServiceProvider.class);
	private ClassPathXmlApplicationContext appContext;
	private EntityExtractor<Actor> actorExtractor;
	private WirelessService wirelessService;
	private EmailService emailService;
	private WebService webService;
	
	/**
	 * Creates/Initializes a new {@linkplain ServiceProvider}
	 */
	private ServiceProvider() {
	}

	/**
	 * Initializes the underlying services
	 * 
	 * @param hostExtractor
	 *            the {@link EntityExtractor} used by the
	 *            {@link ServiceProvider} for a global {@link Host}
	 * @return true when the {@linkplain WirelessService} successfully connected
	 *         to the local device
	 */
	public boolean init(final EntityExtractor<Actor> actorExtractor) {
		disconnect();
		this.actorExtractor = actorExtractor;
		wirelessService = new WirelessService(this.actorExtractor);
		emailService = new EmailService(this.actorExtractor);
		webService = new WebService(this.actorExtractor);
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
		boolean hasDisconnected = false;
		try {
			if (getWebService() != null) {
				getWebService().stop();
				hasDisconnected = true;
			}
		} catch (final Exception e) {
			log.error("Unable to stop web server", e);
		}
		try {
			if (getEmailService() != null) {
				getEmailService().disconnect();
				hasDisconnected = true;
			}
		} catch (final Exception e) {
			log.error("Unable to disconnect from email", e);
		}
		try {
			if (getWirelessService() != null) {
				getWirelessService().disconnect();
				hasDisconnected = true;
			}
		} catch (final Exception e) {
			log.error("Unable to disconnect wireless connection", e);
		}
		try {
			if (appContext != null) {
				appContext.close();
				hasDisconnected = true;
			}
		} catch (final Exception e) {
			log.error("Unable to close application service context", e);
		}
		if (hasDisconnected) {
			log.info(String.format("%1$s disconnected", 
					ServiceProvider.class.getSimpleName()));
		}
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
