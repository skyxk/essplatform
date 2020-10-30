package com.chen.service;

import com.chen.entity.SealImg;

public interface ISealImgService {
    boolean addSealImg(SealImg sealImg);
    SealImg findSealImgById(String id);

}
