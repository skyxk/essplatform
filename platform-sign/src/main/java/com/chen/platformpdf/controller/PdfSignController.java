package com.chen.platformpdf.controller;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chen.core.bean.ESSPdfPage;
import com.chen.entity.Business_System;
import com.chen.entity.Person;
import com.chen.platformpdf.bean.PdfLocation;
import com.chen.platformpdf.bean.SealBean;
import com.chen.platformpdf.bean.VerifyResult;
import com.chen.platformpdf.dto.SealDto;
import com.chen.platformpdf.service.FileService;
import com.chen.platformpdf.service.PowerService;
import com.chen.platformpdf.service.SealService;
import com.chen.platformpdf.service.SignService;
import com.chen.platformpdf.util.ProcessPDF;
import com.chen.service.IBusinessSysService;
import com.chen.service.IPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static com.chen.core.util.Base64Utils.ESSGetBase64Decode;
import static com.chen.core.util.FileUtils.File2byte;
import static com.chen.core.util.FileUtils.copy;
import static com.chen.platformpdf.util.PDFUtils.convertCoordinate;
import static com.chen.platformpdf.util.ProcessPDF.addPdfTextMarkFull;

@Controller
@RequestMapping(value="/pdf")
public class PdfSignController {

    @Autowired
    private FileService fileService;
    @Autowired
    private SignService signService;
    @Autowired
    private SealService sealService;
    @Autowired
    private IBusinessSysService businessSysService;
    @Autowired
    private IPersonService personService;
    @Autowired
    private PowerService powerService;
    @Value("${myConfig.pdfPath}")
    public String pdfPath ;
    @RequestMapping(value ="/verifyAndShow")
    public ModelAndView show(String data) throws Exception {
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
            List<VerifyResult> verifyResultList =  signService.verifyPdfSign(File2byte(pdfFile));
            modelAndView.addObject("verifyResultList",verifyResultList);
            //返回结果
            modelAndView.setViewName("show");
        }
        return modelAndView;
    }

    @RequestMapping(value ="/verify")
    @ResponseBody
    public JSONArray verify(MultipartFile document) throws Exception {
        List<VerifyResult> verifyResultList =  signService.verifyPdfSign(document.getBytes());
        JSONArray jsonArray = new JSONArray();
        for (VerifyResult verifyResult :verifyResultList){
            JSONObject jsonObject =new JSONObject();
            jsonObject.put("sealName",verifyResult.getSealName());
            jsonObject.put("documentVerify",verifyResult.getCertVerify());
            jsonObject.put("certVerify",verifyResult.getDocumentVerify());
            jsonArray.add(jsonObject);
        }
        return jsonArray;
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
        Person signPer = personService.findPersonBySYS(businessSys,signUser);
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
        try {
            File file = new File(pdfFile1.getAbsolutePath()+"1");
            ProcessPDF.addPdfTextMarkFull(file,signPer.getPerson_name(),0.1f);
            ProcessPDF.addPdfQRCode(file,documentCode,10,10);

        } catch (Exception e) {
            e.printStackTrace();
            result.put("code","0012");
            result.put("msg","添加水印失败");
            return result.toJSONString();
        }
        //遍历印章对象
        //印章信息   图片路径   印章ID
        for(Object js :seals){
            JSONObject aa = (JSONObject)js;
            int iOffsetX  = aa.getInteger("iOffsetX");
            int iOffsetY  = aa.getInteger("iOffsetY");
            int KeyNumber  = aa.getInteger("KeyNumber");
            String sKeyWord  = aa.getString("sKeyWord");
            String signType  = aa.getString("signType");
            SealDto sealDto = null;
            if (aa.containsKey("sealId")) {
                String sealId  = aa.getString("sealId");
                sealDto = sealService.findSealById(sealId);
                if (sealDto ==null){
                    //如果为空可能传入的是业务人员ID
                    sealDto = sealService.findSealBySysPersonId(sealId,businessSys);
                }
            }else if (aa.containsKey("sealType")){
                String sealType  = aa.getString("sealType");
                String unitId = aa.getString("unitId");
                sealDto = sealService.findSealDtoByUnitAndType(unitId,sealType);
            }
            if (sealDto ==null){
                result.put("code","0010");
                result.put("msg","印章不存在");
                return result.toJSONString();
            }
//            if (!powerService.checkPowerForSealByUser(seal,signUser)){
//                //如果此印章不存在授权则跳过
//                continue;
//            }
            int width = convertCoordinate(sealDto.getSeal_w());
            int height = convertCoordinate(sealDto.getSeal_h());
            byte[] imgFile = ESSGetBase64Decode(sealDto.getSeal_img());
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
                        sealDto,os);

            }else if("2".equals(signType)){
                signService.addOverSeal(sealDto,documentCode);
            }
        }
        result.put("code","0000");
        result.put("msg",documentCode);
        return result.toJSONString();
    }

    /**
     * 根据书签定位对pdf进行签章
     * @param data 签章数据
     * @return 返回结果
     */
    @RequestMapping(value ="/signPdfByBookmark")
    @ResponseBody
    public String signPdfByBookmark(String data) throws Exception {
        JSONObject result = new JSONObject();
        //首先确认参数
        JSONObject dataJs = JSONObject.parseObject(data);

        String documentCode = dataJs.getString("documentCode");
        String signUser = dataJs.getString("signUser");
        String businessSys = dataJs.getString("businessSys");
        JSONArray seals =dataJs.getJSONArray("seals");
        //查找签章人员  signUser
        Person signPer = personService.findPersonBySYS(businessSys,signUser);
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
        try {
            File file = new File(pdfFile1.getAbsolutePath()+"1");
            ProcessPDF.addPdfTextMarkFull(file,signPer.getPerson_name(),0.1f);
            ProcessPDF.addPdfQRCode(file,documentCode,10,10);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code","0012");
            result.put("msg","添加水印失败");
            return result.toJSONString();
        }
        //遍历印章对象
        //印章信息   图片路径   印章ID
        for(Object js :seals){
            JSONObject aa = (JSONObject)js;
            int iOffsetX  = aa.getInteger("iOffsetX");
            int iOffsetY  = aa.getInteger("iOffsetY");
            String bookmark  = aa.getString("bookmark");
            String signType  = aa.getString("signType");
            SealDto sealDto = null;
            if (aa.containsKey("sealId")) {
                String sealId  = aa.getString("sealId");
                sealDto = sealService.findSealById(sealId);
                if (sealDto ==null){
                    //如果为空可能传入的是业务人员ID
                    sealDto = sealService.findSealBySysPersonId(sealId,businessSys);
                }
            }else if (aa.containsKey("sealType")){
                String sealType  = aa.getString("sealType");
                String unitId = aa.getString("unitId");
                sealDto = sealService.findSealDtoByUnitAndType(unitId,sealType);
            }
            if (sealDto ==null){
                result.put("code","0010");
                result.put("msg","印章不存在");
                return result.toJSONString();
            }
//            if (!powerService.checkPowerForSealByUser(seal,signUser)){
//                //如果此印章不存在授权则跳过
//                continue;
//            }
            byte[] imgFile = ESSGetBase64Decode(sealDto.getSeal_img());
            int width = convertCoordinate(sealDto.getSeal_w());
            int height = convertCoordinate(sealDto.getSeal_h());
            //签章
            //查找文档
            if("1".equals(signType)){
                File pdfFile = fileService.findPDFFileByDocumentCode(documentCode,2);
                byte[] pdfFileByte = File2byte(pdfFile);
                //定位
                //书签定位
                PdfLocation pdfLocation = signService.findLocationByBookMark(bookmark,pdfFile.getAbsolutePath());
                if (pdfLocation ==null){
                    result.put("code","0007");
                    result.put("msg",bookmark+"书签不存在");
                    return result.toJSONString();
                }
                pdfLocation.setX(pdfLocation.getX()+iOffsetX);
                pdfLocation.setY(pdfLocation.getY()+iOffsetY);
                //开始签章
                FileOutputStream os  = new FileOutputStream(new File(pdfFile.getAbsolutePath()));
                signService.addSeal(pdfFileByte,imgFile,width,height,pdfLocation.getPageNum(),pdfLocation.getX(),pdfLocation.getY(),
                        sealDto,os);
            }else if("2".equals(signType)){
                signService.addOverSeal(sealDto,documentCode);
            }
        }
        result.put("code","0000");
        result.put("msg",documentCode);
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
        Person signPer = personService.findPersonBySYS(businessSys,signUser);
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
        try {
            File file = new File(pdfFile1.getAbsolutePath()+"1");
            ProcessPDF.addPdfTextMarkFull(file,signPer.getPerson_name(),0.1f);
            ProcessPDF.addPdfQRCode(file,documentCode,10,10);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code","0012");
            result.put("msg","添加水印失败");
            return result.toJSONString();
        }
        //遍历印章对象
        //印章信息   图片路径   印章ID
        for(Object js :seals){
            JSONObject aa = (JSONObject)js;
            float X  = aa.getFloat("X");
            float Y  = aa.getFloat("Y");
            int pageNum  = aa.getInteger("pageNum");
            SealDto sealDto = null;
            if (aa.containsKey("sealId")) {
                String sealId  = aa.getString("sealId");
                sealDto = sealService.findSealById(sealId);
                if (sealDto ==null){
                    //如果为空可能传入的是业务人员ID
                    sealDto = sealService.findSealBySysPersonId(sealId,businessSys);
                }
            }else if (aa.containsKey("sealType")){
                String sealType  = aa.getString("sealType");
                String unitId = aa.getString("unitId");
                sealDto = sealService.findSealDtoByUnitAndType(unitId,sealType);
            }
            if (sealDto ==null){
                result.put("code","0010");
                result.put("msg","印章不存在");
                return result.toJSONString();
            }
//            if (!powerService.checkPowerForSealByUser(seal,signUser)){
//                //如果此印章不存在授权则跳过
//                continue;
//            }
            //查找文档
            File pdfFile = fileService.findPDFFileByDocumentCode(documentCode,2);
            byte[] pdfFileByte = File2byte(pdfFile);
            byte[] imgFile = ESSGetBase64Decode(sealDto.getSeal_img());
            int width = convertCoordinate(sealDto.getSeal_w());
            int height = convertCoordinate(sealDto.getSeal_h());
            //定位
            PdfLocation pdfLocation =  signService.findLocationByXY(X,Y, pageNum);
            FileOutputStream os  = new FileOutputStream(new File(pdfFile.getAbsolutePath()));
//          签章
            boolean aa1 =  signService.addSeal(pdfFileByte,imgFile,width,height,pdfLocation.getPageNum(),pdfLocation.getX(),pdfLocation.getY(),
                    sealDto,os);
            if (!aa1){
                result.put("code","0010");
                result.put("msg","签章失败");
                return result.toJSONString();
            }
        }
        result.put("code","0000");
        result.put("msg",documentCode);
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
        Person signPer = personService.findPersonBySYS(businessSys,signUser);
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
        try {
            File file = new File(pdfFile1.getAbsolutePath()+"1");
            ProcessPDF.addPdfTextMarkFull(file,signPer.getPerson_name(),0.1f);
            ProcessPDF.addPdfQRCode(file,documentCode,10,10);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code","0012");
            result.put("msg","添加水印失败");
            return result.toJSONString();
        }
        //遍历印章对象
        //印章信息   图片路径   印章ID
        if(seals.size()>1){
            result.put("code","0004");
            result.put("msg","印章数量有误！");
            return result.toJSONString();
        }
        for(Object js :seals){
            JSONObject aa = (JSONObject)js;
            SealDto sealDto = null;
            if (aa.containsKey("sealId")) {
                String sealId  = aa.getString("sealId");
                sealDto = sealService.findSealById(sealId);
                if (sealDto ==null){
                    //如果为空可能传入的是业务人员ID
                    sealDto = sealService.findSealBySysPersonId(sealId,businessSys);
                }
            }else if (aa.containsKey("sealType")){
                String sealType  = aa.getString("sealType");
                String unitId = aa.getString("unitId");
                sealDto = sealService.findSealDtoByUnitAndType(unitId,sealType);
            }
            if (sealDto ==null){
                result.put("code","0010");
                result.put("msg","印章不存在");
                return result.toJSONString();
            }
            int width = convertCoordinate(sealDto.getSeal_w());
            int height = convertCoordinate(sealDto.getSeal_h());
            //如果不是公章的话，返回不能签章。暂时使用图像长宽判断。
            if (width !=height){
                result.put("code","0011");
                result.put("msg","此印章不支持骑缝章");
                return result.toJSONString();
            }
            signService.addOverSeal(sealDto,documentCode);
        }
        result.put("code","0000");
        result.put("msg",documentCode);
        return result.toJSONString();
    }
}