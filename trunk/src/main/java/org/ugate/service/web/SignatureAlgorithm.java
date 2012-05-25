package org.ugate.service.web;

import java.security.cert.X509Certificate;

/**
 * <p>
 * {@linkplain X509Certificate} signing algorithms. Some web browsers will not
 * recognize {@linkplain X509Certificate}s signed using some of the algorithms.
 * </p>
 * <p>
 * For example, IE 8 does not recognize {@linkplain #SHA256WithRSAEncryption}
 * while FireFox 12.0 does. While {@linkplain #SHA1withRSA} is recognized in all
 * modern browser versions.
 * </p>
 */
public enum SignatureAlgorithm {
	GOST3411withGOST3410, GOST3411withECGOST3410, MD2withRSA, MD5withRSA, 
	SHA1withRSA, RIPEMD128withRSA, RIPEMD160withRSA, RIPEMD160withECDSA, 
	RIPEMD256withRSA, SHA1withDSA, SHA1withECDSA, SHA224withECDSA, 
	SHA256withECDSA, SHA384withECDSA, SHA512withECDSA, SHA224withRSA, 
	SHA256withRSA, SHA384withRSA, SHA512withRSA, SHA1withRSAandMGF1, 
	SHA256withRSAandMGF1, SHA384withRSAandMGF1, SHA512withRSAandMGF1;
	
	/**
	 * @return the default {@linkplain SignatureAlgorithm}
	 */
	public static SignatureAlgorithm getDefault() {
		return SHA1withRSA;
	}
}
