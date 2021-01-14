package com.chen.core.util;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by clt_abc on 2017/5/31.
 */
public class Base64Utils {
    public static void main(String[] args) throws UnsupportedEncodingException {
        byte[] aaa = ESSGetBase64Decode("eyJTRVJWRVJJUCI6IjEwLjk1LjAuMTIiLCJQU1ciOiI2MjM2YWYxYTU4NDc0OTcxODYzZWJhZDAiLCJTRVJWRVJQT1JUIjoiNTU2NiIsIkRPQ1RZUEUiOiJPRkQiLCJUT0tFTiI6IjFkYTlkODY3N2IzMDQyMjJhNDA0NTdhYWM5ZjJmNTNjIn0=");
        System.out.println(new String(aaa,"utf-8"));
    }
    
    /**
     * 将传入数据BASE64编码
     * @param bMsg  ?  编码的数 ?
     * @return String
     */
    public static String ESSGetBase64Encode(byte[] bMsg) {
        BASE64Encoder ben = new BASE64Encoder();
        Pattern p = Pattern.compile("\\s*|\t|\r|\n");
        String sBase64File = ben.encode(bMsg);
        Matcher m = p.matcher(sBase64File);
        sBase64File = m.replaceAll("");
        return replaceBlank(sBase64File);
    }

    /**
     * 将传入数据BASE64解码
     * @param sEncMsg  ?  解码码的数据
     * @return byte[]
     */
    public static byte[] ESSGetBase64Decode(String sEncMsg) {
        byte[] date= null;
        try
        {
            BASE64Decoder bdr = new BASE64Decoder();
            date = bdr.decodeBuffer(sEncMsg);
            return date;
        }catch(IOException e)
        {
//            throw(new MuticaCryptException(e.getMessage()));
        }
        return date;
    }

    /**
     * 将传入多媒体文件转化为Base64
     * @param FileData  ?  解码码的数据
     * @return byte[]
     */
//    public static String Base64MultipartFileToString(MultipartFile FileData) {
//        String dataString = null ;
//        try {
//            BASE64Encoder encoder = new BASE64Encoder();
//            // 通过base64来转化图片
//            dataString = encoder.encode(FileData.getBytes());
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return dataString;
//    }

    /**
     * <p>将文件转成base64 字符串</p>
     * @param path 文件路径
     * @return
     * @throws Exception
     */
    public static String encodeBase64File(String path) throws Exception {
        File file = new File(path);
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int)file.length()];
        inputFile.read(buffer);
        inputFile.close();
        return ESSGetBase64Encode(buffer);
    }
    /**
     * <p>将base64字符解码保存文件</p>
     * @param base64Code
     * @param targetPath
     * @throws Exception
     */
    public static void decoderBase64File(String base64Code,String targetPath) throws Exception {
        byte[] buffer = ESSGetBase64Decode(base64Code);
        FileOutputStream out = new FileOutputStream(targetPath);
        out.write(buffer);
        out.close();
    }

    public static void byteToFile(byte[] bytes,File targetPath) throws Exception {
        FileOutputStream out = new FileOutputStream(targetPath);
        out.write(bytes);
        out.close();
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
    public static byte[] getImageStream(String path) {

        byte[] buffer = null;
        File file = new File(path);
        FileInputStream fis;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            fis = new FileInputStream(file);
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
            if(file.exists()) {
                file.delete();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }
    public static void readBin2Image(byte[] byteArray, String targetPath) {
        InputStream in = new ByteArrayInputStream(byteArray);
        File file = new File(targetPath);
        String path = targetPath.substring(0, targetPath.lastIndexOf("/"));
        if (!file.exists()) {
            new File(path).mkdir();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            int len = 0;
            byte[] buf = new byte[1024];
            while ((len = in.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
