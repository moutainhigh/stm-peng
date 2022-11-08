/**
 * 
 */
package com.mainsteam.stm.pluginserver.adapter;

import java.util.List;

import com.mainsteam.stm.pluginserver.excepton.PluginServerExecuteException;
import com.mainsteam.stm.pluginserver.message.PluginRequest;
import com.mainsteam.stm.pluginserver.obj.ReponseData;

/**
 * PluginRequest客户端
 * 
 * 提供给组件外部使用的接口，用来发送pluginRequest请求
 * 
 * @author ziw
 * 
 */
public interface PluginRequestClient {
	/**
	 * 发送plugin执行请求到plugin 容器
	 * 
	 * @param request
	 *            plugin执行请求
	 */
	public void sendPluginRequest(List<PluginRequest> request);

	/**
	 * 发送plugin执行请求到plugin 容器，并接收返回值
	 * 
	 * @param request
	 * @return MetricCalculateData
	 * @throws MetricExecutorException
	 */
	public List<ReponseData> executePluginRequest(List<PluginRequest> request)
			throws PluginServerExecuteException;
}
