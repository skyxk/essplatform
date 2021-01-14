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

import com.sansec.jce.provider.SwxaProvider;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.gm.GMNamedCurves;
import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
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
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.cert.bc.BcX509ExtensionUtils;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.util.encoders.Base64Encoder;

public class SM2Crypto {
	 private static KeyPair GenKeyPair() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException
	 {
		// 获取SM2 椭圆曲线推荐参数
		 X9ECParameters ecParameters = GMNamedCurves.getByName("sm2p256v1");
		 // 构造EC 算法参数
		 ECNamedCurveParameterSpec sm2Spec = new ECNamedCurveParameterSpec(
		         // 设置SM2 算法的 OID
		         GMObjectIdentifiers.sm2p256v1.toString()
		         // 设置曲线方程
		         , ecParameters.getCurve()
		         // 椭圆曲线G点
		         , ecParameters.getG()
		         // 大整数N
		         , ecParameters.getN());
		 // 创建 密钥对生成器
		 KeyPairGenerator gen = KeyPairGenerator.getInstance("EC", new BouncyCastleProvider());
		 // 使用SM2的算法区域初始化密钥生成器
		 gen.initialize(sm2Spec, new SecureRandom());
		 // 获取密钥对
		 KeyPair keyPair = gen.generateKeyPair();
		 return keyPair;
	 }
	 
	 /***
	  *	对数据进行加密 
	  *@param bPlain	需要加密的数据
	  *@param sIp		加密机IP地址
	  *@param iPort		加密机端口
	  *@param iIndex	密钥索引
	  *@param sReserve	保留参数，传“”
	  */
	 public static byte[] EncryptIt(byte[] bPlain,String sIp,int iPort,int iIndex,String sReserve)
	 {
		 return bPlain;
	 }
	 
	 
	 /***
	  *	对数据进行加解密
	  *@param bEncrypt 	需要解密的数据
	  *@param sIp		加密机IP地址
	  *@param iPort		加密机端口
	  *@param iIndex	密钥索引
	  *@param sReserve	保留参数，传“”
	  */
	 public static byte[] DecryptIt(byte[] bEncrypt,String sIp,int iPort,int iIndex,String sReserve)
	 {
		 return bEncrypt;
	 }
	 
	 /**
	  * @author lijin
	  * @param  sProvince   用户所在的省份
	  * @param  sCity		用户所在的城市
	  * @param  sUnit 		用户单位名称
	  * @param  sDepartment	部门
	  * @param	sUserName	印章名称
	  * @param  bRootCert	国密颁发者cer证书
	  * @param 	bRootPfx	国密颁发者pfx证书
	  * @param	sPfxPin		PFX密码 
	 * @throws GeneralSecurityException 
	 * @throws CertificateException 
	 * @throws IOException 
	  */
	 public static CertStruct CreateUserSM2Pfx(String sProvince,String sCity,String sUnit,String sDepartment,String sUserName,byte[] bRootCert,byte[] bRootPfx,String sPfxPin) throws CertificateException, GeneralSecurityException, IOException
	 {
		 Security.addProvider(new BouncyCastleProvider());
		 //解析出颁发者信息
		 X509Certificate x509Certificate = null;
		 CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509","BC");
		 ByteArrayInputStream bInput = new ByteArrayInputStream(bRootCert); 
		 x509Certificate = (X509Certificate) certificateFactory.generateCertificate(bInput);
		 String sRootIssuer = x509Certificate.getIssuerDN().getName();
		 PublicKey issuerPublicKey = x509Certificate.getPublicKey();
	//	 String sPsw = UUID.randomUUID().toString().replaceAll("-","");
		 String sPsw = "111111111111111111111";
		 sPsw = sPsw.substring(0, 6);
		 CertStruct certStru = new CertStruct();
		 System.out.println("PSW:111111");
		 //生成密钥对
		 KeyPair keyPair = null;
		 String sPrivateKey = "";
		 String sPublicKey = "";
		 PCIKeyPair pkp = SM2Util.genKeyPair();
		 byte[] bTmp = pkp.GetPrivateKey();
		 Base64Encoder b64 = new Base64Encoder();
		 try
		 {
			 ByteArrayOutputStream bOutput1 = new ByteArrayOutputStream();
			 b64.encode(bTmp, 0, bTmp.length, bOutput1);
			 sPrivateKey = new String(bOutput1.toByteArray());
			 bTmp = pkp.GetPublicKey();
			 bOutput1.reset();
			 b64.encode(bTmp, 0, bTmp.length, bOutput1);
			 sPublicKey = new String(bOutput1.toByteArray());
			 bOutput1.close();			
			 AsymmetricCipherKeyPair bcKeyPair = SM2Util.ConvertToBCKeyPair(sPublicKey, sPrivateKey);
			 keyPair = SM2Util.convertBcToJceKeyPair(bcKeyPair);
		}catch(IOException e)
		{
			e.printStackTrace();
			return null;
		}
			
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
			extensionsGenerator.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));
			extensionsGenerator.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyCertSign | KeyUsage.cRLSign));
			extensionsGenerator.addExtension(Extension.subjectKeyIdentifier,false,extUtils.createSubjectKeyIdentifier(subjectPublicKeyInfo));
			extensionsGenerator.addExtension(Extension.authorityKeyIdentifier,false,extUtils.createAuthorityKeyIdentifier(issuerPublicKeyInfo));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		} 
		//证书主题
		String sSubject = "CN=" + sUserName + ",OU = " + sDepartment + ",O = " + sUnit + ",L = " + sCity + ",ST = " + sProvince + ",C = CN";
		X500Name subject = new X500Name(sSubject);
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
        tbsGen.setSubjectPublicKeyInfo(subjectPublicKeyInfo);
        tbsGen.setExtensions(extensionsGenerator.generate());
        ASN1ObjectIdentifier oid2 = new ASN1ObjectIdentifier("1.2.156.10197.1.501");
        AlgorithmIdentifier algoid2 = new AlgorithmIdentifier(oid2);
        tbsGen.setSignature(algoid2);   
        TBSCertificate tbs = tbsGen.generateTBSCertificate();
        //设置颁发者的私钥
        PrivateKey issuerPrivateKey = null;
        //SubjectPublicKeyInfo issuerSubjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded());
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

        Signature signature = null;
		try {
			signature = Signature.getInstance(
						 GMObjectIdentifiers.sm2sign_with_sm3.toString());
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
	        certStru.sPrivateKey = sPrivateKey;
	        certStru.sPublicKey = sPublicKey;
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





	 /**
	  * @author lijin
	  * @param  sProvince   用户所在的省份
	  * @param  sCity		用户所在的城市
	  * @param  sUserName	用户单位名称
	  */
	public static CertStruct CreateRootSMPfx(String sProvince,String sCity,String sUserName)
	{
		String sPsw = UUID.randomUUID().toString().replaceAll("-","");
		sPsw = sPsw.substring(0, 8);
		CertStruct certStru = new CertStruct();
		Security.addProvider(new BouncyCastleProvider());
		//生成密钥对
		KeyPair keyPair = null;
		String sPrivateKey = "";
		String sPublicKey = "";
		PCIKeyPair pkp = SM2Util.genKeyPair();
		byte[] bTmp = pkp.GetPrivateKey();
		Base64Encoder b64 = new Base64Encoder();
		try
		{
			ByteArrayOutputStream bOutput1 = new ByteArrayOutputStream();
			b64.encode(bTmp, 0, bTmp.length, bOutput1);
			sPrivateKey = new String(bOutput1.toByteArray());
			bTmp = pkp.GetPublicKey();
			bOutput1.reset();
			b64.encode(bTmp, 0, bTmp.length, bOutput1);
			sPublicKey = new String(bOutput1.toByteArray());
			bOutput1.close();			
			AsymmetricCipherKeyPair bcKeyPair = SM2Util.ConvertToBCKeyPair(sPublicKey, sPrivateKey);
			keyPair = SM2Util.convertBcToJceKeyPair(bcKeyPair);
		}catch(IOException e)
		{
			e.printStackTrace();
			return null;
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
			// TODO Auto-generated catch block
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
        ASN1ObjectIdentifier oid2 = new ASN1ObjectIdentifier("1.2.156.10197.1.501");
        AlgorithmIdentifier algoid2 = new AlgorithmIdentifier(oid2);
        tbsGen.setSignature(algoid2);   
        TBSCertificate tbs = tbsGen.generateTBSCertificate();
        //设置颁发者的私钥
        PrivateKey issuerPrivateKey = keyPair.getPrivate();
  //      SubjectPublicKeyInfo issuerSubjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded());
        //对证书信息进行签名
        Signature signature = null;
		try {
			signature = Signature.getInstance(
						 GMObjectIdentifiers.sm2sign_with_sm3.toString());
			signature.initSign(issuerPrivateKey);
			byte[] plainText = tbs.getEncoded();
			signature.update(plainText);
			byte[] signatureValue = signature.sign();
			
			ASN1EncodableVector v = new ASN1EncodableVector();
	        v.add(tbs);
//	        ASN1ObjectIdentifier oid = new ASN1ObjectIdentifier("1.2.156.10197.1.501");	//签名算法sm3withsm2
//	        AlgorithmIdentifier algoid = new AlgorithmIdentifier(oid);
	        v.add(tbs.getSignature());
	        //v.add(issuerSubjectPublicKeyInfo.getAlgorithm()); //根据公钥算法标识返回对应签名算法标识 ASN1ObjectIdentifier oid = new ASN1ObjectIdentifier(sAlg);
	       
	       
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
	        certStru.sPrivateKey = sPrivateKey;
	        certStru.sPublicKey = sPublicKey;
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
	
	
	/**
	 * 
	 * 
	 * 
	 */
	public static boolean VerifyData(byte[] bPlain,byte[] bCert,byte[] bSignVal)
	{
		String algorithm = "SM3withSM2";
		Security.addProvider(new BouncyCastleProvider());
		try {
	        Signature sig = Signature.getInstance(algorithm, "BC");
	        PublicKey puk = ESSCertificate.GetCertPublicKey2(bCert);
	        sig.initVerify(puk);
	        sig.update(bPlain);
	        return sig.verify(bSignVal);
		} catch ( NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 *	这个函数是为了制作sm2pfx时，签名用
	 * 
	 */
	public static byte[] SignData(byte[] bPlain,byte[] bPfx,String sPin) throws IOException {
		// TODO Auto-generated method stub
		String algorithm = "SM3withSM2";
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 *	从数字证书中获取证书公钥 
	 * 
	 */
    private static byte[] GetPublicKey(byte[] bCert)
    {
        try{
        	Security.addProvider(new BouncyCastleProvider());
            X509Certificate x509Certificate = null;
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509","BC");
            ByteArrayInputStream bInput = new ByteArrayInputStream(bCert); 
            x509Certificate = (X509Certificate) certificateFactory.generateCertificate(bInput);

            PublicKey publicKey = x509Certificate.getPublicKey();
            byte[] bRet = new byte[64];
            byte[] bPub = publicKey.getEncoded();
            System.arraycopy(bPub, bPub.length - 64, bRet, 0, 64);
            return bRet;
        }
        catch(Exception ex) {
            System.out.println(ex);
            return null;
        }
    }
    /**
     * 这个函数是为了制作印章过程中对ASN签名
     * @throws IOException
     */
    public static byte[] SignDateForSeal(byte[] bMakerCert,byte[] bSealForSign,String sPublicKey,String sPrivateKey) throws IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
    	//以下组织数据以及计算HASH的方式，与客户端制作印章时相同
    	byte[] bPublicKey = GetPublicKey(bMakerCert);
    	byte[] input = new byte[210];
    	String s1 = "00,80,31,32,33,34,35,36,37,38,31,32,33,34,35,36,37,38,FF,FF,FF,FE,FF,FF,FF,FF,FF,FF,FF,FF,FF,FF,FF,FF,FF,FF,FF,FF,00,00,00,00,FF,FF,FF,FF,FF,FF,FF,FC,28,E9,FA,9E,9D,9F,5E,34,4D,5A,9E,4B,CF,65,09,A7,F3,97,89,F5,15,AB,8F,92,DD,BC,BD,41,4D,94,0E,93,32,C4,AE,2C,1F,19,81,19,5F,99,04,46,6A,39,C9,94,8F,E3,0B,BF,F2,66,0B,E1,71,5A,45,89,33,4C,74,C7,BC,37,36,A2,F4,F6,77,9C,59,BD,CE,E3,6B,69,21,53,D0,A9,87,7C,C6,2A,47,40,02,DF,32,E5,21,39,F0,A0,4e,70,ed,ea,ad,ec,e1,3e,b9,5b,59,53,46,f5,13,34,e0,e4,af,3e,54,f8,2b,09,5f,b5,64,9c,73,c6,7a,29,2b,4a,75,18,1e,4a,ce,79,5a,ee,6a,9a,d3,c3,39,98,c5,62,6d,0a,06,16,9b,16,12,82,f1,18,4c,af,45,89";
    	String[] s = s1.split(",");
    	for(int i=0;i<210;i++)
    	{
    		input[i] = (byte)Integer.parseInt(s[i], 16);
    	}
    	System.arraycopy(bPublicKey, 0, input, 210-64, 32);
    	System.arraycopy(bPublicKey, 32, input, 210-32, 32);
    	byte[] bHash = GmUtil.sm3(input);
    	int iLen = bHash.length + bSealForSign.length;
    	byte[] bPlain = new byte[iLen];
    	System.arraycopy(bHash, 0, bPlain, 0, bHash.length);
    	System.arraycopy(bSealForSign, 0, bPlain, bHash.length, bSealForSign.length);
    	bHash = GmUtil.sm3(bPlain);

		//安徽加密机调用签名 其他项目需注释
//		SwxaProvider swxaProvider = new SwxaProvider("11111111","12345678","12345678");
//		KeyPair keyPair =
//				KeyPairGenerator.getInstance("SM2",swxaProvider).generateKeyPair();
//		Signature signature = Signature.getInstance("SM3WithSM2",swxaProvider);
//		signature.initSign(keyPair.getPrivate());
//		signature.update(bHash);
//		byte[] outsign = signature.sign();
//		if (outsign ==null){
//			System.out.println("no sign..................");
//			return null;
//		}

    	AsymmetricCipherKeyPair bcPair = SM2Util.ConvertToBCKeyPair(sPublicKey, sPrivateKey);
    	if(bcPair == null)
    	{
    		System.out.println("no pair..................");
    		return null;
    	}
    	byte[] bVal = GmUtil.sm2(bHash, bcPair);
    	return bVal;
    }
    /**
     * 这个函数是为了制作印章过程中对ASN签名
     * @throws IOException 
     */
    public static byte[] SignDateForSeal(byte[] bMakerCert,byte[] bSealForSign,byte[] bMakePfx,String sPfxPin) throws IOException
    {
    	//以下组织数据以及计算HASH的方式，与客户端制作印章时相同
    	byte[] bPublicKey = GetPublicKey(bMakerCert);
    	byte[] input = new byte[210];
    	String s1 = "00,80,31,32,33,34,35,36,37,38,31,32,33,34,35,36,37,38,FF,FF,FF,FE,FF,FF,FF,FF,FF,FF,FF,FF,FF,FF,FF,FF,FF,FF,FF,FF,00,00,00,00,FF,FF,FF,FF,FF,FF,FF,FC,28,E9,FA,9E,9D,9F,5E,34,4D,5A,9E,4B,CF,65,09,A7,F3,97,89,F5,15,AB,8F,92,DD,BC,BD,41,4D,94,0E,93,32,C4,AE,2C,1F,19,81,19,5F,99,04,46,6A,39,C9,94,8F,E3,0B,BF,F2,66,0B,E1,71,5A,45,89,33,4C,74,C7,BC,37,36,A2,F4,F6,77,9C,59,BD,CE,E3,6B,69,21,53,D0,A9,87,7C,C6,2A,47,40,02,DF,32,E5,21,39,F0,A0,4e,70,ed,ea,ad,ec,e1,3e,b9,5b,59,53,46,f5,13,34,e0,e4,af,3e,54,f8,2b,09,5f,b5,64,9c,73,c6,7a,29,2b,4a,75,18,1e,4a,ce,79,5a,ee,6a,9a,d3,c3,39,98,c5,62,6d,0a,06,16,9b,16,12,82,f1,18,4c,af,45,89";
    	String[] s = s1.split(",");
    	for(int i=0;i<210;i++)
    	{
    		input[i] = (byte)Integer.parseInt(s[i], 16);
    	}
    	System.arraycopy(bPublicKey, 0, input, 210-64, 32);
    	System.arraycopy(bPublicKey, 32, input, 210-32, 32);
    	byte[] bHash = GmUtil.sm3(input);
    	int iLen = bHash.length + bSealForSign.length;
    	byte[] bPlain = new byte[iLen];
    	System.arraycopy(bHash, 0, bPlain, 0, bHash.length);
    	System.arraycopy(bSealForSign, 0, bPlain, bHash.length, bSealForSign.length);
    	bHash = GmUtil.sm3(bPlain);
    	
//    	AsymmetricCipherKeyPair bcPair = SM2Util.ConvertToBCKeyPair(sPublicKey, sPrivateKey);
//    	if(bcPair == null)
//    	{
//    		System.out.println("no pair..................");
//    		return null;
//    	}
    	byte[] bVal = GmUtil.sm2(bHash, bMakePfx,sPfxPin); 
    	return bVal;
    }
    
	public static void main(String[] args) throws IOException {
		
//		File f = new File("C:\\Users\\Administrator\\Documents\\各省AK\\制作SM2证书\\test.pfx");
//		FileInputStream fis = new FileInputStream(f);
//		int iLen = (int)f.length();
//		byte[] bPfx = new byte[iLen];
//		fis.read(bPfx);
//		fis.close();
//		
//		byte[] bRet = SM2Crypto.SignData("123456789123456789123456789123456789".getBytes(), bPfx, "111111");
//		if(bRet != null)
//		{
//			f = new File("C:\\Users\\Administrator\\Documents\\各省AK\\制作SM2证书\\test2.cer");
//			int iCert = (int)f.length();
//			byte[] bCert = new byte[iCert];
//			fis = new FileInputStream(f);
//			fis.read(bCert);
//			fis.close();
//			
//			if( SM2Crypto.VerifyData("123456789123456789123456789123456789".getBytes(), bCert, bRet) )
//			{
//				CertificateInfo info =  ESSCertificate.GetCertInfo(bCert);
//				System.out.println(info.sUserInfo);
//				System.out.println("YES");
//			}
//			else
//				System.out.println("NO");
//		}
////		 以下代码生成了一个sm2算法的颁发者PFX证书内容
//		CertStruct retStru = SM2Crypto.CreateRootSMPfx("内蒙古自治区", "呼和浩特市", "内蒙古自治区烟草专卖局");
//		if(retStru != null)
//		{
//			File f = new File("d:/test/sm2root.pfx");
//			FileOutputStream fos = new FileOutputStream(f);
//			fos.write(retStru.bPfx);
//			fos.close();
//			
//			f = new File("d:/test/sm2root.cer");
//			fos = new FileOutputStream(f);
//			fos.write(retStru.bCert);
//			fos.close();	
//			
//			f = new File("d:/test/sm2pwd.txt");				//pfx的密码
//			fos = new FileOutputStream(f);
//			fos.write(retStru.sPin.getBytes());
//			fos.close();
//			
//			f = new File("d:/test/sm2rootbcprv.txt");		//这一项和下面一项是新增的，分别是BC算法的私钥和公钥，需要存入数据库
//			fos = new FileOutputStream(f);
//			fos.write(retStru.sPrivateKey.getBytes());
//			fos.close();
//			
//			f = new File("d:/test/sm2rootbcpub.txt");
//			fos = new FileOutputStream(f);
//			fos.write(retStru.sPublicKey.getBytes());
//			fos.close();
//
//		}
		
		
		

		//以下算法生成一个SM2的用户证书
		File f1 = new File("d:/test/sm2root.cer");
		FileInputStream fis = new FileInputStream(f1);
		int iLen = (int)f1.length();
		byte[] bRootCert = new byte[iLen];
		fis.read(bRootCert);
		fis.close();
		
		f1 = new File("d:/test/sm2root.pfx");
		fis = new FileInputStream(f1);
		iLen = (int)f1.length();
		byte[] bRootPfx = new byte[iLen];
		fis.read(bRootPfx);
		fis.close();
		
		f1 = new File("d:/test/sm2pwd.txt");
		fis = new FileInputStream(f1);
		iLen = (int)f1.length();
		byte[] bPin = new byte[iLen];
		fis.read(bPin);
		fis.close();	
		
		 
		try {
			CertStruct rootCert =  SM2Crypto.CreateUserSM2Pfx("内蒙古自治区", "呼和浩特市", "内蒙古自治区烟草质量监督检测站", "质量监督检测站", "内蒙古自治区烟草质量监督检测站检测专用章", bRootCert, bRootPfx, new String(bPin));
			if(rootCert != null){
				File f = new File("d:/test/sm2user.pfx");
				FileOutputStream fos = new FileOutputStream(f);
				fos.write(rootCert.bPfx);
				fos.close();
				
				f = new File("d:/test/sm2user.cer");
				fos = new FileOutputStream(f);
				fos.write(rootCert.bCert);
				fos.close();	
				
				f = new File("d:/test/sm2user.txt");
				fos = new FileOutputStream(f);
				fos.write(rootCert.sPin.getBytes());
				fos.close();
				
				f = new File("d:/test/sm2userbcprv.txt");
				fos = new FileOutputStream(f);
				fos.write(rootCert.sPrivateKey.getBytes());
				fos.close();
				
				f = new File("d:/test/sm2userbcpub.txt");
				fos = new FileOutputStream(f);
				fos.write(rootCert.sPublicKey.getBytes());
				fos.close();
			}
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
//		File f = new File("d:/test/sm2root.cer");
//		FileInputStream fis = new FileInputStream(f);
//		int iLen = (int)f.length();
//		byte[] b = new byte[iLen];
//		fis.read(b);
//		fis.close();
//		
//		SM2Crypto.GetPublicKey(b);
		
	}

}
