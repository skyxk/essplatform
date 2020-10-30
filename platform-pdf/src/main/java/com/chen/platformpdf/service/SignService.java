package com.chen.platformpdf.service;

import com.chen.platformpdf.bean.PdfLocation;
import com.chen.platformpdf.bean.VerifyResult;
import com.itextpdf.text.DocumentException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public interface SignService {

    List<VerifyResult> verifyPdfSign(File file);


    PdfLocation findLocationByKeyword(String keyword, String fileName,int num);

    PdfLocation findLocationByBookMark(String keyword, String fileName);

    PdfLocation findLocationByXY(float X, float Y,int pageNum);
    void addSeal(byte[] pdfPath,
                         byte[] picPath,
                         float width,
                         float heigth,
                         int pageNum,
                         float x,
                         float y,
                         byte[] pdxPath,
                         String pwd, String signSerialNum, FileOutputStream os) throws IOException, DocumentException;
    void addSeal1(byte[] pdfPath,
                                byte[] picPath,
                                float width,
                                float heigth,
                                int pageNum,
                                float x,
                                float y,
                                byte[] pfxPath,
                                String pwd,String signSerialNum,FileOutputStream os) throws IOException, DocumentException ;
    void addOverSeal(byte[] picPath,byte[] pfxPath,String pwd,String signSerialNum,
                            float width, float heigth,String documentCode) throws IOException, DocumentException;

    void addSeal_UK(byte[] pdfPath,
                  byte[] picPath,
                  float width,
                  float heigth,
                  int pageNum,
                  float x,
                  float y,
                  String cert,
                  String signSerialNum,FileOutputStream os,String documentCode) throws Exception;



}
