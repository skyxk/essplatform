package com.chen.platformpdf.util;


import com.itextpdf.text.*;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.chen.core.util.FileUtils.File2byte;

public class ProcessPDF {
    public static void main(String[] args) throws Exception {
        String path = "D:\\demo1.pdf";
        File pdfFile = new File(path);
        long aa =System.currentTimeMillis();
        addPdfTextMarkFull(pdfFile,"罗锐",0.1f);
        long bb =System.currentTimeMillis();
        System.out.println(bb-aa);
        addPdfQRCode(pdfFile,"asdaadabsdfsdd345234dfsdfase12312asdasd23123",10,10);
    }

    /**
     *  给pdf文件添加图像水印
     * @param pdfFile
     *            要加水印的原pdf文件路径
     * @param markImage
     *            水印图片路径
     * @param imgWidth
     *            图片横坐标
     * @param imgHeight
     *            图片纵坐标
     * @throws Exception dd
     */
    public static void addPdfImgMark(File pdfFile, byte[] markImage,int imgWidth, int imgHeight) throws Exception {
        byte[] pdfByte = File2byte(pdfFile);
        PdfReader reader = new PdfReader(pdfByte, "PDF".getBytes());
        FileOutputStream os  = new FileOutputStream(pdfFile);
        PdfStamper stamp = new PdfStamper(reader, os);
        PdfContentByte under;
        PdfGState gs1 = new PdfGState();
        gs1.setFillOpacity(0.3f);// 透明度设置
        Image img = Image.getInstance(markImage);// 插入图片水印
        img.setAbsolutePosition(imgWidth, imgHeight); // 坐标
        img.setRotation(-20);// 旋转 弧度
        img.setRotationDegrees(45);// 旋转 角度
        // img.scaleAbsolute(200,100);//自定义大小
        // img.scalePercent(50);//依照比例缩放
        int pageSize = reader.getNumberOfPages();// 原pdf文件的总页数
        for(int i = 1; i <= pageSize; i++) {
            under = stamp.getUnderContent(i);// 水印在之前文本下
            // under = stamp.getOverContent(i);//水印在之前文本上
            under.setGState(gs1);// 图片水印 透明度
            under.addImage(img);// 图片水印
        }
        stamp.close();// 关闭
    }

    /**
     *  给pdf文件添加二维码水印
     * @param pdfFile
     *            要加水印的原pdf文件路径
     * @param markInfo
     *            二维码内容
     * @param imgWidth
     *            图片横坐标
     * @param imgHeight
     *            图片纵坐标
     * @throws Exception 异常
     */
    public static void addPdfQRCode(File pdfFile, String markInfo,int imgWidth, int imgHeight) throws Exception {
//        byte[] pdfByte = File2byte(pdfFile);
//        PdfReader reader = new PdfReader(pdfByte, "PDF".getBytes());
//        FileOutputStream os  = new FileOutputStream(pdfFile);
//        PdfStamper stamp = new PdfStamper(reader, os);
//        try{
//            PdfContentByte under;
//            PdfGState gs1 = new PdfGState();
//            gs1.setFillOpacity(0.9f);// 透明度设置
//            BarcodeQRCode barcodeQRCode = new BarcodeQRCode(markInfo, 1000, 1000, null);
//            Image img = barcodeQRCode.getImage();
//            img.scaleAbsolute(100, 100);//自定义大小
//            img.setAbsolutePosition(imgWidth, imgHeight); // 坐标
//            img.setRotation(0);// 旋转 弧度
//            img.setRotationDegrees(0);// 旋转 角度
//            // img.scalePercent(50);//依照比例缩放
//            int pageSize = reader.getNumberOfPages();// 原pdf文件的总页数
//            under = stamp.getUnderContent(pageSize);// 水印在之前文本下
//            // under = stamp.getOverContent(i);//水印在之前文本上
//            under.setGState(gs1);// 图片水印 透明度
//            under.addImage(img);// 图片水印
//            stamp.close();// 关闭
//        }catch (Exception e){
//            e.printStackTrace();
//        }finally {
//            stamp.close();
//            reader.close();
//            if (os != null) {
//                os.close();
//            }
//        }
    }
    /**
     * 给pdf文件添加文字水印
     *
     * @param pdfFile
     *            要加水印的原pdf文件路径
     * @param textMark
     *            水印文字
     * @param textWidth
     *            文字横坐标
     * @param textHeight
     *            文字纵坐标
     * @throws Exception
     */
    public static boolean addPdfTextMark(File pdfFile,String textMark,int textWidth,
                                      int textHeight) throws Exception {
        byte[] pdfByte = File2byte(pdfFile);
        PdfReader reader = new PdfReader(pdfByte, "PDF".getBytes());
        FileOutputStream os  = new FileOutputStream(pdfFile);
        PdfStamper stamp = new PdfStamper(reader, os);
        try{
            PdfContentByte under;
            BaseFont font = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H",
                    true);
            int pageSize = reader.getNumberOfPages();// 原pdf文件的总页数
            for(int i = 1; i <= pageSize; i++) {
                under = stamp.getUnderContent(i);// 水印在之前文本下
                // under = stamp.getOverContent(i);//水印在之前文本上
                under.beginText();
                // 设置水印透明度
                PdfGState gs = new PdfGState();
                // 设置填充字体不透明度为0.4f
                gs.setFillOpacity(0.1f);
                under.setGState(gs);
                under.setColorFill(BaseColor.DARK_GRAY);// 文字水印 颜色
                under.setFontAndSize(font, 12);// 文字水印 字体及字号
                under.setTextMatrix(textWidth, textHeight);// 文字水印 起始位置
                under.showTextAligned(Element.ALIGN_CENTER, textMark, textWidth, textHeight, 0);
                under.endText();
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            stamp.close();
            reader.close();
            if (os != null) {
                os.close();
            }
        }
        return true;
    }
    /**
     * 给pdf文件添加文字水印 整文档
     *
     * @param pdfFile
     *            要加水印的原pdf文件路径
     * @param textMark
     *            水印文字
     * @throws Exception
     */
    public static boolean addPdfTextMarkFull(File pdfFile,String textMark,float Opacity) throws Exception {
//        byte[] pdfByte = File2byte(pdfFile);
//        PdfReader reader = new PdfReader(pdfByte, "PDF".getBytes());
//        FileOutputStream os  = new FileOutputStream(pdfFile);
//        PdfStamper stamp = new PdfStamper(reader, os);
//        try{
//            PdfContentByte under;
//            BaseFont font = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H",
//                    true);
//            int pageSize = reader.getNumberOfPages();// 原pdf文件的总页数
//            for(int i = 1; i <= pageSize; i++) {
//                under = stamp.getUnderContent(i);// 水印在之前文本下
//                // under = stamp.getOverContent(i);//水印在之前文本上
//                under.beginText();
//                // 设置水印透明度
//                PdfGState gs = new PdfGState();
//                // 设置填充字体不透明度为0.4f
//                gs.setFillOpacity(Opacity);
//                under.setGState(gs);
//                under.setColorFill(BaseColor.DARK_GRAY);// 文字水印 颜色
//                under.setFontAndSize(font, 12);// 文字水印 字体及字号
//                for (int a = 10;a<595;a+=100){
//                    for (int b = 10;b<841;b+=100) {
//                        under.setTextMatrix(a, b);// 文字水印 起始位置
//                        under.showTextAligned(Element.ALIGN_CENTER, textMark, a, b, 45);
//                    }
//                }
//                under.endText();
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//            return false;
//        }finally {
//            stamp.close();
//            reader.close();
//            if (os != null) {
//                os.close();
//            }
//        }
        return true;
    }
    /**
     * 合并pdf
     * @param streamOfPDFFiles
     * @param outputStream
     * @param paginate
     */
    public static void concatPDFs(List<InputStream> streamOfPDFFiles,
                                  OutputStream outputStream, boolean paginate) {
        Document document = new Document();
        try {
            List<InputStream> pdfs = streamOfPDFFiles;
            List<PdfReader> readers = new ArrayList<PdfReader>();
            int totalPages = 0;
            Iterator<InputStream> iteratorPDFs = pdfs.iterator();

            // Create Readers for the pdfs.
            while (iteratorPDFs.hasNext()) {
                InputStream pdf = iteratorPDFs.next();
                PdfReader pdfReader = new PdfReader(pdf);
                readers.add(pdfReader);
                totalPages += pdfReader.getNumberOfPages();
            }
            // Create a writer for the outputstream
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);

            document.open();
            BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA,
                    BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            PdfContentByte cb = writer.getDirectContent(); // Holds the PDF
            // data

            PdfImportedPage page;
            int currentPageNumber = 0;
            int pageOfCurrentReaderPDF = 0;
            Iterator<PdfReader> iteratorPDFReader = readers.iterator();

            // Loop through the PDF files and add to the output.
            while (iteratorPDFReader.hasNext()) {
                PdfReader pdfReader = iteratorPDFReader.next();

                // Create a new page in the target for each source page.
                while (pageOfCurrentReaderPDF < pdfReader.getNumberOfPages()) {
                    document.newPage();
                    pageOfCurrentReaderPDF++;
                    currentPageNumber++;
                    page = writer.getImportedPage(pdfReader,
                            pageOfCurrentReaderPDF);
                    cb.addTemplate(page, 0, 0);

                    // Code for pagination.
                    if (paginate) {
                        cb.beginText();
                        cb.setFontAndSize(bf, 9);
                        cb.showTextAligned(PdfContentByte.ALIGN_CENTER, ""
                                        + currentPageNumber + " of " + totalPages, 520,
                                5, 0);
                        cb.endText();
                    }
                }
                pageOfCurrentReaderPDF = 0;
            }
            outputStream.flush();
            document.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (document.isOpen())
                document.close();
            try {
                if (outputStream != null)
                    outputStream.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    public static File mergeFiles(String[] files, String result, boolean smart) throws IOException, DocumentException {
        Document document = new Document();
        PdfCopy copy;
        if (smart)
            copy = new PdfSmartCopy(document, new FileOutputStream(result));
        else
            copy = new PdfCopy(document, new FileOutputStream(result));
        document.open();
        PdfReader[] reader = new PdfReader[files.length];
        for (int i = 0; i < files.length; i++) {
            reader[i] = new PdfReader(files[i]);
            copy.addDocument(reader[i]);
            copy.freeReader(reader[i]);
            reader[i].close();
        }
        document.close();
        return new File(result);
    }
}
