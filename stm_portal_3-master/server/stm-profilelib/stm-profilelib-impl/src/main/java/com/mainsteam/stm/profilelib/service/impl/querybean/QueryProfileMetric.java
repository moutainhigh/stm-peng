package com.mainsteam.stm.profilelib.service.impl.querybean;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.ProfileMetric;
import com.mainsteam.stm.profilelib.obj.ProfileThreshold;
import com.mainsteam.stm.profilelib.service.impl.ProfileServiceImpl;

public class QueryProfileMetric extends ProfileMetric {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7742420581385719826L;


	private static final Log logger = LogFactory.getLog(QueryProfileMetric.class);
	
	private ProfileServiceImpl profileService;
	
	public QueryProfileMetric(ProfileMetric metric,ProfileServiceImpl profileService) {
		super.setAlarm(metric.isAlarm());
		super.setAlarmFlapping(metric.getAlarmFlapping());
		super.setDictFrequencyId(metric.getDictFrequencyId());
		super.setMetric_mkId(metric.getMetric_mkId());
		super.setMetricId(metric.getMetricId());
		super.setMonitor(metric.isMonitor());
		super.setProfileId(metric.getProfileId());
		super.setTimeLineId(metric.getTimeLineId());
		this.profileService = profileService;
	}
	
	@Override
	public List<ProfileThreshold> getMetricThresholds() {
		List<ProfileThreshold> thresholds = null;
		try {
			thresholds = profileService.getThresholdByProfileIdAndMetricId(getProfileId(), getMetricId());
		} catch (ProfilelibException e) {
			if(logger.isErrorEnabled()){
				logger.error("getMetricThresholds", e);
			}
		}
		return thresholds;
	}

}
