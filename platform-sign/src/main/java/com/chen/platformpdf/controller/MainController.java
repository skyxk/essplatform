package com.chen.platformpdf.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chen.core.bean.ESSPdfPage;
import com.chen.entity.Business_System;
import com.chen.entity.Person;
import com.chen.platformpdf.bean.SealBean;
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
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.chen.core.util.Base64Utils.ESSGetBase64Decode;
import static com.chen.core.util.FileUtils.File2byte;
import static com.chen.core.util.FileUtils.copy;
import static com.chen.platformpdf.util.PDFUtils.convertCoordinate;

@Controller
@RequestMapping(value="/")
public class MainController {

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
    public String pdfPath;

    @RequestMapping(value ="/getSealByUidAndType")
    @ResponseBody
    public String getSealByUidAndType(String data,HttpServletRequest request) {
        //取出参数
        JSONObject dataJs = JSONObject.parseObject(data);
        //单位ID
        String unitId = dataJs.getString("unitId");
        //印章类型
        String sealType = dataJs.getString("sealType");
        //根据单位和部门查找印章列表
        List<SealDto> sealList = sealService.getSealByUidAndType(unitId,sealType);
        JSONArray jsonArray = new JSONArray();
        for (SealDto sealDto :sealList){
            JSONObject jsonObject = new JSONObject();
            String sealName = sealDto.getSealName();
            String sealId = sealDto.getSealId();
            jsonObject.put("sealName",sealName);
            jsonObject.put("sealId",sealId);
            jsonArray.add(jsonObject);
        }
        return jsonArray.toJSONString();
    }

    @RequestMapping(value ="/move")
    public ModelAndView signPdf(String data, HttpServletRequest request) throws Exception {
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
        //查找签章人员  signUser
        Person signPer = personService.findPersonBySYS(businessSys,signUser);
        if (signPer==null){
            modelAndView.addObject("msg","用户不存在");
            modelAndView.setViewName("error");
            return modelAndView;
        }
        //查找文档 如果文档不存在。跳转错误页面，提示PDF文件不存在
        File pdfFile = fileService.findPDFFileByDocumentCode(documentCode,1);
        if (pdfFile==null){
            modelAndView.addObject("msg","PDF文件不存在");
            modelAndView.setViewName("error");
            return modelAndView;
        }
        copy(pdfFile,new File(pdfFile.getAbsolutePath()+"1"));
        try {
            File file = new File(pdfFile.getAbsolutePath()+"1");
            ProcessPDF.addPdfTextMarkFull(file,signPer.getPerson_name(),0.1f);
            ProcessPDF.addPdfQRCode(file,documentCode,10,10);
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("msg","添加水印失败");
            modelAndView.setViewName("error");
            return modelAndView;
        }
        //将文档编码放入session中
        HttpSession session = request.getSession();
        session.setAttribute("documentCode",documentCode);
        //处理PDF
        List<ESSPdfPage> pdfImagesList = fileService.splitPDFToImages(pdfFile);
        modelAndView.addObject("pdfImagesList",pdfImagesList);
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
            if (sealDto !=null){
                SealBean sealBean = new SealBean();
                sealBean.setSealId(sealDto.getSealId());
                sealBean.setImgBase64(sealDto.getSeal_img());
                sealBean.setWidth(String.valueOf((sealDto.getSeal_w()*2)));
                sealBean.setHeight(String.valueOf((sealDto.getSeal_h()*2)));
                signs.add(sealBean);
            }
//            if (!powerService.checkPowerForSealByUser(seal,signUser)){
//                //如果此印章不存在授权则跳过
//                continue;
//            }
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
        for(Object js :ja){
            File pdfFile = fileService.findPDFFileByDocumentCode(documentCode,2);
            JSONObject aa = (JSONObject)js;
            int page = aa.getInteger("page");
            String sealId = aa.getString("sealId");
            float x = aa.getFloat("x");
            float y = aa.getFloat("y");
            SealDto sealDto = sealService.findSealById(sealId);
            int width = convertCoordinate(sealDto.getSeal_w());
            int height = convertCoordinate(sealDto.getSeal_h());
            byte[] imgFile = ESSGetBase64Decode(sealDto.getSeal_img());
            byte[] pdfFileByte = File2byte(pdfFile);
            //查找文档
            FileOutputStream os  = new FileOutputStream(new File(pdfFile.getAbsolutePath()));
            signService.addSeal(pdfFileByte,imgFile,width,height,page,x,y, sealDto,os);
        }
        return "success";
    }
}