package com.chen.platformpdf;

import com.chen.dao.SealMapper;
import com.chen.platformpdf.bean.VerifyResult;
import com.chen.platformpdf.service.SignService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PlatformPdfApplicationTests {

    @Autowired
    private SignService signService;

    @Autowired
    public SealMapper sealMapper;
    @Test
    public void contextLoads() {
        File file = new File("D:\\Downloads\\0cb3105e9ed74e5ea92b77d6ed0ad9fa.pdf");
        List<VerifyResult> s =  signService.verifyPdfSign(file);
        System.out.println(s);

    }
}
