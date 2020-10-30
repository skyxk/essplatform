package com.chen.entity;

public class Apply {
    private String apply_id;
    private String apply_type;
    private String seal_id;
    private String seal_name;
    private String img_id;
    private String cert_id;
    private String unit_id;
    private String seal_type_id;
    private String seal_standard;
    private int is_uk;
    private String device_code;
    private String seal_person_id;
    private String seal_start_time;
    private String seal_end_time;
    private String app_sym_key_enc;
    private String data_sym_key_enc;
    private String enc_file;
    private String usb_key_info;
    private String task_id;
    private String jbr_card_data;
    private String jbr_card_type;
    private String jbr_card_name;
    private String uk_id;
    private String file_type;
    private int apply_state;
    private String apply_user_id;
    private String apply_time;
    private String review_user_id;
    private String review_time;
    private String make_user_id;
    private String make_time;
    private String seal_code;
    private String temp_file;
    private int uk_type;

    private Unit unit;
    private User apply_user;
    private User review_user;
    private User make_user;

    private Certificate certificate;

    public String getApply_id() {
        return apply_id;
    }

    public void setApply_id(String apply_id) {
        this.apply_id = apply_id;
    }

    public String getApply_type() {
        return apply_type;
    }

    public void setApply_type(String apply_type) {
        this.apply_type = apply_type;
    }

    public String getSeal_id() {
        return seal_id;
    }

    public void setSeal_id(String seal_id) {
        this.seal_id = seal_id;
    }

    public String getSeal_name() {
        return seal_name;
    }

    public void setSeal_name(String seal_name) {
        this.seal_name = seal_name;
    }

    public String getImg_id() {
        return img_id;
    }

    public void setImg_id(String img_id) {
        this.img_id = img_id;
    }

    public String getCert_id() {
        return cert_id;
    }

    public void setCert_id(String cert_id) {
        this.cert_id = cert_id;
    }

    public String getUnit_id() {
        return unit_id;
    }

    public void setUnit_id(String unit_id) {
        this.unit_id = unit_id;
    }

    public String getSeal_type_id() {
        return seal_type_id;
    }

    public void setSeal_type_id(String seal_type_id) {
        this.seal_type_id = seal_type_id;
    }

    public String getSeal_standard() {
        return seal_standard;
    }

    public void setSeal_standard(String seal_standard) {
        this.seal_standard = seal_standard;
    }

    public int getIs_uk() {
        return is_uk;
    }

    public void setIs_uk(int is_uk) {
        this.is_uk = is_uk;
    }

    public String getDevice_code() {
        return device_code;
    }

    public void setDevice_code(String device_code) {
        this.device_code = device_code;
    }

    public String getSeal_person_id() {
        return seal_person_id;
    }

    public void setSeal_person_id(String seal_person_id) {
        this.seal_person_id = seal_person_id;
    }

    public String getSeal_start_time() {
        return seal_start_time;
    }

    public void setSeal_start_time(String seal_start_time) {
        this.seal_start_time = seal_start_time;
    }

    public String getSeal_end_time() {
        return seal_end_time;
    }

    public void setSeal_end_time(String seal_end_time) {
        this.seal_end_time = seal_end_time;
    }

    public String getApp_sym_key_enc() {
        return app_sym_key_enc;
    }

    public void setApp_sym_key_enc(String app_sym_key_enc) {
        this.app_sym_key_enc = app_sym_key_enc;
    }

    public String getData_sym_key_enc() {
        return data_sym_key_enc;
    }

    public void setData_sym_key_enc(String data_sym_key_enc) {
        this.data_sym_key_enc = data_sym_key_enc;
    }

    public String getEnc_file() {
        return enc_file;
    }

    public void setEnc_file(String enc_file) {
        this.enc_file = enc_file;
    }

    public String getUsb_key_info() {
        return usb_key_info;
    }

    public void setUsb_key_info(String usb_key_info) {
        this.usb_key_info = usb_key_info;
    }

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public String getJbr_card_data() {
        return jbr_card_data;
    }

    public void setJbr_card_data(String jbr_card_data) {
        this.jbr_card_data = jbr_card_data;
    }

    public String getJbr_card_type() {
        return jbr_card_type;
    }

    public void setJbr_card_type(String jbr_card_type) {
        this.jbr_card_type = jbr_card_type;
    }

    public String getJbr_card_name() {
        return jbr_card_name;
    }

    public void setJbr_card_name(String jbr_card_name) {
        this.jbr_card_name = jbr_card_name;
    }

    public String getUk_id() {
        return uk_id;
    }

    public void setUk_id(String uk_id) {
        this.uk_id = uk_id;
    }

    public String getFile_type() {
        return file_type;
    }

    public void setFile_type(String file_type) {
        this.file_type = file_type;
    }

    public int getApply_state() {
        return apply_state;
    }

    public void setApply_state(int apply_state) {
        this.apply_state = apply_state;
    }

    public String getApply_user_id() {
        return apply_user_id;
    }

    public void setApply_user_id(String apply_user_id) {
        this.apply_user_id = apply_user_id;
    }

    public String getApply_time() {
        return apply_time;
    }

    public void setApply_time(String apply_time) {
        this.apply_time = apply_time;
    }

    public String getReview_user_id() {
        return review_user_id;
    }

    public void setReview_user_id(String review_user_id) {
        this.review_user_id = review_user_id;
    }

    public String getReview_time() {
        return review_time;
    }

    public void setReview_time(String review_time) {
        this.review_time = review_time;
    }

    public String getMake_user_id() {
        return make_user_id;
    }

    public void setMake_user_id(String make_user_id) {
        this.make_user_id = make_user_id;
    }

    public String getMake_time() {
        return make_time;
    }

    public void setMake_time(String make_time) {
        this.make_time = make_time;
    }

    public String getSeal_code() {
        return seal_code;
    }

    public void setSeal_code(String seal_code) {
        this.seal_code = seal_code;
    }

    public String getTemp_file() {
        return temp_file;
    }

    public void setTemp_file(String temp_file) {
        this.temp_file = temp_file;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public User getApply_user() {
        return apply_user;
    }

    public void setApply_user(User apply_user) {
        this.apply_user = apply_user;
    }

    public User getReview_user() {
        return review_user;
    }

    public void setReview_user(User review_user) {
        this.review_user = review_user;
    }

    public User getMake_user() {
        return make_user;
    }

    public void setMake_user(User make_user) {
        this.make_user = make_user;
    }

    public Certificate getCertificate() {
        return certificate;
    }

    public void setCertificate(Certificate certificate) {
        this.certificate = certificate;
    }

    public int getUk_type() {
        return uk_type;
    }

    public void setUk_type(int uk_type) {
        this.uk_type = uk_type;
    }
}
