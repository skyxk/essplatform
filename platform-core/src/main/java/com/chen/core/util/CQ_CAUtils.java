package com.chen.core.util;
import cn.com.infosec.jce.provider.InfosecProvider;
import cn.com.infosec.netcert.framework.resource.PropertiesKeysRes;

import cn.com.infosec.netcert.rads61.CertManager;
import cn.com.infosec.netcert.rads61.exception.CAException;
import cn.com.infosec.netcert.rads61.exception.RAException;
import cn.com.infosec.netcert.rads61.resource.CustomExtValue;
import com.chen.core.util.crypt.CertStruct;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveGenParameterSpec;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import java.util.*;

import static com.chen.core.util.Base64Utils.*;
import static com.chen.core.util.Gen_pfx.getPfx;
public class CQ_CAUtils {
    //申请证书
    public static String IP =  "10.167.27.102"; //用户标识
    public static int PORT =  22345; //y有效期
    public static String validTime =  "1095"; //y有效期
    public static String template = "ee_sign";//模板
    /**
     * 重庆中烟项目 从CA申请证书
     * @param sProvince 省份
     * @param sCity 城市
     * @param sUnit 单位
     * @param sDepartment 部门
     * @param sUserName 证书名称
     * @param sPfxPin 证书密码
     * @return 证书对象
     */
    public static CertStruct CreateUserSM2PfxByCA(String sProvince,String sCity,String sUnit,String sDepartment,String sUserName,String sPfxPin){
        CertStruct certStru = new CertStruct();
        //组织主题，主题中不能带有空格
        String sSubject = "CN=" + sUserName + ",OU=" + sDepartment + ",O=" + sUnit + ",L=" + sCity + ",ST=" + sProvince + ",C=CN";
        try {
            //初始化链接
            init();
            //申请证书
            Map<String,String> certMap = requestCert(sSubject,sUserName);
            //获取证书请求 和公私钥对
            Map<String,String> csr1 = genCSR(sSubject, "SM2","BC",certStru);
            //下载证书，返回CA颁发签名的公钥证书
            String cert= downloadCert(certMap.get("ref"),certMap.get("authCode"),csr1.get("CSR"));
            //证书二进制
            byte[] bCert = ESSGetBase64Decode(cert);
            //私钥
            byte[] privateKey = ESSGetBase64Decode(csr1.get("PrivateKey"));
            //公钥
            byte[] publicKey = ESSGetBase64Decode(csr1.get("PublicKey"));
            //pfx证书
            byte[] bPfx = getPfx(privateKey,bCert,sPfxPin);
            //组织返回对象
            certStru.bCert = bCert;
            certStru.bPfx = bPfx;
            certStru.sPin = sPfxPin;
            certStru.sPrivateKey =csr1.get("PrivateKey");
            certStru.sPublicKey =csr1.get("PublicKey");
            return certStru;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void init() throws FileNotFoundException, CertificateException, NoSuchProviderException, RAException, CAException {
        //设置基本属性
        cn.com.infosec.netcert.rads61.SysProperty sysProperty = new cn.com.infosec.netcert.rads61.SysProperty();
        // CA服务器IP
        sysProperty.setTransIP(IP);
        // CA服务器Port
        sysProperty.setTransPort(PORT);
        // 加密机
        sysProperty.setHsmName("");
        // 通信协议
        sysProperty.setProtocolName("XML");
        // 密钥索引
        sysProperty.setKeyIdx("C:\\Users\\Administrator\\Desktop\\ESS\\ESS");
        // 密钥口令
        sysProperty.setPwd("111111");
        // 加密算法
        Security.addProvider(new InfosecProvider());
        sysProperty.setSignAlgName("SM3withSM2");
//        sysProperty.setSignAlgName("SHA1withRSA");
        Certificate cert = CertificateFactory.getInstance("X.509","INFOSEC")
                .generateCertificate(new FileInputStream("C:\\Users\\Administrator\\Desktop\\ESS\\ESS.cer"));
        sysProperty.setSignCert(cert);
        // 通信通道类型 ssl或plain
        sysProperty.setChanelEncryptName("plain");
        System.out.println(sysProperty.toString());
        CertManager.setSysProperty(sysProperty);
    }

    public static Map<String,String> requestCert(String subjectDN,String uuid) throws RAException, CAException {
        Properties pro = new Properties();
        pro.put(PropertiesKeysRes.TEMPLATENAME, template);
        pro.put(PropertiesKeysRes.SUBJECTDN, subjectDN);
        pro.put(PropertiesKeysRes.VALIDITYLEN, validTime);
        pro.put(PropertiesKeysRes.UUID, uuid);
        Map<String, List<CustomExtValue>> map = new HashMap<String, List<CustomExtValue>>();
        CertManager manager = CertManager.getInstance();
        Properties p = manager.requestCert(pro, map);
        Map<String,String> result = new HashMap<String, String>();
        String ref = p.getProperty(PropertiesKeysRes.REFNO);
        String authCode = p.getProperty(PropertiesKeysRes.AUTHCODE);
        result.put("ref",ref);
        result.put("authCode",authCode);
        return result;
    }

    public static String downloadCert(String ref,String authCode,String publicKey) throws CAException, RAException {
        String tmpPubKey = "";
        Properties p1 = new Properties();
        p1.setProperty(PropertiesKeysRes.REFNO, ref);
        p1.setProperty(PropertiesKeysRes.AUTHCODE, authCode);
        p1.setProperty(PropertiesKeysRes.PUBLICKEY, publicKey);
        p1.setProperty(PropertiesKeysRes.KMC_KEYLEN, "256");
        p1.setProperty(PropertiesKeysRes.RETSYMALG, "SM4");
        p1.setProperty(PropertiesKeysRes.RETURNTYPE, "CERT");
        CertManager manager = CertManager.getInstance();
        Properties pro1 = manager.downCert(p1);
        String p7 = pro1.getProperty("P7DATA", "");

        String encCer = pro1.getProperty(PropertiesKeysRes.P7DATA_ENC, "");  //加密证书返回内容
        String encPri = pro1.getProperty(PropertiesKeysRes.ENCPRIVATEKEY, "");    //加密证书私钥
        String ukek = pro1.getProperty(PropertiesKeysRes.TEMPUKEK, "");      //rsa才使用这项内容
//        System.out.println("P7DATA"+p7);
        return p7;
    }
    public static String encoder(byte[] a) {
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(a);
    }
    public static byte[] decoder(String a) {
        Base64.Decoder decoder = Base64.getDecoder();
        return decoder.decode(a);
    }
    public static Map<String,String> genCSR(String subject, String alg,String provider,CertStruct certStru)
            throws NoSuchAlgorithmException, OperatorCreationException, IOException, NoSuchProviderException, InvalidAlgorithmParameterException {
        Security.addProvider(new BouncyCastleProvider());
        String signalg="";
        int alglength=0;
        String keyAlg="";
        if(alg.toUpperCase().equals("RSA1024")){
            signalg="SHA1WithRSA";
            alglength=1024;
            keyAlg="RSA";
        }else if(alg.toUpperCase().equals("RSA2048")){
            signalg="SHA1WithRSA";
            alglength=2048;
            keyAlg="RSA";
        }else if(alg.toUpperCase().equals("SM2")){
            signalg="SM3withSM2";
            alglength=256;
            keyAlg="EC";
        }
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(keyAlg,"BC");
        keyGen.initialize(new ECNamedCurveGenParameterSpec("sm2p256v1"));
        KeyPair kp = keyGen.generateKeyPair();
        PKCS10CertificationRequestBuilder builder = new PKCS10CertificationRequestBuilder(new X500Name(subject), SubjectPublicKeyInfo.getInstance(kp.getPublic().getEncoded()));
        JcaContentSignerBuilder jcaContentSignerBuilder = new JcaContentSignerBuilder(signalg);
        jcaContentSignerBuilder.setProvider(provider);
        ContentSigner contentSigner = jcaContentSignerBuilder.build(kp.getPrivate());
        byte[] csrByte = builder.build(contentSigner).getEncoded();
        String r1 = encoder(csrByte);
        StringBuilder r2 = new StringBuilder();
        r2.append("-----BEGIN CERTIFICATE REQUEST-----\n");
        for (int i=0;i<=r1.length();i+=64){
            int j =i+64;
            String aa;
            if (j>r1.length()){
                j=r1.length();
                aa = r1.substring(i,j);
            }else {
                aa = r1.substring(i,j)+"\r\n";
            }
            r2.append(aa);
        }
        r2.append("\n-----END CERTIFICATE REQUEST-----\n");
        Map<String,String> result = new HashMap<>();
        result.put("PrivateKey",encoder( kp.getPrivate().getEncoded()));
        result.put("PublicKey",encoder( kp.getPublic().getEncoded()));
        result.put("CSR",r2.toString());
        return result;
    }
    public static void main(String[] args) throws Exception {

//        CertStruct certStru = new CertStruct();
//        String sSubject = "CN=" + "信息中心" + ",OU=" + "" + ",O=" + "重庆中烟工业" + ",L=" + "重庆" + ",ST=" + "重庆市" + ",C=CN";
//        //获取证书请求 和公私钥对
//        Map<String,String> csr1 = genCSR(sSubject, "SM2","BC",certStru);
//        //私钥
//        String privateKey = csr1.get("PrivateKey");
//        //公钥
//        String publicKey = csr1.get("PublicKey");
//        //公钥
//        String csr = csr1.get("CSR");
//
//        System.out.println("privateKey:"+privateKey );
//        System.out.println("publicKey:"+publicKey );
//        System.out.println("csr:"+csr );

        //私钥
        String privateKey1 = "MHcCAQEEINYIFP9dr6mxOI1oe4XLNKEfl5ftD/CpJRf/KP8ZgiyzoAoGCCqBHM9V\n" +
                "AYItoUQDQgAEPbrLe4SSz2J6s0OGee+/Dar5cY7edctUwWnzJ34oI/eDTWgOViC3\n" +
                "j3bh0vy2AR1D/2JA9aapyS5jkwez0wnysA==";
//        //公钥
//        String csr = "";
//        //证书二进制
        String bCert = encodeBase64File("D:\\Desktop\\信息中心\\信息中心.pfx");
        System.out.println(bCert);
        //私钥
        byte[] privateKey = ESSGetBase64Decode(privateKey1);
        // 加密算法
//        //pfx证书
//        byte[] bPfx = getPfx(privateKey,ESSGetBase64Decode(bCert),"111111");
//        System.out.println(ESSGetBase64Encode(bPfx));
//        Security.addProvider(new BouncyCastleProvider());
//        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKey);
//        KeyFactory keyFactory = KeyFactory.getInstance("EC","INFOSEC");
//        PrivateKey privateKey2 = keyFactory.generatePrivate(keySpec);


    }


}
