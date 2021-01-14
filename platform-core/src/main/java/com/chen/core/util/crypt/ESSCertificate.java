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
import java.util.*;

import com.chen.core.util.Base64Utils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import static com.chen.core.util.Base64Utils.decoderBase64File;
import static com.chen.core.util.Base64Utils.encodeBase64File;
import static com.chen.core.util.FileUtils.byte2File;
import static com.chen.core.util.StringUtils.getUUID;

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
	        if(trustAnchor == null){
				String  ran2 = getUUID();
				String path = "/opt/esstempfile/";
				byte2File(bUser,path, "user"+ran2+".cer");
				byte2File(bRoot,path, "bRoot"+ran2+".cer");
				return false;
			} else{
				return true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			String  ran2 = getUUID();
			String path = "/opt/esstempfile/";
			byte2File(bUser,path, "user"+ran2+".cer");
			byte2File(bRoot,path, "bRoot"+ran2+".cer");
			System.out.println("验证异常"+ran2);
		}
		return false;
	}
	
	public static void main(String[] args) throws Exception {

//		byte[] a =  Base64Utils.ESSGetBase64Decode("MIICQTCCAeSgAwIBAgIFb3itY1YwDAYIKoEcz1UBg3UFADA1MQswCQYDVQQGEwJDTjEQMA4GA1UECgwHVG9iYWNjbzEUMBIGA1UEAwwL5Zu95a625bGAQ0EwHhcNMjAxMDIxMDIzMTI4WhcNMzAxMDE5MDIzMTI4WjBnMQswCQYDVQQGEwJDTjEQMA4GA1UECgwHVG9iYWNjbzESMBAGA1UECwwJ5Zu95a625bGAMRIwEAYDVQQLDAnlm5vlt53nnIExHjAcBgNVBAMMFemYv+WdneW3nuWFrOWPuOWFmue7hDBZMBMGByqGSM49AgEGCCqBHM9VAYItA0IABBqTq5KCgjg/Ds+4pOKib+f9MwXkqLpSm1fvz9oftA3412kjuKjgrXy11fXo1ntyJKm4dkq4Lrdk13gXdg7iU3OjgawwgakwHwYDVR0jBBgwFoAUMJ/jA9+kBZmMPf4PwOahHUesO/owCQYDVR0TBAIwADBPBgNVHR8ESDBGMESgQqBApD4wPDENMAsGA1UEAwwEY3JsODEMMAoGA1UECwwDY3JsMRAwDgYDVQQKDAdUb2JhY2NvMQswCQYDVQQGEwJDTjALBgNVHQ8EBAMCB4AwHQYDVR0OBBYEFKr8LjbtMZ9WSfQaOm85gEREf9H2MAwGCCqBHM9VAYN1BQADSQAwRgIhAMSQUF+3pSFI73gtbLriQ9b71/8HpmHwwuTdeLMaCsKpAiEAuxYy3Z8mOGmaS+cGQZ36p5t6nkuifWEm+xLR0f0m/P4=");
//		byte[] b = Base64Utils.ESSGetBase64Decode(encodeBase64File("D:\\工作区\\烟草行业OA升级改造\\初始化信息V4\\可信根证书\\root_2.cer"));
//		System.out.println(VerifyIssuer(a,b));
//		System.out.println(encodeBase64File("D:\\工作区\\烟草行业OA升级改造\\初始化信息V4\\可信根证书\\root_2.cer"));
		decoderBase64File("MIICQTCCAeSgAwIBAgIFb3itY1YwDAYIKoEcz1UBg3UFADA1MQswCQYDVQQGEwJDTjEQMA4GA1UECgwHVG9iYWNjbzEUMBIGA1UEAwwL5Zu95a625bGAQ0EwHhcNMjAxMDIxMDIzMTI4WhcNMzAxMDE5MDIzMTI4WjBnMQswCQYDVQQGEwJDTjEQMA4GA1UECgwHVG9iYWNjbzESMBAGA1UECwwJ5Zu95a625bGAMRIwEAYDVQQLDAnlm5vlt53nnIExHjAcBgNVBAMMFemYv+WdneW3nuWFrOWPuOWFmue7hDBZMBMGByqGSM49AgEGCCqBHM9VAYItA0IABBqTq5KCgjg/Ds+4pOKib+f9MwXkqLpSm1fvz9oftA3412kjuKjgrXy11fXo1ntyJKm4dkq4Lrdk13gXdg7iU3OjgawwgakwHwYDVR0jBBgwFoAUMJ/jA9+kBZmMPf4PwOahHUesO/owCQYDVR0TBAIwADBPBgNVHR8ESDBGMESgQqBApD4wPDENMAsGA1UEAwwEY3JsODEMMAoGA1UECwwDY3JsMRAwDgYDVQQKDAdUb2JhY2NvMQswCQYDVQQGEwJDTjALBgNVHQ8EBAMCB4AwHQYDVR0OBBYEFKr8LjbtMZ9WSfQaOm85gEREf9H2MAwGCCqBHM9VAYN1BQADSQAwRgIhAMSQUF+3pSFI73gtbLriQ9b71/8HpmHwwuTdeLMaCsKpAiEAuxYy3Z8mOGmaS+cGQZ36p5t6nkuifWEm+xLR0f0m/P4=",
				"D:\\test.cer");

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

