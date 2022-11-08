/**
 * 
 */
package com.mainsteam.stm.pluginserver.pool;

import com.mainsteam.stm.pluginsession.PluginSession;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;

/**
 * Pluing session池。允许同一个session有多个实例。但是实例的
 * 
 * 个数在这里进行控制。
 * 
 * 池的概念提出，是为了在连接书越多，
 * 
 * @author ziw
 * 
 */
public interface PluginSessionPool {

	public int getMaxActive();

	public int getMaxIdle();

	public String getPluginId();

	public String getPluginSessionKey();

	public PluginInitParameter getInitParameter();

	/**
	 * 拿到一个可用的session对象
	 * 
	 * @return
	 */
	public PluginSession borrowSession() throws Exception;

	/**
	 * 返回session对象
	 * 
	 * @param session
	 * @throws Exception
	 */
	public void returnSession(PluginSession session) throws Exception;

	/**
	 * 销毁该session对象池
	 * 
	 * @throws Exception
	 * 
	 */
	public void destory() throws Exception;

	/**
	 * 销毁该session对象
	 * 
	 * @throws Exception
	 * 
	 */
	public void destory(PluginSession session) throws Exception;
}
