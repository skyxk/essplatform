package com.chen.entity;

public class SealSaveType {
    private int id;
    private String saveType_name;
    private String saveType_code;
    private int state;
    private int st_order;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSaveType_name() {
        return saveType_name;
    }

    public void setSaveType_name(String saveType_name) {
        this.saveType_name = saveType_name;
    }

    public String getSaveType_code() {
        return saveType_code;
    }

    public void setSaveType_code(String saveType_code) {
        this.saveType_code = saveType_code;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getSt_order() {
        return st_order;
    }

    public void setSt_order(int st_order) {
        this.st_order = st_order;
    }
}
