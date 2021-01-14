package com.chen.platformweb.controller;

import com.chen.core.util.FastJsonUtil;
import com.chen.entity.PhoneLoginInfo;
import com.chen.entity.SystemJurInit;
import com.chen.entity.User;
import com.chen.entity.ZTree;
import com.chen.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.util.List;

import static com.chen.core.util.StringUtils.decodeSSO;
import static com.chen.core.util.StringUtils.getRandom16;
import static com.chen.platformweb.utils.SessionUtil.setSessionAttribute;

/**
 * 子系统访问入口控制器
 */
@Controller
@RequestMapping("/")
public class MainController {
    private final IUnitService unitService;
    private final IUserService userService;
    private final PhoneLoginInfoService phoneLoginInfoService;
    private final ISystemService systemService;
    private final IErrorLogService errorLogService;
    public MainController(IUnitService unitService, IUserService userService, PhoneLoginInfoService phoneLoginInfoService, ISystemService systemService, IErrorLogService errorLogService) {
        this.unitService = unitService;
        this.userService = userService;
        this.phoneLoginInfoService = phoneLoginInfoService;
        this.systemService = systemService;
        this.errorLogService = errorLogService;
    }
    @Value("${myConfig.hostURL}")
    public String hostURL;
    @Autowired
    private IStatusPublishService statusPublishService;
    @Autowired
    private IPersonService personService;
    /**
     * 测试入口，正式部署试删掉
     */
    @RequestMapping(value ="make/index")
    public ModelAndView make_index() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("make_index");
        String personId = "025person1";
        User user = userService.findUserByPersonId(personId);
        //设置登陆用户
        setSessionAttribute("loginUser",user);
        //根据当前登录用户获取其单位列表结构
        List<ZTree> tree =  unitService.findUnitMenu(user.getUnit_id());
        mv.addObject("unit_menu", FastJsonUtil.toJSONString(tree));
        mv.addObject("user", user);
        SystemJurInit systemJurInit = systemService.findSystemInit();
        mv.addObject("systemJurInit", systemJurInit);
        mv.addObject("SYSTEMNAME", systemJurInit.getSYSTEMNAME());
        if (systemJurInit==null){
            mv.setViewName("error");
            mv.addObject("message", "系统未检测到授权信息");
        }
        mv.addObject("hostURL", hostURL);
        return mv;
    }
    /**
     * 由签章平台系统跳转入口
     * @param random 口令 （是一串随机数，需要以此向数据库查询用户等跳转信息）
     */
    @RequestMapping(value = "essSealMake.html", method = RequestMethod.GET)
    public ModelAndView essSealMake(String random){
        //新建视图对象
        ModelAndView modelAndView = new ModelAndView();
        //设置返回页面
        modelAndView.setViewName("make_index");
        //判断随机数是否有参数传递过来
        if(random==null || "".equals(random)){
            //如果为空，跳转到错误页面并提示错误信息
            modelAndView.setViewName("error");
            modelAndView.addObject("message", "参数信息被篡改，请重试！");
            //写入错误日
            errorLogService.addErrorLog("MainController-signatureTrack-访问随机数缺失");
            return modelAndView;
        }
        //通过随机数查询用户信息
        PhoneLoginInfo info = phoneLoginInfoService.queryByRandom(random);
        if(info == null || info.getRandom()==null || "".equals(info.getRandom())){
            modelAndView.setViewName("error");
            modelAndView.addObject("message", "参数信息被篡改，请重试！");
            errorLogService.addErrorLog("MainController-signatureTrack-访问随机数无效");
            return modelAndView;
        }
        //通过随机数获取到信息后将其中的随机数设为空并更新
        //这里是为了保证生成的随机数一次可用。
        //提示：这种方式在现有的业务情况下是满足需求的。但在高并发时会出错。
        info.setRandom("");
        phoneLoginInfoService.updatePhoneLoginRandom(info);
        //查找符合条件的用户
        User user = userService.findUserByPersonId(info.getPersonId());
        if(user!=null ){
            //如果用户存在 设置登陆用户
            setSessionAttribute("loginUser",user);
            //添加用户信息到页面上
            modelAndView.addObject("user", user);
            //根据当前登录用户获取其单位列表结构（输入用户所在单位为根单位，显示根单位的所有下级单位）
            List<ZTree> tree =  unitService.findUnitMenu(user.getUnit_id());
            if(tree==null){
                //如果查到的单位列表为空 跳转到错误提示页面
                modelAndView.setViewName("error");
                modelAndView.addObject("message", "获取单位列表结构出错");
                //写入错误日志
                errorLogService.addErrorLog("MainController-signatureTrack-获取单位列表结构出错");
            }else{
                //将单位列表信息添加到页面
                modelAndView.addObject("unit_menu", FastJsonUtil.toJSONString(tree));
            }
        }else{
            errorLogService.addErrorLog("MainController-signatureTrack-没有符合条件的用户");
            modelAndView.setViewName("error");
        }
        //查询系统初始化信息
        SystemJurInit systemJurInit = systemService.findSystemInit(user.getUnit_id());

        if (systemJurInit==null){
            modelAndView.setViewName("error");
            errorLogService.addErrorLog("MainController-signatureTrack-系统未检测到授权信息");
            modelAndView.addObject("message", "系统未检测到授权信息");
        }
        //将授权信息中的系统名称传递到页面上用于展示
        modelAndView.addObject("SYSTEMNAME", systemJurInit.getSYSTEMNAME());
        //hostURL用于在主页进行跳转到平台主页面，制章系统和其他模块使用不同的端口
        modelAndView.addObject("hostURL", hostURL);
        return modelAndView;
    }
    /**
     * 由签章平台系统跳转入口 重庆中烟单点登录接口
     */
    @RequestMapping(value = "index.html", method = RequestMethod.GET)
    public ModelAndView SSO(String urlParam){
        //新建视图对象
        ModelAndView modelAndView = new ModelAndView();
        //设置返回页面
        modelAndView.setViewName("make_index");
        //判断随机数是否有参数传递过来
        String SYSUserId = "";
        try {
            SYSUserId = decodeSSO(urlParam);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if ("".equals(SYSUserId)||SYSUserId ==null){
            modelAndView.setViewName("error");
            modelAndView.addObject("message", "登录信息无效！");
            errorLogService.addErrorLog("MainController-signatureTrack-业务系统登录人员ID为空");
            return modelAndView;
        }
//        String personId = personService.findPersonIdBySYS("SYS001",SYSUserId);
        //查找符合条件的用户
        User user = userService.findUserByPersonId(SYSUserId);
        if(user!=null ){
            //如果用户存在 设置登陆用户
            setSessionAttribute("loginUser",user);
            //添加用户信息到页面上
            modelAndView.addObject("user", user);
            //根据当前登录用户获取其单位列表结构（输入用户所在单位为根单位，显示根单位的所有下级单位）
            List<ZTree> tree =  unitService.findUnitMenu(user.getUnit_id());
            if(tree==null){
                //如果查到的单位列表为空 跳转到错误提示页面
                modelAndView.setViewName("error");
                modelAndView.addObject("message", "获取单位列表结构出错");
                //写入错误日志
                errorLogService.addErrorLog("MainController-signatureTrack-获取单位列表结构出错");
            }else{
                //将单位列表信息添加到页面
                modelAndView.addObject("unit_menu", FastJsonUtil.toJSONString(tree));
            }
        }else{
            errorLogService.addErrorLog("MainController-signatureTrack-没有符合条件的用户");
            modelAndView.setViewName("error");
        }
        //查询系统初始化信息
        SystemJurInit systemJurInit = systemService.findSystemInit(user.getUnit_id());

        if (systemJurInit==null){
            modelAndView.setViewName("error");
            errorLogService.addErrorLog("MainController-signatureTrack-系统未检测到授权信息");
            modelAndView.addObject("message", "系统未检测到授权信息");
        }
        //将授权信息中的系统名称传递到页面上用于展示
        modelAndView.addObject("SYSTEMNAME", systemJurInit.getSYSTEMNAME());
        //hostURL用于在主页进行跳转到平台主页面，制章系统和其他模块使用不同的端口
        modelAndView.addObject("hostURL", hostURL);
        return modelAndView;
    }

    /**
     * 获取跳转页面的随机数信息
     * 用于作为系统模块间跳转的凭证
     * @param personId 人员ID
     * @return 访问后缀
     */
    @RequestMapping("/getJumpRandom.html")
    @ResponseBody
    public String getJumpRandom(String personId){
        if(personId == null || "".equals(personId)){
            return "error";
        }
        String str = getRandom16();
        PhoneLoginInfo info = new PhoneLoginInfo();
        info.setPersonId(personId);
        info.setRandom(str);
        Integer i = phoneLoginInfoService.updatePhoneLoginRandom(info);
        if(i==null || i<=0){
            return "error";
        }
        return str;
    }

    @RequestMapping("/getSystemInit")
    @ResponseBody
    public String getSystemInit(){
        SystemJurInit systemJurInit = systemService.findSystemInit();
        return FastJsonUtil.toJSONString(systemJurInit);
    }

}
