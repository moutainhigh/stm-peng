package com.mainsteam.stm.profile.fault.service.impl;

import java.util.List;

import com.mainsteam.stm.profile.fault.dao.ProfileFaultInstanceDao;
import com.mainsteam.stm.profilelib.fault.ProfileFaultInstanceService;
import com.mainsteam.stm.profilelib.fault.obj.ProfileFaultInstance;

public class ProfileFaultInstanceServiceImpl implements
		ProfileFaultInstanceService {

	private ProfileFaultInstanceDao profileInstanceDao;
	
	@Override
	public int removeInstanceByProfile(long profileId) {
		
		return profileInstanceDao.deleteProfileFaultInstance(profileId);
	}

	@Override
	public int insertProfileFaultInstance(List<ProfileFaultInstance> profileFaultInstances) {
		
		return profileInstanceDao.batchInsertProfileFaultInstance(profileFaultInstances);
	}

	@Override
	public List<ProfileFaultInstance> queryProfileInstanceByProfileId(long profileId) {
		
		return profileInstanceDao.selectProfileFaultInstances(profileId);
	}

	public void setProfileInstanceDao(ProfileFaultInstanceDao profileInstanceDao) {
		this.profileInstanceDao = profileInstanceDao;
	}

	@Override
	public int updateProfilefaultInstances(long profileId,
			List<ProfileFaultInstance> profileFaultInstances) {
		this.removeInstanceByProfile(profileId);
		int result = 0;
		if(null!=profileFaultInstances){
			for (ProfileFaultInstance profileFaultInstance : profileFaultInstances) {
				profileFaultInstance.setProfileId(profileId);
			}
			result = this.insertProfileFaultInstance(profileFaultInstances);
		}
		return result;
	}
}
