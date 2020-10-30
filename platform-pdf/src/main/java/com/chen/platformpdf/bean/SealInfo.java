package com.chen.platformpdf.bean;

public class SealInfo {
    //用于定位印章位置的关键字
    private String sKeyWord;
    //关键字第几次出现（从1开始，-1表示最后一次,-2表示所有）
    private int	iLocationMode;
    //印章位置相对于关键字的水平偏移量，未定义关键字时，以PDF文档的左上角计算偏移量
    private int	iOffsetX;
    //印章位置相对于关键字的垂直偏移量，未定义关键字时，以PDF文档的左上角计算偏移量
    private int iOffsetY;
    //印章所在页码
    private int pageNum = 1;
    //印章图片宽度
    private int iSealImgW = 1;
    //印章图片宽度
    private int iSealImgH = 1;
    //单位ID
    private String sUnitId;
    //员工ID
    private String sUserId;
    //印章类型
    private String sSealType;
    //印章ID
    private String sSealID;
    //签章模式
    private int	iSignatureType;

    public String getsKeyWord() {
        return sKeyWord;
    }

    public void setsKeyWord(String sKeyWord) {
        this.sKeyWord = sKeyWord;
    }

    public int getiLocationMode() {
        return iLocationMode;
    }

    public void setiLocationMode(int iLocationMode) {
        this.iLocationMode = iLocationMode;
    }

    public int getiOffsetX() {
        return iOffsetX;
    }

    public void setiOffsetX(int iOffsetX) {
        this.iOffsetX = iOffsetX;
    }

    public int getiOffsetY() {
        return iOffsetY;
    }

    public void setiOffsetY(int iOffsetY) {
        this.iOffsetY = iOffsetY;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getiSealImgW() {
        return iSealImgW;
    }

    public void setiSealImgW(int iSealImgW) {
        this.iSealImgW = iSealImgW;
    }

    public int getiSealImgH() {
        return iSealImgH;
    }

    public void setiSealImgH(int iSealImgH) {
        this.iSealImgH = iSealImgH;
    }

    public String getsUnitId() {
        return sUnitId;
    }

    public void setsUnitId(String sUnitId) {
        this.sUnitId = sUnitId;
    }

    public String getsUserId() {
        return sUserId;
    }

    public void setsUserId(String sUserId) {
        this.sUserId = sUserId;
    }

    public String getsSealType() {
        return sSealType;
    }

    public void setsSealType(String sSealType) {
        this.sSealType = sSealType;
    }

    public String getsSealID() {
        return sSealID;
    }

    public void setsSealID(String sSealID) {
        this.sSealID = sSealID;
    }

    public int getiSignatureType() {
        return iSignatureType;
    }

    public void setiSignatureType(int iSignatureType) {
        this.iSignatureType = iSignatureType;
    }
}
