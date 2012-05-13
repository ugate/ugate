package org.ugate.service.web;

import java.io.IOException;

import org.eclipse.jetty.security.MappedLoginService;
import org.eclipse.jetty.server.UserIdentity;
import org.eclipse.jetty.util.security.Credential;
import org.eclipse.jetty.util.security.Credential.MD5;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ugate.service.ServiceManager;
import org.ugate.service.entity.jpa.Actor;

/**
 * A JPA {@linkplain MappedLoginService} implementation
 */
public class JPALoginService extends MappedLoginService {
	
	private static final Logger log = LoggerFactory.getLogger(JPALoginService.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected UserIdentity loadUser(final String username) {
		// add the user to 
		final Actor actor = ServiceManager.IMPL.getCredentialService().getActor(username);
		return putUser(username, 
				Credential.getCredential(MD5.__TYPE + actor.getPwd()),
				actor.getRoles().toArray(new String[actor.getRoles().size()])); 
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadUsers() throws IOException {
		// don't need to do anything- users maintained by JPA
		log.debug("Loading Users");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserIdentity login(final String username, final Object credentials) {
		// let JPA handle caching of users
		_users.clear();
		return super.login(username, credentials);
	}
}
