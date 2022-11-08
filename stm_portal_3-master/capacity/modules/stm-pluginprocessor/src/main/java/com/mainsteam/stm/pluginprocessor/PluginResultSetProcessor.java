/**
 * 
 */
package com.mainsteam.stm.pluginprocessor;

import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;

/**
 * 处理pluing的返回数据的处理器
 * 
 * @author ziw
 * 
 */
public interface PluginResultSetProcessor {
	/**
	 * 处理pluing的返回结果集数据，对结果集进行修改。
	 * 
	 * @param resultSet
	 *            plugin返回的结果集
	 * @param parameter
	 *            处理器的输入参数
	 */
	public void process(ResultSet resultSet, ProcessParameter parameter,
			PluginSessionContext context)throws  PluginSessionRunException ;
}
