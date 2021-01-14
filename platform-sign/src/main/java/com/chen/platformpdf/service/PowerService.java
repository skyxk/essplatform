package com.chen.platformpdf.service;

import com.alibaba.fastjson.JSONObject;
import com.chen.entity.Seal;

public interface PowerService {

    boolean checkPowerForSealByUser(Seal seal,String userId);
}
