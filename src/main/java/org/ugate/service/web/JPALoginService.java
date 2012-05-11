package org.ugate.service.web;

import java.io.IOException;

import org.eclipse.jetty.security.MappedLoginService;
import org.eclipse.jetty.server.UserIdentity;

/**
 * A JPA {@linkplain MappedLoginService} implementation
 */
public class JPALoginService extends MappedLoginService {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected UserIdentity loadUser(final String username) {
		// TODO Auto-generated method stub
		return null;//org.eclipse.jetty.security.JDBCLoginService
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadUsers() throws IOException {
		// TODO Auto-generated method stub
		
	}

}
