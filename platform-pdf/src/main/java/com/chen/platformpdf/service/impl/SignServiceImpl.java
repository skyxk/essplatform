package com.chen.platformpdf.service.impl;

import com.chen.core.util.Base64Utils;
import com.chen.core.util.CutImageUtil;
import com.chen.platformpdf.service.FileService;
import com.chen.platformpdf.util.Location;
import com.chen.entity.IssuerUnit;
import com.chen.platformpdf.bean.PdfLocation;
import com.chen.platformpdf.bean.VerifyResult;
import com.chen.platformpdf.service.SignService;
import com.chen.service.IIssuerUnitService;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.security.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.chen.core.util.Base64Utils.ESSGetBase64Decode;
import static com.chen.core.util.FileUtils.File2byte;
import static com.chen.core.util.FileUtils.byte2Input;

import static com.chen.platformpdf.test.demo.getPublicKey;
import static com.chen.platformpdf.util.GetLocation.*;

/**
 * @author ：chen
 * @date ：Created in 2019/9/27 9:33
 */
@Service
public class SignServiceImpl implements SignService {
    @Autowired
    private IIssuerUnitService issuerUnitService;
    @Autowired
    private FileService fileService;
    @Override
    public void addOverSeal(byte[] picPath,byte[] pfxPath,String pwd,String signSerialNum,
                                   float width, float heigth,String documentCode) throws IOException, DocumentException {
        File pdfFile = fileService.findPDFFileByDocumentCode(documentCode,2);
        byte[] pdfFileByte = File2byte(pdfFile);
        //PDF骑缝章签署过程
        //首先处理签章图片，
        //获取签章的页数
        PDDocument document = PDDocument.load(pdfFileByte);
        int pageNum = document.getNumberOfPages();
        PDPageTree pages = document.getPages();
        document.close();
        if (pageNum>1){
            //处理图片，将图片按照pdf页数等分，并获取图片宽度和长度。
            List<String> ImageBase64List = CutImageUtil.cutImageToBase64(picPath,pageNum);
            //每页的签章位置 需要知道 每页的签章图片和签章起始坐标
            for(int num = 0;num<pageNum;num++){
                PDPage p = pages.get(num);
                int pWidth = (int) p.getMediaBox().getWidth();
                int pHeight = (int) p.getMediaBox().getHeight();
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(ESSGetBase64Decode(
                        ImageBase64List.get(num)));
                BufferedImage source = ImageIO.read(byteArrayInputStream);
                int cWidth = source.getWidth();
                int cHeigth = source.getHeight();
                cWidth = (int) width/pageNum;
                cHeigth = (int) heigth;
                //计算签章其实坐标
                int x = pWidth-cWidth/2;
//                int x = cWidth/2+30;
                int y = pHeight/2;
                File pdfFile_1 = fileService.findPDFFileByDocumentCode(documentCode,2);
                byte[] pdfFileByte_1 = File2byte(pdfFile);
                FileOutputStream os  = new FileOutputStream(new File(pdfFile_1.getAbsolutePath()));
                addSeal(pdfFileByte_1, ESSGetBase64Decode(ImageBase64List.get(num)),cWidth,cHeigth,
                        num+1,x,y,pfxPath,pwd,signSerialNum,os);
                //骑缝章有一个签署不成功则离开循环，签章错误
            }
        }
    }
    /**
     *签章并生成新的文件 拖拽函数
     */
    @Override
    public void addSeal1(byte[] pdfPath,
                                byte[] picPath,
                                float width,
                                float heigth,
                                int pageNum,
                                float x,
                                float y,
                                byte[] pfxPath,
                                String pwd,String signSerialNum,FileOutputStream os) throws IOException, DocumentException {

        String uuid = UUID.randomUUID().toString();
        PdfReader reader =null;
        PdfStamper stamper =null;
        InputStream in =null;
        try{
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            //初始化盖章的私钥和证书 和签名的图片信息
            Image pic = Image.getInstance(picPath);
            KeyStore ks = KeyStore.getInstance("PKCS12");
            //将证书转换成FileInputStream
            in = byte2Input(pfxPath);
            ks.load(in, pwd.toCharArray());
            String alias = (String) ks.aliases().nextElement();
            //私钥
            PrivateKey pk = (PrivateKey) ks.getKey(alias, pwd.toCharArray());
            //公钥证书
            Certificate[] chain = ks.getCertificateChain(alias);
            //读取源文件
            reader = new PdfReader(pdfPath);

            stamper = PdfStamper
                    .createSignature(reader, os, '\u0000', null, true);//注意此处的true 允许多次盖章，false则只能盖一个章。
            //设置签名的外观显示
            PdfSignatureAppearance appearance = stamper
                    .getSignatureAppearance();
            //规定签章的权限，包括三种，详见itext 5 api，这里是不允许任何形式的修改
            PdfSigLockDictionary dictionary = new PdfSigLockDictionary(PdfSigLockDictionary.LockPermissions.FORM_FILLING_AND_ANNOTATION);
            appearance.setFieldLockDict(dictionary);
            appearance.setReason(signSerialNum);
            appearance.setImage(pic);
            //此处的fieldName 每个文档只能有一个，不能重名
            appearance.setVisibleSignature(
                    new Rectangle(x, y, x + width,
                            y + heigth), pageNum,
                    "ESS" + uuid);//fileName 一个文档中不能有重名的filedname
            appearance.setLayer2Text("");//设置文字为空否则签章上将会有文字 影响外观

            ExternalSignature es = new PrivateKeySignature(pk,
                    "SHA-256", "BC");

            ExternalDigest digest = new BouncyCastleDigest();

            MakeSignature.signDetached(appearance, digest, es,
                    chain, null, null, null, 0, MakeSignature.CryptoStandard.CMS);

        }catch (Exception e){
            e.printStackTrace();
        }finally {

            if (os != null) {
                os.close();
            }
            if (stamper != null) {
                stamper.close();
            }

            if (reader != null) {
                reader.close();
            }

            if (in != null) {
                in.close();
            }
        }
    }


    /**
     *签章并生成新的文件
     */
    @Override
    public  void addSeal(byte[] pdfPath,
                               byte[] picPath,
                               float width,
                               float heigth,
                               int pageNum,
                               float x,
                               float y,
                               byte[] pfxPath,
                               String pwd,String signSerialNum,FileOutputStream os) throws IOException, DocumentException {

        String uuid = UUID.randomUUID().toString();
        PdfReader reader =null;
        PdfStamper stamper =null;
        InputStream in =null;
        try{
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            InputStream inp = null;
            //初始化盖章的私钥和证书 和签名的图片信息
            Image pic = Image.getInstance(picPath);
            KeyStore ks = KeyStore.getInstance("PKCS12");
            //将证书转换成FileInputStream
            in = byte2Input(pfxPath);
            ks.load(in, pwd.toCharArray());
            String alias = (String) ks.aliases().nextElement();
            PrivateKey pk = (PrivateKey) ks.getKey(alias, pwd.toCharArray());
            Certificate[] chain = ks.getCertificateChain(alias);
            //读取源文件
            reader = new PdfReader(pdfPath);

            stamper = PdfStamper
                    .createSignature(reader, os, '\u0000', null, true);//注意此处的true 允许多次盖章，false则只能盖一个章。
            //设置签名的外观显示
            PdfSignatureAppearance appearance = stamper
                    .getSignatureAppearance();
            //规定签章的权限，包括三种，详见itext 5 api，这里是不允许任何形式的修改
            PdfSigLockDictionary dictionary = new PdfSigLockDictionary(PdfSigLockDictionary.LockPermissions.FORM_FILLING_AND_ANNOTATION);
            appearance.setFieldLockDict(dictionary);
            appearance.setReason(signSerialNum);
            appearance.setImage(pic);

            //此处的fieldName 每个文档只能有一个，不能重名
            appearance.setVisibleSignature(
                    new Rectangle(x-width/2, y-heigth/2, x + width/2,
                            y + heigth/2), pageNum,
                    "ESS" + uuid);//fileName 一个文档中不能有重名的filedname
            appearance.setLayer2Text("");


            ExternalSignature es = new PrivateKeySignature(pk,
                    "SHA-256", "BC");



            ExternalDigest digest = new BouncyCastleDigest();

            MakeSignature.signDetached(appearance, digest, es,
                    chain, null, null, null, 0, MakeSignature.CryptoStandard.CMS);

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (os != null) {
                os.close();
            }
            if (stamper != null) {
                stamper.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (in != null) {
                in.close();
            }
        }
    }
    @Override
    public List<VerifyResult> verifyPdfSign(File file) {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        List<VerifyResult> result = new ArrayList<>();
        try{
            PdfReader reader = new PdfReader(File2byte(file));
            AcroFields acroFields = reader.getAcroFields();
            List<String> names = acroFields.getSignatureNames();
            for (String name : names) {
                VerifyResult verifyResult = new VerifyResult();
                if (acroFields.signatureCoversWholeDocument(name)){
                    verifyResult.setCompleteVerify("true");
                }else {
                    verifyResult.setCompleteVerify("false");
                }
                PdfPKCS7 pk = acroFields.verifySignature(name);
                if (pk.verify()){
                    verifyResult.setDocumentVerify("true");
                }else{
                    verifyResult.setDocumentVerify("false");
                }
                Certificate[] c =  pk.getCertificates();
                try{
                    CertificateInfo.X500Name iss = CertificateInfo.getIssuerFields(pk.getSigningCertificate());
                    IssuerUnit issuerUnit = issuerUnitService.findIssuerUnitByName(iss.getField("CN"));
                    verifyResult.setSealName(CertificateInfo.getSubjectFields(pk.getSigningCertificate()).getField("CN"));
                    if (issuerUnit==null||issuerUnit.getIssuerUnitRoot()==null){
                        verifyResult.setCertVerify("false");
                    }else{
                        c[0].verify(getPublicKey(issuerUnit.getIssuerUnitRoot()));
                        verifyResult.setCertVerify("true");
                    }
                }catch (SignatureException e){
                    verifyResult.setCertVerify("false");
                }
                result.add(verifyResult);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }


    @Override
    public PdfLocation findLocationByKeyword(String keyword, String fileName,int num) {
        List<PdfLocation> pdfLocationList = new ArrayList<>();
        List<Location> list = getLastKeyWord(fileName,keyword.toLowerCase());
        for (Location l :list){
            PdfLocation pdfLocation = new PdfLocation();
            pdfLocation.setX(l.getX());
            pdfLocation.setY(l.getY());
            pdfLocation.setPageNum(l.getPageNum());
            pdfLocationList.add(pdfLocation);
        }
        if (pdfLocationList.size()>=num){
            return pdfLocationList.get(num-1);
        }else {
            return null;
        }
    }

    @Override
    public PdfLocation findLocationByBookMark(String keyword, String fileName) {
        //书签定位
        Location location = null;
        PdfLocation pdfLocation = new PdfLocation();
        try {
            location = locationByBookMark(fileName,keyword);
            pdfLocation.setX(location.getX());
            pdfLocation.setY(location.getY());
            pdfLocation.setPageNum(location.getPageNum());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return pdfLocation;
    }

    @Override
    public PdfLocation findLocationByXY(float X, float Y, int pageNum) {
        PdfLocation pdfLocation = new PdfLocation();
        pdfLocation.setX(X);
        pdfLocation.setY(Y);
        pdfLocation.setPageNum(pageNum);

        return pdfLocation;
    }
    /**
     *签章并生成新的文件 拖拽函数
     */
    @Override
    public void addSeal_UK(byte[] pdfPath,
                         byte[] picPath,
                         float width,
                         float heigth,
                         int pageNum,
                         float x,
                         float y,
                         String cert,
                        String signSerialNum,FileOutputStream os,String UKID) throws Exception {

        String uuid = UUID.randomUUID().toString();
        PdfReader reader =null;
        PdfStamper stamper =null;
        InputStream in =null;
        try{
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            //初始化盖章的私钥和证书 和签名的图片信息
            Image pic = Image.getInstance(picPath);
            KeyStore ks = KeyStore.getInstance("PKCS12");
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            in = new ByteArrayInputStream(ESSGetBase64Decode(cert));
            Certificate c = cf.generateCertificate(in);
            Certificate[] chain = new Certificate[1];
            chain[0] = c;
            //读取源文件
            reader = new PdfReader(pdfPath);
            stamper = PdfStamper
                    .createSignature(reader, os, '\u0000', null, true);//注意此处的true 允许多次盖章，false则只能盖一个章。
            //设置签名的外观显示
            PdfSignatureAppearance appearance = stamper
                    .getSignatureAppearance();
            //规定签章的权限，包括三种，详见itext 5 api，这里是不允许任何形式的修改
            PdfSigLockDictionary dictionary = new PdfSigLockDictionary(PdfSigLockDictionary.LockPermissions.FORM_FILLING_AND_ANNOTATION);
            appearance.setFieldLockDict(dictionary);
            appearance.setReason(signSerialNum);
            appearance.setImage(pic);
            //此处的fieldName 每个文档只能有一个，不能重名
            appearance.setVisibleSignature(
                    new Rectangle(x, y, x + width,
                            y + heigth), pageNum,
                    "ESS" + uuid);//fileName 一个文档中不能有重名的filedname
            appearance.setLayer2Text("");//设置文字为空否则签章上将会有文字 影响外观
//            ExternalSignature es = new PrivateKeySignature(pk,
//                    "SHA-256", "BC");
            ExternalDigest digest = new BouncyCastleDigest();
            MakeSignature.signDetached(appearance, digest, null,
                    chain, null, null, null, 0,
                    MakeSignature.CryptoStandard.CMS,UKID);
        }finally {
            if (os != null) {
                os.close();
            }
            if (stamper != null) {
                stamper.close();
            }

            if (reader != null) {
                reader.close();
            }
            if (in != null) {
                in.close();
            }
        }
    }

}
