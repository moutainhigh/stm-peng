package com.mainsteam.stm.metric.interceptor;

import java.util.List;

import com.mainsteam.stm.metric.obj.CustomChangeData;

/**
 * 自定义指标告警状态修改后通知
 * @author xiaoruqiang
 *
 */
public interface CustomMetricAlarmChange {

	
	public void metricAlarmChange(List<CustomChangeData> ProfileMetricChanges) throws Exception;
	
}
