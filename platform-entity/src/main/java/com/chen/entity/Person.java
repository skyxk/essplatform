package com.chen.entity;

public class Person {
    //人员ID
    private String person_id;
    //姓名
    private String person_name;
    //手机号
    private String name_ap;
    //身份证号
    private String id_num;
    //头像base64
    private String img_base64;
    //性别
    private int sex;
    //状态
    private int state;
    //统一人员id
    private String provincial_user_id;
    //证书ID
    private String cert_id;
    //人员编号
    private String emp_num;

    public String getPerson_id() {
        return person_id;
    }

    public void setPerson_id(String person_id) {
        this.person_id = person_id;
    }

    public String getPerson_name() {
        return person_name;
    }

    public void setPerson_name(String person_name) {
        this.person_name = person_name;
    }

    public String getName_ap() {
        return name_ap;
    }

    public void setName_ap(String name_ap) {
        this.name_ap = name_ap;
    }

    public String getId_num() {
        return id_num;
    }

    public void setId_num(String id_num) {
        this.id_num = id_num;
    }

    public String getImg_base64() {
        return img_base64;
    }

    public void setImg_base64(String img_base64) {
        this.img_base64 = img_base64;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getProvincial_user_id() {
        return provincial_user_id;
    }

    public void setProvincial_user_id(String provincial_user_id) {
        this.provincial_user_id = provincial_user_id;
    }

    public String getCert_id() {
        return cert_id;
    }

    public void setCert_id(String cert_id) {
        this.cert_id = cert_id;
    }

    public String getEmp_num() {
        return emp_num;
    }

    public void setEmp_num(String emp_num) {
        this.emp_num = emp_num;
    }
}
