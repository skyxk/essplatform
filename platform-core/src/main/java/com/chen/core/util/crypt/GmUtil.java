package com.chen.core.util.crypt;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.gm.GMNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.*;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jcajce.spec.SM2ParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;
 
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Enumeration;
 
/**
 * need jars:
 * bcpkix-jdk15on-160.jar
 * bcprov-jdk15on-160.jar
 *
 * ref:
 * https://tools.ietf.org/html/draft-shen-sm2-ecdsa-02
 * http://gmssl.org/docs/oid.html
 * http://www.jonllen.com/jonllen/work/164.aspx
 *
 * 用BC的注意点：
 * 这个版本的BC对SM3withSM2的结果为asn1格式的r和s，如果需要直接拼接的r||s需要自己转换。下面rsAsn1ToPlainByteArray、rsPlainByteArrayToAsn1就在干这事。
 * 这个版本的BC对SM2的结果为C1||C2||C3，据说为旧标准，新标准为C1||C3||C2，用新标准的需要自己转换。下面changeC1C2C3ToC1C3C2、changeC1C3C2ToC1C2C3就在干这事。
 */
public class GmUtil {
 
    private static X9ECParameters x9ECParameters = GMNamedCurves.getByName("sm2p256v1");
    private static ECDomainParameters ecDomainParameters = new ECDomainParameters(x9ECParameters.getCurve(), x9ECParameters.getG(), x9ECParameters.getN());
    private static ECParameterSpec ecParameterSpec = new ECParameterSpec(x9ECParameters.getCurve(), x9ECParameters.getG(), x9ECParameters.getN());
    private static final String ecc_n = "FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFF7203DF6B21C6052B53BBF40939D54123";
    static {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }
 
    

	public static byte[] sm2(byte[] md,byte[] bPfx,String sPfxPin)
	{
		KeyStore ks2;
		try {
			ks2 = KeyStore.getInstance("PKCS12", "BC");
			ByteArrayInputStream bys = new ByteArrayInputStream(bPfx);
			ks2.load(bys, sPfxPin.toCharArray());

			Enumeration<String> enum1 = ks2.aliases();
			String keyAlias = null;
	        if (enum1.hasMoreElements()) 
	        {
	            keyAlias = (String)enum1.nextElement();
	        }
	        Key prvK = ks2.getKey(keyAlias, null);
	        BCECPrivateKey sm2PriK = (BCECPrivateKey)prvK;
	        ECParameterSpec localECParameterSpec = sm2PriK.getParameters();
	        ECDomainParameters localECDomainParameters = new ECDomainParameters(
	            localECParameterSpec.getCurve(), localECParameterSpec.getG(),
	            localECParameterSpec.getN());
	        ECPrivateKeyParameters localECPrivateKeyParameters = new ECPrivateKeyParameters(
	        sm2PriK.getD(), localECDomainParameters);
	        
	        PublicKey publicKey = ks2.getCertificate(keyAlias).getPublicKey();
	        ECPublicKeyParameters localECPublicKeyParameters = null;
	        if (publicKey instanceof BCECPublicKey)
	        {
	            BCECPublicKey localECPublicKey = (BCECPublicKey)publicKey;
//	            ECParameterSpec localECParameterSpec2 = localECPublicKey.getParameters();
//	            ECDomainParameters localECDomainParameters2 = new ECDomainParameters(
//	                localECParameterSpec.getCurve(), localECParameterSpec.getG(),
//	                localECParameterSpec.getN());
	            localECPublicKeyParameters = new ECPublicKeyParameters(localECPublicKey.getQ(),
	                localECDomainParameters);
	        }
	        
			BigInteger bg_ecc_n = new BigInteger(ecc_n,16);
		    ECPublicKeyParameters ecpub = localECPublicKeyParameters;
	        // e
	        BigInteger e = new BigInteger(1, md);
	        // k
	        BigInteger k = null;
	        ECPoint kp = null;
	        BigInteger r = null;
	        BigInteger s = null;
	        BigInteger userD = null;
	        do
	        {
	            do
	            {
	                ECPrivateKeyParameters ecpriv = localECPrivateKeyParameters;
	                k = ecpriv.getD();
	                kp = ecpub.getQ();
	                userD = ecpriv.getD();
	                // r
	                r = e.add(kp.getXCoord().toBigInteger());
	                r = r.mod(bg_ecc_n);
	            }
	            while (r.equals(BigInteger.ZERO) || r.add(k).equals(bg_ecc_n));
	            // (1 + dA)~-1
	            BigInteger da_1 = userD.add(BigInteger.ONE);
	            da_1 = da_1.modInverse(bg_ecc_n);
	            // s
	            s = r.multiply(userD);
	            s = k.subtract(s).mod(bg_ecc_n);
	            s = da_1.multiply(s).mod(bg_ecc_n);
	        }
	        while (s.equals(BigInteger.ZERO));

	        byte[] btRS = new byte[64];
//	        byte[] btR = r.toByteArray();
//	        byte[] btS = s.toByteArray();
            byte[] btR = bigIntToFixexLengthBytes(r);
            byte[] btS = bigIntToFixexLengthBytes(s);
	        System.out.println("btR"+btR.length);
            System.out.println("btS"+btS.length);
	        System.arraycopy(btR, btR.length-32, btRS, 0, 32);
	        System.arraycopy(btS, btS.length-32, btRS, 32, 32);
	        return btRS;

		} catch (KeyStoreException | NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	
    /**
     *
     * @param msg
     * @param userId
     * @param privateKey
     * @return r||s，直接拼接byte数组的rs
     */
    public static byte[] signSm3WithSm2(byte[] msg, byte[] userId, PrivateKey privateKey){
        return rsAsn1ToPlainByteArray(signSm3WithSm2Asn1Rs(msg, userId, privateKey));
    }
    
    
    
    /***
     *	将 "30313233"这样的字符串变成0x31 0x32 0x33... 这样的16进制数 
     * 
     */
	private static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) 
		{
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}
	
	private static byte charToByte(char c) 
	{
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	//不做hash，SM2签名（符合国密要求）
	public static byte[] sm2(byte[] md, AsymmetricCipherKeyPair keypair)
	{
		BigInteger bg_ecc_n = new BigInteger(ecc_n,16);
	    ECPublicKeyParameters ecpub = (ECPublicKeyParameters)keypair.getPublic();
        // e
        BigInteger e = new BigInteger(1, md);
        // k
        BigInteger k = null;
        ECPoint kp = null;
        BigInteger r = null;
        BigInteger s = null;
        BigInteger userD = null;
        do
        {
            do
            {
                ECPrivateKeyParameters ecpriv = (ECPrivateKeyParameters)keypair.getPrivate();
                k = ecpriv.getD();
                kp = ecpub.getQ();
                userD = ecpriv.getD();
                // r
                r = e.add(kp.getXCoord().toBigInteger());
                r = r.mod(bg_ecc_n);
            }
            while (r.equals(BigInteger.ZERO) || r.add(k).equals(bg_ecc_n));
            // (1 + dA)~-1
            BigInteger da_1 = userD.add(BigInteger.ONE);
            da_1 = da_1.modInverse(bg_ecc_n);
            // s
            s = r.multiply(userD);
            s = k.subtract(s).mod(bg_ecc_n);
            s = da_1.multiply(s).mod(bg_ecc_n);
        }
        while (s.equals(BigInteger.ZERO));

        byte[] btRS = new byte[64];
        byte[] btR = r.toByteArray();
        byte[] btS = s.toByteArray();
        System.out.println(btR.length);
        System.out.println(btS.length);
        System.arraycopy(btR, btR.length-32, btRS, 0, 32);
        System.arraycopy(btS, btS.length-32, btRS, 32, 32);
//        if( sm2verify(md,btRS,keypair) )
//        	System.out.println("yes........");
//        else
//        	System.out.println("no........");
        return btRS;
	}
	
    
    public static byte[] signSm3WithSm2(byte[] msg,byte[] userId,byte[] bPfx,String sPfxPin)
    {
		String password = sPfxPin; 
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
	        PrivateKey prk = (PrivateKey) ks2.getKey(keyAlias, password.toCharArray());
	        return signSm3WithSm2(msg,userId,prk);
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		return null;
    }
 
    /**
     *
     * @param msg
     * @param userId
     * @param privateKey
     * @return rs in <b>asn1 format</b>
     */
    public static byte[] signSm3WithSm2Asn1Rs(byte[] msg, byte[] userId, PrivateKey privateKey){
        try {
            SM2ParameterSpec parameterSpec = new SM2ParameterSpec(userId);
            Signature signer = Signature.getInstance("SM3withSM2", "BC");
            signer.setParameter(parameterSpec);
            signer.initSign(privateKey, new SecureRandom());
            signer.update(msg, 0, msg.length);
            byte[] sig = signer.sign();
            return sig;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
 
    /**
     *
     * @param msg
     * @param userId
     * @param rs r||s，直接拼接byte数组的rs
     * @param publicKey
     * @return
     */
    public static boolean verifySm3WithSm2(byte[] msg, byte[] userId, byte[] rs, PublicKey publicKey){
        return verifySm3WithSm2Asn1Rs(msg, userId, rsPlainByteArrayToAsn1(rs), publicKey);
    }
 
    /**
     *
     * @param msg
     * @param userId
     * @param rs in <b>asn1 format</b>
     * @param publicKey
     * @return
     */
    public static boolean verifySm3WithSm2Asn1Rs(byte[] msg, byte[] userId, byte[] rs, PublicKey publicKey){
        try {
            SM2ParameterSpec parameterSpec = new SM2ParameterSpec(userId);
            Signature verifier = Signature.getInstance("SM3withSM2", "BC");
            verifier.setParameter(parameterSpec);
            verifier.initVerify(publicKey);
            verifier.update(msg, 0, msg.length);
            return verifier.verify(rs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
 
    /**
     * bc加解密使用旧标c1||c2||c3，此方法在加密后调用，将结果转化为c1||c3||c2
     * @param c1c2c3
     * @return
     */
    private static byte[] changeC1C2C3ToC1C3C2(byte[] c1c2c3) {
        final int c1Len = (x9ECParameters.getCurve().getFieldSize() + 7) / 8 * 2 + 1; //sm2p256v1的这个固定65。可看GMNamedCurves、ECCurve代码。
        final int c3Len = 32; //new SM3Digest().getDigestSize();
        byte[] result = new byte[c1c2c3.length];
        System.arraycopy(c1c2c3, 0, result, 0, c1Len); //c1
        System.arraycopy(c1c2c3, c1c2c3.length - c3Len, result, c1Len, c3Len); //c3
        System.arraycopy(c1c2c3, c1Len, result, c1Len + c3Len, c1c2c3.length - c1Len - c3Len); //c2
        return result;
    }
 
 
    /**
     * bc加解密使用旧标c1||c3||c2，此方法在解密前调用，将密文转化为c1||c2||c3再去解密
     * @param c1c3c2
     * @return
     */
    private static byte[] changeC1C3C2ToC1C2C3(byte[] c1c3c2) {
        final int c1Len = (x9ECParameters.getCurve().getFieldSize() + 7) / 8 * 2 + 1; //sm2p256v1的这个固定65。可看GMNamedCurves、ECCurve代码。
        final int c3Len = 32; //new SM3Digest().getDigestSize();
        byte[] result = new byte[c1c3c2.length];
        System.arraycopy(c1c3c2, 0, result, 0, c1Len); //c1: 0->65
        System.arraycopy(c1c3c2, c1Len + c3Len, result, c1Len, c1c3c2.length - c1Len - c3Len); //c2
        System.arraycopy(c1c3c2, c1Len, result, c1c3c2.length - c3Len, c3Len); //c3
        return result;
    }
 
    /**
     * c1||c3||c2
     * @param data
     * @param key
     * @return
     */
    public static byte[] sm2Decrypt(byte[] data, PrivateKey key){
        return sm2DecryptOld(changeC1C3C2ToC1C2C3(data), key);
    }
 
    /**
     * c1||c3||c2
     * @param data
     * @param key
     * @return
     */
 
    public static byte[] sm2Encrypt(byte[] data, PublicKey key){
        return changeC1C2C3ToC1C3C2(sm2EncryptOld(data, key));
    }
 
    /**
     * c1||c2||c3
     * @param data
     * @param key
     * @return
     */
    public static byte[] sm2EncryptOld(byte[] data, PublicKey key){
        BCECPublicKey localECPublicKey = (BCECPublicKey) key;
        ECPublicKeyParameters ecPublicKeyParameters = new ECPublicKeyParameters(localECPublicKey.getQ(), ecDomainParameters);
        SM2Engine sm2Engine = new SM2Engine();
        sm2Engine.init(true, new ParametersWithRandom(ecPublicKeyParameters, new SecureRandom()));
        try {
            return sm2Engine.processBlock(data, 0, data.length);
        } catch (InvalidCipherTextException e) {
            throw new RuntimeException(e);
        }
    }
 
    /**
     * c1||c2||c3
     * @param data
     * @param key
     * @return
     */
    public static byte[] sm2DecryptOld(byte[] data, PrivateKey key){
        BCECPrivateKey localECPrivateKey = (BCECPrivateKey) key;
        ECPrivateKeyParameters ecPrivateKeyParameters = new ECPrivateKeyParameters(localECPrivateKey.getD(), ecDomainParameters);
        SM2Engine sm2Engine = new SM2Engine();
        sm2Engine.init(false, ecPrivateKeyParameters);
        try {
            return sm2Engine.processBlock(data, 0, data.length);
        } catch (InvalidCipherTextException e) {
            throw new RuntimeException(e);
        }
    }
 
    public static byte[] sm4Encrypt(byte[] keyBytes, byte[] plain){
        if(keyBytes.length != 16) throw new RuntimeException("err key length");
        if(plain.length % 16 != 0) throw new RuntimeException("err data length");
 
        try {
            Key key = new SecretKeySpec(keyBytes, "SM4");
            Cipher out = Cipher.getInstance("SM4/ECB/NoPadding", "BC");
            out.init(Cipher.ENCRYPT_MODE, key);
            return out.doFinal(plain);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
 
    public static byte[] sm4Decrypt(byte[] keyBytes, byte[] cipher){
        if(keyBytes.length != 16) throw new RuntimeException("err key length");
        if(cipher.length % 16 != 0) throw new RuntimeException("err data length");
 
        try {
            Key key = new SecretKeySpec(keyBytes, "SM4");
            Cipher in = Cipher.getInstance("SM4/ECB/NoPadding", "BC");
            in.init(Cipher.DECRYPT_MODE, key);
            return in.doFinal(cipher);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
 
    /**
     * @param bytes
     * @return
     */
    public static byte[] sm3(byte[] bytes) {
        SM3Digest sm3 = new SM3Digest();
        sm3.update(bytes, 0, bytes.length);
        byte[] result = new byte[sm3.getDigestSize()];
        sm3.doFinal(result, 0);
        return result;
    }
 
    private final static int RS_LEN = 32;
 
    private static byte[] bigIntToFixexLengthBytes(BigInteger rOrS){
        // for sm2p256v1, n is 00fffffffeffffffffffffffffffffffff7203df6b21c6052b53bbf40939d54123,
        // r and s are the result of mod n, so they should be less than n and have length<=32
        byte[] rs = rOrS.toByteArray();
        if(rs.length == RS_LEN) return rs;
        else if(rs.length == RS_LEN + 1 && rs[0] == 0) return Arrays.copyOfRange(rs, 1, RS_LEN + 1);
        else if(rs.length < RS_LEN) {
            byte[] result = new byte[RS_LEN];
            Arrays.fill(result, (byte)0);
            System.arraycopy(rs, 0, result, RS_LEN - rs.length, rs.length);
            return result;
        } else {
            throw new RuntimeException("err rs: " + Hex.toHexString(rs));
        }
    }
 
    /**
     * BC的SM3withSM2签名得到的结果的rs是asn1格式的，这个方法转化成直接拼接r||s
     * @param rsDer rs in asn1 format
     * @return sign result in plain byte array
     */
    private static byte[] rsAsn1ToPlainByteArray(byte[] rsDer){
        ASN1Sequence seq = ASN1Sequence.getInstance(rsDer);
        byte[] r = bigIntToFixexLengthBytes(ASN1Integer.getInstance(seq.getObjectAt(0)).getValue());
        byte[] s = bigIntToFixexLengthBytes(ASN1Integer.getInstance(seq.getObjectAt(1)).getValue());
        byte[] result = new byte[RS_LEN * 2];
        System.arraycopy(r, 0, result, 0, r.length);
        System.arraycopy(s, 0, result, RS_LEN, s.length);
        return result;
    }
 
    /**
     * BC的SM3withSM2验签需要的rs是asn1格式的，这个方法将直接拼接r||s的字节数组转化成asn1格式
     * @param sign in plain byte array
     * @return rs result in asn1 format
     */
    private static byte[] rsPlainByteArrayToAsn1(byte[] sign){
        if(sign.length != RS_LEN * 2) throw new RuntimeException("err rs. ");
        BigInteger r = new BigInteger(1, Arrays.copyOfRange(sign, 0, RS_LEN));
        BigInteger s = new BigInteger(1, Arrays.copyOfRange(sign, RS_LEN, RS_LEN * 2));
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new ASN1Integer(r));
        v.add(new ASN1Integer(s));
        try {
            return new DERSequence(v).getEncoded("DER");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
 
    public static KeyPair generateKeyPair(){
        try {
            KeyPairGenerator kpGen = KeyPairGenerator.getInstance("EC", "BC");
            kpGen.initialize(ecParameterSpec, new SecureRandom());
            KeyPair kp = kpGen.generateKeyPair();
            return kp;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
 
    public static BCECPrivateKey getPrivatekeyFromD(BigInteger d){
        ECPrivateKeySpec ecPrivateKeySpec = new ECPrivateKeySpec(d, ecParameterSpec);
        return new BCECPrivateKey("EC", ecPrivateKeySpec, BouncyCastleProvider.CONFIGURATION);
    }
 
    public static BCECPublicKey getPublickeyFromXY(BigInteger x, BigInteger y){
        ECPublicKeySpec ecPublicKeySpec = new ECPublicKeySpec(x9ECParameters.getCurve().createPoint(x, y), ecParameterSpec);
        return new BCECPublicKey("EC", ecPublicKeySpec, BouncyCastleProvider.CONFIGURATION);
    }
 
    public static PublicKey getPublickeyFromX509File(File file){
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509", "BC");
            FileInputStream in = new FileInputStream(file);
            X509Certificate x509 = (X509Certificate) cf.generateCertificate(in);
//            System.out.println(x509.getSerialNumber());
            return x509.getPublicKey();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
 
    public static void main(String[] args) throws IOException, GeneralSecurityException {

    }
}
