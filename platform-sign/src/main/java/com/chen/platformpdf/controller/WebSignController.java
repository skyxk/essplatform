package com.chen.platformpdf.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chen.core.base.Constant;
import com.chen.entity.SignatureLog;
import com.chen.platformpdf.dto.SealDto;
import com.chen.platformpdf.service.SealService;
import com.chen.platformpdf.util.WebSign;
import com.chen.service.ILogService;
import com.chen.service.IPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.security.cert.CertificateException;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.io.IOException;
import java.security.GeneralSecurityException;

import static com.chen.core.util.Base64Utils.ESSGetBase64Decode;
import static com.chen.core.util.Base64Utils.ESSGetBase64Encode;
import static com.chen.core.util.CutImageUtil.markImageBySingleText;
import static com.chen.core.util.DateUtils.getDate;
import static com.chen.core.util.DateUtils.getDateTime;
import static com.chen.core.util.StringUtils.getUUID;


@Controller
@RequestMapping(value="/web")
public class WebSignController {


    @Autowired
    private IPersonService personService;
    @Autowired
    private SealService sealService;
    @Autowired
    private ILogService logService;

    /**
     * 网页签章接口（暂时只支持手写签名签章）
     * @return JSON字符串
     */
    @RequestMapping(value ="/sign")
    @ResponseBody
    public String sign(String data) throws Exception {
//     * @param SysId 业务系统id
//     * @param SysPersonID 手签人标识，可以是业务人员id，也可以是系统内人员的身份证号。
//     * @param sPlain 需要签名的明文信息
//     * @param sPlainEncodeType 签名编码格式
        JSONObject jsonObject = JSONObject.parseObject(data);
        String SysId = jsonObject.getString("sysId");
        String SysPersonID = jsonObject.getString("sysPersonID");
        String Plain = jsonObject.getString("plain");
        String PlainEncodeType = jsonObject.getString("plainEncodeType");
        //签章序列号 每次签章的序号，取uuid;保证唯一性
        String signSerialNum = getUUID();
        //根据业务系统ID和人员ID查找人员
        String person_id =personService.findPersonIdBySYS(SysId,SysPersonID);
        //根据人员ID查找印章
        SealDto sealDto = sealService.finSealByPersonId(person_id);
        //获取图像字符节
        byte[] bGif = ESSGetBase64Decode(sealDto.getSeal_img());
        //获取证书字符节
        byte[] bPfx = ESSGetBase64Decode(sealDto.getSeal_pfx());
        //获取证书密码
        String pfxPwd = sealDto.getSeal_pwd();
        //签名方法
        WebSign ws = new WebSign();
        try {
            if(ws.SignData(Plain, PlainEncodeType,bPfx, pfxPwd,bGif)) {
                jsonObject.put("resultType","true");
                jsonObject.put("encodeData",ws.EncodedData);
                jsonObject.put("imageData",ws.ImageData);
                jsonObject.put("imageID",ws.ImageID);
                jsonObject.put("signSerialNum",signSerialNum);
            }else{
                jsonObject.put("resultType","false");
                jsonObject.put("errorCode",ws.ErrorCode);
            }
        } catch (IOException | GeneralSecurityException | CertificateException e) {
            jsonObject.put("resultType","false");
            jsonObject.put("errorCode","0001");
            return jsonObject.toString();
        }
        SignatureLog signatureLog = new SignatureLog();
        signatureLog.setSign_log_id(getUUID());
        signatureLog.setSign_user_id("025user1");
        signatureLog.setSign_time(getDateTime());
        signatureLog.setSeal_id(sealDto.getSealId());
        signatureLog.setBusiness_sys_id(SysId);
        signatureLog.setUnit_id("025unit1");
        signatureLog.setTerminal_type("服务器端");
        signatureLog.setDoc_type("OA网页审批");
        signatureLog.setProduct_type("ESSWEBSIGN V1.0");
        signatureLog.setSafe_hash("sdfsadas");
        signatureLog.setSerial_number(signSerialNum);
        boolean result = logService.addSignLog(signatureLog);
        jsonObject.put("resultType","true");
        jsonObject.put("errorCode","0000");
        return jsonObject.toString();
    }
    /**
     * 网页签章接口（暂时只支持手写签名签章）
     * @return JSON字符串
     */
    @RequestMapping(value ="/verify")
    @ResponseBody
    public String Verify(String data) {
        JSONObject jsonObject = JSONObject.parseObject(data);

        String encodeData = jsonObject.getString("encodeData");
        String imageData = jsonObject.getString("imageData");
        String plainText = jsonObject.getString("plain");
        String encodeType = jsonObject.getString("encodeType");
        int imgType = jsonObject.getInteger("imgType");
        String signSerialNum = jsonObject.getString("signSerialNum");
        String wantEncodeType = jsonObject.getString("wantEncodeType");

        JSONObject result = new JSONObject();
        String sealName ="";
        String gifBase64 ="";
        String sealTime ="";
        String bsUnitName ="";
        String url ="";

        WebSign ws = new WebSign();
        if (!"".equals(encodeData)&&!"".equals(imageData)&&!"".equals(plainText)&&"".equals(signSerialNum)){
            if(ws.VerifyData(plainText, encodeType,encodeData, imageData)) {
                result.put("resultType","true");
                result.put("gifBase64",ws.PictureData);
                result.put("sealTime",ws.SealTime);
            }else{
                try {
                    result.put("resultType","false");
                    result.put("errorCode",ws.ErrorCode);
                    if("".equals(ws.PictureData)){
                        result.put("gifBase64", Constant.errImg);
                    }else{
                        result.put("gifBase64",addMarkErrorText(ws.PictureData));
                    }
                } catch (IOException e) {
                    result.put("resultType","false");
                    result.put("errorCode","10004");
                    result.put("gifBase64",Constant.errImg);
                }
            }

        }

        if(!"".equals(encodeData)&&!"".equals(imageData)&&!"".equals(plainText)&&!"".equals(signSerialNum)){
            //根据签章序列号获取签章日志
            SignatureLog signatureLog = logService.findSignatureLogBySerNum(signSerialNum);

            sealTime = signatureLog.getSign_time();
            String sealId = signatureLog.getSeal_id();
            String businessSysId = signatureLog.getBusiness_sys_id();
            SealDto seal = sealService.findSealById(sealId);
            String signUserName = seal.getSealName();
            sealName = seal.getSealName();
            gifBase64 = seal.getSeal_img();
            bsUnitName = "测试单位";
            result.put("resultType","true");
            result.put("sealName",sealName);
            result.put("sealTime",sealTime);
            result.put("signUserName",signUserName);
            result.put("bsSystemName",bsUnitName);
            if(ws.VerifyData(plainText, encodeType,encodeData, imageData)) {
                result.put("gifBase64",gifBase64);
            }else{
                try {
                    result.put("resultType","false");
                    result.put("errorCode",ws.ErrorCode);
                    if("".equals(ws.PictureData)){
                        result.put("gifBase64", Constant.errImg);
                    }else{
                        result.put("gifBase64",addMarkErrorText(ws.PictureData));
                    }
                } catch (IOException e) {
                    result.put("resultType","false");
                    result.put("errorCode","10004");
                    result.put("gifBase64",Constant.errImg);
                }
            }
        }
        if (imgType ==0){
            return result.toString();
        }else if (imgType == 1){
            return ws.GetImgHtml(signSerialNum,result.getString("gifBase64"),sealName,sealTime,bsUnitName);
        }else{
            return result.toString();
        }

    }
    private String addMarkErrorText(String data) throws IOException {
        byte[] img = markImageBySingleText(ESSGetBase64Decode(data), Color.green,"——————————————",null);
        return ESSGetBase64Encode(img);
    }

}
