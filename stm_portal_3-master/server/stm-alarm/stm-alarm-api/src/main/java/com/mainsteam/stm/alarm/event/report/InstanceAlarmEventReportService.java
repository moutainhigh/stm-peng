package com.mainsteam.stm.alarm.event.report;

import java.util.List;

import com.mainsteam.stm.alarm.obj.AlarmEvent;

public interface InstanceAlarmEventReportService {
	
	public List<InstanceAlarmEventReportData> findTotleAlarmReport(InstanceAlarmEventReportQuery query);

	public List<AlarmEvent> findTotleAlarmDetail(InstanceAlarmEventReportQuery query);
}
