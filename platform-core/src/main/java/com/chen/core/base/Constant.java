package com.chen.core.base;

public class Constant {
    //状态常量
    public static final int STATE_YES = 1;
    public static final int STATE_NO = 0;

    //印章申请状态
    //待审核 .
    public static final int SUBMIT_APPLICATION = 1;
    //审核通过
    public static final int REVIEW_THROUGH = 2;
    //审核驳回
    public static final int STATE_10 = 10;
    //审核锁定
    public static final int REVIEW_NO_THROUGH = 3;
    //制作驳回
    public static final int MAKE_NO_THROUGH = 4;
    //制作 锁定
    public static final int MAKE_REJECT = 5;
    //制作完成
    public static final int MAKE_COMPLETION = 7;
    //制作失败
    public static final int MAKE_NO_COMPLETION = 8;
    //失效
    public static final int NO_AVAIL = 9;


    //申请审核制作信息类别
    //申请新印章
    public static final String APPLYTYPE_NEW = "1";
    //注册UK
    public static final String APPLYTYPE_REGISTER_UK = "2";
    //授权延期
    public static final String APPLY_TYPE_DELAY = "3";
    //证书延期
    public static final String APPLYTYPE_DELAY_CER = "4";
    //印章重做
    public static final String APPLYTYPE_REPEAT = "5";

    //顶级单位层级,多个单位(公司)使用同一套系统时,该值需要修改为0
    public static final int topUnitLevel=1;
    //每个公司的层级 一级单位的层级
    public static final int  companyLevel=1;

    public static String PFX_FILE_PATH = "D:\\temp\\pfxTemp\\";


    /**
     * 消息中心 消息类别
     */
    //驳回
    public static final String Message_Type_reject = "1";
    //注册
    public static final String Message_Type_register = "2";
    //重做
    public static final String Message_Type_reMake = "3";
    //授权延期
    public static final String Message_Type_auDelay = "4";
    //证书延期
    public static final String Message_Type_cerDelay = "5";




}
