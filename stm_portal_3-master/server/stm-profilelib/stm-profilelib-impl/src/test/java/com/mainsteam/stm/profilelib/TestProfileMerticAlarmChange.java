//package com.mainsteam.stm.profilelib;
//
//import java.util.List;
//
//import com.mainsteam.stm.profilelib.interceptor.ProfileMetricAlarmChange;
//import com.mainsteam.stm.profilelib.obj.ProfileMetricChangeData;
//
//public class TestProfileMerticAlarmChange implements ProfileMetricAlarmChange {
//
//	@Override
//	public void metricAlarmChange(
//			List<ProfileMetricChangeData> ProfileMetricChanges)
//			throws Exception {
//		for (ProfileMetricChangeData profileMetricChangeData : ProfileMetricChanges) {
//			System.out.println("alarm:");
//			System.out.println(profileMetricChangeData.getProfileId());
//			System.out.println(profileMetricChangeData.getMetricId());
//			System.out.println(profileMetricChangeData.getIsAlarm());
//		}
//	}
//
//}
