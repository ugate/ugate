package org.ugate.service.web;

import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;

import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ugate.UGateUtil;

/**
 * SSL certificate holder
 */
public class CertificateHolder {
	
	private static final Logger log = LoggerFactory.getLogger(CertificateHolder.class);
	public static final String SIGNING_ALGORITHM = "SHA256WithRSAEncryption"; //"SHA1withRSA"; // "SHA256WithRSAEncryption";
	/**
	 * The key size used by the {@linkplain CertificateHolder}. In 2030 expiry
	 * of 2048 bits will occur. <a
	 * href="http://www.zytrax.com/tech/survival/ssl.html#self">Self Signed
	 * Certificates</a>
	 */
	public static final int KEY_SIZE = 2048;
	private final X509Certificate certificate;
	private final KeyPair keyPair;
	private final KeyStore keyStore;
	
	static {
		// add the bouncy castle security provider
		Security.insertProviderAt(new BouncyCastleProvider(), 1);
		//System.setProperty("ssl.KeyManagerFactory.algorithm", "X509");
	}

	/**
	 * Constructor
	 * 
	 * @param certificate
	 *            the {@linkplain X509Certificate}
	 * @param keyPair
	 *            the {@linkplain KeyPair}
	 * @param keyStore
	 *            the {@linkplain KeyStore}
	 */
	private CertificateHolder(final X509Certificate certificate, final KeyPair keyPair,
			final KeyStore keyStore) {
		this.certificate = certificate;
		this.keyPair = keyPair;
		this.keyStore = keyStore;
	}
	
	/**
	 * Creates a new {@linkplain CertificateHolder} for a self-signed X.509
	 * version 3 certificate
	 * 
	 * @param countryCode
	 *            two character country code of the issuer (i.e. AU, US, etc.)
	 * @param organizationName
	 *            the organization name of the issuer
	 * @param localityName
	 *            the locality name (city) of the issuer
	 * @param state
	 *            the state of the issuer
	 * @param emailAddress
	 *            the email address of the issuer
	 * @param commonName
	 *            the common name of the issuer (i.e. www.example.com)
	 * @param keyStorePassword
	 *            the password for the {@linkplain KeyStore}
	 */
	public static CertificateHolder newSelfSignedCertificate(final String countryCode, final String organizationName, 
			final String localityName, final String state, final String emailAddress, final String commonName,
			final String keyStorePassword) {
		try {
			if (log.isInfoEnabled()) {
				log.info(String.format("Creating self signed certificate for: country = %1$s, organization = %2$s," 
						+ " locality = %3$s, state = %4$s, email = %5$s", countryCode, organizationName, 
						localityName, state, emailAddress));
			}
		
			// yesterday
			final Calendar calBegin = Calendar.getInstance();
			calBegin.add(Calendar.DATE, -1);
			// in 10 years
			final Calendar calEnd = Calendar.getInstance();
			calEnd.add(Calendar.YEAR, 10);

			// generate the public and private RSA key pair using the
			// BouncyCastle provider with a KEY_SIZE bit key size for
			// the digital signature
			final KeyPairGenerator keyPairGenerator = KeyPairGenerator
					.getInstance("RSA");
			keyPairGenerator.initialize(KEY_SIZE, new SecureRandom());
			KeyPair kp = keyPairGenerator.generateKeyPair();
			
//			final PKCS12BagAttributeCarrier bagAttr =(PKCS12BagAttributeCarrier) kp.getPrivate();
//			bagAttr.setBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_localKeyId, 
//					new SubjectKeyIdentifier(kp.getPublic().getEncoded()));
			
			// build the X500 name
			final X500NameBuilder builder = new X500NameBuilder(
					BCStyle.INSTANCE);
			builder.addRDN(BCStyle.C, countryCode);
			builder.addRDN(BCStyle.O, organizationName);
			builder.addRDN(BCStyle.OU, organizationName);
			builder.addRDN(BCStyle.L, localityName);
			builder.addRDN(BCStyle.ST, state);
			builder.addRDN(BCStyle.E, emailAddress);
			builder.addRDN(BCStyle.CN, commonName);
			
			final ContentSigner sigGen = new JcaContentSignerBuilder(
					SIGNING_ALGORITHM).setProvider(
					BouncyCastleProvider.PROVIDER_NAME).build(kp.getPrivate());
			// JcaX509v3CertificateBuilder parameters:
			// issuer X500Name representing the issuer of this certificate.
			// serial the serial number for the certificate. 
			// notBefore date before which the certificate is not valid. 
			// notAfter date after which the certificate is not valid. 
			// subject X500Name representing the subject of this certificate.
			// publicKey the public key to be associated with the certificate.
			final X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(
					builder.build(), generateSerialNumber(BigInteger.valueOf(KEY_SIZE)), 
					calBegin.getTime(), calEnd.getTime(), builder.build(), 
					kp.getPublic());
			certGen.addExtension(X509Extension.subjectKeyIdentifier, false, 
					new SubjectKeyIdentifier(kp.getPublic().getEncoded()));
			certGen.addExtension(X509Extension.basicConstraints, false, 
					new BasicConstraints(0));
			// convert the certificate to a standard one
			final X509Certificate cert = new JcaX509CertificateConverter()
					.setProvider(BouncyCastleProvider.PROVIDER_NAME)
					.getCertificate(certGen.build(sigGen));
			
			cert.checkValidity(new Date());

			// build key store
			final KeyStore ks = KeyStore.getInstance("PKCS12", BouncyCastleProvider.PROVIDER_NAME);
			//ks.load(new ByteArrayInputStream(cert.getEncoded()), "testMe".toCharArray());
			ks.load(null, null);
			//ks.setCertificateEntry(organizationName, cert);
			ks.setKeyEntry(organizationName, kp.getPrivate(), 
					keyStorePassword.toCharArray(), new X509Certificate[] { cert });
			
			return new CertificateHolder(cert, kp, ks);
		} catch (final Throwable t) {
			log.error("Unable to generate a self signed certificate", t);
			return null;
		}
	}
	
//	static SealedObject encrypt(final String data) throws Exception{
//	    PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, 20);
//	    PBEKeySpec pbeKeySpec = new PBEKeySpec(passPherase);
//	    SecretKeyFactory secretKeyFactory = 
//	        SecretKeyFactory.getInstance(algorithm);
//	    SecretKey secretKey = secretKeyFactory.generateSecret(pbeKeySpec);
//
//	    Cipher cipher = Cipher.getInstance(algorithm);
//	    cipher.init(Cipher.ENCRYPT_MODE, secretKey, pbeParamSpec);
//
//	    return new SealedObject(data, cipher);
//	}

	/**
	 * Generates a serial number for a {@linkplain JcaX509v3CertificateBuilder}
	 * 
	 * @param numberOfBits
	 *            the number of bits the serial number will have
	 * @return the serial number
	 */
	private static BigInteger generateSerialNumber(final BigInteger numberOfBits) {
		final Random rnd = new Random();
		final int nlen = numberOfBits.bitLength();
		final BigInteger nm1 = numberOfBits.subtract(BigInteger.ONE);
		BigInteger r, s;
		do {
		    s = new BigInteger(nlen + 100, rnd);
		    r = s.mod(numberOfBits);
		} while (s.subtract(r).add(nm1).bitLength() >= nlen + 100);
		return r;
	}
	
	public String getAlias() {
		try {
			return getKeyStore().getCertificateAlias(getCertificate());
		} catch (final KeyStoreException e) {
			log.warn("Unable to get certificate alias", e);
			return null;
		}
	}
	
	public KeyStore getKeyStore() {
		return keyStore;
	}
	
	public KeyStore getTrustStore() {
		return null;
	}

	/**
	 * Logs the {@linkplain #getCertificate()} and {@linkplain #getKeyPair()}
	 */
	public void dumpLog() {
		try {
			SSLContext context = SSLContext.getInstance("SSL");
	        context.init(null, null, null);
	        SSLParameters parameters = context.getDefaultSSLParameters();
        	UGateUtil.PLAIN_LOGGER.info("======================================================");
        	UGateUtil.PLAIN_LOGGER.info("Supported Cipher Suites:");
	        for(final String s : parameters.getCipherSuites()) {
	        	UGateUtil.PLAIN_LOGGER.info(s);
	        }
	        SSLContext context2 = SSLContext.getInstance("TLSv1");
	        context2.init(null, null, null);
	        SSLParameters parameters2 = context2.getDefaultSSLParameters();
        	UGateUtil.PLAIN_LOGGER.info("======================================================");
        	UGateUtil.PLAIN_LOGGER.info("Supported Cipher Suites:");
	        for(final String s : parameters2.getCipherSuites()) {
	        	UGateUtil.PLAIN_LOGGER.info(s);
	        }
		    UGateUtil.PLAIN_LOGGER.info("======================================================");
		    UGateUtil.PLAIN_LOGGER.info("CERTIFICATE INFO");
		    UGateUtil.PLAIN_LOGGER.info("======================================================");
		    UGateUtil.PLAIN_LOGGER.info(getCertificate().toString());
	
		    UGateUtil.PLAIN_LOGGER.info("======================================================");
		    UGateUtil.PLAIN_LOGGER.info("CERTIFICATE PEM (to store in a certificate.pem file)");
		    UGateUtil.PLAIN_LOGGER.info("======================================================");
		    final PEMWriter pemWriter = new PEMWriter(new PrintWriter(System.out));
		    pemWriter.writeObject(getCertificate());
		    pemWriter.flush();
	
		    UGateUtil.PLAIN_LOGGER.info("======================================================");
		    UGateUtil.PLAIN_LOGGER.info("PRIVATE KEY PEM (to store in a private.pem file)");
		    UGateUtil.PLAIN_LOGGER.info("======================================================");
		    pemWriter.writeObject(getKeyPair().getPrivate());
		    pemWriter.flush();
		} catch (final Throwable t) {
			log.warn("Unable to print " + this.toString(), t);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return getCertificate().toString() + getKeyPair().toString();
	}

	/**
	 * @return the {@linkplain X509Certificate}
	 */
	public X509Certificate getCertificate() {
		return certificate;
	}

	/**
	 * @return the {@linkplain KeyPair}
	 */
	public KeyPair getKeyPair() {
		return keyPair;
	}

	public static void main(String[] args) {
		try {
			final CertificateHolder cert = newSelfSignedCertificate("AU", 
					"The Legion of the Bouncy Castle", "Melbourne", 
					"Victoria", "feedback-crypto@bouncycastle.org", 
					"www.example.com", "testPassword");
			cert.dumpLog();
		} catch (final Throwable t) {
			t.printStackTrace();
		}
	}
}
