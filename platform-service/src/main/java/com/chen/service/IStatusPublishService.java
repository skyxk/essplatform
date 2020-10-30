package com.chen.service;

public interface IStatusPublishService {
    boolean sealStatusSync(String sealCode,String userName);
    boolean updateSealStatus(String sealCode,String userName,String state);

}
