package com.chen.platformweb.controller;

import com.alibaba.fastjson.JSONObject;
import com.chen.core.base.Constant;
import com.chen.core.base.PageBean;
import com.chen.core.util.FastJsonUtil;
import com.chen.core.util.StringUtils;
import com.chen.entity.*;
import com.chen.platformweb.bean.ResultMessageBeen;
import com.chen.platformweb.utils.PowerUtil;
import com.chen.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.List;
import java.util.Objects;

import static com.chen.core.util.DateUtils.getDateTime;
import static com.chen.core.util.StringUtils.getUUID;
import static com.chen.core.util.StringUtils.replaceBlank;
import static com.chen.platformweb.utils.SessionUtil.getSessionAttribute;

@Controller
@RequestMapping("/apply/")
public class ApplyController {
    @Autowired
    private  IUnitService unitService;
    @Autowired
    private IApplyService applyService;
    @Autowired
    private ICertificateService certificateService;
    @Autowired
    private ISealService sealService;
    @Autowired
    private  IUserService userService;
    @Autowired
    private  ILogService logService;
    @Value("${myConfig.hostURL}")
    public String hostURL;
    @Value("${myConfig.tempFilepath}")
    public String tempFilepath;
    /**
     * 访问申请信息列表
     * @param unitId 单位ID
     * @param pageNum 访问的页数
     */
    @RequestMapping(value ="list")
    public ModelAndView list(String unitId,int pageNum) {
        ModelAndView mv = new ModelAndView();
        //设置返回页面
        mv.setViewName("apply/list");
        //查询本次访问的单位
        Unit unit =unitService.findUnitById(unitId);
        //添加单位信息到页面中
        mv.addObject("unit",unit);
        //查询当前登陆人在本单位下的申请信息
        Apply apply = new Apply();
        apply.setUnit_id(unitId);
        //申请信息列表里只显示登录人申请的信息
        String userId = ((User) Objects.requireNonNull(
                getSessionAttribute("loginUser"))).getUser_id();
        apply.setApply_user_id(userId);
        //这里是查出不同状态的申请信息
        //状态：提交申请
        apply.setApply_state(Constant.SUBMIT_APPLICATION);
        //查找符合条件的申请信息
        List<Apply> applyList = applyService.findApplyList(apply);
        //审核通过的信息
        apply.setApply_state(Constant.REVIEW_NO_THROUGH);
        List<Apply> applyList_through = applyService.findApplyList(apply);
        //审核驳回的信息
        apply.setApply_state(Constant.STATE_10);
        List<Apply> applyList_re = applyService.findApplyList(apply);
        //将驳回信息添加到第一个list里
        applyList.addAll(applyList_through);
        applyList.addAll(applyList_re);
        //分页数据 自定义的分页类
        PageBean pageBean = new PageBean(pageNum,5,applyList.size());
        pageBean.setList(applyList);
        mv.addObject("pageBean",pageBean);
        return mv;
    }

    /**
     * 对申请信息列表的关键字搜索
     * @param keyword 关键字 （印章名称，经办人姓名，经办人信息）
     * @param unitId 单位ID
     * @param pageNum 访问的页数
     */
    @RequestMapping(value ="selectApplyByName")
    public ModelAndView selectApplyByName(String keyword,String unitId,int pageNum) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("apply/list");
        //查询本次访问的单位
        Unit unit =unitService.findUnitById(unitId);
        mv.addObject("unit",unit);
        //查询本单位下的印章制作申请信息
        Apply apply = new Apply();
        apply.setUnit_id(unitId);
        //申请信息列表里只显示登录人申请的信息
        String userId = ((User) Objects.requireNonNull(
                getSessionAttribute("loginUser"))).getUser_id();
        apply.setApply_user_id(userId);
        //状态：提交申请
        apply.setApply_state(Constant.SUBMIT_APPLICATION);
        //查找符合条件的申请信息
        List<Apply> applyList = applyService.findApplyListByKeyword(apply,keyword);
        //审核通过的信息
        apply.setApply_state(Constant.REVIEW_NO_THROUGH);
        List<Apply> sealApplyList_through = applyService.findApplyListByKeyword(apply,keyword);
        //审核通过的信息
        apply.setApply_state(Constant.STATE_10);
        List<Apply> STATE_10 = applyService.findApplyListByKeyword(apply,keyword);
        //将驳回信息添加到list里
        applyList.addAll(sealApplyList_through);
        applyList.addAll(STATE_10);
        PageBean pageBean = new PageBean(1,applyList.size(),applyList.size());
        pageBean.setList(applyList);
        mv.addObject("pageBean",pageBean);
        return mv;
    }

    /**
     * 访问添加申请页面
     * @param unitId 单位ID
     */
    @RequestMapping(value ="add")
    public ModelAndView add(String unitId) {
        ModelAndView mv = new ModelAndView();
        //查询当前单位
        Unit unit = unitService.findUnitById(unitId);
        //获取当前印章类型数据
        List<SealType> sealTypeList = sealService.findSealTypeList();
        mv.addObject("sealTypeList",sealTypeList);
        mv.addObject("unit",unit);
        //申请类别，制作新印章
        mv.addObject("applyType",Constant.APPLYTYPE_NEW);
        //hostURL用于在主页进行跳转到平台主页面，制章系统和其他模块使用不同的端口
        mv.addObject("hostURL", hostURL);
        //设置返回
        mv.setViewName("apply/add");
        return mv;
    }
    /**
     * 执行添加印章制作申请动作
     * @param sealName 印章名称
     * @param applyType 申请类别
     * @param sealTypeId 印章类型ID
     * @param jbr_name 经办人姓名
     * @param jbr_number 经办人信息
     * @param unitId 单位ID
     * @param upload_file 附件的保存路径
     * @param seal_person_id 如果此印章为个人签名章则为个人ID标识，可为空
     */
    @RequestMapping(value ="add_do",method = RequestMethod.POST)
    @ResponseBody
    public String add_do(String sealName, String applyType, String sealTypeId, String jbr_name, String jbr_number, String unitId,
                         String upload_file, String seal_person_id, HttpServletRequest request) {
        Unit unit = unitService.findUnitById(unitId);
        Seal rSeal = sealService.findSealByName(unit.getUnit_name(),sealName);
        if (rSeal!=null){
            return "error";
        }
        //组织信息
        Apply apply = new Apply();
        apply.setApply_id(getUUID());
        //设置印章ID
        apply.setSeal_id(getUUID());
        //设置申请类别
        apply.setApply_type(applyType);
        //设置申请的印章名称
        apply.setSeal_name(sealName);
        //申请的印章类别
        apply.setSeal_type_id(sealTypeId);
        apply.setSeal_person_id(seal_person_id);
        //设置经办人的姓名
        apply.setJbr_card_name(jbr_name);
        //设置经办人的身份证号码
        apply.setJbr_card_data(jbr_number);
        //设置申请的单位
        apply.setUnit_id(unitId);
        User user = (User)getSessionAttribute("loginUser");
        assert user != null;
        //设置申请人
        apply.setApply_user_id(user.getUser_id());
        //设置申请时间
        apply.setApply_time(getDateTime());
        //设置申请文件
        apply.setTemp_file(upload_file);
        //设置申请状态为已提交或待审核
        apply.setApply_state(Constant.SUBMIT_APPLICATION);
        //添加
        boolean result = applyService.addApply(apply);
        if(!result){
            return "error";
        }
        String ip = PowerUtil.getUserIp(request);
        //添加系统操作日志：添加印章申请
        logService.addSysLog(user,"添加印章申请",
                user.getPerson().getPerson_name()+"申请制作印章："+sealName,ip);
        //错误处理
        return "success";
    }
    @RequestMapping(value ="uploadApply.do",method = RequestMethod.POST)
    @ResponseBody
    public String add_do1(String sealName,String sealTypeId,String jbr_name,String jbr_number,String unitId,
                          MultipartFile upload_file, String applyUserId,String sealPerId, HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code","0000");
        jsonObject.put("message","申请成功");
        if (!StringUtils.isNull(sealName)||!StringUtils.isNull(sealTypeId)||!StringUtils.isNull(jbr_name)||!StringUtils.isNull(jbr_number)||
                !StringUtils.isNull(unitId)||!StringUtils.isNull(applyUserId)){
            jsonObject.put("code","0005");
            jsonObject.put("message","存在空参数");
            return jsonObject.toJSONString();
        }
        if ("ESS001".equals(sealTypeId)){
            //如果是手写签名
            if (!StringUtils.isNull(sealPerId)){
                jsonObject.put("code","0007");
                jsonObject.put("message","申请手签时必须提供人员标识");
                return jsonObject.toJSONString();
            }
        }
        SealType sealType = sealService.findSealTypeById(sealTypeId);
        if (sealType==null){
            jsonObject.put("code","0006");
            jsonObject.put("message","印章类型不存在");
            return jsonObject.toJSONString();
        }
        String filePath = "";
        if (upload_file!=null) {
            //保存文件并获取文件储存路径
            try {
                // 文件保存路径
                String fileType = upload_file.getOriginalFilename().split("\\.")[1];
                if ("PDF".equals(fileType)||"pdf".equals(fileType)||"JPG".equals(fileType)||"jpg".equals(fileType)||
                        "zip".equals(fileType)||"ZIP".equals(fileType)){
                    String UUID = getUUID();
                    filePath = tempFilepath + UUID+"."+fileType;
                    // 转存文件
                    upload_file.transferTo(new File(filePath));
                }else{
                    jsonObject.put("code","0010");
                    jsonObject.put("message","附件类型不正确");
                    return jsonObject.toJSONString();
                }
            } catch (Exception e) {
                e.printStackTrace();
                jsonObject.put("code","0001");
                jsonObject.put("message","保存文件错误");
                return jsonObject.toJSONString();
            }
        }else{
            jsonObject.put("code","0002");
            jsonObject.put("message","上传文件为空");
            return jsonObject.toJSONString();
        }
        Unit unit = unitService.findUnitById(unitId);
        if (unit==null){
            jsonObject.put("code","0008");
            jsonObject.put("message","单位不存在");
            return jsonObject.toJSONString();
        }
        Seal rSeal = sealService.findSealByName(unit.getUnit_name(),sealName);
        if (rSeal!=null){
            jsonObject.put("code","0003");
            jsonObject.put("message","该单位下已存在同名印章");
            return jsonObject.toJSONString();
        }
        //组织信息
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
        //手写签名人员标识
        apply.setSeal_person_id(sealPerId);
        //设置经办人的姓名
        apply.setJbr_card_name(jbr_name);
        //设置经办人的身份证号码
        apply.setJbr_card_data(jbr_number);
        //设置申请的单位
        apply.setUnit_id(unitId);
        User user = userService.findUserByPersonId(applyUserId);
        if (user==null){
            jsonObject.put("code","0009");
            jsonObject.put("message","人员不存在");
            return jsonObject.toJSONString();
        }
        //设置申请人
        apply.setApply_user_id(user.getUser_id());
        //设置申请时间
        apply.setApply_time(getDateTime());
        //设置申请状态为已提交或待审核
        apply.setApply_state(Constant.SUBMIT_APPLICATION);
        apply.setTemp_file(filePath);
        //添加
        boolean result = applyService.addApply(apply);
        if(!result){
            jsonObject.put("code","0004");
            jsonObject.put("message","申请失败");
        }
        String ip = PowerUtil.getUserIp(request);
        //添加系统操作日志：添加印章申请
        logService.addSysLog(user,"添加印章申请",
                user.getPerson().getPerson_name()+"申请制作印章："+sealName,ip);
        return jsonObject.toJSONString();
    }
    /**
     * 印章重做申请
     * 重做时只保留原印章的ID，其他重写生成
     * 并且重做过程中不影响原印章的状态，只有在最后一步制作时才会生成新印章注销原印章
     */
    @RequestMapping(value ="rework")
    @ResponseBody
    public String rework(String sealId, HttpServletRequest request) {
        //重做时首先查询已有印章信息
        Seal seal =  sealService.findSealById(sealId);
        if (seal==null){
            return "error";
        }
        //新建申请信息
        Apply apply = new Apply();
        apply.setApply_id(getUUID());
        //设置印章ID
        apply.setSeal_id(sealId);
        //设置申请类别
        apply.setApply_type(Constant.APPLYTYPE_REPEAT);
        //设置申请的印章名称
        apply.setSeal_name(seal.getSeal_name());
        //申请的印章类别
        apply.setSeal_type_id(seal.getSeal_type_id());
        //设置经办人的姓名
        apply.setJbr_card_name(seal.getJbr_card_name());
        //设置经办人的身份证号码
        apply.setJbr_card_data(seal.getJbr_card_data());
        //设置申请的单位
        apply.setUnit_id(seal.getUnit_id());
        User user = (User)getSessionAttribute("loginUser");
        assert user != null;
        //设置申请人
        apply.setApply_user_id(user.getUser_id());
        //设置申请时间
        apply.setApply_time(getDateTime());
        apply.setApply_state(Constant.SUBMIT_APPLICATION);
        //这里添加申请附件
//        apply.setTemp_file(seal.get);
        //添加
        boolean result = applyService.addApply(apply);
        if(!result){
            return "error";
        }
        String ip = PowerUtil.getUserIp(request);
        boolean result2 = logService.addSysLog(user,"添加印章申请",
                user.getPerson().getPerson_name()+"申请重做印章："+seal.getSeal_name()
                ,ip);
        return "success";
    }
    /**
     * 印章续期申请
     */
    @RequestMapping(value ="renew")
    @ResponseBody
    public String renew(String sealId,HttpServletRequest request) {
        //重做时首先查询已有印章信息
        Seal seal =  sealService.findSealById(sealId);
        if (seal==null){
            return "error";
        }
        //新建申请信息
        Apply apply = new Apply();
        apply.setApply_id(getUUID());
        //设置印章ID
        apply.setSeal_id(sealId);
        //设置申请类别
        apply.setApply_type(Constant.APPLY_TYPE_DELAY);
        //设置申请的印章名称
        apply.setSeal_name(seal.getSeal_name());
        //申请的印章类别
        apply.setSeal_type_id(seal.getSeal_type_id());
        //设置经办人的姓名
        apply.setJbr_card_name(seal.getJbr_card_name());
        //设置经办人的身份证号码
        apply.setJbr_card_data(seal.getJbr_card_data());
        //设置申请的单位
        apply.setUnit_id(seal.getUnit_id());
        User user = (User)getSessionAttribute("loginUser");
        assert user != null;
        //设置申请人
        apply.setApply_user_id(user.getUser_id());
        //设置申请时间
        apply.setApply_time(getDateTime());
        apply.setApply_state(Constant.SUBMIT_APPLICATION);
        //这里添加申请附件
//        apply.setTemp_file(seal.get);
        //添加
        boolean result = applyService.addApply(apply);
        if(!result){
            return "error";
        }
        String ip = PowerUtil.getUserIp(request);
        boolean result2 = logService.addSysLog(user,"添加印章申请",user.getPerson().getPerson_name()+"申请续期印章："+seal.getSeal_name()
                ,ip);
        return "success";
    }
    /**
     * 接收客户端发起的申请信息（客户端申请）
     */
    @RequestMapping(value ="clientApply")
    @ResponseBody
    public String clientApply(String NAME,String PHONE,String SEALNAME,String UNITNAME,
                              String UKID,String ENCCERT,String SIGNCERT,String SEALTYPE,
                                String FILENAME) {
        //去除空格
        NAME = replaceBlank(NAME);
        SEALNAME = replaceBlank(SEALNAME);
        UNITNAME = replaceBlank(UNITNAME);

        Apply apply = new Apply();
        apply.setApply_id(getUUID());
        //设置申请列别
        apply.setApply_type(Constant.APPLYTYPE_NEW);
        //设置申请状态
        apply.setApply_state(Constant.SUBMIT_APPLICATION);
        //设置印章ID
        apply.setSeal_id(getUUID());
        //设置经办人
        apply.setJbr_card_name(NAME);
        apply.setJbr_card_data(PHONE);
        //设置印章名称
        apply.setSeal_name(SEALNAME);
        //设置UKID
        apply.setUk_id(UKID);
        //设置附件地址
        apply.setTemp_file(FILENAME);
        //设置印章类型
        if("0".equals(SEALTYPE)){
            apply.setSeal_type_id("025st1");
        }else{
            apply.setSeal_type_id("ESS001");
        }
        //创建证书
        Certificate certificate = new Certificate();
        certificate.setCert_id(getUUID());
        certificate.setState(1);
        certificate.setUnit(UNITNAME);
        certificate.setCer_base64(SIGNCERT);
        certificate.setPfx_base64(ENCCERT);
        certificate.setCert_state(4);
        boolean a =  certificateService.addCertificate(certificate);
        //设置证书ID
        apply.setCert_id(certificate.getCert_id());

        //设置申请人
        apply.setApply_user_id("025user1");
        //设置申请时间
        apply.setApply_time(getDateTime());
        //设置申请的单位
        apply.setUnit_id("025unit1");
        //添加
        boolean result = applyService.addApply(apply);
        return "success";
    }

    /**
     *删除申请信息
     */
    @RequestMapping(value="delete", method = RequestMethod.GET,produces="text/html;charset=UTF-8")
    @ResponseBody
    public String sealApply_delete(String applyId,HttpServletRequest request){
        Apply apply = applyService.findApplyById(applyId);
        if (apply==null){
            return "申请信息不存在";
        }
        User user = (User)getSessionAttribute("loginUser");
        assert user != null;
        String ip = PowerUtil.getUserIp(request);
        boolean result2 = logService.addSysLog(user,"添加印章申请",
                user.getPerson().getPerson_name()+"撤销申请信息：" +apply.getSeal_name(),ip);
        //执行删除动作
        boolean result = applyService.delApplyById(applyId);
        if(!result){
            return "操作失败";
        }
        return "success";
    }
    /**
     * 获取人员信息
     * @param keyword 关键词
     */
    @RequestMapping(value="findPerson",produces="text/html;charset=UTF-8")
    @ResponseBody
    public String findPerson(String keyword) {
        //结果对象
        ResultMessageBeen messageBeen = new ResultMessageBeen();
        //判断关键词是为空
        if(!"".equals(keyword) && keyword!=null){
            //根据关键词查找人员信息
            List<Person> personList = userService.findPersonListByKeyword(keyword);
            //添加数据到返回结果中
            messageBeen.setBody(personList);
            //返回结果类型为成功
            messageBeen.setMessage("ESSSUCCESS");
        }else {
            messageBeen.setMessage("ESSERROR");
            messageBeen.setBody("关键字不可为空！");
        }
        return FastJsonUtil.toJSONString(messageBeen);
    }

}
