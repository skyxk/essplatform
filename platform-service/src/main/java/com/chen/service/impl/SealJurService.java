package com.chen.service.impl;

import com.chen.dao.SealJurMapper;
import com.chen.entity.Seal;
import com.chen.entity.SealJur;
import com.chen.service.ISealJurService;
import com.chen.service.ISealService;
import org.springframework.beans.factory.annotation.Autowired;

public class SealJurService implements ISealJurService {

    @Autowired
    ISealService sealService;
    @Autowired
    SealJurMapper sealJurMapper;

    @Override
    public boolean checkJur(String sealId, String signUser) {
        Seal seal = sealService.findSealById(sealId);

        SealJur sealJur = sealJurMapper.selectSealJur(sealId,signUser);


        return false;
    }
}
