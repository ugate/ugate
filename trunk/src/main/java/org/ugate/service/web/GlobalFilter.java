package org.ugate.service.web;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ugate.resources.RS;
import org.ugate.resources.RS.KEYS;

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
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) {
		try {
			if (log.isDebugEnabled()) {
				log.debug(String.format("Entering filter from remote address %1$s", 
						request.getRemoteAddr()));
			}
			response.setContentType("text/html;charset=utf-8");
			response.getWriter().println("<html><body><button onclick=\"header('HTTP/1.1 401 Unauthorized');\">Logout</button>");
			response.getWriter().println("<h1>" + RS.rbLabel(KEYS.APP_TITLE) + "</h1>");

			// delegate the request to the next filter, and eventually to the target servlet or JSP
			chain.doFilter(request, response);

			response.getWriter().println("</body></html>");
		} catch (final Throwable t) {
			log.error("Unable to process " + GlobalFilter.class.getSimpleName(), t);
		}
	}
	// TODO : add logout feature for digest
//	private String getLogoutJS(final ServletRequest request) {
//        return "header('HTTP/1.1 401 Unauthorized');" +
//        "header('WWW-Authenticate: Digest realm=\"'" + request.get.getUserPrincipal().getName() + "'\",qop="auth",nonce="'.$_SESSION['http_digest_nonce'].'",opaque="'.md5($realm).'"');
//	}

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
