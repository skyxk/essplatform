package com.chen.core.util;



import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import java.net.URLDecoder;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.chen.core.util.Base64Utils.ESSGetBase64Decode;


public class StringUtils {

    /**
     * 自动生成32位的UUid，对应数据库的主键id进行插入用。
     * @return
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**推荐，速度最快
     * 判断是否为整数
     * @param str 传入的字符串
     * @return 是整数返回true,否则返回false
     */
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    /**
     * 获取一个对称加密的随机密码
     * @return  随机密码
     */
//    public static String getEncryptPwd() {
//        //得到一个uuid
//        String uuid = getUUID();
//        //取中间的8位
//        String pwd = uuid.substring(8,16);
//        byte[] b = new byte[0];
//        try {
//            //对密码进行对称加密
//            b = ESSEncryptData(pwd.getBytes(),null,"esspwd".getBytes());
//        } catch (MuticaCryptException e) {
//            e.printStackTrace();
//        }
//        //将加密后的密码base64编码
//        return ESSGetBase64Encode(b);
//    }
    /**
     * 解密本系统生成的密码
     * @param pwd  密码
     * @return 密码明文
     */
//    public static String getDecryptPwd(String pwd) throws MuticaCryptException {
//        byte[]  pwdByte = ESSDecryptData(ESSGetBase64Decode(pwd),"esspwd".getBytes());
//        return new String(pwdByte);
//    }
//    /**
//     * 对传入的字符串惊醒对称加密
//     * @return  加密并base64后的数据
//     */
//    public static String getEncryptString(String data) {
//        if (data==null){
//            return null;
//        }
//        //加密机加密
//        return EncryptIt(data);
////        return data;
//    }
//    /**
//     * 解密本系统生成的加密密文
//     * @return 密码明文
//     */
//    public static String getDecryptString(String data)  {
//        if (data==null){
//            return null;
//        }
//        //加密机解密
//        return DecryptIt(data);
////        return data;
//    }
    /**
     * 获取hash
     */
//    public static String getHash(String data){
//        String safeHash = null;
//        try {
//            byte[] essGetDigest = MuticaCrypt.ESSGetDigest(data.getBytes());
//            safeHash = MuticaCrypt.ESSGetBase64Encode(essGetDigest);
//        } catch (MuticaCryptException e) {
//            e.printStackTrace();
//        }
//        return safeHash;
//    }
    /**
     * 判断字符是否为空
     * @param s 待判断字符
     * @return 判断结果
     */
    public static boolean isNull(String s) {
        if(s == null){
            return false;
        }
        s = s.replace(" ","");
        if("".equals(s) ){
            return false;
        }
        return true;
    }
    public static String getRandom16(){
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 16; i++) {
            String str = random.nextInt(2) % 2 == 0 ? "num" : "char";
            if ("char".equalsIgnoreCase(str)) {
                code.append((char) (random.nextInt(26) + 65));//我写的是只是随机获取大写
            } else { // 产生数字
                code.append(random.nextInt(10));
            }
        }
        return code.toString();
    }

    /**
     * 去除字符串中的空格、回车、换行符、制表符
     * @param str
     * @return
     */
    public static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    /**
     * 去除字符串中的\
     * @param str
     * @return
     */
    public static String replaceJson(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\\\");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    /**
     * 重庆中烟OA系统单点登录解析
     * @param urlParam
     * @return
     * @throws Exception
     */
    public static String decodeSSO(String urlParam)  throws Exception {
        String key = "C9Uqeq7pUf2FqFfgcONbdk+hE6IlecKt";
        String paramValue = URLDecoder.decode(urlParam);
        byte[] keyData = ESSGetBase64Decode(key); // 字符串类型密钥用Base64解码
        //生成SecretKey
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
        DESedeKeySpec dks = new DESedeKeySpec(keyData); // 获取DESKeySpec
        SecretKey securekey = keyFactory.generateSecret(dks); // 获取SecretKey
        // 初始加密操作类
        Cipher cp = Cipher.getInstance("DESede");
        cp.init(Cipher.DECRYPT_MODE, securekey);
        String result = new String(cp.doFinal(ESSGetBase64Decode(paramValue)));
        return result.split(",")[0];
    }
}
