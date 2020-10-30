package com.chen.entity;

public class IssuerUnit {
    //证书颁发机构Id
    private String IssuerUnitId;
    //证书颁发机构名称
    private String IssuerUnitName;
    //证书颁发机构签名根证书pfx
    private String IssuerUnitPfx;
    //证书颁发机构根证书cer
    private String IssuerUnitRoot;
    //根证书密钥
    private String PfxPwd;
    //根证书密钥
    private String bc_pub;
    //根证书密钥
    private String bc_prv;
    //是否支持生成证书
    private int isCreateCer;
    //状态
    private int state;

    public String getIssuerUnitId() {
        return IssuerUnitId;
    }

    public void setIssuerUnitId(String issuerUnitId) {
        IssuerUnitId = issuerUnitId;
    }

    public String getIssuerUnitName() {
        return IssuerUnitName;
    }

    public void setIssuerUnitName(String issuerUnitName) {
        IssuerUnitName = issuerUnitName;
    }

    public String getIssuerUnitPfx() {
        return IssuerUnitPfx;
    }

    public void setIssuerUnitPfx(String issuerUnitPfx) {
        IssuerUnitPfx = issuerUnitPfx;
    }

    public String getIssuerUnitRoot() {
        return IssuerUnitRoot;
    }

    public void setIssuerUnitRoot(String issuerUnitRoot) {
        IssuerUnitRoot = issuerUnitRoot;
    }

    public String getPfxPwd() {
        return PfxPwd;
    }

    public void setPfxPwd(String pfxPwd) {
        PfxPwd = pfxPwd;
    }

    public int getIsCreateCer() {
        return isCreateCer;
    }

    public void setIsCreateCer(int isCreateCer) {
        this.isCreateCer = isCreateCer;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getBc_pub() {
        return bc_pub;
    }

    public void setBc_pub(String bc_pub) {
        this.bc_pub = bc_pub;
    }

    public String getBc_prv() {
        return bc_prv;
    }

    public void setBc_prv(String bc_prv) {
        this.bc_prv = bc_prv;
    }

    @Override
    public String toString() {
        return "IssuerUnit{" +
                "IssuerUnitId='" + IssuerUnitId + '\'' +
                ", IssuerUnitName='" + IssuerUnitName + '\'' +
                ", IssuerUnitPfx='" + IssuerUnitPfx + '\'' +
                ", IssuerUnitRoot='" + IssuerUnitRoot + '\'' +
                ", PfxPwd='" + PfxPwd + '\'' +
                ", isCreateCer=" + isCreateCer +
                ", state=" + state +
                '}';
    }
}
