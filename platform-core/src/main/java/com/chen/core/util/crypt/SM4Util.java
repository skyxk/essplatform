package com.chen.core.util.crypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.chen.core.util.Base64Utils;
import org.bouncycastle.crypto.engines.SM4Engine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.encoders.Hex;

/**
 * @Author: dzy
 * @Date: 2018/10/9 16:41
 * @Describe: SM4算法
 */
public class SM4Util {

    //加解密的字节快大小
    public static final int BLOCK_SIZE = 16;

    /**
     * SM4ECB加密算法
     * @param in            待加密内容
     * @param keyBytes      密钥
     * @return
     */
    public static byte[] encryptByEcb0(byte[] in, byte[] keyBytes) {
    	
        byte[] bZero = new byte[BLOCK_SIZE];
        for(int i=0;i<BLOCK_SIZE;i++)
        	bZero[i] = 0;
        
        SM4Engine sm4Engine = new SM4Engine();
        sm4Engine.init(true, new KeyParameter(keyBytes));
        int inLen = in.length;

        int times = inLen / BLOCK_SIZE;        
        
        if( BLOCK_SIZE * times < in.length )
        	times = times + 1;
        
        byte[] out = new byte[BLOCK_SIZE * times + BLOCK_SIZE];
        
        int iLen = BLOCK_SIZE * times;
        byte[] b = new byte[iLen];
        System.arraycopy(bZero, 0, b, iLen - BLOCK_SIZE, BLOCK_SIZE);
        System.arraycopy(in, 0, b, 0, in.length);


        for (int i = 0; i < times; i++) {
            sm4Engine.processBlock(b, i * BLOCK_SIZE, out, i * BLOCK_SIZE);
        }
        
        String sLen = Integer.toString(inLen);
        System.arraycopy(bZero, 0, out, out.length - BLOCK_SIZE, BLOCK_SIZE);
        System.arraycopy(sLen.getBytes(), 0, out, out.length - BLOCK_SIZE, sLen.length());

        return out;
    }

//    /**
//     * SM4ECB加密算法
//     * @param in            待加密内容
//     * @param keyBytes      密钥
//     * @return
//     */
//    public static String encryptByEcb(byte[] in, byte[] keyBytes) {
//        byte[] out = encryptByEcb0(in, keyBytes);
//        String cipher = Hex.toHexString(out);
//        return cipher;
//    }
//
//    /**
//     * SM4的ECB加密算法
//     * @param content   待加密内容
//     * @param key       密钥
//     * @return
//     */
//    public static String encryptByEcb(String content, String key) {
//        byte[] in = Hex.decode(content);
//        byte[] keyBytes = Hex.decode(key);
//
//        String cipher = encryptByEcb(in, keyBytes);
//        return cipher;
//    }

    /**
     * SM4的ECB解密算法
     * @param in        密文内容
     * @param keyBytes  密钥
     * @return
     */
    public static byte[] decryptByEcb0(byte[] in, byte[] keyBytes) {  
        SM4Engine sm4Engine = new SM4Engine();
        sm4Engine.init(false, new KeyParameter(keyBytes));
        int inLen = in.length - BLOCK_SIZE;
        byte[] bPlain = new byte[inLen];
        System.arraycopy(in, 0, bPlain, 0, inLen);
        

        int times = inLen / BLOCK_SIZE;
        if( BLOCK_SIZE * times < inLen )
        	return null;
        

        byte[] out = new byte[inLen];
        for (int i = 0; i < times; i++) {
            sm4Engine.processBlock(bPlain, i * BLOCK_SIZE, out, i * BLOCK_SIZE);
        }
        
        int iLength = 0;
        for(int i=0;i<BLOCK_SIZE;i++)
        {
        	if(in[inLen + i] >= '0' && in[inLen + i] <= '9')
        		iLength++;
        	else
        		break;
        }
        byte[] bLen = new byte[iLength];
        System.arraycopy(in, inLen, bLen, 0, iLength);
        int iTrue = Integer.parseInt(new String(bLen));
        byte[] bout = new byte[iTrue];
        System.arraycopy(out, 0, bout, 0, iTrue);
        return bout;
    }

//    /**
//     * SM4的ECB解密算法
//     * @param in        密文内容
//     * @param keyBytes  密钥
//     * @return
//     */
//    public static String decryptByEcb(byte[] in, byte[] keyBytes) {
//        byte[] out = decryptByEcb0(in, keyBytes);
//        String plain = Hex.toHexString(out);
//        return plain;
//    }
//
//    /**
//     * SM4的ECB解密算法
//     * @param cipher    密文内容
//     * @param key       密钥
//     * @return
//     */
//    public static String decryptByEcb(String cipher, String key) {
//        byte[] in = Hex.decode(cipher);
//        byte[] keyBytes = Hex.decode(key);
//
//        String plain = decryptByEcb(in, keyBytes);
//        return plain;
//    }

    
    public static void main(String[] args) throws IOException{
    	String sPlain = "江西烟草测试章";
    	String sPwd = "514e48f534aab801";
    	byte[] bEnc2 = SM4Util.encryptByEcb0(sPlain.getBytes(), sPwd.getBytes());
    	System.out.println(Base64Utils.ESSGetBase64Encode(bEnc2));
        bEnc2 = Base64Utils.ESSGetBase64Decode("SDfQ+mEAj+LD6EO8RSniaTYAAAAAAAAAAAAAAAAAAAA=");
        byte[] bEnc = SM4Util.decryptByEcb0(bEnc2, sPwd.getBytes());
        System.out.println(new String(bEnc,"utf-8"));
        System.out.println(new String(bEnc,"GBK"));
    }
}