package com.chen.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chen.core.bean.HttpResult;
import com.chen.core.util.HttpHelper;
import com.chen.core.util.HttpUtils;
import com.chen.service.IStatusPublishService;

import com.chen.service.ISystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class StatusPublishServiceImpl implements IStatusPublishService {

    @Value("${myConfig.hostURL}")
    public String hostURL;

    @Autowired
    public ISystemService systemService;
    /**
     * 状态发布：生成 有效
     * @param sealCode 印章编码
     * @return 状态发布结果
     */
    @Override
    public boolean sealStatusSync(String sealCode,String userName) {

        String sealStatusSyncAnd_url = hostURL + "/ess_seal_status_publish/sealstatus/sealStatusSyncAnd";

        JSONObject jsonObject  = new JSONObject();
        jsonObject.put("sealCode",sealCode);
        jsonObject.put("userName",userName);
        JSONObject aa = HttpUtils.post(sealStatusSyncAnd_url,jsonObject.toJSONString());
        if ("200".equals(aa.getString("code"))){
            JSONObject js1 = JSONObject.parseObject(aa.getString("result"));
            return "true".equals(js1.getString("success"));
        }
        return false;
    }

    /**
     * 状态发布：撤销 无效
     * @param sealCode 印章编码
     * @return 状态发布结果
     */
    @Override
    public boolean updateSealStatus(String sealCode,String userName,String state) {
        String updateSealStatus_url = hostURL + "/ess_seal_status_publish/sealstatus/updateSealStatus";
        //0：因密钥泄露撤销；
        //1：因信息变更撤销；
        //2：因业务终止撤销；
        //3：因印章丢失挂失；
        //4：因其他原因撤销；
        //5：因虚假注册吊销；
        //6：因超范围经营吊销；
        //7：无故不开业或停业吊销；
        //8：因单位逾期不办理年检手续吊销；
        //9：因其他原因吊销
        JSONObject jsonObject  = new JSONObject();
        jsonObject.put("sealCode",sealCode);
        jsonObject.put("userName",userName);
        jsonObject.put("sealStatus",state);
        JSONObject aa = HttpUtils.post(updateSealStatus_url,jsonObject.toJSONString());
        if ("200".equals(aa.getString("code"))){
            JSONObject js1 = JSONObject.parseObject(aa.getString("result"));
            return "true".equals(js1.getString("success"));
        }
        return false;
    }
}
