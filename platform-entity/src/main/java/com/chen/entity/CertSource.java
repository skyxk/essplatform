package com.chen.entity;

public class CertSource {
    private int id;
    private String source_name;
    private String source_code;
    private int state;
    private int so_order;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSource_name() {
        return source_name;
    }

    public void setSource_name(String source_name) {
        this.source_name = source_name;
    }

    public String getSource_code() {
        return source_code;
    }

    public void setSource_code(String source_code) {
        this.source_code = source_code;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getSo_order() {
        return so_order;
    }

    public void setSo_order(int so_order) {
        this.so_order = so_order;
    }
}
