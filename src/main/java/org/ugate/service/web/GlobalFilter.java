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

/**
 * {@linkplain Filter} to wrap common output for {@linkplain Servlet}s
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
			response.getWriter().println("<html><body>");
			response.getWriter().println("<h1>" + RS.rbLabel("app.title") + "</h1>");

			// delegate the request to the next filter, and eventually to the target servlet or JSP
			chain.doFilter(request, response);

			response.getWriter().println("</body></html>");
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
