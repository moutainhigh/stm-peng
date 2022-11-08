package com.mainsteam.stm.alarm.confirm.dao;

import com.mainsteam.stm.alarm.obj.AlarmConfirm;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 * Created by Xiaopf on 2016/5/12.
 */
public class AlarmConfirmDAOImpl implements AlarmConfirmDAO{

    private SqlSession session;

    public void setSession(SqlSession session) {
        this.session = session;
    }

    @Override
    public int addAlarmConfirm(AlarmConfirm alarmConfirm) {
        return session.insert("addAlarmConfirm",alarmConfirm);
    }

    @Override
    public int deleteAlarmConfirm(AlarmConfirm alarmConfirm) {
        return session.delete("deleteAlarmConfirm", alarmConfirm);
    }

    @Override
    public int updateAlarmConfirm(AlarmConfirm alarmConfirm) {
        return 0;
    }

    @Override
    public AlarmConfirm findAlarmConfirm(AlarmConfirm alarmConfirm) {
        return session.selectOne("findAlarmConfirm", alarmConfirm);
    }

    @Override
    public List<AlarmConfirm> findAll() {
        return session.selectList("findAll");
    }
}
