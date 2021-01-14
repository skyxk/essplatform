package com.chen.dao;

import com.chen.entity.SealImg;
import org.springframework.stereotype.Repository;

/**
 * @author ：chen
 * @date ：Created in 2019/10/14 15:55
 */
@Repository
public interface SealImgMapper {

    SealImg findSealImgById(String id);

    int addSealImg(SealImg sealImg);

    boolean updateSealImg(SealImg c);
}
