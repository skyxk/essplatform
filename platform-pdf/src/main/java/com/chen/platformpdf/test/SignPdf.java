package com.chen.platformpdf.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.HashMap;
import java.util.UUID;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.security.BouncyCastleDigest;
import com.itextpdf.text.pdf.security.DigestAlgorithms;
import com.itextpdf.text.pdf.security.ExternalDigest;
import com.itextpdf.text.pdf.security.ExternalSignature;
import com.itextpdf.text.pdf.security.MakeSignature;
import com.itextpdf.text.pdf.security.PrivateKeySignature;
import com.itextpdf.text.pdf.security.MakeSignature.CryptoStandard;

public class SignPdf {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try
		{
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
//			Field digestNamesField = DigestAlgorithms.class.getDeclaredField("digestNames");
//			digestNamesField.setAccessible(true);
//			HashMap<String,String> digestNames = (HashMap<String,String>)digestNamesField.get(null);
//			digestNames.put("1.2.156.10197.1.401", "SM3");
			
			PdfReader reader = new PdfReader("d:/test/test.pdf");
			Image pic = Image.getInstance("d:/test/test2.gif");
			KeyStore ks = KeyStore.getInstance("PKCS12","BC");
//			InputStream sbs = new FileInputStream("d:/test/rsauser.pfx");
//			ks.load(sbs, "111111".toCharArray());
			
			InputStream sbs = new FileInputStream("d:/test/guangxizhijian.pfx");
			ks.load(sbs, "111111".toCharArray());
			
			
			String alias = (String) ks.aliases().nextElement();
			PrivateKey pk = (PrivateKey) ks.getKey(alias, "111111".toCharArray());
			//CertificateFactory cf = CertificateFactory.getInstance("X.509");
			Certificate[] chain = ks.getCertificateChain(alias);

			
			File file = new File("d:/test/signed.pdf");
			FileOutputStream os = new FileOutputStream(file);
			PdfStamper stamper = PdfStamper.createSignature(reader, os, '\u0000', null, true);
			PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
			appearance.setImage(pic);
			String uuid = UUID.randomUUID().toString();
			// 左下角为原点
			appearance.setVisibleSignature(new Rectangle(50, 50, 160, 160), 1, "ESSPDFServerSign" + uuid);
			
			appearance.setLayer2Text("");
			ExternalSignature es = new PrivateKeySignature(pk, "SM3", "BC");
			ExternalDigest digest = new BouncyCastleDigest();
			
			
			MakeSignature.signDetached(appearance, digest, es, chain, null, null, null, 0, CryptoStandard.CMS);
			os.close();
			stamper.close();
			sbs.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
