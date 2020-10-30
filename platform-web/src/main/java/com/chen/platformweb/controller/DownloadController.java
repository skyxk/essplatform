package com.chen.platformweb.controller;

import com.chen.entity.Apply;
import com.chen.entity.Seal;
import com.chen.entity.UKDll;
import com.chen.platformweb.service.IFileService;
import com.chen.service.IApplyService;
import com.chen.service.ISealService;
import com.chen.service.ISystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
import java.util.List;

import static com.chen.core.util.StringUtils.getUUID;

/**
 * 实现 和客户端交互接口 大多数废弃或在其他项目里有应用
 */
@Controller
@RequestMapping("/download/")
public class DownloadController {
    @Autowired
    private ISealService sealService;
    @Autowired
    private IApplyService applyService;
    @Value("${myConfig.tempFilepath}")
    public String tempFilepath;
    /**
     * 根据 UKID 获取印章列表
     */
    @RequestMapping(value ="getSealList")
    @ResponseBody
    public String getSealList(String UKID) {
        StringBuffer stringBuffer = new StringBuffer();
        List<Seal> sealList = sealService.findSealListByUKId(UKID);
        if (sealList.size() >= 4) {//判断list长度
            sealList= sealList.subList(0, 3);//取前3条数据
        } else {
            sealList = sealList;
        }
        for(Seal seal :sealList){
            stringBuffer.append(seal.getSeal_id());
            stringBuffer.append("@@@@");
            stringBuffer.append(seal.getSeal_name());
            stringBuffer.append("####");
        }
        return stringBuffer.toString();
    }
    /**
     * 废弃
     */
    @RequestMapping(value ="getSeal")
    @ResponseBody
    public String getSeal(String sealId) {
        Seal seal = sealService.findSealById(sealId);
        return seal.getSealImg().getImg_jpg();
    }

    /**
     * 提供上传附件的下载功能  本系统内的文件上传和下载只适合单机模式
     * 多服务器请考虑文件服务器模式
     */
    @RequestMapping(value="attachment", method = RequestMethod.GET)
    public void attachment(String applyId, HttpServletResponse response) throws IOException {
        File file = null;
        Apply sealApply = applyService.findApplyById(applyId);
        if(sealApply==null){
            //设置MIME类型
            response.setContentType("application/json");
            ServletOutputStream outputStream=response.getOutputStream();
            outputStream.write(("error！").getBytes());
            outputStream.close();
        }else{
            file = new File(sealApply.getTemp_file());
            String[] a = sealApply.getTemp_file().split("\\\\");
            //设置MIME类型
            response.setContentType("application/octet-stream");
            //或者为response.setContentType("application/x-msdownload");
            //设置头信息,设置文件下载时的默认文件名，同时解决中文名乱码问题
            response.addHeader("Content-disposition", "attachment;filename="+new String(a[a.length-1].getBytes(),
                    StandardCharsets.ISO_8859_1));
            InputStream inputStream=new FileInputStream(file);
            ServletOutputStream outputStream=response.getOutputStream();
            byte[] bs=new byte[1024];
            while((inputStream.read(bs)>0)){
                outputStream.write(bs);
            }
            outputStream.close();
            inputStream.close();
        }
    }
    @RequestMapping(value ="uploadFile",method = RequestMethod.POST)
    @ResponseBody
    public String uploadFile(HttpServletRequest request, HttpServletResponse response,MultipartFile upload_file) throws IOException{
        response.setHeader("Access-Control-Allow-Origin","*");
        String filename = "";
        if (upload_file!=null) {
            //保存文件并获取文件储存路径
            try {
                // 文件保存路径
                String fileName = upload_file.getOriginalFilename();
                String fileType = fileName.split("\\.")[1];
                String UUID = getUUID();
                String filePath = tempFilepath + UUID+"."+fileType;
                // 转存文件
                upload_file.transferTo(new File(filePath));
                return filePath;
            } catch (Exception e) {
                e.printStackTrace();
                return "error1";
            }
        }else{
            return "error2";
        }
    }
    @RequestMapping(value ="getSealInfo")
    @ResponseBody
    public String getSealInfo(String SEALID) {
        Seal seal = sealService.findSealById(SEALID);
        return seal.getUsb_key_info();
    }
    /**
     * 这个接口在制章时用到了 通过ID获取需要UK型号代码
     * @param id
     * @return
     */
    @RequestMapping(value ="getUKTypeCodeById")
    @ResponseBody
    public String getUKTypeCodeById(String id) {
        UKDll ukDll = sealService.getUKTypeById(id);
        return ukDll.getType_code();
    }
}