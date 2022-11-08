package com.mainsteam.stm.profilelib.interceptor.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.profilelib.interceptor.ProfileMetricAlarmChange;
import com.mainsteam.stm.profilelib.obj.ProfileChangeData;

public class ProfileMetricAlarmManagerImpl extends DefaultProfileChangeManagerImpl{

	private static final Log logger = LogFactory.getLog(ProfileMetricAlarmManagerImpl.class);
	
	private List<ProfileMetricAlarmChange> alarmChanges;

	public ProfileMetricAlarmManagerImpl(){
		alarmChanges = new ArrayList<ProfileMetricAlarmChange>(5);
	}

	@Override
	public void register(ProfileMetricAlarmChange profileMetricAlarmChange) {
		alarmChanges.add(profileMetricAlarmChange);
	}

	@Override
	public void doMetricChangeInterceptor(List<ProfileChangeData> datas) {
		for (ProfileMetricAlarmChange alarmChange : alarmChanges) {
			try {
				if(logger.isInfoEnabled()){
					logger.info(alarmChange.getClass().getName() + "-" + datas);
				}
				alarmChange.metricAlarmChange(datas);
			} catch (Exception e) {
				if(logger.isErrorEnabled()){
					logger.error("doInterceptor metricAlarmChange", e);
				}
			}
		}
	}
	
}
