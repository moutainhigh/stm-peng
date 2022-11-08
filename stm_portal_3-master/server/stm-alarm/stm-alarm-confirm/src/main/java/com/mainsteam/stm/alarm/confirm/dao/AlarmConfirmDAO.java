package com.mainsteam.stm.alarm.confirm.dao;


import com.mainsteam.stm.alarm.obj.AlarmConfirm;

import java.util.List;

/**
 * Created by Xiaopf on 2016/5/12.
 */
public interface AlarmConfirmDAO {

    public int addAlarmConfirm(AlarmConfirm alarmConfirm);

    public int deleteAlarmConfirm(AlarmConfirm alarmConfirm);

    public int updateAlarmConfirm(AlarmConfirm alarmConfirm);

    public AlarmConfirm findAlarmConfirm(AlarmConfirm alarmConfirm);

    public List<AlarmConfirm> findAll();
}
