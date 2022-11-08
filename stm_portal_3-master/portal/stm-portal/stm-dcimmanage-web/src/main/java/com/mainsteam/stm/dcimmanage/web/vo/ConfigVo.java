package com.mainsteam.stm.dcimmanage.web.vo;

public class ConfigVo {

    private int id;

    private String name;

    private String gainPropertyUrl;

    private String sendPropertyUrl;

    private String alarmUrl;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGainPropertyUrl() {
        return gainPropertyUrl;
    }

    public void setGainPropertyUrl(String gainPropertyUrl) {
        this.gainPropertyUrl = gainPropertyUrl;
    }

    public String getSendPropertyUrl() {
        return sendPropertyUrl;
    }

    public void setSendPropertyUrl(String sendPropertyUrl) {
        this.sendPropertyUrl = sendPropertyUrl;
    }

    public String getAlarmUrl() {
        return alarmUrl;
    }

    public void setAlarmUrl(String alarmUrl) {
        this.alarmUrl = alarmUrl;
    }
}
