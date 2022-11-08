/**
 * 
 */
package com.mainsteam.stm.pluginserver.manager;

import java.util.List;

import com.mainsteam.stm.pluginserver.obj.ReponseData;

/**
 * @author ziw
 * 
 */
public interface PluginRequestMonitor {
	
	public void forceFinish();

	public boolean isFinished();

	public List<ReponseData> waitCalculateDatas();
	
	public void recieveResult(ReponseData result, long requestId);
}
