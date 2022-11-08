package com.mainsteam.stm.alarm.event.report;

import java.util.List;

import com.mainsteam.stm.alarm.event.dao.AlarmEventDAO;
import com.mainsteam.stm.alarm.obj.AlarmEvent;

public class InstanceAlarmEventReportServiceImpl implements InstanceAlarmEventReportService{
	private AlarmEventDAO alarmEventDAO;
	

	public void setAlarmEventDAO(AlarmEventDAO alarmEventDAO) {
		this.alarmEventDAO = alarmEventDAO;
	}
	
	@Override
	public List<InstanceAlarmEventReportData> findTotleAlarmReport(InstanceAlarmEventReportQuery query) {
		return alarmEventDAO.findTotleAlarmReport(query);
	}
	
	@Override
	public List<AlarmEvent> findTotleAlarmDetail(InstanceAlarmEventReportQuery query) {
		return alarmEventDAO.findTotleAlarmDetail(query);
	}

}
