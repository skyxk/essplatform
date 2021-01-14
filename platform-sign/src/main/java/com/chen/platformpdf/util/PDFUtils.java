package com.chen.platformpdf.util;

import com.chen.core.bean.ESSPdfPage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ：chen
 * @date ：Created in 2019/9/27 15:13
 */
public class PDFUtils {

    public static List<ESSPdfPage> splitPDF(File pdfFile){
        String documentCode = pdfFile.getParentFile().getName();
        List<ESSPdfPage> result = new ArrayList<>();
        PDDocument document = null;
        try {
            Runnable runnable1 = new PDFUtils().dealMsg1(pdfFile);
            new Thread(runnable1).start();
            document = PDDocument.load(pdfFile);
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            int pageNum=0;
            for (PDPage page : document.getPages()) {
                pageNum++;
                ESSPdfPage pdf = new ESSPdfPage();
                pdf.setPageSize(getPageInfo(page));
                pdf.setPicPath(documentCode+"\\\\"+pdfFile.getName()+pageNum+".png");
                result.add(pdf);
            }
            BufferedImage bim = pdfRenderer.renderImageWithDPI(0, 192, ImageType.RGB);
            ImageIOUtil.writeImage(bim, pdfFile.getAbsolutePath()+1+".png", 192);
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * 获得传入Pdf页面对象获得页面大小信息
     * @param page
     * @return
     */
    public static String getPageInfo(PDPage page) {
        int width = (int) page.getMediaBox().getWidth();
        int height = (int) page.getMediaBox().getHeight();
        return width + "_" + height;
    }
    /**
     * 将印章数据中的大小mm转换为pdf内坐标大小 42 119 44 124
     */
    public static int convertCoordinate(int mm) {
        return (595*mm)/210;
    }

    //创建一个Runnable，重写run方法
    public Runnable dealMsg(BufferedImage bim,String absolutePath,int pageNum){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //保存分页为图片
                try {
                    if (!new File(absolutePath+pageNum+".png").exists()){
                        ImageIOUtil.writeImage(bim, absolutePath+pageNum+".png", 192);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        return runnable;
    }
    //创建一个Runnable，重写run方法
    public Runnable dealMsg1(File pdfFile){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String path = pdfFile.getAbsolutePath();
                String documentCode = path.split("\\\\")[2];
                PDDocument document = null;
                try {
                    document = PDDocument.load(pdfFile);
                    PDFRenderer pdfRenderer = new PDFRenderer(document);
                    int pageNum=0;
                    for (PDPage page : document.getPages()) {
                        BufferedImage bim = pdfRenderer.renderImageWithDPI(pageNum, 192, ImageType.RGB);
                        pageNum++;
                        Runnable runnable = new PDFUtils().dealMsg(bim,pdfFile.getAbsolutePath(),pageNum);
                        new Thread(runnable).start();
//                        ImageIOUtil.writeImage(bim, pdfFile.getAbsolutePath()+pageNum+".png", 192);
                    }
                    document.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        return runnable;
    }
}
