package org.ugate.service.web;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Application level {@link WebSocketAdapter}
 */
public class UGateWebSocket extends WebSocketAdapter {

	private static final Logger log = LoggerFactory
			.getLogger(UGateWebSocket.class);
	/**
	 * The number of milliseconds that the server will close a web socket
	 * connection when idle (defaults to 5 minutes)
	 */
	public static final long IDLE_TIMEOUT = 5 * 60000L;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onWebSocketClose(int statusCode, String reason) {
		super.onWebSocketClose(statusCode, reason);
		UGateWebSocketServlet.members.remove(this);
		if (log.isDebugEnabled()) {
			log.debug(String.format("Closed with status %1$s, reason: %2$s",
					statusCode, reason));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onWebSocketConnect(final Session session) {
		session.setIdleTimeout(IDLE_TIMEOUT);
		super.onWebSocketConnect(session);
		UGateWebSocketServlet.members.add(this);
		if (log.isDebugEnabled()) {
			log.debug(String.format("Connected to %1$s",
					session.getRemoteAddress()));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onWebSocketError(final Throwable error) {
		log.error(UGateWebSocket.class.getSimpleName() + " error", error);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onWebSocketText(final String message) {
		for (final UGateWebSocket member : UGateWebSocketServlet.members) {
			try {
				getRemote().sendString(message);
			} catch (final Throwable t) {
				log.warn(String.format("Unable to send message %1$s to %2$s",
						message, member), t);
			}
		}
	}
}