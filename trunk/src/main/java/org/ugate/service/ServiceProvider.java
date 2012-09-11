package org.ugate.service;

import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Properties;

import javax.jms.IllegalStateException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.ugate.service.entity.jpa.Host;
import org.ugate.service.entity.jpa.RemoteNode;
import org.ugate.service.web.SignatureAlgorithm;
import org.ugate.service.web.WebServer;

/**
 * Renders application services
 */
public enum ServiceProvider {
	
	IMPL;

	private final Logger log = LoggerFactory.getLogger(ServiceProvider.class);
	private ClassPathXmlApplicationContext appContext;
	private final WirelessService wirelessService;
	private final EmailService emailService;
	private WebServer webServer;
	private Host host;
	private RemoteNode remoteNode;
	
	/**
	 * Creates/Initializes a new {@linkplain ServiceProvider}
	 */
	private ServiceProvider() {
		wirelessService = new WirelessService();
		emailService = new EmailService();
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
	 * Starts\Restarts all underlying managed services.
	 * 
	 * @param host
	 *            the {@linkplain Host} to open the services for
	 * @param remoteNode
	 *            the {@linkplain RemoteNode} in the
	 *            {@linkplain Host#getRemoteNodes()} used to open the services
	 * @param startWebServer
	 *            true to start the {@linkplain WebServer}
	 * @return true when the {@linkplain WirelessService} successfully connected
	 *         to the local device
	 */
	public boolean connect(final Host host, final RemoteNode remoteNode,
			final boolean startWebServer) {
		return connect(host, remoteNode, startWebServer, null);
	}
	
	/**
	 * Starts\Restarts all underlying managed services.
	 * 
	 * @param host
	 *            the {@linkplain Host} to open the services for
	 * @param remoteNode
	 *            the {@linkplain RemoteNode} in the
	 *            {@linkplain Host#getRemoteNodes()} used to open the services
	 * @param startWebServer
	 *            true to start the {@linkplain WebServer}
	 * @param sa
	 *            the {@linkplain SignatureAlgorithm} to use when the
	 *            {@linkplain X509Certificate} needs to be created/signed
	 * @return true when the {@linkplain WirelessService} successfully connected
	 *         to the local device (required for application to run)
	 */
	public boolean connect(final Host host, final RemoteNode remoteNode,
			final boolean startWebServer, final SignatureAlgorithm sa) {
		try {
			if (host == null && this.host == null) {
				throw new NullPointerException(Host.class.getName() + " cannot be null");
			}
			if (this.host == null) {
				this.host = host;
			}
			if (this.host.getRemoteNodes() == null) {
				throw new NullPointerException(RemoteNode.class.getName() + " cannot be null");
			} else if (remoteNode == null) {
				throw new NullPointerException(String.format(
						"(%1$s) %2$s on %3$s", this.host.getRemoteNodes()
								.size(), RemoteNode.class.getName(), Host.class
								.getName()));
			}
			// validate that the remote node is part of the host
			RemoteNode rn;
			for (final Iterator<RemoteNode> rni = this.host.getRemoteNodes()
					.iterator(); rni.hasNext();) {
				rn = rni.next();
				if (rn == remoteNode) {
					this.remoteNode = remoteNode;
				}
			}
			if (this.remoteNode == null) {
				throw new IllegalStateException(String.format(
						"%1$s is invalid at address %2$s for host ID %3$s",
						RemoteNode.class.getName(), remoteNode.getAddress(),
						host.getId()));
			}
			init();
			if (!getWirelessService().connect(this.host, this.remoteNode)) {
				log.warn("The local wireless device is unavailable");
				return false;
			}
			if (!getEmailService().connect(this.host)) {
				log.info("Host is not available for an automatic email connection");
			}
			if (startWebServer) {
				startWebServer(this.host, sa);
			}
			return true;
		} catch (final Throwable t) {
			log.error(String.format("Unable to initialize/start %1$s", 
					ServiceProvider.class.getSimpleName()), t);
			disconnect();
			throw new RuntimeException(t);
		}
	}

	/**
	 * Stops all underlying services
	 */
	public void disconnect() {
		try {
			stopWebServer();
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
	 * Starts a {@linkplain WebServer}. If it has already been started it will
	 * be stopped and restarted
	 * 
	 * @param host
	 *            the {@linkplain Host#getId()}
	 * @param sa
	 *            the {@linkplain SignatureAlgorithm} to use when the
	 *            {@linkplain X509Certificate} needs to be created/signed
	 * @return true when started
	 */
	public boolean startWebServer(final Host host, final SignatureAlgorithm sa) {
		if (host == null || host.getId() <= 0) {
			return false;
		}
		if (webServer != null) {
			webServer.stop();
		}
		webServer = WebServer.start(host.getId(), (sa != null ? sa
				: SignatureAlgorithm.getDefault()));
		return true;
	}
	
	/**
	 * Stops a {@linkplain WebServer} if it has been previously started
	 */
	public void stopWebServer() {
		if (webServer != null) {
			webServer.stop();
			webServer = null;
		}
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
}
