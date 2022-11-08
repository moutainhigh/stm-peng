package com.mainsteam.stm.alarm.confirm;


import com.mainsteam.stm.alarm.obj.AlarmConfirm;

/**
 * Created by Xiaopf on 2016/5/12.
 */
public interface AlarmConfirmService {

    public int addAlarmConfirm(AlarmConfirm alarmConfirm);

    public int deleteAlarmConfirm(AlarmConfirm alarmConfirm);

    public String confirmAlarmEventById(String alarmID, final String instanceID, final String metricId, boolean isOther);

    public AlarmConfirm findAlarmConfirm(AlarmConfirm alarmConfirm);

    public boolean getAlarmConfirmBlocked(AlarmConfirm alarmConfirm);
}
