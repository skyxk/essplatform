package com.chen.platformweb.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * web请求和返回数据日志切面
 */
@Aspect
@Component
public class WebLogAspect {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    ThreadLocal<Long> startTime = new ThreadLocal<>();
    /**
     * 通过@Pointcut定义的切入点为com.chen.platformweb.controller
     * 包下的所有函数（对web层所有请求处理做切入点）记录所有对Controller接口访问的
     * 信息，包括IP，URL等
     */
    @Pointcut("execution(public * com.chen.platformweb.controller..*.*(..))")
    public void webLog(){}

    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        //记录处理起始时间
        startTime.set(System.currentTimeMillis());
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = null;
        if (attributes != null) {
            request = attributes.getRequest();
            // 记录下请求内容
            String requestInfo = "URL : " + request.getRequestURL().toString()+ "||"
                    +"HTTP_METHOD : " + request.getMethod()+ "||"
                    +"IP : " + request.getRemoteAddr()+ "||"
                    +"CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName()+"||"
                    +"ARGS : " + Arrays.toString(joinPoint.getArgs());
            logger.info("requestInfo : " + requestInfo);

        }else{
            logger.info("request ： " + "request == null");
        }
    }

    /**
     * 记录接口接收数据和返回数据的AOP内容
     */
    @AfterReturning(returning = "ret", pointcut = "webLog()")
    public void doAfterReturning(Object ret) throws Throwable {
        // 处理完请求，返回内容
        logger.info("RESPONSE : " + ret);
        //打印处理时间
        logger.info("SPEND TIME : " + (System.currentTimeMillis() - startTime.get()));
    }

}