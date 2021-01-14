package com.chen.service;

import com.chen.entity.SignatureLog;
import com.chen.entity.User;

public interface ILogService {
    boolean addSysLog(User user, String powerName, String detail, String ip);

    boolean addSignLog(SignatureLog signatureLog);

    SignatureLog findSignatureLogBySerNum(String signSerialNum);
}
