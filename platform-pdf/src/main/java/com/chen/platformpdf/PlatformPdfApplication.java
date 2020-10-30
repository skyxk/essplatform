package com.chen.platformpdf;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication(scanBasePackages="com.chen")
@MapperScan("com.chen.dao")
public class PlatformPdfApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlatformPdfApplication.class, args);
    }

}
