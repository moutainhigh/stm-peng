package com.mainsteam.stm.pluginserver.adapter.interceptor;

import com.mainsteam.stm.dataprocess.MetricCalculateData;

/**
 * @author 采集器用于接收指标值
 */
public interface ReceiverMetricData {

	public void transData(MetricCalculateData data);
	
}
