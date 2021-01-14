package com.chen.platformpdf.service.impl;

import com.chen.entity.Seal;
import com.chen.platformpdf.dto.SealDto;
import com.chen.platformpdf.service.SealService;
import com.chen.service.IPersonService;
import com.chen.service.ISealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SealPdfServiceImpl implements SealService {

    @Autowired
    private ISealService sealService;
    @Autowired
    private IPersonService personService;
    @Override
    public SealDto findSealDtoByUnitAndType(String unitId, String typeId) {
        List<Seal> sealList = sealService.findSealListByUnitId(unitId);
        for (Seal seal :sealList){
            if (typeId.equals(seal.getSeal_type_id())){
                return convertSealDto(seal);
            }
        }
        return null;
    }

    @Override
    public SealDto findSealById(String sealId) {
        Seal seal = sealService.findSealById(sealId);
        //查找到的印章
        if (seal != null){
            return convertSealDto(seal);
        }
        return null;
    }

    @Override
    public SealDto finSealByPersonId(String person_id) {
        Seal seal = sealService.finSealByPersonId(person_id);
        //查找到的印章
        if (seal != null){
            return convertSealDto(seal);
        }
        return null;
    }
    @Override
    public List<SealDto> findSealDtoByUidAndBid(String unitId, String depId) {
        List<Seal> sealList = sealService.findSealByUidAndBid(unitId,depId);
        //查找到的印章列表
        if (sealList != null){
            List<SealDto> sealDtoList = new ArrayList<>();
            for (Seal seal :sealList){
                sealDtoList.add(convertSealDto(seal));
            }
            return sealDtoList;
        }
        return null;
    }
    @Override
    public SealDto findSealBySysPersonId(String sysPersonId,String businessSys) {
        //首先根据人员id找到本系统人员ID
        String personId = personService.findPersonIdBySYS(businessSys,sysPersonId);
        Seal seal = sealService.finSealByPersonId(personId);
        //查找到的印章
        if (seal != null){
            return convertSealDto(seal);
        }
        return null;
    }

    @Override
    public List<SealDto> getSealByUidAndType(String unitId, String sealType) {
        List<Seal> sealList = sealService.getSealByUidAndType(unitId,sealType);
        //查找到的印章列表
        if (sealList != null){
            List<SealDto> sealDtoList = new ArrayList<>();
            for (Seal seal :sealList){
                sealDtoList.add(convertSealDto(seal));
            }
            return sealDtoList;
        }
        return null;
    }

    public SealDto convertSealDto(Seal seal) {
        SealDto sealDto = new SealDto();
        if (seal.getSeal_id() !=null){
            sealDto.setSealId(seal.getSeal_id());
        }
        if (seal.getSeal_name()!=null){
            sealDto.setSealName(seal.getSeal_name());
        }
        if (seal.getCertificate().getCer_base64()!=null){
            sealDto.setSeal_cert(seal.getCertificate().getCer_base64());
        }
        if (seal.getCertificate().getPfx_base64()!=null){
            sealDto.setSeal_pfx(seal.getCertificate().getPfx_base64());
        }
        if (seal.getCertificate().getCert_psw()!=null){
            sealDto.setSeal_pwd(seal.getCertificate().getCert_psw());
        }
        if (seal.getSealImg().getImg_gif_data()!=null){
            sealDto.setSeal_img(seal.getSealImg().getImg_gif_data());
        }
        if (seal.getSeal_type_id()!=null){
            sealDto.setSeal_type(seal.getSeal_type_id());
        }
        if (seal.getSeal_standard()!=null){
            sealDto.setSeal_standard(seal.getSeal_standard());
        }
        if (seal.getSealImg().getImage_h()!=0){
            sealDto.setSeal_h(seal.getSealImg().getImage_h());
        }
        if (seal.getSealImg().getImage_w()!=0){
            sealDto.setSeal_w(seal.getSealImg().getImage_w());
        }
        if (seal.getUk_id()!=null){
            sealDto.setUk_id(seal.getUk_id());
        }
        return sealDto;
    }
}
