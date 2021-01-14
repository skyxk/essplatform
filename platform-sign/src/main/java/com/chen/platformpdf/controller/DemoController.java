package com.chen.platformpdf.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chen.core.bean.ESSPdfPage;
import com.chen.entity.Business_System;
import com.chen.entity.Person;
import com.chen.entity.Seal;
import com.chen.platformpdf.bean.SealBean;
import com.chen.platformpdf.service.FileService;
import com.chen.platformpdf.service.PowerService;
import com.chen.platformpdf.service.SignService;
import com.chen.service.IBusinessSysService;
import com.chen.service.IPersonService;
import com.chen.service.ISealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.chen.core.util.Base64Utils.ESSGetBase64Decode;
import static com.chen.core.util.FileUtils.File2byte;
import static com.chen.core.util.FileUtils.copy;

@Controller
@RequestMapping(value="/demo")
public class DemoController {


    @Autowired
    private FileService fileService;
    @Autowired
    private SignService signService;
    @Autowired
    private ISealService sealService;
    @Autowired
    private IBusinessSysService businessSysService;
    @Autowired
    private IPersonService personService;
    @Autowired
    private PowerService powerService;
    @Value("${myConfig.pdfPath}")
    public String pdfPath ;




}
