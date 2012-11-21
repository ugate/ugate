package org.ugate.service.web.ui;

import static org.ugate.service.web.WebServer.VAR_CONTENT_NAME;
import static org.ugate.service.web.WebServer.VAR_FOOTER_NAME;
import static org.ugate.service.web.WebServer.VAR_HEADER_NAME;
import static org.ugate.service.web.WebServer.VAR_REMOTE_USER;
import static org.ugate.service.web.WebServer.VAR_TITLE_NAME;
import static org.ugate.service.web.WebServer.VAR_URI_WEB_SOCKET_NAME;
import static org.ugate.service.web.WebServer.VAR_URL_AJAX_UPDATE_NAME;
import static org.ugate.service.web.WebServer.VAR_URL_NAME;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes.Name;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.ugate.resources.RS;
import org.ugate.resources.RS.KEY;
import org.ugate.service.ServiceProvider;
import org.ugate.service.entity.jpa.Actor;
import org.ugate.service.web.UGateAjaxUpdaterServlet;
import org.ugate.service.web.UGateWebSocketServlet;

/**
 * Base MVC Controller
 */
public abstract class BaseController {

	private static final Logger log = LoggerFactory
			.getLogger(BaseController.class);
	protected static final String POSTFIX_LABEL= "_LABEL";
	private static final Map<Class<?>, Class<?>> PRIMS = new HashMap<>();
	static {
		PRIMS.put(boolean.class, Boolean.class);
		PRIMS.put(char.class, Character.class);
		PRIMS.put(double.class, Double.class);
		PRIMS.put(float.class, Float.class);
		PRIMS.put(long.class, Long.class);
		PRIMS.put(int.class, Integer.class);
		PRIMS.put(short.class, Short.class);
		PRIMS.put(long.class, Long.class);
		PRIMS.put(byte.class, Byte.class);
	}

	/**
	 * Processes the view using the {@link TemplateEngine}
	 * 
	 * @param req
	 *            the {@link HttpServletRequest}
	 * @param res
	 *            the {@link HttpServletResponse}
	 * @param servletContext
	 *            the {@link ServletContext}
	 * @param templateEngine
	 *            the {@link TemplateEngine}
	 * @throws Throwable
	 *             any exception thrown during processing
	 */
	public final void process(final HttpServletRequest req,
			final HttpServletResponse res, final ServletContext servletContext,
			final TemplateEngine templateEngine) throws Throwable {
		try {
			if (!hasHtmlSource()) {
				processContext(req, res, servletContext, null);
			} else {
				final WebContext ctx = new WebContext(req, res, servletContext,
						req.getLocale());
				ctx.setVariable(VAR_CONTENT_NAME, getPageName());
				ctx.setVariable(VAR_URL_NAME, '/' + getPageName());
				ctx.setVariable(VAR_URL_AJAX_UPDATE_NAME, '/' + UGateAjaxUpdaterServlet.class.getSimpleName());
				ctx.setVariable(VAR_URI_WEB_SOCKET_NAME, '/' + UGateWebSocketServlet.class.getSimpleName());
				ctx.setVariable(VAR_REMOTE_USER, req.getRemoteUser());
				final RequiredValues rvs = processContext(req, res, servletContext, ctx);
				ctx.setVariable(VAR_TITLE_NAME,
						rvs != null && rvs.getTitle() != null ? rvs.getTitle()
								: RS.rbLabel(KEY.APP_TITLE));
				ctx.setVariable(
						VAR_HEADER_NAME,
						rvs != null && rvs.getHeader() != null ? rvs
								.getHeader() : RS.rbLabel(
								KEY.APP_TITLE_USER,
								(req.getRemoteUser() != null ? req
										.getRemoteUser() : "")));
				ctx.setVariable(
						VAR_FOOTER_NAME,
						rvs != null && rvs.getFooter() != null ? rvs
								.getFooter() : RS.rbLabel(KEY.APP_TITLE));
				templateEngine.process(getPageName(BaseController.class), ctx,
						res.getWriter());
			}
		} catch (final Throwable t) {
			log.warn(String
					.format("Unable to process request for %1$s (remote user: %2$s) Reason: %3$s",
							req.getRequestURL(), req.getRemoteUser(),
							t.getMessage()));
			throw t;
		}
	}

	/**
	 * Processes the view using the {@link WebContext}
	 * 
	 * @param req
	 *            the {@link HttpServletRequest}
	 * @param res
	 *            the {@link HttpServletResponse}
	 * @param servletContext
	 *            the {@link ServletContext}
	 * @param ctx
	 *            the {@link WebContext}
	 * @return the {@link RequiredValues} for the {@link BaseController} (null
	 *         to use the default values)
	 * @throws Throwable
	 *             any exception thrown during processing
	 */
	protected abstract RequiredValues processContext(final HttpServletRequest req,
			final HttpServletResponse res, final ServletContext servletContext,
			final WebContext ctx) throws Throwable;

	/**
	 * @return true if the {@link BaseController} processes any HTML sources
	 *         (default is <code>true</code>)
	 */
	public boolean hasHtmlSource() {
		return true;
	}

	/**
	 * Gets a {@link HttpServletRequest#getParameter(String)} and attempts to
	 * convert it to the specified class
	 * 
	 * @param req
	 *            the {@link HttpServletRequest}
	 * @param name
	 *            the parameter name
	 * @param type
	 *            the class type the parameter will be converted to
	 * @return the converted parameter (or null when it cannot be converted or
	 *         does not exist)
	 */
	protected <T> T getParameter(final HttpServletRequest req,
			final String name, final Class<T> type) {
		final String param = req.getParameter(name);
		if (param == null || param.isEmpty()) {
			return null;
		}
		try {
			return valueOf(type, param);
		} catch (final Throwable t) {
			log.info(String.format("Unable to convert %1$s to %2$s", param,
					type), t);
			return null;
		}
	}

	/**
	 * Finds an {@linkplain Actor} based upon the
	 * {@linkplain HttpServletRequest#getRemoteUser()}
	 * 
	 * @param req
	 *            the {@link HttpServletRequest}
	 * @return the {@linkplain Actor} or null when none can be found
	 */
	protected Actor findActor(final HttpServletRequest req) {
		final String ru = req.getRemoteUser();
		if (ru == null) {
			return null;
		}
		return ServiceProvider.IMPL.getCredentialService().getActor(ru);
	}

	/**
	 * @return the HTML page name
	 */
	public String getPageName() {
		return getPageName(this.getClass());
	}

	/**
	 * @param clazz
	 *            the class to get the HTML page {@link Name} for
	 * @return the HTML page name
	 */
	public static <T extends BaseController> String getPageName(final Class<T> clazz) {
		final String className = clazz.getSimpleName();
		if (className.indexOf("Controller") > -1) {
			return className.replace("Controller", "").toLowerCase();
		}
		return className.toLowerCase();
	}

	/**
	 * Attempts to invoke a <code>valueOf</code> using the specified class
	 * 
	 * @param valueOfClass
	 *            the class to attempt to invoke a <code>valueOf</code> method
	 *            on
	 * @param value
	 *            the value to invoke the <code>valueOf</code> method on
	 * @return the result (null if the operation fails)
	 */
	@SuppressWarnings("unchecked")
	protected static <VT> VT valueOf(final Class<VT> valueOfClass,
			final Object value) {
		if (value != null && String.class.isAssignableFrom(valueOfClass)) {
			return (VT) value.toString();
		}
		final Class<?> clazz = PRIMS.containsKey(valueOfClass) ? PRIMS
				.get(valueOfClass) : valueOfClass;
		MethodHandle mh1 = null;
		try {
			mh1 = MethodHandles.lookup().findStatic(clazz, "valueOf",
					MethodType.methodType(clazz, String.class));
		} catch (final Throwable t) {
			// class doesn't support it- do nothing
		}
		if (mh1 != null) {
			try {
				return (VT) mh1.invoke(value);
			} catch (final Throwable t) {
				throw new IllegalArgumentException(String.format(
						"Unable to invoke valueOf on %1$s using %2$s", value,
						valueOfClass), t);
			}
		}
		return null;
	}

	/**
	 * Required values for {@link BaseController}
	 */
	protected static class RequiredValues {
		private final String title;
		private final String header;
		private final String footer;

		/**
		 * {@link RequiredValues} for {@link BaseController}
		 * 
		 * @param title
		 *            the {@link #getTitle()}
		 * @param header
		 *            the {@link #getHeader()}
		 * @param footer
		 *            the {@link #getFooter()}
		 */
		public RequiredValues(final String title, final String header,
				final String footer) {
			this.title = title;
			this.header = header;
			this.footer = footer;
		}

		/**
		 * @return the title of the HTML page
		 */
		public String getTitle() {
			return title;
		}

		/**
		 * @return the header of the HTML page
		 */
		public String getHeader() {
			return header;
		}

		/**
		 * @return the footer text of the HTML page
		 */
		public String getFooter() {
			return footer;
		}
	}
}
