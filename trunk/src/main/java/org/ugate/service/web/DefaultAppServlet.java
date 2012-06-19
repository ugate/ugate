package org.ugate.service.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.servlet.DefaultServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@linkplain DefaultServlet} for root context calls
 */
@WebServlet
public class DefaultAppServlet extends DefaultServlet {

	private static final long serialVersionUID = 6841946295927734658L;
	private static final Logger log = LoggerFactory.getLogger(DefaultAppServlet.class);
	
	public DefaultAppServlet() {
		super();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		try {
			response.getWriter().println("<h1>RESULTS</h1>");
			// TODO : Implement web GUI
//	        final Message msg = new Message("Hello Persistence! " + System.currentTimeMillis());
//	        ServiceManager.IMPL.getRemoteNodeService().saveMessage(msg);
//	        log.info("NEW Message ID: " + msg.getId());
//	        // Go through each of the entities and print out each of their
//	        // messages, as well as the date on which it was created 
//	        for (Message m : ServiceManager.IMPL.getRemoteNodeService().getAllMessages()) { // (List<Message>) q.getResultList()) {
//	            UGateUtil.PLAIN_LOGGER.info(m.getMessage() + " (created on: " + m.getCreated() + ')');
//	            response.getWriter().println("<h3>" + m.getMessage() + " (created on: " + m.getCreated() + ')' + "</h3>");
//	        }
	        response.setStatus(HttpServletResponse.SC_OK);
		} catch (final Throwable t) {
			log.error("JPA error: ", t);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}
}
