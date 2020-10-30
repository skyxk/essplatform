package com.chen.entity;

public class SealType {
    //印章类型ID
    private String sealTypeId;
    //印章类型名称
    private String sealTypeName;
    //印章类型编号
    private String sealTypeNum;
    //顶级单位ID
    private String topUnitId;

    public String getSealTypeId() {
        return sealTypeId;
    }

    public void setSealTypeId(String sealTypeId) {
        this.sealTypeId = sealTypeId;
    }

    public String getSealTypeName() {
        return sealTypeName;
    }

    public void setSealTypeName(String sealTypeName) {
        this.sealTypeName = sealTypeName;
    }

    public String getSealTypeNum() {
        return sealTypeNum;
    }

    public void setSealTypeNum(String sealTypeNum) {
        this.sealTypeNum = sealTypeNum;
    }

    public String getTopUnitId() {
        return topUnitId;
    }

    public void setTopUnitId(String topUnitId) {
        this.topUnitId = topUnitId;
    }
}
