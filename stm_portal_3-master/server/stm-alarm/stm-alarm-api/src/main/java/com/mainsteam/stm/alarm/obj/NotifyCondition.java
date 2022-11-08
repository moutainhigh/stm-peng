package com.mainsteam.stm.alarm.obj;

/**
 * Created by Xiaopf on 8/2/2016.
 */
public class NotifyCondition {
    private long userid;

    private long ruleId = -1L;
    private long conditionId = -1L;
    private String sendWay;
    private int enabled = -1;
    private int continus = -1;
    private int continusCount = -1;
    private String continusUnit;
    private String alarmLevels;
    private boolean allTime;
    private boolean sendIntime;
    private String dayPeriodes;
    private String weekPeriodes;
    private long templateId;

    public String getDayPeriodes() {
        return this.dayPeriodes;
    }

    public void setDayPeriodes(String dayPeriodes) {
        this.dayPeriodes = dayPeriodes;
    }

    public String getWeekPeriodes() {
        return this.weekPeriodes;
    }

    public void setWeekPeriodes(String weekPeriodes) {
        this.weekPeriodes = weekPeriodes;
    }

    public boolean isAllTime() {
        return this.allTime;
    }

    public void setAllTime(boolean allTime) {
        this.allTime = allTime;
    }

    public boolean isSendIntime() {
        return this.sendIntime;
    }

    public void setSendIntime(boolean sendIntime) {
        this.sendIntime = sendIntime;
    }

    public final long getRuleId() {
        return this.ruleId;
    }

    public final void setRuleId(long ruleId) {
        this.ruleId = ruleId;
    }

    public final String getSendWay() {
        return this.sendWay;
    }

    public final void setSendWay(String sendWay) {
        this.sendWay = sendWay;
    }

    public final int getEnabled() {
        return this.enabled;
    }

    public final void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    public final int getContinus() {
        return this.continus;
    }

    public final void setContinus(int continus) {
        this.continus = continus;
    }

    public final int getContinusCount() {
        return this.continusCount;
    }

    public final void setContinusCount(int continusCount) {
        this.continusCount = continusCount;
    }

    public final String getContinusUnit() {
        return this.continusUnit;
    }

    public final void setContinusUnit(String continusUnit) {
        this.continusUnit = continusUnit;
    }

    public final String getAlarmLevels() {
        return this.alarmLevels;
    }

    public final void setAlarmLevels(String alarmLevels) {
        this.alarmLevels = alarmLevels;
    }

    public long getConditionId() {
        return this.conditionId;
    }

    public void setConditionId(long conditionId) {
        this.conditionId = conditionId;
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(long templateId) {
        this.templateId = templateId;
    }
}
