package com.mainsteam.stm.alarm.obj;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Xiaopf on 2016/5/12.
 */
public class AlarmConfirm implements Serializable{

    private static final long serialVersionUID = 521301239689293820L;

    private long confirmId;

    private long instanceId;

    private String metricId;

    private Date confirmTime;

    private boolean isOtherAlarm;

    public long getConfirmId() {
        return confirmId;
    }

    public void setConfirmId(long confirmId) {
        this.confirmId = confirmId;
    }

    public long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(long instanceId) {
        this.instanceId = instanceId;
    }

    public String getMetricId() {
        return metricId;
    }

    public void setMetricId(String metricId) {
        this.metricId = metricId;
    }

    public Date getConfirmTime() {
        return confirmTime;
    }

    public void setConfirmTime(Date confirmTime) {
        this.confirmTime = confirmTime;
    }

    public boolean isOtherAlarm() {
        return isOtherAlarm;
    }

    public void setOtherAlarm(boolean otherAlarm) {
        isOtherAlarm = otherAlarm;
    }

    @Override
    public String toString() {
        return "AlarmConfirm{" +
                "confirmId=" + confirmId +
                ", instanceId=" + instanceId +
                ", metricId='" + metricId + '\'' +
                ", confirmTime=" + confirmTime +
                ", isOtherAlarm=" + isOtherAlarm +
                '}';
    }
}
