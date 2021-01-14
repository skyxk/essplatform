package com.chen.platformpdf.util;

import com.aspose.words.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

public class DocConvertPdf {
    public static void main(String[] args) throws FileNotFoundException {

        FileOutputStream os  = new FileOutputStream(new File("D:\\demo.pdf"));
        docTurnPdf("D:\\demo1.docx",os);
    }
    /**
     * doc转pdf
     * @param sourceFileName doc文档的路径 如：C:\Users\weipc\Desktop\html\询问笔录.doc
     * @param os 将要生成的pdf路径 如：C:\Users\weipc\Desktop\html\询问笔录.pdf
     * @throws Exception
     */
    public static boolean docTurnPdf(String sourceFileName,FileOutputStream os) {
        if (!getLicense()) {// 验证License 若不验证则转化出的pdf文档会有水印产生
            return false;
        }
        try {
            long l1 = System.currentTimeMillis();
            Document doc = new Document(sourceFileName);//Address是将要被转化的word文档
            doc.save(os, SaveFormat.PDF);//全面支持DOC, DOCX, OOXML, RTF HTML, OpenDocument, PDF, EPUB, XPS, SWF 相互转换
            os.close();
            File f = new File(sourceFileName);
//            if(f.exists()){
//                f.delete();
//            }
            long l2 = System.currentTimeMillis();
            System.out.println(l2-l1);
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public static boolean getLicense(){
        boolean result = false;
        try {
            InputStream is = DocConvertPdf.class.getClassLoader().getResourceAsStream("license.xml"); //Test要替换成当前类名  license.xml应放在..\WebRoot\WEB-INF\classes路径下
            License aposeLic = new License();
            aposeLic.setLicense(is);
//            FontSettings.setFontsFolder("/usr/share/fonts/chinese", false);//设置字体文件夹
            FontSettings.setFontsFolder("C:\\Windows\\Fonts", false);//设置字体文件夹
            is.close();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
