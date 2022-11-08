/**
 * 
 */
package com.mainsteam.stm.pluginsession;

import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.pluginsession.parameter.PluginExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;

/**
 * plugin执行
 * 
 * @author ziw
 * 
 */
public interface PluginSession {
	/**
	 * 初始化，建立好session连接
	 * 
	 * @param initParameters
	 */
	public void init(PluginInitParameter initParameters)
			throws PluginSessionRunException;

	/**
	 * 检查是否可用，如果可用返回true，如果不可用抛出异常并设置错误码
	 * @param initParameters
	 * @return
	 * @throws PluginSessionRunException
	 */
	public boolean check(PluginInitParameter initParameters)
			throws PluginSessionRunException;
	/**
	 * 销毁，释放资源
	 */
	public void destory();

	/**
	 * 重新建立session连接
	 */
	public void reload();

	/**
	 * 判断session是否可用
	 * 
	 * @return true:可用,false:不可用
	 */
	public boolean isAlive();

	/**
	 * 执行session
	 * 
	 * @param executorParameter
	 *            执行参数
	 * @param context
	 *            执行时的上下文。
	 * @return PluginResultSet
	 */
	public PluginResultSet execute(
			PluginExecutorParameter<?> executorParameter,
			PluginSessionContext context) throws PluginSessionRunException;
}
