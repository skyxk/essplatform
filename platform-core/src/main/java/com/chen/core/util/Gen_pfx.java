package com.chen.core.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 *  1. pfx 是什么？
 *       pfx  是带有  私钥的证书文件，是符合 pkcs12标准的证书文件。
 *  2. keystore是什么 ？
 *       keystore 是 类似于证书库的存在，记录一条证书的信息，可以通过 指定算法，序列号、私钥、证书来产生。
 *  3. 跟 chain  有什么关系 ？
 *        还不清楚？？？
 *
 * @author Administrator
 *
 */
public class Gen_pfx {
    /**
     *  1. 获取 私钥  ------ 通过字符串经过 BASE64编码成 字节数组，然后通过 KeyFactory 转换成私钥
     *  2. 获取 证书------- 通过 CertificateFactory 获取
     *  3. 获取 keystore------- 通过指定的属性值来生成
     *  4. 生成 pfx 证书
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        //=====私钥Base64字符串转私钥对象
        PrivateKey privateKey2 = getPrivateKey("MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQglu0MM7KHjnh0Z6Pr\n" +
                "i2tO74lTTMrYqGw+7nte9WXLGeegCgYIKoEcz1UBgi2hRANCAASVafpbSCuOJfqZ\n" +
                "rhrijnrVslWJhKganMox20j28ksfsWcDRPfMs15cXMuB5p1qjMw42LmMYwQ9zOfw\n" +
                "ZqiSRA6b");
        System.out.println("私钥Base64字符串2：" + Base64.encodeBase64String(privateKey2.getEncoded()));
        // ===导入证书数据
        CertificateFactory cf = CertificateFactory.getInstance("X.509","BC");
        X509Certificate certificate = (X509Certificate)cf.generateCertificate(new FileInputStream("D:\\18.cer"));

        //PFX：合成pfx（需要私钥、公钥证书）
        String savePath = generatorPFX(privateKey2, certificate, "123456", new File("D:\\18.pfx"));
        System.out.println(savePath);
    }

    /**
     * 根据私钥和cer证书文件合成pfx证书文件（国密格式）
     * @param privateKey 私钥
     * @param certByte 证书
     * @param pin pfx密码
     * @return pfx文件
     * @throws Exception
     */
    public static byte[] getPfx(byte[] privateKey,byte[] certByte,String pin) throws Exception{
        PrivateKey privateKey2 = getPrivateKey(privateKey);
        // ===导入证书数据
        CertificateFactory cf = CertificateFactory.getInstance("X.509","BC");
        ByteArrayInputStream bys = new ByteArrayInputStream(certByte);
        X509Certificate certificate = (X509Certificate)cf.generateCertificate(bys);
        //PFX：合成pfx（需要私钥、公钥证书）
        byte[] pkcs12Byte = generatorPkcx12(privateKey2, certificate, pin);
        return pkcs12Byte;
    }
    /**
     * 根据私钥Base64字符串获取私钥对象
     * @param privateKeyStr
     * @return
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(String privateKeyStr) throws Exception {
        byte[] privateKeyByte = Base64.decodeBase64(privateKeyStr);
        return getPrivateKey(privateKeyByte);
    }
    /**
     * 根据私钥字节数组获取私钥对象
     * @param privateKeyByte
     * @return
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(byte[] privateKeyByte) throws Exception {
        PrivateKey privateKey = null;
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyByte);
        KeyFactory keyFactory = getKeyFactory("EC");
        privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }
    /**
     * 获取指定算法的keyFactory
     * @param algorithm
     * @return
     */
    private static KeyFactory getKeyFactory(String algorithm) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm,"BC");
        return keyFactory;
    }
    /**
     * 根据私钥、公钥证书、密码生成pkcs12
     *
     * @param privateKey      私钥
     * @param x509Certificate 公钥证书
     * @param password        需要设置的密钥
     * @return
     * @throws Exception
     */
    public static byte[] generatorPkcx12(PrivateKey privateKey, X509Certificate x509Certificate, String password)
            throws Exception {
        Certificate[] chain = {x509Certificate};
        KeyStore keystore = KeyStore.getInstance("PKCS12");
        keystore.load(null, password.toCharArray());
        keystore.setKeyEntry(x509Certificate.getSerialNumber().toString(), privateKey, password.toCharArray(), chain);
        ByteArrayOutputStream bytesos = new ByteArrayOutputStream();
        keystore.store(bytesos, password.toCharArray());
        byte[] bytes = bytesos.toByteArray();
        return bytes;
    }
    /**
     * 根据私钥、公钥证书、密钥，保存为pfx文件
     *
     * @param privateKey      私钥
     * @param x509Certificate 公钥证书
     * @param password        打开pfx的密钥
     * @param saveFile        保存的文件
     * @return
     * @throws Exception
     */
    public static String generatorPFX(PrivateKey privateKey, X509Certificate x509Certificate, String password, File
            saveFile) throws Exception {
        //判断文件是否存在
        if (!saveFile.exists()) {
            //判断文件的目录是否存在
            if (!saveFile.getParentFile().exists()) {
                saveFile.getParentFile().mkdirs();
            }
            saveFile.createNewFile();
        }
        byte[] pkcs12Byte = generatorPkcx12(privateKey, x509Certificate, password);
        Base64Utils.byteToFile(pkcs12Byte,saveFile);
        return saveFile.getPath();
    }
}


