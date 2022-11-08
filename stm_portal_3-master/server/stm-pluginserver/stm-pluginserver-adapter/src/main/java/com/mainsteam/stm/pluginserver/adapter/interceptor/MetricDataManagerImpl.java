package com.mainsteam.stm.pluginserver.adapter.interceptor;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.dataprocess.MetricCalculateData;

public class MetricDataManagerImpl implements MetricDataManager {
	
	private static final Log logger = LogFactory.getLog(MetricDataManagerImpl.class);
	
	private List<ReceiverMetricData> receiverMetricDataClass;
	
	@Override
	public void register(ReceiverMetricData receiverMetricData) {
		if(receiverMetricDataClass == null){
			receiverMetricDataClass = new ArrayList<ReceiverMetricData>(5);
		}
		receiverMetricDataClass.add(receiverMetricData);
	}

	@Override
	public void doInterceptor(MetricCalculateData data) throws Exception {
		if(receiverMetricDataClass != null){
			try {
				for (ReceiverMetricData tempClass : receiverMetricDataClass) {
					tempClass.transData(data);
				}
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("receiverMetricDataClass doInterceptor", e);
				}
			}
		}
	}
}
