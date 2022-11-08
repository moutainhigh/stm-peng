/**
 * 
 */
package com.mainsteam.stm.pluginserver.adapter;

import java.util.List;

import com.mainsteam.stm.pluginserver.message.PluginRequest;

/**
 * 提供接口，接收Plugin的执行请求
 * 
 * @author ziw
 * 
 */
public interface PluginRequestReceiver {
	/**
	 * 接收plugin请求执行数据
	 * 
	 * @param request
	 *            plugin请求执行数据
	 */
	public void receivePluginRequest(List<PluginRequest> request);
}
