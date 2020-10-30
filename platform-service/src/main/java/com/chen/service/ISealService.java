package com.chen.service;

import com.chen.entity.*;

import java.text.ParseException;
import java.util.List;

public interface ISealService {
    Seal findSealById(String id);

    Seal findSealByName(String unitName, String sealName);

    boolean addSeal(Seal seal);

    List<Seal> findSealListByUKId(String ukid);

    List<Seal> findSealListByUnitId(String unitId);

    List<Seal> findSealList(Seal seal);

    List<Seal> findSealListByKeyword(String keyword, String unitId);

    boolean updateSeal(Seal seal);

    /**
     *印章类型相关
     */
    List<SealType> findSealTypeList();
    SealType findSealTypeById(String typeId);

    List<SealStandard> findSealStandardList();

    List<SealSaveType> findSealSaveType();

    List<UKDll> findUKDll(int sealStandard);

    String getASN1SealData(String seal_id, String seal_type_id, String seal_name, String cert_id,
                           String input_time, String seal_start_time, String seal_end_time, byte[] bPicData,
                           int imgW, int imgH) throws ParseException;

    UKDll getUKTypeById(String id);

    List<SealInDate> findSealInDate();

    boolean verifySealCount(String sealType,String unitId);

    boolean deleteSealById(String sealId);
}
