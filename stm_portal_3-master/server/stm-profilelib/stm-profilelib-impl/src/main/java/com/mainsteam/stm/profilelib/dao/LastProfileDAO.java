package com.mainsteam.stm.profilelib.dao;

import java.util.List;

import com.mainsteam.stm.profilelib.po.ProfileInstRelationPO;;

public interface LastProfileDAO {

	/**
	 * 添加信息
	 * @param lastProfilePOs
	 * @throws Exception
	 */
	public void insertLastProfile(ProfileInstRelationPO lastProfilePO) throws Exception;
	/**
	 * 添加信息
	 * @param lastProfilePOs
	 * @throws Exception
	 */
	public void insertLastProfiles(List<ProfileInstRelationPO> lastProfilePOs) throws Exception;
	
	/**
	 * 更新信息
	 * @throws Exception
	 */
	public void updateLastProfiles(List<ProfileInstRelationPO> lastProfilePOs) throws Exception;
	
	/**
	 * 更新信息
	 * @throws Exception
	 */
	public void updateLastProfile(ProfileInstRelationPO lastProfilePO) throws Exception;
	
	/**
	 * 删除信息
	 * @param instanceId
	 */
	public void removeLastProfileByInstanceId(long instanceId);
	/**
	 * 删除信息
	 * @param instanceId
	 */
	public void removeLastProfileByInstanceIds(List<Long> instanceIds);
	
	
	/**
	* @Title: removeLastProfilesByParentIds
	* @Description: 通过父资源实例ID删除
	* @param instanceIds  void
	* @throws
	*/
	public void removeLastProfilesByParentIds(List<Long> instanceIds);
	
	/**
	* @Title: removeLastProfilesByProfileIds
	* @Description: 通过策略ID删除
	* @param profileIds  void
	* @throws
	*/
	public void removeLastProfilesByProfileIds(List<Long> profileIds);
	
	/**
	 * 通过父资源实例Id 获取上一次策略Id
	 * @return
	 */
	public List<ProfileInstRelationPO> getLastProfileByParentInstanceId(long parentInstanceId) throws Exception;
	
	public List<ProfileInstRelationPO> getLastProfileByParentInstanceIds(List<Long> parentInstanceIds) throws Exception;
}
