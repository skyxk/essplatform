package com.chen.platformweb.test;

import static com.chen.core.util.Base64Utils.ESSGetBase64Decode;
import static com.chen.core.util.Base64Utils.encodeBase64File;
import static com.chen.core.util.crypt.ESSCertificate.VerifyIssuer;

public class demo {

    public static void main(String[] args) throws Exception {

//        String cert = encodeBase64File("D:\\绥化市局党组.cer");
////        String cert = encodeBase64File("D:\\鸡西市局_ESS107_45_45.cer");
//        byte[] cer = ESSGetBase64Decode(cert);
//        String root = encodeBase64File("D:\\SM2SUBCA.cer");
//        byte[] rootByte = ESSGetBase64Decode(root);
//        boolean result = VerifyIssuer(cer,rootByte);
//        System.out.println(result);
        String sEncString = "JMJ_AAAA";
        System.out.println(sEncString = sEncString.split("_")[1]);

    }
}
