package com.chen.platformpdf.service;

import com.chen.platformpdf.dto.SealDto;

import java.util.List;

public interface SealService {
    SealDto findSealDtoByUnitAndType(String unitId,String typeId);

    SealDto findSealById(String sealId);

    SealDto finSealByPersonId(String person_id);

    List<SealDto> findSealDtoByUidAndBid(String unitId, String depId);

    SealDto findSealBySysPersonId(String sysPersonId,String businessSys);

    List<SealDto> getSealByUidAndType(String unitId, String sealType);
}
