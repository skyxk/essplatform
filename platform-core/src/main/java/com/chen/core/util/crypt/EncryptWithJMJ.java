package com.chen.core.util.crypt;

import cn.com.infosec.netsign.agent.PBCAgent2G;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
public class EncryptWithJMJ {
	public static void main(String[] args) {
		String sPlain = "ssssssssss";
		String ip = "192.168.1.99";
		int port = 50005;
		String sPwd = "11111111";
		String result = EncryptIt(sPlain,ip,port,sPwd);
		System.out.println(result);
		String result_dec = DecryptIt(result,ip,port,sPwd);
		System.out.println(result_dec);
	}

	public static String EncryptIt(String sPlain,String sIP,int iPort,String sPwd){
		PBCAgent2G agent = new PBCAgent2G();
		int iRet = agent.getReturnCode();
		if(iRet < 0) {
			return "";
		}
		if( agent.openSignServer(sIP,iPort,sPwd) == false )
			return "";
		try {
			int iPlain = sPlain.getBytes("utf8").length;
			iPlain = ((iPlain / 32) + 1) * 32;
			byte[] bPlain = new byte[iPlain];
			for(int i=0;i<iPlain;i++)
				bPlain[i] = '~';
			
			byte[] btmp = sPlain.getBytes(StandardCharsets.UTF_8);
			System.arraycopy(btmp, 0, bPlain, 0, btmp.length);
			
			byte[] bEnc = agent.symmEncrypt(bPlain, "ESSDB", "/ECB/NoPadding");
			if(bEnc == null)
			{
				agent.closeSignServer();
				return "";
			}else{
				BASE64Encoder b64 = new BASE64Encoder();
				String s = b64.encode(bEnc);
				s = "JMJ_"+s;
				return s;
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			agent.closeSignServer();
			return "";
		}
	}
	
	public static String DecryptIt(String sEncString,String sIP,int iPort,String sPwd){
		PBCAgent2G agent = new PBCAgent2G();
		int iRet = agent.getReturnCode();
		if(iRet < 0) {
			return "";
		}
		if( agent.openSignServer(sIP,iPort,sPwd) == false)
			return "";
		BASE64Decoder d64 = new BASE64Decoder();
		byte[] bEnc = null;
		try {
			bEnc = d64.decodeBuffer(sEncString.substring(4));
			byte[] bPlain = agent.symmDecrypt(bEnc, "ESSDB", "/ECB/NoPadding");
			String s = new String(bPlain, StandardCharsets.UTF_8);
			agent.closeSignServer();
			int i = s.indexOf("~");
			if(i > 0)
				s = s.substring(0, i);
//			if (s.contains("国家")){
//				readBin2Image(bPlain,"/data/gj.aaa");
//			}
			return s;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			agent.closeSignServer();
			return "";
		}
	}
}
