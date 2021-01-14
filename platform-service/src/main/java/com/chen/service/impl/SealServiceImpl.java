package com.chen.service.impl;

import com.chen.core.util.crypt.AKSeal;
import com.chen.dao.IssuerUnitMapper;
import com.chen.dao.SealMapper;
import com.chen.dao.SystemInitMapper;
import com.chen.entity.*;
import com.chen.service.ICertificateService;
import com.chen.service.ISealImgService;
import com.chen.service.ISealService;
import com.chen.service.ISystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.chen.core.util.Base64Utils.ESSGetBase64Decode;
import static com.chen.core.util.Base64Utils.ESSGetBase64Encode;
import static com.chen.core.util.DateUtils.formatDate;

@Service
public class SealServiceImpl implements ISealService {

    @Autowired
    public SealMapper sealMapper;
    @Autowired
    public ICertificateService certificateService;
    @Autowired
    public IssuerUnitMapper issuerUnitMapper;
    @Autowired
    public ISystemService systemService;
    @Autowired
    public ISealImgService sealImgService;


    @Override
    public Seal findSealById(String id) {
        Seal seal = decryptSeal(sealMapper.findSealById(id));
        if (seal == null){
            return null;
        }
        if (seal.getSealImg()!=null){
            seal.setSealImg(sealImgService.findSealImgById(seal.getSeal_img_id()));
        }
        return seal;
    }

    /**
     * 根据印章所属单位名称和印章名称查找
     * --PDF签章用
     * @param unitName 单位名称
     * @param sealName 印章名称
     * @return 印章
     */
    @Override
    public Seal findSealByName(String unitName, String sealName) {
        sealName = systemService.encryptString(sealName);
        Seal seal = decryptSeal(sealMapper.findSealByName(unitName,sealName));
        if (seal!=null){
            seal.setSealImg(sealImgService.findSealImgById(seal.getSeal_img_id()));
        }
        return seal;
    }

    @Override
    public boolean addSeal(Seal seal) {
        int result = sealMapper.addSeal(encryptSeal(seal));
        if (result==1){
            return true;
        }
        seal = decryptSeal(seal);
        return false;
    }
 
    @Override
    public List<Seal> findSealListByUKId(String ukid) {
        List<Seal> sealList = sealMapper.findSealListByUKId(ukid);
        for (Seal seal1 :sealList){
            seal1 = decryptSeal(seal1);
            seal1.setSealImg(sealImgService.findSealImgById(seal1.getSeal_img_id()));
        }
        return sealList;
    }

    @Override
    public List<Seal> findSealListByUnitId(String unitId) {
        List<Seal> sealList = sealMapper.findSealListByUnitId(unitId);
        for (Seal seal1 :sealList){
            seal1 = decryptSeal(seal1);
            seal1.setSealImg(sealImgService.findSealImgById(seal1.getSeal_img_id()));
        }
        return sealList;

    }

    @Override
    public List<Seal> findSealList(Seal seal) {
        List<Seal> sealList = sealMapper.findSealList(seal);
        for (Seal seal1 :sealList){
            seal1 = decryptSeal(seal1);
            seal1.setSealImg(sealImgService.findSealImgById(seal1.getSeal_img_id()));
        }
        return sealList;

    }

    @Override
    public List<Seal> findSealListByKeyword(String keyword, String unitId) {
        keyword = systemService.encryptString(keyword);
        List<Seal> sealList = sealMapper.findSealListByKeyword(keyword,unitId);
        for (Seal seal1 :sealList){
            seal1 = decryptSeal(seal1);
            seal1.setSealImg(sealImgService.findSealImgById(seal1.getSeal_img_id()));
        }
        return sealList;
    }

    @Override
    public boolean updateSeal(Seal seal) {
        return sealMapper.updateSeal(encryptSeal(seal));
    }

    @Override
    public List<SealType> findSealTypeList() {
        return sealMapper.findSealTypeList();
    }

    @Override
    public SealType findSealTypeById(String typeId) {
        return sealMapper.findSealTypeById(typeId);
    }

    @Override
    public List<SealStandard> findSealStandardList() {
        return sealMapper.findSealStandardList();
    }

    @Override
    public List<SealSaveType> findSealSaveType() {
        return sealMapper.findSealSaveType();
    }

    @Override
    public List<UKDll> findUKDll(int sealStandard) {
        return sealMapper.findUKDll(sealStandard);
    }

    @Override
    public String getASN1SealData(String seal_id, String seal_type_id, String seal_name, String cert_id,
                                  String input_time, String seal_start_time, String seal_end_time,
                                  byte[] bPicData, int imgW, int imgH) throws ParseException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {

        int iSealType = 1;
        if ("ESS001".equals(seal_type_id)){
            iSealType = 2;
        }
        Certificate certificate = certificateService.findCertificateById(cert_id);
        byte[] bSignerCert = ESSGetBase64Decode(certificate.getCer_base64());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = sdf.parse(input_time);
        input_time = formatDate(date,"yyMMddHHmmss");
        input_time = input_time+"Z";
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        date = sdf.parse(seal_start_time);
        seal_start_time = formatDate(date,"yyMMddHHmmss");
        seal_start_time = seal_start_time+"Z";

        sdf = new SimpleDateFormat("yyyy-MM-dd");
        date = sdf.parse(seal_end_time);
        seal_end_time = formatDate(date,"yyMMddHHmmss");
        seal_end_time = seal_end_time+"Z";
        IssuerUnit issuerUnit = issuerUnitMapper.findIssuerUnitBySM2();
        byte[] bMakerCert =ESSGetBase64Decode(issuerUnit.getIssuerUnitRoot());

//        byte[] result = AKSeal.CreateAKSeal(seal_id,2,"BJ.CLT",iSealType,seal_name,
//                bSignerCert,input_time,seal_start_time,seal_end_time,"GIF",bPicData,imgW,imgH,
//                bMakerCert,"1.2.156.10197.1.501",
//                ESSGetBase64Decode(issuerUnit.getIssuerUnitPfx()),
//                systemService.decryptString(issuerUnit.getPfxPwd()));

        byte[] result = AKSeal.CreateAKSeal(seal_id,2,"BJ.CLT",iSealType,seal_name,
                bSignerCert,input_time,seal_start_time,seal_end_time,"GIF",bPicData,imgW,imgH,
                bMakerCert,"1.2.156.10197.1.501",
                issuerUnit.getBc_pub(),
                issuerUnit.getBc_prv());
        return ESSGetBase64Encode(result);
    }
    @Override
    public UKDll getUKTypeById(String id) {
        return sealMapper.getUKTypeById(id);
    }

    @Override
    public List<SealInDate> findSealInDate() {
        return sealMapper.findSealInDate();
    }
    @Override
    public boolean verifySealCount(String sealType,String unitId) {
        //首先获取印章数量
        int sealCount = 0;
        int initSealCount = 0;
        //获取授权数量
        SystemJurInit  systemJurInit = systemService.findSystemInit(unitId);
        if (systemJurInit==null){
            return false;
        }
        if ("ESS001".equals(sealType)){
            initSealCount = systemJurInit.getHWCOUNT();
            sealCount =  sealMapper.getSealCountByType(sealType);
        }else{
            initSealCount = systemJurInit.getSEALCOUNT();
            sealCount =  sealMapper.getSealCountBySeal();
        }
        if (sealCount<initSealCount){
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteSealById(String sealId) {
        int result =  sealMapper.deleteSealById(sealId);
        return result == 1;
    }

    @Override
    public Seal finSealByPersonId(String person_id) {

        Seal seal = decryptSeal(sealMapper.finSealByPersonId(person_id));
        if (seal!=null){
            seal.setSealImg(sealImgService.findSealImgById(seal.getSeal_img_id()));
        }
        return seal;
    }

    @Override
    public Seal findSealBySealName(String sealName) {
        sealName = systemService.encryptString(sealName);
        Seal seal = decryptSeal(sealMapper.findSealBySealName(sealName));
        if (seal!=null){
            seal.setSealImg(sealImgService.findSealImgById(seal.getSeal_img_id()));
        }
        return seal;
    }

    /**
     * 用于重庆中烟项目获取印章列表用
     * @param unitId 单位ID
     * @param depId 部门ID
     * @return 印章列表
     */
    @Override
    public List<Seal> findSealByUidAndBid(String unitId, String depId) {
        List<Seal> sealList = sealMapper.findSealByUidAndBid(unitId,depId);
        for (Seal seal1 :sealList){
            seal1 = decryptSeal(seal1);
            seal1.setSealImg(sealImgService.findSealImgById(seal1.getSeal_img_id()));
        }
        return sealList;
    }

    @Override
    public List<Seal> getSealByUidAndType(String unitId, String sealType) {
        List<Seal> sealList = sealMapper.getSealByUidAndType(unitId,sealType);
        for (Seal seal1 :sealList){
            seal1 = decryptSeal(seal1);
            seal1.setSealImg(sealImgService.findSealImgById(seal1.getSeal_img_id()));
        }
        return sealList;
    }

    public Seal encryptSeal(Seal seal){
        if (seal==null){
            return null;
        }
        //印章名称
        if (seal.getSeal_name()!=null){
            seal.setSeal_name(systemService.encryptString(seal.getSeal_name()));
        }
        if (seal.getJbr_card_data()!=null){
            seal.setJbr_card_data(systemService.encryptString(seal.getJbr_card_data()));
        }

        if (seal.getJbr_card_name()!=null){
            seal.setJbr_card_name(systemService.encryptString(seal.getJbr_card_name()));
        }
        return seal;
    }
    public Seal decryptSeal(Seal seal){
        if (seal==null){
            return null;
        }
        //印章名称
        if (seal.getSeal_name()!=null){
            seal.setSeal_name(systemService.decryptString(seal.getSeal_name()));
        }
        if (seal.getJbr_card_data()!=null){
            seal.setJbr_card_data(systemService.decryptString(seal.getJbr_card_data()));
        }
        if (seal.getJbr_card_name()!=null){
            seal.setJbr_card_name(systemService.decryptString(seal.getJbr_card_name()));
        }
        return seal;
    }

}
