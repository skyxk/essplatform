package com.chen.platformpdf;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages="com.chen")
@MapperScan("com.chen.dao")
public class PlatformSignApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlatformSignApplication.class, args);
    }

}
