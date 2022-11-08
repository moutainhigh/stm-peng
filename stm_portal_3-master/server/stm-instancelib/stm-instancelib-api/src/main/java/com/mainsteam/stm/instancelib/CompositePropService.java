
package com.mainsteam.stm.instancelib;

import java.util.List;

import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.CompositeProp;


/**
 * 
 * 复合实例属性定义
 * @author xiaoruqiang
 */
public interface CompositePropService {

	
	/**
	 * 更新属性
	 * 
	 * @param prop 更新的属性。
	 * @throws Exception
	 */
	public void updateProp(CompositeProp prop)  throws InstancelibException;

	/**
	 * 更新属性
	 * 
	 * @param prop  更新的属性。
	 * @throws Exception
	 */
	public void updateProps(List<CompositeProp> prop) throws InstancelibException;

	/**
	 * 删除指定的属性
	 * 
	 * @param instanceId 实例Id
	 * @param key        属性的key
	 * @throws InstancelibException 自定义业务异常
	 * @throws Exception 
	 */
	public void removePropByInstanceAndKey(long instanceId,String key)  throws InstancelibException;
	
	/**
	 * 删除指定的实例全部属性
	 * 
	 * @param instanceId 实例Id
	 * @throws InstancelibException 自定义业务异常
	 * @throws Exception 
	 */
	public void removePropByInstance(long instanceId) throws InstancelibException;
	/**
	 * 查询属性的信息
	 * 
	 * @param instanceId 实例Id
	 * @param key  属性的key
	 * @return属性的信息
	 */
	public CompositeProp getPropByInstanceAndKey(long instanceId ,String key) throws InstancelibException;

	/**
	 * 查询属性的信息
	 * 
	 * @param instanceId
	 *             实例Id
	 * @return List<CompositeProp>属性的信息
	 *         key:属性的key,value:属性的信息
	 */
	public List<CompositeProp> getPropByInstanceId(long instanceId) throws InstancelibException;
	
}

