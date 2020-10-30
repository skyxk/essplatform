package com.chen.core.util.crypt;

import cn.com.infosec.netsign.agent.exception.NetSignAgentException;
import cn.com.infosec.netsign.der.util.DERSegment;
import cn.com.infosec.util.Base64;

public class FormatSM2Signature {
	
	public static void main(String[] args) {
		try {
			byte[] signedData_afterf = formatSignedMsg( Base64.decode( "MEUCIGeKaJ2hIW8dL0heVP+7QQlymw/Nq0+JO4ZTuCJugF1iAiEAsOQ6kiDoDJOG0XcY9U1gC1f1c6xou8tyjEvVwtiG2K4=" ) );
			System.out.println( "after fomat signed data: " + Base64.encode(signedData_afterf)  );
		}catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static byte[] formatSignedMsg( byte[] signed ) throws NetSignAgentException {
		if( signed.length == 64 )
			return signed;
		while ( signed[ 0 ] == 0 ) {
			byte[] tmp = new byte[ signed.length - 1 ];
			System.arraycopy( signed , 1 , tmp , 0 , tmp.length );
			signed = tmp;
		}

		/**
		 * �ж�ǰ4���ֽڣ���ֹ30��ͷ�Ĵ���������ڴ�������
		 *
		 */
		if( signed[ 0 ] != 0x30 ) {
			throw new NetSignAgentException( "Bad signature struction t1" );
		}
		if( signed[1] < 0 || signed[1] > 75 ){
			throw new NetSignAgentException( "Bad signature struction l1" );
		}
		if( signed[2] != 2 ){
			throw new NetSignAgentException( "Bad signature struction t2" );
		}
		if( signed[3] < 0 || signed[3] > 37 ){
			throw new NetSignAgentException( "Bad signature struction l2" );
		}

		byte[] signedf = new byte[ 64 ];
		try {
			DERSegment ds = new DERSegment( signed );
			ds = ds.getInnerDERSegment();
			byte[] tmp = ds.nextDERSegment().getInnerData();
			if( tmp.length >= 32 )
				System.arraycopy( tmp , tmp.length - 32 , signedf , 0 , 32 );
			else
				System.arraycopy( tmp , 0 , signedf , 32 - tmp.length , tmp.length );
			tmp = ds.nextDERSegment().getInnerData();
			if( tmp.length >= 32 )
				System.arraycopy( tmp , tmp.length - 32 , signedf , 32 , 32 );
			else
				System.arraycopy( tmp , 0 , signedf , 64 - tmp.length , tmp.length );
			return signedf;
		} catch ( Exception e ) {
			e.printStackTrace();;
			return null;
		}
	}

}
