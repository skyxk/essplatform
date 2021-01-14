package com.chen.platformpdf.util;
import java.io.File;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

public class WordToPdf {
    public static void main(String[] args) {
        wordToPDF("D:\\demo1.docx","D:\\demo1.pdf");
    }
    static final int wdFormatPDF = 17;// PDF 格式

    public static void wordToPDF(String docFileName,String toFileName){

        ActiveXComponent app = null;
        Dispatch doc = null;
        try {
            app = new ActiveXComponent("Word.Application");
            app.setProperty("Visible", new Variant(false));
            Dispatch docs = app.getProperty("Documents").toDispatch();
            for (int i=1;i<2;i++){
                long start = System.currentTimeMillis();
                doc = Dispatch.call(docs,  "Open" , docFileName).toDispatch();
                File tofile = new File(toFileName);
                if (tofile.exists()) {
                    tofile.delete();
                }
                Dispatch.call(doc,
                        "SaveAs",
                        toFileName, // FileName
                        wdFormatPDF);
                long end = System.currentTimeMillis();
                System.out.println("转换完成..用时：" + (end - start) + "ms.");
            }
        } catch (Exception e) {
            System.out.println("========Error:文档转换失败：" + e.getMessage());
        } finally {
            Dispatch.call(doc,"Close",false);
//            System.out.println("关闭文档");
            if (app != null)
                app.invoke("Quit", new Variant[] {});
        }
        //如果没有这句话,winword.exe进程将不会关闭
        ComThread.Release();

    }
}