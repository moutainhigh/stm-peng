package com.mainsteam.stm.metric.interceptor;

import java.util.List;

import com.mainsteam.stm.metric.obj.CustomChangeData;

/**
 * 自定义指标取消指标监控通知
 * @author xiaoruqiang
 */
public interface CustomMetricMonitorChange {

	public void metricMonitorChange(List<CustomChangeData> ProfileMetricChanges) throws Exception;
}
