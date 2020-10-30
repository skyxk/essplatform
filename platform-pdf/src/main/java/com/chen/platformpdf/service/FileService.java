package com.chen.platformpdf.service;

import com.chen.core.bean.ESSPdfPage;
import com.chen.entity.pdf.PDFDocument;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface FileService {

    /**
     * 保存上传的文件，并使用documentCode作为存放文件夹
     * 需要保存文件的同时再保存一个用于签章的文件复制
     * @param file 文件
     * @param documentCode 文档编码
     * @return 保存结果
     */
     boolean saveMultipartFile(MultipartFile file, String documentCode);
     /**
      * 用与查找存放于硬盘上的pdf文件
      * @param DocumentCode 文档编码
      * @param type 类型 1 查找原文件 2 查找签章文件
      * @return
      */
     File findPDFFileByDocumentCode(String DocumentCode,int type);

    /**
     * 将传入的PDF文件处理成图片集合
     * @param pdfFile PDF文件
     * @return 图片集合
     */
     List<ESSPdfPage> splitPDFToImages(File pdfFile);

     boolean addPdfDocument(PDFDocument pdf);

     PDFDocument findPDFDocumentByCode(String documentCode);
}
