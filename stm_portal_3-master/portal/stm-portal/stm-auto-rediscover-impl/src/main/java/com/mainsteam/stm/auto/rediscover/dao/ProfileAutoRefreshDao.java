package com.mainsteam.stm.auto.rediscover.dao;

import java.util.List;

import com.mainsteam.stm.profilelib.obj.ProfileAutoRefresh;

public interface ProfileAutoRefreshDao {

	void addAutoRefreshProfile(ProfileAutoRefresh autoRefresh);

	void addAutoRefreshProfile(List<ProfileAutoRefresh> autoRefresh);

	void removeAutoRefreshProfile(long instanceId);

	void removeAutoRefreshProfile(List<Long> instanceIds);

	void updateAutoRefreshProfile(ProfileAutoRefresh autoRefresh);

	void updateAutoRefreshProfile(List<ProfileAutoRefresh> autoRefreshs);

	List<ProfileAutoRefresh> getAllAutoRefreshProfile();
	
	ProfileAutoRefresh getAutoRefreshProfileByInstance(long instanceId);
	
	List<ProfileAutoRefresh> getAutoRefreshProfileById(long id);
}
