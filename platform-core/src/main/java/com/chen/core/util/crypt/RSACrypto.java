package com.chen.core.util.crypt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.TBSCertificate;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.asn1.x509.V3TBSCertificateGenerator;
import org.bouncycastle.cert.bc.BcX509ExtensionUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;


public class RSACrypto {
	
	 public static KeyPair GenKeyPair() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException
	 {
		 // 创建 密钥对生成器
		 KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA", new BouncyCastleProvider());
		 // 获取密钥对
		 KeyPair keyPair = gen.generateKeyPair();
		 
//		 byte[] b1 = keyPair.getPrivate().getEncoded();
//		 byte[] b2 = keyPair.getPublic().getEncoded();
//		 
//		 File f = new File("d:/test/prv.dat");
//		 FileOutputStream fos;
//		try {
//			fos = new FileOutputStream(f);
//			fos.write(b1);
//			fos.close();
//			
//			f = new File("d:/test/pub.dat");
//			fos = new FileOutputStream(f);
//			fos.write(b2);
//			fos.close();
//		} catch ( IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		 
		 return keyPair;
	 }
	 
	 /**
	  * @author lijin
	  * @param  sProvince   用户所在的省份
	  * @param  sCity		用户所在的城市
	  * @param  sUserName	用户单位名称
	  */
	 
	public static CertStruct CreateRootRSAPfx(String sProvince,String sCity,String sUserName)
	{
		String sPsw = UUID.randomUUID().toString().replaceAll("-","");
		sPsw = sPsw.substring(0, 8);
		System.out.println("根证书密码:" + sPsw);
		CertStruct certStru = new CertStruct();
		Security.addProvider(new BouncyCastleProvider());
		//生成密钥对
		KeyPair keyPair = null;
		try {
			keyPair = GenKeyPair();
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (InvalidAlgorithmParameterException e1) {
			e1.printStackTrace();
		}
		if(keyPair == null)
			return null;
		//设置要制作的证书的信息
		//1、证书公钥
		SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded());
		BcX509ExtensionUtils extUtils = new BcX509ExtensionUtils();
		//2、扩展属性
		ExtensionsGenerator extensionsGenerator = new ExtensionsGenerator();
		try {
			extensionsGenerator.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));
			extensionsGenerator.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyCertSign | KeyUsage.cRLSign));
			extensionsGenerator.addExtension(Extension.subjectKeyIdentifier,false,extUtils.createSubjectKeyIdentifier(subjectPublicKeyInfo));
			extensionsGenerator.addExtension(Extension.authorityKeyIdentifier,false,extUtils.createAuthorityKeyIdentifier(subjectPublicKeyInfo));
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		} 
		//证书主题
		String sSubject = "CN=" + sUserName + ",OU = " + sUserName + "电子印章平台,O = 电子印章平台,L = " + sCity + ",ST = " + sProvince + ",C = CN";
		X500Name subject = new X500Name(sSubject);
		//有效期
		Calendar c1 = Calendar.getInstance();
		int iY = c1.get(Calendar.YEAR);
	//	c1.set(2014, 5 - 1, 9);
		Date notBefore = c1.getTime();
		c1.set(iY + 10, 11,31);	//这是到12月！！
		Date notAfter = c1.getTime();
		
		//设置证书信息到结构中
		V3TBSCertificateGenerator   tbsGen = new V3TBSCertificateGenerator();
		tbsGen.setSerialNumber(new ASN1Integer(UUID.randomUUID().getMostSignificantBits()&Long.MAX_VALUE));
      	tbsGen.setIssuer(subject);  //颁发者信息 自签证书颁发者等于使用者
      	tbsGen.setStartDate(new Time(notBefore, Locale.CHINA));
      	tbsGen.setEndDate(new Time(notAfter,Locale.CHINA));
      	tbsGen.setSubject(subject);
      	tbsGen.setSubjectPublicKeyInfo(subjectPublicKeyInfo);
      	tbsGen.setExtensions(extensionsGenerator.generate());
      	
        ASN1ObjectIdentifier oid2 = new ASN1ObjectIdentifier("1.2.840.113549.1.1.5");
        AlgorithmIdentifier algoid2 = new AlgorithmIdentifier(oid2);
        tbsGen.setSignature(algoid2);   

      	TBSCertificate tbs = tbsGen.generateTBSCertificate();
      	//设置颁发者的私钥
      	PrivateKey issuerPrivateKey = keyPair.getPrivate();
      	//对证书信息进行签名
      	Signature signature = null;
		try {
			signature = Signature.getInstance("SHA1withRSA","BC");
			signature.initSign(issuerPrivateKey);
			byte[] plainText = tbs.getEncoded();
			signature.update(plainText);
			byte[] signatureValue = signature.sign();
			
			ASN1EncodableVector v = new ASN1EncodableVector();
	        v.add(tbs);
	        v.add(tbs.getSignature());
	        v.add(new DERBitString(signatureValue));
	        //生成签名后的证书
	        Certificate cert = Certificate.getInstance(new DERSequence(v));
	        int iCertLen = cert.getEncoded().length;
	        certStru.bCert = new byte[iCertLen];
	        System.arraycopy(cert.getEncoded(), 0, certStru.bCert, 0, iCertLen);
	        
	        //生成PFX
	        X509Certificate x509Cert;
	        CertificateFactory cf;

			cf = CertificateFactory.getInstance("X.509","BC");
	        ByteArrayInputStream bys = new ByteArrayInputStream(cert.getEncoded());
	        x509Cert = (X509Certificate)cf.generateCertificate(bys);

	        KeyStore store = KeyStore.getInstance("PKCS12");  
	        store.load(null, null);  			
	        store.setKeyEntry("esspfx", keyPair.getPrivate(), sPsw.toCharArray(), new java.security.cert.Certificate[] { x509Cert });  
	        ByteArrayOutputStream bOutput = new ByteArrayOutputStream();
	        store.store(bOutput, sPsw.toCharArray());         
	        bys.close();
	        byte[] bPfx = bOutput.toByteArray();
	        iCertLen = bPfx.length;
	        certStru.sPin = sPsw;
	        certStru.bPfx = new byte[iCertLen];
	        System.arraycopy(bPfx, 0, certStru.bPfx,0,iCertLen);
	        return certStru;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static byte[] SignData(byte[] bPlain,byte[] bPfx,String sPin) throws IOException {
		String algorithm = "SHA1withRSA";
		String password = sPin; 
		Security.addProvider(new BouncyCastleProvider());
		
		KeyStore ks2;
		try {
			ks2 = KeyStore.getInstance("PKCS12", "BC");
			ByteArrayInputStream bys = new ByteArrayInputStream(bPfx);
			ks2.load(bys, password.toCharArray());
			Enumeration<String> enum1 = ks2.aliases();
			String keyAlias = null;
	        if (enum1.hasMoreElements()) 
	        {
	            keyAlias = (String)enum1.nextElement();
	        }
	        Signature sig = Signature.getInstance(algorithm, "BC");
	        sig.initSign((PrivateKey) ks2.getKey(keyAlias, null), new SecureRandom());
	        sig.update(bPlain);
	        byte[] rs = sig.sign();
			bys.close();
	        return rs;
		} catch (KeyStoreException | NoSuchProviderException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		} catch (SignatureException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/***
	 *	此函数接收传入的证书，然后使用指定的根证书和传入的证书中的公钥重新生成用户证书
	 * 	  * @author lijin
	 * @param bOrgCert 传入的证书
	  * @param  sProvince   用户所在的省份
	  * @param  sCity		用户所在的城市
	  * @param  sUnit 		用户单位名称
	  * @param  sDepartment	部门
	  * @param	sUserName	印章名称
	  * @param  bRootCert	RSA 颁发者cer证书
	  * @param 	bRootPfx	RSA 颁发者pfx证书
	  * @param	sPfxPin		PFX密码 
	 * @throws GeneralSecurityException 
	 * @throws KeyStoreException 
	 * @throws IOException 
	 * 
	 */
	
	public static byte[] CreateUserCert(byte[] bOrgCert,String sProvince,String sCity,String sUnit,String sDepartment,String sUserName,byte[] bRootCert,byte[] bRootPfx,String sPfxPin) throws KeyStoreException, GeneralSecurityException, IOException
	{
		 Security.addProvider(new BouncyCastleProvider());
		 //解析出颁发者信息
		 X509Certificate x509Certificate = null;
		 CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509","BC");
		 ByteArrayInputStream bInput = new ByteArrayInputStream(bRootCert); 
		 x509Certificate = (X509Certificate) certificateFactory.generateCertificate(bInput);
		 String sRootIssuer = x509Certificate.getIssuerDN().getName();
		 PublicKey issuerPublicKey = x509Certificate.getPublicKey();
       	
		 //从传入的证书中获取公钥
		 PublicKey puk = ESSCertificate.GetCertPublicKey2(bOrgCert);
		 if(puk == null)
			 return null;
		//设置要制作的证书的信息
		//1、用户证书公钥
		SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(puk.getEncoded());
		//颁发者公钥
		
		SubjectPublicKeyInfo issuerPublicKeyInfo = SubjectPublicKeyInfo.getInstance(issuerPublicKey.getEncoded());
		
		//2、扩展属性
		BcX509ExtensionUtils extUtils = new BcX509ExtensionUtils();
		ExtensionsGenerator extensionsGenerator = new ExtensionsGenerator();
		try {
			extensionsGenerator.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));
			extensionsGenerator.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyCertSign | KeyUsage.cRLSign));
			extensionsGenerator.addExtension(Extension.subjectKeyIdentifier,false,extUtils.createSubjectKeyIdentifier(subjectPublicKeyInfo));
			extensionsGenerator.addExtension(Extension.authorityKeyIdentifier,false,extUtils.createAuthorityKeyIdentifier(issuerPublicKeyInfo));
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		} 
		//证书主题
		String sSubject = "CN=" + sUserName + ",OU = " + sDepartment + ",O = " + sUnit + ",L = " + sCity + ",ST = " + sProvince + ",C = CN";
		X500Name subject = new X500Name(sSubject);
		//颁发者主题
		X500Name rootSubject = new X500Name(sRootIssuer);
		//有效期
		Calendar c1 = Calendar.getInstance();
		int iY = c1.get(Calendar.YEAR);
		Date notBefore = c1.getTime();
		c1.set(iY + 10, 11,31);	//这是到12月！！
		Date notAfter = c1.getTime();
		
		//设置证书信息到结构中
		V3TBSCertificateGenerator   tbsGen = new V3TBSCertificateGenerator();
		tbsGen.setSerialNumber(new ASN1Integer(UUID.randomUUID().getMostSignificantBits()&Long.MAX_VALUE));
     	tbsGen.setIssuer(rootSubject);  //颁发者信息 
     	tbsGen.setStartDate(new Time(notBefore, Locale.CHINA));
     	tbsGen.setEndDate(new Time(notAfter,Locale.CHINA));

     	tbsGen.setSubject(subject);		//用户信息
     	tbsGen.setSubjectPublicKeyInfo(subjectPublicKeyInfo);	//公钥信息
     	tbsGen.setExtensions(extensionsGenerator.generate());
     	ASN1ObjectIdentifier oid2 = new ASN1ObjectIdentifier("1.2.840.113549.1.1.5");	//签名算法 sha1withrsa
     	AlgorithmIdentifier algoid2 = new AlgorithmIdentifier(oid2);
     	tbsGen.setSignature(algoid2);   
     	TBSCertificate tbs = tbsGen.generateTBSCertificate();
     	//设置颁发者的私钥
     	PrivateKey issuerPrivateKey = null;
     	//对证书信息进行签名
      
		KeyStore ks;
		ks = KeyStore.getInstance("PKCS12", "BC");
		ByteArrayInputStream bys1 = new ByteArrayInputStream(bRootPfx);
		ks.load(bys1, sPfxPin.toCharArray());
		Enumeration<String> enum1 = ks.aliases();
		String keyAlias = null;
      if (enum1.hasMoreElements()) 
      {
          keyAlias = (String)enum1.nextElement();
      }
      issuerPrivateKey = (PrivateKey)ks.getKey(keyAlias, null);
      bys1.close();
      if( issuerPrivateKey == null )
   	   return null;

      Signature signature = null;
		try {
			signature = Signature.getInstance("SHA1withRSA","BC");
			signature.initSign(issuerPrivateKey);
			byte[] plainText = tbs.getEncoded();
			signature.update(plainText);
			byte[] signatureValue = signature.sign();
			
			ASN1EncodableVector v = new ASN1EncodableVector();
	        v.add(tbs);
	        v.add(tbs.getSignature());
	        v.add(new DERBitString(signatureValue));
	        //生成签名后的证书
	        Certificate cert = Certificate.getInstance(new DERSequence(v));
	        return cert.getEncoded();
	        
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	 /**
	  * @author lijin
	  * @param  sProvince   用户所在的省份
	  * @param  sCity		用户所在的城市
	  * @param  sUnit 		用户单位名称
	  * @param  sDepartment	部门
	  * @param	sUserName	印章名称
	  * @param  bRootCert	RSA 颁发者cer证书
	  * @param 	bRootPfx	RSA 颁发者pfx证书
	  * @param	sPfxPin		PFX密码 
	 * @throws GeneralSecurityException 
	 * @throws CertificateException 
	 * @throws IOException 
	  */
	 public static CertStruct CreateUserRSAPfx(String sProvince,String sCity,String sUnit,String sDepartment,String sUserName,byte[] bRootCert,byte[] bRootPfx,String sPfxPin) throws CertificateException, GeneralSecurityException, IOException
	 {
		 Security.addProvider(new BouncyCastleProvider());
		 //解析出颁发者信息
		 X509Certificate x509Certificate = null;
		 CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509","BC");
		 ByteArrayInputStream bInput = new ByteArrayInputStream(bRootCert); 
		 x509Certificate = (X509Certificate) certificateFactory.generateCertificate(bInput);
		 String sRootIssuer = x509Certificate.getIssuerDN().getName();
		 PublicKey issuerPublicKey = x509Certificate.getPublicKey();
        
        
		 String sPsw = UUID.randomUUID().toString().replaceAll("-","");
		 sPsw = sPsw.substring(0, 8);
		 CertStruct certStru = new CertStruct();
			
		 //生成密钥对
		 KeyPair keyPair = GenKeyPair();
		if(keyPair == null)
			return null;
		//设置要制作的证书的信息
		//1、证书公钥
		SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded());
		
		SubjectPublicKeyInfo issuerPublicKeyInfo = SubjectPublicKeyInfo.getInstance(issuerPublicKey.getEncoded());
		
		//2、扩展属性
		BcX509ExtensionUtils extUtils = new BcX509ExtensionUtils();
		ExtensionsGenerator extensionsGenerator = new ExtensionsGenerator();
		try {
			
			//颁发者密钥标识
//			DigestCalculator calculator = new BcDigestCalculatorProvider().get(new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1));
//			X509ExtensionUtils extensionUtils = new X509ExtensionUtils(calculator);
//			builder.addExtension(Extension.authorityKeyIdentifier, false, extensionUtils.createAuthorityKeyIdentifier(publicKeyInfo));
//			//使用者密钥标识 
//			builder.addExtension(Extension.subjectKeyIdentifier, false,extensionUtils.createSubjectKeyIdentifier(publicKeyInfo));
			
			
			extensionsGenerator.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));
			extensionsGenerator.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyCertSign | KeyUsage.cRLSign));
			extensionsGenerator.addExtension(Extension.subjectKeyIdentifier,false,extUtils.createSubjectKeyIdentifier(subjectPublicKeyInfo));
			extensionsGenerator.addExtension(Extension.authorityKeyIdentifier,false,extUtils.createAuthorityKeyIdentifier(issuerPublicKeyInfo));
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		} 
		//证书主题
		String sSubject = "CN=" + sUserName + ",OU = " + sDepartment + ",O = " + sUnit + ",L = " + sCity + ",ST = " + sProvince + ",C = CN";
		X500Name subject = new X500Name(sSubject);
		//颁发者主题
		X500Name rootSubject = new X500Name(sRootIssuer);
		//有效期
		Calendar c1 = Calendar.getInstance();
		int iY = c1.get(Calendar.YEAR);
		Date notBefore = c1.getTime();
		c1.set(iY + 10, 11,31);	//这是到12月！！
		Date notAfter = c1.getTime();
		
		//设置证书信息到结构中
		V3TBSCertificateGenerator   tbsGen = new V3TBSCertificateGenerator();
		tbsGen.setSerialNumber(new ASN1Integer(UUID.randomUUID().getMostSignificantBits()&Long.MAX_VALUE));
      	tbsGen.setIssuer(rootSubject);  //颁发者信息 
      	tbsGen.setStartDate(new Time(notBefore, Locale.CHINA));
      	tbsGen.setEndDate(new Time(notAfter,Locale.CHINA));

       tbsGen.setSubject(subject);		//用户信息
       tbsGen.setSubjectPublicKeyInfo(subjectPublicKeyInfo);	//公钥信息
       tbsGen.setExtensions(extensionsGenerator.generate());
       ASN1ObjectIdentifier oid2 = new ASN1ObjectIdentifier("1.2.840.113549.1.1.5");	//签名算法 sha1withrsa
       AlgorithmIdentifier algoid2 = new AlgorithmIdentifier(oid2);
       tbsGen.setSignature(algoid2);   
       TBSCertificate tbs = tbsGen.generateTBSCertificate();
       //设置颁发者的私钥
       PrivateKey issuerPrivateKey = null;
       //对证书信息进行签名
       
		KeyStore ks;
		ks = KeyStore.getInstance("PKCS12", "BC");
		ByteArrayInputStream bys1 = new ByteArrayInputStream(bRootPfx);
		ks.load(bys1, sPfxPin.toCharArray());
		Enumeration<String> enum1 = ks.aliases();
		String keyAlias = null;
       if (enum1.hasMoreElements()) 
       {
           keyAlias = (String)enum1.nextElement();
       }
       issuerPrivateKey = (PrivateKey)ks.getKey(keyAlias, null);
       bys1.close();
       if( issuerPrivateKey == null )
    	   return null;

       Signature signature = null;
		try {
			signature = Signature.getInstance("SHA1withRSA","BC");
			signature.initSign(issuerPrivateKey);
			byte[] plainText = tbs.getEncoded();
			signature.update(plainText);
			byte[] signatureValue = signature.sign();
			
			ASN1EncodableVector v = new ASN1EncodableVector();
	        v.add(tbs);
	        v.add(tbs.getSignature());
	        v.add(new DERBitString(signatureValue));
	        //生成签名后的证书
	        Certificate cert = Certificate.getInstance(new DERSequence(v));
	        int iCertLen = cert.getEncoded().length;
	        certStru.bCert = new byte[iCertLen];
	        System.arraycopy(cert.getEncoded(), 0, certStru.bCert, 0, iCertLen);
	        
	        //生成PFX
	        X509Certificate x509Cert;
	        CertificateFactory cf;


			cf = CertificateFactory.getInstance("X.509","BC");
	        ByteArrayInputStream bys = new ByteArrayInputStream(cert.getEncoded());
	        x509Cert = (X509Certificate)cf.generateCertificate(bys);

	        KeyStore store = KeyStore.getInstance("PKCS12");  
	        store.load(null, null);  			
	        
	        System.out.println("用户pfx密码 111111");
	        
	        sPsw = "111111";
	        store.setKeyEntry("esspfx", keyPair.getPrivate(), sPsw.toCharArray(), new java.security.cert.Certificate[] { x509Cert });  
	        ByteArrayOutputStream bOutput = new ByteArrayOutputStream();
	        store.store(bOutput, sPsw.toCharArray());         
	        bys.close();
	        byte[] bPfx = bOutput.toByteArray();
	        iCertLen = bPfx.length;
	        certStru.sPin = sPsw;
	        certStru.bPfx = new byte[iCertLen];
	        certStru.sPrivateKey = "";
	        certStru.sPublicKey = "";
	        System.arraycopy(bPfx, 0, certStru.bPfx,0,iCertLen);
	        return certStru;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	 }

	 public static byte[] EncryptData(byte[] bPlain,String sPsw)
	 {
		 String Algorithm = "DESede";    
		 String PASSWORD_CRYPT_KEY = "ing#an%Vi&ta*l0755226288";

		 byte[] bPsw = null;
		 if(sPsw == null || sPsw.length() == 0)
			 bPsw = PASSWORD_CRYPT_KEY.getBytes();
		 else
			 bPsw = sPsw.getBytes();
		 
	     try {
	    	 SecretKey deskey = new SecretKeySpec(bPsw,Algorithm);    //生成密钥
	    	 Cipher c1 = Cipher.getInstance(Algorithm);    //实例化负责加密/解密的Cipher工具类
	    	 c1.init(Cipher.ENCRYPT_MODE, deskey);    //初始化为加密模式
	         return c1.doFinal(bPlain);
	      } catch (NoSuchAlgorithmException e1) {
	          e1.printStackTrace();
	      } catch (javax.crypto.NoSuchPaddingException e2) {
	          e2.printStackTrace();
	      } catch (Exception e3) {
	          e3.printStackTrace();
	      }
		 return null;
	 }
	
	 public static byte[] DecryptData(byte[] bEncrypt,String sPsw)
	 {
		 String Algorithm = "DESede";    
		 String PASSWORD_CRYPT_KEY = "ing#an%Vi&ta*l0755226288";
		 byte[] bPsw = null;
		 if(sPsw == null || sPsw.length() == 0)
			 bPsw = PASSWORD_CRYPT_KEY.getBytes();
		 else
			 bPsw = sPsw.getBytes();
		 
	     try {
	    	 SecretKey deskey = new SecretKeySpec(bPsw,Algorithm);    //生成密钥
	    	 Cipher c1 = Cipher.getInstance("DESede/ECB/PKCS5Padding");    //实例化负责加密/解密的Cipher工具类
	    	 c1.init(Cipher.DECRYPT_MODE, deskey);    //初始化为加密模式
	         return c1.doFinal(bEncrypt);
	      } catch (NoSuchAlgorithmException e1) {
	          e1.printStackTrace();
	      } catch (javax.crypto.NoSuchPaddingException e2) {
	          e2.printStackTrace();
	      } catch (Exception e3) {
	          e3.printStackTrace();
	      }
		 return null;
	 }
	 
	 /**
	  * @author lijin
	  * @param bPlain 	被签名数据
	  * @param bSignVal 签名值
	  * @param bCert	签名人证书
	  */
		public static boolean VerifyData(byte[] bPlain,byte[] bSignVal,byte[] bCert) throws IOException {
			String algorithm = "SHA1withRSA";
			Security.addProvider(new BouncyCastleProvider());
			boolean blRet = false;
			try {
		        Signature sig = Signature.getInstance(algorithm, "BC");
		        X509Certificate x509Cert;
		        CertificateFactory cf;
				cf = CertificateFactory.getInstance("X.509","BC");
		        ByteArrayInputStream bys = new ByteArrayInputStream(bCert);
		        x509Cert = (X509Certificate)cf.generateCertificate(bys);
		        sig.initVerify(x509Cert.getPublicKey());
		        sig.update(bPlain);
		        blRet = sig.verify(bSignVal);
				bys.close();
		        return blRet;
			} catch (NoSuchProviderException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (CertificateException e) {
				e.printStackTrace();
			} catch (InvalidKeyException e) {
				e.printStackTrace();
			} catch (SignatureException e) {
				e.printStackTrace();
			}
			return false;
		}
	
		/**
		 * @author lijin
		 * @param sIp			服务器的机器码
		 * @param sSealCount	可以制作的印章的数量
		 * @param sHwCount 		可以制作的手写签名的数量
		 * @param sSealUseCount	总的盖章次数
		 * @param sHwUseCount   总的签名次数
		 * @param sProduct		被授权的产品
		 * @param sEndTime		授权到期时间
		 * @param sSignVal		签名值
		 * 
		 * 说明：此函数用于检查本地服务器的授权签名是否有效
		 */
		public static boolean VerifyServerAuth(String sIp,String sSealCount,String sHwCount,String sSealUseCount,String sHwUseCount,String sProduct,String sEndTime,String sSignVal){
			String sCert = "MIIEozCCA42gAwIBAgIICOfkoKSZSHkwCwYJKoZIhvcNAQEFMIHgMTwwOgYDVQQDDDPljJfkuqzor5rliKnpgJrmlbDnoIHmioDmnK/mnInpmZDlhazlj7jmjojmnYPkuJPnlKgxTjBMBgNVBAsMReWMl+S6rOivmuWIqemAmuaVsOeggeaKgOacr+aciemZkOWFrOWPuOaOiOadg+S4k+eUqOeUteWtkOWNsOeroOW5s+WPsDEbMBkGA1UECgwS55S15a2Q5Y2w56ug5bmz5Y+wMRIwEAYDVQQHDAnljJfkuqzluIIxEjAQBgNVBAgMCeWMl+S6rOW4gjELMAkGA1UEBhMCQ04wHhcNMjAwNjAxMDgzMzIzWhcNMzAxMjMxMDgzMzIzWjCB4DE8MDoGA1UEAwwz5YyX5Lqs6K+a5Yip6YCa5pWw56CB5oqA5pyv5pyJ6ZmQ5YWs5Y+45o6I5p2D5LiT55SoMU4wTAYDVQQLDEXljJfkuqzor5rliKnpgJrmlbDnoIHmioDmnK/mnInpmZDlhazlj7jmjojmnYPkuJPnlKjnlLXlrZDljbDnq6DlubPlj7AxGzAZBgNVBAoMEueUteWtkOWNsOeroOW5s+WPsDESMBAGA1UEBwwJ5YyX5Lqs5biCMRIwEAYDVQQIDAnljJfkuqzluIIxCzAJBgNVBAYTAkNOMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuaiRcPTVHuVuCPGtJVwJyzJehdC3hruJvBpc9xSZ2LbVFXo4Dyni4/4c/NjvYYb6UZowv8i96CldmiEJCLPje7fneioSM14D/vmEmTo5qCM1pYG1cmLiVCHeIbXiPkD0wByaH+ZWjLgMJ18iJ7Q2OmpIyPneyAYTDq/PibGxrWDZ3or6VX2WL71hKisjnwaYWafmvxDOcgIluM7GdDzY0MNY7VKyXox7iCOZYPxA1OnAHpE33lhE/BcYjKHmJTQrQLrjxTx1OBsjWwzUK48sM7WoYnLkpLaeFeTI0Wbuo1QdRfcdnkXlXlv8V35NCHsCBa5gE5g3LLY2/8LIfc2+9QIDAQABo2MwYTAPBgNVHRMBAf8EBTADAQH/MA4GA1UdDwEB/wQEAwIBhjAdBgNVHQ4EFgQUNB/lT8TYBAO/wVSNLU9mEFNd27gwHwYDVR0jBBgwFoAUNB/lT8TYBAO/wVSNLU9mEFNd27gwCwYJKoZIhvcNAQEFA4IBAQBkyV1K5geGreDqL5ArxsUzQ5ddjfJz9v9kVadH8F2HDmg07KQAfhR2Ul9LpEnEtSiCS7nENRdpcdqgQV1j6OT3vXoHkhW92T1XrE7d4ilWG4ATqYCJyrisLo2/QdvObRUEgkaEE76gFKEqcrzK+slC3mJ+rnxEwPsnLy7g4KKytSuLZCLh9KrEN2dBphEFTbHbI1TKY1Y38Ifi7R9Fn96if5IsAcmK8DasB1GbPqVwP26kdEf1HuDj6YTdaJNn2JCJoc273nuxqgXPWmG+egNye/sLySS6IIMvLX4pKEZPpDcONNqK8kQjGDhbCc4qC6WBT20rHVPcxQzQ9+e8QzTf";
			String sPlain = sIp + "@@@@" + sSealCount + "@@@@" + sHwCount + "@@@@" + sSealUseCount + "@@@@" + sHwUseCount + "@@@@" + sProduct + "@@@@" + sEndTime;
			byte[] bSign = Base64.decode(sSignVal);
			byte[] bCert = Base64.decode(sCert);
			
			byte[] bSignConv = new byte[(int)bSign.length];
			for(int i=0;i<bSign.length;i++)
				bSignConv[i] = bSign[bSign.length-i-1];
			try {
				return VerifyData(sPlain.getBytes(),bSignConv,bCert);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
	 
	public static void main(String[] args) throws IOException {
//		String sSignVal = "6lTY3jLa/OOLMP/GOAASBzTzAvYKmc4IECAiuaZWk2JK9bHMcaJWMtuKD7SK3nF4CxnEK6eQK2rkIW5RCZYSiW5joJ7bWkppVttDMF6RzvLPo+tDFKIlThYZC0ysK0pWkLzCjkBAO2XTzMkPiqAq0EhqUg6++edMYhO8AX2NV2aJ6zjxs6dYf0AH5Rb4f+vb8HhJJ6FyjBiv61P7fjOuj4tTbhtsY9hIlkw1AEwp7pM3O8fuA/6O3Xikhj4cslc9gjdHX8GjbTo/lPsLpyy7NZokYixc2EzqPWrT/oj0MrOFYpYLvSTnrUlrhFHpJITfLosb2w4QQNPxLXOaHOoxhA==";
//		if( RSACrypto.VerifyServerAuth("123", "123", "123", "123", "123", "123", "123", sSignVal) )
//			System.out.println("YES");
//		else
//			System.out.println("NO");

		
		
	//	byte[] b1 = RSACrypto.EncryptData("12345661231432432543653462412342523423".getBytes(), null);
		
		
////		//以下算法生成一个RSA的用户证书
//		File f1 = new File("d:/test/rsaroot.cer");
//		FileInputStream fis = new FileInputStream(f1);
//		int iLen = (int)f1.length();
//		byte[] bRootCert = new byte[iLen];
//		fis.read(bRootCert);
//		fis.close();
//		
//		f1 = new File("d:/test/rsaroot.pfx");
//		fis = new FileInputStream(f1);
//		iLen = (int)f1.length();
//		byte[] bRootPfx = new byte[iLen];
//		fis.read(bRootPfx);
//		fis.close();
//		
//		f1 = new File("d:/test/rsarootpwd.txt");
//		fis = new FileInputStream(f1);
//		iLen = (int)f1.length();
//		byte[] bPin = new byte[iLen];
//		fis.read(bPin);
//		fis.close();	
//		
//		File f = new File("d:/test/test.cer");
//		fis = new FileInputStream(f);
//		iLen = (int)f.length();
//		byte[] bUserCert = new byte[iLen];
//		fis.read(bUserCert);
//		fis.close();
//		
//		try {
//			byte[] b = RSACrypto.CreateUserCert(bUserCert, "江西省", "南昌市", "江西省烟草质量监督检测站", "江西省烟草质量监督检测站", "检验专用章", bRootCert, bRootPfx, new String(bPin));
//			f = new File("d:/test/haha.cer");
//			FileOutputStream fos = new FileOutputStream(f);
//			fos.write(b);
//			fos.close();
//		} catch (GeneralSecurityException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
//
//		// 以下代码生成了一个RSA算法的颁发者PFX证书内容
//		CertStruct retStru = RSACrypto.CreateRootRSAPfx("江西省", "南昌市", "江西省烟草质量监督检测站");
//		if(retStru != null)
//		{
//			File f = new File("d:/test/rsaroot.pfx");
//			FileOutputStream fos = new FileOutputStream(f);
//			fos.write(retStru.bPfx);
//			fos.close();
//			
//			f = new File("d:/test/rsaroot.cer");
//			fos = new FileOutputStream(f);
//			fos.write(retStru.bCert);
//			fos.close();	
//			
//			f = new File("d:/test/rsarootpwd.txt");
//			fos = new FileOutputStream(f);
//			fos.write(retStru.sPin.getBytes());
//			fos.close();
//		}
		
//		//以下算法生成一个RSA的用户证书
		File f1 = new File("d:/test/rsaroot.cer");
		FileInputStream fis = new FileInputStream(f1);
		int iLen = (int)f1.length();
		byte[] bRootCert = new byte[iLen];
		fis.read(bRootCert);
		fis.close();
		
		f1 = new File("d:/test/rsaroot.pfx");
		fis = new FileInputStream(f1);
		iLen = (int)f1.length();
		byte[] bRootPfx = new byte[iLen];
		fis.read(bRootPfx);
		fis.close();
		
		f1 = new File("d:/test/rsarootpwd.txt");
		fis = new FileInputStream(f1);
		iLen = (int)f1.length();
		byte[] bPin = new byte[iLen];
		fis.read(bPin);
		fis.close();	
		
		 
		try {
			CertStruct userCert =  RSACrypto.CreateUserRSAPfx("江西省", "南昌市", "江西省烟草质量监督检测站", "江西省烟草质量监督检测站", "江西省烟草质量监督检测站章", bRootCert, bRootPfx, new String(bPin));
			if(userCert != null){
				File f = new File("d:/test/rsauser.pfx");
				FileOutputStream fos = new FileOutputStream(f);
				fos.write(userCert.bPfx);
				fos.close();
				
				f = new File("d:/test/rsauser.cer");
				fos = new FileOutputStream(f);
				fos.write(userCert.bCert);
				fos.close();	
				
				f = new File("d:/test/rsauserpwd.txt");
				fos = new FileOutputStream(f);
				fos.write(userCert.sPin.getBytes());
				fos.close();
			}
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
