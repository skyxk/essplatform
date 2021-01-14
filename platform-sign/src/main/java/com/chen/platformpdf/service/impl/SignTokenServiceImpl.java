package com.chen.platformpdf.service.impl;

import com.chen.core.util.StringUtils;
import com.chen.dao.WaitForSIgnMapper;
import com.chen.entity.pdf.WaitForSign;
import com.chen.platformpdf.service.SignTokenService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.chen.core.util.Base64Utils.ESSGetBase64Decode;
import static com.chen.core.util.Base64Utils.ESSGetBase64Encode;
import static com.chen.core.util.DateUtils.getDateTime;
import static com.chen.core.util.StringUtils.getUUID;
import static java.lang.Thread.sleep;

@Service
public class SignTokenServiceImpl implements SignTokenService {

    static private final Log log = LogFactory.getLog(SignTokenServiceImpl.class);
    @Autowired
    private WaitForSIgnMapper waitForSIgnMapper;
    @Override
    public WaitForSign getUseSeal(String UKID) {
        try {
            //查询数据
            WaitForSign waitForSign = waitForSIgnMapper.queryByUkid(UKID);
            if (waitForSign !=null){
                updateSignvalState(waitForSign.getUUID(),"1") ;
            }else {
                return null;
            }
            String returnStr =null;
            //删除10分钟前的数据
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long oldtime = new Date().getTime() - 600000;
            String formatoldTime = sf.format(new Date(oldtime));
            List<WaitForSign> waitForSigns = waitForSIgnMapper.getAllOldTime(formatoldTime);
            //进行与当前时间相减 当差值超过十分钟时 进行数据库删除
            if (waitForSigns!=null&&waitForSigns.size()>0){
                for (WaitForSign waitForSign1 : waitForSigns) {
                    //进行删除
                    Integer resCode = waitForSIgnMapper.delByUUID(waitForSign1.getUUID());
                }
            }
            return waitForSign;
        } catch (Exception e) {
            e.printStackTrace();
            log.info(e);
            return null;
        }
    }
    @Override
    public boolean updateSignval(String UUID,String SIGNVAL) {
        Integer resCode = waitForSIgnMapper.updateSignvalByUUID(UUID,SIGNVAL);
        updateSignvalState(UUID,"2");
        if (resCode == 1){
            return true;
        }
        return false;
    }

    public boolean updateSignvalState(String UUID,String state) {
        Integer resCode = waitForSIgnMapper.updateSignvalState(UUID,state);
        if (resCode == 1){
            return true;
        }
        return false;
    }
    @Override
    public boolean addWaitForSign(WaitForSign waitForSign){
        int result = waitForSIgnMapper.addWaitForSign(waitForSign);
        if (result==1){
            return true;
        }
        return false;
    }
    @Override
    public WaitForSign findWaitForSign(String wid){
        WaitForSign result = waitForSIgnMapper.findWaitForSign(wid);
        if (result!=null){
            return result;
        }
        return null;
    }
    @Override
    public byte[] getSignValue(String UKID,byte[] sh){
        WaitForSign waitForSign = new WaitForSign();
        String uuid = getUUID();
        waitForSign.setUUID(uuid);
        waitForSign.setUKID(UKID);
        waitForSign.setHASH(ESSGetBase64Encode(sh));
        waitForSign.setNOWSTATUS(0);
        waitForSign.setSUBTIME(getDateTime());
        //插入一条
        int result = waitForSIgnMapper.addWaitForSign(waitForSign);
        if (result==1){
            //轮询获取签名值
            long beginTime = System.currentTimeMillis();//开始时bai间du
            long overTime = 30 * 1000;//运行时间
            for(long i = 0; i >= 0; i++) {
                long nowTime = System.currentTimeMillis();
                try {
                    sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                WaitForSign result1 = waitForSIgnMapper.findWaitForSign(uuid);
                if (result1 != null){
                    return ESSGetBase64Decode(result1.getSIGNVAL());
                }
                if((nowTime - beginTime) > overTime) break;
            }
        }
        return null;
    }

}
