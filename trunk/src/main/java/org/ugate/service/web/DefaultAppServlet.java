package org.ugate.service.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ugate.UGateUtil;
import org.ugate.service.ServiceManager;
import org.ugate.service.entity.jpa.Message;

@WebServlet
public class DefaultAppServlet extends DefaultServlet {

	private static final long serialVersionUID = 6841946295927734658L;
	private static final Logger log = LoggerFactory.getLogger(DefaultAppServlet.class);
	
	public DefaultAppServlet() {
		super();
	}
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doHandle(null, null, request, response);
	}

	//@Override
	public void doHandle(final String target, final Request baseRequest, final HttpServletRequest request, 
			final HttpServletResponse response) throws IOException, ServletException {
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		//baseRequest.setHandled(true);
		response.getWriter().println("<h1>RESULTS</h1>");
		try {
	        final Message msg = new Message("Hello Persistence! " + System.currentTimeMillis());
	        ServiceManager.IMPL.getSettingsService().saveMessage(msg);
	        log.info("NEW Message ID: " + msg.getId());
	        // Go through each of the entities and print out each of their
	        // messages, as well as the date on which it was created 
	        for (Message m : ServiceManager.IMPL.getSettingsService().getAllMessages()) { // (List<Message>) q.getResultList()) {
	            UGateUtil.PLAIN_LOGGER.info(m.getMessage() + " (created on: " + m.getCreated() + ')');
	            response.getWriter().println("<h3>" + m.getMessage() + " (created on: " + m.getCreated() + ')' + "</h3>");
	        }
		} catch (Throwable t) {
			log.error("JPA error: ", t);
		}
	}
}
