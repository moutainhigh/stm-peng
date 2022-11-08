/**
 * 
 */
package com.mainsteam.stm.instancelib;

import java.util.List;

import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.Instance;

/**
 * 
 * 维护实例之间的关系
 * 
 * @author ziw
 *
 */
public interface InstanceRelationService {
	
	/**
	 * 通过复合实例Id获取当前包含的资源实例
	 * @param instanceId 复合实例Id
	 * @return
	 * @throws InstantiationException
	 */
	public List<Instance> getInstaceCollectPOsByInstanceId(long instanceId)  throws InstantiationException ;

	/**
	 * 添加复合实例包含的资源实例
	 * @param instanceId 复合实例Id
	 * @param instances 
	 * @throws InstancelibException
	 */
	public void insertInstanceCollectionPOs(long instanceId,List<Instance> instances)  throws InstancelibException;
	
	/**
	 * 添加复合实例包含的资源实例
	 * @param instanceId
	 * @param instance
	 * @throws InstancelibException
	 */
	public void insertInstanceCollectionPO(long instanceId,Instance instance)  throws InstancelibException;
	
	/**
	 * 更新复合实例包含的资源实例
	 * @param instanceId
	 * @param instances
	 * @throws InstancelibException
	 */
	public void updateInstanceCollectionPOs(long instanceId,List<Instance> instances)  throws InstancelibException;
	
	/**
	 * 更新复合实例包含的资源实例
	 * @param instanceId
	 * @param instance
	 * @throws InstancelibException
	 */
	public void updateInstanceCollectionPO(long instanceId,Instance instance)  throws InstancelibException;
	
	/**
	 * 删除复合实例包含的资源实例
	 * @param instanceId
	 * @throws InstancelibException
	 */
	public void removeInstanceCollectionPOByInstanceId(long instanceId)  throws InstancelibException;
}
