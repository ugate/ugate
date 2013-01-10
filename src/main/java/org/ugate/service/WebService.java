package org.ugate.service;

import java.security.cert.X509Certificate;

import org.ugate.service.entity.EntityExtractor;
import org.ugate.service.entity.jpa.Actor;
import org.ugate.service.web.SignatureAlgorithm;
import org.ugate.service.web.WebServer;

/**
 * {@linkplain WebServer} service
 */
public class WebService extends ExtractorService<Actor> {

	private WebServer webServer;

	/**
	 * Constructor
	 * 
	 * @param extractor
	 *            the {@link EntityExtractor} for the {@link Actor} used by the
	 *            {@link EmailService}
	 */
	WebService(final EntityExtractor<Actor> extractor) {
		super(extractor);
	}
	
	/**
	 * Starts a {@linkplain WebServer}. If it has already been started it will
	 * be stopped and restarted
	 * 
	 * @param sa
	 *            the {@linkplain SignatureAlgorithm} to use when the
	 *            {@linkplain X509Certificate} needs to be created/signed
	 * @return true when started
	 */
	public boolean start(final SignatureAlgorithm sa) {
		if (extract() == null || extract().getId() <= 0
				|| extract().getHost() == null
				|| extract().getHost().getId() <= 0) {
			return false;
		}
		if (webServer != null) {
			webServer.stop();
		}
		webServer = WebServer.start(getExtractor(), (sa != null ? sa
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
