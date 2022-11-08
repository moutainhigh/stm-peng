package com.mainsteam.stm.alarm.notify.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.mainsteam.stm.alarm.obj.AlarmNotify;
import com.mainsteam.stm.alarm.obj.NotifyState;
import com.mainsteam.stm.alarm.obj.NotifyTypeEnum;
import com.mainsteam.stm.alarm.obj.AlarmEventWait;

public interface AlarmNotifyDAO {

	void addNotify(AlarmNotify notify);

	void addNotifyBatch(List<AlarmNotify> notifies);

	List<AlarmNotify> findByAlarmID(long alarmID);

	void update(AlarmNotify notify);
	
	void updateNotifyState(List<Long> notifyIDes,NotifyState state);

	AlarmNotify getNotifyByID(long notifyID);

	List<AlarmNotify> findByTime(NotifyTypeEnum type,Long userID,Date start, Date end);

	List<AlarmNotify> findByState(NotifyState state, NotifyTypeEnum sendWay) ;

	List<AlarmEventWait> findNotifyWait(Date startTime);

	void updateNotifyWait(AlarmEventWait eventWait);

	void deleteNotifyWait(long id);

	void deleteNotifyWaitByEventId(long eventId);

	void deleteNotifyWaitByRecoverKey(String recoverKey,long alarmEventID);

	void deleteNotifyWaitByRule(String recoverKey, long ruleId);

	void replaceNotifyWait(AlarmEventWait eventWait);

	void replaceNotifyWaitBatch(List<AlarmEventWait> eventWaits);

	void deleteNotifyWaitByRecoveryKey(String recoveryKey);

	void deleteNotifyWaitByParameters(Map<String, String> map);

	List<AlarmEventWait> findNotifyWaitByRecoveryKey(AlarmEventWait alarmEventWait);
	
	
}
