package com.mainsteam.stm.profilelib;

import java.util.List;

import com.mainsteam.stm.profilelib.obj.ProfileAutoRefresh;

/**
 * 
 * 自动重新发现资源业务处理
 * @author Administrator
 *
 */
public interface ProfileAutoRefreshService {

	/**
	 * 添加自动刷新资源策略
	 * @param autoRefresh
	 */
	void addAutoRefreshProfile(ProfileAutoRefresh autoRefresh);
	
	/**
	 * 批量添加自动刷新资源策略
	 * @param autoRefreshs
	 */
	void addAutoRefreshProfile(List<ProfileAutoRefresh> autoRefreshs);
	
	
	/**
	 * 删除自动刷新资源策略
	 * @param instanceId
	 */
	void removeAutoRefreshProfile(long instanceId);
	
	/**
	 * 批量删除自动刷新资源策略
	 * @param instanceIds
	 */
	void removeAutoRefreshProfile(List<Long> instanceIds);
	
	/**
	 * 更新自动刷新资源策略
	 * @param autoRefresh
	 */
	void updateAutoRefreshProfile(ProfileAutoRefresh autoRefresh);
	
	/**
	 * 批量更新自动刷新资源策略
	 * @param autoRefreshs
	 */
	void updateAutoRefreshProfile(List<ProfileAutoRefresh> autoRefreshs);
	
	/**
	 * 后台调度执行更新
	 */
	void autoUpdateAutoRefreshProfile(ProfileAutoRefresh autoRefresh);
	/**
	 * 获取所有自动刷新资源策略
	 * @return
	 */
	List<ProfileAutoRefresh> getAllAutoRefreshProfile();
	
	
	/**
	 * 通过策略ID获取自动刷新资源策略
	 * @param id
	 * @return
	 */
	List<ProfileAutoRefresh> getAutoRefreshProfileByProfileId(long id);
	
	
	/**
	 * 通过InstanceId 获取自动刷新资源策略
	 * @param instanceId
	 * @return
	 */
	ProfileAutoRefresh getAutoRefreshProfileByInstance(long instanceId);
}
