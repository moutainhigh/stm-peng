package com.mainsteam.stm.profilelib.dao;

import java.util.List;

import com.mainsteam.stm.profilelib.po.ProfileInfoPO;

/**
 * 操作策略表
 * 
 * @author
 */
public interface ProfileDAO {

	/**
	 * 根据实例ID，策略key,查找具体策略值
	 * @param profileId 实例Id
	 * @return ProfileInfoPO 策略
	 */
	public ProfileInfoPO getProfilePoById(final long profileId) throws Exception;
	
	/**
	 * 根据实例ID，策略key,查找具体策略值
	 * @param profileIds 实例Ids
	 * @return ProfileInfoPO 策略
	 */
	public List<ProfileInfoPO> getProfilePoByIds(final List<Long> profileIds) throws Exception;
	
	/**
	 * 根据实例ID，策略key,查找具体策略值
	 * 
	 * parentProfileId 父策略Id
	 * @return List<ProfileInfoPO> 策 略
	 */
	public List<ProfileInfoPO> getProfileInfoPO(final ProfileInfoPO profileInfoPO) throws Exception;
	
	/**
	 * 根据模型ID，策略类型
	 * @param resourceId 资源Id
	 * @return List<ProfileInfoPO>
	 */
	public List<ProfileInfoPO> getProfileBasicInfoByResourceIds(final List<String> resourceIds,final String profileType) throws Exception;
	
	/**
	 * 根据模型ID，策略类型
	 * @param resourceId 资源Id
	 * @return List<ProfileInfoPO>
	 */
	public List<ProfileInfoPO> getProfileBasicInfoByResourceIds(final List<String> resourceIds) throws Exception;
	
	/**
	 * 获取所有策略
	 * @return List<ProfileInfoPO>
	 */
	public List<ProfileInfoPO> getAllProfilePos() throws Exception;
	
	/**
	 * 根据父资源实例，查找个性化信息,包含所有子个性化信息
	 * @param parentInstanceId 资源实例Id
	 * @return List<ProfileInfoPO>
	 */
	public List<ProfileInfoPO> getPersonalizeProfileBasicInfoByParentProfileId(final long parentProfileId) throws Exception;
	
	
	/**
	 * 批量插入策略
	 * 
	 * @param profiles
	 */
	public void insertProfiles(final List<ProfileInfoPO> profiles) throws Exception;

	/**
	 * 插入策略
	 * 
	 * @param profiles
	 */
	public void insertProfile(final ProfileInfoPO profile) throws Exception;

	/**
	 * 批量更新策略
	 * 
	 * @param profiles
	 */
	public void updateProfiles(final List<ProfileInfoPO> profiles) throws Exception;
	/**
	 * 批量更新策略状态无可用
	 * @param profiles
	 */
	public void updateProfileStateByResourceIds(List<String> resourceIds,String isUse) throws Exception;
	/**
	 * 更新策略
	 * 
	 * @param profiles
	 */
	public void updateProfile(final ProfileInfoPO profile) throws Exception;

	/**
	 * 删除某个策略
	 * 
	 * @param profileIds
	 *            策略id
	 * @return 删除的行数
	 */
	public int removeProfileByProfileIds(final List<Long> profileIds) throws Exception;
	
	/**
	 * 删除某个策略
	 * 
	 * @param profileIds
	 *            策略id
	 * @return 删除的行数
	 */
	public int removeProfileByProfileId(final long profileId) throws Exception;

}
