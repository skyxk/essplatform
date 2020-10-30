package com.chen.entity;

public class Department {
    //部门ID
    private String dep_id;
    //部门名称
    private String dep_name;
    //录入时间
    private String input_time;
    //录入人
    private String input_user_id;
    //单位ID
    private String unit_id;
    //状态
    private int state;

    public String getDep_id() {
        return dep_id;
    }

    public void setDep_id(String dep_id) {
        this.dep_id = dep_id;
    }

    public String getDep_name() {
        return dep_name;
    }

    public void setDep_name(String dep_name) {
        this.dep_name = dep_name;
    }

    public String getInput_time() {
        return input_time;
    }

    public void setInput_time(String input_time) {
        this.input_time = input_time;
    }

    public String getInput_user_id() {
        return input_user_id;
    }

    public void setInput_user_id(String input_user_id) {
        this.input_user_id = input_user_id;
    }

    public String getUnit_id() {
        return unit_id;
    }

    public void setUnit_id(String unit_id) {
        this.unit_id = unit_id;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
