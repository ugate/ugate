package org.ugate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
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

/**
 * Storage files are used to store simple key/value pair data to disk. The file approach is taken
 * versus the typical {@link java.util.prefs.Preferences} so they can be readily accessible for
 * manual editing when needed.
 */
public class StorageFile {

	private static final Logger log = Logger.getLogger(StorageFile.class);
	public static final String FILE_EXTENSION = ".properties";
	public static final String ENCRYPTION_TYPE = "AES";
	public static final String ENCRYPTION_POSTFIX = ".encrypted";
	private final Properties properties;
	private final String filePath;
	private SecretKeySpec skeySpec;
	private boolean isLoaded = false;
	private String absoluteFilePath;

	/**
	 * Storage file that can be used to store simple key/value pairs (generated when non-existent)
	 * 
	 * @param filePath the file path (without a file extension)
	 * @param createIfNotExists true to create the storage file when it doesn't exist
	 */
	public StorageFile(final String filePath, final boolean createIfNotExists) {
		this.filePath = filePath + (filePath.indexOf(FILE_EXTENSION) > -1 ? "" : FILE_EXTENSION);
		this.properties = new Properties();
		try {
			final File file = new File(this.filePath);
			this.absoluteFilePath = file.getAbsolutePath();
			if (!file.exists() && file.canWrite() && createIfNotExists) {
				file.createNewFile();
			}
			this.properties.load(new FileInputStream(this.filePath));
			this.isLoaded = true;
			initEncryption();
		} catch (final FileNotFoundException e) {
			if (!createIfNotExists) {
				log.debug(String.format("Unable to find storage file: %1$s", this.filePath));
				return;
			}
			create(this.filePath);
		} catch (final IOException e) {
			log.error("Unable to retrieve/create storage file", e);
		}
	}
	
	/**
	 * Creates a copy of the current storage file at the specified path
	 * 
	 * @param filePath the path to the new/existing file copy (without a file extension)
	 * @return a copy of the {@linkplain StorageFile} located at the specified path
	 */
	public StorageFile createCopy(final String filePath) {
		if (filePath != null && !filePath.isEmpty()) {
			final String cp = filePath + (filePath.indexOf(FILE_EXTENSION) > -1 ? "" : FILE_EXTENSION);
			FileChannel in = null;
			FileChannel out = null;
			try {
				final File fileCopy = new File(cp);
				if (!fileCopy.exists()) {
					fileCopy.createNewFile();
				}
				in = new FileInputStream(getAbsoluteFilePath()).getChannel();
				out = new FileOutputStream(cp).getChannel();
				out.transferFrom(in, 0, in.size());
			} catch (final IOException e) {
				log.warn(String.format("Unable to create storage file %1$s", cp), e);
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (final IOException e) {
						log.error(String.format("Unable to close storage file %1$s", getAbsoluteFilePath()), e);
					}
				}
				if (out != null) {
					try {
						out.close();
						return new StorageFile(filePath, false);
					} catch (final IOException e) {
						log.error(String.format("Unable to load storage file %1$s", cp), e);
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Initializes encryption
	 */
	private void initEncryption() {
		try {
			// TODO : add encryption support for properties
			final KeyGenerator kgen = KeyGenerator.getInstance(ENCRYPTION_TYPE);
			kgen.init(128);
			final SecretKey skey = kgen.generateKey();
			final byte[] raw = skey.getEncoded();
			this.skeySpec = new SecretKeySpec(raw, ENCRYPTION_TYPE);
		} catch (final NoSuchAlgorithmException e) {
			log.warn(e);
		}
	}
	
	private void create(final String filePath) {
        BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(filePath));
	        out.write("");
		} catch (final IOException e2) {
			throw new RuntimeException(String.format("Unable to create storage file %1$s", filePath), e2);
		} finally {
			if (out != null) {
				try {
					out.close();
					this.properties.load(new FileInputStream(filePath));
					this.isLoaded = true;
				} catch (final IOException e3) {
					log.error(String.format("Unable to load storage file %1$s", filePath), e3);
				}
			}
		}
	}
	
	/**
	 * @return true when the storage is loaded
	 */
	public boolean isLoaded() {
		return this.isLoaded;
	}
	
	/**
	 * @param key the key to check for
	 * @return true when the key exists
	 */
	public boolean hasKey(final String key) {
		return properties.containsKey(key);
	}
	
	/**
	 * Gets a list of storage values by key using a delimiter
	 * 
	 * @param key the storage key
	 * @param delimiter the delimiter for the multi-value preference
	 * @return the list of values
	 */
	public List<String> get(final String key, final String delimiter) {
		final String value = get(key);
		return value != null ? Arrays.asList(value.split(delimiter)) : new ArrayList<String>();
	}

	/**
	 * Gets a storage value by key. If the key does not exist it will be created
	 * 
	 * @param key the key
	 * @return the value
	 */
	public String get(final String key) {
		if (hasKey(key)) {
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

	/**
	 * Sets a key/value storage
	 * 
	 * @param key the key
	 * @param value the value
	 */
	public void set(final String key, final String value) {
		set(key, value, false);
	}
	
	/**
	 * Sets a a key/value storage
	 * 
	 * @param key the key
	 * @param value the value
	 * @param encrypt true to encrypt the value
	 */
	private void set(String key, String value, final boolean encrypt) {
		try {
			if (encrypt) {
				try {
					value = asHex(encryptDecrypt(value, Cipher.ENCRYPT_MODE));
					key += ENCRYPTION_POSTFIX;
				} catch (Exception e) {
					throw new RuntimeException("Unable to encrypt", e);
				}
			}
			properties.setProperty(key, value);
			properties.store(new FileOutputStream(filePath), null);
		} catch (IOException e) {
			log.error("Unable to save storage file", e);
		}
	}

	private byte[] encryptDecrypt(final String value, final int encryptDecryptMode) throws NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance(ENCRYPTION_TYPE);
		cipher.init(encryptDecryptMode, skeySpec);
		return cipher.doFinal(value.getBytes());
	}

	/**
	 * Turns array of bytes into string
	 * 
	 * @param buf
	 *            array of bytes to convert to hex string
	 * @return generated hex string
	 */
	private static String asHex(final byte buf[]) {
		StringBuffer strbuf = new StringBuffer(buf.length * 2);
		for (int i = 0; i < buf.length; i++) {
			if (((int) buf[i] & 0xff) < 0x10) {
				strbuf.append("0");
			}
			strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
		}
		return strbuf.toString();
	}

	/**
	 * @return the file path
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * @return the absolute file path of the storage file
	 */
	public String getAbsoluteFilePath() {
		return absoluteFilePath;
	}
}
