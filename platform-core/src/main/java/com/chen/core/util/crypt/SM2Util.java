package com.chen.core.util.crypt;


import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.gm.GMNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.*;
import org.bouncycastle.crypto.signers.SM2Signer;
import org.bouncycastle.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.crypto.util.SubjectPublicKeyInfoFactory;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Base64Encoder;
import org.bouncycastle.util.encoders.Hex;



import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

/**
 * @Author: dzy
 * @Date: 2018/9/28 15:53
 * @Describe: SM2工具类
 */
public class SM2Util {

    /**
     * 生成SM2公私钥对
     * @return
     */
    private static AsymmetricCipherKeyPair genKeyPair0() {
        //获取一条SM2曲线参数
        X9ECParameters sm2ECParameters = GMNamedCurves.getByName("sm2p256v1");

        //构造domain参数
        ECDomainParameters domainParameters = new ECDomainParameters(sm2ECParameters.getCurve(),
                sm2ECParameters.getG(), sm2ECParameters.getN());

        //1.创建密钥生成器
        ECKeyPairGenerator keyPairGenerator = new ECKeyPairGenerator();

        //2.初始化生成器,带上随机数
        try {
            keyPairGenerator.init(new ECKeyGenerationParameters(domainParameters, SecureRandom.getInstance("SHA1PRNG")));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        //3.生成密钥对
        AsymmetricCipherKeyPair asymmetricCipherKeyPair = keyPairGenerator.generateKeyPair();
        return asymmetricCipherKeyPair;
    }
    
    public static AsymmetricCipherKeyPair ConvertToBCKeyPair(String sPublicKey,String sPrivateKey) throws IOException
    {
		ByteArrayOutputStream bOutput1 = new ByteArrayOutputStream();
		Base64Encoder b64 = new Base64Encoder();
		b64.decode(sPrivateKey,bOutput1);
		byte[] privateKey = bOutput1.toByteArray();
		bOutput1.reset();
		b64.decode(sPublicKey, bOutput1);
		byte[] publicKey = bOutput1.toByteArray();
		bOutput1.close();
        // 获取一条SM2曲线参数
        X9ECParameters sm2ECParameters = GMNamedCurves.getByName("sm2p256v1");
        // 构造domain参数
        ECDomainParameters domainParameters = new ECDomainParameters(sm2ECParameters.getCurve(),
                sm2ECParameters.getG(),
                sm2ECParameters.getN());
        //提取公钥点
        ECPoint pukPoint = sm2ECParameters.getCurve().decodePoint(publicKey);
        // 公钥前面的02或者03表示是压缩公钥，04表示未压缩公钥, 04的时候，可以去掉前面的04
        ECPublicKeyParameters publicKeyParameters = new ECPublicKeyParameters(pukPoint, domainParameters);
        
        BigInteger privateKeyD = new BigInteger(1,privateKey);
        ECPrivateKeyParameters privateKeyParameters = new ECPrivateKeyParameters(privateKeyD, domainParameters);
        AsymmetricCipherKeyPair ak = new AsymmetricCipherKeyPair(publicKeyParameters,privateKeyParameters);
        return ak;
    }
    

    /***
     * 将BC的  AsymmetricCipherKeyPair 转为 JCE的 KeyPair
     * 
     */
    public static KeyPair convertBcToJceKeyPair(AsymmetricCipherKeyPair bcKeyPair) {
    	try
    	{
	    	Security.addProvider(new BouncyCastleProvider());  	
	    	bcKeyPair.getPrivate();
	        byte[] pkcs8Encoded = PrivateKeyInfoFactory.createPrivateKeyInfo(bcKeyPair.getPrivate()).getEncoded();
	        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(pkcs8Encoded);
	        byte[] spkiEncoded = SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(bcKeyPair.getPublic()).getEncoded();
	        X509EncodedKeySpec spkiKeySpec = new X509EncodedKeySpec(spkiEncoded);
	        KeyFactory keyFac = KeyFactory.getInstance("EC","BC");
	        return new KeyPair(keyFac.generatePublic(spkiKeySpec), keyFac.generatePrivate(pkcs8KeySpec));
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    		return null;
    	}
    }
    
    /**
     * 生成公私钥对(默认压缩公钥)
     * @return
     */
    public static PCIKeyPair genKeyPair() {
        return genKeyPair(true);
    }

    /**
     * 生成公私钥对
     * @param compressedPubKey  是否压缩公钥
     * @return
     */
    public static PCIKeyPair genKeyPair(boolean compressedPubKey) {
        AsymmetricCipherKeyPair asymmetricCipherKeyPair = genKeyPair0();
        //提取公钥点
        ECPoint ecPoint = ((ECPublicKeyParameters) asymmetricCipherKeyPair.getPublic()).getQ();
        //公钥前面的02或者03表示是压缩公钥,04表示未压缩公钥,04的时候,可以去掉前面的04
        byte[] pubKey = ecPoint.getEncoded(compressedPubKey);
        BigInteger privatekey = ((ECPrivateKeyParameters) asymmetricCipherKeyPair.getPrivate()).getD();
        byte[] priKey = privatekey.toByteArray();
        if(priKey[0] == 0)
        {
        	byte[] tmp = new byte[priKey.length - 1];
        	System.arraycopy(priKey, 1, tmp, 0, tmp.length);
        	priKey = tmp;
        }
        PCIKeyPair keyPair = new PCIKeyPair();
        keyPair.SetKeyPair(priKey, pubKey);
        return keyPair;
    }

    /**
     * 私钥签名
     * @param privateKey    私钥
     * @param message       待签名内容
     * @return
     */
    public static byte[] sign(byte[] privateKey, byte[] message) {
        //获取一条SM2曲线参数
        X9ECParameters sm2ECParameters = GMNamedCurves.getByName("sm2p256v1");
        //构造domain参数
        ECDomainParameters domainParameters = new ECDomainParameters(sm2ECParameters.getCurve(),
                sm2ECParameters.getG(), sm2ECParameters.getN());

        BigInteger privateKeyD = new BigInteger(1,privateKey);
        ECPrivateKeyParameters privateKeyParameters = new ECPrivateKeyParameters(privateKeyD, domainParameters);

        
        
        //创建签名实例
        SM2Signer sm2Signer = new SM2Signer();

        //初始化签名实例,带上ID,国密的要求,ID默认值:1234567812345678
        try {
            sm2Signer.init(true, new ParametersWithID(new ParametersWithRandom(privateKeyParameters, SecureRandom.getInstance("SHA1PRNG")), Strings.toByteArray("1234567812345678")));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        //生成签名,签名分为两部分r和s,分别对应索引0和1的数组
        sm2Signer.update(message, 0, message.length);
        try {
			byte[] bSignVal = sm2Signer.generateSignature();
			return bSignVal;
		} catch (CryptoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

//        byte[] rBytes = modifyRSFixedBytes(bigIntegers[0].toByteArray());
//        byte[] sBytes = modifyRSFixedBytes(bigIntegers[1].toByteArray());
//
//        byte[] signBytes = ByteUtils.concatenate(rBytes, sBytes);
//        String sign = Hex.toHexString(signBytes);
//
//        return sign;
    }

    /**
     * 将R或者S修正为固定字节数
     * @param rs
     * @return
     */
    private static byte[] modifyRSFixedBytes(byte[] rs) {
        int length = rs.length;
        int fixedLength = 32;
        byte[] result = new byte[fixedLength];
        if (length < 32) {
            System.arraycopy(rs, 0, result, fixedLength - length, length);
        } else {
            System.arraycopy(rs, length - fixedLength, result, 0, fixedLength);
        }
        return result;
    }

    /**
     * 验证签名
     * @param publicKey     公钥
     * @param message       待签名内容
     * @param signData          签名值
     * @return
     */
    public static boolean verify(byte[] publicKey, byte[] message, byte[] signData) {
        // 获取一条SM2曲线参数
        X9ECParameters sm2ECParameters = GMNamedCurves.getByName("sm2p256v1");
        // 构造domain参数
        ECDomainParameters domainParameters = new ECDomainParameters(sm2ECParameters.getCurve(),
                sm2ECParameters.getG(),
                sm2ECParameters.getN());
        //提取公钥点
        ECPoint pukPoint = sm2ECParameters.getCurve().decodePoint(publicKey);
        // 公钥前面的02或者03表示是压缩公钥，04表示未压缩公钥, 04的时候，可以去掉前面的04
        ECPublicKeyParameters publicKeyParameters = new ECPublicKeyParameters(pukPoint, domainParameters);

        //获取签名
//        BigInteger R = null;
//        BigInteger S = null;
        byte[] rBy = new byte[33];
        System.arraycopy(signData, 0, rBy, 1, 32);
        rBy[0] = 0x00;
        byte[] sBy = new byte[33];
        System.arraycopy(signData, 32, sBy, 1, 32);
        sBy[0] = 0x00;
//        R = new BigInteger(rBy);
//        S = new BigInteger(sBy);

        //创建签名实例
        SM2Signer sm2Signer = new SM2Signer();
        ParametersWithID parametersWithID = new ParametersWithID(publicKeyParameters, Strings.toByteArray("1234567812345678"));
        sm2Signer.init(false, parametersWithID);

        //验证签名结果
        sm2Signer.update(message, 0, message.length);
        boolean verify = sm2Signer.verifySignature(signData);
        return verify;
    }

    /**
     * SM2加密算法
     * @param publicKey     公钥
     * @param data          数据
     * @return
     */
    public static String encrypt(byte[] publicKey, String data){
        // 获取一条SM2曲线参数
        X9ECParameters sm2ECParameters = GMNamedCurves.getByName("sm2p256v1");
        // 构造domain参数
        ECDomainParameters domainParameters = new ECDomainParameters(sm2ECParameters.getCurve(),
                sm2ECParameters.getG(),
                sm2ECParameters.getN());
        //提取公钥点
        ECPoint pukPoint = sm2ECParameters.getCurve().decodePoint(publicKey);
        // 公钥前面的02或者03表示是压缩公钥，04表示未压缩公钥, 04的时候，可以去掉前面的04
        ECPublicKeyParameters publicKeyParameters = new ECPublicKeyParameters(pukPoint, domainParameters);

        SM2Engine sm2Engine = new SM2Engine();
        sm2Engine.init(true, new ParametersWithRandom(publicKeyParameters, new SecureRandom()));

        byte[] arrayOfBytes = null;
        try {
            byte[] in = data.getBytes("utf-8");
            arrayOfBytes = sm2Engine.processBlock(in, 0, in.length);
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return Hex.toHexString(arrayOfBytes);
    }

    /**
     * SM2加密算法
     * @param publicKey     公钥
     * @param data          明文数据
     * @return
     */
    public static String encrypt(PublicKey publicKey, String data) {

        ECPublicKeyParameters ecPublicKeyParameters = null;
        if (publicKey instanceof BCECPublicKey) {
            BCECPublicKey bcecPublicKey = (BCECPublicKey) publicKey;
            ECParameterSpec ecParameterSpec = bcecPublicKey.getParameters();
            ECDomainParameters ecDomainParameters = new ECDomainParameters(ecParameterSpec.getCurve(),
                    ecParameterSpec.getG(), ecParameterSpec.getN());
            ecPublicKeyParameters = new ECPublicKeyParameters(bcecPublicKey.getQ(), ecDomainParameters);
        }

        SM2Engine sm2Engine = new SM2Engine();
        sm2Engine.init(true, new ParametersWithRandom(ecPublicKeyParameters, new SecureRandom()));

        byte[] arrayOfBytes = null;
        try {
            byte[] in = data.getBytes("utf-8");
            arrayOfBytes = sm2Engine.processBlock(in,0, in.length);
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return Hex.toHexString(arrayOfBytes);
    }

    /**
     * SM2解密算法
     * @param privateKey    私钥
     * @param cipherData    密文数据
     * @return
     */
    public static String decrypt(String privateKey, String cipherData) {
        byte[] cipherDataByte = Hex.decode(cipherData);

        //获取一条SM2曲线参数
        X9ECParameters sm2ECParameters = GMNamedCurves.getByName("sm2p256v1");
        //构造domain参数
        ECDomainParameters domainParameters = new ECDomainParameters(sm2ECParameters.getCurve(),
                sm2ECParameters.getG(), sm2ECParameters.getN());

        BigInteger privateKeyD = new BigInteger(privateKey, 16);
        ECPrivateKeyParameters privateKeyParameters = new ECPrivateKeyParameters(privateKeyD, domainParameters);

        SM2Engine sm2Engine = new SM2Engine();
        sm2Engine.init(false, privateKeyParameters);

        String result = null;
        try {
            byte[] arrayOfBytes = sm2Engine.processBlock(cipherDataByte, 0, cipherDataByte.length);
            return new String(arrayOfBytes, "utf-8");
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return result;

    }
    

    /**
     * BC的SM3withSM2验签需要的rs是asn1格式的，这个方法将直接拼接r||s的字节数组转化成asn1格式
     * @param sign in plain byte array
     * @return rs result in asn1 format
     */
    public static byte[] rsPlainByteArrayToAsn1(byte[] sign){
        if(sign.length != 32 * 2) throw new RuntimeException("err rs. ");
        BigInteger r = new BigInteger(1, Arrays.copyOfRange(sign, 0, 32));
        BigInteger s = new BigInteger(1, Arrays.copyOfRange(sign, 32, 32 * 2));
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new ASN1Integer(r));
        v.add(new ASN1Integer(s));
        try {
            return new DERSequence(v).getEncoded("DER");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * SM2解密算法
     * @param privateKey        私钥
     * @param cipherData        密文数据
     * @return
     */
    public static String decrypt(PrivateKey privateKey, String cipherData) {
        byte[] cipherDataByte = Hex.decode(cipherData);

        BCECPrivateKey bcecPrivateKey = (BCECPrivateKey) privateKey;
        ECParameterSpec ecParameterSpec = bcecPrivateKey.getParameters();

        ECDomainParameters ecDomainParameters = new ECDomainParameters(ecParameterSpec.getCurve(),
                ecParameterSpec.getG(), ecParameterSpec.getN());

        ECPrivateKeyParameters ecPrivateKeyParameters = new ECPrivateKeyParameters(bcecPrivateKey.getD(),
                ecDomainParameters);

        SM2Engine sm2Engine = new SM2Engine();
        sm2Engine.init(false, ecPrivateKeyParameters);

        String result = null;
        try {
            byte[] arrayOfBytes = sm2Engine.processBlock(cipherDataByte, 0, cipherDataByte.length);
            return new String(arrayOfBytes, "utf-8");
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return result;
    }
    
    public static void main(String[] args) throws IOException {
    	
    	
    	
//    	File f = new File("d:/test/sm2rootbcprv.txt");
//    	FileInputStream fis = new FileInputStream(f);
//    	int iLen = (int)f.length();
//    	byte[] bPrv = new byte[iLen];
//    	fis.read(bPrv);
//    	fis.close();
//    	
//    	f = new File("d:/test/sm2rootbcpub.txt");
//    	fis = new FileInputStream(f);
//    	iLen = (int)f.length();
//    	byte[] bPub = new byte[iLen];
//    	fis.read(bPub);
//    	fis.close();	
//    	
//    	ByteArrayOutputStream bOutput = new ByteArrayOutputStream();
//    	Base64Encoder b64 = new Base64Encoder();
//    	b64.decode(new String(bPub), bOutput);
//    	bPub = bOutput.toByteArray();
//    	bOutput.reset();
//    	b64.decode(new String(bPrv), bOutput);
//    	bPrv = bOutput.toByteArray();
//    	bOutput.close(); 	
//    	
//    	byte[] bpubno64 = new byte[bPub.length];
//    	System.arraycopy(bPub, 0, bpubno64, 0, bPub.length);
//
//    	
//    	byte[] sPrk = bPrv;
//    	String s = "13456782345673456789456789567890567890";
//    	byte[] bSignVal = SM2Util.sign(sPrk, s.getBytes());
//    	if(bSignVal != null)
//    		System.out.println(bSignVal.length);
//    	else
//    		return;
//    	s = "13456782345673456789456789567890567890";
//    	if( SM2Util.verify(bPub, s.getBytes(), bSignVal) == true )
//    	{
//    		System.out.println("YES");
//    	}else
//    		System.out.println("NO");
//    	
//    	
//    	
//    	f = new File("d:/test/sm2rootbcprv.txt");
//    	fis = new FileInputStream(f);
//    	iLen = (int)f.length();
//    	bPrv = new byte[iLen];
//    	fis.read(bPrv);
//    	fis.close();
//    	
//    	f = new File("d:/test/sm2rootbcpub.txt");
//    	fis = new FileInputStream(f);
//    	iLen = (int)f.length();
//    	bPub = new byte[iLen];
//    	fis.read(bPub);
//    	fis.close();	
//    	
//    	AsymmetricCipherKeyPair bcPair = SM2Util.ConvertToBCKeyPair(new String(bPub), new String(bPrv));
//    	byte[] b = MySM2.sm2("13456782345673456789456789567890567890".getBytes(), bcPair);
//    	b = SM2Util.rsPlainByteArrayToAsn1(b);
//    	
//    	
//    	if( SM2Util.verify(bpubno64, s.getBytes(), b) == true )
//    	{
//    		System.out.println("YES2");
//    	}else
//    		System.out.println("NO2");
    
    	
    	
    	
    	
    }

//    /**
//     * 将未压缩公钥压缩成压缩公钥
//     * @param pubKey    未压缩公钥(16进制,不要带头部04)
//     * @return
//     */
//    public static String compressPubKey(String pubKey) {
//        pubKey = CustomStringUtils.append("04", pubKey);    //将未压缩公钥加上未压缩标识.
//        // 获取一条SM2曲线参数
//        X9ECParameters sm2ECParameters = GMNamedCurves.getByName("sm2p256v1");
//        // 构造domain参数
//        ECDomainParameters domainParameters = new ECDomainParameters(sm2ECParameters.getCurve(),
//                sm2ECParameters.getG(),
//                sm2ECParameters.getN());
//        //提取公钥点
//        ECPoint pukPoint = sm2ECParameters.getCurve().decodePoint(CommonUtils.hexString2byte(pubKey));
//        // 公钥前面的02或者03表示是压缩公钥，04表示未压缩公钥, 04的时候，可以去掉前面的04
////        ECPublicKeyParameters publicKeyParameters = new ECPublicKeyParameters(pukPoint, domainParameters);
//
//        String compressPubKey = Hex.toHexString(pukPoint.getEncoded(Boolean.TRUE));
//
//        return compressPubKey;
//    }
//
//    /**
//     * 将压缩的公钥解压为非压缩公钥
//     * @param compressKey   压缩公钥
//     * @return
//     */
//    public static String unCompressPubKey(String compressKey) {
//        // 获取一条SM2曲线参数
//        X9ECParameters sm2ECParameters = GMNamedCurves.getByName("sm2p256v1");
//        // 构造domain参数
//        ECDomainParameters domainParameters = new ECDomainParameters(sm2ECParameters.getCurve(),
//                sm2ECParameters.getG(),
//                sm2ECParameters.getN());
//        //提取公钥点
//        ECPoint pukPoint = sm2ECParameters.getCurve().decodePoint(CommonUtils.hexString2byte(compressKey));
//        // 公钥前面的02或者03表示是压缩公钥，04表示未压缩公钥, 04的时候，可以去掉前面的04
////        ECPublicKeyParameters publicKeyParameters = new ECPublicKeyParameters(pukPoint, domainParameters);
//
//        String pubKey = Hex.toHexString(pukPoint.getEncoded(Boolean.FALSE));
//        pubKey = pubKey.substring(2);       //去掉前面的04   (04的时候，可以去掉前面的04)
//
//        return pubKey;
//    }

}