/**
 * 
 */
package com.mainsteam.stm.pluginprocessor;

import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;

/**
 * plugin结果集数据转换器
 * 
 * @author ziw
 * 
 */
public interface PluginResultSetConverter {
	/**
	 * 
	 * 将plugin结果集数据转换为指标数据
	 * 
	 * @param resultSet
	 *            plugin结果集
	 * @param parameter
	 *            转换器执行需要的参数
	 * @return 指标数据。指标数据指定为一维数据
	 */
	public ConverterResult[] convert(ResultSet resultSet, ProcessParameter parameter)throws  PluginSessionRunException ;
}
