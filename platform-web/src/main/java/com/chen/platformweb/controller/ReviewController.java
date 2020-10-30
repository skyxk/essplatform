package com.chen.platformweb.controller;

import com.chen.core.base.Constant;
import com.chen.core.base.PageBean;
import com.chen.entity.*;
import com.chen.platformweb.utils.PowerUtil;
import com.chen.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.chen.core.util.DateUtils.getDateTime;
import static com.chen.platformweb.utils.SessionUtil.getSessionAttribute;

@Controller
@RequestMapping("/review/")
public class ReviewController {
    @Autowired
    private IUnitService unitService;
    @Autowired
    private IApplyService applyService;
    @Autowired
    private ICertificateService certificateService;
    @Autowired
    private ISealService sealService;
    @Autowired
    private IMessageService messageService;
    @Autowired
    private ILogService logService;
    /**
     * 到达审核页面list
     */
    @RequestMapping(value ="list")
    public ModelAndView list(String unitId,int pageNum) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("review/list");
        //查询本次访问的单位
        Unit unit =unitService.findUnitById(unitId);
        mv.addObject("unit",unit);
        //查询本单位下的印章制作申请信息
        Apply apply = new Apply();
        apply.setUnit_id(unitId);
        //状态：提交申请
        apply.setApply_state(Constant.SUBMIT_APPLICATION);
        //查找符合条件的申请信息
        List<Apply> applyList = applyService.findApplyList(apply);
        //锁定的申请
        apply.setApply_state(Constant.REVIEW_NO_THROUGH);
        List<Apply> sealApplyList_through = applyService.findApplyList(apply);
        //制作人驳回的信息
        apply.setApply_state(Constant.MAKE_NO_THROUGH);
        List<Apply> sealApplyList_no_through =  applyService.findApplyList(apply);
        //将驳回信息添加到list里
        applyList.addAll(sealApplyList_no_through);
        applyList.addAll(sealApplyList_through);
        //排除其他管理员锁定的申请
        User user = (User)getSessionAttribute("loginUser");
        assert user != null;
        for(int i=0;i<applyList.size();i++){
            String reviewId = applyList.get(i).getReview_user_id();
            if (reviewId!=null&&!"".equals(reviewId)&&!reviewId.equals(user.getUser_id())){
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
        mv.setViewName("review/list");
        //查询本次访问的单位
        Unit unit =unitService.findUnitById(unitId);
        mv.addObject("unit",unit);
        //查询本单位下的印章制作申请信息
        Apply apply = new Apply();
        apply.setUnit_id(unitId);
        //状态：提交申请
        apply.setApply_state(Constant.SUBMIT_APPLICATION);
        //查找符合条件的申请信息
        List<Apply> applyList = applyService.findApplyListByKeyword(apply,keyword);
        //锁定的申请
        apply.setApply_state(Constant.REVIEW_NO_THROUGH);
        List<Apply> sealApplyList_through = applyService.findApplyListByKeyword(apply,keyword);
        //制作人驳回的信息
        apply.setApply_state(Constant.MAKE_NO_THROUGH);
        List<Apply> sealApplyList_no_through =  applyService.findApplyList(apply);
        //将驳回信息添加到list里
        applyList.addAll(sealApplyList_no_through);
        applyList.addAll(sealApplyList_through);
        //排除其他管理员锁定的申请
        User user = (User)getSessionAttribute("loginUser");
        assert user != null;
        for(int i=0;i<applyList.size();i++){
            String reviewId = applyList.get(i).getReview_user_id();
            if (reviewId!=null&&!"".equals(reviewId)&&!reviewId.equals(user.getUser_id())){
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
        if(apply.getReview_user_id()!=null&&!"".equals(apply.getReview_user_id())&&!user.getUser_id().equals(apply.getReview_user_id())){
            mv.setViewName("error");
            mv.addObject("message","此条记录已被其他管理员锁定");
            return mv;
        }
        //设置审核人
        apply.setReview_user_id(user.getUser_id());
        boolean a = applyService.updateApply(apply);
        mv.addObject("apply", apply);
        Certificate certificate = certificateService.findCertificateById(apply.getCert_id());
        mv.addObject("certificate", certificate);
        List<SealInDate> sealInDateList = sealService.findSealInDate();
        mv.addObject("sealInDateList", sealInDateList);
        switch (apply.getApply_type()) {
            case Constant.APPLYTYPE_NEW:
                SealType sealType = sealService.findSealTypeById(apply.getSeal_type_id());
                mv.addObject("sealType", sealType);
                mv.setViewName("review/detail");
                break;
            case Constant.APPLYTYPE_REGISTER_UK:
                List<SealType> sealTypeList = sealService.findSealTypeList();
                mv.addObject("sealTypeList",sealTypeList);
                mv.setViewName("review/detail_r");
                break;
            case Constant.APPLYTYPE_REPEAT:
                SealType sealType_1 = sealService.findSealTypeById(apply.getSeal_type_id());
                mv.addObject("sealType", sealType_1);
                mv.setViewName("review/detail_rework");
                break;
            case Constant.APPLY_TYPE_DELAY:
                SealType sealType_3 = sealService.findSealTypeById(apply.getSeal_type_id());
                mv.addObject("sealType", sealType_3);
                mv.setViewName("review/detail");
                break;
        }
        return mv;
    }
    /**
     * 制作新印章通过
     * @param applyId s
     * @param sealStartTime s
     * @param sealEndTime s
     * @return s
     */
    @RequestMapping(value ="through")
    @ResponseBody
    public String through(String applyId, String sealStartTime, String sealEndTime, HttpServletRequest request) {
        Apply apply = applyService.findApplyById(applyId);
        apply.setSeal_start_time(sealStartTime);
        apply.setSeal_end_time(sealEndTime);
        User user = (User)getSessionAttribute("loginUser");
        assert user != null;
        //设置审核人
        apply.setReview_user_id(user.getUser_id());
        //设置审核时间
        apply.setReview_time(getDateTime());
        apply.setApply_state(Constant.REVIEW_THROUGH);
        boolean a = applyService.updateApply(apply);
        String ip = PowerUtil.getUserIp(request);
        boolean result2 = logService.addSysLog(user,"审核印章申请",user.getPerson().getPerson_name()+"审核通过申请信息："
                        +apply.getSeal_name(),ip);
        return "success";
    }
    /**
     * 注册印章通过
     * @return s
     */
    @RequestMapping(value ="through_r")
    @ResponseBody
    public String through_r(String applyId,String sealTypeId,HttpServletRequest request) {
        Apply apply = applyService.findApplyById(applyId);

        User user = (User)getSessionAttribute("loginUser");
        assert user != null;
        //设置审核人
        apply.setReview_user_id(user.getUser_id());
        apply.setSeal_type_id(sealTypeId);
        //设置审核时间
        apply.setReview_time(getDateTime());
        apply.setApply_state(Constant.MAKE_COMPLETION);
        boolean a = applyService.updateApply(apply);
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
        seal.setUsb_key_info(apply.getUsb_key_info());
        Seal seal1 = sealService.findSealById(seal.getSeal_id());
        if (seal1==null){
            boolean c = sealService.addSeal(seal);
        }else{
            return "error";
        }
        String ip = PowerUtil.getUserIp(request);
        boolean result2 = logService.addSysLog(user,"审核印章注册申请",user.getPerson().getPerson_name()+"审核通过申请信息："
                +apply.getSeal_name(),ip);
        return "success";
    }

    /**
     *审核驳回
     * @param applyId 申请信息ID
     */
    @RequestMapping(value="/review_reject", method = RequestMethod.GET)
    @ResponseBody
    public String review_reject(String applyId, String message,HttpServletRequest request) {
        //注销申请信息
        Apply apply = applyService.findApplyById(applyId);
        if(apply==null){
            return "error";
        }
        //向消息中心发消息
        Message message_new  = new Message();
        User user = (User)getSessionAttribute("loginUser");
        message_new.setSender(user.getUser_id());
        message_new.setReceiver(apply.getApply_user_id());
        message_new.setMessageType(Constant.Message_Type_reject);
        message_new.setMessageTitle("申请"+apply.getSeal_name()+"被审核人驳回");
        message_new.setMessageContent(message);
        message_new.setApplyInfoId(apply.getApply_id());
        boolean result =  messageService.add(message_new);
        //信息状态修改为审核人驳回
        apply.setApply_state(Constant.STATE_10);
        boolean result1 = applyService.updateApply(apply);
        if (result&&result1){
            return "success";
        }
        String ip = PowerUtil.getUserIp(request);
        boolean result2 = logService.addSysLog(user,"驳回印章注册申请",user.getPerson().getPerson_name()+"审核驳回申请信息："
                +apply.getSeal_name(),ip);
        return "error";
    }

    @RequestMapping(value ="unlockReview")
    @ResponseBody
    public String unlockReview(String applyId) {
        Apply apply = applyService.findApplyById(applyId);
        apply.setReview_user_id("");
        boolean a = applyService.updateApply(apply);
        return "success";
    }

}
