package org.ugate.service.web.ui;

import java.io.IOException;

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

import org.thymeleaf.TemplateEngine;
import org.ugate.service.web.UGateAjaxUpdaterServlet;
import org.ugate.service.web.UGateWebSocketServlet;
import org.ugate.service.web.ui.WebApplication.ControllerResource;

/**
 * Global web {@link Filter} that handles routing of {@link BaseController}s
 */
public class WebFilter implements Filter {

	private ServletContext servletContext;

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
			if (req.getRemoteUser() == null || req.getRemoteUser().isEmpty()) {
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
	 * {@inheritDoc}
	 */
	@Override
	public void init(final FilterConfig filterConfig) throws ServletException {
		this.servletContext = filterConfig.getServletContext();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void destroy() {
		WebApplication.DFLT.getTemplateEngine().clearTemplateCache();
	}
}
