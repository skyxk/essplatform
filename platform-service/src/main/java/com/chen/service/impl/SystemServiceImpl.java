package com.chen.service.impl;

import com.chen.core.util.Base64Utils;
import com.chen.core.util.crypt.EncryptWithJMJ;
import com.chen.core.util.crypt.SM4Util;
import com.chen.dao.SystemInitMapper;
import com.chen.entity.EncryptorServer;
import com.chen.entity.SystemJurInit;
import com.chen.service.ISystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.chen.core.util.DateUtils.dateCompare;
import static com.chen.core.util.StringUtils.getUUID;

@Service
public class SystemServiceImpl implements ISystemService {
    @Autowired
    private SystemInitMapper systemInitMapper;
    @Value("${myConfig.securityType}")
    private int securityType;
    @Override
    public SystemJurInit findSystemInit() {
        return systemInitMapper.findSystemInit();
    }

    @Override
    public SystemJurInit findSystemInit(String unitId) {
        SystemJurInit result = null;
        result = systemInitMapper.findSystemInitByUnitId(unitId);
        if (result == null){
            result = systemInitMapper.findSystemInit();
        }
        return result;
    }

    @Override
    public boolean verifySystemTime() {
        //获取授权时间
        SystemJurInit systemJurInit = systemInitMapper.findSystemInit();
        if (systemJurInit ==null){
            return false;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date dateFirst = dateFormat.parse(systemJurInit.getAUTHENDTIME(),pos);
        //获取当前时间
        Date dateLast = new Date();
        int re =  dateCompare(dateFirst,dateLast);
        if (re==1){
            //未到授权日期
            return true;
        }
        return false;
    }
    @Override
    @Cacheable(value = "HelloWorldCache",key = "#root.targetClass.simpleName+':'+#root.methodName+':'+#str")
    public String encryptString(String str) {
        String token= systemInitMapper.findSignToken();
        try {
            if (str == null || "".equals(str.trim()) || token == null || "".equals(token)) {
                return null;
            }
            return Base64Utils.ESSGetBase64Encode((SM4Util.encryptByEcb0(str.getBytes(), token.getBytes())));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    @Override
    @Cacheable(value = "HelloWorldCache",key = "#root.targetClass.simpleName+':'+#root.methodName+':'+#str")
    public String decryptString(String str) {
        String token= systemInitMapper.findSignToken();
        try {
            if (str == null || "".equals(str.trim()) || token == null || "".equals(token)) {
                return null;
            }
            return new String(SM4Util.decryptByEcb0(Base64Utils.ESSGetBase64Decode(str),token.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(token);
            System.out.println(token);
            return null;
        }
    }
    @Override
    @Cacheable(value = "HelloWorldCache",key = "#root.targetClass.simpleName+':'+#root.methodName+':'+#str")
    public String encryptStringJMJ(String str) {
        if (securityType == 0){
            String token= systemInitMapper.findSignToken();
            try {
                if (str == null || "".equals(str.trim()) || token == null || "".equals(token)) {
                    return null;
                }
                return Base64Utils.ESSGetBase64Encode((SM4Util.encryptByEcb0(str.getBytes(), token.getBytes())));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }else if (securityType == 1){
            EncryptorServer encryptorServer = systemInitMapper.findEncryptorServer();
            if (str == null || "".equals(str.trim()) || encryptorServer == null) {
                return null;
            }
            String result = EncryptWithJMJ.EncryptIt(str,encryptorServer.getServer_ip(),encryptorServer.getServer_port(),
                    encryptorServer.getServer_password());
            if ("".equals(result)){
                return null;
            }
            return result;
        }
        return null;
    }
    @Override
    @Cacheable(value = "HelloWorldCache",key = "#root.targetClass.simpleName+':'+#root.methodName+':'+#str")
    public String decryptStringJMJ(String str) {
        if (securityType ==1){
            EncryptorServer encryptorServer = systemInitMapper.findEncryptorServer();
            if (str == null || "".equals(str.trim()) || encryptorServer == null) {
                return null;
            }
            String result = EncryptWithJMJ.DecryptIt(str,encryptorServer.getServer_ip(),encryptorServer.getServer_port(),
                    encryptorServer.getServer_password());
            if ("".equals(result)){
                return null;
            }
            return result;
        }else{
            String token= systemInitMapper.findSignToken();
            try {
                if (str == null || "".equals(str.trim()) || token == null || "".equals(token)) {
                    return null;
                }
                return new String(SM4Util.decryptByEcb0(Base64Utils.ESSGetBase64Decode(str),token.getBytes()));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    @Override
    public List<String> findFileType() {
        List<String> resultList = new ArrayList<>();
        SystemJurInit systemInit= systemInitMapper.findSystemInit();
        if (systemInit == null ){
            return null;
        }
        //格式  XXX@XXX@XXX
        try {
            String[] list = (systemInit.getPRODUCTS()).split("@");
            resultList.addAll(Arrays.asList(list));
            return resultList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
