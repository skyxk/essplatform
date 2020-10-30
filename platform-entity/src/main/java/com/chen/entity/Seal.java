package com.chen.entity;

public class Seal {
    //印章id
    private String seal_id;
    //印章id
    private String seal_code;
    //印章名称
    private String seal_name;
    //印章图片Id
    private String seal_img_id;
    //证书id
    private String seal_cert_id;

    private String seal_type_id;

    private String seal_person_id;
    //所属单位id
    private String unit_id;

    private String seal_start_time;

    private String seal_end_time;

    private String device_code;

    private int is_uk;

    private String seal_standard;

    private String app_sym_key_enc;

    private String data_sym_key_enc;

    private String enc_file;

    private String usb_key_info;

    private String task_id;
    private String signature;
    private String input_time;
    private String input_user_id;
    private String state;
    private String jbr_card_data;
    private String jbr_card_type;
    private String jbr_card_name;
    private String uk_id;
    private String file_type;
    private int uk_type;
    private SealImg sealImg;
    private Certificate certificate;


    public String getSeal_id() {
        return seal_id;
    }

    public void setSeal_id(String seal_id) {
        this.seal_id = seal_id;
    }

    public String getSeal_code() {
        return seal_code;
    }

    public void setSeal_code(String seal_code) {
        this.seal_code = seal_code;
    }

    public String getSeal_name() {
        return seal_name;
    }

    public void setSeal_name(String seal_name) {
        this.seal_name = seal_name;
    }

    public String getSeal_img_id() {
        return seal_img_id;
    }

    public void setSeal_img_id(String seal_img_id) {
        this.seal_img_id = seal_img_id;
    }

    public String getSeal_cert_id() {
        return seal_cert_id;
    }

    public void setSeal_cert_id(String seal_cert_id) {
        this.seal_cert_id = seal_cert_id;
    }

    public String getSeal_type_id() {
        return seal_type_id;
    }

    public void setSeal_type_id(String seal_type_id) {
        this.seal_type_id = seal_type_id;
    }

    public String getSeal_person_id() {
        return seal_person_id;
    }

    public void setSeal_person_id(String seal_person_id) {
        this.seal_person_id = seal_person_id;
    }

    public String getUnit_id() {
        return unit_id;
    }

    public void setUnit_id(String unit_id) {
        this.unit_id = unit_id;
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

    public String getDevice_code() {
        return device_code;
    }

    public void setDevice_code(String device_code) {
        this.device_code = device_code;
    }

    public int getIs_uk() {
        return is_uk;
    }

    public void setIs_uk(int is_uk) {
        this.is_uk = is_uk;
    }

    public String getSeal_standard() {
        return seal_standard;
    }

    public void setSeal_standard(String seal_standard) {
        this.seal_standard = seal_standard;
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

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getInput_time() {
        return input_time;
    }

    public void setInput_time(String input_time) {
        this.input_time = input_time;
    }

    public String getInput_user_id() {
        return input_user_id;
    }

    public void setInput_user_id(String input_user_id) {
        this.input_user_id = input_user_id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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

    public SealImg getSealImg() {
        return sealImg;
    }

    public void setSealImg(SealImg sealImg) {
        this.sealImg = sealImg;
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
