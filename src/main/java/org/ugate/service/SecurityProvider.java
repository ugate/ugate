package org.ugate.service;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Base {@link Security} provider
 */
public abstract class SecurityProvider {

	static {
		// add the bouncy castle security provider
		Security.insertProviderAt(new BouncyCastleProvider(), 1);
		// System.setProperty("ssl.KeyManagerFactory.algorithm", "X509");
	}
}
