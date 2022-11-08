package com.mainsteam.stm.metric.interceptor.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.metric.interceptor.CustomMetricMonitorChange;
import com.mainsteam.stm.metric.obj.CustomChangeData;

public class CustomMetricMonitorManagerImpl extends DefaultCustomChangeManagerImpl{

	private static final Log logger = LogFactory.getLog(CustomMetricMonitorManagerImpl.class);
	
	private List<CustomMetricMonitorChange> monitorChanges;

	public CustomMetricMonitorManagerImpl(){
		monitorChanges = new ArrayList<CustomMetricMonitorChange>(5);
	}

	@Override
	public void register(CustomMetricMonitorChange CustomMetricMonitorChange) {
		monitorChanges.add(CustomMetricMonitorChange);
	}

	@Override
	public void doMetricChangeInterceptor(List<CustomChangeData> datas) {
		for (CustomMetricMonitorChange monitorChange : monitorChanges) {
			try {
				if(logger.isInfoEnabled()){
					logger.info("CustomMetric MetricChange:" + datas);
				}
				monitorChange.metricMonitorChange(datas);
			} catch (Exception e) {
				if(logger.isErrorEnabled()){
					logger.error("doInterceptor CustomMetricMonitorChange", e);
				}
			}
		}
	}
	
}
