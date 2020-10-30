package com.chen.service;

import com.chen.entity.SystemJurInit;

import java.util.List;

public interface ISystemService {
    SystemJurInit findSystemInit();

    SystemJurInit findSystemInit(String unitId);

    boolean verifySystemTime();

    String encryptString(String str);

    String decryptString(String str);

    String encryptStringJMJ(String str);

    String decryptStringJMJ(String str);

    List<String> findFileType();
}
