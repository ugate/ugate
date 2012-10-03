package org.ugate.service.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.servlet.DefaultServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ugate.resources.RS;
import org.ugate.service.ServiceProvider;
import org.ugate.service.entity.ActorType;
import org.ugate.service.entity.RemoteNodeType;
import org.ugate.service.entity.jpa.Actor;
import org.ugate.service.entity.jpa.RemoteNode;

/**
 * {@linkplain DefaultServlet} for root context calls. <a
 * href="http://wiki.eclipse.org/Jetty/Feature/Servlets_Bundled_with_Jetty"
 * >Complete list of bundled Servlets</a>
 */
@WebServlet
public class DefaultAppServlet extends DefaultServlet {

	private static final long serialVersionUID = 6841946295927734658L;
	private static final Logger log = LoggerFactory.getLogger(DefaultAppServlet.class);
	private static final String RA_REMOTENODE = "remoteNodeId";
	
	public DefaultAppServlet() {
		super();
	}
	
	protected void process(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		try {
			Actor actor = null;
			String username = null;
			if (request.getRemoteUser() != null && !request.getRemoteUser().toString().isEmpty()) {
				username = request.getRemoteUser().toString();
			} else if (request.getParameter(ActorType.USERNAME.name()) != null && 
					!request.getParameter(ActorType.USERNAME.name()).isEmpty()) {
				username = request.getParameter(ActorType.USERNAME.name());
			}
			if (username != null) {
				actor = ServiceProvider.IMPL.getCredentialService().getActor(username);
				if (actor == null) {
					response.setStatus(HttpServletResponse.SC_FORBIDDEN);
					return;
				}
			} else {
				// TODO : show list of actors to choose from
			}
			String content = RS.getEscapedResource(RS.WEB_PAGE_INDEX, actor, ActorType.values());
			// capture remote node
			final String remoteNodeId = request.getParameter(RA_REMOTENODE);
			RemoteNode remoteNode = null;
			String rni = "";
			String rns = "";
			for (final RemoteNode rn : actor.getHost().getRemoteNodes()) {
				if (remoteNodeId != null && !remoteNodeId.isEmpty() && rn.getId() == Integer.parseInt(remoteNodeId)) {
					// remote node values
					remoteNode = rn;
					content = RS.getEscapedContent(content, remoteNode, RemoteNodeType.values());
					// add random sequence to ensure the request is not cached
					rni = "<li><a href=\"/?seq=" + rn.getId() + '_' + Math.random() + "\" data-transition=\"flip\">" + rn.getAddress() + " (select to change)</a></li>";
				}
				// remote node selection
				rns += "<li><a href=\"/?" + RA_REMOTENODE + "=" + rn.getId() + "\" data-transition=\"flip\">" + rn.getAddress() + "</a></li>";
			}
			// show remote node selection content
			content = content.replaceAll(WebServer.CTRL_CHAR + "NODE_DETAIL_DISPLAY" + WebServer.CTRL_CHAR, remoteNode == null ? "none" : "block");
			content = content.replace(WebServer.CTRL_CHAR + ActorType.REMOTE_NODES.name() + WebServer.CTRL_CHAR, remoteNode != null ? rni : rns);
			// print results
			response.getWriter().print(content);
	        response.setStatus(HttpServletResponse.SC_OK);
		} catch (final Throwable t) {
			log.error("Error: ", t);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

	@Override
	protected void doHead(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}
	
	@Override
	protected void doPut(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}
	
	@Override
	protected void doDelete(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

	@Override
	protected void doOptions(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}
	
	@Override
	protected void doTrace(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}
}
