package com.chen.platformpdf.controller;

import com.alibaba.fastjson.JSONObject;
import com.chen.entity.pdf.WaitForSign;
import com.chen.platformpdf.service.SignTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.chen.core.util.Base64Utils.ESSGetBase64Decode;
import static com.chen.core.util.Base64Utils.ESSGetBase64Encode;
@Controller
@RequestMapping(value="/client")
public class ClientController {
    @Autowired
    public SignTokenService signTokenService;
    @RequestMapping(value="/COMAPI")
    @ResponseBody
    public String uploadDocument(String data){
        JSONObject paramJs = JSONObject.parseObject(data);
        System.out.println(data);
        JSONObject result = new JSONObject();
        int com =  paramJs.getInteger("COM");
        switch (com){
            case 1 :
                //签章客户端要求查询是否需要盖章的数据
                String UKID = paramJs.getString("UKID");
                WaitForSign waitForSign = signTokenService.getUseSeal(UKID);
                if (waitForSign == null){
                    return "NODATA";
                }
                result.put("COM",3);
                result.put("UUID",waitForSign.getUUID());
                result.put("HASH",waitForSign.getHASH());
                break;
            case 4 :
                //修改签名值
                String UUID = paramJs.getString("UUID");
                String SIGNVAL = paramJs.getString("SIGNVAL");
                boolean res = signTokenService.updateSignval(UUID,SIGNVAL);
                if (res){
                    result.put("Message","success");
                }else{
                    result.put("Message","error");
                }
                break;
            default :
                break;
        }
        System.out.println(result.toString());
        return result.toString();
    }

    @RequestMapping(value="/getSignValue")
    @ResponseBody
    public String getSignValue(String UKID,String sh){
        byte[] hash = ESSGetBase64Decode(sh);
        byte[] signValue = signTokenService.getSignValue(UKID,hash);
        if (signValue == null){
            return "error";
        }
        return ESSGetBase64Encode(signValue);
    }
}
