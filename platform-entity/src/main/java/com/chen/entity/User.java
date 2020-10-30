package com.chen.entity;

public class User {
    //用户id
    private String user_id;
    //所属部门id  如果是管理员角色可能没有部门id
    private String dep_id;
    //所属单位id
    private String unit_id;
    //管理范围 1-6
    private int power_range;
    //角色id
    private String role_id;
    //用户层级
    private int u_level;
    //人员id
    private String person_id;
    //是否已激活 1已激活 0未激活
    private int is_active;
    //用户状态 1有效 0无效
    private int state;
    //录入时间
    private String input_time;
    //录入人用户id
    private String input_user_id;

    private String signSafeLevel;

    private Person person;

    private Unit unit;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDep_id() {
        return dep_id;
    }

    public void setDep_id(String dep_id) {
        this.dep_id = dep_id;
    }

    public String getUnit_id() {
        return unit_id;
    }

    public void setUnit_id(String unit_id) {
        this.unit_id = unit_id;
    }

    public int getPower_range() {
        return power_range;
    }

    public void setPower_range(int power_range) {
        this.power_range = power_range;
    }

    public String getRole_id() {
        return role_id;
    }

    public void setRole_id(String role_id) {
        this.role_id = role_id;
    }

    public int getU_level() {
        return u_level;
    }

    public void setU_level(int u_level) {
        this.u_level = u_level;
    }

    public String getPerson_id() {
        return person_id;
    }

    public void setPerson_id(String person_id) {
        this.person_id = person_id;
    }

    public int getIs_active() {
        return is_active;
    }

    public void setIs_active(int is_active) {
        this.is_active = is_active;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
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

    public String getSignSafeLevel() {
        return signSafeLevel;
    }

    public void setSignSafeLevel(String signSafeLevel) {
        this.signSafeLevel = signSafeLevel;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }
}
