package com.chen.platformweb.interceptor;

import com.chen.service.ISystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.chen.platformweb.utils.SessionUtil.getSessionAttribute;

/**
 * 登陆拦截器
 */
@Component
public class LoginHandlerInterceptor implements HandlerInterceptor {
    @Autowired
    private ISystemService systemService;

    @Value("${myConfig.hostURL}")
    public String hostURL;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //检查系统授权到期时间
        boolean result =  systemService.verifySystemTime();
        if (!result){
            response.sendRedirect(hostURL +"/EssSealPlatform/user/login");
            return false;
        }
        //获取请求的URL
        String url = request.getRequestURI();
        //URL:login.jsp是公开的;这个demo是除了login.jsp是可以公开访问的，其它的URL都进行拦截控制
        if(url.contains("index")||url.contains("essSealMake")||url.contains("error")||url.contains("register")
                ||url.contains("html") ||url.contains("download")||url.contains("static")||url.contains("css")
                ||url.contains("js")||url.contains("images")||url.contains("getUKType")||url.contains("write")
                ||url.contains(".do")||url.contains("getCertPair")){
            return true;
        }
        //拦截逻辑
        Object user = getSessionAttribute("loginUser");
        if (user == null) {
            //未登陆，返回登陆界面
            request.setAttribute("msg","没有权限请先登陆");
            response.sendRedirect(hostURL +"/EssSealPlatform/user/login");
            return false;
        } else {
            //已登陆，放行请求
            return true;
        }
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
