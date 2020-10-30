package com.chen.dao;
import com.chen.entity.SystemLog;
import com.chen.entity.SystemLogIp;
import org.springframework.stereotype.Repository;
@Repository
public interface SystemLogMapper {

    int addSystemLog(SystemLog systemLog);
    int addSystemLogIp(SystemLogIp systemLogIp);
}
