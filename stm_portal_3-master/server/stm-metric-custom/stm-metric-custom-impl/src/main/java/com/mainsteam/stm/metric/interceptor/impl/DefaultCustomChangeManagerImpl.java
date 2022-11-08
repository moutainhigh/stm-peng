package com.mainsteam.stm.metric.interceptor.impl;

import java.util.List;

import com.mainsteam.stm.metric.interceptor.CustomMetricAlarmChange;
import com.mainsteam.stm.metric.interceptor.CustomMetricChangeManager;
import com.mainsteam.stm.metric.interceptor.CustomMetricMonitorChange;
import com.mainsteam.stm.metric.interceptor.CustomResourceMonitorChange;
import com.mainsteam.stm.metric.obj.CustomChangeData;

/**
 * 默认空实现
 * @author xiaoruqiang
 *
 */
public class DefaultCustomChangeManagerImpl implements CustomMetricChangeManager{

	@Override
	public void register(CustomMetricAlarmChange customMetricAlarmChange) {
	}

	@Override
	public void register(CustomMetricMonitorChange customCancelMonitorChange) {
	}

	@Override
	public void register(CustomResourceMonitorChange customResourceMonitorChange) {
	}

	@Override
	public void doMetricChangeInterceptor(
			List<CustomChangeData> customMetricChanges) {
	}

	@Override
	public void doResourceMonitorChangeInterceptor(
			List<CustomChangeData> customMetricChanges) {
	}
	
}

