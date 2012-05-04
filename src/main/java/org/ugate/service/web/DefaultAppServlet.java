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
import org.ugate.service.dao.SettingsDao;
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
//	@SuppressWarnings("unchecked")
	public void doHandle(final String target, final Request baseRequest, final HttpServletRequest request, 
			final HttpServletResponse response) throws IOException, ServletException {
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		//baseRequest.setHandled(true);
		response.getWriter().println("<h1>RESULTS</h1>");
		try {
			SettingsDao ss = new SettingsDao();
	        // Create a new EntityManagerFactory using the System properties.
	        // The "hellojpa" name will be used to configure based on the
	        // corresponding name in the META-INF/persistence.xml file
//	        EntityManagerFactory factory = Persistence.
//	            createEntityManagerFactory(RS.rbLabel("persistent.unit"), System.getProperties());

	        // Create a new EntityManager from the EntityManagerFactory. The
	        // EntityManager is the main object in the persistence API, and is
	        // used to create, delete, and query objects, as well as access
	        // the current transaction
//	        em = ServiceManager.getEmFactory().createEntityManager();
	        
//	        OpenJPAEntityManager oem = OpenJPAPersistence.cast(em);
//	        Connection conn = (Connection) oem.getConnection();
//	        boolean autoCommit = conn.getAutoCommit();

	        // Begin a new local transaction so that we can persist a new entity
//	        em.getTransaction().begin();

	        // Create and persist a new Message entity
	        //Query q1 = em.createNativeQuery("SET LOCK_TIMEOUT 1");
	        //Object o = q1.executeUpdate();
	        final Message msg = new Message("Hello Persistence! " + System.currentTimeMillis());
	        ss.persistMessage(msg);
//	        em.persist(msg);
	        log.info("NEW Message ID: " + msg.getId());

	        // Commit the transaction, which will cause the entity to
	        // be stored in the database
//	        em.getTransaction().commit();

	        // It is always good practice to close the EntityManager so that
	        // resources are conserved.
//	        em.close();

	        // Create a fresh, new EntityManager
//	        EntityManager em2 = ServiceManager.getEmFactory().createEntityManager();

	        // Perform a simple query for all the Message entities
//	        Query q = em2.createQuery("select m from Message m");

	        // Go through each of the entities and print out each of their
	        // messages, as well as the date on which it was created 
	        for (Message m : ss.getAllMessages()) { // (List<Message>) q.getResultList()) {
	            UGateUtil.PLAIN_LOGGER.info(m.getMessage() + " (created on: " + m.getCreated() + ')');
	            response.getWriter().println("<h3>" + m.getMessage() + " (created on: " + m.getCreated() + ')' + "</h3>");
	        }

	        // Again, it is always good to clean up after ourselves
//	        em2.close();
//	        factory.close();
		} catch (Throwable t) {
			log.error("JPA error: ", t);
		}
	}
}
