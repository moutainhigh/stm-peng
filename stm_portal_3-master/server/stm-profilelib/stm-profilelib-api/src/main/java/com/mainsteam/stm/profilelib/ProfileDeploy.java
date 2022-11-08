package com.mainsteam.stm.profilelib;

import java.util.List;

import com.mainsteam.stm.profilelib.obj.Profile;
import com.mainsteam.stm.profilelib.obj.ProfileInstanceRelation;
import com.mainsteam.stm.profilelib.obj.ProfileMetricMonitor;
import com.mainsteam.stm.profilelib.obj.TimelineProfileMetricMonitor;

public interface ProfileDeploy {
	
	public void addInstancesMonitor(List<ProfileInstanceRelation> instanceRelations);

	public void cancelInstancesMonitor(List<Long> instanceIdList);

	public void changeMetricsMonitor(List<ProfileMetricMonitor> metricMonitors);

	public void changeMetricsMonitor(ProfileMetricMonitor metricMonitor);

	public void deleteTimelines(List<Long> timelineIds);

	public void addTimeline(TimelineProfileMetricMonitor timelineProfileMetricMonitor);
	
	public void updateTimelineTime(TimelineProfileMetricMonitor timelineProfileMetricMonitor) ;
	
	public void deleteProfile(List<Long> profileIds);
	
	public void addProfile(List<Profile> profiles);
	
}
