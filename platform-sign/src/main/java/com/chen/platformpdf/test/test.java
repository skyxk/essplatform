package com.chen.platformpdf.test;

import com.alibaba.fastjson.JSONObject;
import com.chen.core.bean.HttpResult;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.chen.core.util.Base64Utils.ESSGetBase64Decode;
import static com.chen.core.util.Base64Utils.encodeBase64File;
import static com.chen.core.util.HttpHelper.executePost;
import static com.chen.core.util.HttpHelper.executeUploadFile;
import static java.lang.Thread.sleep;

public class test {
    public int j=0;
    //创建一个Runnable，重写run方法
    public Runnable dealMsg1(String pdfFile){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                HttpResult ht = null;
                HttpResult ht1 =null;
                try {
                    ht = executeUploadFile(null,"http://123.56.11.240:8080/essplatformpdf/document/upload",
                            pdfFile,
                            "document","UTF-8",true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                JSONObject js = JSONObject.parseObject(ht.getContent());
                Map<String,String> map = new HashMap<>();
                map.put("data","{\n" +
                        "    \"documentCode\":\""+js.getString("msg")+"\",\n" +
                        "    \"signUser\":\"0251022016295421464782bcb2e\",\n" +
                        "    \"businessSys\":\"025bs1\",\n" +
                        "    \"seals\":[\n" +
                        "        {\n" +
                        "            \"X\":\"200\",\n" +
                        "            \"Y\":\"200\",\n" +
                        "            \"pageNum\":\"1\",\n" +
                        "            \"unitName\":\"响水县局\",\n" +
                        "            \"sealName\":\"江苏省响水县烟草专卖局章\"\n" +
                        "        }\n" +
                        "    ]\n" +
                        "}");
                try {
                    ht1 = executePost("http://123.56.11.240:8080/essplatformpdf/signPdfByXY", map, "UTF-8", 300000);
                    JSONObject js1 = JSONObject.parseObject(ht.getContent());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(ht1);
            }
        };
        return runnable;
    }
    public static void main(String[] args) throws InterruptedException {
//        Runnable runnable = new test().dealMsg1("D:\\pdf\\许可证.pdf");
//        for (int i =0;i<200;i++){
//            new Thread(runnable).start();
//        }

        try {
            System.out.println(encodeBase64File("D:\\test.cer"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
