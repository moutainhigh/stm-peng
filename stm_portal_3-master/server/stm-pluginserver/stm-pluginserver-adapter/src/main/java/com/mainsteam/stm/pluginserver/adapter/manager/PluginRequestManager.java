/**
 * 
 */
package com.mainsteam.stm.pluginserver.adapter.manager;

import com.mainsteam.stm.pluginserver.message.PluginRequest;

/**
 * @author ziw
 * 
 */
public interface PluginRequestManager {
	
	/**
	 * 查看PluginRequest是否已经在处理中
	 * 
	 * @param req
	 * @return
	 */
	public boolean isPluginRequestInProcess(PluginRequest req);
	
	public void recordPluginRequest(PluginRequest req);

	public PluginRequest getPluginRequest(long requestId);

	public PluginRequest takePluginRequest(long requestId);
	
	/**
	 * 取消指定的PluginRequest的监控信息 
	 * 
	 * @param requestId
	 */
	public void removeTrapPluginRequest(long requestId);
}
