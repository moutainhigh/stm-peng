package com.mainsteam.stm.auto.rediscover.dao;

import java.util.List;

import com.mainsteam.stm.profilelib.obj.ProfilelibAutoRediscover;
import com.mainsteam.stm.profilelib.obj.ProfilelibAutoRediscoverInstance;

public interface ProfileAutoRediscoverDao {

	void addProfileAutoRediscover(ProfilelibAutoRediscover autoRediscover);
	
	void addProfileAutoRediscoverInstance(List<ProfilelibAutoRediscoverInstance> profilelibAutoRediscoverInstances);
	
	void deleteProfileAutoRediscover(long profileId);
	
	void deleteProfileAutoRediscover(List<Long> profileIds);
	
	void deleteProfileAutoRediscoverInstanceByProfileId(long profileId);
	
	void deleteProfileAutoRediscoverInstanceByProfileId(List<Long> profileIds);
	
	void deleteProfileAutoRediscoverInstanceByInstanceId(long instanceId);
	
	void deleteProfileAutoRediscoverInstanceByInstanceId(List<Long> instanceId);
	
	ProfilelibAutoRediscover getProfileAutoRediscover(long profileId);
	
	List<ProfilelibAutoRediscover> getAllProfileAutoRediscover();
	
	List<ProfilelibAutoRediscover> getUsedProfileAutoRediscover();
	
	void updateProfileAutoRediscover(ProfilelibAutoRediscover profilelibAutoRediscover);
	
	List<ProfilelibAutoRediscoverInstance> getAutoRediscoverProfileInstanceByProfileId(long profileId);
	
	void updateProfileInstanceExecuteTime(ProfilelibAutoRediscoverInstance profilelibAutoRediscoverInstance);
	
	void updateProfileInstanceExecuteTime(List<ProfilelibAutoRediscoverInstance> profilelibAutoRediscoverInstances);
	
	int getProfileInstanceCount();
	
	ProfilelibAutoRediscoverInstance getProfilelibAutoRediscoverInstanceByInstanceId(long instanceId);
}
