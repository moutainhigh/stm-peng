package com.mainsteam.stm.pluginserver.adapter.interceptor;

import com.mainsteam.stm.dataprocess.MetricCalculateData;


public interface MetricDataManager {
	/**
	 * 注册
	 */
	void register(ReceiverMetricData receiverMetricData);
	
	
	/**
	 * 执行拦截器
	 */
	void doInterceptor(MetricCalculateData data) throws Exception;
}
