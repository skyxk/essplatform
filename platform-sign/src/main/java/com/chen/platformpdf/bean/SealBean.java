package com.chen.platformpdf.bean;

/**
 * 这个类是VO类，用于向页面提供组织印章参数
 * @author ：chen
 * @date ：Created in 2019/10/9 13:43
 */
public class SealBean {
    private String imgBase64;
    private String width;
    private String height;
    private String sealId;

    public String getImgBase64() {
        return imgBase64;
    }

    public void setImgBase64(String imgBase64) {
        this.imgBase64 = imgBase64;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getSealId() {
        return sealId;
    }

    public void setSealId(String sealId) {
        this.sealId = sealId;
    }
}
