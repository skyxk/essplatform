package com.chen.dao;

import com.chen.entity.SealJur;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
@Repository
public interface SealJurMapper {

    SealJur selectSealJur(@Param("sealId")String sealId, @Param("signUser")String signUser);

}
