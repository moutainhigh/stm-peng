package com.mainsteam.stm.profilelib.interceptor.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.profilelib.interceptor.ResourceMonitorChange;
import com.mainsteam.stm.profilelib.obj.ProfileChangeData;

public class ProfileResourceCancelMonitorManagerImpl extends
		DefaultProfileChangeManagerImpl {

	private static final Log logger = LogFactory.getLog(ProfileResourceCancelMonitorManagerImpl.class);

	
	private List<ResourceMonitorChange> monitorChanges;
	
	public ProfileResourceCancelMonitorManagerImpl(){
		monitorChanges = new ArrayList<>(50);
	}
	
	@Override
	public void register(ResourceMonitorChange resourceMonitorChange) {
		monitorChanges.add(resourceMonitorChange);
	}

	@Override
	public void doResourceMonitorChangeInterceptor(
			List<ProfileChangeData> datas) {
		for (ResourceMonitorChange monitorChange : monitorChanges) {
			try {
				if(logger.isInfoEnabled()){
					logger.info(monitorChange.getClass().getName() + "-" + datas);
				}
				monitorChange.monitorChange(datas);
			} catch (Exception e) {
				if(logger.isErrorEnabled()){
					logger.error("doInterceptor metricMonitorChange", e);
				}
			}
		}
	}
}
