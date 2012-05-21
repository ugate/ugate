package org.ugate.service.web;

import java.util.Arrays;
import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.authentication.ClientCertAuthenticator;
import org.eclipse.jetty.security.authentication.DigestAuthenticator;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.eclipse.jetty.server.ssl.SslSocketConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.security.Credential;
import org.eclipse.jetty.util.security.Password;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ugate.service.RoleType;

/**
 * Embedded web server
 */
public class WebServer {

	private static final Logger log = LoggerFactory.getLogger(WebServer.class);
	public static final String[] PROTOCOL_INCLUDE = new String[] {
		"TLSv1", "TLSv1.1", "TLSv1.2"
	};
	public static final String[] PROTOCOL_EXCLUDE = new String[] {
		"TLSv1", "TLSv1.1", "TLSv1.2"
	};
	public static final String[] CIPHER_SUITES_INCLUDE = new String[] { 
		"TLS_RSA_WITH_AES_256_CBC_SHA", "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA",
		"TLS_DHE_RSA_WITH_AES_256_CBC_SHA", "PBEWithSHA1AndDES"
	};
	public static final String[] CIPHER_SUITES_EXCLUDE = new String[] { 
		"SSL_RSA_WITH_RC4_128_MD5", "SSL_RSA_WITH_RC4_128_SHA",
		"SSL_RSA_WITH_3DES_EDE_CBC_SHA", "SSL_DHE_RSA_WITH_3DES_EDE_CBC_SHA",
		"SSL_DHE_DSS_WITH_3DES_EDE_CBC_SHA", "SSL_RSA_WITH_DES_CBC_SHA",
		"SSL_DHE_RSA_WITH_DES_CBC_SHA", "SSL_DHE_DSS_WITH_DES_CBC_SHA",
		"SSL_RSA_EXPORT_WITH_RC4_40_MD5", "SSL_RSA_EXPORT_WITH_DES40_CBC_SHA",
		"SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA", 
		"SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA", "SSL_RSA_WITH_NULL_MD5",
		"SSL_RSA_WITH_NULL_SHA", "SSL_DH_anon_WITH_RC4_128_MD5",
		"SSL_DH_anon_WITH_3DES_EDE_CBC_SHA", "SSL_DH_anon_WITH_DES_CBC_SHA",
		"SSL_DH_anon_EXPORT_WITH_RC4_40_MD5", 
		"SSL_DH_anon_EXPORT_WITH_DES40_CBC_SHA",
		"TLS_KRB5_WITH_RC4_128_MD5", "TLS_KRB5_WITH_3DES_EDE_CBC_MD5",
		"TLS_KRB5_WITH_DES_CBC_MD5", "TLS_KRB5_EXPORT_WITH_RC4_40_MD5",
		"TLS_KRB5_EXPORT_WITH_DES_CBC_40_MD5"
	};
	private final int portNumber;
	private Server server;
	private CertificateHolder certHolder;

	/**
	 * Constructor
	 * 
	 * @param portNumber
	 *            the port number
	 */
	private WebServer(final int portNumber) {
		this.portNumber = portNumber <= 0 ? 80 : portNumber;
	}

	/**
	 * Starts the web server in a new thread
	 * 
	 * @param portNumber
	 *            the port number to run the server on
	 */
	public static final WebServer start(final int portNumber) {
		final WebServer webServer = new WebServer(portNumber);
		final Thread webServerAgent = new Thread(Thread.currentThread()
				.getThreadGroup(), new Runnable() {
			@Override
			public void run() {
				webServer.startServer();
			}
		}, WebServer.class.getSimpleName() + '-' + System.currentTimeMillis());
		webServerAgent.setDaemon(true);
		webServerAgent.start();
		return webServer;
	}

	/**
	 * Starts the web server
	 */
	protected final void startServer() {
		try {
			// Get server from configuration file
			// final Resource serverXml =
			// Resource.newSystemResource("META-INF/jetty.xml");
			// final XmlConfiguration configuration = new
			// XmlConfiguration(serverXml.getInputStream());
			// server = (Server) configuration.configure();

			server = new Server();
			// TODO : clean up SSL support along with pulling certificate from DB (if present)
			certHolder = CertificateHolder.newSelfSignedCertificate("US", "UGate", "New York", 
					"New York", "certificate@example.org", "localhost", "testPassword");
			if (log.isDebugEnabled()) {
				certHolder.dumpLog();
			}
			final SslContextFactory sslCnxt = new SslContextFactory();
			//sslCnxt.setCertAlias(certHolder.getAlias());
			sslCnxt.setKeyStore(certHolder.getKeyStore());
			sslCnxt.setKeyStoreType(certHolder.getKeyStore().getType());
			sslCnxt.setKeyStoreProvider(certHolder.getKeyStore().getProvider().getName());
			//sslCnxt.setIncludeCipherSuites(CIPHER_SUITES_INCLUDE);
			//sslCnxt.setExcludeCipherSuites(CIPHER_SUITES_EXCLUDE);
			//sslCnxt.setIncludeProtocols(PROTOCOL_INCLUDE);
			sslCnxt.setTrustAll(false);
			final SslSocketConnector sslCnct = new SslSocketConnector(sslCnxt);
			sslCnct.setPort(443);
			// Do not use an HTTP channel selector in addition to the SSL or else 
			// requests will fail with "no cipher suites in common"
//			final SslSelectChannelConnector sslCnct = new SslSelectChannelConnector(sslContextFactory);
//			sslCnct.setPort(443);
//			final SelectChannelConnector httpCnct = new SelectChannelConnector();
//			httpCnct.setPort(getPortNumber());
			
			server.setConnectors(new Connector[] { sslCnct });

			final EnumSet<DispatcherType> dispatchers = EnumSet.range(
					DispatcherType.FORWARD, DispatcherType.ERROR);
			final ServletContextHandler context = new ServletContextHandler(
					ServletContextHandler.SESSIONS);
			context.setContextPath("/");
			context.addFilter(GlobalFilter.class, "/*", dispatchers);
			context.addServlet(DefaultAppServlet.class, "/");

			addAuthentication(context);
			server.setHandler(context);

			// server.setDumpAfterStart(true);
			server.start();
			server.join();
		} catch (final Throwable e) {
			log.error("Unable to start web server", e);
		} finally {

		}
	}

	/**
	 * Programmatic approach to <a
	 * href="http://wiki.eclipse.org/Jetty/Howto/Secure_Passwords">Jetty's
	 * command line password generation</a>
	 * 
	 * @param username
	 *            the login ID
	 * @param password
	 *            the password
	 * @return a {@linkplain String} array of {@linkplain Password}, Obfuscated,
	 *         {@linkplain Credential.MD5#digest(String)} and
	 *         {@linkplain Credential.Crypt#crypt(String, String)} versions of
	 *         the user name and password
	 */
	public static String[] createRealmProperty(final String username,
			final String password) {
		// http://wiki.eclipse.org/Jetty/Howto/Secure_Passwords
		final Password pw = new Password(password);
		final String obf = Password.obfuscate(pw.toString());
		final String digest = Credential.MD5.digest(pw.toString());
		final String crypt = Credential.Crypt.crypt(username, pw.toString());
		log.info(String.format(
				"Password: %1$s, Obfuscated: %2$s, Digest: %3$s, Crypt: %4$s",
				pw.toString(), obf, digest, crypt));
		return new String[] { pw.toString(), obf, digest, crypt };
	}

	/**
	 * Adds a {@linkplain DigestAuthenticator} via {@linkplain JPALoginService}.
	 * <b>NOTE: digest authentication will be valid until the user closes the
	 * browser. Therefore, there is no default logout mechanism</b>
	 * 
	 * @param handler
	 *            the {@linkplain HandlerWrapper} to add authentication to
	 */
	protected void addAuthentication(final HandlerWrapper handler) {
		final Constraint constraint = new Constraint();
		constraint.setName(Constraint.__DIGEST_AUTH);
		constraint.setRoles(RoleType.names());new ClientCertAuthenticator();
		constraint.setAuthenticate(true);

		final ConstraintMapping cm = new ConstraintMapping();
		cm.setPathSpec("/*");
		cm.setConstraint(constraint);

		final ConstraintSecurityHandler sh = new ConstraintSecurityHandler();
		sh.setAuthenticator(new DigestAuthenticator());
		sh.setConstraintMappings(Arrays.asList(new ConstraintMapping[] { cm }));

//		final JDBCLoginService loginService = new JDBCLoginService();
//		loginService.setConfig("META-INF/realm.properties");

//		 final HashLoginService loginService = new HashLoginService("MyRealm",
//		 "META-INF/realm.properties");
		
		final JPALoginService loginService = new JPALoginService();
		
		sh.setLoginService(loginService);
		sh.setConstraintMappings(Arrays.asList(new ConstraintMapping[] { cm }));

		handler.setHandler(sh);
	}

	/**
	 * Stops the web server
	 */
	public final void stop() {
		try {
			if (server != null && !server.isStopped() && !server.isStopping()) {
				server.stop();
			}
			server.destroy();
			certHolder = null;
		} catch (final Exception e) {
			log.error("Unable to shutdown", e);
		}
	}

	/**
	 * @return the port number of the web server
	 */
	public int getPortNumber() {
		return portNumber;
	}
}
