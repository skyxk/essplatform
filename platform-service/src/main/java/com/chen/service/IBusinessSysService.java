package com.chen.service;

import com.chen.entity.Business_System;

public interface IBusinessSysService {
    /**
     * 查看IP是否存在于已注册系统
     * @param ip
     * @return
     */
    boolean checkBusinessSysByIp(String ip);

    Business_System findBusinessSysById(String businessSysId);
}
