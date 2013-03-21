package org.ugate.service;

import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.util.encoders.Base64;

/**
 * {@link SecurityProvider} with {@link #encrypt(String)} and
 * {@link #decrypt(String)} functionality
 */
public class SecurityEncryptionProvider extends SecurityProvider {

	// NIST specifically names SHA-1 "PBKDF2WithHmacSHA1" as an acceptable
	// hashing algorithm for PBKDF2 ("PBKDF2WithHmacSHA256" requires
	// unrestricted policy files for key size over 128:
	// http://docs.oracle.com/javase/7/docs/technotes/guides/security/SunProviders.html)
	private static final String ALGORITHM = "PBKDF2WithHmacSHA1";
	private static final String ALGORITHM_STANDARD = "AES";
	// SHA-1 generates 160 bit hashes, SHA-256 generates 256 bit hashes
	private static final int KEY_LENGTH = 128;
	// NIST recommends 1,000 iterations >= 1,000
	// http://csrc.nist.gov/publications/nistpubs/800-132/nist-sp800-132.pdf
	private static final int ITERATION_COUNT = 65536;
	private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
	private static final byte[] SALT = toBytes("tl1ms;1snw.hmm2ld1gp:hlmbtsw.hrms:hlm1tporfhns.yt1wttvotsod,1wfne:ftawmtr&tstcm.tp@tbm1tpome:tamhwo;mcro.sg&msfmatdoml:&1WD1THOTL4E.");
	private static final String CHAR_ENCODING = "UTF8";
	private final SecretKey secretKey;
	private static final int IV_LENGTH = 16;

	/**
	 * Constructor
	 * 
	 * @param passPhrase
	 *            the password phrase used for {@link #encrypt(String)} and
	 *            {@link #decrypt(String)}
	 * @throws Exception
	 *             thrown when {@link EncyptionProvider} initialization
	 *             fails
	 */
	public SecurityEncryptionProvider(final String passPhrase) throws Exception {
		final SecretKeyFactory secretKeyFactory = SecretKeyFactory
				.getInstance(ALGORITHM);
		final KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray(),
				SALT, ITERATION_COUNT, KEY_LENGTH);
		final SecretKey secretKeyTemp = secretKeyFactory
				.generateSecret(keySpec);
		this.secretKey = new SecretKeySpec(secretKeyTemp.getEncoded(),
				ALGORITHM_STANDARD);
	}

	/**
	 * Encrypts a given password
	 * 
	 * @param pwd
	 *            the password to encrypt
	 * @return the encrypted password
	 * @throws Exception
	 *             thrown when the encryption fails
	 */
	public String encrypt(final String pwd) throws Exception {
		final Cipher eCipher = Cipher.getInstance(TRANSFORMATION);
		eCipher.init(Cipher.ENCRYPT_MODE, secretKey);
		final byte[] encrypted = eCipher.doFinal(toBytes(pwd));
		byte[] iv = eCipher.getParameters()
				.getParameterSpec(IvParameterSpec.class).getIV();
		byte[] cipherText = new byte[encrypted.length + iv.length];
		System.arraycopy(iv, 0, cipherText, 0, iv.length);
		System.arraycopy(encrypted, 0, cipherText, iv.length,
				encrypted.length);
		return new String(Base64.encode(cipherText));
	}

	/**
	 * Decrypts a given password
	 * 
	 * @param pwd
	 *            the encrypted password
	 * @return the clear text password
	 * @throws Exception
	 *             thrown when decryption fails
	 */
	public String decrypt(final String pwd) throws Exception {
		final byte[] encrypted = Base64.decode(pwd);
		byte[] iv = new byte[IV_LENGTH];
		System.arraycopy(encrypted, 0, iv, 0, iv.length);
		final Cipher dCipher = Cipher.getInstance(TRANSFORMATION);
		dCipher.init(Cipher.DECRYPT_MODE, secretKey,
				new IvParameterSpec(iv));
		byte[] cipherText = new byte[encrypted.length - iv.length];
		System.arraycopy(encrypted, 16, cipherText, 0, cipherText.length);
		return new String(dCipher.doFinal(cipherText), CHAR_ENCODING);
		// DatatypeConverter.printHexBinary(eCipher.doFinal(pwd.getBytes("UTF8")));
	}

	/**
	 * Converts a {@link String} to bytes
	 * 
	 * @param source
	 *            the source {@link String}
	 * @return the bytes
	 */
	protected static byte[] toBytes(final String source) {
		try {
			return source.getBytes(CHAR_ENCODING);
		} catch (final Throwable t) {
			if (RuntimeException.class.isAssignableFrom(t.getClass())) {
				throw (RuntimeException) t;
			} else {
				throw new RuntimeException(t);
			}
		}
	}
}
