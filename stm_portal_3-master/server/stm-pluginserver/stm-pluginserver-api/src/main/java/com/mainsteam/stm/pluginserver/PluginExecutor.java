/**
 * 
 */
package com.mainsteam.stm.pluginserver;

import com.mainsteam.stm.pluginprocessor.ResultSet;
import com.mainsteam.stm.pluginserver.message.PluginRequest;
import com.mainsteam.stm.pluginserver.pool.PluginSessionPool;
import com.mainsteam.stm.pluginsession.PluginSessionContext;

/**
 * plugin执行器
 * 
 * @author ziw
 * 
 */
public interface PluginExecutor {
	/**
	 * 执行PluginRequest
	 * 
	 * @param request
	 *            请求执行的数据
	 * 
	 * @param context
	 *            当前请求的session的上下文对象
	 * 
	 * @return ResultSet 处理后的结果集
	 */
	public ResultSet executePlugin(PluginRequest request,
			PluginSessionContext context, PluginSessionPool pool) throws Exception;
}
