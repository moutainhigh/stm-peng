/**
 * 
 */
package com.mainsteam.stm.pluginserver.pool;

import com.mainsteam.stm.pluginserver.message.PluginRequest;

/**
 * Plugin session池管理
 * 
 * @author ziw
 * 
 */
public interface PluginSessionPoolManager {
	/**
	 * 得到PluginSession池对象
	 * 
	 * @param request
	 *            Plugin执行请求
	 * @return PluginSession池对象
	 */
	public PluginSessionPool getPluginSessionPool(PluginRequest request);
	
	/**
	 * 关闭PluginSession池对象
	 * 
	 * @param request
	 *            Plugin执行请求
	 */
	public void closePluginSessionPool(PluginRequest request);
	
	/**
	 * 获取或者创建PluginSession池对象 
	 * 
	 * @param request
	 * @return
	 */
	public PluginSessionPool getOrCreatePluginSessionPool(PluginRequest request);
}
