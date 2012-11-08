package org.ugate.service.web;

import static org.ugate.service.web.ui.IndexController.VAR_ACTION_CONNECT_NAME;
import static org.ugate.service.web.ui.IndexController.VAR_ACTION_NAME;
import static org.ugate.service.web.ui.IndexController.VAR_COMMAND_NAME;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.servlet.DefaultServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ugate.Command;
import org.ugate.service.ServiceProvider;
import org.ugate.service.entity.RemoteNodeType;
import org.ugate.service.entity.jpa.RemoteNode;

/**
 * {@link javax.servlet.http.HttpServlet} for handling AJAX requests for
 * {@link RemoteNode}s
 */
public class UGateAjaxUpdaterServlet extends DefaultServlet {

	private static final long serialVersionUID = 3081647720588957725L;
	private static final Logger log = LoggerFactory
			.getLogger(UGateAjaxUpdaterServlet.class);

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
		if (request.getRemoteUser() == null
				|| request.getRemoteUser().isEmpty()) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return false;
		}
		return true;
	}

	/**
	 * Executes {@link Command}(s) using the
	 * {@link HttpServletRequest#getParameter(String)}
	 * 
	 * @param request
	 *            the {@link HttpServletRequest}
	 * @param response
	 *            the {@link HttpServletResponse}
	 * @param rn
	 *            the {@link RemoteNode} to execute the {@link Command} on
	 */
	protected void executeCommands(final HttpServletRequest request,
			final HttpServletResponse response, final RemoteNode rn) {
		if (rn == null) {
			return;
		}
		final String c = request.getParameter(VAR_COMMAND_NAME);
		if (c == null || c.isEmpty()) {
			return;
		}
		final Command cmd = Command.valueOf(c);
		if (cmd != null) {
			if (log.isInfoEnabled()) {
				log.info(String.format(
						"Executing %1$s for %2$s at address %3$s)", cmd,
						RemoteNode.class.getSimpleName(), rn.getAddress()));
			}
			ServiceProvider.IMPL.getWirelessService().sendData(rn, cmd, true);
		}
	}

	/**
	 * Gets the {@link RemoteNode} from an {@link HttpServletRequest}
	 * 
	 * @param request
	 *            the {@link HttpServletRequest}
	 * @param response
	 *            the {@link HttpServletResponse}
	 * @return the {@link RemoteNode}
	 * @throws ServletException
	 *             the {@link ServletException}
	 * @throws IOException
	 *             the {@link IOException}
	 */
	protected RemoteNode getRemoteNode(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {
		int id;
		final String idStr = request.getParameter(RemoteNodeType.ID.getKey());
		if (idStr != null && !idStr.isEmpty()
				&& (id = Integer.valueOf(idStr)) >= 0) {
			final RemoteNode rn = ServiceProvider.IMPL.getRemoteNodeService()
					.findById(id);
			if (rn == null) {
				log.warn(String.format("Unable to find %1$s with %2$s = %3$s",
						RemoteNode.class.getSimpleName(),
						RemoteNodeType.WIRELESS_ADDRESS, idStr));
				return null;
			}
			return rn;
		} else {
			log.warn(String.format(
					"Request %1$s must contain %2$s in order to perform PUT",
					request, RemoteNodeType.ID.getKey()));
		}
		return null;
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
	protected void doAll(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {
		if (validate(request, response)) {
			response.setHeader("Content-Type: application/json",
					Boolean.TRUE.toString());
			// try {
			// getServletContext().getNamedDispatcher("default").forward(request,
			// response);
			// } catch (final Throwable t) {
			// log.error("Error: ", t);
			// response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			// }
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doGet(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {
		doAll(request, response);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doPost(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {
		try {
			if (validate(request, response)) {
				final RemoteNode rn = getRemoteNode(request, response);
				executeCommands(request, response, rn);
				final String p = request.getParameter(VAR_ACTION_NAME);
				if (p != null && p.equals(VAR_ACTION_CONNECT_NAME)) {
					final boolean connected = ServiceProvider.IMPL
							.getWirelessService().testRemoteConnection(rn);
					if (!connected) {
						response.setStatus(HttpServletResponse.SC_CONFLICT);
						return;
					}
				}
				response.setStatus(HttpServletResponse.SC_OK);
			}
		} catch (final Throwable t) {
			log.error("POST Error: ", t);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doHead(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {
		doAll(request, response);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doPut(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {
		try {
			RemoteNode rn;
			if (validate(request, response)
					&& (rn = getRemoteNode(request, response)) != null) {
				boolean hasParams = false;
				String p;
				Object v;
				for (final RemoteNodeType rnt : RemoteNodeType.values()) {
					p = request.getParameter(rnt.getKey());
					if (p == null || p.isEmpty()) {
						continue;
					}
					v = rnt.getValue(rn);
					if (v != null && v.toString().equals(p)) {
						continue;
					}
					if (log.isInfoEnabled()) {
						log.info(String
								.format("Setting request parameter %1$s to %2$s for %3$s at address %4$s)",
										rnt.getKey(), p,
										RemoteNode.class.getSimpleName(),
										rn.getAddress()));
					}
					rnt.setValue(rn, p);
					hasParams = true;
				}
				if (hasParams) {
					ServiceProvider.IMPL.getRemoteNodeService().merge(rn);
				}
			}
		} catch (final Throwable t) {
			log.error("PUT Error: ", t);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doDelete(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {
		doAll(request, response);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doOptions(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {
		doAll(request, response);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doTrace(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {
		doAll(request, response);
	}
}
