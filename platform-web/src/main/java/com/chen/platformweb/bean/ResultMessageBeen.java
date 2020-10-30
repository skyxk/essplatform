package com.chen.platformweb.bean;

/**
 * 添加手写签名制作申请时查找人员信息时用到的返回类
 */
public class ResultMessageBeen {
    private String message;
    private Object body;
    public ResultMessageBeen() { }
    public ResultMessageBeen(String message, Object body) {
        message = message;
        body = body;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }
}
