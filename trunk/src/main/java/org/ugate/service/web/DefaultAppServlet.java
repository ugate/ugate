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
	private static final String SA_ACTOR = "actor";
	private static final String SA_REMOTENODE = "remoteNode";
	
	public DefaultAppServlet() {
		super();
	}
	
	protected void process(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		try {
			Actor actor = null;
			if (request.getSession().getAttribute(SA_ACTOR) instanceof Actor) {
				actor = (Actor) request.getSession().getAttribute(SA_ACTOR);
			} else {
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
					request.getSession().setAttribute(SA_ACTOR, actor);
				} else {
					// TODO : show list of actors to choose from
				}
			}
			final String remoteNodeId = request.getParameter(SA_REMOTENODE);
			if (remoteNodeId != null && !remoteNodeId.isEmpty()) {
				RemoteNode srn = null;
				for (final RemoteNode rn : actor.getHost().getRemoteNodes()) {
					if (rn.getId() == Integer.parseInt(remoteNodeId)) {
						srn = rn;
						break;
					}
				}
				if (srn != null) {
					// proceed to main remote node content
					String content = RS.getEscapedResource(RS.WEB_PAGE_REMOTE_NODE, actor,
							ActorType.values());
					content = RS.getEscapedContent(content, srn, RemoteNodeType.values());
					response.getWriter().print(content);
					response.setStatus(HttpServletResponse.SC_OK);
					return;
				}
			}
			// show remote node selection content
			String content = RS.getEscapedResource(RS.WEB_PAGE_REMOTE_NODE_INDEX, actor,
					ActorType.values());
			String rno = "";
			int i = 0;
			for (final RemoteNode rn : actor.getHost().getRemoteNodes()) {
				rno += "<input type=\"radio\" name=\"" + SA_REMOTENODE + "\" id=\"" + SA_REMOTENODE + i + "\" value=\"" + rn.getId() + "\"/>";
				rno += "<label for=\"" + SA_REMOTENODE + i + "\">" + rn.getAddress() + "</label>";
				i++;
			}
			content = content.replace(ActorType.REMOTE_NODES.name(), rno);
			response.getWriter().print(content);
	        response.setStatus(HttpServletResponse.SC_OK);
		} catch (final Throwable t) {
			log.error("JPA error: ", t);
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
