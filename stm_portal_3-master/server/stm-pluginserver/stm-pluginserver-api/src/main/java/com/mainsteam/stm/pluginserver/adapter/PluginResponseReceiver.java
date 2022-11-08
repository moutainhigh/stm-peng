/**
 * 
 */
package com.mainsteam.stm.pluginserver.adapter;

import java.util.List;

import com.mainsteam.stm.pluginserver.message.PluginResult;

/**
 * 接收PluginResponse数据
 * 
 * 提供给组件外部使用的接口，用来接收pluginResult数据
 * 
 * @author ziw
 * 
 */
public interface PluginResponseReceiver {
	/**
	 * 接收PluginResponse数据
	 * 
	 * @param response
	 */
	public void receivePluginResponse(List<PluginResult> responses);
}
