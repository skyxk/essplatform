package com.chen.core.util.crypt;

public class CertStruct {
	public byte[] bPfx;		//PFX证书内容
	public byte[] bCert;	//CER证书内容
	public String sPin;			//PFX证书密码
	String sPrivateKey;		//BC格式的私钥
	String sPublicKey;		//BC格式的公钥
}
