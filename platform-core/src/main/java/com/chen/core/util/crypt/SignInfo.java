package com.chen.core.util.crypt;

import java.io.Serializable;

//签名信息
public class SignInfo implements Serializable {
    //签名算法 oid 字符串，1.2.156.10197.1.501
    //必须
    private String signAlgorithm;
    //BASE64 编码的签名值,编码前的签名值长度为 64 字 节，其中 r 和 s 分别为 32 字节。该签名值是由各地区各部门政务服务平台电子印章系统的签名私钥进行签名生成的。signValue 的待签名数据格式见 6.3.3.1 中的待签名业务参数说明
    //必须
    private String signValue;

    /**
     {
         "signAlgorithm":"1.2.156.10197.1.501",
         "signValue ":" BASE64 编码的签名值"
     }
     */

    public SignInfo() {
    }

    public SignInfo(String signAlgorithm, String signValue) {
        this.signAlgorithm = signAlgorithm;
        this.signValue = signValue;
    }

    public String getSignAlgorithm() {
        return signAlgorithm;
    }

    public void setSignAlgorithm(String signAlgorithm) {
        this.signAlgorithm = signAlgorithm;
    }

    public String getSignValue() {
        return signValue;
    }

    public void setSignValue(String signValue) {
        this.signValue = signValue;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"signAlgorithm\":\"")
                .append(signAlgorithm).append('\"');
        sb.append(",\"signValue\":\"")
                .append(signValue).append('\"');
        sb.append('}');
        return sb.toString();
    }
}
