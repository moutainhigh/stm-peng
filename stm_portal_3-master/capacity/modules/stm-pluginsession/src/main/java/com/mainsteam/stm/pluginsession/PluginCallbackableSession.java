/**
 * 
 */
package com.mainsteam.stm.pluginsession;

import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;

/**
 * plugin执行
 * 
 * @author ziw
 * 
 */
public interface PluginCallbackableSession {
	/**
	 * 初始化，建立好session连接
	 * 
	 * @param initParameters
	 */
	public void init(PluginInitParameter initParameters);

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
	 * 注册回调方法
	 * 
	 * @param callback
	 */
	public void setCallBack(PluginSessionCallback callback);
}
