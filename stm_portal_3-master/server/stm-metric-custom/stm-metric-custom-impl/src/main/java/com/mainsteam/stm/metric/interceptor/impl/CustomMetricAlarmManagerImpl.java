package com.mainsteam.stm.metric.interceptor.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.metric.interceptor.CustomMetricAlarmChange;
import com.mainsteam.stm.metric.obj.CustomChangeData;

public class CustomMetricAlarmManagerImpl extends DefaultCustomChangeManagerImpl{

	private static final Log logger = LogFactory.getLog(CustomMetricAlarmManagerImpl.class);
	
	private List<CustomMetricAlarmChange> alarmChanges;

	public CustomMetricAlarmManagerImpl(){
		alarmChanges = new ArrayList<CustomMetricAlarmChange>(5);
	}

	@Override
	public void register(CustomMetricAlarmChange customMetricAlarmChange) {
		alarmChanges.add(customMetricAlarmChange);
	}

	@Override
	public void doMetricChangeInterceptor(List<CustomChangeData> datas) {
		for (CustomMetricAlarmChange alarmChange : alarmChanges) {
			try {
				if(logger.isInfoEnabled()){
					logger.info("CustomMetric AlarmChange:" + datas);
				}
				alarmChange.metricAlarmChange(datas);
			} catch (Exception e) {
				if(logger.isErrorEnabled()){
					logger.error("doInterceptor customMetricAlarmChange", e);
				}
			}
		}
	}
	
}
