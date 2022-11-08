
package com.mainsteam.stm.instancelib;

import java.util.List;

import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.CompositeInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceTypeEnum;

/**
 * 复合资源实例业务类
 * @author xiaoruqiang
 */
public interface CompositeInstanceService{

	/**
	 * 
	 * 添加实例
	 * @param CompositeInstance 实例值
	 * @throws InstancelibException 数据不符合规则
	 * @throws Exception 
	 */
	public long addCompositeInstance(CompositeInstance CompositeInstance) throws InstancelibException;
	
	/**
	 * 更新指定的实例
	 *  
	 * @param CompositeInstance 更新的实例。
	 * @throws InstancelibException 数据不符合规则
	 * @throws Exception 
	 */
	public void updateCompositeInstance(CompositeInstance CompositeInstance)throws InstancelibException;


	/**
	 * 删除指定的实例
	 * 
	 * @param instanceId 实例Id
	 * @throws InstancelibException 如果该实例已经被资源实例使用，抛出异常
	 * @throws Exception 
	 */
	public void removeCompositeInstance(long instanceId)throws InstancelibException;
	
	/**
	 * 查询实例的信息（只型获取下一级复合实例信息）
	 * 例如：复合实例A 包含 复合实例B, 复合实例B 包含资源实例C
	 * 改方法传人实例A  可以获取到复合实例B的信息，复合实例B以下的信息将不获取。
	 * @param instanceId 实例Id
	 * @throws Exception 
	 * @return 实例的信息
	 */
	public CompositeInstance getCompositeInstance(long instanceId)throws InstancelibException ;
	
	/**
	 * 通过复合实例类型获取下一级复合实例信息
	 * 例如：复合实例A 包含 复合实例B, 复合实例B 包含资源实例C
	 * 改方法传人实例A  可以获取到复合实例B的信息，复合实例B以下的信息将不获取。
	 * @param type 复合实例类型
	 * @throws Exception 
	 * @return 复合实例
	 */
	public List<CompositeInstance> getCompositeInstanceByInstanceType(InstanceTypeEnum type)throws InstancelibException;
	
	/**
	 * 通过复合实例名字模糊查询获取下一级复合实例信息
	 * 例如：复合实例A 包含 复合实例B, 复合实例B 包含资源实例C
	 * 改方法传人实例A  可以获取到复合实例B的信息，复合实例B以下的信息将不获取。
	 * @param name 复合实例类型
	 * @throws Exception 
	 * @return 复合实例
	 */
	public List<CompositeInstance> getCompositeInstanceLikeName(String name)throws InstancelibException;
	
	
	/**
	* @Title: getCompositeInstancesByContainInstanceId
	* @Description: 通过复合实例关联InstanceId获取复合实例
	* @param containInstanceId
	* @return  List<CompositeInstance>
	* @throws
	*/
	public List<CompositeInstance> getCompositeInstancesByContainInstanceId(long containInstanceId);
}

