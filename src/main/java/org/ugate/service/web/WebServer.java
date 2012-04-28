package org.ugate.service.web;

import java.nio.file.Paths;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.xml.XmlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import org.eclipse.jetty.server.Connector;
//import org.eclipse.jetty.server.Server;
//import org.eclipse.jetty.server.nio.SelectChannelConnector;
//import org.eclipse.jetty.util.resource.Resource;
//import org.eclipse.jetty.xml.XmlConfiguration;

public class WebServer {

	private static final Logger log = LoggerFactory.getLogger(WebServer.class);
	private static Server server;
//	private static Tomcat server;

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
	public static final void start() {
		try {
			String jtaService = System.getProperty("com.atomikos.icatch.service");
			System.setProperty("com.atomikos.icatch.service", "com.atomikos.icatch.standalone.UserTransactionServiceFactory");
			System.setProperty("com.atomikos.icatch.file", 
					Paths.get(WebServer.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toAbsolutePath().toString());
			jtaService = System.getProperty("com.atomikos.icatch.service");
			log.info(jtaService + " path: " + System.getProperty("com.atomikos.icatch.file"));
			
			final Resource serverXml = Resource.newSystemResource("META-INF/jetty.xml");
			final XmlConfiguration configuration = new XmlConfiguration(serverXml.getInputStream());
			server = (Server) configuration.configure();
			// set the connector based upon user settings
			final SelectChannelConnector defaultConnnector = new SelectChannelConnector();
			defaultConnnector.setPort(9080);
			defaultConnnector.setMaxIdleTime(30000);
			defaultConnnector.setRequestHeaderSize(8192);
			server.setConnectors(new Connector[] { defaultConnnector });
			server.setHandler(new DefaultHandler());
			server.setStopAtShutdown(true);
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
//			defaultServlet.setName(DefaultHandler.class.getSimpleName() + "-test");
//			defaultServlet.setServletClass(DefaultHandler.class.toString());
//			defaultServlet.addInitParameter("debug", "0");
//			defaultServlet.addInitParameter("listings", "false");
//			defaultServlet.setLoadOnStartup(1);
//			rootContext.addChild(defaultServlet);
//			rootContext.addServletMapping("/", DefaultHandler.class.getSimpleName() + "-test");
//
//			rootContext.setConfigFile(appBaseMetaInf.resolve("context.xml").toUri().toURL());
//			log.info(String.format("Hosting %1$s from web directory: %2$s", server.getClass().getSimpleName(), 
//					server.getHost().getAppBase()));
//			Tomcat.addServlet(context, DefaultHandler.class.getSimpleName(), new DefaultHandler());
//			context.addServletMapping("/*", DefaultHandler.class.getSimpleName());
//			server.start();
//			server.getServer().await();
		} catch (final Throwable e) {
			log.error("Unable to start web server", e);
		}
	}

	public static final void stop() {
		try {
			if (server != null && !server.isStopped() && !server.isStopping()) {
				server.stop();
			}
			server.destroy();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
