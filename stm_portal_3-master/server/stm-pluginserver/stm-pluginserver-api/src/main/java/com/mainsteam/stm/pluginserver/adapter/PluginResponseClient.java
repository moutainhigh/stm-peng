/**
 * 
 */
package com.mainsteam.stm.pluginserver.adapter;

import java.util.List;

import com.mainsteam.stm.pluginserver.message.PluginResult;

/**
 * plugin响应客户端
 * 
 * @author ziw
 * 
 */
public interface PluginResponseClient {
	/**
	 * 将plugin响应数据发送到数据处理组件
	 * 
	 * @param response
	 */
	public void sendPluginReponse(List<PluginResult> response);
}
