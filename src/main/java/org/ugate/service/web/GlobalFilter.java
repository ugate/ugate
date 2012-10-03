package org.ugate.service.web;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ugate.resources.RS;

/**
 * {@linkplain Filter} to wrap common output for {@linkplain Servlet}s. <a
 * href="http://wiki.eclipse.org/Jetty/Feature/Servlets_Bundled_with_Jetty"
 * >Complete list of bundled Filters</a>
 */
@WebFilter
public class GlobalFilter implements Filter {

	private static final Logger log = LoggerFactory.getLogger(GlobalFilter.class);
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain) {
		final HttpServletRequest request = (HttpServletRequest) req;
		final HttpServletResponse response = (HttpServletResponse) res;
		try {
			if (log.isDebugEnabled()) {
				log.debug(String.format("Entering filter from remote address %1$s", 
						req.getRemoteAddr()));
			}
			final String logout = request.getParameter(WebServer.RA_LOGOUT);
			final boolean isLogout = logout != null && !logout.isEmpty();
			if (isLogout || request.getRemoteUser() == null || request.getRemoteUser().isEmpty()) {
				if (isLogout) {
					request.getSession().invalidate();
				}
				// write login error page
				String errorHtml = "";
				if (request.getRequestURI().indexOf(RS.WEB_PAGE_LOGIN_ERROR) > -1) {
					errorHtml = "<h2 style=\"color: red\">Authentication Failed</h2>";
				}
				// write login page
				String content = RS.getEscapedResource(RS.WEB_PAGE_LOGIN, null);
				content = content.replace(WebServer.CTRL_CHAR + "ERROR_MSG" + WebServer.CTRL_CHAR, errorHtml);
				response.getWriter().print(content);
//			} else if (request.getRequestURI().indexOf("j_security_check") > -1) {
//				request.getRequestDispatcher("/").forward(request, response);
			} else {
				// delegate the request to the next filter, and eventually to the target servlet or JSP
				chain.doFilter(request, response);
			}	
		} catch (final Throwable t) {
			log.error("Unable to process " + GlobalFilter.class.getSimpleName(), t);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void destroy() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// 
	}
}
