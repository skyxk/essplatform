package com.chen.platformweb.controller;

import com.chen.core.base.Constant;
import com.chen.entity.Department;
import com.chen.entity.Unit;
import com.chen.platformweb.service.IPowerService;
import com.chen.service.IDepartmentService;
import com.chen.service.IUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/unit/")
public class UnitController {
    @Autowired
    private  IUnitService unitService;
    @Autowired
    private IDepartmentService departmentService;
    @Autowired
    private IPowerService powerService;
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

    /**
     * 单位展示页默认页面
     */
    @RequestMapping(value="index", method = RequestMethod.GET)
    public ModelAndView unit_index() {
        //视图对象
        ModelAndView modelAndView = new ModelAndView();
        //设置返回视图
        modelAndView.setViewName("unit/index");
        return modelAndView;
    }
    /**
     * 单位展示页默认页面
     */
    @RequestMapping(value="page", method = RequestMethod.GET)
    public ModelAndView page(String openUnitId) {
        //视图对象
        ModelAndView modelAndView = new ModelAndView();
        //设置返回视图
        modelAndView.setViewName("unit/page");

        //判断是否有进入当前单位的权限
        if(powerService.checkIsHaveThisRangePower(session,unitService.findUnitById(openUnitId),Constant.topUnitLevel)){
            //查找本次访问的单位
            Unit unit = unitService.findUnitById(openUnitId);
            modelAndView.addObject("unit",unit);
            //查找本单位下的部门
            //根据要打开的单位ID查询当前单位下的部门列表
            Department department = new Department();
            department.setUnit_id(openUnitId);
            department.setState(Constant.STATE_YES);
            modelAndView.addObject("departmentList",  departmentService.findDepartmentList(department));

            //判断当前登录用户对于各个按钮的权限
            //审核
            if(powerService.getPowerForButton(session,"makeSealPower_auditSeal")){
                modelAndView.addObject("reviewButton",  true);
            }else {
                modelAndView.addObject("reviewButton",  false);
            }
            //制作
            if(powerService.getPowerForButton(session,"makeSealPower_makeSeal")){
                modelAndView.addObject("makeButton",  true);
            }else {
                modelAndView.addObject("makeButton",  false);
            }
            //制作
            if(powerService.getPowerForButton(session,"makeSealPower_makeSeal")){
                modelAndView.addObject("makeButton",  true);
            }else {
                modelAndView.addObject("makeButton",  false);
            }
        }else {
            modelAndView.addObject("message",  "您不能访问此单位");
            modelAndView.setViewName("error");
        }
        return modelAndView;
    }
}
