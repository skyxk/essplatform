package com.chen.platformpdf.bean;

/**
 * 用于存放验证信息
 * @author ：chen
 * @date ：Created in 2019/10/30 15:41
 */
public class VerifyResult {
    private String sealName;
    private String documentVerify;
    private String completeVerify;
    private String certVerify;

    public String getSealName() {
        return sealName;
    }

    public void setSealName(String sealName) {
        this.sealName = sealName;
    }

    public String getDocumentVerify() {
        return documentVerify;
    }

    public void setDocumentVerify(String documentVerify) {
        this.documentVerify = documentVerify;
    }

    public String getCompleteVerify() {
        return completeVerify;
    }

    public void setCompleteVerify(String completeVerify) {
        this.completeVerify = completeVerify;
    }

    public String getCertVerify() {
        return certVerify;
    }

    public void setCertVerify(String certVerify) {
        this.certVerify = certVerify;
    }
}
