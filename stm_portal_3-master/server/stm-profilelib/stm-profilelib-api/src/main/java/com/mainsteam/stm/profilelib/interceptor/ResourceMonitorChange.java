package com.mainsteam.stm.profilelib.interceptor;

import java.util.List;

import com.mainsteam.stm.profilelib.obj.ProfileChangeData;

/**
 * 策略指标告警状态修改后通知
 * @author xiaoruqiang
 *
 */
public interface ResourceMonitorChange {

	
	public void monitorChange(List<ProfileChangeData> ProfileMetricChanges) throws Exception;
	
}
