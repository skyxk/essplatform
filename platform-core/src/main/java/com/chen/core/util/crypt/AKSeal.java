package com.chen.core.util.crypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERUTCTime;
import org.bouncycastle.asn1.DERUTF8String;

public class AKSeal {
	
	public static byte[] Test() throws IOException
	{
		File f = new File("d:/test.gif");
		FileInputStream fis = new FileInputStream(f);
		int iLen = (int)f.length();
		byte[] bPic = new byte[iLen];
		fis.read(bPic);
		fis.close();
		ASN1EncodableVector asn1 = CreatePictureInfo("GIF",bPic,42,42);
		DERSequence der = new DERSequence(asn1);
		return der.getEncoded();
	}
	
	private static ASN1EncodableVector CreatePictureInfo(String sType,byte[] bPicData,int iWidth,int iHeight)
	{
		ASN1EncodableVector v = new ASN1EncodableVector();
		DERIA5String ia5String = new DERIA5String(sType);
		DEROctetString octString = new DEROctetString(bPicData);
		
		v.add(ia5String);
		v.add(octString);
		v.add(new ASN1Integer(iWidth));
		v.add(new ASN1Integer(iHeight));
		
		return v;
		
	}
	
	private static ASN1EncodableVector CreateHeader(int iVersion,String cltID)
	{
		ASN1EncodableVector v = new ASN1EncodableVector();
		v.add(new DERIA5String("ES"));
		v.add(new ASN1Integer(iVersion));
		v.add(new DERIA5String(cltID));
		
		return v;
	}
	
	private static ASN1EncodableVector CreatePropertyInfo(int sealType,String sealName,byte[] bSignerCert,String makeDate,String validateBeginDate,String validateEndDate)
	{
		ASN1EncodableVector v = new ASN1EncodableVector();
		v.add(new ASN1Integer(sealType));
		v.add(new DERUTF8String(sealName));
		ASN1EncodableVector vCert = new ASN1EncodableVector();
		vCert.add(new DEROctetString(bSignerCert));
		v.add(new DERSequence(vCert));
		v.add(new DERUTCTime(makeDate));
		v.add(new DERUTCTime(validateBeginDate));
		v.add(new DERUTCTime(validateEndDate));
		return v;
		
	}
	
	
	/***
	 *@author lijin
	 *@param sealID 	印章ID
	 *@param iVersion	印章版本，固定填写  2
	 *@param cltID		诚利通公司ID
	 *@param iSealType	印章类型，1：公章 2：签名
	 *@param sealName	印章名称
	 *@param bSignerCert 印章绑定的数字证书
	 *@param makeDate   印章制作日期 ，格式为 200324124526Z, 表示2020年3月24日12时45分26秒,以字母Z结尾
	 *@param beginDate  有效期起始日
	 *@param endDate    有效期截止日
	 *@param sPicType   印章图像类型： GIF、BMP、PNG、JPG
	 *@param bPicData   印章图像
	 *@param iPicWidth  印章宽度（毫米）
	 *@param iPicHeight 印章高度
	 *@param bMakerCert 电子印章平台证书
	 *@param sAlgOid    签名算法，固定为 "1.2.156.10197.1.501"
	 *@param pbMakerPfx 印章平台的SM2根PFX
	 *@param sMakerPfxPin SM2根PFX的密码
	 */
	public static byte[] CreateAKSeal(
				String 		sealID,			
				int 		iVersion,		
				String		cltID,			
				int			iSealType,		
				String		sealName,		
				byte[]		bSignerCert,	
				String		makeDate,		
				String		beginDate,		
				String		endDate,		
				String		sPicType,		
				byte[]		bPicData,		
				int			iPicWidth,		
				int			iPicHeight,		
				byte[]		bMakerCert,		
				String 		sAlgOid,		
				String		sPublicKey,		
				String		sPrivateKey
			) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		try
		{
			ASN1EncodableVector Header = CreateHeader(2,cltID);
			ASN1EncodableVector PictureInfo = CreatePictureInfo(sPicType,bPicData,iPicWidth,iPicHeight);
			ASN1EncodableVector PropertyInfo = CreatePropertyInfo(iSealType,sealName,bSignerCert,makeDate,beginDate,endDate);
			
			ASN1EncodableVector sealInfo = new ASN1EncodableVector();
			sealInfo.add(new DERSequence(Header));
			sealInfo.add(new DERIA5String(sealID));
			sealInfo.add(new DERSequence(PropertyInfo));
			sealInfo.add(new DERSequence(PictureInfo));

			
			ASN1EncodableVector signInfo = new ASN1EncodableVector();
			signInfo.add(new DEROctetString(bMakerCert));
			signInfo.add(new ASN1ObjectIdentifier(sAlgOid));
			
			
			ASN1EncodableVector sealForSign = new ASN1EncodableVector();
			sealForSign.add(new DERSequence(sealInfo));
			sealForSign.add(new DEROctetString(bMakerCert));
			sealForSign.add(new ASN1ObjectIdentifier(sAlgOid));
			
			ASN1EncodableVector seal = new ASN1EncodableVector();
			seal.add(new DERSequence(sealInfo));
			seal.add(new DERSequence(signInfo));
			
			DERSequence der = new DERSequence(sealForSign);
			byte[] bForSign = der.getEncoded();

			byte[] bSignVal = SM2Crypto.SignDateForSeal(bMakerCert, bForSign, sPublicKey, sPrivateKey);
			signInfo.add(new DERBitString(bSignVal));
			ASN1EncodableVector sealFinal = new ASN1EncodableVector();
			sealFinal.add(new DERSequence(sealInfo));
			sealFinal.add(new DERSequence(signInfo));
			der = new DERSequence(sealFinal);
			return der.getEncoded();
			
		}catch(IOException e)
		{
			e.printStackTrace();
			return null;
		}	
	}
	
	
	/***
	 *@author lijin
	 *@param sealID 	印章ID
	 *@param iVersion	印章版本，固定填写  2
	 *@param cltID		诚利通公司ID
	 *@param iSealType	印章类型，1：公章 2：签名
	 *@param sealName	印章名称
	 *@param bSignerCert 印章绑定的数字证书
	 *@param makeDate   印章制作日期 ，格式为 200324124526Z, 表示2020年3月24日12时45分26秒,以字母Z结尾
	 *@param beginDate  有效期起始日
	 *@param endDate    有效期截止日
	 *@param sPicType   印章图像类型： GIF、BMP、PNG、JPG
	 *@param bPicData   印章图像
	 *@param iPicWidth  印章宽度（毫米）
	 *@param iPicHeight 印章高度
	 *@param bMakerCert 电子印章平台证书
	 *@param sAlgOid    签名算法，固定为 "1.2.156.10197.1.501"
	 *@param pbMakerPfx 印章平台的SM2根PFX
	 *@param sMakerPfxPin SM2根PFX的密码
	 */
	public static byte[] CreateAKSeal(
				String 		sealID,			
				int 		iVersion,		
				String		cltID,			
				int			iSealType,		
				String		sealName,		
				byte[]		bSignerCert,	
				String		makeDate,		
				String		beginDate,		
				String		endDate,		
				String		sPicType,		
				byte[]		bPicData,		
				int			iPicWidth,		
				int			iPicHeight,		
				byte[]		bMakerCert,		
				String 		sAlgOid,		
				byte[]		bMakerPfx,		
				String		sPfxPin
			)
	{
		try
		{
			ASN1EncodableVector Header = CreateHeader(2,cltID);
			ASN1EncodableVector PictureInfo = CreatePictureInfo(sPicType,bPicData,iPicWidth,iPicHeight);
			ASN1EncodableVector PropertyInfo = CreatePropertyInfo(iSealType,sealName,bSignerCert,makeDate,beginDate,endDate);
			
			ASN1EncodableVector sealInfo = new ASN1EncodableVector();
			sealInfo.add(new DERSequence(Header));
			sealInfo.add(new DERIA5String(sealID));
			sealInfo.add(new DERSequence(PropertyInfo));
			sealInfo.add(new DERSequence(PictureInfo));

			ASN1EncodableVector signInfo = new ASN1EncodableVector();
			signInfo.add(new DEROctetString(bMakerCert));
			signInfo.add(new ASN1ObjectIdentifier(sAlgOid));

			ASN1EncodableVector sealForSign = new ASN1EncodableVector();
			sealForSign.add(new DERSequence(sealInfo));
			sealForSign.add(new DEROctetString(bMakerCert));
			sealForSign.add(new ASN1ObjectIdentifier(sAlgOid));
			
			ASN1EncodableVector seal = new ASN1EncodableVector();
			seal.add(new DERSequence(sealInfo));
			seal.add(new DERSequence(signInfo));
			
			DERSequence der = new DERSequence(sealForSign);
			byte[] bForSign = der.getEncoded();

			byte[] bSignVal = SM2Crypto.SignDateForSeal(bMakerCert, bForSign, bMakerPfx, sPfxPin);
			signInfo.add(new DERBitString(bSignVal));
			ASN1EncodableVector sealFinal = new ASN1EncodableVector();
			sealFinal.add(new DERSequence(sealInfo));
			sealFinal.add(new DERSequence(signInfo));
			der = new DERSequence(sealFinal);
			return der.getEncoded();
			
		}catch(IOException e)
		{
			e.printStackTrace();
			return null;
		}	
	}
	
//	public static void main(String[] args) throws IOException{
//		byte[] b = AKSeal.Test();
//		File f = new File("d:/test/asn1.dat");
//		FileOutputStream fos = new FileOutputStream(f);
//		fos.write(b);
//		fos.close();
//	}

	public static void main(String[] args) throws IOException {
		//用户证书
		File f = new File("d:/test/user.cer");
		FileInputStream fis = new FileInputStream(f);
		int iLen = (int)f.length();
		byte[] bUserCert = new byte[iLen];
		fis.read(bUserCert);
		fis.close();
		
		//颁发者证书
		f = new File("d:/test/sm2root.cer");
		fis = new FileInputStream(f);
		iLen = (int)f.length();
		byte[] bRootCert = new byte[iLen];
		fis.read(bRootCert);
		fis.close();
		
		//颁发者PFX
		f = new File("d:/test/sm2root.pfx");
		fis = new FileInputStream(f);
		iLen = (int)f.length();
		byte[] bRootPfx = new byte[iLen];
		fis.read(bRootPfx);
		fis.close();
		
		//印章图
		f = new File("d:/test/test.gif");
		fis = new FileInputStream(f);
		iLen = (int)f.length();
		byte[] bGif = new byte[iLen];
		fis.read(bGif);
		fis.close();
		
//		//公钥(新增）
//		f = new File("d:/test/sm2rootbcpub.txt");
//		fis = new FileInputStream(f);
//		iLen = (int)f.length();
//		byte[] bTmp = new byte[iLen];
//		fis.read(bTmp);
//		fis.close();
//		String sPublicKey = new String(bTmp);
//		
//		//私钥(新增）
//		f = new File("d:/test/sm2rootbcprv.txt");
//		fis = new FileInputStream(f);
//		iLen = (int)f.length();
//		bTmp = new byte[iLen];
//		fis.read(bTmp);
//		fis.close();
//		String sPrivateKey = new String(bTmp);	
		
		

		
		byte[] bAsn1Seal = AKSeal.CreateAKSeal("245432-f4fds-f2sdfd-1eds2", 2, "BJCLTDZYZPT", 1, "诚利通测试印章", bUserCert, "200324102030Z", "200324102030Z", "210324102030Z", "GIF", bGif, 40, 40, bRootCert, "1.2.156.10197.1.501", bRootPfx, "dc784bea");
		if(bAsn1Seal != null)
		{
			f = new File("d:/test/akseal.dat");
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(bAsn1Seal);
			fos.close();
		}else
			System.out.println("error.................");
	}

}
