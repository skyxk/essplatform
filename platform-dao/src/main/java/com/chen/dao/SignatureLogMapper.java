package com.chen.dao;

import com.chen.entity.SignatureLog;
import org.springframework.stereotype.Repository;

@Repository
public interface SignatureLogMapper {
    int addSignLog(SignatureLog signatureLog);

    SignatureLog findSignatureLogBySerNum(String signSerialNum);
}
