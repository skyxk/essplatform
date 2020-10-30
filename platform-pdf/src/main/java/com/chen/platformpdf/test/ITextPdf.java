package com.chen.platformpdf.test;


import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

/**
 * 采用pdf模板填充技术，pdf上嵌入pdf表单，设置表单名称，进行填充
 * @author Administrator
 *
 */
public class ITextPdf {

    public static void main(String[] args) throws Exception {
        FillForm();
    }

    public static void  FillForm() throws Exception {
        // 模板路径
        String templatePath = "D:/create-forms-sample.pdf";
        // 生成的新文件路径
        String newPDFPath = "D:/create-forms-sample1.pdf";
        PdfReader reader;
        FileOutputStream out;
        ByteArrayOutputStream bos;
        PdfStamper stamper;
        try{
            out = new FileOutputStream(newPDFPath);// 输出流到新的pdf,没有b2.pdf时会创建
            reader = new PdfReader(templatePath);// 读取pdf模板
            bos = new ByteArrayOutputStream();
            stamper = new PdfStamper(reader, bos);
            AcroFields form = stamper.getAcroFields();


////            使用iTextAsian.jar中的字体
//            BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",BaseFont.NOT_EMBEDDED);
////            使用Windows系统字体(TrueType)
//            BaseFont.createFont("C:/WINDOWS/Fonts/SIMYOU.TTF", BaseFont.IDENTITY_H,BaseFont.NOT_EMBEDDED);
////            使用资源字体(ClassPath)
//            BaseFont.createFont("/SIMYOU.TTF", BaseFont.IDENTITY_H,BaseFont.NOT_EMBEDDED);


            BaseFont bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.EMBEDDED);//加载字体
            //获取form表单的表单名称--》测试看能不能获得pdf文件中的Form表单的名称
            java.util.Iterator<String> it = form.getFields().keySet()
                    .iterator();
            while (it.hasNext())
            {
                String name = it.next().toString();
                System.out.println(name);
            }
            form.setFieldProperty("location", "textfont", bf, null);//设置字体
            form.setField("location", "啊实打实打算");

//            form.setFieldProperty("age", "textfont", bf, null);
//            form.setField("age", "1");
//            form.setFieldProperty("class", "textfont", bf, null);
//            form.setField("class","计算机科学与技术");


            stamper.setFormFlattening(true);// 如果为false那么生成的PDF文件还能编辑，一定要设为true
            stamper.close();

            Document doc = new Document();

            PdfCopy copy = new PdfCopy(doc, out);
            doc.open();
            PdfImportedPage importPage = null;
            for(int i = 1; i <= reader.getNumberOfPages(); i++) {
                importPage = copy
                        .getImportedPage(new PdfReader(bos.toByteArray()), i);
                copy.addPage(importPage);
            }
            doc.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (DocumentException e)
        {
            e.printStackTrace();
        }
    }
}
