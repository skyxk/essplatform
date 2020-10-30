package com.chen.platformweb.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chen.core.base.Constant;
import com.chen.core.bean.BaseResult;
import com.chen.core.util.FastJsonUtil;
import com.chen.entity.*;
import com.chen.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import static com.chen.core.util.DateUtils.getDateTime;
import static com.chen.core.util.StringUtils.getUUID;

@Controller
@RequestMapping("/register/")
public class RegisterController {
    @Autowired
    private  IUnitService unitService;
    @Autowired
    private IApplyService applyService;
    @Autowired
    private ICertificateService certificateService;
    @Autowired
    private ISealService sealService;
    @Autowired
    private ISealImgService sealImgService;

    /**
     * 到达制章系统list页面
     */
    @RequestMapping(value ="list")
    public ModelAndView list() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("register/list");
        List<Unit> unitList = unitService.findUnitList();
        mv.addObject("unitList",unitList);
        return mv;
    }
    /**
     * 到达制章系统add页面
     */
    @RequestMapping(value ="add")
    public ModelAndView add(String unitId) {
        ModelAndView mv = new ModelAndView();
        Unit unit = unitService.findUnitById(unitId);
        //获取印章类型数据
        List<SealType> sealTypeList = sealService.findSealTypeList();
        mv.addObject("sealTypeList",sealTypeList);
        mv.addObject("unit",unit);
        //申请类别，制作新印章
        mv.addObject("applyType",Constant.APPLYTYPE_REGISTER_UK);
        mv.setViewName("register/add");
        //印章标准列表
        List<SealStandard> standards = sealService.findSealStandardList();
        mv.addObject("standards", standards);
        return mv;
    }
    /**
     * 到达制章系统add页面
     */
    @RequestMapping(value ="add_do")
    @ResponseBody
    public String add_do(String applyType,String jbr_name,String jbr_number,String unitId,
                         String DATA,String upload_file,String standard) {
        //准备一个申请列表
        BaseResult baseResult = new BaseResult<>();
        StringBuilder sealNameList = new StringBuilder();
//        String filename = "";
//        if (upload_file.isEmpty()) {
//            baseResult.setCode(100);
//            baseResult.setMsg("附件不能为空");
//            return FastJsonUtil.toJSONString(baseResult);
//        }
//        filename = fileService.saveFile(upload_file);
        System.out.println("*********************************************************");
        System.out.println(jbr_name);
        System.out.println(DATA);
        System.out.println("*********************************************************");
        JSONArray jsonArray = JSONArray.parseArray(DATA);
        if(jsonArray.size()>0){
            for(int i = 0;i<jsonArray.size();i++){
                //遍历 jsonarray 数组，把每一个对象转成 json 对象
                JSONObject job = jsonArray.getJSONObject(i);
                String sealId =job.getString("SEALID");
                String sealName =job.getString("SEALNAME");
                String UKID =job.getString("UKID");
                String cert =job.getString("CERT");
                String gifData = job.getString("GIFDATA");
                String ASN1 = job.getString("ASN1");
                int imgW = job.getInteger("IMGW");
                int imgH = job.getInteger("IMGH");
                String startTime = job.getString("STARTTIME");
                String endTime = job.getString("ENDTIME");
                Apply apply = new Apply();
                apply.setApply_type(applyType);
                apply.setApply_id(getUUID());
                //设置印章ID
                apply.setSeal_id(sealId);
                //设置申请的印章名称
                apply.setSeal_name(sealName);
                //设置经办人的姓名
                apply.setJbr_card_name(jbr_name);
                //设置经办人的身份证号码
                apply.setJbr_card_data(jbr_number);
                //设置申请的单位
                apply.setUnit_id(unitId);
                //设置申请人
                apply.setApply_user_id("025user1");
                apply.setUk_id(UKID);
                apply.setUsb_key_info(ASN1);
                apply.setSeal_start_time(startTime);
                apply.setSeal_end_time(endTime);

                apply.setSeal_standard(standard);
                //设置申请时间
                apply.setApply_time(getDateTime());
                apply.setApply_state(Constant.SUBMIT_APPLICATION);
                //保存文件并获取文件储存路径
                apply.setTemp_file(upload_file);
                //添加图片
                //根据上传的图像生成印章图片
                SealImg sealImg = new SealImg();
                sealImg.setImg_id(getUUID());
                sealImg.setImg_gif_data(gifData);
                sealImg.setImage_type("gif");
                sealImg.setImage_w(imgW);
                sealImg.setImage_h(imgH);
                boolean b = sealImgService.addSealImg(sealImg);
                apply.setImg_id(sealImg.getImg_id());
                //添加证书
                Unit unit = unitService.findUnitById(unitId);
                Certificate certificate =new Certificate();
                certificate.setCert_id(getUUID());
                certificate.setCountry("中国");
                certificate.setProvince(unit.getUnit_province());
                certificate.setCity(unit.getUnit_city());
                certificate.setUnit(unit.getUnit_name());
                certificate.setDepartment(unit.getUnit_name());
                certificate.setCert_name(sealName);
                certificate.setState(1);
                certificate.setCer_base64(cert);
                boolean result1 = certificateService.addCertificate(certificate);
                if (result1){
                    apply.setCert_id(certificate.getCert_id());
                }else{
                    break;
                }
                //添加
                boolean result = applyService.addApply(apply);
                if(!result){
                    break;
                }
                sealNameList.append(sealName).append(";");
            }
        }else{
            baseResult.setCode(101);
            baseResult.setMsg("上传的印章数量为空");
            return FastJsonUtil.toJSONString(baseResult);
        }
        baseResult.setCode(102);
        baseResult.setMsg("成功添加："+sealNameList.toString());
        return FastJsonUtil.toJSONString(baseResult);
    }
}
