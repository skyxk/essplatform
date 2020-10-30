package com.chen.platformweb.controller;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static com.chen.core.util.StringUtils.getUUID;

/**
 * 自定义Servlet 为了解决在国产中间件中不兼容ajax上传文件到spring boot控制器的问题
 * 文件保存到指定的位置后返回相应的文件路径
 */
@WebServlet(name = "myServlet", urlPatterns = "/upload")
public class MyServlet  extends HttpServlet {
    @Value("${myConfig.tempFilepath}")
    public String tempFilepath;
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        DiskFileItemFactory fu = new DiskFileItemFactory();
        fu.setSizeThreshold(2 * 1024 * 1024);
        fu.setSizeThreshold(4096);
        ServletFileUpload upload = new ServletFileUpload(fu);
        upload.setHeaderEncoding("UTF-8");
        List<FileItem> fileItems = null;
        try {
            fileItems = upload.parseRequest(request);
            Iterator<FileItem> iter = fileItems.iterator();
            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();
                item.getString("UTF-8");
                //忽略其他不是文件域的所有表单信息
                if (!item.isFormField()) {
                    String name1 = item.getName();//获取上传的文件名
                    long size = item.getSize();//获取上传的文件大小(字节为单位)
                    if ((name1 == null || name1.equals("")) && size == 0) {
                        continue;//跳到while检查条件
                    }
                    int end = name1.length();
                    int begin = name1.lastIndexOf("\\");
                    String newName = name1.substring(begin + 1, end);
                    if (newName.length() == 0) {
                        System.out.println("上传文件导入异常，请重新上传...");
                        ServletOutputStream outputStream=response.getOutputStream();
                        outputStream.write("error".getBytes());
                        outputStream.close();
                    } else {
                        try {
                            String fileType = name1.split("\\.")[1];
                            String UUID = getUUID();
                            String filePath = tempFilepath + UUID+"."+fileType;
                            //保存文件
                            File savedFile = new File(filePath);//用原文件名，作为上传文件的文件名。“/code”为目标路径
                            item.write(savedFile);
                            item.delete();

                            ServletOutputStream outputStream=response.getOutputStream();
                            outputStream.write(filePath.getBytes());
                            outputStream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                            ServletOutputStream outputStream=response.getOutputStream();
                            outputStream.write("error".getBytes());
                            outputStream.close();
                        }
                    }
                }
            }
        } catch (FileUploadException e) {
            e.printStackTrace();
            ServletOutputStream outputStream=response.getOutputStream();
            outputStream.write("error".getBytes());
            outputStream.close();
        }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }
}