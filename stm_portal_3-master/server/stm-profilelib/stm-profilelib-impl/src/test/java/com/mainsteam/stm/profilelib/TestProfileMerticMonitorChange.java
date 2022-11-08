//package com.mainsteam.stm.profilelib;
//
//import java.util.List;
//
//import com.mainsteam.stm.profilelib.interceptor.ProfileMetricMonitorChange;
//import com.mainsteam.stm.profilelib.obj.ProfileMetricChangeData;
//
//public class TestProfileMerticMonitorChange implements ProfileMetricMonitorChange {
//
//	@Override
//	public void metricMonitorChange(List<ProfileMetricChangeData> arg0)
//			throws Exception {
//		for (ProfileMetricChangeData profileMetricChangeData : arg0) {
//			System.out.println("monitor:");
//			System.out.println(profileMetricChangeData.getProfileId());
//			System.out.println(profileMetricChangeData.getMetricId());
//			System.out.println(profileMetricChangeData.getIsMonitor());
//		}
//	}
//}
