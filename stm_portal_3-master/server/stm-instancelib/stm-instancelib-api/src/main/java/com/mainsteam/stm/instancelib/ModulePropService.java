
package com.mainsteam.stm.instancelib;

import java.util.List;

import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ModuleProp;


/**
 * 
 * 资源实例参数属性定义
 * @author xiaoruqiang
 */
public interface ModulePropService {

	/**
	 * 查询属性的信息
	 * 
	 * @param instanceId 实例Id
	 * @param key  属性的key
	 * @return属性的信息
	 */
	public ModuleProp getPropByInstanceAndKey(long instanceId ,String key) throws InstancelibException;

	/**
	 * 查询属性的信息
	 * 
	 * @param instanceId
	 *             实例Id
	 * @return List<ModuleProp>属性的信息
	 *         key:属性的key,value:属性的信息
	 */
	public List<ModuleProp> getPropByInstanceId(long instanceId) throws InstancelibException;
	
	
	/**
	 * 更新指定的属性
	 * 
	 * @param prop 更新的属性。
	 * @throws Exception
	 */
	public void updateProp(ModuleProp prop)  throws InstancelibException;

	/**
	 * 更新指定的属性
	 * 
	 * @param prop  更新的属性。
	 * @throws Exception
	 */
	public void updateProps(List<ModuleProp> prop) throws InstancelibException;
}

