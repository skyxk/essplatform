package com.chen.platformpdf.service.impl;

import com.chen.core.bean.ESSPdfPage;
import com.chen.platformpdf.util.PDFUtils;
import com.chen.dao.PDFDocumentMapper;
import com.chen.entity.pdf.PDFDocument;
import com.chen.platformpdf.service.FileService;
import com.chen.platformpdf.util.ProcessPDF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import static com.chen.core.util.DateUtils.getDate;
import static com.chen.core.util.FileUtils.File2byte;
import static com.chen.core.util.FileUtils.copy;
import static com.chen.platformpdf.util.DocConvertPdf.docTurnPdf;

/**
 * @author ：chen
 * @date ：Created in 2019/9/27 14:15
 */
@Service
public class FileServiceImpl implements FileService {

    @Value("${myConfig.pdfPath}")
    public String pdfPath;
    @Autowired
    private PDFDocumentMapper documentMapper;
    @Override
    public boolean saveMultipartFile(MultipartFile file, String fileName){
        boolean result = false;
        if (file!=null&&fileName!=null){
            try {
                File filePath  = new File(pdfPath+fileName);
                if(!filePath.exists()){
                    filePath.mkdirs();
                }
                String fileNameTemp = pdfPath+fileName+"\\temp"+".pdf";
                file.transferTo(new File(fileNameTemp));
                //复制出签章临时文档
                copy(new File(fileNameTemp),new File(fileNameTemp+"1"));
                result = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public boolean saveMultipartFileDoc(MultipartFile file, String fileName){
        boolean result = false;
        if (file!=null&&fileName!=null){
            try {
                File filePath  = new File(pdfPath+fileName);
                if(!filePath.exists()){
                    filePath.mkdirs();
                }
                String fileNameTemp = pdfPath+fileName+"\\temp"+".doc";
                file.transferTo(new File(fileNameTemp));
                FileOutputStream os  = new FileOutputStream(new File( pdfPath+fileName+"\\temp"+".pdf"));
                boolean result1 = docTurnPdf(fileNameTemp,os);
                if (!result1){
                    return false;
                }
                fileNameTemp = pdfPath+fileName+"\\temp"+".pdf";
                //复制出签章临时文档
                copy(new File(fileNameTemp),new File(fileNameTemp+"1"));
                result = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public File findPDFFileByDocumentCode(String DocumentCode,int type){
        String  path = "";
        if (type == 1){
            path = pdfPath+DocumentCode+"\\temp"+".pdf";
        }else if(type == 2){
            path = pdfPath+DocumentCode+"\\temp"+".pdf1";
        }

        return new File(path);
    }

    @Override
    public List<ESSPdfPage> splitPDFToImages(File pdfFile) {
        List<ESSPdfPage> result;
        result = PDFUtils.splitPDF(pdfFile);
        return result;

    }
    @Override
    public boolean addPdfDocument(PDFDocument pdf) {
        int i = documentMapper.addPDFDocument(pdf);
        if (i==1){
            return true;
        }
        return false;
    }

    @Override
    public PDFDocument findPDFDocumentByCode(String documentCode) {
        return documentMapper.findPDFDocumentByCode(documentCode);
    }
}
