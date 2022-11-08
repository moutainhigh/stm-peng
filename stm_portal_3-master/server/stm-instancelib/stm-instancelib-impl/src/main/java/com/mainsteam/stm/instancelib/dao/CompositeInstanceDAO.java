/**
 * 
 */
package com.mainsteam.stm.instancelib.dao;

import java.util.List;

import com.mainsteam.stm.instancelib.dao.pojo.CompositeInstanceDO;

/**
 * 资源实例操作
 * @author xiaoruqiang
 */
public interface CompositeInstanceDAO {
	/**
	 * 根据ID，查询实例信息
	 * 
	 * @param instanceId 实例ID
	 * @return CompositeInstance 信息
	 */
	public CompositeInstanceDO getCompositeInstanceById(long instanceId) throws Exception;
	
	

	/**
	 * 批量插入实例信息
	 * 
	 * @param instances 实例信息
	 */
	public void insertCompositeInstances(List<CompositeInstanceDO> instances)throws Exception;

	/**
	 * 插入实例信息
	 * 
	 * @param instance 实例信息
	 */
	public void insertCompositeInstance(CompositeInstanceDO instance)throws Exception;
	
	/**
	 * 批量更新实例信息
	 * 
	 * @param instances 实例信息
	 */
	public void updateCompositeInstances(List<CompositeInstanceDO> instances)throws Exception;

	
	/**
	 * 更新实例信息
	 * @param instance 实例信息
	 */
	public void updateCompositeInstance(CompositeInstanceDO instance)throws Exception;
	
	/**
	 * 删除的某个实例信息
	 * 
	 * @instanceId  实例ID
	 * @return 删除的行数
	 */
	public int removeCompositeInstanceById(long instanceId)throws Exception;
	
	/**
	 * 通过复合实例类型获取复合实例
	 * @return 复合实例
	 */
	public List<CompositeInstanceDO> getCompositeInstanceByInstanceType(String type)throws Exception;
	
	/**
	 * 通过复合实例名字模糊查询复合实例
	 * @return 复合实例
	 */
	public List<CompositeInstanceDO> getCompositeInstanceLikeName(String name)throws Exception;
	
	/**
	* @Title: getCompositeInstanceDOsByContainInstanceId
	* @Description: 通过关联的资源实例ID查询复合资源实例关系
	* @param containInstanceId
	* @return
	* @throws Exception  List<CompositeInstanceDO>
	* @throws
	*/
	public List<CompositeInstanceDO> getCompositeInstanceByContainInstanceId(long containInstanceId) throws Exception;
}
