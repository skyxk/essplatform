package com.chen.platformpdf.controller;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chen.core.bean.ESSPdfPage;
import com.chen.entity.Business_System;
import com.chen.entity.Person;
import com.chen.entity.Seal;
import com.chen.platformpdf.bean.PdfLocation;
import com.chen.platformpdf.bean.SealBean;
import com.chen.platformpdf.bean.VerifyResult;
import com.chen.platformpdf.service.FileService;
import com.chen.platformpdf.service.PowerService;
import com.chen.platformpdf.service.SignService;
import com.chen.service.IBusinessSysService;
import com.chen.service.IPersonService;
import com.chen.service.ISealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static com.chen.core.util.Base64Utils.ESSGetBase64Decode;
import static com.chen.core.util.FileUtils.File2byte;
import static com.chen.core.util.FileUtils.copy;
@Controller
@RequestMapping(value="/")
public class MainController {

    @Autowired
    private FileService fileService;
    @Autowired
    private SignService signService;
    @Autowired
    private ISealService sealService;
    @Autowired
    private IBusinessSysService businessSysService;
    @Autowired
    private IPersonService personService;
    @Autowired
    private PowerService powerService;
    @Value("${myConfig.pdfPath}")
    public String pdfPath ;
    @RequestMapping(value ="/move")
    public ModelAndView signPdf(String data, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        //取出参数
        JSONObject dataJs = JSONObject.parseObject(data);
        //签章文档编码
        String documentCode = dataJs.getString("documentCode");
        //签章人
        String signUser = dataJs.getString("signUser");
        //业务系统
        String businessSys = dataJs.getString("businessSys");
        //印章列表
        JSONArray seals =dataJs.getJSONArray("seals");
        //查找文档 如果文档不存在。跳转错误页面，提示PDF文件不存在
        File pdfFile = fileService.findPDFFileByDocumentCode(documentCode,1);
        if (pdfFile==null){
            modelAndView.addObject("msg","PDF文件不存在");
            modelAndView.setViewName("error");
            return modelAndView;
        }
        //将文档编码放入session中
        HttpSession session = request.getSession();
        session.setAttribute("documentCode",documentCode);
        //处理PDF
        List<ESSPdfPage> pdfImagesList = fileService.splitPDFToImages(pdfFile);
        modelAndView.addObject("pdfImagesList",pdfImagesList);
        //查找签章人员  signUser
        Person signPer = personService.findPersonById(signUser);
        if (signPer==null){
            modelAndView.addObject("msg","用户不存在");
            modelAndView.setViewName("error");
            return modelAndView;
        }
        modelAndView.addObject("userName",signPer.getPerson_name());
        //查找业务系统名称
        Business_System business_system  = businessSysService.findBusinessSysById(businessSys);
        if (business_system==null){
            modelAndView.addObject("msg","业务系统不存在");
            modelAndView.setViewName("error");
            return modelAndView;
        }
        modelAndView.addObject("businessName",business_system.getB_name());
        List<SealBean> signs = new ArrayList<>();
        if (seals==null||seals.size()==0){
            modelAndView.addObject("msg","缺少印章数据");
            modelAndView.setViewName("error");
            return modelAndView;
        }
        //遍历印章对象
        for(Object js :seals){
            JSONObject aa = (JSONObject)js;
            String unitName  = aa.getString("unitName");
            String sealName  = aa.getString("sealName");
            Seal seal = sealService.findSealByName(unitName,sealName);
            if (!powerService.checkPowerForSealByUser(seal,signUser)){
                //如果此印章不存在授权则跳过
                continue;
            }
            SealBean sealBean = new SealBean();
            sealBean.setSealId(seal.getSeal_id());
            sealBean.setImgBase64(seal.getSealImg().getImg_gif_data());
            if ("ESS001".equals(seal.getSeal_type_id())){
                sealBean.setWidth("80");
                sealBean.setHeight("30");
            }else{
                sealBean.setWidth("100");
                sealBean.setHeight("100");
            }
            signs.add(sealBean);
        }
        modelAndView.addObject("signs",signs);
        //返回结果
        modelAndView.setViewName("showPdf");
        return modelAndView;
    }

    @RequestMapping(value ="/sign")
    @ResponseBody
    public String sign(String data, HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession();
        String documentCode = (String)session.getAttribute("documentCode");
        JSONArray ja = JSONArray.parseArray(data);
        long l1 =System.currentTimeMillis();
        File pdfFile1 = fileService.findPDFFileByDocumentCode(documentCode,1);
        copy(pdfFile1,new File(pdfFile1.getAbsolutePath()+"1"));
        for(Object js :ja){
            File pdfFile = fileService.findPDFFileByDocumentCode(documentCode,2);
            JSONObject aa = (JSONObject)js;
            int page = aa.getInteger("page");
            String sealId = aa.getString("sealId");
            float x = aa.getFloat("x");
            float y = aa.getFloat("y");
            int Type = aa.getInteger("Type");
            System.out.println(x);
            System.out.println(y);
            Seal seal = sealService.findSealById(sealId);
            byte[] pdfFileByte = File2byte(pdfFile);
            byte[] pfxFile = ESSGetBase64Decode(seal.getCertificate().getPfx_base64());
            byte[] imgFile = ESSGetBase64Decode(seal.getSealImg().getImg_gif_data());
            int width ;
            int height;
            if (Type == 3){
                width = 119;
                height = 119;
            }else{
                width = 60;
                height = 25;
            }
            //查找文档
            FileOutputStream os  = new FileOutputStream(new File(pdfFile.getAbsolutePath()));
            signService.addSeal1(pdfFileByte,imgFile,width,height,page,x,y,pfxFile,seal.getCertificate().getCert_psw(),sealId,os);

        }
        long l2 =System.currentTimeMillis();
        System.out.println(l2-l1);
        return "success";
    }

    @RequestMapping(value ="/verify")
    public ModelAndView show(String data) throws Exception {
//        data="{\"documentCode\":\"3ea184fbc8fd4dcbb2a641901df7eef0\",\"type\":\"2\"}";
        ModelAndView modelAndView = new ModelAndView();
//        //首先确认参数
        JSONObject dataJs = JSONObject.parseObject(data);
        String documentCode = dataJs.getString("documentCode");
        String type = dataJs.getString("type");
        //查找文档
        File pdfFile = fileService.findPDFFileByDocumentCode(documentCode,2);
        if (pdfFile==null){
            modelAndView.addObject("msg","PDF文件不存在");
            modelAndView.setViewName("error");
            return modelAndView;
        }
        //处理PDF
        List<ESSPdfPage> pdfImagesList = fileService.splitPDFToImages(pdfFile);
        modelAndView.addObject("pdfImagesList",pdfImagesList);
        //验证PDF内印章完整性
        if ("1".equals(type)){
            //返回结果
            modelAndView.setViewName("show1");
        }else if ("2".equals(type)){
            List<VerifyResult> verifyResultList =  signService.verifyPdfSign(pdfFile);
            modelAndView.addObject("verifyResultList",verifyResultList);
            //返回结果
            modelAndView.setViewName("show");
        }
        return modelAndView;
    }
    /**
     * 根据关键字定位对pdf进行签章
     * @param data 签章数据
     * @return 返回结果
     */
    @RequestMapping(value ="/signPdfByKeyword")
    @ResponseBody
    public String signPdfByKeyword(String data,HttpServletRequest request) throws Exception {
        JSONObject result = new JSONObject();
        //首先确认参数
        JSONObject dataJs = JSONObject.parseObject(data);
        String documentCode = dataJs.getString("documentCode");
        String signUser = dataJs.getString("signUser");
        String businessSys = dataJs.getString("businessSys");
        JSONArray seals =dataJs.getJSONArray("seals");
        //查找签章人员  signUser
        Person signPer = personService.findPersonById(signUser);
        if (signPer==null){
            result.put("code","0001");
            result.put("msg","找不到签章人");
            return result.toJSONString();
        }
        //查找业务系统名称
        Business_System business_system  = businessSysService.findBusinessSysById(businessSys);
        if (business_system==null){
            result.put("code","0002");
            result.put("msg","找不到业务系统");
            return result.toJSONString();
        }
        File pdfFile1 = fileService.findPDFFileByDocumentCode(documentCode,1);
        if (pdfFile1==null){
            result.put("code","0003");
            result.put("msg","找不到PDF文件");
            return result.toJSONString();
        }
        copy(pdfFile1,new File(pdfFile1.getAbsolutePath()+"1"));
        //遍历印章对象
        //印章信息   图片路径   印章ID
        for(Object js :seals){
            JSONObject aa = (JSONObject)js;
            int iOffsetX  = aa.getInteger("iOffsetX");
            int iOffsetY  = aa.getInteger("iOffsetY");
            int KeyNumber  = aa.getInteger("KeyNumber");

            String unitName  = aa.getString("unitName");
            String sealName  = aa.getString("sealName");

//            String sealName  = aa.getString("sealName");
//            String unitName  = aa.getString("unitName");
            String sKeyWord  = aa.getString("sKeyWord");
            String signType  = aa.getString("signType");
            Seal seal = sealService.findSealByName(unitName,sealName);
            if (!powerService.checkPowerForSealByUser(seal,signUser)){
                //如果此印章不存在授权则跳过
                continue;
            }
            byte[] pfxFile = ESSGetBase64Decode(seal.getCertificate().getPfx_base64());
            byte[] imgFile = ESSGetBase64Decode(seal.getSealImg().getImg_gif_data());
            float width = 0;
            float height = 0;
            if ("ESS001".equals(seal.getSeal_type_id())){
                width=60;
                height=25;
            }else{
                width=119;
                height=119;
            }
            //签章
            //查找文档
            if("1".equals(signType)){
                File pdfFile = fileService.findPDFFileByDocumentCode(documentCode,2);
                byte[] pdfFileByte = File2byte(pdfFile);
                //定位
                PdfLocation pdfLocation =  signService.findLocationByKeyword(sKeyWord,pdfFile.getAbsolutePath(), KeyNumber);
                if (pdfLocation ==null){
                    result.put("code","0007");
                    result.put("msg",sKeyWord+"关键字不存在");
                    return result.toJSONString();
                }
                pdfLocation.setX(pdfLocation.getX()+iOffsetX);
                pdfLocation.setY(pdfLocation.getY()+iOffsetY);
                //开始签章
                FileOutputStream os  = new FileOutputStream(new File(pdfFile.getAbsolutePath()));
                signService.addSeal(pdfFileByte,imgFile,width,height,pdfLocation.getPageNum(),pdfLocation.getX(),pdfLocation.getY(),
                        pfxFile,seal.getCertificate().getCert_psw(),seal.getSeal_id(),os);
            }else if("2".equals(signType)){
                signService.addOverSeal(imgFile,pfxFile,seal.getCertificate().getCert_psw(),seal.getSeal_id(),width,height,documentCode);
            }
        }
        result.put("code","0000");
        result.put("msg","签章成功");
        return result.toJSONString();
    }

    /**
     * 根据关键字定位对pdf进行签章
     * @param data 签章数据
     * @return 返回结果
     */
    @RequestMapping(value ="/signPdfByXY")
    @ResponseBody
    public String signPdfByXY(String data,HttpServletRequest request) throws Exception {
        JSONObject result = new JSONObject();
        //首先确认参数
        JSONObject dataJs = JSONObject.parseObject(data);
        String documentCode = dataJs.getString("documentCode");
        String signUser = dataJs.getString("signUser");
        String businessSys = dataJs.getString("businessSys");
        JSONArray seals =dataJs.getJSONArray("seals");
        //将文档编码放入session中
        HttpSession session = request.getSession();
        session.setAttribute("documentCode",documentCode);
        //查找签章人员  signUser
        Person signPer = personService.findPersonById(signUser);
        if (signPer==null){
            result.put("code","0001");
            result.put("msg","找不到签章人");
            return result.toJSONString();
        }
        //查找业务系统名称
        Business_System business_system  = businessSysService.findBusinessSysById(businessSys);
        if (business_system==null){
            result.put("code","0002");
            result.put("msg","找不到业务系统");
            return result.toJSONString();
        }
        File pdfFile1 = fileService.findPDFFileByDocumentCode(documentCode,1);
        if (pdfFile1==null){
            result.put("code","0003");
            result.put("msg","找不到PDF文件");
            return result.toJSONString();
        }
        copy(pdfFile1,new File(pdfFile1.getAbsolutePath()+"1"));
        //遍历印章对象
        //印章信息   图片路径   印章ID
        for(Object js :seals){
            JSONObject aa = (JSONObject)js;
            float X  = aa.getFloat("X");
            float Y  = aa.getFloat("Y");
            int pageNum  = aa.getInteger("pageNum");

            String unitName  = aa.getString("unitName");
            String sealName  = aa.getString("sealName");

//            String sealName  = aa.getString("sealName");
//            String unitName  = aa.getString("unitName");
            Seal seal = sealService.findSealByName(unitName,sealName);
            if (!powerService.checkPowerForSealByUser(seal,signUser)){
                //如果此印章不存在授权则跳过
                continue;
            }
            //查找文档
            File pdfFile = fileService.findPDFFileByDocumentCode(documentCode,2);
            byte[] pdfFileByte = File2byte(pdfFile);
            byte[] pfxFile = ESSGetBase64Decode(seal.getCertificate().getPfx_base64());
            byte[] imgFile = ESSGetBase64Decode(seal.getSealImg().getImg_gif_data());
            float width = 0;
            float height = 0;
            if ("ESS001".equals(seal.getSeal_type_id())){
                width=60;
                height=25;
            }else{
                width=119;
                height=119;
            }
            //定位
            PdfLocation pdfLocation =  signService.findLocationByXY(X,Y, pageNum);
            FileOutputStream os  = new FileOutputStream(new File(pdfFile.getAbsolutePath()));
//            签章
            signService.addSeal(pdfFileByte,imgFile,width,height,pdfLocation.getPageNum(),pdfLocation.getX(),pdfLocation.getY(),
                    pfxFile,seal.getCertificate().getCert_psw(),seal.getSeal_id(),os);
        }
        result.put("code","0000");
        result.put("msg","签章成功");
        return result.toJSONString();
    }
    /**
     * 根据关键字定位对pdf进行签章
     * @param data 签章数据
     * @return 返回结果
     */
    @RequestMapping(value ="/signPdfByOver")
    @ResponseBody
    public String signPdfByOver(String data,HttpServletRequest request) throws Exception {
        JSONObject result = new JSONObject();
        //首先确认参数
        JSONObject dataJs = JSONObject.parseObject(data);
        String documentCode = dataJs.getString("documentCode");
        String signUser = dataJs.getString("signUser");
        String businessSys = dataJs.getString("businessSys");
        JSONArray seals =dataJs.getJSONArray("seals");
        //将文档编码放入session中
        HttpSession session = request.getSession();
        session.setAttribute("documentCode",documentCode);
        //查找签章人员  signUser
        Person signPer = personService.findPersonById(signUser);
        if (signPer==null){
            result.put("code","0001");
            result.put("msg","找不到签章人");
            return result.toJSONString();
        }
        //查找业务系统名称
        Business_System business_system  = businessSysService.findBusinessSysById(businessSys);
        if (business_system==null){
            result.put("code","0002");
            result.put("msg","找不到业务系统");
            return result.toJSONString();
        }
        File pdfFile1 = fileService.findPDFFileByDocumentCode(documentCode,1);
        if (pdfFile1==null){
            result.put("code","0003");
            result.put("msg","找不到PDF文件");
            return result.toJSONString();
        }
        copy(pdfFile1,new File(pdfFile1.getAbsolutePath()+"1"));
        //遍历印章对象
        //印章信息   图片路径   印章ID
        if(seals.size()>1){
            result.put("code","0004");
            result.put("msg","印章数量有误！");
            return result.toJSONString();
        }
        for(Object js :seals){
            JSONObject aa = (JSONObject)js;
            String unitName  = aa.getString("unitName");
            String sealName  = aa.getString("sealName");
//            String sealName  = aa.getString("sealName");
//            String unitName  = aa.getString("unitName");
            Seal seal = sealService.findSealByName(unitName,sealName);
            if (!powerService.checkPowerForSealByUser(seal,signUser)){
                //如果此印章不存在授权则跳过
                continue;
            }
            float width = 0;
            float height = 0;
            if ("ESS001".equals(seal.getSeal_type_id())){
                width=60;
                height=25;
            }else{
                width=126;
                height=126;
            }
            byte[] pfxFile = ESSGetBase64Decode(seal.getCertificate().getPfx_base64());
            byte[] imgFile = ESSGetBase64Decode(seal.getSealImg().getImg_gif_data());
            signService.addOverSeal(imgFile,pfxFile,seal.getCertificate().getCert_psw(),seal.getSeal_id(),width,height,documentCode);
        }
        result.put("code","0000");
        result.put("msg","签章成功");
        return result.toJSONString();
    }



    @RequestMapping(value ="/move_demo")
    public ModelAndView signPdf_demo(String data, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        //取出参数
        JSONObject dataJs = JSONObject.parseObject(data);
        //签章文档编码
        String documentCode = dataJs.getString("documentCode");
        //签章人
        String signUser = dataJs.getString("signUser");
        //业务系统
        String businessSys = dataJs.getString("businessSys");
        //印章列表
        JSONArray seals =dataJs.getJSONArray("seals");
        //查找文档 如果文档不存在。跳转错误页面，提示PDF文件不存在
        File pdfFile = fileService.findPDFFileByDocumentCode(documentCode,1);
        if (pdfFile==null){
            modelAndView.addObject("msg","PDF文件不存在");
            modelAndView.setViewName("error");
            return modelAndView;
        }
        //将文档编码放入session中
        HttpSession session = request.getSession();
        session.setAttribute("documentCode",documentCode);
        //处理PDF
        List<ESSPdfPage> pdfImagesList = fileService.splitPDFToImages(pdfFile);
        modelAndView.addObject("pdfImagesList",pdfImagesList);
        //查找签章人员  signUser
        Person signPer = personService.findPersonById(signUser);
        if (signPer==null){
            modelAndView.addObject("msg","用户不存在");
            modelAndView.setViewName("error");
            return modelAndView;
        }
        modelAndView.addObject("userName",signPer.getPerson_name());
        //查找业务系统名称
        Business_System business_system  = businessSysService.findBusinessSysById(businessSys);
        if (business_system==null){
            modelAndView.addObject("msg","业务系统不存在");
            modelAndView.setViewName("error");
            return modelAndView;
        }
        modelAndView.addObject("businessName",business_system.getB_name());
        List<SealBean> signs = new ArrayList<>();
        if (seals==null||seals.size()==0){
            modelAndView.addObject("msg","缺少印章数据");
            modelAndView.setViewName("error");
            return modelAndView;
        }
        //遍历印章对象
        for(Object js :seals){
            JSONObject aa = (JSONObject)js;
            String unitName  = aa.getString("unitName");
            String sealName  = aa.getString("sealName");
            Seal seal = sealService.findSealByName(unitName,sealName);
            if (!powerService.checkPowerForSealByUser(seal,signUser)){
                //如果此印章不存在授权则跳过
                continue;
            }
            SealBean sealBean = new SealBean();
            sealBean.setSealId(seal.getSeal_id());
            sealBean.setImgBase64(seal.getSealImg().getImg_gif_data());
            if ("ESS001".equals(seal.getSeal_type_id())){
                sealBean.setWidth("80");
                sealBean.setHeight("30");
            }else{
                sealBean.setWidth("100");
                sealBean.setHeight("100");
            }
            signs.add(sealBean);
        }
        modelAndView.addObject("signs",signs);
        modelAndView.addObject("documentCode",documentCode);
        //返回结果
        modelAndView.setViewName("showPdf_demo");
        return modelAndView;
    }

    @RequestMapping(value ="/sign_demo")
    @ResponseBody
    public String sign_demo(String data, HttpServletRequest request) throws Exception {
        System.out.println("***************1");
        HttpSession session = request.getSession();
        String documentCode = (String)session.getAttribute("documentCode");
        JSONArray ja = JSONArray.parseArray(data);
        File pdfFile1 = fileService.findPDFFileByDocumentCode(documentCode,1);
        copy(pdfFile1,new File(pdfFile1.getAbsolutePath()+"1"));
        for(Object js :ja){
            File pdfFile = fileService.findPDFFileByDocumentCode(documentCode,2);
            JSONObject aa = (JSONObject)js;
            int page = aa.getInteger("page");
            String sealId = aa.getString("sealId");
            float x = aa.getFloat("x");
            float y = aa.getFloat("y");
            int Type = aa.getInteger("Type");
            String cert = aa.getString("CERT");
            Seal seal = sealService.findSealById(sealId);
            byte[] pdfFileByte = File2byte(pdfFile);
//            byte[] pfxFile = ESSGetBase64Decode(seal.getCertificate().getPfx_base64());
            byte[] imgFile = ESSGetBase64Decode(seal.getSealImg().getImg_gif_data());
            int width ;
            int height;
            if (Type == 3){
                width = 119;
                height = 119;
            }else{
                width = 60;
                height = 25;
            }
            //查找文档
            FileOutputStream os  = new FileOutputStream(new File(pdfFile.getAbsolutePath()));
            System.out.println("***************2");
            signService.addSeal_UK(pdfFileByte,imgFile,width,height,1,x,y,
                    cert,sealId,os,documentCode);
        }
        return "success";
    }
}