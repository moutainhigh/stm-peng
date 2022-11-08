/**
 * 
 */
package com.mainsteam.stm.instancelib;

import java.util.List;
import java.util.Map;

import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.CustomPropDefinition;

/**
 * 
 * 用来管理资源实例的自定义属性的定义
 * 
 * @author ziw
 * 
 */
public interface CustomPropDefService {
	/**
	 * 添加一个新的自定义属性
	 * 
	 * @param definition
	 *            自定义属性
	 * @throws Exception 
	 */
	public void addCustomPropDefinition(CustomPropDefinition definition) throws InstancelibException;

	/**
	 * 批量新的自定义属性
	 * 
	 * @param definition
	 *            自定义属性
	 * @throws Exception 
	 */
	public void addCustomPropDefinition(List<CustomPropDefinition> definition) throws InstancelibException;
	
	/**
	 * 更新指定的自定义属性
	 * 
	 * @param definition
	 *            要更新的自定义属性。用key来指定具体的属性
	 * @throws Exception 
	 */
	public void updateCustomPropDefinition(CustomPropDefinition definition) throws InstancelibException;

	/**
	 * 删除指定的自定义属性
	 * 
	 * @param keys
	 *            自定义属性的key
	 * @return true:成功,false:失败，属性不存在
	 * 
	 *         //TODO:如果该属性已经被资源实例使用，抛出异常
	 * @throws Exception 
	 */
	public void removeCustomPropDefinitionByKey(List<String> keys) throws InstancelibException;

	/**
	 * 查询自定义属性的信息
	 * 
	 * @param key
	 *            自定义属性的key
	 * @return 自定义属性的信息
	 * @throws Exception 
	 */
	public CustomPropDefinition getCustomPropDefinitionByKey(String key) throws InstancelibException;

	/**
	 * 查询自定义属性的信息
	 * 
	 * @param keys
	 *            自定义属性的key列表
	 * @return Map<String,CustomPropDefinition> 自定义属性的信息
	 *         key:自定义属性的key,value:自定义属性的信息
	 */
	public Map<String, CustomPropDefinition> getCustomPropDefinitionsByKeys(
			List<String> keys) throws InstancelibException;
}
