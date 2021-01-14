package com.chen.platformweb.test;

import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.pkcs.CertificationRequestInfo;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.util.encoders.Base64;

import java.io.*;
import java.security.*;

import static com.chen.core.util.Base64Utils.encodeBase64File;


public class demo {

    public static void main(String[] args) throws Exception {
        String aa = encodeBase64File("D:\\测试16.pri");
        System.out.println(aa);

//        String cert = encodeBase64File("D:\\绥化市局党组.cer");
////        String cert = encodeBase64File("D:\\鸡西市局_ESS107_45_45.cer");
//        byte[] cer = ESSGetBase64Decode(cert);
//        String root = encodeBase64File("D:\\SM2SUBCA.cer");
//        byte[] rootByte = ESSGetBase64Decode(root);
//        boolean result = VerifyIssuer(cer,rootByte);
//        System.out.println(result);
//        String sEncString = "JMJ_AAAA";
//        System.out.println(sEncString = sEncString.split("_")[1]);
//        String relativelyPath=System.getProperty( "user.dir" );
//        System.out.println(relativelyPath);
//        String  sealName = "B64_1eO9rdHMst29+LP2v9rT0M/euavLvg==";
//        if (sealName.contains("B64")){
//            sealName = sealName.split("_")[1];
//            sealName = new String(ESSGetBase64Decode(sealName),"GBK");
//            sealName = new String(sealName.getBytes(), StandardCharsets.UTF_8);
//            System.out.println(sealName);
//        }
    }
    /**
     * 将文件转换成byte数组
     * @return
     */
    public static byte[] File2byte(File tradeFile){
        byte[] buffer = null;
        try
        {
            FileInputStream fis = new FileInputStream(tradeFile);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1)
            {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return buffer;
    }

//    public static String genCSR()
//            throws InvalidKeyException, NoSuchAlgorithmException,
//            NoSuchProviderException, SignatureException {
//        try
//        {
//            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
//            X509Name dn = new X509Name("CN=TEST,O=TEST,L=BJ,C=CN");
//            //定义密钥对生成算法
//            KeyPairGenerator keyGen =KeyPairGenerator.getInstance("RSA");
//            //定义加密位数，RSA2048的生成略慢
//            keyGen.initialize(2048);
//            KeyPair kp = keyGen.generateKeyPair();
//
//            PKCS10CertificationRequest p10 = new PKCS10CertificationRequest("SHA1WithRSA", dn, kp.getPublic(),new DERSet(), kp.getPrivate());
//
//            byte[] der = p10.getEncoded();
//            String code = "-----BEGIN CERTIFICATE REQUEST-----\n";
//            code += new String(Base64.encode(der));
//            code += "\n-----END CERTIFICATE REQUEST-----\n";
//            CertificationRequestInfo csrinfo = p10.getCertificationRequestInfo();
//            return code;
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        return null;
//    }
}
