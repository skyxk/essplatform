package com.chen.platformpdf.util;
import cn.com.infosec.netsign.agent.NetSignAgent;
import cn.com.infosec.netsign.agent.NetSignResult;
import cn.com.infosec.netsign.agent.exception.NetSignAgentException;
import cn.com.infosec.netsign.agent.exception.ServerProcessException;
import com.alibaba.fastjson.JSONObject;
import com.chen.dao.WaitForSIgnMapper;
import org.apache.ibatis.session.SqlSession;

import java.io.*;
import java.net.Socket;

import static com.chen.core.util.Base64Utils.ESSGetBase64Decode;
import static com.chen.core.util.Base64Utils.ESSGetBase64Encode;
import static java.lang.Thread.sleep;

public class NetSignAgentUtils {
    public static byte[] signByNet(byte[] plainText){
        try {
            //签名证书DN，null表示用服务器默认证书进行签名
            String subject = null ;
            //是否做TSA签名
            boolean useTSA = false ;
            //签名
            NetSignResult result = NetSignAgent.rawSignature( plainText , subject , useTSA ) ;
            //获取byte形式的签名结果
            byte[] signedText = result.getByteArrayResult( NetSignResult.SIGN_TEXT ) ;

            return signedText;
//            //获取base64编码后的签名结果
//            String signedTextB64 = result.getStringResult( NetSignResult.SIGN_TEXT ) ;
//            //获取获取byte形式的tsa签名
//            byte[] tsaText = result.getByteArrayResult( NetSignResult.TSA_TEXT ) ;
//            //获取获取byte形式的tsa签名
//            String tsaTextB64 = result.getStringResult( NetSignResult.TSA_TEXT ) ;
        } catch ( NetSignAgentException e ) {
            e.printStackTrace();
            System.out.println( "errorCode:" + e.getErrorCode() );
            System.out.println( "errorMsg:" + e.getMessage() );
            return null;
        } catch ( ServerProcessException e ) {
            e.printStackTrace();
            System.out.println( "errorCode:" + e.getErrorCode() );
            System.out.println( "errorMsg:" + e.getMessage() );
            return null;
        }
    }
    public static byte[] SocketSign(byte[] plainText,String documentCode) throws InterruptedException {
        System.out.println("*******************************4");
        //0.发送hash和文档编码
        JSONObject jsonObject_send = new JSONObject();
        jsonObject_send.put("COM","1");
        jsonObject_send.put("DOCID",documentCode);
        jsonObject_send.put("HASH",ESSGetBase64Encode(plainText));
        String re = clientSocket(jsonObject_send.toJSONString());
        //1.循环查询签名情况
        sleep(300);
        for (int i =0 ;i<70;i++){
            JSONObject jsonObject_get = new JSONObject();
            jsonObject_get.put("COM","3");
            jsonObject_get.put("DOCID",documentCode);
            String result = clientSocket(jsonObject_get.toJSONString());
            JSONObject jsonObject_result = JSONObject.parseObject(result);
            if (jsonObject_result.getString("SIGNVAL")!=null){
                System.out.println(jsonObject_result.getString("SIGNVAL"));
                System.out.println("*************************************************");
                return ESSGetBase64Decode(jsonObject_result.getString("SIGNVAL"));
            }
            sleep(500);
        }
        return null;
    }
//    public static byte[] getSign(String UKID,byte[] sh){
//
//        Integer resCode = MapperCreator.exeMapper(WaitForSIgnMapper.class, new MapperCreator.ISQLExecutor<WaitForSIgnMapper, Integer>() {
//            @Override
//            public Integer execute(WaitForSIgnMapper mapper) {
//                return mapper.updateSignvalByUUID("UUID","");
//            }
//        });
//        return null;
//    }
    public static String clientSocket(String params){
        Socket socket = null;
        try {
            System.out.println("***************1");
            socket = new Socket("119.45.7.196", 7788);
            socket.setKeepAlive(false);
            OutputStream os = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(os);
            writer.write(params);
            //发送
            writer.flush();
            // 读取消息
            InputStream is = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            os.close();
            writer.close();
            is.close();
            reader.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


}
