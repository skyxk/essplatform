package com.chen.core.util.crypt;

import cn.com.infosec.netsign.agent.PBCAgent2G;
import cn.com.infosec.netsign.agent.exception.NetSignAgentException;
import sun.misc.BASE64Decoder;
import java.io.IOException;

import static com.chen.core.util.Base64Utils.ESSGetBase64Encode;
public class CypherUtil {

    public static void main(String[] args) {

        byte[] sPlain = "ssssssssss".getBytes();
        String ip = "192.168.1.99";
        int port = 50005;
        String sPwd = "11111111";
        String sDN = "CN=黑龙江烟草专卖局OFD专版专用章";
        SignInfo signInfo = signValueG(sPlain,ip,port,sPwd,sDN);
        System.out.println(signInfo.getSignAlgorithm());
        System.out.println(signInfo.getSignValue());
    }
    /**
     * 国家政务平台签名函数
     * @param data 数据
     * @return 返回值
     */
    public static SignInfo signValueG(byte[] data,String sIP,int iPort,String sPwd,String sDN){
        //方法主体
        SignInfo signInfo = new SignInfo();
        signInfo.setSignAlgorithm("1.2.156.10197.1.501");
        PBCAgent2G agent = new PBCAgent2G();
        int iRet = agent.getReturnCode();
        if(iRet < 0)
            return null;
        agent.openSignServer(sIP,iPort,sPwd);
        iRet = agent.getReturnCode();
        if(iRet < 0) {
            System.out.println("sign fail");
            return null;
        }
        byte[] bSignVal = null;
        try {
            String netSignVal =  agent.rawSign(data, sDN, "SM3"); //agent.ra.rawSignature(bMsg,sDN,"SM3",false);
            agent.closeSignServer();
//            byte[] b = netSignVal.getByteArrayResult(NetSignResult.SIGN_TEXT);
            BASE64Decoder d64 = new BASE64Decoder();//			BASE64Dec
            bSignVal = FormatSM2Signature.formatSignedMsg(d64.decodeBuffer(netSignVal));
            signInfo.setSignValue(ESSGetBase64Encode(bSignVal));
            return signInfo;
        } catch (NetSignAgentException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * CA平台签名函数
     * @param data 数据
     * @return 返回值
     */
    public static SignInfo signValueCA(byte[] data){

        //方法主体
        SignInfo signInfo = new SignInfo();
        signInfo.setSignAlgorithm("1.2.156.10197.1.501");
        PBCAgent2G agent = new PBCAgent2G();
        int iRet = agent.getReturnCode();
        if(iRet < 0)
            return null;
        String sIP = "10.1.8.84";
        int iPort = 50010;
        String sPwd = "11111111";
        String sDN = "CN=中国烟草总公司电子签章系统,O=中国烟草总公司,ST=北京市,C=CN";

        agent.openSignServer(sIP,iPort,sPwd);
        iRet = agent.getReturnCode();
        if(iRet < 0)
            return null;

        byte[] bSignVal = null;
        try {
            String netSignVal =  agent.rawSign(data, "CN=中国烟草总公司电子签章系统,O=中国烟草总公司,ST=北京市,C=CN", "SM3"); //agent.ra.rawSignature(bMsg,sDN,"SM3",false);
            agent.closeSignServer();
            BASE64Decoder d64 = new BASE64Decoder();//			BASE64Dec
            bSignVal = FormatSM2Signature.formatSignedMsg(d64.decodeBuffer(netSignVal));
            signInfo.setSignValue(ESSGetBase64Encode(bSignVal));
            return signInfo;
        } catch (NetSignAgentException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * CA平台签名函数
     * @param data 数据
     * @return 返回值
     */
    public static byte[] signValueAsn1(byte[] data){

        //方法主体
        PBCAgent2G agent = new PBCAgent2G();
        int iRet = agent.getReturnCode();
        if(iRet < 0)
            return null;
        String sIP = "10.1.8.84";
        int iPort = 50010;
        String sPwd = "11111111";
        String sDN = "CN=中国烟草总公司电子签章系统,O=中国烟草总公司,ST=北京市,C=CN";
//        System.out.println("ip port password  .....................");

        agent.openSignServer(sIP,iPort,sPwd);
        iRet = agent.getReturnCode();
        if(iRet < 0)
            return null;
        byte[] bSignVal = null;
        try {
            String netSignVal =  agent.rawSign(data, "CN=中国烟草总公司电子签章系统,O=中国烟草总公司,ST=北京市,C=CN", "SM3"); //agent.ra.rawSignature(bMsg,sDN,"SM3",false);
            BASE64Decoder d64 = new BASE64Decoder();//			BASE64Dec
            bSignVal = d64.decodeBuffer(netSignVal);
            agent.closeSignServer();
            return bSignVal;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * CA平台签名函数
     * @param data 数据
     * @return 返回值
     */
    public static byte[] signValue(byte[] data){

        //方法主体
        PBCAgent2G agent = new PBCAgent2G();
        int iRet = agent.getReturnCode();
        if(iRet < 0)
            return null;
        String sIP = "10.1.8.84";
        int iPort = 50010;
        String sPwd = "11111111";
        String sDN = "CN=中国烟草总公司电子签章系统,O=中国烟草总公司,ST=北京市,C=CN";
        agent.openSignServer(sIP,iPort,sPwd);
        iRet = agent.getReturnCode();
        if(iRet < 0)
            return null;

        byte[] bSignVal = null;
        try {
            String netSignVal =  agent.rawSign(data, "CN=中国烟草总公司电子签章系统,O=中国烟草总公司,ST=北京市,C=CN", "SM3"); //agent.ra.rawSignature(bMsg,sDN,"SM3",false);

            BASE64Decoder d64 = new BASE64Decoder();//			BASE64Dec
            bSignVal = FormatSM2Signature.formatSignedMsg(d64.decodeBuffer(netSignVal));

            agent.closeSignServer();

            return bSignVal;
        } catch (NetSignAgentException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
