package com.chen.service.impl;

import com.chen.dao.BusinessSysMapper;
import com.chen.entity.Business_System;
import com.chen.service.IBusinessSysService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ：chen
 * @date ：Created in 2019/10/17 13:38
 */

@Service
public class BusinessSysServiceImpl implements IBusinessSysService {
    @Autowired
    private BusinessSysMapper businessSysMapper;
    @Override
    public boolean checkBusinessSysByIp(String ip) {
        List<String> ipList =  businessSysMapper.findAllServerIp();
        for (String ips:ipList){
            if (ips.contains(ip)){
                return true;
            }
        }
        return false;
    }

    @Override
    public Business_System findBusinessSysById(String businessSysId) {
        return businessSysMapper.findBusinessSysById(businessSysId);
    }
}
