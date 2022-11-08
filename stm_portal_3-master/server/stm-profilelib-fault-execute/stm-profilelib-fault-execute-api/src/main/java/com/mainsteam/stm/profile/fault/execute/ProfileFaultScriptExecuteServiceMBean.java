package com.mainsteam.stm.profile.fault.execute;

import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.profile.fault.execute.obj.FaultExecutPluginParameter;
import com.mainsteam.stm.profile.fault.execute.obj.FaultScriptExecuteResult;

public interface ProfileFaultScriptExecuteServiceMBean {

	/**
	* @Title: executeSnapshotScript
	* @Description: 执行快照脚本
	* @return  Object
	* @throws
	*/
	String executeSnapshotScript(FaultExecutPluginParameter params);
	
	/**
	* @Title: executeRecoveryScript
	* @Description: 执行故障恢复脚本
	* @param params
	* @return  Object
	* @throws
	*/
	String executeRecoveryScript(FaultExecutPluginParameter params);
	
	/**
	* @Title: executScript
	* @Description: 同时执行快照脚本和恢复脚本
	* @param params
	* @param snapshotScript
	* @param recoveryScript
	* @return
	* @throws PluginSessionRunException  FaultScriptExecuteResult
	* @throws
	*/
	FaultScriptExecuteResult executScript(FaultExecutPluginParameter params,String snapshotScript,String recoveryScript) throws PluginSessionRunException;
	
	public FaultScriptExecuteResult networkDeviceTerminalExecute(FaultExecutPluginParameter paramter, String snapshotScript, String recoveryScript);
}
