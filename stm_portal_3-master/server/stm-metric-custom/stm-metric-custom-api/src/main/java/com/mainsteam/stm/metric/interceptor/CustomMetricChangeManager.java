package com.mainsteam.stm.metric.interceptor;

import java.util.List;

import com.mainsteam.stm.metric.obj.CustomChangeData;


/**
 * 事件管理
 * @author xiaoruqiang
 *
 */
public interface CustomMetricChangeManager {

	/**
	 * 告警改变注册
	 */
	void register(CustomMetricAlarmChange customMetricAlarmChange);
	
	/**
	 * 取消监控注册
	 */
	void register(CustomMetricMonitorChange customCancelMonitorChange);
	/**
	 * 取消资源注册
	 */
	void register(CustomResourceMonitorChange customResourceMonitorChange);
	
	
	/////////////////////////////////////////////////////
	// 分发
	/////////////////////////////////////////////////////
	
	/**
	 * 自定义指标告警改变分发
	 */
	void doMetricChangeInterceptor(List<CustomChangeData> customMetricChanges);
	
	/**
	 * 自定义指标取消监控分发
	 * @param profileMetricChanges
	 */
	void doResourceMonitorChangeInterceptor(List<CustomChangeData> customMetricChanges);
}
