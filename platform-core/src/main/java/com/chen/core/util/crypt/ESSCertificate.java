package com.chen.core.util.crypt;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import com.chen.core.util.Base64Utils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class ESSCertificate {
	//获取证书的公钥
	public static byte[] GetCertPublicKey(byte[] bCert)
	{
		Security.addProvider(new BouncyCastleProvider());  	
		X509Certificate x509Certificate = null;
		CertificateFactory certificateFactory;
		try {
			certificateFactory = CertificateFactory.getInstance("X.509","BC");
			ByteArrayInputStream bInput = new ByteArrayInputStream(bCert); 
			x509Certificate = (X509Certificate) certificateFactory.generateCertificate(bInput);
			return x509Certificate.getPublicKey().getEncoded();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;	
	}
	
	//获取证书的公钥
	public static PublicKey GetCertPublicKey2(byte[] bCert)
	{
		Security.addProvider(new BouncyCastleProvider());  	
		X509Certificate x509Certificate = null;
		CertificateFactory certificateFactory;
		try {
			certificateFactory = CertificateFactory.getInstance("X.509","BC");
			ByteArrayInputStream bInput = new ByteArrayInputStream(bCert); 
			x509Certificate = (X509Certificate) certificateFactory.generateCertificate(bInput);
			return x509Certificate.getPublicKey();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;	
	}
	

	//解析证书
	public static CertificateInfo GetCertInfo(byte[] bCert){
		Security.addProvider(new BouncyCastleProvider());  	
		X509Certificate x509Certificate = null;
		CertificateFactory certificateFactory;
		CertificateInfo certInfo = new CertificateInfo();
		try {
			certificateFactory = CertificateFactory.getInstance("X.509","BC");
			ByteArrayInputStream bInput = new ByteArrayInputStream(bCert); 
			x509Certificate = (X509Certificate) certificateFactory.generateCertificate(bInput);
			certInfo.sIssuerInfo = x509Certificate.getIssuerDN().getName();
			certInfo.sCertNo = x509Certificate.getSerialNumber().toString();
			certInfo.sSignAlgName = x509Certificate.getSigAlgName().toString();
			Date dt = x509Certificate.getNotBefore();
			DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.CHINA);
			certInfo.sTimeBegin = df.format(dt);
			dt = x509Certificate.getNotAfter();
			certInfo.sTimeEnd = df.format(dt);
			certInfo.sUserInfo = x509Certificate.getSubjectDN().toString();
			return certInfo;
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	// 验证证书的颁发者
	// bUserCert	:	用户证书
	// bRootCert	:	颁发者证书
	public static boolean VerifyIssuer(byte[] bUser,byte[] bRoot)
	{
		Security.addProvider(new BouncyCastleProvider());  	
		CertificateFactory certificateFactory;
		try {
			certificateFactory = CertificateFactory.getInstance("X.509","BC");
			ByteArrayInputStream bInput = new ByteArrayInputStream(bRoot); 
			X509Certificate trustedCert = (X509Certificate) certificateFactory.generateCertificate(bInput);
			bInput.close();
			
			bInput = new ByteArrayInputStream(bUser); 
			X509Certificate userCert = (X509Certificate) certificateFactory.generateCertificate(bInput);
			bInput.close();
			
	        HashSet<TrustAnchor> trustAnchors = new HashSet<TrustAnchor>();
	        trustAnchors.add(new TrustAnchor(trustedCert, null));


	        CertificateFactory factory = CertificateFactory.getInstance("X509","BC");
	        List<X509Certificate> certList = new ArrayList<X509Certificate>();
	        certList.add(userCert);
	        CertPath certPath = factory.generateCertPath(certList);
	        PKIXParameters parameters = new PKIXParameters(trustAnchors);
	        parameters.setRevocationEnabled(false);
	        CertPathValidator validator = CertPathValidator.getInstance("PKIX","BC");
	        
	        PKIXCertPathValidatorResult result = (PKIXCertPathValidatorResult) validator.validate(certPath, parameters);
	        TrustAnchor trustAnchor = result.getTrustAnchor();
	        if(trustAnchor == null)
	        	return false;
	        else
	        	return true;
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertPathValidatorException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public static void main(String[] args) throws IOException, GeneralSecurityException, NoSuchAlgorithmException {

		byte[] a =  Base64Utils.ESSGetBase64Decode("MIICTzCCAfWgAwIBAgICCCEwCgYIKoEcz1UBg3UwgZcxCzAJBgNVBAYTAkNOMRAwDgYDVQQIDAdCZWlqaW5nMRAwDgYDVQQHDAdCZWlqaW5nMRswGQYDVQQKDBJDaGVuZ0xpVG9uZy5Dby5MTVQxEzARBgNVBAsMClNlYWxDZW50ZXIxEzARBgNVBAMMClNlYWxDZW50ZXIxHTAbBgkqhkiG9w0BCQEWDmNsdGVzc0AxNjMuY29tMB4XDTIwMDkwNDExMTk0M1oXDTMwMDkwMjExMTk0M1owYTELMAkGA1UEBhMCQ04xFDASBgNVBAgMC0JlaWppbmcgQ0xUMRQwEgYDVQQKDAtCZWlqaW5nIENMVDEPMA0GA1UECwwGQ2UgU2hpMRUwEwYDVQQDDAzmtYvor5XljbDnq6AwWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAAQKP6qmCXeXk669RDJfG6TjwxJ27Cb2coqr2aax5WASUlGUeRiSzGFwRNrhOnJvwqHMkopwZS0KlVdGK/xDJmECo2YwZDAMBgNVHRMBAf8EAjAAMAsGA1UdDwQEAwICBDAdBgNVHQ4EFgQUrhe2UeWhknyPgFA+cVBz/hQeEhYwKAYJYIZIAYb4QgENBBsWGWV4YW1wbGUgY29tbWVudCBleHRlbnNpb24wCgYIKoEcz1UBg3UDSAAwRQIhAIB+iB5JRJzgcvenvtqkdBTvF93VwQ6LnZR66gouYNRRAiBGZcHrxUUE9qIbjvVTWHBT1OWWMT8kU/EGuFD7a5sUFw==");
		byte[] b = Base64Utils.ESSGetBase64Decode("MIIB0jCCAXegAwIBAgIFFISq6WAwDAYIKoEcz1UBg3UFADA+MQswCQYDVQQGEwJDTjEQMA4GA1UECgwHVG9iYWNjbzEdMBsGA1UEAwwU54Of6I2J6KGM5Lia5qC5U00yQ0EwHhcNMTkwMjEyMDMwODM1WhcNNDkwMjEyMDMwODM1WjA+MQswCQYDVQQGEwJDTjEQMA4GA1UECgwHVG9iYWNjbzEdMBsGA1UEAwwU54Of6I2J6KGM5Lia5qC5U00yQ0EwWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAARQ0OJGt23GBrmJUirhuktCYwH2S+ekpz9/SSAEHyurCviLjIJEqHbKZHqyJnVWo9OAvpNRNON1N78puN5ZUaxbo2AwXjAfBgNVHSMEGDAWgBQwn+MD36QFmYw9/g/A5qEdR6w7+jAPBgNVHRMBAf8EBTADAQH/MAsGA1UdDwQEAwIBBjAdBgNVHQ4EFgQUMJ/jA9+kBZmMPf4PwOahHUesO/owDAYIKoEcz1UBg3UFAANHADBEAiBsWcC0KeIBZCgUId2XUcG4xOaZkS6XQDVP8MhD3AoqIwIgJ1QjRzWe+R1qmCs7p+yQfE03OLWe6uI0NM//0Hu0lJI=");
		System.out.println(VerifyIssuer(a,b));

        // build a valid cerificate path
//        CertPathBuilder certPathBuilder = CertPathBuilder.getInstance("PKIX", "BC");
//        PKIXBuilderParameters certPathBuilderParams = new PKIXBuilderParameters(trustAnchors, certSelector);

//        certPathBuilderParams.addCertStore(certStore);
//        certPathBuilderParams.setRevocationEnabled(false);
//        CertPathBuilderResult result = certPathBuilder.build(certPathBuilderParams);
//
//        // get and show cert path
//        CertPath certPath = result.getCertPath();
//        
//        if(certPath != null)
//        	System.out.println("YES");
        	
		
		
//		File f = new File("d:/test/山西/rsauser.cer");
//		FileInputStream fis = new FileInputStream(f);
//		int iLen = (int)f.length();
//		byte[] b = new byte[iLen];
//		fis.read(b);
//		fis.close();
//		
//		CertificateInfo cert = ESSCertificate.GetCertInfo(b);
//		if(cert != null)
//		{
//			System.out.println(cert.sCertNo);
//			System.out.println(cert.sIssuerInfo);
//			System.out.println(cert.sSignAlgName);
//			System.out.println(cert.sTimeBegin);
//			System.out.println(cert.sTimeEnd);
//			System.out.println(cert.sUserInfo);
//		}
	}
}

