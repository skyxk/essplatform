package com.chen.service.impl;


import com.chen.core.util.DateUtils;
import com.chen.dao.ErrorLogMapper;
import com.chen.entity.ErrorLog;
import com.chen.service.IErrorLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import static com.chen.core.util.StringUtils.getUUID;
@Service
public class ErrorLogServiceImpl implements IErrorLogService {


    @Autowired
    ErrorLogMapper errorLogMapper;
    @Override
    public boolean addErrorLog(String errorDetail) {
        ErrorLog errorLog = new ErrorLog();
        errorLog.setErrorLogId(getUUID());
        errorLog.setTime(DateUtils.getDateTime());
        errorLog.setSysName("制章平台");
        errorLog.setErrorDetail(errorDetail);
        int result = errorLogMapper.addErrorLog(errorLog);
        if(result ==1){
            return true;
        }
        return false;
    }

}
