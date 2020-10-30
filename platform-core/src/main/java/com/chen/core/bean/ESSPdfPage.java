package com.chen.core.bean;

/**
 * 用于保存PDF转换图片后的图片对象
 * @author ：chen
 * @date ：Created in 2019/9/27 14:35
 */
public class ESSPdfPage {
    private String picPath; //图片的地址
    private String pageSize;//图片的大小

    public ESSPdfPage() {
        super();
    }
    public ESSPdfPage(String picPath, String pageSize) {
        super();
        this.picPath = picPath;
        this.pageSize = pageSize;
    }

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

}
