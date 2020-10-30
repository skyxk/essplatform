package com.chen.entity;

public class UKDll {
    private int id;
    private String type_name;
    private String type_code;
    private String dll_name;
    private int seal_standard;
    private int state;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }

    public String getType_code() {
        return type_code;
    }

    public void setType_code(String type_code) {
        this.type_code = type_code;
    }

    public String getDll_name() {
        return dll_name;
    }

    public void setDll_name(String dll_name) {
        this.dll_name = dll_name;
    }

    public int getSeal_standard() {
        return seal_standard;
    }

    public void setSeal_standard(int seal_standard) {
        this.seal_standard = seal_standard;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
