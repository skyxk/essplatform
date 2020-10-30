package com.chen.service.impl;

import com.chen.dao.IssuerUnitMapper;
import com.chen.entity.IssuerUnit;
import com.chen.service.IIssuerUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.chen.core.util.Base64Utils.ESSGetBase64Decode;
import static com.chen.core.util.crypt.ESSCertificate.VerifyIssuer;

/**
 * @author ：chen
 * @date ：Created in 2019/10/31 15:27
 */
@Service
public class IssuerUnitServiceImpl implements IIssuerUnitService {

    @Autowired
    IssuerUnitMapper issuerUnitMapper;

    /**
     * 根据颁发者单位查找颁发者单位信息
     * @param name 单位名称
     * @return 发者单位信息
     */
    @Override
    public IssuerUnit findIssuerUnitByName(String name){
        String cerName =  issuerUnitMapper.findIssuerUnitByName(name);
        System.out.println(cerName);
        IssuerUnit issuerUnit =  new IssuerUnit();
        issuerUnit.setIssuerUnitRoot(cerName);
        return issuerUnit;
    }

    /**
     * 验证证书是否可信任机构颁发的
     * @param certBase64 上传证书的base64编码数据
     * @return 是否可信任
     */
    @Override
    public boolean VerifyCert(String certBase64) {
        //将证书转换成二进制数组
        byte[] cer  = ESSGetBase64Decode(certBase64);
        //取到所有的可信任颁发者的根证书
        List<String> rootList = issuerUnitMapper.findTrustRoot();
        //遍历验证
        for (String root :rootList){
            byte[] rootByte = ESSGetBase64Decode(root);
            boolean result = VerifyIssuer(cer,rootByte);
            //当有一份验证通过时就返回true，否则继续循环至结束
            if (result){
                return true;
            }
        }
        return false;
    }


}
