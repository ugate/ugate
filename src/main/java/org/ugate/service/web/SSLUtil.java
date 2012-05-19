package org.ugate.service.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SSLUtil {
	
	private static final Logger log = LoggerFactory.getLogger(SSLUtil.class);
	
	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	private SSLUtil() {
	}
	
	public static void genV3Cert() throws OperatorCreationException, CertificateException, NoSuchAlgorithmException, NoSuchProviderException, IOException {
		Security.addProvider(new BouncyCastleProvider());
		
	    // yesterday
	    Date validityBeginDate = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
	    // in 2 years
	    Date validityEndDate = new Date(System.currentTimeMillis() + 2 * 365 * 24 * 60 * 60 * 1000);

	    // GENERATE THE PUBLIC/PRIVATE RSA KEY PAIR
	    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC");
	    keyPairGenerator.initialize(1024, new SecureRandom());
	    KeyPair kp = keyPairGenerator.generateKeyPair();
	    
//		final RSAKeyPairGenerator kpg = new RSAKeyPairGenerator();
//		kpg.init(new KeyGenerationParameters(new SecureRandom(), 1024));
//		final KeyPair kp = kpg.generateKeyPair();
		
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.C, "AU");
        builder.addRDN(BCStyle.O, "The Legion of the Bouncy Castle");
        builder.addRDN(BCStyle.L, "Melbourne");
        builder.addRDN(BCStyle.ST, "Victoria");
        builder.addRDN(BCStyle.E, "feedback-crypto@bouncycastle.org");

        ContentSigner sigGen = new JcaContentSignerBuilder("SHA256WithRSAEncryption").setProvider(
        		BouncyCastleProvider.PROVIDER_NAME).build(kp.getPrivate());
        X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(builder.build(), 
        		BigInteger.valueOf(1), new Date(System.currentTimeMillis() - 50000), 
        		new Date(System.currentTimeMillis() + 50000), 
        		builder.build(), kp.getPublic());

        X509Certificate cert = new JcaX509CertificateConverter().setProvider(
        		BouncyCastleProvider.PROVIDER_NAME).getCertificate(certGen.build(sigGen));

        cert.checkValidity(new Date());
        
	    // DUMP CERTIFICATE AND KEY PAIR

	    System.out.println("======================================================");
	    System.out.println("CERTIFICATE TO_STRING");
	    System.out.println("======================================================");
	    System.out.println();
	    System.out.println(cert);
	    System.out.println();

	    System.out.println("======================================================");
	    System.out.println("CERTIFICATE PEM (to store in a cert-johndoe.pem file)");
	    System.out.println("======================================================");
	    System.out.println();
	    PEMWriter pemWriter = new PEMWriter(new PrintWriter(System.out));
	    pemWriter.writeObject(cert);
	    pemWriter.flush();
	    System.out.println();

	    System.out.println("======================================================");
	    System.out.println("PRIVATE KEY PEM (to store in a priv-johndoe.pem file)");
	    System.out.println("======================================================");
	    System.out.println();
	    pemWriter.writeObject(kp.getPrivate());
	    pemWriter.flush();
	    System.out.println();
	}
	
	public static void logCert(final X509Certificate cert, final KeyPair kp ) {
		
	}

	public static X509Certificate generateV3Certificate(KeyPair pair)
			throws InvalidKeyException, NoSuchProviderException,
			SignatureException {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

		X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();

		certGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
		certGen.setIssuerDN(new X500Principal("CN=Test Certificate"));
		certGen.setNotBefore(new Date(System.currentTimeMillis() - 10000));
		certGen.setNotAfter(new Date(System.currentTimeMillis() + 10000));
		certGen.setSubjectDN(new X500Principal("CN=Test Certificate"));
		certGen.setPublicKey(pair.getPublic());
		certGen.setSignatureAlgorithm("SHA256WithRSAEncryption");

		certGen.addExtension(X509Extensions.BasicConstraints, true,
				new BasicConstraints(false));
		certGen.addExtension(X509Extensions.KeyUsage, true, new KeyUsage(
				KeyUsage.digitalSignature | KeyUsage.keyEncipherment));
		certGen.addExtension(X509Extensions.ExtendedKeyUsage, true,
				new ExtendedKeyUsage(KeyPurposeId.id_kp_serverAuth));

		certGen.addExtension(X509Extensions.SubjectAlternativeName, false,
				new GeneralNames(new GeneralName(GeneralName.rfc822Name,
						"test@test.test")));

		return certGen.generateX509Certificate(pair.getPrivate(), "BC");
	}
	public static void main(String[] args) {
		try {
			genV3Cert();
		} catch (final Throwable t) {
			t.printStackTrace();
		}
	}
}
