package com.chen.service.impl;

import com.chen.dao.CertificateMapper;
import com.chen.dao.SealImgMapper;
import com.chen.entity.Seal;
import com.chen.entity.SealImg;
import com.chen.service.ISealImgService;
import com.chen.service.ISystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SealImgServiceImpl implements ISealImgService {

    @Autowired
    private SealImgMapper sealImgMapper;
    @Autowired
    public ISystemService systemService;
    @Override
    public boolean addSealImg(SealImg sealImg) {
        int result = sealImgMapper.addSealImg(encryptSealImg(sealImg));
        if (result==1){
            return true;
        }
        return false;
    }
    @Override
    public SealImg findSealImgById(String id){
        return decryptSealImg(sealImgMapper.findSealImgById(id));
    }

    public SealImg encryptSealImg(SealImg sealImg){
        if (sealImg==null){
            return null;
        }
        //印章名称
        if (sealImg.getImg_gif_data()!=null){
            sealImg.setImg_gif_data(systemService.encryptString(sealImg.getImg_gif_data()));
        }
        return sealImg;
    }
    public SealImg decryptSealImg(SealImg sealImg){
        if (sealImg==null){
            return null;
        }
        //印章名称
        if (sealImg.getImg_gif_data()!=null){
            sealImg.setImg_gif_data(systemService.decryptString(sealImg.getImg_gif_data()));
        }
        return sealImg;
    }
}
