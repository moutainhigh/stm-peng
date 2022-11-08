package com.mainsteam.stm.alarm.event;

import com.mainsteam.stm.alarm.obj.AlarmEvent;

/** 告警监控接口
 * @author cx
 *
 */
public interface AlarmEventMonitor {

	void handleEvent(AlarmEvent event);
}
