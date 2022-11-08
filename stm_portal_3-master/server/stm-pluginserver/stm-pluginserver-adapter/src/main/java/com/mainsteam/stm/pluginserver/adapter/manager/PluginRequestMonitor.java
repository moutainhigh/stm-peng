/**
 * 
 */
package com.mainsteam.stm.pluginserver.adapter.manager;

import java.util.List;

import com.mainsteam.stm.pluginserver.excepton.PluginServerExecuteException;
import com.mainsteam.stm.pluginserver.obj.ReponseData;

/**
 * @author ziw
 * 
 */
public interface PluginRequestMonitor {
	
	public void forceFinish();

	public boolean isFinished();

	public List<ReponseData> waitCalculateDatas() throws PluginServerExecuteException;
	
	public void recieveResult(ReponseData result, Long requestId);
}
