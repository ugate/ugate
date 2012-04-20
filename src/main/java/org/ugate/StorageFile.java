package org.ugate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ugate.resources.RS;

/**
 * {@linkplain StorageFile}s are used to store simple key/value pair data to disk. The file approach is taken
 * versus the typical {@link java.util.prefs.Preferences} so they can be readily accessible for
 * manual editing when needed.
 */
public class StorageFile {

	private static final Logger log = LoggerFactory.getLogger(StorageFile.class);
	public static final String PACKAGE_CHECK_EXTENSION = ".jar!";
	public static final String FILE_EXTENSION = ".properties";
	public static final String ENCRYPTION_TYPE = "AES";
	public static final String ENCRYPTION_POSTFIX = ".encrypted";
	private Properties properties;
	private Path filePath;
	private SecretKeySpec skeySpec;
	private boolean fromCopy = false;
	private boolean wasCreated = false;
	private boolean isLoaded = false;
	
	/**
	 * {@linkplain StorageFile} that can be used to store simple key/value pairs (generated when non-existent)
	 * 
	 * @param filePath the file {@linkplain Path} (without a file extension)
	 * @param copyFromFilePathIfNotExists the {@linkplain Path} to copy the storage file from when it doesn't exist
	 */
	public StorageFile(final Path filePath, final Path copyFromFilePathIfNotExists) {
		this(filePath, true, copyFromFilePathIfNotExists);
	}

	/**
	 * {@linkplain StorageFile} that can be used to store simple key/value pairs (generated when non-existent)
	 * 
	 * @param filePath the file path (without a file extension)
	 * @param createIfNotExists true to create the storage file when it doesn't exist
	 */
	public StorageFile(final Path filePath, final boolean createIfNotExists) {
		this(filePath, createIfNotExists, null);
	}
	
	/**
	 * {@linkplain StorageFile} that can be used to store simple key/value pairs (generated when non-existent)
	 * 
	 * @param filePath the file path (without a file extension)
	 * @param createIfNotExists true to create the storage file when it doesn't exist
	 * @param copyFromFilePathIfNotExists the path to copy the storage file from when it doesn't exist
	 */
	protected StorageFile(final Path filePath, final boolean createIfNotExists, 
			final Path copyFromFilePathIfNotExists) {
		this.filePath = pathWithExt(filePath);
		this.properties = new Properties();
		try {
			final File file = this.filePath.toFile();
			if (!file.exists() && createIfNotExists) {
				//file.canWrite()
				if (copyFromFilePathIfNotExists != null && !copyFromFilePathIfNotExists.toAbsolutePath().toString().isEmpty()) {
					if (copy(copyFromFilePathIfNotExists, this.filePath)) {
						wasCreated = true;
					} else {
						return;
					}
				} else {
					file.createNewFile();
					log.info(String.format("Created new %1$s in %2$s", 
							StorageFile.class.getSimpleName(), this.filePath));
					wasCreated = true;
				}
			}
			this.properties.load(new FileInputStream(file));
			this.isLoaded = true;
			initEncryption();
		} catch (final FileNotFoundException e) {
			if (!createIfNotExists) {
				log.debug(String.format("Unable to find %1$s: %2$s", StorageFile.class.getSimpleName(), 
						this.filePath), e);
				return;
			}
			create(this.filePath);
		} catch (final IOException e) {
			log.error(String.format("Unable to retrieve/create %1$s", StorageFile.class.getSimpleName()), e);
		}
	}
	
	/**
	 * Creates a copy of the current {@linkplain StorageFile} at the specified path
	 * 
	 * @param toFilePath the path to the new/existing file copy (without a file extension)
	 * @return a copy of the {@linkplain StorageFile} located at the specified path
	 */
	public StorageFile createCopy(final Path toFilePath) {
		if (copy(getFilePath(), toFilePath)) {
			final StorageFile copy = new StorageFile(toFilePath, false);
			copy.fromCopy = true;
			return copy;
		}
		return null;
	}
	
	/**
	 * Creates a copy of the current {@linkplain StorageFile} at the specified path
	 * 
	 * @param toFilePath the path to the new/existing file copy (without a file extension)
	 * @return a copy of the {@linkplain StorageFile} located at the specified path
	 */
	protected static boolean copy(final Path fromFilePath, final Path toFilePath) {
		boolean success = false;
		if (toFilePath != null && !toFilePath.toString().isEmpty()) {
			final Path toPath = pathWithExt(toFilePath);
			InputStream in = null;
			File fileCopy = null;
			try {
				fileCopy = toPath.toFile();
				if (!fileCopy.exists()) {
					fileCopy.createNewFile();
				}
				//final int pckIndex = fromFilePath.toAbsolutePath().toString().toLowerCase().indexOf(PACKAGE_CHECK_EXTENSION);
				Path fromPath = fromFilePath; // pckIndex > -1 ? null : fromFilePath;
				if (fromPath == null) {
					// when the from file is within a package we need to extract it to a temporary file
					in = StorageFile.class.getResourceAsStream(
							fromFilePath.toAbsolutePath().toString());
					Files.copy(in, toPath, StandardCopyOption.REPLACE_EXISTING);
				} else {
					if (fromPath.getFileSystem().isOpen()) {
						Files.copy(fromPath, toPath, StandardCopyOption.REPLACE_EXISTING);
					} else {
						FileSystem fromFs = null;
						try {
							log.debug(String.format("Opening %1$s in order to copy to %2$s", fromPath.toAbsolutePath(), toPath));
							fromFs = RS.applicationFileSystem();
							fromPath = fromFs.getPath(fromPath.toString());
							Files.copy(fromPath, toPath, StandardCopyOption.REPLACE_EXISTING);
						} finally {
							if (fromFs != null) {
								fromFs.close();
							}
						}
					}
					
				}
				success = true;
			} catch (final IOException e) {
				log.warn(String.format("Unable to copy file from %1$s to %2$s", fromFilePath, toPath), e);
			} finally {
				// TODO : can move to java 7 try block, but IDE may not like
				if (in != null) {
					try {
						in.close();
						log.info(String.format("Copied file from %1$s to %2$s", fromFilePath, toPath));
					} catch (final IOException e) {
						log.error(String.format("Unable to close storage file %1$s", fromFilePath), e);
					}
				}
			}
		}
		return success;
	}
	
	/**
	 * Ensures the specified {@linkplain Path} has the required {@linkplain #FILE_EXTENSION}
	 * 
	 * @param path the {@linkplain Path} to check
	 * @return the {@linkplain Path} with the required {@linkplain #FILE_EXTENSION}
	 */
	protected static Path pathWithExt(final Path path) {
		if (path.getFileName().toString().indexOf(FILE_EXTENSION) > -1) {
			return path;
		}
		FileSystem fs = null;
		try {
			fs = path.getFileSystem();
			return fs.getPath(path.toAbsolutePath() + FILE_EXTENSION).toAbsolutePath();
		} finally {
			try {
				fs.close();
			} catch (final Exception e) {
			}
		}
	}
	
	/**
	 * Deletes the {@linkplain StorageFile} and disposes of any residual data
	 * 
	 * @return true when successful
	 */
	public boolean dispose() {
		try {
			this.filePath.toFile().delete();
			return true;
		} catch (final Throwable t) {
			log.error(String.format("Unable to delete %1$s: %2$s", StorageFile.class.getSimpleName(), 
					this.filePath.toAbsolutePath().toString()), t);
		}
		return false;
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
			log.warn("Encryption failed", e);
		}
	}
	
	/**
	 * Creates the actual {@linkplain StorageFile} and stores it to disk
	 * 
	 * @param filePath the file path to write to
	 */
	private void create(final Path filePath) {
        BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(filePath.toFile()));
	        out.write("");
		} catch (final IOException e2) {
			throw new RuntimeException(String.format("Unable to create storage file %1$s", filePath), e2);
		} finally {
			if (out != null) {
				try {
					out.close();
					this.properties.load(new FileInputStream(filePath.toFile()));
					this.isLoaded = true;
				} catch (final IOException e3) {
					log.error(String.format("Unable to load storage file %1$s", filePath), e3);
				}
			}
		}
	}
	
	/**
	 * @return true when the {@linkplain StorageFile} is loaded
	 */
	public boolean isLoaded() {
		return this.isLoaded;
	}
	
	/**
	 * @return true when the {@linkplain File} was created on initialization of the {@linkplain StorageFile}
	 */
	public boolean wasCreated() {
		return wasCreated;
	}
	
	/**
	 * @return true when the {@linkplain StorageFile} was created from another {@linkplain StorageFile}
	 */
	public boolean fromCopy() {
		return fromCopy;
	}
	
	/**
	 * Determines an entry in the {@linkplain StorageFile} exists
	 * 
	 * @param key the key to check for
	 * @return true when the key exists
	 */
	public boolean hasKey(final String key) {
		return properties.containsKey(key);
	}
	
	/**
	 * Gets a list of {@linkplain StorageFile} values by key using a delimiter
	 * 
	 * @param key the {@linkplain StorageFile} parameter key
	 * @param delimiter the delimiter for the multi-value preference
	 * @return the list of values
	 */
	public List<String> get(final String key, final String delimiter) {
		final String value = get(key);
		return value != null ? Arrays.asList(value.split(delimiter)) : new ArrayList<String>();
	}

	/**
	 * Gets a {@linkplain StorageFile} value by key. If the key does not exist it will be created
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
	 * Sets a key/value within the {@linkplain StorageFile}
	 * 
	 * @param key the key
	 * @param value the value
	 */
	public void set(final String key, final String value) {
		set(key, value, false);
	}
	
	/**
	 * Sets a a key/value within the {@linkplain StorageFile}
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
			properties.store(new FileOutputStream(this.filePath.toFile()), null);
		} catch (IOException e) {
			log.error("Unable to save storage file", e);
		}
	}

	/**
	 * Encrypts or Decrypts a string
	 * 
	 * @param value the value to encrypt/decrypt
	 * @param encryptDecryptMode
	 * @return the encrypted or decrypted bytes
	 * @throws NoSuchAlgorithmException {@linkplain Cipher#getInstance(String)}
	 * @throws NoSuchPaddingException {@linkplain Cipher#getInstance(String)}
	 * @throws InvalidKeyException {@linkplain Cipher#init(int, java.security.cert.Certificate)}
	 * @throws IllegalBlockSizeException {@linkplain Cipher#doFinal(byte[])}
	 * @throws BadPaddingException {@linkplain Cipher#doFinal(byte[])}
	 */
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
	 * @return the file {@linkplain Path} of the {@linkplain StorageFile}
	 */
	public Path getFilePath() {
		return filePath;
	}
}
