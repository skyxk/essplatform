package com.chen.entity;

public class SealImg {
    //印章图片id
    private String img_id;
    //jpg图片
    private String img_jpg;
    //印章图片base64
    private String img_gif_data;
    //印章图片hash
    private String img_hash;
    //图片高
    private int image_h;
    //图片宽
    private int image_w;
    //图片格式
    private String image_type;


    public String getImg_id() {
        return img_id;
    }

    public void setImg_id(String img_id) {
        this.img_id = img_id;
    }

    public String getImg_jpg() {
        return img_jpg;
    }

    public void setImg_jpg(String img_jpg) {
        this.img_jpg = img_jpg;
    }

    public String getImg_gif_data() {
        return img_gif_data;
    }

    public void setImg_gif_data(String img_gif_data) {
        this.img_gif_data = img_gif_data;
    }

    public String getImg_hash() {
        return img_hash;
    }

    public void setImg_hash(String img_hash) {
        this.img_hash = img_hash;
    }

    public int getImage_h() {
        return image_h;
    }

    public void setImage_h(int image_h) {
        this.image_h = image_h;
    }

    public int getImage_w() {
        return image_w;
    }

    public void setImage_w(int image_w) {
        this.image_w = image_w;
    }

    public String getImage_type() {
        return image_type;
    }

    public void setImage_type(String image_type) {
        this.image_type = image_type;
    }
}
