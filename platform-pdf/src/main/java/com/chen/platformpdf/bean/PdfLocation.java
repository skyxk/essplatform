package com.chen.platformpdf.bean;

/**
 * 这个类是用于存放pdf 的定位信息的
 * @author ：chen
 * @date ：Created in 2019/11/4 14:24
 */
public class PdfLocation {
    //x坐标
    private float y;
    //y坐标
    private float x;
    //宽度
    private double width;
    //高度
    private double height;
    //所在页码
    private int pageNum;

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }
}
