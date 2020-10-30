package com.chen.core.util.crypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;

public class TestAsn1Decode {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		byte[] b = new byte[72];
		File f = new File("d:/test/guangxi/guangxi/guangxinsign.dat");
		FileInputStream fis = new FileInputStream(f);
		fis.read(b);
		fis.close();
		
		ASN1InputStream input = new ASN1InputStream(b);
		ASN1Primitive p;
		while ((p = input.readObject()) != null) {
			
			ASN1Sequence asn1 = ASN1Sequence.getInstance(p);
			ASN1Integer i1 = ASN1Integer.getInstance(asn1.getObjectAt(0));
			ASN1Integer i2 = ASN1Integer.getInstance(asn1.getObjectAt(1));
			
			byte[] b1 = i1.getValue().toByteArray();
			System.out.println(b1.length);
			
			byte[] b2 = i2.getValue().toByteArray();
			System.out.println(b2.length);
		}
		input.close();

	}

}
