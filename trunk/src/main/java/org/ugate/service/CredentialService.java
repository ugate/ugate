package org.ugate.service;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.ugate.service.dao.CredentialDao;
import org.ugate.service.entity.jpa.Actor;
import org.ugate.service.entity.jpa.Role;

/**
 * Credential service
 */
@Service
@Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
public class CredentialService {

	private static final Logger log = LoggerFactory.getLogger(CredentialService.class);
	private static final Object __md5Lock = new Object();
	private static MessageDigest __md;

	@Resource
	private CredentialDao credentialDao;
	
	/**
	 * @return a {@linkplain List} of all {@linkplain Actor}s
	 */
	public List<Actor> getAllActors() {
        return credentialDao.getAllActors();
	}
	
	/**
	 * @return the total number of {@linkplain Actor}s
	 */
	public long getActorCount() {
		return credentialDao.getActorCount();
	}

	/**
	 * Authenticates a user against a central data source
	 * 
	 * @param username
	 *            the user's login ID
	 * @param password
	 *            the user's password
	 * @return true when authenticated
	 */
	public boolean authenticate(final String username, final String password) {
		try {
			final Actor actor = credentialDao.getActor(username);
			if (actor != null) {
				String pwd = actor.getPwd();
				if (actor.getEncrypted()) {
					return hasDigestMatch(username, pwd, password);
				}
				return password.equals(pwd);
			} else if (log.isDebugEnabled()) {
				log.debug(String.format("No %1$s exists with a login of %2$s", username));
			}
		} catch (final Exception e) {
			log.error(String.format("Unable to authenticate user %1$s", username), e);
		}
		return false;
	}

	/**
	 * Adds a user with the specified roles to a central data source
	 * 
	 * @param username
	 *            the user's login ID
	 * @param password
	 *            the user's password
	 * @param encrypted true when the password should be MD5 encrypted
	 * @param roles
	 *            the {@linkplain Role}(s) that the user should have
	 * @throws UnsupportedOperationException
	 *             when using an OAuth 2.0 vendor
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void addUser(final String username, final String password, final boolean encrypted, final Role... roles) 
			throws UnsupportedOperationException {
		final Actor actor = new Actor();
		actor.setLogin(username);
		String pwd = password;
		if (encrypted) {
			pwd = digest(username, password);
			actor.setEncrypted(true);
		}
		actor.setPwd(pwd);
		actor.setRoles(new HashSet<Role>(Arrays.asList(roles)));
		credentialDao.persistActor(actor);
	}
	
	/**
	 * Determines if two passwords match for a user name
	 * 
	 * @param username
	 *            the login ID
	 * @param password
	 *            the password
	 * @param otherPassword
	 *            the password to compare to
	 * @return true when the passwords match
	 */
	public static boolean hasDigestMatch(final String username, final String password, final String otherPassword) {
		final byte[] digest = digestBytes(username, password);
		final byte[] enteredDigest = digestBytes(username, otherPassword);
		if (enteredDigest == null || enteredDigest.length != digest.length) {
			return false;
		}
		for (int i = 0; i < enteredDigest.length; i++) {
			if (enteredDigest[i] != digest[i]) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @see #digestBytes(String, String)
	 * 
	 * @param username
	 *            the login ID
	 * @param password
	 *            the password
	 * @return the digested user name and password
	 */
	protected static String digest(final String username, final String password) {
		try {
			return toString(digestBytes(username, password), 16);
		} catch (final Exception e) {
			log.warn("Unable to digest password for " + username, e);
			return null;
		}
	}
	
	/**
	 * Digests the bytes of a user name and password
	 * 
	 * @param username
	 *            the login ID
	 * @param password
	 *            the password
	 * @return the digested user name and password bytes
	 */
	protected static byte[] digestBytes(final String username, final String password) {
		try {
			byte[] digest;
			synchronized (__md5Lock) {
				if (__md == null) {
					try {
						__md = MessageDigest.getInstance("MD5");
					} catch (final Exception e) {
						log.warn("Unable to create message digest for MD5", e);
						return null;
					}
				}
				__md.reset();
				__md.update(password.getBytes("ISO-8859-1"));
				digest = __md.digest();
			}
			return digest;
		} catch (final Exception e) {
			log.warn("Unable to digest password for " + username, e);
			return null;
		}
	}
	
	/**
	 * Converts bytes into a {@linkplain String}
	 * 
	 * @param bytes
	 *            the bytes to convert
	 * @param base
	 *            the base to convert them to
	 * @return the {@linkplain String}
	 */
	protected static String toString(final byte[] bytes, final int base) {
		final StringBuilder buf = new StringBuilder();
		for (final byte b : bytes) {
			int bi = 0xff & b;
			int c = '0' + (bi / base) % base;
			if (c > '9')
				c = 'a' + (c - '0' - 10);
			buf.append((char) c);
			c = '0' + bi % base;
			if (c > '9')
				c = 'a' + (c - '0' - 10);
			buf.append((char) c);
		}
		return buf.toString();
	}
}
