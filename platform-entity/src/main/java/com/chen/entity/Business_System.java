package com.chen.entity;

/**
 * 业务系统类
 * @author ：chen
 * @date ：Created in 2019/9/25 15:20
 */
public class Business_System {
    //业务系统Id 区号+uuid
    private String b_sys_id;
    //业务系统名称
    private String b_name;
    //服务器ip,多个以@间隔
    private String server_ip;
    //顶级单位id
    private String top_unit_id;

    public String getB_sys_id() {
        return b_sys_id;
    }

    public void setB_sys_id(String b_sys_id) {
        this.b_sys_id = b_sys_id;
    }

    public String getB_name() {
        return b_name;
    }

    public void setB_name(String b_name) {
        this.b_name = b_name;
    }

    public String getServer_ip() {
        return server_ip;
    }

    public void setServer_ip(String server_ip) {
        this.server_ip = server_ip;
    }

    public String getTop_unit_id() {
        return top_unit_id;
    }

    public void setTop_unit_id(String top_unit_id) {
        this.top_unit_id = top_unit_id;
    }
}
