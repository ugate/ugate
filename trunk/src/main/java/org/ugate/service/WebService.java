package org.ugate.service;

import java.security.cert.X509Certificate;

import org.ugate.service.entity.jpa.Host;
import org.ugate.service.web.SignatureAlgorithm;
import org.ugate.service.web.WebServer;

/**
 * {@linkplain WebServer} service
 */
public class WebService {

	private WebServer webServer;

	/**
	 * Only {@linkplain ServiceProvider} constructor
	 */
	WebService() {
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
	public boolean start(final Host host, final SignatureAlgorithm sa) {
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
	public void stop() {
		if (webServer != null) {
			webServer.stop();
			webServer = null;
		}
	}

	/**
	 * @return true when the {@linkplain WebServer} is currently running
	 */
	public boolean isRunning() {
		return webServer != null && webServer.isRunning();
	}
}
