package org.ugate.service.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.servlet.DefaultServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UGateAjaxUpdaterServlet extends DefaultServlet {

	private static final long serialVersionUID = 3081647720588957725L;
	private static final Logger log = LoggerFactory.getLogger(UGateAjaxUpdaterServlet.class);

	/**
	 * Validates the request
	 * 
	 * @param request
	 *            the {@link HttpServletRequest}
	 * @param response
	 *            the {@link HttpServletResponse}
	 * @return true when validation is successful
	 * @throws ServletException
	 *             the {@link ServletException}
	 * @throws IOException
	 *             the {@link IOException}
	 */
	protected boolean validate(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {
		return true;
	}

	/**
	 * Does all the HTTP operations
	 * 
	 * @param request
	 *            the {@link HttpServletRequest}
	 * @param response
	 *            the {@link HttpServletResponse}
	 * @throws ServletException
	 *             the {@link ServletException}
	 * @throws IOException
	 *             the {@link IOException}
	 */
	protected void doAll(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Content-Type: application/json", Boolean.TRUE.toString());
//		try {
//			getServletContext().getNamedDispatcher("default").forward(request, response);
//		} catch (final Throwable t) {
//			log.error("Error: ", t);
//			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		doAll(request, response);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		doAll(request, response);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doHead(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		doAll(request, response);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doPut(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		try {
			if (validate(request, response)) {
				final String json = request.getParameter("json");
				log.info(json);
			}
		} catch (final Throwable t) {
			log.error("Error: ", t);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doDelete(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		doAll(request, response);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doOptions(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		doAll(request, response);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doTrace(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		doAll(request, response);
	}
}
