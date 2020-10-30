package com.chen.dao;

import com.chen.entity.Business_System;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessSysMapper {
    List<String> findAllServerIp();

    Business_System findBusinessSysById(String b_sys_id);
}
