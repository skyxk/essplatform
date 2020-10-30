package com.chen.core.util.crypt;


import java.math.BigInteger;


import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;

import org.bouncycastle.math.ec.ECPoint;

public class MySM2 {
	public static final String ecc_a = "FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFC";
	public static final String ecc_b = "28E9FA9E9D9F5E344D5A9E4BCF6509A7F39789F515AB8F92DDBCBD414D940E93";
	public static final String ecc_gx = "32C4AE2C1F1981195F9904466A39C9948FE30BBFF2660BE1715A4589334C74C7";
	public static final String ecc_gy = "BC3736A2F4F6779C59BDCEE36B692153D0A9877CC62A474002DF32E52139F0A0";
	public static final String SM2_ID = "31323334353637383132333435363738";
	public static final String ecc_n = "FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFF7203DF6B21C6052B53BBF40939D54123";
	public static final String ecc_p = "FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFF";
	
	private static byte[] BigIntegerToByteArray(BigInteger bi) {
	    byte[] array = bi.toByteArray();
	    if (array[0] == 0) {
	      byte[] tmp = new byte[array.length - 1];
	      System.arraycopy(array, 1, tmp, 0, tmp.length);
	      array = tmp;
	    }
	    return array;
	}
	
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

			
	private static byte[] sm2GetZSM(ECPoint ecp) {

	    SM3Digest sm3 = new SM3Digest();
	    byte[] userId = hexStringToBytes(SM2_ID);
	    int len = userId.length * 8;
	    sm3.update((byte) (len >> 8 & 255));
	    sm3.update((byte) (len & 255));
	    sm3.update(userId, 0, userId.length);
	    byte[] p = hexStringToBytes(ecc_a);
	    sm3.update(p, 0, p.length);
	    p = hexStringToBytes(ecc_b);
	    sm3.update(p, 0, p.length);
	    p = hexStringToBytes(ecc_gx);
	    sm3.update(p, 0, p.length);
	    p = hexStringToBytes(ecc_gy);
	    sm3.update(p, 0, p.length);
	    p = BigIntegerToByteArray(ecp.getXCoord().toBigInteger());
	    sm3.update(p, 0, p.length);
	    p = BigIntegerToByteArray(ecp.getYCoord().toBigInteger());
	    sm3.update(p, 0, p.length);
	    byte[] md = new byte[sm3.getDigestSize()];
	    sm3.doFinal(md, 0);
	    return md;
	}
	
	//获得SM2算法中要签名的hash
	private static byte[] sm2GetSignData(byte[] z, byte[] sourceData) {
	    SM3Digest sm3 = new SM3Digest();
	    sm3.update(z, 0, z.length);
	    sm3.update(sourceData, 0, sourceData.length);
	    byte[] md = new byte[sm3.getDigestSize()];
	    sm3.doFinal(md, 0);
	    return md;
	}
	
	public static byte[] sm2(byte[] md, AsymmetricCipherKeyPair keypair)
	{
		byte[] b_ecc_n = hexStringToBytes(ecc_n);
		BigInteger bg_ecc_n = new BigInteger(1, b_ecc_n);
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
        System.arraycopy(btR, 0, btRS, 0, 32);
        System.arraycopy(btS, 0, btRS, 32, 32);
        return btRS;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		

		
	}

}
