package com.mainsteam.stm.profilelib.interceptor.impl;

import java.util.List;

import com.mainsteam.stm.profilelib.interceptor.ProfileMetricMonitorChange;
import com.mainsteam.stm.profilelib.interceptor.ProfileChangeManager;
import com.mainsteam.stm.profilelib.interceptor.ProfileMetricAlarmChange;
import com.mainsteam.stm.profilelib.interceptor.ProfileSwitchChange;
import com.mainsteam.stm.profilelib.interceptor.ResourceMonitorChange;
import com.mainsteam.stm.profilelib.obj.ProfileChangeData;
import com.mainsteam.stm.profilelib.obj.ProfileSwitchData;

public class DefaultProfileChangeManagerImpl implements ProfileChangeManager {

	@Override
	public void register(ProfileMetricAlarmChange profileMetricAlarmChange) {
	}

	@Override
	public void doMetricChangeInterceptor(List<ProfileChangeData> profileMetricChanges) {
	}

	@Override
	public void register(ProfileMetricMonitorChange profileCancelMonitorChange) {
	}

	@Override
	public void register(ResourceMonitorChange resourceMonitorChange) {
	}

	@Override
	public void doResourceMonitorChangeInterceptor(
			List<ProfileChangeData> profileMetricChanges) {
	}

	@Override
	public void doProfileSwitchInterceptor(List<ProfileSwitchData> profileSwitchDatas) {
	}

	@Override
	public void register(ProfileSwitchChange profileSwitchChange) {
	}
}
