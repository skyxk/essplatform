package com.chen.dao;

import com.chen.entity.ErrorLog;
import org.springframework.stereotype.Repository;

@Repository
public interface ErrorLogMapper {
    /**
     * 添加错误日志
     */
    int addErrorLog(ErrorLog errorLog);


}
