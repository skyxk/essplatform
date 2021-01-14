package com.chen.platformpdf.service.impl;

import com.chen.core.util.CutImageUtil;
import com.chen.core.util.crypt.ESSCertificate;
import com.chen.dao.IssuerUnitMapper;
import com.chen.platformpdf.dto.SealDto;
import com.chen.platformpdf.service.FileService;
import com.chen.platformpdf.util.Location;
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
import org.bouncycastle.asn1.esf.SignaturePolicyIdentifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Field;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.*;

import static com.chen.core.util.Base64Utils.ESSGetBase64Decode;
import static com.chen.core.util.FileUtils.File2byte;
import static com.chen.core.util.FileUtils.byte2Input;

import static com.chen.core.util.crypt.ESSCertificate.VerifyIssuer;
import static com.chen.platformpdf.util.GetLocation.*;
import static com.chen.platformpdf.util.PDFUtils.convertCoordinate;
import static com.itextpdf.text.pdf.security.MakeSignature.processCrl;

/**
 * @author ：chen
 * @date ：Created in 2019/9/27 9:33
 */
@Service
public class SignServiceImpl implements SignService {
    @Autowired
    private FileService fileService;
    @Autowired
    IssuerUnitMapper issuerUnitMapper;
    @Override
    public void addOverSeal(SealDto sealDto,String documentCode) throws IOException, DocumentException {
        byte[] imgFile = ESSGetBase64Decode(sealDto.getSeal_img());
        int width = convertCoordinate(sealDto.getSeal_w());
        int height = convertCoordinate(sealDto.getSeal_h());
        File pdfFile = fileService.findPDFFileByDocumentCode(documentCode,2);
        byte[] pdfFileByte = File2byte(pdfFile);
        //PDF骑缝章签署过程
        //首先处理签章图片
        //获取签章的页数
        PDDocument document = PDDocument.load(pdfFileByte);
        int pageNum = document.getNumberOfPages();
        PDPageTree pages = document.getPages();
        document.close();
        if (pageNum>1){
            //处理图片，将图片按照pdf页数等分，并获取图片宽度和长度。
            List<String> ImageBase64List = CutImageUtil.cutImageToBase64(imgFile,pageNum);
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
                cHeigth = (int) height;
                //计算签章其实坐标
                int x = pWidth-cWidth/2;
//                int x = cWidth/2+30;
                int y = pHeight/2;
                File pdfFile_1 = fileService.findPDFFileByDocumentCode(documentCode,2);
                byte[] pdfFileByte_1 = File2byte(pdfFile);
                FileOutputStream os  = new FileOutputStream(new File(pdfFile_1.getAbsolutePath()));
                addSeal(pdfFileByte_1, ESSGetBase64Decode(ImageBase64List.get(num)),cWidth,cHeigth,
                        num+1,x,y,sealDto,os);
            }
        }
    }
    @Override
    public boolean addSeal(byte[] pdfPath, byte[] imgFile,int width, int height, int pageNum, float x, float y, SealDto sealDto,
                        FileOutputStream os) throws IOException {

        String seal_standard = sealDto.getSeal_standard();
        String pwd = sealDto.getSeal_pwd();
        InputStream in =null;
        try{
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            if ("5".equals(seal_standard)){
                byte[] pfxFile = ESSGetBase64Decode(sealDto.getSeal_pfx());
                //RSA软证书
                KeyStore ks = KeyStore.getInstance("PKCS12");
                //将证书转换成FileInputStream
                in = byte2Input(pfxFile);
                ks.load(in, pwd.toCharArray());
                String alias = (String) ks.aliases().nextElement();
                //私钥
                PrivateKey pk = (PrivateKey) ks.getKey(alias, pwd.toCharArray());
                //公钥证书
                Certificate[] chain = ks.getCertificateChain(alias);
                ExternalSignature es = new PrivateKeySignature(pk,
                        "SHA-256", "BC");
                addSealByRSA(pdfPath, imgFile, width,  height,  pageNum,  x,  y,
                    chain,  es, sealDto.getSealId(), os);
            }else if ("3".equals(seal_standard)){
                byte[] pfxFile = ESSGetBase64Decode(sealDto.getSeal_pfx());
                //国密软证书
                Field digestNamesField = DigestAlgorithms.class.getDeclaredField("digestNames");
                digestNamesField.setAccessible(true);
                HashMap<String, String> digestNames = (HashMap<String,String>)digestNamesField.get(null);
                digestNames.put("1.2.156.10197.1.401", "SM3");
                Field algorithmNamesField = EncryptionAlgorithms.class.getDeclaredField("algorithmNames");
                algorithmNamesField.setAccessible(true);
                HashMap<String, String> algorithmNames = (HashMap<String,String>)algorithmNamesField.get(null);
                algorithmNames.put("1.2.156.10197.1.501", "SM2");
                //初始化盖章的私钥和证书 和签名的图片信息
                KeyStore ks = KeyStore.getInstance("PKCS12","BC");
                //将证书转换成FileInputStream
                in = byte2Input(pfxFile);
                ks.load(in, pwd.toCharArray());
                String alias = (String) ks.aliases().nextElement();
                PrivateKey pk = (PrivateKey) ks.getKey(alias, pwd.toCharArray());
                Certificate[] chain = ks.getCertificateChain(alias);
                com.chen.core.util.crypt.CertificateInfo certificateInfo = ESSCertificate.GetCertInfo(chain[0].getEncoded());
                //SM3WITHSM2  SHA1WITHRSA
                String AlgName = "SHA-256";
                if (certificateInfo.sSignAlgName.equals("SM3WITHSM2"))
                    AlgName = "SM3";
                ExternalSignature es = new PrivateKeySignature(pk,
                        AlgName, "BC");
                addSealByRSA(pdfPath, imgFile, width,  height,  pageNum,  x,  y,
                    chain,  es, sealDto.getSealId(), os);
            }else if ("1".equals(seal_standard)||"2".equals(seal_standard)||"4".equals(seal_standard)){
                //UK印章
                String UKID = sealDto.getUk_id();
                byte[] certFile = ESSGetBase64Decode(sealDto.getSeal_cert());
                CertificateFactory cf = CertificateFactory.getInstance("X.509","BC");
                in = new ByteArrayInputStream(certFile);
                Certificate c = cf.generateCertificate(in);
                Certificate[] chain = new Certificate[1];
                chain[0] = c;
                addSeal_UK(pdfPath, imgFile, width,  height,  pageNum,  x,  y,
                        chain,sealDto.getSealId(), os,UKID);
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            if (in != null) {
                in.close();
            }
        }
        return true;
    }
    /**
     *签章并生成新的文件 拖拽函数(坐标计算方式不一样)
     */
    @Override
    public void addSealByRSA(byte[] pdfPath, byte[] picPath, float width, float height, int pageNum, float x, float y,
                             Certificate[] chain, ExternalSignature es, String signSerialNum,FileOutputStream os)
            throws IOException, DocumentException {
        String uuid = UUID.randomUUID().toString();
        PdfReader reader =null;
        PdfStamper stamper =null;
        try{
            //初始化盖章的私钥和证书 和签名的图片信息
            Image pic = Image.getInstance(picPath);
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
//            appearance.setVisibleSignature(
//                    new Rectangle(x, y, x + width,
//                            y + height), pageNum,
//                    "ESS" + uuid);//fileName 一个文档中不能有重名的filedname 拖拽定位

            appearance.setVisibleSignature(
                    new Rectangle(x-width/2, y-height/2, x + width/2,
                            y + height/2), pageNum,
                    "ESS" + uuid);//fileName 一个文档中不能有重名的filedname  普通定位

            appearance.setLayer2Text("");//设置文字为空否则签章上将会有文字 影响外观
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
        }
    }
    /**
     *签章并生成新的文件 拖拽函数
     */
    @Override
    public void addSeal_UK(byte[] pdfPath, byte[] picPath, float width, float height, int pageNum, float x, float y,
                           Certificate[] chain, String signSerialNum,FileOutputStream os,String UKID) throws Exception {
        String uuid = UUID.randomUUID().toString();
        PdfReader reader =null;
        PdfStamper stamper =null;
        try{
            //初始化盖章的私钥和证书 和签名的图片信息
            Image pic = Image.getInstance(picPath);
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
                    new Rectangle(x-width/2, y-height/2, x + width/2,
                            y + height/2), pageNum,
                    "ESS" + uuid);//fileName 一个文档中不能有重名的filedname  普通定位
            appearance.setLayer2Text("");//设置文字为空否则签章上将会有文字 影响外观

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
        }
    }
    @Override
    public List<VerifyResult> verifyPdfSign(byte[] file) {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        List<VerifyResult> result = new ArrayList<>();
        try{
            PdfReader reader = new PdfReader(file);
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
                CertificateInfo.X500Name iss = CertificateInfo.getIssuerFields(pk.getSigningCertificate());
                verifyResult.setSealName(CertificateInfo.getSubjectFields(pk.getSigningCertificate()).getField("CN"));
                //取到所有的可信任颁发者的根证书
                List<String> rootList = issuerUnitMapper.findTrustRoot();
                verifyResult.setCertVerify("false");
                if (rootList!=null){
                    for (String cert :rootList){
//                        boolean result1 =false;
//                        try{
//                            c[0].verify(GetCertPublicKey2(ESSGetBase64Decode(cert)));
//                            result1 =true;
//                        }catch (Exception e){
//                            System.out.println("证书验证失败");
//                        }
                        boolean result1 = VerifyIssuer(c[0].getEncoded(),ESSGetBase64Decode(cert));
                        if (result1) {
                            verifyResult.setCertVerify("true");
                            break;
                        }
                    }
                }
                verifyResult.setDocumentVerify("true");
                verifyResult.setCompleteVerify("true");
                verifyResult.setCertVerify("true");
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
        if (num == 0){
            return pdfLocationList.get(pdfLocationList.size()-1);
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
}
