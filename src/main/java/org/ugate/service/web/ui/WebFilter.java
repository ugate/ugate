package org.ugate.service.web.ui;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.ugate.service.ServiceProvider;
import org.ugate.service.entity.ActorType;
import org.ugate.service.entity.jpa.Actor;
import org.ugate.service.web.UGateAjaxUpdaterServlet;
import org.ugate.service.web.UGateWebSocketServlet;
import org.ugate.service.web.ui.WebApplication.ControllerResource;

/**
 * Global web {@link Filter} that handles routing of {@link BaseController}s
 */
public class WebFilter implements Filter {

	private static final Logger log = LoggerFactory.getLogger(WebFilter.class);
	private ServletContext servletContext;
	private int actorId;

	/**
	 * Processes a {@link HttpServletRequest} using the {@link WebApplication}'s
	 * {@link TemplateEngine} using a {@link BaseController} that has a mounted
	 * path that is being requested
	 * 
	 * @param req
	 *            the {@link HttpServletRequest}
	 * @param res
	 *            the {@link HttpServletResponse}
	 * @return true when processed
	 * @throws Throwable
	 *             thrown when the request cannot be processed
	 */
	private boolean process(final HttpServletRequest req,
			final HttpServletResponse res) throws Throwable {
		final ControllerResource ctrlRes = WebApplication.DFLT.resolve(req);
		if (ctrlRes == null) {
			return false;
		}
		final TemplateEngine templateEngine = WebApplication.DFLT
				.getTemplateEngine();

		res.setContentType("text/html;charset=UTF-8");
		res.setHeader("Pragma", "no-cache");
		res.setHeader("Cache-Control", "no-cache");
		res.setDateHeader("Expires", 0);

		ctrlRes.getController().process(req, res, this.servletContext,
				templateEngine);
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doFilter(final ServletRequest request,
			final ServletResponse response, final FilterChain chain)
			throws IOException, ServletException {
		try {
			final HttpServletRequest req = (HttpServletRequest) request;
			final HttpServletResponse res = (HttpServletResponse) response;
			boolean processed = false;
			if (!isLoggedIn(req)) {
				// authentication required
				if (hasOrigURI(req, UGateWebSocketServlet.class.getSimpleName())
						|| hasOrigURI(req,
								UGateAjaxUpdaterServlet.class.getSimpleName())) {
					res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				} else {
					ControllerResource.LOGIN.getController().process(req,
							res, servletContext,
							WebApplication.DFLT.getTemplateEngine());
				}
			} else if (hasOrigURI(req, ControllerResource.LOGIN.path())) {
				// user is logged in, but is requesting the login page
				ControllerResource.INDEX.getController().process(req,
						res, servletContext,
						WebApplication.DFLT.getTemplateEngine());
			} else if (!(processed = process(req, res))
					&& (hasOrigURI(req,
							UGateWebSocketServlet.class.getSimpleName()) || hasOrigURI(
							req, UGateAjaxUpdaterServlet.class.getSimpleName()))) {
				// other servlet process
				chain.doFilter(req, res);
			} else if (!processed) {
				res.sendError(HttpServletResponse.SC_NOT_FOUND);
			}
		} catch (final Throwable t) {
			throw new ServletException(t);
		}
	}

	/**
	 * Determines if the {@link HttpServletRequest} contains a URI part in the
	 * original request
	 * 
	 * @param request
	 *            the {@link HttpServletRequest} to check
	 * @param uriPart
	 *            the URI part to check for
	 * @return true when the URI part was in the original request
	 */
	protected static boolean hasOrigURI(final HttpServletRequest request, final String uriPart) {
		//http://docs.oracle.com/javaee/6/api/constant-values.html
		if (uriPart != null && !uriPart.isEmpty()) {
			if (request.getRequestURI().indexOf(uriPart) > -1) {
				return true;
			}
			Object uri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
			if (uri != null && uri.toString().indexOf(uriPart) > -1) {
				return true;
			}
			uri = request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI);
			if (uri != null && uri.toString().indexOf(uriPart) > -1) {
				return true;
			}
			uri = request.getAttribute(RequestDispatcher.INCLUDE_REQUEST_URI);
			if (uri != null && uri.toString().indexOf(uriPart) > -1) {
				return true;
			}
			uri = request.getAttribute(RequestDispatcher.INCLUDE_REQUEST_URI);
			if (uri != null && uri.toString().indexOf(uriPart) > -1) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines if the {@link HttpServletRequest#getRemoteUser()} is logged
	 * in. When the host IP matches the
	 * {@link HttpServletRequest#getLocalAddr()} an attempt will be made to
	 * automatically log the user in using the {@link Actor#getId()} from
	 * {@link FilterConfig#getInitParameter(String)}.
	 * 
	 * @param req
	 *            the {@link HttpServletRequest}
	 * @return true when the user is logged in or automatically logged in
	 */
	private boolean isLoggedIn(final HttpServletRequest req) {
		try {
			if (req.getRemoteUser() != null && !req.getRemoteUser().isEmpty()) {
				return true;
			}
			if (req.getLocalAddr() == null || req.getLocalAddr().isEmpty()) {
				return false;
			}
			final Enumeration<NetworkInterface> e = NetworkInterface
					.getNetworkInterfaces();
			NetworkInterface ni;
			InetAddress ip;
			Enumeration<InetAddress> e2;
			while (e.hasMoreElements()) {
				ni = e.nextElement();
				e2 = ni.getInetAddresses();
				while (e2.hasMoreElements()) {
					ip = e2.nextElement();
					if (req.getRemoteAddr().equals(ip.getHostAddress())) {
						final Actor actor = ServiceProvider.IMPL
								.getCredentialService().getActorById(
										Integer.valueOf(actorId));
						if (actor != null) {
							req.login(actor.getUsername(), actor.getPassword());
							return true;
						}
						return false;
					}
				}
			}
		} catch (final Throwable t) {
			log.warn("Unable to auto login", t);
		}
		return false;
	}

	/**
	 * Delegates to the
	 * {@link WebApplication#handleError(HttpServletRequest, HttpServletResponse, ServletContext, int, String)}
	 * 
	 * @param req
	 *            the {@link HttpServletRequest}
	 * @param res
	 *            the {@link HttpServletResponse}
	 * @param servletContext
	 *            the {@link ServletContext}
	 * @param code
	 *            the HTTP status code
	 * @param message
	 *            the error message
	 * @throws IOException
	 *             the {@link IOException}
	 */
	public static void handleErrorPage(final HttpServletRequest req,
			final HttpServletResponse res, final ServletContext servletContext,
			final int code, final String message) throws IOException {
		WebApplication.DFLT
				.handleError(req, res, servletContext, code, message);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(final FilterConfig filterConfig) throws ServletException {
		this.servletContext = filterConfig.getServletContext();
		final String actorIdStr = filterConfig.getInitParameter(ActorType.ID.name());
		if (actorIdStr == null || actorIdStr.isEmpty()) {
			throw new NullPointerException(String.format(
					"%1$s %2$s cannot be null",
					Actor.class.getSimpleName(), ActorType.ID.name()));
		}
		this.actorId = Integer.valueOf(actorIdStr);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void destroy() {
		WebApplication.DFLT.getTemplateEngine().clearTemplateCache();
	}
}
