
package com.mainsteam.stm.instancelib;

import java.util.List;

import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.CustomProp;


/**
 * 
 * 资源实例参数属性定义
 * @author xiaoruqiang
 */
public interface CustomPropService {

	/**
	 * 添加属性
	 * @param prop 属性值
	 * @throws Exception 
	 */
	public void addProp(CustomProp prop) throws InstancelibException;
	
	/**
	 * 添加属性
	 * @param prop 属性值
	 * @throws Exception
	 */
	public void addProps(List<CustomProp> prop)  throws InstancelibException;

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
	public CustomProp getPropByInstanceAndKey(long instanceId ,String key) throws InstancelibException;

	/**
	 * 查询属性的信息
	 * 
	 * @param instanceId
	 *             实例Id
	 * @return List<CustomProp>属性的信息
	 *         key:属性的key,value:属性的信息
	 */
	public List<CustomProp> getPropByInstanceId(long instanceId) throws InstancelibException;
	
}

