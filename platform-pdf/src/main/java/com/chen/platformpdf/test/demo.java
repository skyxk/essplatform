package com.chen.platformpdf.test;


import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.File;
import java.io.InputStream;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;


import static com.chen.core.util.Base64Utils.ESSGetBase64Decode;
import static com.chen.core.util.FileUtils.byte2Input;
import static com.chen.core.util.FileUtils.fileToByte;

public class demo {
    /**
     * 验签
     * @throws Exception
     */
    public static void setUp() throws Exception {
        BouncyCastleProvider bcp = new BouncyCastleProvider();
        //Security.addProvider(bcp);
        Security.insertProviderAt(bcp, 1);
    }

    public static PublicKey getPublicKey(String fileName){
        PublicKey publicKey = null;
        try {
            //读取证书文件
            byte[] cerByte = fileToByte(new File(fileName));

//            byte[] cerByte = ESSGetBase64Decode(fileName);

            InputStream inStream = byte2Input(cerByte);
            //创建X509工厂类
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            //创建证书对象
            X509Certificate oCert = (X509Certificate)cf.generateCertificate(inStream);
            inStream.close();
            publicKey = oCert.getPublicKey();
        }
        catch (Exception e) {
            System.out.println("解析证书出错！");
        }

        return publicKey;
    }
}
