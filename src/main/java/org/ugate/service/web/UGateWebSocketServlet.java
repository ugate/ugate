package org.ugate.service.web;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.util.ajax.JSON;
import org.eclipse.jetty.util.ajax.JSONObjectConvertor;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ugate.UGateEvent;
import org.ugate.UGateKeeper;
import org.ugate.UGateListener;
import org.ugate.service.entity.RemoteNodeType;
import org.ugate.service.entity.jpa.RemoteNode;
import org.ugate.service.entity.jpa.RemoteNodeReading;
import org.ugate.wireless.data.RxTxRemoteNodeReadingDTO;

/**
 * {@linkplain WebSocketServlet} for context calls related to {@link WebSocket}
 * s that will push {@link RemoteNode} and {@link RemoteNodeReading} changes to
 * connected clients as JSON objects.
 * {@link UGateEvent.Type#WIRELESS_REMOTE_NODE_COMMITTED} and
 * {@link UGateEvent.Type#WIRELESS_REMOTE_NODE_COMMITTED} (with
 * {@link RemoteNodeReading}s as it's {@link UGateEvent#getNewValue()}) events
 * will be listened to that will prompt pushes to client members.
 */
@WebServlet
public class UGateWebSocketServlet extends WebSocketServlet {

	private static final long serialVersionUID = 6841946295927734658L;
	private static final Logger log = LoggerFactory
			.getLogger(UGateWebSocketServlet.class);
	final static Set<UGateWebSocket> members = new CopyOnWriteArraySet<>();
	private JSON jsonRemoteNode;
	private JSON jsonRemoteNodeReading;
	private UGateListener uiListener;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() throws ServletException {
		super.init();
		final JSONObjectConvertor cvt = new JSONObjectConvertor();
		jsonRemoteNode = new JSON();
		jsonRemoteNode.addConvertor(RemoteNode.class, cvt);
		jsonRemoteNodeReading = new JSON();
		jsonRemoteNodeReading.addConvertor(RemoteNodeReading.class, cvt);
		uiListener = new UGateListener() {
			@Override
			public void handle(final UGateEvent<?, ?> event) {
				if (members.size() <= 0) {
					return;
				}
				if (event.getType() == UGateEvent.Type.WIRELESS_REMOTE_NODE_COMMITTED) {
					final RemoteNode rn = (RemoteNode) event.getSource();
					final String jsonData = jsonRemoteNode.toJSON(rn);
					if (log.isInfoEnabled()) {
						log.info(String
								.format("Sending %1$s (address: %2$s) notification to %3$s web member(s): %4$s",
										RemoteNode.class.getSimpleName(),
										rn.getAddress(), members.size(),
										jsonData));
					}
					notifyMembers(jsonData);
				} else if (event.getType() == UGateEvent.Type.WIRELESS_DATA_RX_SUCCESS
						&& event.getNewValue() instanceof RxTxRemoteNodeReadingDTO) {
					final RxTxRemoteNodeReadingDTO sr = (RxTxRemoteNodeReadingDTO) event
							.getNewValue();
					final RemoteNodeReading rnr = sr.getRemoteNodeReading();
					String jsonData = jsonRemoteNode
							.toJSON(rnr.getRemoteNode());
					final String rnrJsonData = jsonRemoteNodeReading
							.toJSON(rnr);
					final String rnrsKey = '"'
							+ RemoteNodeType.REMOTE_NODE_READINGS.getKey()
							+ "\":";
					jsonData = jsonData.replaceAll(rnrsKey
							+ rnr.getRemoteNode().getRemoteNodeReadings(),
							rnrsKey + rnrJsonData);
					if (log.isInfoEnabled()) {
						log.info(String
								.format("Sending %1$s (address: %2$s) notification to %3$s web member(s): %4$s",
										RemoteNodeReading.class.getSimpleName(),
										rnr.getRemoteNode().getAddress(),
										members.size(), jsonData));
					}
					notifyMembers(jsonData);
				}
			}
		};
		UGateKeeper.DEFAULT.addListener(uiListener);
	}

	/**
	 * Notifies all the {@link DefaultWebSocket} members that are currently
	 * connected of the JSON change
	 * 
	 * @param jsonData
	 *            the JSON data send the notification for
	 */
	protected void notifyMembers(final String jsonData) {
		if (jsonData == null || jsonData.isEmpty()) {
			return;
		}
		for (final UGateWebSocket member : members) {
			try {
				member.getRemote().sendString(jsonData);
			} catch (final Throwable t) {
				log.warn(String.format("Unable to send message %1$s to %2$s",
						jsonData, member), t);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void destroy() {
		super.destroy();
		if (uiListener != null) {
			UGateKeeper.DEFAULT.removeListener(uiListener);
		}
		jsonRemoteNode = null;
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
		if (request.getRemoteUser() == null
				|| request.getRemoteUser().isEmpty()) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		// response.sendRedirect("/");
		// try {
		// getServletContext().getNamedDispatcher("default").forward(request,
		// response);
		// } catch (final Throwable t) {
		// log.error("Error: ", t);
		// response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		// }
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
		doAll(request, response);
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
		doAll(request, response);
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void configure(final WebSocketServletFactory factory) {
		// set a 10 second idle timeout
		factory.getPolicy().setIdleTimeout(10000);
		// register web socket
		factory.register(UGateWebSocket.class);
	}
}
