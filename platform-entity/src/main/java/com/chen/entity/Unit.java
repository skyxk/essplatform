package com.chen.entity;


import java.util.Collections;
import java.util.List;

public class Unit implements Comparable<Unit>{

    private int score;
    private String unit_id;
    private String unit_name;
    private String credit_code;
    private String area_number;
    private int level;
    private String parent_unit_id;
    private String input_user_id;
    private String input_time;
    private String unit_name_en;
    private String unit_name_sn;
    private String unit_province;
    private String unit_city;
    private int province_num;
    private int state;

    private List<Unit> menus; //子菜单列表

    public String getUnit_id() {
        return unit_id;
    }

    public void setUnit_id(String unit_id) {
        this.unit_id = unit_id;
    }

    public String getUnit_name() {
        return unit_name;
    }

    public void setUnit_name(String unit_name) {
        this.unit_name = unit_name;
    }

    public String getCredit_code() {
        return credit_code;
    }

    public void setCredit_code(String credit_code) {
        this.credit_code = credit_code;
    }

    public String getArea_number() {
        return area_number;
    }

    public void setArea_number(String area_number) {
        this.area_number = area_number;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getParent_unit_id() {
        return parent_unit_id;
    }

    public void setParent_unit_id(String parent_unit_id) {
        this.parent_unit_id = parent_unit_id;
    }

    public String getInput_user_id() {
        return input_user_id;
    }

    public void setInput_user_id(String input_user_id) {
        this.input_user_id = input_user_id;
    }

    public String getInput_time() {
        return input_time;
    }

    public void setInput_time(String input_time) {
        this.input_time = input_time;
    }

    public String getUnit_name_en() {
        return unit_name_en;
    }

    public void setUnit_name_en(String unit_name_en) {
        this.unit_name_en = unit_name_en;
    }

    public String getUnit_name_sn() {
        return unit_name_sn;
    }

    public void setUnit_name_sn(String unit_name_sn) {
        this.unit_name_sn = unit_name_sn;
    }

    public String getUnit_province() {
        return unit_province;
    }

    public void setUnit_province(String unit_province) {
        this.unit_province = unit_province;
    }

    public String getUnit_city() {
        return unit_city;
    }

    public void setUnit_city(String unit_city) {
        this.unit_city = unit_city;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public List<Unit> getMenus() {
        return menus;
    }
    public void setMenus(List<Unit> menus) {
        Collections.sort(menus);
        this.menus = menus;
    }

    public int getProvince_num() {
        return province_num;
    }

    public void setProvince_num(int province_num) {
        this.province_num = province_num;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public int compareTo(Unit o) {
        int i = this.getProvince_num() - o.getProvince_num();//先按照年龄排序
        if(i == 0){
            return this.score - o.getScore();//如果年龄相等了再用分数进行排序
        }
        return i;
    }
}
