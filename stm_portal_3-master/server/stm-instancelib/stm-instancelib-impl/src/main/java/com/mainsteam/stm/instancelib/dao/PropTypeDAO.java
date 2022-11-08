package com.mainsteam.stm.instancelib.dao;

import java.util.List;

import com.mainsteam.stm.instancelib.dao.pojo.PropDO;

/**
 * 操作属性类型类型表
 * @author xiaoruqiang
 */
public interface PropTypeDAO {

	/**
	 * 根据实例ID，查询改实例下的所有属性类型信息列表
	 * 
	 * @param instanceId  实例ID
	 * @param type 参数属性类型
	 * @return List<PropDO> 属性类型信息列表
	 */
	public List<PropDO> getPropTypeDOsByInstance(long instanceId,String type);
	


	/**
	 * 批量插入属性类型信息
	 * 
	 * @param DOs
	 */
	public void insertPropTypeDOs(List<PropDO> DOs);

	/**
	 * 插入属性类型信息
	 * 
	 * @param DOs
	 */
	public void insertPropTypeDO(PropDO DO);
	
	/**
	 * 批量更新属性类型信息
	 * 
	 * @param DOs
	 */
	public void updatePropTypeDOs(List<PropDO> DOs);

	
	/**
	 * 批量更新属性类型信息
	 * 
	 * @param DOs
	 */
	public void updatePropTypeDO(PropDO DO);
	
	/**
	 * 删除实例的某个属性类型信息
	 * 
	 * @instanceId 实例ID
	 * @param key 属性类型key
	 * @param type 参数类型
	 * @return 删除的行数
	 */
	public int removePropTypeDOByInstanceAndKey(long instanceId,String key,String type);
	
	/**
	 * 删除实例所有属性类型信息
	 * 
	 * @instanceId 实例ID
	 * @type 参数类型
	 * @return 删除的行数
	 */
	public int removePropTypeDOByInstanceAndType(long instanceId,String type);
	
	/**
	 * @param instanceId
	 * @return
	 */
	public int removePropTypeDOByInstances(List<Long> instanceIds);
	
}

