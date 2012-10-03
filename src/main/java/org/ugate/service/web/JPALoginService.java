package org.ugate.service.web;

import java.io.IOException;

import org.eclipse.jetty.security.MappedLoginService;
import org.eclipse.jetty.server.UserIdentity;
import org.eclipse.jetty.util.security.Credential;
import org.eclipse.jetty.util.security.Credential.MD5;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ugate.service.CredentialService;
import org.ugate.service.ServiceProvider;
import org.ugate.service.entity.jpa.Actor;
import org.ugate.service.entity.jpa.Role;

/**
 * A JPA {@linkplain MappedLoginService} implementation
 */
public class JPALoginService extends MappedLoginService {
	
	private static final Logger log = LoggerFactory.getLogger(JPALoginService.class);
	
	/**
	 * Constructor
	 */
	public JPALoginService() {
		// need to use the same salt for the Realm name for authentication
		// or else authentication will not work using digest.
		setName(CredentialService.SALT);
	}

	/**
	 * Loads an {@linkplain Actor} either by authentication or by login ID
	 * 
	 * @param username
	 *            the login ID
	 * @param credentials
	 *            the password
	 * @return the {@linkplain UserIdentity}
	 */
	protected UserIdentity loadActor(final String username, final Object credentials) {
		try {
			// get the user and roles from JPA (roles should be eagerly fetched or pre-fetched
			// or a null pointer will be thrown)
			final Actor actor = credentials != null ? ServiceProvider.IMPL
					.getCredentialService().authenticate(username,
							credentials.toString()) : ServiceProvider.IMPL
					.getCredentialService().getActor(username);
			if (actor == null) {
				throw new NullPointerException(Actor.class.getName());
			}
			final String[] roles = new String[actor.getRoles().size()];
			int i = -1;
			for (final Role role : actor.getRoles()) {
				roles[++i] = role.getRole();
			}
			// The password should already be an MD5 hash using the same salt pattern as the 
			// JPA provider
			final Credential cred = Credential.getCredential(MD5.__TYPE + actor.getPassword());
			return putUser(username, cred, roles);
		} catch (final Throwable t) {
			final String msg = String.format("Unable to %1$s %2$s",
					(credentials == null ? "load" : "authenticate"), username);
			if (log.isDebugEnabled()) {
				log.debug(msg, t);
			} else if (log.isInfoEnabled()) {
				log.info(String.format(msg + " Message: %1$s", t.getMessage()));
			}
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected UserIdentity loadUser(final String username) {
		if (log.isDebugEnabled()) {
			log.debug("Attempting to load user for " + username);
		}
		return loadActor(username, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadUsers() throws IOException {
		// don't need to do anything- users maintained by JPA
		log.debug("Skipping loading of users (maintained by JPA provider)");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserIdentity login(final String username, final Object credentials) {
		// let JPA handle caching of users
		_users.clear();
		if (log.isDebugEnabled()) {
			log.debug("Attempting login for " + username);
		}
		return loadActor(username, credentials);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void logout(final UserIdentity identity) {
		super.logout(identity);
	}
}
