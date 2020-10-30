package com.chen.core.util.crypt;

/**
 * @Author: dzy
 * @Date: 2018/9/27 14:18
 * @Describe: 公私钥对
 */
public class PCIKeyPair {
    private byte[] priKey = null;      //私钥
    private byte[] pubKey = null;      //公钥
    public void SetKeyPair(byte[] prK,byte[] pbK)
    {
    	int i1 = prK.length;
    	int i2 = pbK.length;
    	priKey = new byte[i1];
    	pubKey = new byte[i2];
    	System.arraycopy(prK, 0, priKey, 0, i1);
    	System.arraycopy(pbK, 0, pubKey, 0, i2);
    }
    public byte[] GetPrivateKey()
    {
    	return priKey;
    }
    public byte[] GetPublicKey()
    {
    	return pubKey;
    }

}