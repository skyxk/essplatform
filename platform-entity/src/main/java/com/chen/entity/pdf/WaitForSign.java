package com.chen.entity.pdf;

public class WaitForSign {
    private String UUID;
    private String UKID;
    private String HASH;
    private String SIGNVAL;
    private Integer NOWSTATUS;
    private String SUBTIME;

    public WaitForSign() {
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getUKID() {
        return UKID;
    }

    public void setUKID(String UKID) {
        this.UKID = UKID;
    }

    public String getHASH() {
        return HASH;
    }

    public void setHASH(String HASH) {
        this.HASH = HASH;
    }

    public String getSIGNVAL() {
        return SIGNVAL;
    }

    public void setSIGNVAL(String SIGNVAL) {
        this.SIGNVAL = SIGNVAL;
    }

    public Integer getNOWSTATUS() {
        return NOWSTATUS;
    }

    public void setNOWSTATUS(Integer NOWSTATUS) {
        this.NOWSTATUS = NOWSTATUS;
    }

    public String getSUBTIME() {
        return SUBTIME;
    }

    public void setSUBTIME(String SUBTIME) {
        this.SUBTIME = SUBTIME;
    }
}
