package org.ugate.service.web;

import java.security.cert.X509Certificate;
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
import org.eclipse.jetty.server.ssl.SslSelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.security.Credential;
import org.eclipse.jetty.util.security.Password;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ugate.service.RoleType;
import org.ugate.service.entity.jpa.Host;

/**
 * Embedded web server
 */
public class WebServer {

	private static final Logger log = LoggerFactory.getLogger(WebServer.class);
	public static final String[] PROTOCOL_INCLUDE = new String[] {
		"TLSv1", "TLSv1.1", "TLSv1.2"
	};
	private final Host host;
	private Server server;
	private HostKeyStore hostKeyStore;
	private final SignatureAlgorithm sa;

	/**
	 * Constructor
	 * 
	 * @param host
	 *            the {@linkplain Host}
	 * @param sa
	 *            the {@linkplain SignatureAlgorithm} to use when the
	 *            {@linkplain X509Certificate} needs to be created/signed
	 * @return the started {@linkplain WebServer}
	 */
	private WebServer(final Host host, final SignatureAlgorithm sa) {
		this.host = host;
		this.sa = sa;
	}

	/**
	 * Starts the web server in a new thread
	 * 
	 * @param host
	 *            the {@linkplain Host}
	 * @param sa
	 *            the {@linkplain SignatureAlgorithm} to use when the
	 *            {@linkplain X509Certificate} needs to be created/signed
	 * @return the started {@linkplain WebServer}
	 */
	public static final WebServer start(final Host host, final SignatureAlgorithm sa) {
		final WebServer webServer = new WebServer(host, sa);
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
			
			// X.509 Setup
			// TODO : Add password control to the key store
			hostKeyStore = HostKeyStore.loadOrCreate(getHost(), "", sa);
			final SslContextFactory sslCnxt = new SslContextFactory();
			//sslCnxt.setCertAlias(hostKeyStore.getAlias());
			sslCnxt.setKeyStore(hostKeyStore.getKeyStore());
			sslCnxt.setKeyStoreType(hostKeyStore.getKeyStore().getType());
			sslCnxt.setKeyStoreProvider(hostKeyStore.getKeyStore().getProvider().getName());
			sslCnxt.setIncludeProtocols(PROTOCOL_INCLUDE);
			sslCnxt.setTrustAll(false);
			final SslSelectChannelConnector sslCnct = new SslSelectChannelConnector(sslCnxt);
			sslCnct.setPort(getHost().getWebPort());
//			final SslSocketConnector sslCnct = new SslSocketConnector(sslCnxt);
//			sslCnct.setPort(getHost().getWebPort());
			// Do not use an HTTP channel selector in addition to the SSL or else 
			// requests will fail with "no cipher suites in common"
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
			hostKeyStore = null;
		} catch (final Exception e) {
			log.error("Unable to shutdown", e);
		}
	}

	/**
	 * @return the {@linkplain Host} of the web server
	 */
	public Host getHost() {
		return host;
	}
}
