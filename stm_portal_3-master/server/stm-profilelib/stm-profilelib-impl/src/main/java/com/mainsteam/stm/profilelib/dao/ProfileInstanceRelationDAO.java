package com.mainsteam.stm.profilelib.dao;

import java.util.List;

import com.mainsteam.stm.profilelib.po.ProfileInstRelationPO;

/**
 * 策略关联资源DAO <br>
 * <p>
 * Create on : 2014-6-18<br>
 * <p>
 * </p>
 * <br>
 * 
 * @author <br>
 * @version oc-profilelib-impl v4.1
 *          <p>
 *          <br>
 *          <strong>Modify History:</strong><br>
 *          user modify_date modify_content<br>
 *          -------------------------------------------<br>
 *          <br>
 */
public interface ProfileInstanceRelationDAO {

	/**
	 * 插入绑定关系
	 * @param profileInstRelPojo
	 * @throws Exception
	 */
	public void insertInstRel(final ProfileInstRelationPO profileInstRelPojo)
			throws Exception;
	
	/**
	 * 插入绑定关系
	 * @param profileInstRelPojos
	 * @throws Exception
	 */
	public void insertInstRels(final List<ProfileInstRelationPO> profileInstRelPojos)
			throws Exception;

	/**
	 * 根据策略ID批量删除其所关联的所有资源.
	 * 
	 * @param profileIds
	 *            策略ID集合
	 * @throws Exception
	 *             DAO层异常
	 */
	public void removeInstRelByProfileIds(final List<Long> profileIds)
			throws Exception;

	/**
	 * 根据策略ID删除其所关联的所有资源.
	 * 
	 * @param profileId
	 *            策略ID
	 * @throws Exception
	 *             DAO层异常
	 */
	public void removeInstRelByProfileId(final long profileId)
			throws Exception;

	/**
	 * 根据关联的资源实例ID删除其所关联的资源.
	 * 
	 * @param profileId
	 *            策略ID
	 * @param resInstanceIds
	 *            关联资源实例ID
	 * @throws Exception
	 *             DAO层异常
	 */
	public void removeInstRelByInstIds(final List<Long> instanceIds)
			throws Exception;

	/**
	 * 根据关联的父资源实例ID删除其所关联的资源.
	 * @param parentInstanceIds
	 *            关联父资源实例ID
	 * @throws Exception
	 *             DAO层异常
	 */
	public void removeInstRelByparentInstIds(final List<Long> parentInstanceIds)
			throws Exception;
	
	/**
	 * 根据策略ID和关联的资源实例ID删除其所关联的资源.
	 * 
	 * @param profileId
	 *            策略ID
	 * @param parentInstanceIds
	 *            关联资源实例ID
	 * @throws Exception
	 *             DAO层异常
	 */
	public void removeInstRelByparentInstId(final long parentInstanceId)
			throws Exception;
	/**
	 * 根据策略ID获取策略关联资源.
	 * 
	 * @param profileId
	 *            策略ID
	 * @return 策略所关联的资源
	 * @throws Exception
	 *             DAO层异常
	 */
	public List<ProfileInstRelationPO> getInstRelationsByProfileId(final long profileId)
			throws Exception;

	/**
	 * 根据策略ID获取策略关联资源.
	 * 
	 * @param profileId
	 *            策略ID
	 * @return 策略所关联的资源
	 * @throws Exception
	 *             DAO层异常
	 */
	public List<ProfileInstRelationPO> getInstRelationsByProfileId(final long profileId,final int nodeGroupId)
			throws Exception;
	
	/**
	 * 根据策略id获取所对应的实例ID
	 * 
	 * @param profileId
	 * @return
	 * @throws Exception
	 */

	public List<ProfileInstRelationPO> getInstRelationsByProfileIds(final List<Long> profileIds)
			throws Exception;
	
	/**
	 * 根据策略id获取所对应的实例ID
	 * 
	 * @param profileId
	 * @return
	 * @throws Exception
	 */

	public List<ProfileInstRelationPO> getInstRelationsByProfileIds(final List<Long> profileIds,final int nodeGroupId)
			throws Exception;
	
	/**
	 * 根据instId获取所对应的实例ID
	 * 
	 * @param profileId
	 * @return
	 * @throws Exception
	 */
	public List<ProfileInstRelationPO> getInstRelationByInstIds(List<Long> instances)
			throws Exception;
	
	/**
	 * 根据instId获取所对应的实例ID
	 * 
	 * @param profileId
	 * @return
	 * @throws Exception
	 */

	public ProfileInstRelationPO getInstRelationByInstId(long instanceId)
			throws Exception;
	/**
	 * 根据parentInstId获取所对应的实例ID
	 * @param parentInstId
	 * @return
	 */
	public List<ProfileInstRelationPO> getInstRelationByParentInstId(long parentInstId)
		throws Exception;
	
	/**
	 * 根据parentInstId获取所对应的实例ID
	 * @param parentInstIds 
	 * @return
	 */
	public List<ProfileInstRelationPO> getInstRelationByParentInstIds(List<Long> parentInstIds)
		throws Exception;
	
	/**
	 * 获取所有的绑定关系
	 * @param parentInstIds
	 * @return
	 * @throws Exception
	 */
	public List<ProfileInstRelationPO> getAllInstRelation() throws Exception;
}
