package com.chen.platformpdf.service;

import com.chen.platformpdf.bean.PdfLocation;
import com.chen.platformpdf.bean.VerifyResult;
import com.chen.platformpdf.dto.SealDto;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.security.ExternalSignature;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.cert.Certificate;
import java.util.List;

public interface SignService {

    List<VerifyResult> verifyPdfSign(byte[] file);
    PdfLocation findLocationByKeyword(String keyword, String fileName,int num);
    PdfLocation findLocationByBookMark(String keyword, String fileName);
    PdfLocation findLocationByXY(float X, float Y,int pageNum);

    boolean addSeal(byte[] pdfPath, byte[] imgFile,int width, int height ,int pageNum, float x, float y, SealDto sealDto, FileOutputStream os) throws IOException ;

    void addSealByRSA(byte[] pdfPath,
                      byte[] picPath,
                      float width,
                      float heigth,
                      int pageNum,
                      float x,
                      float y,
                      Certificate[] chain, ExternalSignature es, String signSerialNum, FileOutputStream os) throws IOException, DocumentException;

    void addOverSeal(SealDto sealDto,String documentCode) throws IOException, DocumentException;

    void addSeal_UK(byte[] pdfPath,
                  byte[] picPath,
                  float width,
                  float heigth,
                  int pageNum,
                  float x,
                  float y, Certificate[] chain,
                  String signSerialNum,FileOutputStream os,String documentCode) throws Exception;



}
