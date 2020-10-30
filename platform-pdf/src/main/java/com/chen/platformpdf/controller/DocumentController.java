package com.chen.platformpdf.controller;
import com.alibaba.fastjson.JSONObject;
import com.chen.entity.pdf.PDFDocument;
import com.chen.platformpdf.service.FileService;
import com.chen.service.IBusinessSysService;
import com.chen.service.IPersonService;
import com.chen.service.ISealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static com.chen.core.util.FileUtils.deleteEveryThing;
import static com.chen.core.util.StringUtils.getUUID;
/**
 * @author ：chen
 * @date ：Created in 2019/10/30 14:39
 */
@Controller
@RequestMapping(value="/document")
public class DocumentController {

    @Autowired
    private FileService fileService;
    @Autowired
    private IBusinessSysService businessSysService;

    @Value("${myConfig.pdfPath}")
    public String pdfPath ;

    @RequestMapping(value="/upload")
    @ResponseBody
    public String uploadDocument(MultipartFile document, HttpServletRequest request){
        JSONObject result = new JSONObject();
        result.put("code","0001");
        result.put("msg","未知错误");
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        //验证IP
        boolean checkIp = businessSysService.checkBusinessSysByIp(ip);
        //如果ip不存在
        //测试
        if (checkIp){
            result.put("code","0005");
            result.put("msg","服务器IP没有权限");
            return result.toString();
        }
        //判断上传的文件是否符合要求
        //获取原始文件名
        String fileName = document.getOriginalFilename();
        if (fileName!=null&&!"".equals(fileName)) {
            String fileType = fileName.split("\\.")[1];
            if ("pdf".equals(fileType) || "PDF".equals(fileType)) {
                //生成UUID作为文档编码
                String documentCode = getUUID();
                //保存文件
                boolean  saveFileResult = fileService.saveMultipartFile(document,documentCode);
                if (saveFileResult){
                    PDFDocument pdf = new PDFDocument();
                    pdf.setDocumentCode(documentCode);
                    pdf.setDocumentName(document.getOriginalFilename());
                    boolean aa = fileService.addPdfDocument(pdf);
                    if (aa){
                        result.put("code","0000");
                        result.put("msg",documentCode);
                    }else{
                        result.put("code","0004");
                        result.put("msg","文件保存失败！");
                    }
                }
            } else{
                result.put("code","0002");
                result.put("msg","文件类型错误");
            }
        }else{
            result.put("code","0003");
            result.put("msg","文件为空");
        }
        return result.toString();
    }
    @RequestMapping(value="/download")
    public void downloadDocument(String documentCode, HttpServletResponse response) throws IOException {

        File pdfFile = fileService.findPDFFileByDocumentCode(documentCode,2);

        PDFDocument pdfDocument = fileService.findPDFDocumentByCode(documentCode);
        //设置MIME类型
        response.setContentType("application/octet-stream");
        //或者为response.setContentType("application/x-msdownload");
        //设置头信息,设置文件下载时的默认文件名，同时解决中文名乱码问题
        response.addHeader("Content-disposition", "attachment;filename="+
                new String((pdfDocument.getDocumentName()).getBytes(),
                StandardCharsets.ISO_8859_1));
        InputStream inputStream=new FileInputStream(pdfFile);
        deleteEveryThing(pdfPath+""+documentCode);
        ServletOutputStream outputStream=response.getOutputStream();
        byte[] bs=new byte[1024];
        while((inputStream.read(bs)>0)){
            outputStream.write(bs);
        }
        outputStream.close();
        inputStream.close();

    }

}
