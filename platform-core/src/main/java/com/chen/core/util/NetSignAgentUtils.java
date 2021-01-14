package com.chen.core.util;

import cn.com.infosec.netsign.agent.NetSignAgent;
import cn.com.infosec.netsign.agent.NetSignResult;
import cn.com.infosec.netsign.agent.exception.NetSignAgentException;
import cn.com.infosec.netsign.agent.exception.ServerProcessException;
import java.util.HashMap;
import java.util.Map;

import static com.chen.core.util.Base64Utils.ESSGetBase64Decode;
import static com.chen.core.util.Base64Utils.ESSGetBase64Encode;

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
    public static byte[] getSign(String UKID,byte[] sh){
        String url = "http://localhost:8080/sign/client/getSignValue";
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("UKID",UKID);
        paramsMap.put("sh",ESSGetBase64Encode(sh));
        String aa = HttpUtils.post(url,paramsMap);
        if ("error".equals(aa)){
            return null;
        }
        return ESSGetBase64Decode(aa);
    }
}
