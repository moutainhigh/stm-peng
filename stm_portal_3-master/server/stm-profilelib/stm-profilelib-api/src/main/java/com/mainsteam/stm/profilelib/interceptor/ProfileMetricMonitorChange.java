package com.mainsteam.stm.profilelib.interceptor;

import java.util.List;

import com.mainsteam.stm.profilelib.obj.ProfileChangeData;

/**
 * 取消指标监控通知
 * 
 */
public interface ProfileMetricMonitorChange {

	public void metricMonitorChange(List<ProfileChangeData> ProfileMetricChanges) throws Exception;
}
