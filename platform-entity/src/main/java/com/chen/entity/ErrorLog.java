package com.chen.entity;

public class ErrorLog {
    //ID
    private String errorLogId;
    //错误系统名称
    private String sysName;
    //详细描述
    private String errorDetail;
    //时间
    private String time;

    public String getErrorLogId() {
        return errorLogId;
    }

    public void setErrorLogId(String errorLogId) {
        this.errorLogId = errorLogId;
    }

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

    public String getErrorDetail() {
        return errorDetail;
    }

    public void setErrorDetail(String errorDetail) {
        this.errorDetail = errorDetail;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


}
