package org.ugate.service.web;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class SSLUtil {

	private SSLUtil() {
	}
	
	public static void generateKey() {
		try {
			KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
			KeyPair pair = gen.generateKeyPair();
			//pair.getPrivate().getEncoded()
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
