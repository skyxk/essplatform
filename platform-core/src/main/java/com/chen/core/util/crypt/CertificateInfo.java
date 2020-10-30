package com.chen.core.util.crypt;

public class CertificateInfo {
	//证书序号
	public String sCertNo;
	//证书签名算法
	public String sSignAlgName;
	//证书颁发者信息（也就是证书DN）
	public String sIssuerInfo;
	//证书使用者信息
	public String sUserInfo;
	//证书有效期起始
	public String sTimeBegin;
	//证书有效期截至
	public String sTimeEnd;
}
