package com.chen.entity;

public class Certificate {
    //证书id
    private String cert_id;
    private String algorithm;
    //证书名称
    private String cert_name;
    private String country;
    private String province;
    private String city;
    private String unit;

    private String department;

    private String issuer;
    private String issuer_unit;
    private String cer_base64;
    private String pfx_base64;
    private String start_time;
    private String end_time;
    private String apply_time;
    private String cert_psw;
    private int cert_state;
    private int state;

    public String getCert_id() {
        return cert_id;
    }

    public void setCert_id(String cert_id) {
        this.cert_id = cert_id;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getCert_name() {
        return cert_name;
    }

    public void setCert_name(String cert_name) {
        this.cert_name = cert_name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getIssuer_unit() {
        return issuer_unit;
    }

    public void setIssuer_unit(String issuer_unit) {
        this.issuer_unit = issuer_unit;
    }

    public String getCer_base64() {
        return cer_base64;
    }

    public void setCer_base64(String cer_base64) {
        this.cer_base64 = cer_base64;
    }

    public String getPfx_base64() {
        return pfx_base64;
    }

    public void setPfx_base64(String pfx_base64) {
        this.pfx_base64 = pfx_base64;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getApply_time() {
        return apply_time;
    }

    public void setApply_time(String apply_time) {
        this.apply_time = apply_time;
    }

    public String getCert_psw() {
        return cert_psw;
    }

    public void setCert_psw(String cert_psw) {
        this.cert_psw = cert_psw;
    }

    public int getCert_state() {
        return cert_state;
    }

    public void setCert_state(int cert_state) {
        this.cert_state = cert_state;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
