package com.chen.platformweb.controller;

import com.chen.core.base.Constant;
import com.chen.core.base.PageBean;
import com.chen.entity.*;
import com.chen.platformweb.service.IPowerService;
import com.chen.platformweb.utils.PowerUtil;
import com.chen.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

import static com.chen.platformweb.utils.SessionUtil.getSessionAttribute;

@Controller
@RequestMapping("/seal/")
public class SealController {

    @Autowired
    private IUnitService unitService;
    @Autowired
    private ISealService sealService;
    @Autowired
    private IApplyService applyService;
    @Autowired
    private IStatusPublishService statusPublishService;
    @Autowired
    private ILogService logService;
    @Autowired
    private IPowerService powerService;

    @Value("${myConfig.socketIP}")
    public String socketIP;

    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected HttpSession session;
    /**
     * 每次拦截到请求会先访问此函数，
     * 获取request,session,response等实例
     * @param request http请求
     * @param response http回应
     */
    @ModelAttribute
    public void setReqAndRes(HttpServletRequest request, HttpServletResponse response){
        this.request = request;
        this.response = response;
        this.session = request.getSession();
    }

    @RequestMapping(value ="list")
    public ModelAndView list(String unitId,int pageNum) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("seal/list");
        //查询本次访问的单位
        Unit unit =unitService.findUnitById(unitId);
        mv.addObject("unit",unit);
        Seal seal = new Seal();
        seal.setUnit_id(unitId);
        seal.setState("1");
        List<Seal> sealList = sealService.findSealList(seal);
        PageBean pageBean = new PageBean(pageNum,5,sealList.size());
        pageBean.setList(sealList);
        mv.addObject("pageBean",pageBean);
        return mv;
    }

//    @RequestMapping(value ="all")
//    public ModelAndView all(int pageNum) {
//        ModelAndView mv = new ModelAndView();
//        mv.setViewName("seal/list");
//        //查询本次访问的单位
//        Unit unit =unitService.findUnitById("025unit1");
//        mv.addObject("unit",unit);
//        Seal seal = new Seal();
//        seal.setState("1");
//        List<Seal> sealList = sealService.findSealList(seal);
//        PageBean pageBean = new PageBean(pageNum,9,sealList.size());
//        pageBean.setList(sealList);
//        mv.addObject("pageBean",pageBean);
//        return mv;
//    }

    @RequestMapping(value ="detail")
    public ModelAndView detail(String sealId) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("seal/detail");
        Seal seal = sealService.findSealById(sealId);
        mv.addObject("seal", seal);
        SealType sealType = sealService.findSealTypeById(seal.getSeal_type_id());
        mv.addObject("sealType", sealType);
        mv.addObject("socketIP",socketIP);
        return mv;
    }
    @RequestMapping(value ="selectSealByName")
    public ModelAndView selectSealByName(String keyword,String unitId) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("seal/list");
        //查询本次访问的单位
        Unit unit =unitService.findUnitById(unitId);
        mv.addObject("unit",unit);
        List<Seal> sealList = sealService.findSealListByKeyword(keyword,unitId);

        PageBean pageBean = new PageBean(1,sealList.size(),sealList.size());
        pageBean.setList(sealList);
        mv.addObject("pageBean",pageBean);
        return mv;
    }
    @RequestMapping(value ="cancel")
    @ResponseBody
    public String cancel(String sealId, HttpServletRequest request) {
        if(!powerService.getPowerForButton(session,"makeSealPower_stopSeal")){
            //没有注销印章的权限
            return "error";
        }
        Seal seal = new Seal();
        seal.setSeal_id(sealId);
        seal.setState("0");
        boolean result = sealService.updateSeal(seal);
        seal = sealService.findSealById(sealId);
        Apply apply = new Apply();
        apply.setSeal_id(sealId);
        List<Apply> applyList = applyService.findApplyList(apply);
        if (applyList.size()==1){
            apply = applyList.get(0);
            apply.setApply_state(Constant.NO_AVAIL);
            boolean a =applyService.updateApply(apply);
        }
        User user = (User)getSessionAttribute("loginUser");
        assert user != null;
        boolean aa = statusPublishService.updateSealStatus(seal.getSeal_code(),
                user.getPerson().getPerson_name(),"1");
        String ip = PowerUtil.getUserIp(request);
        boolean result2 = logService.addSysLog(user,"制作印章",user.getPerson().getPerson_name()+"制作印章："
                +seal.getSeal_name(),ip);
        if (aa||result2||result){
            return "success";
        }
        return "error";
    }
    @RequestMapping(value ="getSealTypeName")
    @ResponseBody
    public String getSealTypeName(String typeId) {
        String result = "未知类型";
        SealType sealType = sealService.findSealTypeById(typeId);
        return sealType.getSealTypeName();
    }
    @RequestMapping(value ="write")
    public ModelAndView write() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("seal/write");
        return mv;
    }
    @RequestMapping(value ="write_uk")
    public ModelAndView write_uk(String sealId) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("seal/write_uk");
        Seal seal = sealService.findSealById(sealId);
        if (seal == null){
            mv.setViewName("error");
            mv.addObject("message", "不存在此印章");
            return mv;
        }
        mv.addObject("seal", seal);
        SealType sealType = sealService.findSealTypeById(seal.getSeal_type_id());
        mv.addObject("sealType", sealType);
        mv.addObject("socketIP",socketIP);
        return mv;
    }

    @RequestMapping(value ="addUKID")
    @ResponseBody
    public String addUKID(String sealId,String UKID) {
        Seal seal = sealService.findSealById(sealId);
        if (seal == null){
            return "error";
        }
        seal.setUk_id(UKID);
        boolean result = sealService.updateSeal(seal);
        if (result){
            return "success";
        }
        return "error";
    }

    /**
     *暂停和恢复印章
     * @param sealId
     */
    @RequestMapping(value="/seal_pause_switch.html", method = RequestMethod.GET)
    @ResponseBody
    public String seal_pause_switch(String sealId,int sealState) {
        if(!powerService.getPowerForButton(session,"makeSealPower_stopSeal")){
            //没有注销印章的权限
            return "error";
        }
        Seal seal = new Seal();
        seal.setSeal_id(sealId);
        if(sealState == 1){
            sealState = 2;
        }else if (sealState == 2){
            sealState = 1;
        }
        seal.setState(String.valueOf(sealState));
        boolean result = sealService.updateSeal(seal);
        seal = sealService.findSealById(sealId);
        User user = (User)getSessionAttribute("loginUser");
        assert user != null;
        boolean aa = statusPublishService.updateSealStatus(seal.getSeal_code(),
                user.getPerson().getPerson_name(),"1");
        String ip = PowerUtil.getUserIp(request);
        boolean result2 = logService.addSysLog(user,"制作印章",user.getPerson().getPerson_name()+"制作印章："
                +seal.getSeal_name(),ip);
        if (aa||result2||result){
            return "success";
        }
        return "error";
    }
}
