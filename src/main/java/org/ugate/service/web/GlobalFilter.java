package org.ugate.service.web;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.transaction.TransactionManager;

@WebFilter
public class GlobalFilter implements Filter {
	
	// Use the Atomikos UserTransaction implementation to start and end
	// the transactions!
	//private J2eeUserTransaction utx = new J2eeUserTransaction();

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
		try {

			// start a new transaction for this request
			//utx.begin();
			// http://stackoverflow.com/questions/2131798/jetty-mysql-connection-pool-configuration-error-javax-naming-namenotfoundexcept
//			getTransaction().setTransactionTimeout(1000);
//			getTransaction().begin();

			// delegate the request to the next filter, and eventually to the
			// target servlet or JSP
			chain.doFilter(request, response);

			// if no exception happened: commit the transaction
			//utx.commit();
//			getTransaction().commit();
		} catch (final Throwable t) {
			// analyze exception to dermine of rollback is required or not
			// and then call rollback or commit on utx as appropriate
			t.printStackTrace();
			try {
				getTransaction().rollback();
			} catch (final Throwable t2) {
			}
		}
	}

	@Override
	public void destroy() {

	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		try {
			listContext(new InitialContext(), "");
		} catch (final NamingException e) {
			e.printStackTrace();
		}
	}
	
	protected TransactionManager getTransaction() {
		try {
			return (TransactionManager) new InitialContext().lookup("UserTransaction");
		} catch (final NamingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected static final void listContext(Context ctx, String indent) {
	    try {
	        NamingEnumeration<?> list = ctx.listBindings("");
	        while (list.hasMore()) {
	            Binding item = (Binding) list.next();
	            String className = item.getClassName();
	            String name = item.getName();
	            System.out.println(indent + className + " " + name);
	            Object o = item.getObject();
	            if (o instanceof javax.naming.Context) {
	                listContext((Context) o, indent + " ");
	            }
	        }
	    } catch (NamingException ex) {
	        System.out.println(ex);
	    }
	}
}
