package com.mainsteam.stm.instancelib.dao;

import java.util.List;

import com.mainsteam.stm.instancelib.dao.pojo.CustomPropDefinitionDO;
import com.mainsteam.stm.instancelib.dao.pojo.CustomPropDefinitionQueryDO;

/**
 * 
 * 操作自定义属性表
 * 
 * @author ziw
 * 
 */
public interface CustomPropDefinitionDAO {
	/**
	 * 根据属性类型，查询类型晓得自定义属性信息列表
	 * 
	 * @param category
	 * @return List<CustomPropDefinitionDO> 自定义属性信息列表
	 */
	public List<CustomPropDefinitionDO> getCustomPropDefinitionDOsByCategory(
			String category) throws Exception;

	/**
	 * 根据属性key，查询自定义属性
	 * 
	 * @param key
	 *            属性key
	 * @return CustomPropDefinitionDO 自定义属性信息
	 */
	public CustomPropDefinitionDO getCustomPropDefinitionDOByKey(String key)
			throws Exception;

	/**
	 * 根据属性key，查询自定义属性
	 * 
	 * @param key
	 *            属性key
	 * @return CustomPropDefinitionDO 自定义属性信息
	 */
	public List<CustomPropDefinitionDO> getCustomPropDefinitionDOsByKeys(
			List<String> key) throws Exception;

	/**
	 * 根据CustomPropDefinitionDO里的非空对象，查询自定义属性信息列表
	 * 
	 * @param def
	 *            自定义属性信息
	 * @return List<CustomPropDefinitionDO> 自定义属性信息列表
	 */
	public List<CustomPropDefinitionDO> getCustomPropDefinitionDOsByQuery(
			CustomPropDefinitionQueryDO def) throws Exception;

	/**
	 * 插入属性信息
	 * 
	 * @param definitionDO
	 */
	public void insertCustomPropDefinitionDO(CustomPropDefinitionDO definitionDO)
			throws Exception;

	/**
	 * 根据属性key,更新属性信息
	 * 
	 * @param definitionDOs
	 */
	public void updateCustomPropDefinitionDO(CustomPropDefinitionDO definitionDO)
			throws Exception;

	/**
	 * 删除自定义属性信息
	 * 
	 * @param key
	 *            自定义属性key
	 * @return 删除的行数
	 */
	public int removeCustomPropDefinitionDOByKey(String key) throws Exception;

	/**
	 * 插入属性信息
	 * 
	 * @param definitionDO
	 */
	void insertCustomPropDefinitionDOs(
			List<CustomPropDefinitionDO> definitionDOs) throws Exception;

	/**
	 * 根据属性key,批量更新属性信息
	 * 
	 * @param definitionDOs
	 */
	void updateCustomPropDefinitionDOs(
			List<CustomPropDefinitionDO> definitionDOs) throws Exception;

	/**
	 * 删除自定义属性信息
	 * 
	 * @param key
	 *            自定义属性key
	 * @return 删除的行数
	 */
	int removeCustomPropDefinitionDOByKey(List<String> keys) throws Exception;
}
