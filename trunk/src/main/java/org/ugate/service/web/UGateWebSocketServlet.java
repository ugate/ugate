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
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ugate.UGateEvent;
import org.ugate.UGateKeeper;
import org.ugate.UGateListener;
import org.ugate.service.entity.RemoteNodeType;
import org.ugate.service.entity.jpa.RemoteNode;

/**
 * {@linkplain WebSocketServlet} for context calls related to {@link WebSocket}
 * s that will push {@link RemoteNode} changes to connected clients as JSON
 * objects.
 */
@WebServlet
public class UGateWebSocketServlet extends WebSocketServlet {

	private static final long serialVersionUID = 6841946295927734658L;
	private static final Logger log = LoggerFactory
			.getLogger(UGateWebSocketServlet.class);
	private final Set<DefaultWebSocket> members = new CopyOnWriteArraySet<>();
	private JSON json;
	private UGateListener uiListener;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() throws ServletException {
		super.init();
		json = new JSON();
		json.addConvertor(RemoteNode.class, new JSONObjectConvertor());
		uiListener = new UGateListener() {
			@Override
			public void handle(final UGateEvent<?, ?> event) {
				if (event.getType() == UGateEvent.Type.WIRELESS_REMOTE_NODE_COMMITTED) {
					final RemoteNode rn = (RemoteNode) event.getSource();
					if (members.size() > 0) {
						String jsonData = json.toJSON(rn);
						// cycle through the remote node JSON and convert the
						// field names to the remote node type names so they
						// match the input fields in the page
						for (final RemoteNodeType rnt : RemoteNodeType.values()) {
							jsonData = jsonData.replaceAll(rnt.getKey(),
									rnt.name());
						}
						if (log.isInfoEnabled()) {
							log.info(String
									.format("Sending %1$s (address: %2$s) notification to %3$s web member(s): %4$s",
											RemoteNode.class, rn.getAddress(),
											members.size(), jsonData));
						}
						for (final DefaultWebSocket member : members) {
							try {
								member.sendMessage(jsonData);
							} catch (final Throwable t) {
								log.warn(String.format(
										"Unable to send message %1$s to %2$s",
										jsonData, member), t);
							}
						}
					} else if (log.isInfoEnabled()) {
						log.info(String
								.format("No members listening to %1$s updates for %2$s at address %3$s",
										WebSocket.class.getSimpleName(),
										RemoteNode.class.getSimpleName(),
										rn.getAddress()));
					}
				}
			}
		};
		UGateKeeper.DEFAULT.addListener(uiListener);
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
		json = null;
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
	public WebSocket doWebSocketConnect(final HttpServletRequest request,
			final String protocol) {
		return new DefaultWebSocket();
	}

	/**
	 * Default {@link WebSocket.OnTextMessage}
	 */
	class DefaultWebSocket implements WebSocket.OnTextMessage {

		private Connection connection;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onOpen(Connection connection) {
			this.connection = connection;
			members.add(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onClose(int closeCode, String message) {
			members.remove(this);
		}

		/**
		 * Sends a message using {@link Connection#sendMessage(String)}
		 * 
		 * @param message
		 *            {@link Connection#sendMessage(String)}
		 * @throws IOException
		 *             from {@link Connection#sendMessage(String)}
		 */
		public void sendMessage(final String message) throws IOException {
			connection.sendMessage(message);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onMessage(String data) {
			for (final DefaultWebSocket member : members) {
				try {
					member.connection.sendMessage(data);
				} catch (final Throwable t) {
					log.warn(String
							.format("Unable to send message %1$s to %2$s",
									data, member), t);
				}
			}
		}
	}
}
