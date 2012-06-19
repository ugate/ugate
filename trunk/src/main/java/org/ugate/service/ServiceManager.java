package org.ugate.service;

import java.security.cert.X509Certificate;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.ugate.service.entity.jpa.Host;
import org.ugate.service.web.SignatureAlgorithm;
import org.ugate.service.web.WebServer;

/**
 * Service manager
 */
public enum ServiceManager {
	
	IMPL;

	private final Logger log = LoggerFactory.getLogger(ServiceManager.class);
	private ClassPathXmlApplicationContext appContext;
	private WebServer webServer;
	
	/**
	 * Creates/Initializes a new {@linkplain ServiceManager}
	 */
	private ServiceManager() {
	}
	
	/**
	 * Opens all underlying managed services (<b>excluding the web server- see
	 * {@linkplain #startWebServer(Host)}</b>).
	 */
	public void open() {
		open(0, null);
	}
	
	/**
	 * Opens all underlying managed services
	 * 
	 * @param host
	 *            the {@linkplain Host#getId()} to open the services for
	 * @param sa
	 *            the {@linkplain SignatureAlgorithm} to use when the
	 *            {@linkplain X509Certificate} needs to be created/signed
	 */
	public void open(final int hostId, final SignatureAlgorithm sa) {
		try {
			appContext = new ClassPathXmlApplicationContext(new String[] { "spring-all.xml" });
			appContext.start();
			startWebServer(hostId, sa);
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
	 * Starts a {@linkplain WebServer}. If it has already been started it will
	 * be stopped and restarted
	 * 
	 * @param host
	 *            the {@linkplain Host#getId()}
	 * @param sa
	 *            the {@linkplain SignatureAlgorithm} to use when the
	 *            {@linkplain X509Certificate} needs to be created/signed
	 */
	public void startWebServer(final int hostId, final SignatureAlgorithm sa) {
		if (hostId <= 0) {
			return;
		}
		if (webServer != null) {
			webServer.stop();
		}
		webServer = WebServer.start(hostId, sa);
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
}
