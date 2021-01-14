package com.chen.platformpdf.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.chen.entity.Seal;
import com.chen.platformpdf.service.PowerService;
import org.springframework.stereotype.Service;

/**
 * @author ：chen
 * @date ：Created in 2019/11/8 9:32
 */
@Service
public class PowerServiceImpl implements PowerService {


    @Override
    public boolean checkPowerForSealByUser(Seal seal, String userId) {
//        String userId_chen = "0251022016295421464782bcb2e";//陈敬益
//        String userId_ding = "02510220162954fd414b95bb0bd";//丁华锋
//        if ("陈敬益".equals(seal.getSeal_name())){
//            if (userId.equals(userId_chen)){
//                return true;
//            }
//        }
//        if ("丁华锋".equals(seal.getSeal_name())){
//            if (userId.equals(userId_ding)){
//                return true;
//            }
//        }
//        if (seal.getSeal_name().contains("响水县")){
//            if (userId.equals(userId_chen)){
//                return true;
//            }
//        }
//        if (seal.getSeal_name().contains("淮安市")){
//            if (userId.equals(userId_ding)){
//                return true;
//            }
//        }
        return true;
    }

}
