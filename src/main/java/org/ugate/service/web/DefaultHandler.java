package org.ugate.service.web;

import java.io.IOException;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ugate.UGateUtil;
import org.ugate.resources.RS;
import org.ugate.service.entity.jpa.Message;

public class DefaultHandler extends AbstractHandler {

	private static final long serialVersionUID = 7695326070064595909L;
	private static final Logger log = LoggerFactory.getLogger(DefaultHandler.class);

	public void handle(final String target, final Request baseRequest, final HttpServletRequest request, 
			final HttpServletResponse response) throws IOException, ServletException {
		javax.naming.Context ctx;
		try {
			ctx = new javax.naming.InitialContext();
			Object dataSource = ctx.lookup("jdbc/UGateDS");
			log.info(String.format("=================== JDBC DS: %1$s", dataSource));
			Object obj = com.atomikos.icatch.jta.TransactionManagerImp.getTransactionManager();
			Object transaction = ctx.lookup("TransactionManager");
			log.info(String.format("=================== UserTransaction: %1$s", transaction));
		} catch (final NamingException e) {
			log.error("NamingException: " + e.getMessage());
		}
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		//baseRequest.setHandled(true);
		response.getWriter().println("<h1>Hello UGate World</h1>");
		try {
	        // Create a new EntityManagerFactory using the System properties.
	        // The "hellojpa" name will be used to configure based on the
	        // corresponding name in the META-INF/persistence.xml file
	        EntityManagerFactory factory = Persistence.
	            createEntityManagerFactory(RS.rbLabel("persistent.unit"), System.getProperties());

	        // Create a new EntityManager from the EntityManagerFactory. The
	        // EntityManager is the main object in the persistence API, and is
	        // used to create, delete, and query objects, as well as access
	        // the current transaction
	        EntityManager em = factory.createEntityManager();

	        // Begin a new local transaction so that we can persist a new entity
	        em.getTransaction().begin();

	        // Create and persist a new Message entity
	        em.persist(new Message("Hello Persistence!"));

	        // Commit the transaction, which will cause the entity to
	        // be stored in the database
	        em.getTransaction().commit();

	        // It is always good practice to close the EntityManager so that
	        // resources are conserved.
	        em.close();

	        // Create a fresh, new EntityManager
	        EntityManager em2 = factory.createEntityManager();

	        // Perform a simple query for all the Message entities
	        Query q = em2.createQuery("select m from Message m");

	        // Go through each of the entities and print out each of their
	        // messages, as well as the date on which it was created 
	        for (Message m : (List<Message>) q.getResultList()) {
	            UGateUtil.PLAIN_LOGGER.info(m.getMessage() + " (created on: " + m.getCreated() + ')');
	            response.getWriter().println("<h3>" + m.getMessage() + " (created on: " + m.getCreated() + ')' + "</h3>");
	        }

	        // Again, it is always good to clean up after ourselves
	        em2.close();
	        factory.close();
		} catch (Throwable t) {
			log.error("JPA error: ", t);
		}
	}
}
