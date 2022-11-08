package com.mainsteam.stm.profilelib.service.impl.querybean;

import java.util.List;

import com.mainsteam.stm.profilelib.obj.ProfileMetric;
import com.mainsteam.stm.profilelib.obj.ProfileThreshold;
import com.mainsteam.stm.profilelib.service.impl.TimelineServiceImpl;

public class QueryTimelineMetric extends ProfileMetric {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 35805795688525058L;
	private TimelineServiceImpl timelineService;
	
	public QueryTimelineMetric(ProfileMetric metric,TimelineServiceImpl timelineService) {
		super.setAlarm(metric.isAlarm());
		super.setAlarmFlapping(metric.getAlarmFlapping());
		super.setDictFrequencyId(metric.getDictFrequencyId());
		super.setMetric_mkId(metric.getMetric_mkId());
		super.setMetricId(metric.getMetricId());
		super.setMonitor(metric.isMonitor());
		super.setProfileId(metric.getProfileId());
		super.setTimeLineId(metric.getTimeLineId());
		this.timelineService = timelineService;
	}
	
	@Override
	public List<ProfileThreshold> getMetricThresholds() {
		List<ProfileThreshold> thresholds = null;
		thresholds = timelineService.getThresholdByTimelineIdAndMetricId(getTimeLineId(), getMetricId());
		return thresholds;
	}

}
