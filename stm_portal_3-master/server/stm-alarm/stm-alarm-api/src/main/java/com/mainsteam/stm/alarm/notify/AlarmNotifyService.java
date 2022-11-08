package com.mainsteam.stm.alarm.notify;

import java.util.Date;
import java.util.List;

import com.mainsteam.stm.alarm.obj.*;

/**
 * @author heshengchao
 */
public interface AlarmNotifyService {

	 void addNotify(List<AlarmNotify> notifies, String recoveryKey, long alarmEventID);
	
	 List<AlarmNotify> findByAlarmID(long alarmID);
	
	/** 查询指定ID后的最新数据
	 * @param notifyID
	 * @return
	 */
	 AlarmNotify getNotifyByID(long notifyID);
	
	/**
	 * @param notifyIDes
	 */
	 void updateNotifyState(List<Long> notifyIDes,NotifyState state);
	
	/**
	 * @param type
	 * @param start
	 * @param end
	 * @return
	 */
	 List<AlarmNotify> findByTime(NotifyTypeEnum type,Long userID,Date start,Date end);
	
	 void deleteAlarmNotifyWaitByEventId(long eventID);

	 void deleteAlarmNotifyWaitByRecoveryKey(String recoveryKey);

	 List<AlarmEventWait> findNotifyWaitByRecoveryKey(String recoveryKey);

	 void deleteAlarmNotifyWaitById(long id);

	 void deleteAlarmWaitByRule(String recoveryKey, long ruleId);

	 void deleteAlarmNotifyWaitByInsts(boolean isParent, long instanceId);

}
