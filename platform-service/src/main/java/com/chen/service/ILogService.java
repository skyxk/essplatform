package com.chen.service;

import com.chen.entity.User;

public interface ILogService {
    boolean addSysLog(User user, String powerName, String detail, String ip);
}
