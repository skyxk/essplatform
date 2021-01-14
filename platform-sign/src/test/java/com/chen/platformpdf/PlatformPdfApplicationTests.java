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

import static com.chen.core.util.FileUtils.File2byte;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PlatformPdfApplicationTests {

    @Autowired
    private SignService signService;

    @Autowired
    public SealMapper sealMapper;
    @Test
    public void contextLoads() {
        File file = new File("D:\\Downloads\\demo (3).pdf");
        List<VerifyResult> s =  signService.verifyPdfSign(File2byte(file));
        System.out.println(s.toString());
    }
}
