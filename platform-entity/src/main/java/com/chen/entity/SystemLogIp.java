package com.chen.entity;

public class SystemLogIp {

    private String sys_log_ip_id;
    private String sys_log_id;
    private String ip;
    private String mac;

    public String getSys_log_ip_id() {
        return sys_log_ip_id;
    }

    public void setSys_log_ip_id(String sys_log_ip_id) {
        this.sys_log_ip_id = sys_log_ip_id;
    }

    public String getSys_log_id() {
        return sys_log_id;
    }

    public void setSys_log_id(String sys_log_id) {
        this.sys_log_id = sys_log_id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}
