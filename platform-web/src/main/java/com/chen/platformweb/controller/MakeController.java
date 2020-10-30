package com.chen.platformweb.controller;

import com.alibaba.fastjson.JSONObject;
import com.chen.core.base.Constant;
import com.chen.core.base.PageBean;
import com.chen.core.util.crypt.CertStruct;
import com.chen.dao.IssuerUnitMapper;
import com.chen.entity.*;
import com.chen.platformweb.utils.PowerUtil;
import com.chen.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.chen.core.util.Base64Utils.ESSGetBase64Decode;
import static com.chen.core.util.Base64Utils.ESSGetBase64Encode;
import static com.chen.core.util.DateUtils.getDate;
import static com.chen.core.util.DateUtils.getDateTime;
import static com.chen.core.util.FastJsonUtil.toJSONString;
import static com.chen.core.util.StringUtils.getUUID;
import static com.chen.core.util.StringUtils.isNull;
import static com.chen.core.util.crypt.RSACrypto.CreateUserRSAPfx;
import static com.chen.core.util.crypt.SM2Crypto.CreateUserSM2Pfx;
import static com.chen.platformweb.utils.PFXUtil.getX509Certificate;
import static com.chen.platformweb.utils.SessionUtil.getSessionAttribute;

@Controller
@RequestMapping("/make/")
public class MakeController {
    @Autowired
    private IUnitService unitService;
    @Autowired
    private IApplyService applyService;
    @Autowired
    private ICertificateService certificateService;
    @Autowired
    private ISealImgService sealImgService;
    @Autowired
    private ISealService sealService;
    @Autowired
    private ILogService logService;
    @Autowired
    private IStatusPublishService statusPublishService;
    @Autowired
    private  IUserService userService;
    @Autowired
    private IIssuerUnitService issuerUnitService;
    @Autowired
    private ISystemService systemService;
    @Value("${myConfig.socketIP}")
    public String socketIP;
    @Autowired
    private IssuerUnitMapper issuerUnitMapper;

    @RequestMapping(value ="list")
    public ModelAndView list(String unitId,int pageNum) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("make/list");
        //查询本次访问的单位
        Unit unit =unitService.findUnitById(unitId);
        mv.addObject("unit",unit);
        //查询本单位下的印章制作申请信息
        Apply apply = new Apply();
        apply.setUnit_id(unitId);
        //状态：提交申请
        apply.setApply_state(Constant.REVIEW_THROUGH);
        //查找符合条件的申请信息
        List<Apply> applyList = applyService.findApplyList(apply);
        //审核通过的信息
        apply.setApply_state(Constant.MAKE_NO_COMPLETION);
        List<Apply> sealApplyList_through = applyService.findApplyList(apply);
        applyList.addAll(sealApplyList_through);
        //排除其他管理员锁定的申请
        User user = (User)getSessionAttribute("loginUser");
        assert user != null;
        for(int i=0;i<applyList.size();i++){
            String makeId = applyList.get(i).getMake_user_id();
            if (makeId!=null&&!"".equals(makeId)&&!makeId.equals(user.getUser_id())){
                applyList.remove(i--);
            }
        }
        PageBean pageBean = new PageBean(pageNum,5,applyList.size());
        pageBean.setList(applyList);
        mv.addObject("pageBean",pageBean);
        return mv;
    }

    @RequestMapping(value ="selectApplyByName")
    public ModelAndView selectApplyByName(String keyword,String unitId,int pageNum) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("make/list");
        //查询本次访问的单位
        Unit unit =unitService.findUnitById(unitId);
        mv.addObject("unit",unit);
        //查询本单位下的印章制作申请信息
        Apply apply = new Apply();
        apply.setUnit_id(unitId);
        //状态：提交申请
        apply.setApply_state(Constant.REVIEW_THROUGH);
        //查找符合条件的申请信息
        List<Apply> applyList = applyService.findApplyListByKeyword(apply,keyword);
        //审核通过的信息
        apply.setApply_state(Constant.MAKE_NO_COMPLETION);
        List<Apply> sealApplyList_through = applyService.findApplyListByKeyword(apply,keyword);
        //制作人驳回的信息
        apply.setApply_state(Constant.MAKE_NO_THROUGH);
        List<Apply> sealApplyList_no_through =  applyService.findApplyListByKeyword(apply,keyword);
        //将驳回信息添加到list里
        applyList.addAll(sealApplyList_no_through);
        applyList.addAll(sealApplyList_through);
        //排除其他管理员锁定的申请
        User user = (User)getSessionAttribute("loginUser");
        assert user != null;
        for(int i=0;i<applyList.size();i++){
            String makeId = applyList.get(i).getMake_user_id();
            if (makeId!=null&&!"".equals(makeId)&&!makeId.equals(user.getUser_id())){
                applyList.remove(i--);
            }
        }
        PageBean pageBean = new PageBean(1,applyList.size(),applyList.size());
        pageBean.setList(applyList);
        mv.addObject("pageBean",pageBean);
        return mv;
    }
    @RequestMapping(value ="detail")
    public ModelAndView detail(String applyId) {
        User user = (User)getSessionAttribute("loginUser");
        assert user != null;
        ModelAndView mv = new ModelAndView();

        Apply apply = applyService.findApplyById(applyId);
        //检查印章是否可制作
        boolean re = sealService.verifySealCount(apply.getSeal_type_id(),apply.getUnit_id());
        if (!re){
            mv.setViewName("error");
            mv.addObject("message","印章授权数量已到达上限！");
            return mv;
        }
        if(apply.getMake_user_id()!=null&&!"".equals(apply.getMake_user_id())&&!user.getUser_id().equals(apply.getMake_user_id())){
            mv.setViewName("error");
            mv.addObject("message","此条记录已被其他管理员锁定");
            return mv;
        }
        //设置制作人锁定本记录
        apply.setMake_user_id(user.getUser_id());
        boolean a = applyService.updateApply(apply);
        mv.addObject("apply", apply);
        //证书算法列表
        //印章标准列表
        List<SealStandard> standards = sealService.findSealStandardList();
        mv.addObject("standards", standards);
        //证书来源列表
        List<CertSource> certSources = certificateService.findCertSource();
        mv.addObject("certSources", certSources);
        //印章存储类型列表
//        List<SealSaveType> sealSaveTypes = sealService.findSealSaveType();
//        mv.addObject("sealSaveTypes", sealSaveTypes);
        List<String> typeList = systemService.findFileType();
        if (typeList==null){
            mv.setViewName("error");
            mv.addObject("message", "授权出错或者没有授权产品");
        }
        mv.addObject("typeList", typeList);
        SealType sealType = sealService.findSealTypeById(apply.getSeal_type_id());
        mv.addObject("sealType", sealType);
        switch (apply.getApply_type()) {
            case Constant.APPLYTYPE_NEW:
                mv.setViewName("make/detail");
                break;
            case Constant.APPLY_TYPE_DELAY:
                mv.setViewName("make/detail_renew");
                break;
        }
        mv.addObject("socketIP",socketIP);
        return mv;
    }
    @RequestMapping(value ="getUKType")
    @ResponseBody
    public String getUKType(int sealStandard) {
        String result = "";
        List<UKDll> ukDllList = sealService.findUKDll(sealStandard);
        result = toJSONString(ukDllList);
        return result;
    }
    @RequestMapping(value ="through")
    @ResponseBody
    public String through(String applyId, String gifImg, String imgW, String imgH, String certId,
                          String standard, String ukId, String UKType, String algorithm,
                          String fileType, HttpServletRequest request) {
        Apply apply = applyService.findApplyById(applyId);
        //根据上传的图像生成印章图片
        SealImg sealImg = new SealImg();
        sealImg.setImg_id(getUUID());
        try{
            if (gifImg!=null) {
                sealImg.setImg_gif_data(gifImg);
                sealImg.setImage_type("gif");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        sealImg.setImage_w(Integer.parseInt(imgW));
        sealImg.setImage_h(Integer.parseInt(imgH));
        boolean b = sealImgService.addSealImg(sealImg);
        apply.setImg_id(sealImg.getImg_id());
        apply.setApply_state(Constant.MAKE_COMPLETION);
        apply.setCert_id(certId);
        apply.setSeal_standard(standard);
        if ("null".equals(UKType)) {
            apply.setUk_type(0);
            apply.setIs_uk(0);
        }else{
            apply.setUk_type( Integer.parseInt(UKType));
            apply.setIs_uk(1);
        }
        apply.setUk_id(ukId);
        User user = (User)getSessionAttribute("loginUser");
        assert user != null;
        //设置申请人
        apply.setMake_user_id(user.getUser_id());
        //设置申请时间
        apply.setMake_time(getDateTime());
        //fileType 页面传来的拼接值为 XXX@XXX@XXX@  这里去掉最后一个字符@
        fileType = fileType.substring(0,fileType.length()-1);
        apply.setFile_type(fileType);

        //添加 印章数据
        Seal seal =new Seal();
        seal.setSeal_id(apply.getSeal_id());
        //印章编码为 印章id的前14位
        seal.setSeal_code(apply.getSeal_id().substring(0,14));
        seal.setSeal_name(apply.getSeal_name());
        seal.setSeal_img_id(apply.getImg_id());
        seal.setSeal_cert_id(apply.getCert_id());
        seal.setSeal_type_id(apply.getSeal_type_id());
        seal.setSeal_person_id(apply.getSeal_person_id());
        seal.setUnit_id(apply.getUnit_id());
        seal.setSeal_start_time(apply.getSeal_start_time());
        seal.setSeal_end_time(apply.getSeal_end_time());
        seal.setDevice_code(apply.getDevice_code());
        seal.setIs_uk(apply.getIs_uk());
        seal.setSeal_standard(apply.getSeal_standard());
        seal.setApp_sym_key_enc(apply.getApp_sym_key_enc());
        seal.setData_sym_key_enc(apply.getData_sym_key_enc());
        seal.setEnc_file(apply.getEnc_file());
        seal.setInput_time(getDateTime());
        //设置制作人
        seal.setInput_user_id(user.getUser_id());
        seal.setState("1");
        seal.setJbr_card_data(apply.getJbr_card_data());
        seal.setJbr_card_name(apply.getJbr_card_name());
        seal.setJbr_card_type(apply.getJbr_card_type());
        seal.setUk_id(apply.getUk_id());
        seal.setFile_type(apply.getFile_type());
        seal.setUk_type(apply.getUk_type());
        //填写ASN1编码的印章数据
//        if (Integer.parseInt(algorithm)==2){ }
        try {
            String base64 = sealService.getASN1SealData(apply.getSeal_id(),apply.getSeal_type_id(),apply.getSeal_name(),
                    apply.getCert_id(),seal.getInput_time(),seal.getSeal_start_time(),seal.getSeal_end_time(),
                    ESSGetBase64Decode(gifImg),Integer.parseInt(imgW),Integer.parseInt(imgH));
            seal.setUsb_key_info(base64);
        }catch (Exception e){
            e.printStackTrace();
            return "error";
        }
        if (Constant.APPLYTYPE_REPEAT.equals(apply.getApply_type())){
            //如果申请类型为重做，需先删除旧印章
            boolean d = sealService.deleteSealById(seal.getSeal_id());
        }

        boolean a = applyService.updateApply(apply);
        boolean s = sealService.addSeal(seal);
        boolean sp = statusPublishService.sealStatusSync(seal.getSeal_code(),user.getPerson().getPerson_name());
        String ip = PowerUtil.getUserIp(request);
        boolean result2 = logService.addSysLog(user,"制作印章",user.getPerson().getPerson_name()+"制作印章："
                +seal.getSeal_name(),ip);
        return seal.getSeal_id();
    }
    @RequestMapping(value ="through_renew")
    @ResponseBody
    public String through_renew(String applyId) throws ParseException {
        Apply apply = applyService.findApplyById(applyId);
        Seal seal = sealService.findSealById(apply.getSeal_id());
        seal.setSeal_start_time(apply.getSeal_start_time());
        seal.setSeal_end_time(apply.getSeal_end_time());
        //首先判断是否UK印章
        if (seal.getIs_uk()==1){
            //重新生成asn1数据
            String base64 = sealService.getASN1SealData(seal.getSeal_id(),seal.getSeal_type_id(),seal.getSeal_name(),
                    seal.getSeal_cert_id(),seal.getInput_time(),seal.getSeal_start_time(),seal.getSeal_end_time(),
                    ESSGetBase64Decode(seal.getSealImg().getImg_gif_data()),seal.getSealImg().getImage_w(),seal.getSealImg().getImage_h());
            seal.setUsb_key_info(base64);
        }
        apply.setApply_state(Constant.MAKE_COMPLETION);
        applyService.updateApply(apply);
        boolean aa = sealService.updateSeal(seal);
        return "success";
    }

    @RequestMapping(value ="unlockMake")
    @ResponseBody
    public String unlockMake(String applyId) {
        Apply apply = applyService.findApplyById(applyId);
        apply.setMake_user_id("");
        boolean a = applyService.updateApply(apply);
        return "success";
    }

    @RequestMapping(value ="getCertPair")
    @ResponseBody
    public String getCertPair(String country,String province,String city, String unit,
                              String department,String name,String startTime,String endTime,String algorithm) {
        if (country==null||province==null||city==null||unit==null||department==null||name==null){
            return "error";
        }
        Certificate certificate =new Certificate();
        certificate.setCert_id(getUUID());

        certificate.setCountry(country);
        certificate.setProvince(province);
        certificate.setCity(city);
        certificate.setUnit(unit);
        certificate.setDepartment(department);
        certificate.setCert_name(name);
        certificate.setState(1);
        IssuerUnit issuerUnit = null;
        CertStruct cert = null;
        if ("1".equals(algorithm)){
            issuerUnit = issuerUnitMapper.findIssuerUnitByRSA();
            certificate.setAlgorithm("SHA1withRSA");
            try {
                cert = CreateUserRSAPfx(province,city,unit,department,name,ESSGetBase64Decode(issuerUnit.getIssuerUnitRoot()),
                        ESSGetBase64Decode(issuerUnit.getIssuerUnitPfx()),issuerUnit.getPfxPwd());
            } catch (GeneralSecurityException | IOException e) {
                e.printStackTrace();
            }
        }else{
            issuerUnit = issuerUnitMapper.findIssuerUnitBySM2();
            certificate.setAlgorithm("SM2");
            try {
                cert = CreateUserSM2Pfx(province,city,unit,department,name,ESSGetBase64Decode(issuerUnit.getIssuerUnitRoot()),
                        ESSGetBase64Decode(issuerUnit.getIssuerUnitPfx()),issuerUnit.getPfxPwd());
            } catch (GeneralSecurityException | IOException e) {
                e.printStackTrace();
            }
        }
        if (cert!=null){
            certificate.setPfx_base64(ESSGetBase64Encode(cert.bPfx));
            certificate.setCer_base64(ESSGetBase64Encode(cert.bCert));
            certificate.setCert_psw(cert.sPin);
        }else {
            return "error";
        }
        boolean result1 = certificateService.addCertificate(certificate);
        if (result1){
            return certificate.getCert_id();
        }
        return "error";
    }
    @RequestMapping(value ="upLoadCert")
    @ResponseBody
    public String upLoadCert(String country,String province,String city, String unit,
                             String department,String name,String certBase64,String pfxBase64,String password,int type) {
        if (country==null||province==null||city==null||unit==null||department==null||name==null){
            return "{\"error\":\"证书信息缺失\"}";
        }
        boolean result = issuerUnitService.VerifyCert(certBase64);
        if (result){
            Certificate certificate =new Certificate();
            certificate.setCert_id(getUUID());
            certificate.setCountry(country);
            certificate.setProvince(province);
            certificate.setCity(city);
            certificate.setUnit(unit);
            certificate.setDepartment(department);
            certificate.setCert_name(name);
            certificate.setState(1);
            if (type==1){
                //上传的时cer证书
                certificate.setCer_base64(certBase64);
            }else if(type==2){
                if(isNull(pfxBase64)&&isNull(password)){
                    //当证书和密码都不为空时
                    Map<String,String> cerInfo = new HashMap<>();
                    try {
                        //解析出cer证书 存入
                        X509Certificate certificateObject = getX509Certificate(ESSGetBase64Decode(pfxBase64),password);
                        certBase64 = ESSGetBase64Encode(certificateObject.getEncoded());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return "{\"error\":\"PFX解析失败，请检查密码\"}";
                    }
                    //上传的是pfx证书
                    certificate.setCer_base64(certBase64);
                    certificate.setCer_base64(pfxBase64);
                    certificate.setCert_psw(password);
                }else {
                    return "{\"error\":\"PFX证书信息缺失\"}";
                }
            }
            boolean result1 = certificateService.addCertificate(certificate);
            if (result1){
                return certificate.getCert_id();
            }
        }else {
            return "{\"error\":\"证书的颁发者不在可信任列表中，不可使用\"}";
        }
        return "{\"error\":\"未知错误，请联系管理员\"}";
    }
    /**
     *审核驳回
     * @param applyId 申请信息ID
     */
    @RequestMapping(value="/make_reject", method = RequestMethod.GET)
    @ResponseBody
    public String make_reject(String applyId, String message,HttpServletRequest request) {
        //注销申请信息
        Apply apply = applyService.findApplyById(applyId);
        apply.setEnc_file(message);
        if(apply==null){
            return "error";
        }
        //信息状态修改为审核人驳回
        apply.setApply_state(Constant.MAKE_NO_THROUGH);
        apply.setReview_user_id("");
        apply.setMake_user_id("");
        applyService.updateApply(apply);
        String ip = PowerUtil.getUserIp(request);
        User user = (User)getSessionAttribute("loginUser");
        assert user != null;
        boolean result2 = logService.addSysLog(user,"制章驳回",user.getPerson().getPerson_name()+"驳回印章制作申请："
                +apply.getSeal_name(),ip);
        return "success";
    }
    /**
     *提供批量制作印章的接口，内部使用。
     */
    @RequestMapping(value="/make_seal_more.do")
    @ResponseBody
    public String make_seal_more(String sealName, String imgBase64,String imgW ,String imgH,
                                 String certBase64, String unitId,String jbrName,String jbrNum,
                                 String makeUser,String startTime,String endTime,String sealTypeId,
                                 String algorithm,String UKType,String standard) {
        User user = userService.findUserByPersonId(makeUser);
        Unit unit = unitService.findUnitById(unitId);
        Seal rSeal = sealService.findSealByName(unit.getUnit_name(),sealName);
        if (rSeal!=null){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code","500");
            jsonObject.put("msg","证书验证失败");
            return jsonObject.toJSONString();
        }
        //首先生成申请信息,设置状态为待制作
        Apply apply = new Apply();
        apply.setApply_id(getUUID());
        //设置印章ID
        apply.setSeal_id(getUUID());
        //设置申请类别
        apply.setApply_type(Constant.APPLYTYPE_NEW);
        //设置申请的印章名称
        apply.setSeal_name(sealName);
        //申请的印章类别
        apply.setSeal_type_id(sealTypeId);
        apply.setSeal_person_id("");
        //设置经办人的姓名
        apply.setJbr_card_name(jbrName);
        //设置经办人的身份证号码
        apply.setJbr_card_data(jbrNum);
        //设置申请的单位
        apply.setUnit_id(unitId);
        //设置申请人
        apply.setApply_user_id(user.getUser_id());
        //设置申请时间
        apply.setApply_time(getDateTime());
        boolean result2 = logService.addSysLog(user,"添加印章申请",
                "管理员申请制作印章："+sealName,"127.0.0.1");
        //设置审核人
        apply.setReview_user_id(user.getUser_id());
        //设置审核时间
        apply.setReview_time(getDateTime());

        apply.setSeal_start_time(getDate());
        apply.setSeal_end_time(endTime);
        boolean result3 = logService.addSysLog(user,"审核印章申请",
                "管理员审核通过制作申请："+sealName,"127.0.0.1");
        //设置制作人
        apply.setMake_user_id(user.getUser_id());
        //设置制作时间
        apply.setMake_time(getDateTime());
        apply.setApply_state(Constant.MAKE_COMPLETION);
        apply.setTemp_file("");
        //根据上传的图像生成印章图片
        SealImg sealImg = new SealImg();
        sealImg.setImg_id(getUUID());
        try{
            if (imgBase64!=null) {
                sealImg.setImg_gif_data(imgBase64);
                sealImg.setImage_type("gif");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        sealImg.setImage_w(Integer.parseInt(imgW));
        sealImg.setImage_h(Integer.parseInt(imgH));
        boolean b = sealImgService.addSealImg(sealImg);
        apply.setImg_id(sealImg.getImg_id());
        boolean result6 = issuerUnitService.VerifyCert(certBase64);

        Certificate certificate =new Certificate();
        if (result6){
            //上传证书
            certificate.setCert_id(getUUID());
            certificate.setAlgorithm(algorithm);
            certificate.setCountry("中国");
            certificate.setProvince(unit.getUnit_province());
            certificate.setCity(unit.getUnit_city());
            certificate.setUnit(unit.getUnit_name());
            certificate.setDepartment(unit.getUnit_name());
            certificate.setCert_name(apply.getSeal_name());
            certificate.setState(1);
            certificate.setCert_state(2);
            certificate.setCer_base64(certBase64);
            boolean result1 = certificateService.addCertificate(certificate);
            if (!result1){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("code","500");
                jsonObject.put("msg","添加证书失败");
                return jsonObject.toJSONString();
            }
        }else{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code","500");
            jsonObject.put("msg","证书验证失败");
            return jsonObject.toJSONString();
        }
        apply.setCert_id(certificate.getCert_id());
        apply.setSeal_standard(standard);
        apply.setUk_type(Integer.parseInt(UKType));
        apply.setIs_uk(1);
        //添加
        boolean result = applyService.addApply(apply);
        apply = applyService.findApplyById(apply.getApply_id());
        //添加 印章数据
        Seal seal =new Seal();
        seal.setSeal_id(apply.getSeal_id());
        seal.setSeal_code(apply.getSeal_id().substring(0,14));
        seal.setSeal_name(apply.getSeal_name());
        seal.setSeal_img_id(apply.getImg_id());
        seal.setSeal_cert_id(apply.getCert_id());
        seal.setSeal_type_id(apply.getSeal_type_id());
        seal.setSeal_person_id(apply.getSeal_person_id());
        seal.setUnit_id(apply.getUnit_id());
        seal.setSeal_start_time(apply.getSeal_start_time());
        seal.setSeal_end_time(apply.getSeal_end_time());
        seal.setDevice_code(apply.getDevice_code());
        seal.setIs_uk(apply.getIs_uk());
        seal.setSeal_standard(apply.getSeal_standard());
        seal.setApp_sym_key_enc(apply.getApp_sym_key_enc());
        seal.setData_sym_key_enc(apply.getData_sym_key_enc());
        seal.setEnc_file(apply.getEnc_file());
        seal.setInput_time(getDateTime());
        //设置制作人
        seal.setInput_user_id(user.getUser_id());
        seal.setState("1");
        seal.setJbr_card_data(apply.getJbr_card_data());
        seal.setJbr_card_name(apply.getJbr_card_name());
        seal.setJbr_card_type(apply.getJbr_card_type());
        seal.setUk_id(apply.getUk_id());
        seal.setFile_type(apply.getFile_type());
        seal.setUk_type(apply.getUk_type());
        //填写ASN1编码的印章数据
        try {
            String base64 = sealService.getASN1SealData(apply.getSeal_id(),apply.getSeal_type_id(),apply.getSeal_name(),
                    apply.getCert_id(),seal.getInput_time(),seal.getSeal_start_time(),seal.getSeal_end_time(),
                    ESSGetBase64Decode(imgBase64),Integer.parseInt(imgW),Integer.parseInt(imgH));
            seal.setUsb_key_info(base64);
        }catch (Exception e){
            e.printStackTrace();
            return "error";
        }
        boolean c = sealService.addSeal(seal);
        boolean aa = statusPublishService.sealStatusSync(seal.getSeal_code(),user.getPerson().getPerson_name());
        boolean result4 = logService.addSysLog(user,"制作印章",
                "管理员制作了印章："+sealName,"127.0.0.1");
        JSONObject jsonObject = new JSONObject();

        if (c&&result4&&result){
            jsonObject.put("code","200");
            jsonObject.put("sealId",seal.getSeal_id());
            jsonObject.put("ASN1",seal.getUsb_key_info());
        }else {
            jsonObject.put("code","500");
            jsonObject.put("msg","制章失败");
        }
        return jsonObject.toJSONString();
    }
    /**
     *提供批量制作印章的接口，内部使用。
     */
    @RequestMapping(value="/make_hand_sign_more.do")
    @ResponseBody
    public String make_hand_sign_more(String sealName, String imgBase64,String imgW ,String imgH,
                                       String unitId,String jbrName,String jbrNum,
                                      String makeUser,String startTime,String endTime,String sealTypeId,
                                      String algorithm,String UKType,String standard,String personId) {
        //首先根据单位名称找到对应的单位信息
        User user = userService.findUserByPersonId(makeUser);
        Unit unit = unitService.findUnitById(unitId);
        //首先生成申请信息,设置状态为待制作
        Apply apply = new Apply();
        apply.setApply_id(getUUID());
        //设置印章ID
        apply.setSeal_id(getUUID());
        //设置申请类别
        apply.setApply_type(Constant.APPLYTYPE_NEW);
        //设置申请的印章名称
        apply.setSeal_name(sealName);
        //申请的印章类别
        apply.setSeal_type_id(sealTypeId);
        apply.setSeal_person_id(personId);
        //设置经办人的姓名
        apply.setJbr_card_name(jbrName);
        //设置经办人的身份证号码
        apply.setJbr_card_data(jbrNum);
        //设置申请的单位
        apply.setUnit_id(unitId);
        //设置申请人
        apply.setApply_user_id(user.getUser_id());
        //设置申请时间
        apply.setApply_time(getDateTime());
        boolean result2 = logService.addSysLog(user,"添加印章申请",
                "管理员申请制作印章："+sealName,"127.0.0.1");
        //设置审核人
        apply.setReview_user_id(user.getUser_id());
        //设置审核时间
        apply.setReview_time(getDateTime());
        apply.setSeal_start_time(startTime);
        apply.setSeal_end_time(endTime);
        boolean result3 = logService.addSysLog(user,"审核印章申请",
                "管理员审核通过制作申请："+sealName,"127.0.0.1");
        //设置制作人
        apply.setMake_user_id(user.getUser_id());
        //设置制作时间
        apply.setMake_time(getDateTime());
        apply.setApply_state(Constant.MAKE_COMPLETION);
        apply.setTemp_file("");
        //根据上传的图像生成印章图片
        SealImg sealImg = new SealImg();
        sealImg.setImg_id(getUUID());
        try{
            if (imgBase64!=null) {
                sealImg.setImg_gif_data(imgBase64);
                sealImg.setImage_type("gif");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        sealImg.setImage_w(Integer.parseInt(imgW));
        sealImg.setImage_h(Integer.parseInt(imgH));
        boolean b = sealImgService.addSealImg(sealImg);
        apply.setImg_id(sealImg.getImg_id());

        //生成国密证书
        Certificate certificate =new Certificate();
        certificate.setCert_id(getUUID());
        certificate.setCountry("中国");
        certificate.setProvince(unit.getUnit_province());
        certificate.setCity(unit.getUnit_city());
        certificate.setUnit(unit.getUnit_name());
        certificate.setDepartment(unit.getUnit_name());
        certificate.setCert_name(apply.getSeal_name());
        certificate.setState(1);
        certificate.setCert_state(4);

        IssuerUnit issuerUnit = null;
        CertStruct cert = null;
        issuerUnit = issuerUnitMapper.findIssuerUnitBySM2();
        certificate.setAlgorithm("SM2");
        try {
            cert = CreateUserSM2Pfx(unit.getUnit_province(),unit.getUnit_city(),unit.getUnit_name(),unit.getUnit_name(),
                    apply.getSeal_name(),ESSGetBase64Decode(issuerUnit.getIssuerUnitRoot()),
                    ESSGetBase64Decode(issuerUnit.getIssuerUnitPfx()),issuerUnit.getPfxPwd());
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        if (cert!=null){
            certificate.setPfx_base64(ESSGetBase64Encode(cert.bPfx));
            certificate.setCer_base64(ESSGetBase64Encode(cert.bCert));
            certificate.setCert_psw(cert.sPin);
        }else {
            return "error";
        }
        boolean result1 = certificateService.addCertificate(certificate);
        apply.setCert_id(certificate.getCert_id());

        apply.setSeal_standard(standard);
        apply.setIs_uk(0);
        //添加
        boolean result = applyService.addApply(apply);
        apply = applyService.findApplyById(apply.getApply_id());
        //添加 印章数据
        Seal seal =new Seal();
        seal.setSeal_id(apply.getSeal_id());
        seal.setSeal_code(apply.getSeal_id().substring(0,14));
        seal.setSeal_name(apply.getSeal_name());
        seal.setSeal_img_id(apply.getImg_id());
        seal.setSeal_cert_id(apply.getCert_id());
        seal.setSeal_type_id(apply.getSeal_type_id());
        seal.setSeal_person_id(apply.getSeal_person_id());
        seal.setUnit_id(apply.getUnit_id());
        seal.setSeal_start_time(apply.getSeal_start_time());
        seal.setSeal_end_time(apply.getSeal_end_time());
        seal.setDevice_code(apply.getDevice_code());
        seal.setIs_uk(apply.getIs_uk());
        seal.setSeal_standard(apply.getSeal_standard());
        seal.setApp_sym_key_enc(apply.getApp_sym_key_enc());
        seal.setData_sym_key_enc(apply.getData_sym_key_enc());
        seal.setEnc_file(apply.getEnc_file());
        seal.setInput_time(getDateTime());
        //设置制作人
        seal.setInput_user_id(user.getUser_id());
        seal.setState("1");
        seal.setJbr_card_data(apply.getJbr_card_data());
        seal.setJbr_card_name(apply.getJbr_card_name());
        seal.setJbr_card_type(apply.getJbr_card_type());
        seal.setUk_id(apply.getUk_id());
        seal.setFile_type(apply.getFile_type());
        seal.setUk_type(apply.getUk_type());
        try {
            String base64 = sealService.getASN1SealData(apply.getSeal_id(),apply.getSeal_type_id(),apply.getSeal_name(),
                    apply.getCert_id(),seal.getInput_time(),seal.getSeal_start_time(),seal.getSeal_end_time(),
                    ESSGetBase64Decode(imgBase64),Integer.parseInt(imgW),Integer.parseInt(imgH));
            seal.setUsb_key_info(base64);
        }catch (Exception e){
            e.printStackTrace();
            return "error";
        }

        boolean c = sealService.addSeal(seal);
        boolean aa = statusPublishService.sealStatusSync(seal.getSeal_code(),user.getPerson().getPerson_name());
        boolean result4 = logService.addSysLog(user,"制作印章",
                "管理员制作了印章："+sealName,"127.0.0.1");

        JSONObject jsonObject = new JSONObject();

        if (c&&result4&&result){
            jsonObject.put("code","200");
            jsonObject.put("sealId",seal.getSeal_id());
            jsonObject.put("ASN1",seal.getUsb_key_info());
        }else {
            jsonObject.put("code","500");
            jsonObject.put("msg","制章失败");
        }
        return jsonObject.toJSONString();
    }
}
