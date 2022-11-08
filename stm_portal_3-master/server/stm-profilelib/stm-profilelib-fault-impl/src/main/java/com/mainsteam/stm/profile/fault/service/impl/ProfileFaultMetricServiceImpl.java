package com.mainsteam.stm.profile.fault.service.impl;

import java.util.List;

import com.mainsteam.stm.profile.fault.dao.ProfileFaultMetricDao;
import com.mainsteam.stm.profilelib.fault.ProfileFaultMetricService;
import com.mainsteam.stm.profilelib.fault.obj.ProfileFaultMetric;

public class ProfileFaultMetricServiceImpl implements
		ProfileFaultMetricService {

	private ProfileFaultMetricDao profileMetricDao;
	
	@Override
	public int removeMetricByProfile(long profileId) {
		
		return profileMetricDao.deleteProfileFaultMetric(profileId);
	}

	@Override
	public int insertProfileFaultMetric(List<ProfileFaultMetric> profileFaultMetrics) {
		
		return profileMetricDao.batchInsertProfileFaultMetric(profileFaultMetrics);
	}

	@Override
	public List<ProfileFaultMetric> queryProfileMetricByProfileId(long profileId) {
		
		return profileMetricDao.selectProfileFaultMetrics(profileId);
	}

	public void setProfileMetricDao(ProfileFaultMetricDao profileMetricDao) {
		this.profileMetricDao = profileMetricDao;
	}

	@Override
	public int updateProfileFaultMetrics(long profileId,
			List<ProfileFaultMetric> profileFaultMetrics) {
		this.removeMetricByProfile(profileId);
		int result = 0;
		if(null!=profileFaultMetrics){
			for (ProfileFaultMetric profileFaultMetric : profileFaultMetrics) {
				profileFaultMetric.setProfileId(profileId);
			}
			result = this.insertProfileFaultMetric(profileFaultMetrics);
		}
		return result;
	}
}
