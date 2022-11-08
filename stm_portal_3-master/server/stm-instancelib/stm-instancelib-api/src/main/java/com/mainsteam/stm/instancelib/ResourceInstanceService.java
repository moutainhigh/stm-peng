
package com.mainsteam.stm.instancelib;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mainsteam.stm.caplib.dict.DiscoverWayEnum;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.BatchResourceInstanceResult;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.obj.ResourceInstanceResult;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;

/**
 * 资源实例业务类
 * @author xiaoruqiang
 */
public interface ResourceInstanceService{

	/**
	 * 添加实例
	 * @param resourceInstance 实例值
	 * @return 实例主键值
	 */
	public ResourceInstanceResult addResourceInstance(ResourceInstance resourceInstance) throws InstancelibException;
	
	/**
	 * 批量添加实例
	 * @param resourceInstance 实例值
	 * @return 实例主键值
	 */
	public BatchResourceInstanceResult addResourceInstances(List<ResourceInstance> resourceInstances) throws InstancelibException;
	
	/**
	 * 添加链路资源实例
	 * @param resourceInstances 实例值
	 * @parmm isAll 是否所有的连线, true 所有连线 false 部分连线
	 * @return 实例主键值
	 * 
	 */
	public void addResourceInstanceForLink(List<ResourceInstance> resourceInstances,boolean isAll) throws InstancelibException;
	
	/**
	 * 刷新指定的实例
	 * 
	 * @param resourceInstance 刷新指定的实例。
	 */
	public Map<InstanceLifeStateEnum,List<Long>> refreshResourceInstance(ResourceInstance resourceInstance) throws InstancelibException;
	
	/**
	 * 刷新指定的实例
	 * 
	 * @param resourceInstance 刷新指定的实例。
	 * @param isAutoDeleteChildInstance 是否自动删除缺失的子资源
	 */
	public Map<InstanceLifeStateEnum,List<Long>> refreshResourceInstance(ResourceInstance resourceInstance,boolean isAutoDeleteChildInstance) throws InstancelibException;

	/**
	 * 
	 * 批量修改状实例生命周期值
	 *
	 * @param states key资源实例ID value生命周期值
	 * @throws InstantiationException 
	 */
	public void updateResourceInstanceState(Map<Long, InstanceLifeStateEnum> states) throws InstancelibException;

	/**
	 * 更新实例名称
	 * @param instanceId 实例ID
	 * @param name       实例名称
	 * @throws InstantiationException 
	 */
	public void updateResourceInstanceName(long instanceId, String name) throws InstancelibException;
	
	/**
	 * 更新实例发现IP
	 * @param instanceId 实例ID
	 * @param showIP 显示IP地址
	 * @throws InstantiationException 
	 */
	public void updateResourceInstanceShowIP(long instanceId,  String showIP) throws InstancelibException;
	
	/**
	 * 更新实例发现接节点
	 * @param instanceId    实例Id
	 * @param discoverNode  发现节点
	 * @throws InstantiationException 
	 */
	public void updateResourceInstanceDiscoverNode(long instanceId,  String discoverNode) throws InstancelibException;
	
	/**
	 * 
	 * 批量修改域
	 *
	 * @param domainIds key 资源实例ID value 域id
	 * @throws InstantiationException 
	 */
	public void updateResourceInstanceDomain(Map<Long, Long> domainIds) throws InstancelibException;
	/**
	 * 删除指定的实例
	 * 
	 * @param instanceId 实例Id
	 * @throws InstantiationException 
	 */
	public void removeResourceInstance(long instanceId) throws InstancelibException;
	
	/**
	* @Title: removeChildResourceInstance
	* @Description: 删除指定子资源实例
	* @param instanceId
	* @throws InstancelibException  void
	* @throws
	*/
	public void removeChildResourceInstance(long instanceId) throws InstancelibException;
	
	/**
	* @Title: removeChildResourceInstance
	* @Description: 删除指定子资源实例
	* @param instanceIds
	* @throws InstancelibException  void
	* @throws
	*/
	public void removeChildResourceInstance(List<Long> instanceIds) throws InstancelibException;
	/**
	 * 移除链路（真实删除）
	 * @param deleteIds
	 * @throws InstancelibException
	 */
	public void removeResourceInstanceByLinks(List<Long> deleteIds) throws InstancelibException;
	/**
	 * 删除指定的实例
	 * 
	 * @param instanceIds 实例Id
	 * @throws InstantiationException 
	 */
	public void removeResourceInstances(List<Long> instanceIds) throws InstancelibException;
	
	/**
	 * 查询实例的信息
	 * 
	 * @param instanceId 实例Id
	 * @return 实例的信息
	 */
	public ResourceInstance getResourceInstance(long instanceId) throws InstancelibException;
	
	/**
	 * 通过资源实例ID获取资源，包括删除状态的资源
	 * @param instanceId
	 * @return
	 * @throws InstancelibException
	 */
	public ResourceInstance getResourceInstanceWithDeleted(long instanceId) throws InstancelibException;
	
	/**
	 * 查询实例的信息
	 * 
	 * @param instanceIds 实例Id
	 * @return 实例的信息
	 */
	public List<ResourceInstance> getResourceInstances(List<Long> instanceIds) throws InstancelibException;
	
	/**
	 * 获取所有父实例:所有的资源---主机-网络等
	 * @return 父实例信息
	 */
	public List<ResourceInstance>  getAllParentInstance() throws InstancelibException;
	/**
	 * 通过资源分类获取所有父实例
	 * @param  categoryId  categoryID
	 * @return 父实例信息
	 */
	public List<ResourceInstance>  getParentInstanceByCategoryId(String categoryId) throws InstancelibException;
	
	/**
	 * 通过资源模型ID获取所有实例
	 * @param resourceId 资源Id
	 * @return 实例信息
	 */
	public List<ResourceInstance>  getResourceInstanceByResourceId(String resourceId) throws InstancelibException;
	
	
	/**
	 * 通过资源分类获取所有父实例
	 * @param  categoryId  categoryID
	 * @return 父实例信息
	 */
	public List<ResourceInstance>  getParentInstanceByCategoryIds(HashSet<String> categoryIds) throws InstancelibException;
	
	
	/**
	 * 通过资源分类直接从数据库获取所有资源父实例
	 * @param categoryIds
	 * @return
	 * @throws InstancelibException
	 */
	public List<ResourceInstance> getParentInstanceByCategoryIdsFordb(Set<String> categoryIds) throws InstancelibException;
	
	
	/**
	 * 通过资源模型ID获取所有实例
	 * @param resourceIds 资源Id
	 * @return 实例信息
	 */
	public List<ResourceInstance> getResourceInstanceByResourceId(HashSet<String> resourceIds) throws InstancelibException;
	
	/**
	 * 通过父实例ID获取所有的子实例
	 * @param parentId 父实例的Id
	 * @return 父实例信息
	 */
	public List<ResourceInstance>  getChildInstanceByParentId(long parentId) throws InstancelibException;
	
	/**
	 * 通过父实例ID获取所有的子实例
	 * @param parentId 父实例的Id
	 * @param isAll 是否查询所有资源包含已删除的
	 * @return 父实例信息
	 */
	public List<ResourceInstance>  getChildInstanceByParentId(long parentId,boolean isAll) throws InstancelibException;
	
	/**
	 * 获取所有的父实例的，通过生命周期
	 * @param lifeState 生命周期值
	 * @return 父实例
	 */
	public List<ResourceInstance> getParentResourceInstanceByLifeState(InstanceLifeStateEnum lifeState) throws InstancelibException;
	
	/**
	 * 通过发现节点组Id 查询资源所有的父实例
	 * @param groudNodeId  发现节点组Id
	 * @return 父实例
	 */
	public List<ResourceInstance> getParentResourceInstanceByNode(String groudNodeId) throws InstancelibException;
	
	/**
	 * 通过域查找父的资源实例对象
	 */
	public List<ResourceInstance> getParentResourceInstanceByDomainIds(Set<Long> domainIds) throws InstancelibException;
	
	/**
	 * 通过发现方式查找父的资源实例对象
	 */
	public List<ResourceInstance> getParentResourceInstanceByDiscoverWay(DiscoverWayEnum way) throws InstancelibException;
	
	/**
	 * 通过父资源id拿到所有子资源Id
	 * @param parentIds 父实例Id
	 * @return
	 */
	public List<Long> getAllChildrenInstanceIdbyParentId(Set<Long> parentIds);
	
	/**
	 * 通过父资源id拿到所有未监控或已监控的子资源Id
	 * @param parentIds 父实例Id
	 * @param state 资源状态（NOT_MONITORED/MONITORED）
	 * @return
	 */
	public List<Long> getAllChildrenInstanceIdbyParentId(Set<Long> parentIds,InstanceLifeStateEnum state);
	
	/**
	* @Title: getAllResourceInstancesForLink
	* @Description: 查询所有链路
	* @return  List<ResourceInstance>
	* @throws
	*/
	public List<ResourceInstance> getAllResourceInstancesForLink();
	
	/**
	 * 通过资源实例计算License
	 * @param resourceInstance
	 * @param isCalcLicense 是否允许占用License数量
	 * @throws InstancelibException
	 */
	public boolean checkLicense(ResourceInstance resourceInstance) throws InstancelibException;
}

