package com.mainsteam.stm.instancelib.dao;

import java.util.List;

import com.mainsteam.stm.instancelib.dao.pojo.PropDO;

/**
 * 操作属性表
 * @author xiaoruqiang
 */
public interface PropDAO {

	/**
	 * 根据实例ID，查询改实例下的所有属性信息列表
	 * 
	 * @param instanceId
	 *            实例ID
	 * @param type 参数属性
	 * @return List<PropDO> 属性信息列表
	 */
	public List<PropDO> getPropDOsByInstance(long instanceId,String type) throws Exception;
	
	/**
	 * 根据实例ID，属性key,查找具体属性值
	 * @instanceId 实例Id
	 * @param key
	 *            属性key
	 * @return PropDO 属性信息
	 */
	public List<PropDO> getPropDOByInstanceAndKey(long instanceId,String key,String type) throws Exception;
	
	/**
	 * 获取所有的模型和发现属性
	 * @return PropDO 属性信息
	 */
	public List<PropDO> getAllModuleAndDiscoverProp(List<String> moduleKeys,List<String> discoverKeys) throws Exception;

	/**
	 * 根据实例ID，属性key,查找具体属性值
	 * @instanceId 实例Id
	 * @param key
	 *            属性key
	 * @return PropDO 属性信息
	 */
	public List<PropDO> getPropDOByInstanceAndKeys(long instanceId,List<String> key,String type) throws Exception;
	/**
	 * 批量插入属性信息
	 * 
	 * @param DOs
	 */
	public void insertPropDOs(List<PropDO> DOs) throws Exception;

	/**
	 * 插入属性信息
	 * 
	 * @param DOs
	 */
	public void insertPropDO(PropDO DO) throws Exception;
	
	/**
	 * 批量更新属性信息
	 * 
	 * @param DOs
	 */
	public void updatePropDOs(List<PropDO> DOs) throws Exception;

	
	/**
	 * 批量更新属性信息
	 * 
	 * @param DOs
	 */
	public void updatePropDO(PropDO DO) throws Exception;
	
	/**
	 * 删除实例的某个属性信息
	 * 
	 * @instanceId 实例ID
	 * @param key 属性key
	 * @param type 参数类型
	 * @return 删除的行数
	 */
	public int removePropDOByInstanceAndKey(long instanceId,String key,String type)throws Exception;
	
	/**
	 * 删除实例所有属性信息
	 * 
	 * @instanceId 实例ID
	 * @type 参数类型
	 * @return 删除的行数
	 */
	public int removePropDOByInstanceAndType(long instanceId,String type)throws Exception;
	
	/**
	 * 删除实例所有属性信息
	 * 
	 * @instanceId 实例ID
	 * @type 参数类型
	 * @return 删除的行数
	 */
	public int removePropDOByInstances(List<Long> instanceIds) throws Exception;
	
}

