package com.mainsteam.stm.profilelib;

import java.util.List;

import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.ProfilelibAutoRediscover;
import com.mainsteam.stm.profilelib.obj.ProfilelibAutoRediscoverInstance;

/**
 * 
 * 自动重新发现资源业务处理
 * @author Administrator
 *
 */
public interface ProfileAutoRediscoverService {

	/**
	 * 添加自动重新发现资源策略基本信息
	 * @param autoRefresh
	 */
	void addAutoRediscoverProfileBaseInfo(ProfilelibAutoRediscover autoRediscover);
	
	/**
	 * 添加自动重新发现资源策略与资源关系
	 * @param autoRefreshs
	 */
	void addAutoRediscoverProfileInstance(List<ProfilelibAutoRediscoverInstance> autoRediscoverInstances);
	
	/**
	 * 删除自动刷新资源策略
	 * @param instanceId
	 */
	void deleteAutoRediscoverProfile(long profileId);
	
	/**
	 * 批量删除自动刷新资源策略
	 * @param instanceId
	 */
	void deleteAutoRediscoverProfile(List<Long> profileIds);
	
	
	/**
	 * 通过InstanceId删除策略与资源的关系 
	 * @param instanceId
	 */
	void deleteAutoRediscoverProfileInstanceByInstanceId(long instanceId);
	
	/**
	 * 通过InstanceId删除策略与资源的关系 
	 * @param instanceId
	 */
	void deleteAutoRediscoverProfileInstanceByInstanceId(List<Long> instanceIds);
	
	/**
	 * 通过策略Id删除策略与资源的关系 
	 * @param instanceId
	 */
	void deleteAutoRediscoverProfileInstanceByProfileId(long profileId);
	
	/**
	 * 通过策略Id删除策略与资源的关系 
	 * @param instanceId
	 */
	void deleteAutoRediscoverProfileInstanceByProfileId(List<Long> profileIds);
	
	/**
	 * 更新自动重新发现资源策略
	 * @param autoRefresh
	 */
	void updateAutoRediscoverProfileBaseInfo(ProfilelibAutoRediscover autoRediscoverProfile);
	
	/**
	 * 自动更新策略启用状态，启用<->禁用
	 */
	void updateAutoRediscoverProfileUsedState(long userId,long profileId);
	
	/**
	 * 查询所有自动刷新资源策略信息
	 * @return
	 */
	List<ProfilelibAutoRediscover> queryAllAutoRediscoverProfile();
	
	/**
	 * 查询所有启用状态的自动刷新资源策略信息
	 * @return
	 */
	List<ProfilelibAutoRediscover> queryAllAutoRediscoverProfileByUsed();
	
	/**
	 * 通过策略ID查询策略与资源的关系
	 * @param profileId
	 * @return
	 */
	List<ProfilelibAutoRediscoverInstance> queryAutoRediscoverProfileInstance(long profileId);
	
	
	/**
	 * 通过ID获取自动重新发现策略
	 * @param profileId
	 * @return
	 */
	ProfilelibAutoRediscover getAutoRediscoverProfile(long profileId);
	
	/**
	 * 更新策略执行时间
	 * @param profilelibAutoRediscoverInstance
	 */
	void updateProfileInstanceExecuteTime(ProfilelibAutoRediscoverInstance profilelibAutoRediscoverInstance);
	
	/**
	 * 批量更新策略执行时间
	 * @param profilelibAutoRediscoverInstance
	 */
	void updateProfileInstanceExecuteTime(List<ProfilelibAutoRediscoverInstance> profilelibAutoRediscoverInstances);
	
	/**
	 * 获取已有策略的资源实例总数
	 * @return
	 */
	int countProfileInstanceSize();
	
	/**
	 * 通过资源实例ID查询策略
	 * @param instanceId
	 * @return
	 */
	ProfilelibAutoRediscover getProfilelibAutoRediscoverByInstance(long instanceId);
	
	/**
	 * 通过InstanceId获取策略与资源的关系
	 * @param instanceId
	 * @return
	 */
	ProfilelibAutoRediscoverInstance getProfilelibAutoRediscoverInstanceByInstanceId(long instanceId);
	
	/**
	 * 更新自动重新发现策略与资源绑定关系
	 * 
	 * @param profileId
	 * @param instanceRel
	 */
	void updateProfileAutoRediscoverInstanceRelByProfile(long profileId,List<ProfilelibAutoRediscoverInstance> instanceRel);
	
	boolean checkProfileNameIsExist(String name) throws ProfilelibException;
}
