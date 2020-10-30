package com.chen.platformweb;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
/**
 * @author mrche
 */
@SpringBootApplication(scanBasePackages="com.chen")
//扫描自定义servlet
@ServletComponentScan
//开启缓存
@EnableCaching
//指定扫描mapper资源，缺失会导致无法注入
@MapperScan("com.chen.dao")
public class PlatformWebApplication   {
    public static void main(String[] args) {
        long time = System.currentTimeMillis();
        SpringApplication.run(PlatformWebApplication.class, args);
        System.out.println("===应用启动耗时："+(System.currentTimeMillis()-time)/1000+"s===");
    }
}
