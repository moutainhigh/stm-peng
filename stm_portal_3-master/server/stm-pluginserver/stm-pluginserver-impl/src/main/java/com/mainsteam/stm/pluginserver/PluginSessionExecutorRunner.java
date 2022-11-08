/**
 * 
 */
package com.mainsteam.stm.pluginserver;

import com.mainsteam.stm.pluginserver.cable.RunnerCoreCable;
import com.mainsteam.stm.pluginserver.message.PluginRequest;

/** 
 * @author 作者：ziw
 * @date 创建时间：2016年10月26日 下午4:20:10
 * @version 1.0
 */
/** 
 */
public interface PluginSessionExecutorRunner extends Runnable {
	public RunnerCoreCable getRunnerCoreCable();
	public void setRunnerCoreCable(RunnerCoreCable c);
	public PluginRequest getPluginRequest();
}
