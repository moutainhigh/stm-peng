package com.mainsteam.stm.instancelib.service;

import java.util.HashSet;
import java.util.List;

import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;

public interface ResourceInstanceExtendService {

	/**
	 * 加载所有的资源实例
	 * @param nodeGroupId 节点nodegroupId
	 * @return
	 * @throws Exception
	 */
	public List<ResourceInstance> loadAllResourceInstance(long nodeGroupId) throws Exception;
	
	/**
	 * 通过资源实例Id获取实例，已删除的也可以查询
	 * @param instanceId
	 * @return
	 * @throws InstancelibException
	 */
	public ResourceInstance getResourceInstanceById(long instanceId) throws InstancelibException;

	/**
	 * 通过模型Id,查找是否有资源实例存在
	 * @param resourceIds 模型Id
	 * @return
	 */
	public List<ResourceInstance> getExistParentInstanceByResourceId(HashSet<String> resourceIds) throws InstancelibException;
	
//	/**
//	 * 物理删除资源实例
//	 * @param deleteIds 资源实例Id
//	 */
//	public void deleteResourceInstanceByIds(List<Long> deleteIds) throws InstancelibException;
//	
	
	/**
	 * 根据模型文件物理删除资源实例
	 * @param resourceIds 模型Id
	 */
	public List<Long> deleteResourceInstanceByResourceIds(HashSet<String> resourceIds) throws InstancelibException;
}
