package com.mainsteam.stm.metric.interceptor.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.metric.interceptor.CustomResourceMonitorChange;
import com.mainsteam.stm.metric.obj.CustomChangeData;

public class CustomResourceCancelManagerImpl extends DefaultCustomChangeManagerImpl {

	private static final Log logger = LogFactory.getLog(CustomResourceCancelManagerImpl.class);
	
	private List<CustomResourceMonitorChange> resourceChanges;
	
	public CustomResourceCancelManagerImpl(){
		resourceChanges = new ArrayList<>(50);
	}
	
	@Override
	public void register(CustomResourceMonitorChange customResourceMonitorChange) {
		resourceChanges.add(customResourceMonitorChange);
	}

	@Override
	public void doResourceMonitorChangeInterceptor(
			List<CustomChangeData> customMetricChanges) {
		for (CustomResourceMonitorChange customResourceMonitorChange : resourceChanges) {
			try {
				if(logger.isInfoEnabled()){
					logger.info("CustomMetric bind MonitorChange:" + customMetricChanges);
				}
				customResourceMonitorChange.monitorChange(customMetricChanges);
			} catch (Exception e) {
				if(logger.isErrorEnabled()){
					logger.error("doInterceptor CustomResourceMonitorChange error", e);
				}
			}
		}
	}

	
}
