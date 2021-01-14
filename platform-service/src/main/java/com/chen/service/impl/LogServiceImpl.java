package com.chen.service.impl;

import com.chen.core.util.DateUtils;
import com.chen.dao.SignatureLogMapper;
import com.chen.dao.SystemLogMapper;
import com.chen.entity.SignatureLog;
import com.chen.entity.SystemLog;
import com.chen.entity.SystemLogIp;
import com.chen.entity.User;
import com.chen.service.ILogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.chen.core.util.StringUtils.getUUID;

@Service
public class LogServiceImpl implements ILogService {
    @Autowired
    private SystemLogMapper systemLogMapper;
    @Autowired
    private SignatureLogMapper signatureLogMapper;

    @Override
    public boolean addSysLog(User user, String powerName, String detail, String ip) {
        SystemLog systemLog = new SystemLog();
        systemLog.setSysLogId(getUUID());
        systemLog.setPowerName(powerName);
        systemLog.setLogDetail(detail);
        systemLog.setLogTime(DateUtils.getDateTime1());
        systemLog.setUserId(user.getUser_id());
        systemLog.setDepId(user.getDep_id());
        systemLog.setUnitId(user.getUnit_id());
        systemLog.setSafeHash("");
        systemLog.setGroupNum(1);
        int result1 = systemLogMapper.addSystemLog(systemLog);
        SystemLogIp systemLogIp =new SystemLogIp();
        systemLogIp.setSys_log_ip_id(getUUID());
        systemLogIp.setSys_log_id(systemLog.getSysLogId());
        systemLogIp.setIp(ip);
        systemLogIp.setMac("");
        int result2 = systemLogMapper.addSystemLogIp(systemLogIp);
        return result2 == 1 && result1 == 1;
    }


    @Override
    public boolean addSignLog(SignatureLog signatureLog) {
        int result = signatureLogMapper.addSignLog(signatureLog);
        return false;
    }

    @Override
    public SignatureLog findSignatureLogBySerNum(String signSerialNum) {
        SignatureLog result = signatureLogMapper.findSignatureLogBySerNum(signSerialNum);
        return result;
    }
}
