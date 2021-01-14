package com.chen.platformpdf.dto;

public class SealDto {

    private String sealId;
    private String sealName;
    private String seal_cert;
    private String seal_pfx;
    private String seal_img;
    private String seal_standard;
    private String seal_type;
    private String uk_id;
    private String seal_pwd;
    private int seal_w;
    private int seal_h;

    public String getSealId() {
        return sealId;
    }

    public void setSealId(String sealId) {
        this.sealId = sealId;
    }

    public String getSealName() {
        return sealName;
    }

    public void setSealName(String sealName) {
        this.sealName = sealName;
    }

    public String getSeal_cert() {
        return seal_cert;
    }

    public void setSeal_cert(String seal_cert) {
        this.seal_cert = seal_cert;
    }

    public String getSeal_pfx() {
        return seal_pfx;
    }

    public void setSeal_pfx(String seal_pfx) {
        this.seal_pfx = seal_pfx;
    }

    public String getSeal_img() {
        return seal_img;
    }

    public void setSeal_img(String seal_img) {
        this.seal_img = seal_img;
    }

    public String getSeal_standard() {
        return seal_standard;
    }

    public void setSeal_standard(String seal_standard) {
        this.seal_standard = seal_standard;
    }

    public String getSeal_pwd() {
        return seal_pwd;
    }

    public void setSeal_pwd(String seal_pwd) {
        this.seal_pwd = seal_pwd;
    }

    public String getSeal_type() {
        return seal_type;
    }

    public void setSeal_type(String seal_type) {
        this.seal_type = seal_type;
    }

    public int getSeal_w() {
        return seal_w;
    }
    public void setSeal_w(int seal_w) {
        this.seal_w = seal_w;
    }

    public int getSeal_h() {
        return seal_h;
    }

    public void setSeal_h(int seal_h) {
        this.seal_h = seal_h;
    }

    public String getUk_id() {
        return uk_id;
    }

    public void setUk_id(String uk_id) {
        this.uk_id = uk_id;
    }
}
