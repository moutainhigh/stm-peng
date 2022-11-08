package com.mainsteam.stm.profilelib.interceptor.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.profilelib.interceptor.ProfileMetricMonitorChange;
import com.mainsteam.stm.profilelib.obj.ProfileChangeData;

public class ProfileMetricMonitorManagerImpl extends
		DefaultProfileChangeManagerImpl {

	private static final Log logger = LogFactory.getLog(ProfileMetricMonitorManagerImpl.class);

	
	private List<ProfileMetricMonitorChange> monitorChanges;
	
	public ProfileMetricMonitorManagerImpl(){
		monitorChanges = new ArrayList<>(5);
	}
	
	@Override
	public void register(ProfileMetricMonitorChange profileCancelMonitorChange) {
		monitorChanges.add(profileCancelMonitorChange);
	}

	@Override
	public void doMetricChangeInterceptor(List<ProfileChangeData> datas) {
		for (ProfileMetricMonitorChange monitorChange : monitorChanges) {
			try {
				if(logger.isInfoEnabled()){
					logger.info(monitorChange.getClass().getName() + "-" + datas);
				}
				monitorChange.metricMonitorChange(datas);
			} catch (Exception e) {
				if(logger.isErrorEnabled()){
					logger.error("doInterceptor metricMonitorChange", e);
				}
			}
		}
	}
}
