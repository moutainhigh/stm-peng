package com.mainsteam.stm.profilelib.interceptor;

import java.util.List;

import com.mainsteam.stm.profilelib.obj.ProfileChangeData;
import com.mainsteam.stm.profilelib.obj.ProfileSwitchData;

/**
 * 事件管理
 * @author xiaoruqiang
 *
 */
public interface ProfileChangeManager {

	/**
	 * 告警改变注册
	 */
	void register(ProfileMetricAlarmChange profileMetricAlarmChange);
	
	/**
	 * 取消监控注册
	 */
	void register(ProfileMetricMonitorChange profileCancelMonitorChange);
	/**
	 * 取消资源注册
	 */
	void register(ResourceMonitorChange resourceMonitorChange);
	/**
	 * 策略切换注册
	 * @param profileSwitchChange
	 */
	void register(ProfileSwitchChange profileSwitchChange);
	
	/////////////////////////////////////////////////////
	// 分发
	/////////////////////////////////////////////////////
	
	/**
	 * 告警改变分发
	 */
	void doMetricChangeInterceptor(List<ProfileChangeData> profileMetricChanges);
	
	/**
	 * 取消监控
	 * @param profileMetricChanges
	 */
	void doResourceMonitorChangeInterceptor(List<ProfileChangeData> profileMetricChanges);
	
	/**
	 * 策略切换通知
	 */
	void doProfileSwitchInterceptor(List<ProfileSwitchData> profileSwitchDatas);
}
