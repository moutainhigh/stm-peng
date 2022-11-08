package com.mainsteam.stm.instancelib.dao;

import java.util.List;

import com.mainsteam.stm.instancelib.dao.pojo.RelationPO;

/**
 * 操作关系表
 * @author xiaoruqiang
 */
public interface RelationDAO {

	/**
	 * 根据实例ID，查询改实例下的所有关系信息列表
	 * 
	 * @param instanceId 实例ID
	 * @return List<RelationPO> 关系信息列表
	 */
	public List<RelationPO> getRelationPOsByInstanceId(long instanceId) throws Exception;
	
	
	/**
	 * 根据实例类型查询改实例下的所有关系信息列表
	 * 
	 * @param type 实例ID
	 * @return List<RelationPO> 关系信息列表
	 */
	public List<RelationPO> getRelationPOsByInstanceType(String type) throws Exception;
	/**
	 * 批量插入关系信息
	 * 
	 * @param POs 
	 */
	public void insertRelationPOs(List<RelationPO> POs) throws Exception;

	/**
	 * 插入关系信息
	 * 
	 * @param POs
	 */
	public void insertRelationPO(RelationPO PO) throws Exception;
	
	/**
	 * 批量更新关系信息
	 * 
	 * @param POs
	 */
	public void updateRelationPOs(List<RelationPO> POs) throws Exception;

	
	/**
	 * 批量更新关系信息
	 * 
	 * @param POs
	 */
	public void updateRelationPO(RelationPO PO) throws Exception;
	
	/**
	 * 删除实例的某个关系信息
	 * 
	 * @instanceId 实例ID
	 * @return 删除的行数
	 */
	public int removeRelationPOByInstanceId(long instanceId) throws Exception;
	
	/**
	 * 删除实例的某个关系信息
	 * 
	 * @instanceId 实例ID
	 * @return 删除的行数
	 */
	public void removeRelationPOByInstanceIdAndType(long instanceId,String type) throws Exception;
}
