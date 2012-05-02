package org.ugate.service.web;

import java.util.EnumSet;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.DispatcherType;

import org.eclipse.jetty.plus.servlet.ServletHandler;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.xml.XmlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ugate.resources.RS;
import org.ugate.service.ServiceManager;

//import org.eclipse.jetty.server.Connector;
//import org.eclipse.jetty.server.Server;
//import org.eclipse.jetty.server.nio.SelectChannelConnector;
//import org.eclipse.jetty.util.resource.Resource;
//import org.eclipse.jetty.xml.XmlConfiguration;

public class WebServer {

	private static final Logger log = LoggerFactory.getLogger(WebServer.class);
	private final int portNumber;
	private Server server;
//	private static Tomcat server;
	
	private WebServer(final int portNumber) {
		this.portNumber = portNumber <=0 ? 80 : portNumber;
	}
	
	/**
	 * Starts the web server
	 */
	public static final WebServer start(final int portNumber) {
		final WebServer webServer = new WebServer(portNumber);
		final Thread webServerAgent = new Thread(Thread.currentThread().getThreadGroup(), new Runnable() {
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
	 * <p>{@linkplain Tomcat#addContext(String, String)}
	 * <ol>
	 * <li>Completely programmatic</li>
	 * <li>You must do everything</li>
	 * <li>No default servlet</li>
	 * <li>No JSP servlet</li>
	 * <li>No welcome files</li>
	 * <li>No web.xml parsing</li>
	 * </ol>
	 * </p>
	 * <p>{@linkplain Tomcat#addWebapp(String, String)}
	 * <ol>
	 * <li>Emulates $CATALINA_BASE/conf/web.xml</li>
	 * <li>Default servlet present</li>
	 * <li>JSP servlet present</li>
	 * <li>Welcome files configured</li>
	 * <li>MIME mappings configured</li>
	 * <li>WEB-INF/web.xml parsed</li>
	 * </ol>
	 * </p>
	 */
	protected final void startServer() {
		try {
			final Resource serverXml = Resource.newSystemResource("META-INF/jetty.xml");
			final XmlConfiguration configuration = new XmlConfiguration(serverXml.getInputStream());
			server = (Server) configuration.configure();
			//server.addBean(new org.eclipse.jetty.plus.jndi.Transaction(ServiceManager.TM));
			// set the connector based upon user settings
			final SelectChannelConnector defaultConnnector = new SelectChannelConnector();
			defaultConnnector.setPort(9080);
			server.setConnectors(new Connector[] { defaultConnnector });
			
			final EnumSet<DispatcherType> dispatchers = EnumSet.range(DispatcherType.FORWARD, DispatcherType.ERROR);
			ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
			context.setContextPath("/");
			context.addFilter(TransactionFilter.class, "/*", dispatchers);
			context.addServlet(DefaultAppServlet.class, "/");
			server.setHandler(context);
			
			server.setDumpAfterStart(true);
			server.start();
			server.join();
			
			// Tomcat currently doesn't allow for a self-contained executable JAR
//			server = new Tomcat();
//			server.setPort(9080);
//			server.enableNaming();
//			
//			//final File tempDirectory = Paths.get(System.getProperty("java.io.tmpdir"));
//			final Path rootDir = Paths.get(System.getProperty("user.home"), RS.rbLabel("persistent.unit"));
//			final Path tempDir = rootDir.resolve("temp");
//			final Path appBase = rootDir.resolve("webapps");
//			final Path appBaseWebInf = appBase.resolve("WEB-INF");
//			final Path appBaseMetaInf = appBase.resolve("META-INF");
//			Files.createDirectories(tempDir);
//			Files.createDirectories(appBaseWebInf);
//			Files.createDirectories(appBaseMetaInf);
//			
//			final Path sourcePath = Paths.get(WebServer.class.getProtectionDomain().getCodeSource().getLocation().toURI());
//			final Path sourceWebXml = sourcePath.resolve("WEB-INF").resolve("web.xml");
//			final Path sourceContextXml = sourcePath.resolve("META-INF").resolve("context.xml");
//			final Path sourcePersistenceXml = sourcePath.resolve("META-INF").resolve("persistence.xml");
//			Files.copy(sourceWebXml, appBaseWebInf.resolve("web.xml"), StandardCopyOption.REPLACE_EXISTING);
//			Files.copy(sourceContextXml, appBaseMetaInf.resolve("context.xml"), StandardCopyOption.REPLACE_EXISTING);
//			Files.copy(sourcePersistenceXml, appBaseMetaInf.resolve("persistence.xml"), StandardCopyOption.REPLACE_EXISTING);
//			
//			// set the temporary directory where compilation files will be stored (i.e. JSPs, etc.)
//			server.setBaseDir(tempDir.toAbsolutePath().toString());
//			//server.getHost().setAppBase(appBase.toAbsolutePath().toString());
//			final StandardContext rootContext = (StandardContext) server.addWebapp("/", sourcePath.toAbsolutePath().toString()); // server.addContext("/", "");
//			rootContext.setDefaultWebXml(appBaseWebInf.resolve("web.xml").toAbsolutePath().toString());
//			rootContext.setDefaultContextXml(appBaseMetaInf.resolve("context.xml").toAbsolutePath().toString());
//			
//			Wrapper defaultServlet = rootContext.createWrapper();
//			defaultServlet.setName(DefaultAppServlet.class.getSimpleName() + "-test");
//			defaultServlet.setServletClass(DefaultAppServlet.class.toString());
//			defaultServlet.addInitParameter("debug", "0");
//			defaultServlet.addInitParameter("listings", "false");
//			defaultServlet.setLoadOnStartup(1);
//			rootContext.addChild(defaultServlet);
//			rootContext.addServletMapping("/", DefaultAppServlet.class.getSimpleName() + "-test");
//
//			rootContext.setConfigFile(appBaseMetaInf.resolve("context.xml").toUri().toURL());
//			log.info(String.format("Hosting %1$s from web directory: %2$s", server.getClass().getSimpleName(), 
//					server.getHost().getAppBase()));
//			Tomcat.addServlet(context, DefaultAppServlet.class.getSimpleName(), new DefaultAppServlet());
//			context.addServletMapping("/*", DefaultAppServlet.class.getSimpleName());
//			server.start();
//			server.getServer().await();
		} catch (final Throwable e) {
			log.error("Unable to start web server", e);
		} finally {

		}
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
		} catch (final Exception e) {
			log.error("Unable to shutdown", e);
		}
	}
	
	public int getPortNumber() {
		return portNumber;
	}
}
