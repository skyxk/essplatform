package com.chen.dao;

import com.chen.entity.EncryptorServer;
import com.chen.entity.SystemJurInit;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemInitMapper {
    SystemJurInit findSystemInit();

    String findSignToken();

    EncryptorServer findEncryptorServer();

    SystemJurInit findSystemInitByUnitId(String unitId);
}
