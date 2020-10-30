package com.chen.dao;

import com.chen.entity.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SealMapper {
    Seal findSealById(@Param("sealId")	String id);

    Seal findSealByName(@Param("unitName")String unitName, @Param("sealName")String sealName);

    int addSeal(Seal seal);

    List<Seal> findSealListByUKId(String ukid);

    List<Seal> findSealList(Seal seal);

    List<Seal> findSealListByKeyword(@Param("keyword")String keyword, @Param("unitId")String unitId);

    boolean updateSeal(Seal seal);

    List<SealType> findSealTypeList();

    List<SealStandard> findSealStandardList();

    SealType findSealTypeById(String typeId);

    List<SealSaveType> findSealSaveType();

    List<UKDll> findUKDll(int sealStandard);

    UKDll getUKTypeById(String id);

    List<SealInDate> findSealInDate();

    int getSealCount();

    int getSealCountByType(String sealType);
    int getSealCountBySeal();
    int deleteSealById(String sealId);
}
