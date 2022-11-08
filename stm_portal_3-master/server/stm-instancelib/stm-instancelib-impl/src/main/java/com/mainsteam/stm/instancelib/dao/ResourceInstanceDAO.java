/**
 * 
 */
package com.mainsteam.stm.instancelib.dao;

import java.util.List;

import com.mainsteam.stm.instancelib.dao.pojo.ResourceInstanceDO;

/**
 * 资源实例操作
 * @author xiaoruqiang
 */
public interface ResourceInstanceDAO {
	/**
	 * 根据ID，查询实例信息
	 * 
	 * @param instanceId 实例ID
	 * @return ResourceInstance 信息
	 */
	public ResourceInstanceDO getResourceInstanceById(long instanceId);
	
	

	/**
	 * 批量插入实例信息
	 * 
	 * @param instances 实例信息
	 */
	public void insertResourceInstances(List<ResourceInstanceDO> instances) throws Exception;

	/**
	 * 插入实例信息
	 * 
	 * @param instance 实例信息
	 */
	public void insertResourceInstance(ResourceInstanceDO instance) throws Exception;
	
	/**
	 * 批量更新实例信息
	 * 
	 * @param instances 实例信息
	 */
	public void updateResourceInstances(List<ResourceInstanceDO> instances) throws Exception;

	
	/**
	 * 更新实例信息
	 * @param instance 实例信息
	 */
	public void updateResourceInstance(ResourceInstanceDO instance) throws Exception;
	
	/**
	 * 删除的某个实例信息
	 * 
	 * @instanceId  实例ID
	 * @return 删除的行数
	 */
	public int removeResourceInstanceById(long instanceId) throws Exception;
	/**
	 * 删除实例信息
	 * @instanceId  实例ID
	 * @return 删除的行数
	 */
	public void removeResourceInstanceByIds(List<Long> instanceIds) throws Exception;
	
	/**
	* @Title: removeResourceInstance
	* @Description: 删除资源实例，修改资源实例的状态为delete
	* @param instancesIds  void
	* @throws
	*/
	public void removeResourceInstance(List<Long> instancesIds);
	
	/**
	 * 根据实例对象拼装sql（and方式）
	 * @param instance 实例对象
	 * @return
	 */
	public List<ResourceInstanceDO> getInstancesByResourceDO(ResourceInstanceDO instance) throws Exception;
	
	/**
	 * 获取所有的父实例
	 * @return
	 */
	public List<ResourceInstanceDO> getAllParentInstance() throws Exception;
	
	/**
	 * 获取某个采集器所有的实例
	 * @return
	 */
	public List<ResourceInstanceDO> getAllInstanceByNode(String discoverNode) throws Exception;
	
	/**
	 * 获取所有的父实例
	 * @return
	 */
	public List<ResourceInstanceDO> getAllParentInstanceByNode(String discoverNode);
	
	/**
	 * 获取所有的父实例
	 * @return
	 */
	public List<ResourceInstanceDO> getAllParentInstanceByDomain(long domainId);
	
	/**
	 * 获取所有的实例(不包含已经删除的实例)
	 * @return
	 */
	public List<ResourceInstanceDO> getAllInstance();
	/**
	 * 通过模型获取是否有设备
	 * @return
	 */
	public List<ResourceInstanceDO> getExistParentInstanceByResourceId(List<String> resourceIds);
	
	/**
	 * 通过模型获取是否有子设备
	 * @return
	 */
	public List<ResourceInstanceDO> getExistChildInstanceByResourceId(List<String> resourceIds);
	
	/**
	 * 通过父资源查询所有的子资源
	 * @param parentIds
	 * @return
	 */
	public List<ResourceInstanceDO> getAllChildrenInstanceIdbyParentIds(List<Long> parentIds);
	
	/**
	 * 通过父资源ID查询所有已监控的子资源实例ID 
	 * @param parentIds
	 * @return
	 */
	public List<Long> getAllMonirotedChildrenInstanceIdByParentIds(List<Long> parentIds);
	
	/**
	 * 通过父资源ID查询所有未监控的子资源实例ID 
	 * @param parentIds
	 * @return
	 */
	public List<Long> getAllNotMonirotedChildrenInstanceIdByParentIds(List<Long> parentIds);
	
	/**
	* @Title: getAllResourceInstanceForLink
	* @Description: 查询所有链路
	* @return  List<ResourceInstanceDO>
	* @throws
	*/
	public List<ResourceInstanceDO> getAllResourceInstanceForLink();
}

