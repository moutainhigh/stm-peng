package com.mainsteam.stm.instancelib.dao;

import java.util.List;

import com.mainsteam.stm.instancelib.dao.pojo.InstanceCollectionPO;

public interface InstaceCollectionDAO {

	/**
	 * 根据实例ID，查询改实例下的所有复合实例集合信息列表
	 * 
	 * @param instanceId  实例ID
	 * @return List<InstanceCollectionPO> 复合实例集合信息列表
	 */
	public List<InstanceCollectionPO> getInstaceCollectPOsByInstanceId(long instanceId)  throws Exception ;
	
	/**
	 * 批量插入复合实例集合信息
	 * 
	 * @param POs
	 */
	public void insertInstanceCollectionPOs(List<InstanceCollectionPO> POs)  throws Exception ;

	/**
	 * 插入复合实例集合信息
	 * 
	 * @param POs
	 */
	public void insertInstanceCollectionPO(InstanceCollectionPO DO)  throws Exception ;
	
	/**
	 * 批量更新复合实例集合信息
	 * 
	 * @param POs
	 */
	public void updateInstanceCollectionPOs(List<InstanceCollectionPO> POs)  throws Exception ;

	
	/**
	 * 批量更新复合实例集合信息
	 * 
	 * @param POs
	 */
	public void updateInstanceCollectionPO(InstanceCollectionPO DO)  throws Exception ;
	
	/**
	 * 删除实例的某个复合实例集合信息
	 * 
	 * @instanceId 实例ID
	 * @return 删除的行数
	 */
	public int removeInstanceCollectionPOByInstanceId(long instanceId)  throws Exception ;
	
}
