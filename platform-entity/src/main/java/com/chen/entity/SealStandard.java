package com.chen.entity;

public class SealStandard {
    private int id;
    private String standard_name;
    private String standard_code;
    private int state;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStandard_name() {
        return standard_name;
    }

    public void setStandard_name(String standard_name) {
        this.standard_name = standard_name;
    }

    public String getStandard_code() {
        return standard_code;
    }

    public void setStandard_code(String standard_code) {
        this.standard_code = standard_code;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
