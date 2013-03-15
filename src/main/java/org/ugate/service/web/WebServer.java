package org.ugate.service.web;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpScheme;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.authentication.FormAuthenticator;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.security.Credential;
import org.eclipse.jetty.util.security.Password;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ugate.UGateEvent;
import org.ugate.UGateEvent.Type;
import org.ugate.UGateKeeper;
import org.ugate.service.entity.ActorType;
import org.ugate.service.entity.EntityExtractor;
import org.ugate.service.entity.RoleType;
import org.ugate.service.entity.jpa.Actor;
import org.ugate.service.web.ui.WebApplication;
import org.ugate.service.web.ui.WebFilter;

/**
 * Embedded web {@linkplain Server}
 */
public class WebServer {

	private static final Logger log = LoggerFactory.getLogger(WebServer.class);
	public static final String VAR_REMOTE_USER = "remoteUser";
	public static final String VAR_CONTENT_NAME = "content";
	public static final String VAR_TITLE_NAME = "title";
	public static final String VAR_HEADER_NAME = "header";
	public static final String VAR_FOOTER_NAME = "footer";
	public static final String VAR_URL_NAME = "url";
	public static final String VAR_URL_AJAX_UPDATE_NAME = "ajaxUpdateUrl";
	public static final String VAR_URI_WEB_SOCKET_NAME = "webSocketUri";
	public static final String VAR_REMOTE_NODES_NAME = "remoteNodes";
	public static final String VAR_REMOTE_NODE_READING_NAME = "rnr";
	public static final String VAR_REMOTE_NODE_NAME = "rn";
	public static final String VAR_COMMAND_NAME = "command";
	public static final String VAR_ACTION_NAME = "action";
	public static final String VAR_ACTION_CONNECT_NAME = "connect";
	public static final String[] PROTOCOL_INCLUDE = new String[] { "TLSv1",
			"TLSv1.1", "TLSv1.2" };
	private Server server;
	private HostKeyStore hostKeyStore;
	private final SignatureAlgorithm sa;
	private final EntityExtractor<Actor> actorExtractor;

	/**
	 * Constructor
	 * 
	 * @param actorExtractor
	 *            the {@link EntityExtractor} for the {@link Actor}
	 * @param sa
	 *            the {@linkplain SignatureAlgorithm} to use when the
	 *            {@linkplain X509Certificate} needs to be created/signed
	 * @return the started {@linkplain WebServer}
	 */
	private WebServer(final EntityExtractor<Actor> actorExtractor,
			final SignatureAlgorithm sa) {
		this.actorExtractor = actorExtractor;
		this.sa = sa;
	}

	/**
	 * Starts the {@linkplain WebServer} in a new {@linkplain Thread}
	 * 
	 * @param actorExtractor
	 *            the {@link EntityExtractor} for the {@link Actor} (owner) of
	 *            the {@link WebServer}
	 * @param sa
	 *            the {@linkplain SignatureAlgorithm} to use when the
	 *            {@linkplain X509Certificate} needs to be created/signed
	 * @return the started {@linkplain WebServer}
	 */
	public static final WebServer start(
			final EntityExtractor<Actor> actorExtractor,
			final SignatureAlgorithm sa) {
		final WebServer webServer = new WebServer(actorExtractor, sa);
		final Thread webServerAgent = new Thread(Thread.currentThread()
				.getThreadGroup(), new Runnable() {
			@Override
			public void run() {
				try {
					webServer.startServer();
				} catch (final Throwable t) {
					log.error("Failed to start web server", t);
				}
			}
		}, WebServer.class.getSimpleName() + '-' + System.currentTimeMillis());
		webServerAgent.setDaemon(true);
		webServerAgent.start();
		return webServer;
	}

	/**
	 * Starts the {@linkplain WebServer}
	 */
	protected final void startServer() {
		// Additional reference examples can be found at:
		// http://git.eclipse.org/c/jetty/org.eclipse.jetty.project.git/tree/examples/embedded/src/main/java/org/eclipse/jetty/embedded
		try {
			UGateKeeper.DEFAULT.notifyListeners(new UGateEvent<>(
					WebServer.this, Type.WEB_INITIALIZE, false));
			// Get server from configuration file
			// final Resource serverXml =
			// Resource.newSystemResource("META-INF/jetty.xml");
			// final XmlConfiguration configuration = new
			// XmlConfiguration(serverXml.getInputStream());
			// server = (Server) configuration.configure();
			if (actorExtractor.extract() == null
					|| actorExtractor.extract().getId() <= 0) {
				throw new NullPointerException(Actor.class.getName()
						+ " cannot be null");
			} else if (actorExtractor.extract().getId() <= 0) {
				throw new IllegalArgumentException(
						String.format("%1$s#%2$s cannot be %3$s",
								Actor.class.getName(), ActorType.ID.getKey(),
								actorExtractor.extract().getId()));
			}
			server = new Server();
			server.addLifeCycleListener(new LifeCycle.Listener() {
				@Override
				public void lifeCycleStopping(final LifeCycle lifeCycle) {
					UGateKeeper.DEFAULT.notifyListeners(new UGateEvent<>(
							WebServer.this, Type.WEB_DISCONNECTING, false));
				}

				@Override
				public void lifeCycleStopped(final LifeCycle lifeCycle) {
					UGateKeeper.DEFAULT.notifyListeners(new UGateEvent<>(
							WebServer.this, Type.WEB_DISCONNECTED, false));
				}

				@Override
				public void lifeCycleStarting(final LifeCycle lifeCycle) {
					UGateKeeper.DEFAULT.notifyListeners(new UGateEvent<>(
							WebServer.this, Type.WEB_CONNECTING, false));
				}

				@Override
				public void lifeCycleStarted(final LifeCycle lifeCycle) {
					UGateKeeper.DEFAULT.notifyListeners(new UGateEvent<>(
							WebServer.this, Type.WEB_CONNECTED, false));
				}

				@Override
				public void lifeCycleFailure(final LifeCycle lifeCycle,
						final Throwable t) {
					log.error(
							String.format("%1$s failure",
									LifeCycle.class.getName()), t);
					UGateKeeper.DEFAULT.notifyListeners(new UGateEvent<>(
							WebServer.this, Type.WEB_CONNECT_FAILED, false, t
									.getMessage()));
				}
			});

			// SSL for HTTPS and SPDY (X.509 Setup)
			// TODO : Add password control to the key store
			hostKeyStore = HostKeyStore.loadOrCreate(actorExtractor.extract()
					.getHost(), "", sa);
			final SslContextFactory sslCnxt = new SslContextFactory();
			// sslCnxt.setCertAlias(hostKeyStore.getAlias());
			sslCnxt.setKeyStore(hostKeyStore.getKeyStore());
			sslCnxt.setKeyStoreType(hostKeyStore.getKeyStore().getType());
			sslCnxt.setKeyStoreProvider(hostKeyStore.getKeyStore()
					.getProvider().getName());
			sslCnxt.setIncludeProtocols(PROTOCOL_INCLUDE);
			sslCnxt.setTrustAll(false);

			// HTTP Configuration
			final HttpConfiguration httpConf = new HttpConfiguration();
			httpConf.setSecureScheme(HttpScheme.HTTPS.asString());
			httpConf.setSecurePort(actorExtractor.extract().getHost()
					.getWebPort());
			// httpConf.setOutputBufferSize(32768);

			// HTTP connector
			final ServerConnector http = new ServerConnector(server,
					new HttpConnectionFactory(httpConf));
			http.setHost(actorExtractor.extract().getHost().getWebHostLocal());
			http.setPort(actorExtractor.extract().getHost().getWebPortLocal());
			// http.setIdleTimeout(30000);

			// HTTPS Configuration
			final HttpConfiguration httpsConf = new HttpConfiguration(httpConf);
			httpsConf.addCustomizer(new SecureRequestCustomizer());

			// HTTPS connector
			final ServerConnector https = new ServerConnector(server,
					new SslConnectionFactory(sslCnxt, "http/1.1"),
					new HttpConnectionFactory(httpsConf));
			https.setHost(actorExtractor.extract().getHost().getWebHost());
			https.setPort(actorExtractor.extract().getHost().getWebPort());

			server.setConnectors(new Connector[] { http, https });

			// Serve via servlet
			final EnumSet<DispatcherType> dispatchers = EnumSet.range(
					DispatcherType.FORWARD, DispatcherType.ERROR);
			final ServletContextHandler context = new ServletContextHandler(
					ServletContextHandler.SESSIONS
							| ServletContextHandler.SECURITY);

			// final SessionIdManager idManager = new HashSessionIdManager();
			// final SessionManager sessionManager = new HashSessionManager();
			// SessionHandler sessionHandler = new
			// SessionHandler(sessionManager);
			// sessionManager.setSessionIdManager(idManager);
			// sessionManager.setSessionHandler(sessionHandler);
			// handler.setSessionHandler(sessionHandler);

			context.setContextPath("/");
			final FilterHolder fh = new FilterHolder(WebFilter.class);
			// let the filter know who to use to auto authenticate against when
			// accessed by host machine
			fh.setInitParameter(ActorType.ID.name(),
					String.valueOf(actorExtractor.extract().getId()));
			context.addFilter(fh, "/*", dispatchers);
			final ServletHolder sh = new ServletHolder(
					UGateWebSocketServlet.class);
			context.addServlet(sh,
					"/" + UGateWebSocketServlet.class.getSimpleName());
			final ServletHolder sh2 = new ServletHolder(
					UGateAjaxUpdaterServlet.class);
			context.addServlet(sh2, "/*");
			context.setErrorHandler(new ErrorHandler() {
				@Override
				public void handle(String target, Request baseRequest,
						HttpServletRequest request, HttpServletResponse response)
						throws IOException {
					// delegate errors to the filter
					final String reason = (response instanceof Response) ? ((Response) response)
							.getReason() : null;
					WebFilter.handleErrorPage(request, response,
							baseRequest.getServletContext(),
							response.getStatus(), reason);
				}
			});

			// Serve files from internal resource location
			// final org.eclipse.jetty.webapp.WebAppContext handler = new
			// org.eclipse.jetty.webapp.WebAppContext();
			// final String webappDirInsideJar =
			// this.getClass().getResource("webapp").toExternalForm();
			// handler.setWar(webappDirInsideJar);

			addSecurityHandler(context);

			// server.setDumpAfterStart(true);
			server.start();
			server.join();
		} catch (final Throwable e) {
			log.error("Unable to start web server", e);
			UGateKeeper.DEFAULT.notifyListeners(new UGateEvent<>(
					WebServer.this, Type.WEB_INITIALIZE_FAILED, false));
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
	 * Adds a {@linkplain FormAuthenticator} via {@linkplain JPALoginService}.
	 * 
	 * @param context
	 *            the {@linkplain HandlerWrapper} to add authentication to
	 */
	protected void addSecurityHandler(final ServletContextHandler context) {
		// final JDBCLoginService loginService = new JDBCLoginService();
		// loginService.setConfig("META-INF/realm.properties");
		// final HashLoginService loginService = new HashLoginService("MyRealm",
		// "META-INF/realm.properties");
		final JPALoginService loginService = new JPALoginService();
		// server.addBean(loginService);

		final ConstraintSecurityHandler security = new ConstraintSecurityHandler();

		final Constraint constraint = new Constraint();
		constraint.setName(Constraint.__FORM_AUTH);
		constraint.setAuthenticate(true);
		constraint.setRoles(RoleType.names());

		final ConstraintMapping cm = new ConstraintMapping();
		cm.setPathSpec("/*");
		cm.setConstraint(constraint);

		security.setConstraintMappings(Arrays
				.asList(new ConstraintMapping[] { cm }));
		final FormAuthenticator fa = new FormAuthenticator(
				WebApplication.ControllerResource.LOGIN.path(),
				WebApplication.ControllerResource.ERROR.path(), false);
		security.setAuthenticator(fa);
		// security.setAuthenticator(new DigestAuthenticator());
		security.setLoginService(loginService);

		context.setSecurityHandler(security);
		server.setHandler(context);
	}

	/**
	 * Stops the {@linkplain WebServer}
	 */
	public final void stop() {
		try {
			if (server != null && !server.isStopped() && !server.isStopping()) {
				server.stop();
			}
			server.destroy();
			hostKeyStore = null;
		} catch (final Throwable t) {
			log.error("Unable to shutdown web server", t);
		}
	}

	/**
	 * @return {@linkplain Server#isRunning()}
	 */
	public final boolean isRunning() {
		return server != null && server.isRunning();
	}

	/**
	 * @return the {@link EntityExtractor} for the {@link Actor} (owner) of the
	 *         {@link WebServer}
	 */
	public EntityExtractor<Actor> getActorExtractor() {
		return actorExtractor;
	}
}
