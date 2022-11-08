/**
 * 
 */
package com.mainsteam.stm.pluginserver;

import java.util.List;

import com.mainsteam.stm.pluginserver.adapter.PluginResponseClient;
import com.mainsteam.stm.pluginserver.message.PluginRequest;
import com.mainsteam.stm.pluginserver.message.PluginResult;

/**
 *
 * malachi in plugin容器，提供plugin的运行环境和管理
 * 
 * @author ziw
 * 
 */
public interface PluginContainer {
	
	//public void registerDelayedRunningIp(String ip);
	
	/**
	 * 处理一个plugin请求对象
	 * 
	 * @param request
	 */
	public void handlePluginRequest(List<PluginRequest> request);

	/**
	 * 处理PluginResult
	 * 
	 * @param result
	 */
	public void handlePluginResult(List<PluginResult> result);

	/**
	 * 获取plugin响应数据的发送客户端
	 * 
	 * @return PluginResponseClient
	 */
	public PluginResponseClient getPluginResponseClient();
	
	/**
	 * 设置plugin响应数据的发送客户端
	 * 
	 * @param client
	 */
	public void setPluginResponseClient(PluginResponseClient client);
}
