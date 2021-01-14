package com.chen.platformpdf.service;


import com.chen.entity.pdf.WaitForSign;

public interface SignTokenService {


    WaitForSign getUseSeal(String resq);


    boolean updateSignval(String UUID,String SIGNVAL);

    boolean addWaitForSign(WaitForSign waitForSign);

    WaitForSign findWaitForSign(String wid);

    byte[] getSignValue(String UKID,byte[] sh);
}
