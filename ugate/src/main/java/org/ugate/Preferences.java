package org.ugate;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;

public class Preferences {

	private static final Logger log = Logger.getLogger(Preferences.class);
	public static final String ENCRYPTION_TYPE = "AES";
	public static final String ENCRYPTION_POSTFIX = ".encrypted";
	private final Properties properties;
	private final String fileName;
	private SecretKeySpec skeySpec;

	public Preferences(String fileName) {
		this.fileName = fileName;
		this.properties = new Properties();
		try {
			KeyGenerator kgen = KeyGenerator.getInstance(ENCRYPTION_TYPE);
			kgen.init(128);
			SecretKey skey = kgen.generateKey();
			byte[] raw = skey.getEncoded();
			this.skeySpec = new SecretKeySpec(raw, ENCRYPTION_TYPE);
		} catch (NoSuchAlgorithmException e) {
			log.warn(e);
		}
		try {
			this.properties.load(new FileInputStream(fileName + ".properties"));
		} catch (FileNotFoundException e) {
	        BufferedWriter out = null;
			try {
				out = new BufferedWriter(new FileWriter(fileName + ".properties"));
		        out.write("");
			} catch (IOException e2) {
				throw new RuntimeException("Unable to create preferences file \"" + fileName + ".properties\"", e2);
			} finally {
				if (out != null) {
					try {
						out.close();
						this.properties.load(new FileInputStream(fileName + ".properties"));
					} catch (IOException e3) {
						log.error(e3);
					}
				}
			}
		} catch (IOException e) {
			log.error("Unable to retrieve preferences", e);
		}
	}
	
	public List<String> get(String key, String delimiter) {
		final String value = get(key);
		return value != null ? Arrays.asList(value.split(delimiter)) : new ArrayList<String>();
	}

	public String get(String key) {
		if (properties.containsKey(key)) {
			return properties.getProperty(key);
		}
		String value = "";
		String eValue = properties.getProperty(key + ENCRYPTION_POSTFIX);
		if (eValue != null) {
			try {
				value = new String(encryptDecrypt(eValue, Cipher.DECRYPT_MODE));
			} catch (Exception e) {
				throw new RuntimeException("Unable to encrypt", e);
			}
		}
		return value;
	}

	public void set(String key, String value) {
		set(key, value, false);
	}

	// TODO : write secret key to Arduino EEPROM so encrypted preference values can be used?
	private void set(String key, String value, boolean encrypt) {
		try {
			if (encrypt) {
				try {
					value = asHex(encryptDecrypt(value, Cipher.ENCRYPT_MODE));
					key += ENCRYPTION_POSTFIX;
				} catch (Exception e) {
					throw new RuntimeException("Unable to encrypt");
				}
			}
			properties.setProperty(key, value);
			properties.store(new FileOutputStream(fileName + ".properties"),
					null);
		} catch (IOException e) {
			log.error("Unable to store preferences", e);
		}
	}

	private byte[] encryptDecrypt(String value, int encryptDecryptMode) throws NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance(ENCRYPTION_TYPE);
		cipher.init(encryptDecryptMode, skeySpec);
		return cipher.doFinal(value.getBytes());
	}

	/**
	 * Turns array of bytes into string
	 * 
	 * @param buf
	 *            Array of bytes to convert to hex string
	 * @return Generated hex string
	 */
	private static String asHex(byte buf[]) {
		StringBuffer strbuf = new StringBuffer(buf.length * 2);
		for (int i = 0; i < buf.length; i++) {
			if (((int) buf[i] & 0xff) < 0x10) {
				strbuf.append("0");
			}
			strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
		}
		return strbuf.toString();
	}
}
