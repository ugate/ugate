package org.ugate.service.web.ui;

import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Arguments;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.messageresolver.IMessageResolver;
import org.thymeleaf.messageresolver.MessageResolution;
import org.thymeleaf.resourceresolver.IResourceResolver;
import org.thymeleaf.templateresolver.TemplateResolver;
import org.ugate.resources.RS;
import org.ugate.resources.RS.KEY;
import org.ugate.service.web.UGateAjaxUpdaterServlet;
import org.ugate.service.web.UGateWebSocketServlet;

/**
 * Web application entry point
 */
public enum WebApplication {
	DFLT;

	private static final Logger log = LoggerFactory.getLogger(WebApplication.class);
	private final TemplateEngine templateEngine;

	/**
	 * The {@link BaseController} path 
	 */
	public static enum ControllerResource {
		LOGIN("/login", new LoginController()), 
		LOGOUT("/logout", new BaseController() {

			@Override
			protected RequiredValues processContext(final HttpServletRequest req,
					final HttpServletResponse res, final ServletContext servletContext,
					final WebContext ctx) throws Throwable {
				req.getSession().invalidate();
			    final String urlWithSessionID = LOGIN.path();//res.encodeRedirectURL(LOGIN.path());
			    res.sendRedirect(urlWithSessionID);
				return null;
			}

			@Override
			public boolean hasHtmlSource() {
				return false;
			};
		}), 
		ERROR("/error", new ErrorController()),
		INDEX("/", new IndexController());

		private final String path;
		private final BaseController baseController;

		/**
		 * Constructor
		 * 
		 * @param path
		 *            the {@link #path()}
		 * @param baseController
		 *            the {@link BaseController}
		 */
		private ControllerResource(final String path,
				final BaseController baseController) {
			this.path = path;
			this.baseController = baseController;
		}

		/**
		 * @return the path to the
		 */
		public String path() {
			return path;
		}

		/**
		 * @return the {@link BaseController}
		 */
		public BaseController getController() {
			return baseController;
		}

		/**
		 * Gets a {@link ControllerResource} based upon a path. The order of the
		 * {@link ControllerResource} is the order that the path will be checked
		 * against.
		 * 
		 * @param path
		 *            the {@link #path()}
		 * @return the {@link ControllerResource}
		 */
		public static ControllerResource pathValueOf(final String path) {
			if (path != null && !path.isEmpty()) {
				for (final ControllerResource wcr : values()) {
					if (path.startsWith(wcr.path())) {
						return wcr;
					}
				}
			}
			return null;
		}
	}

	/**
	 * Constructor
	 */
	private WebApplication() {
		final TemplateResolver templateResolver = new TemplateResolver();
		templateResolver.setResourceResolver(new IResourceResolver() {
			
			@Override
			public InputStream getResourceAsStream(
					TemplateProcessingParameters templateProcessingParameters,
					String resourceName) {
				return RS.stream(resourceName);
			}
			
			@Override
			public String getName() {
				return RS.class.getSimpleName();
			}
		});
		templateResolver.setTemplateMode("HTML5");
		// This will convert "home" to "/WEB-INF/templates/home.html"
		//templateResolver.setPrefix("/WEB-INF/templates/");
		templateResolver.setSuffix(".html");
		// Set template cache TTL to 1/2 hour. If not set, entries would live in
		// cache until expelled by LRU
		templateResolver.setCacheTTLMs(Long.valueOf(1800000L));
		// Cache is set to true by default. Set to false if you want templates
		// to be automatically updated when modified.
		templateResolver.setCacheable(true);

		templateEngine = new TemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);
		templateEngine.setMessageResolver(new IMessageResolver() {

			@Override
			public MessageResolution resolveMessage(final Arguments arguments,
					final String key, final Object[] messageParameters) {
				try {
					return new MessageResolution(RS.rbLabel(
							KEY.keyValueOf(key), messageParameters));
				} catch (final NullPointerException e) {
					throw new IllegalArgumentException(String.format(
							"Unable to find \"%1$s\" in %2$s", key,
							RS.KEY.class.getSimpleName()), e);
				}
			}

			@Override
			public void initialize() {
				// nothing to initialize
			}

			@Override
			public Integer getOrder() {
				return 1;
			}

			@Override
			public String getName() {
				return RS.class.getSimpleName();
			}
		});
		//templateEngine.addDialect(new LayoutDialect());
	}

	/**
	 * Resolves a {@link ControllerResource} using the
	 * {@link HttpServletRequest#getRequestURI()} bound on a
	 * {@link ControllerResource}
	 * 
	 * @param request
	 *            the {@link HttpServletRequest} to resolve a
	 *            {@link BaseController} for
	 * @return the {@link ControllerResource} (or null when none exist)
	 */
	public ControllerResource resolve(final HttpServletRequest request) {
		try {
			final String reqPath = getRequestPath(request);
			if (reqPath.indexOf(UGateAjaxUpdaterServlet.class.getSimpleName()) > -1
					|| reqPath.indexOf(UGateWebSocketServlet.class.getSimpleName()) > -1) {
				return null;
			}
			return ControllerResource.pathValueOf(reqPath);
		} catch (final Throwable t) {
			log.info(String.format("Unable to get %1$s for URI %2$s",
					ControllerResource.class.getSimpleName(),
					request.getRequestURI()));
			return null;
		}
	}

	/**
	 * @return the {@link TemplateEngine}
	 */
	public TemplateEngine getTemplateEngine() {
		return templateEngine;
	}

	/**
	 * Gets a {@link BaseController}'s path from an {@link HttpServletRequest}
	 * 
	 * @param request
	 *            the {@link HttpServletRequest}
	 * @return the path
	 */
	private static String getRequestPath(final HttpServletRequest request) {
		final String requestURI = request.getRequestURI();
		final String contextPath = request.getContextPath();
		if (requestURI.startsWith(contextPath)) {
			return requestURI.substring(contextPath.length());
		}
		return requestURI;
	}
}
