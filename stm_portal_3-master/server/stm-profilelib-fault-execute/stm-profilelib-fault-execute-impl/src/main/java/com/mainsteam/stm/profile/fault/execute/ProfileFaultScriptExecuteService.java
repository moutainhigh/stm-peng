package com.mainsteam.stm.profile.fault.execute;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.caplib.dict.PluginIdEnum;
import com.mainsteam.stm.executor.obj.PluginInitParameterData;
import com.mainsteam.stm.network.ssh.SshTerminal;
import com.mainsteam.stm.network.telnet.TelnetTerminal;
import com.mainsteam.stm.plugin.ssh.SSHSession;
import com.mainsteam.stm.plugin.telnet.TelnetSession;
import com.mainsteam.stm.plugin.wmi.WmiSession;
import com.mainsteam.stm.pluginprocessor.ParameterValue;
import com.mainsteam.stm.pluginsession.PluginResultSet;
import com.mainsteam.stm.pluginsession.PluginSession;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginArrayExecutorParameter;
import com.mainsteam.stm.profile.fault.execute.obj.FaultExecutPluginParameter;
import com.mainsteam.stm.profile.fault.execute.obj.FaultScriptExecuteResult;


public class ProfileFaultScriptExecuteService implements
		ProfileFaultScriptExecuteServiceMBean {

	private static final Log logger = LogFactory.getLog(ProfileFaultScriptExecuteService.class);
	
	@Override
	public String executeSnapshotScript(FaultExecutPluginParameter params) {
		if(logger.isInfoEnabled()){
			logger.info("begin execute snapshot script!");
		}
		StringBuffer resultContent = new StringBuffer();
		try {
			PluginResultSet resultSet = execute(params);
			if(null!=resultSet){
				for (int i = 0; i < resultSet.getRowLength(); i++) {
					for (int j = 0; j < resultSet.getColumnLength(); j++) {
						resultContent.append(resultSet.getValue(i, j));
						if(j!=resultSet.getColumnLength()-1){
							resultContent.append("\t\t\t\t");
						}
					}
					
					resultContent.append("\n");
				}
			}
		} catch (PluginSessionRunException e) {
			if(logger.isErrorEnabled()){
				logger.error("execute snapshot script error!",e);
			}
		}
		if(logger.isInfoEnabled()){
			logger.info("execyt snapshot script end! result Content:"+(resultContent==null?"0":resultContent.length()));
		}
		return resultContent==null?null:resultContent.toString();
	}

	@Override
	public String executeRecoveryScript(FaultExecutPluginParameter params) {
		if(logger.isInfoEnabled()){
			logger.info("begin execute recovery script!");
		}
		StringBuffer resultContent = new StringBuffer();
		try {
			PluginResultSet resultSet = execute(params);
			if(null!=resultSet){
				for (int i = 0; i < resultSet.getRowLength(); i++) {
					for (int j = 0; j < resultSet.getColumnLength(); j++) {
						resultContent.append(resultSet.getValue(i, j));
						if(j!=resultSet.getColumnLength()-1){
							resultContent.append("\t\t\t\t");
						}
					}
					resultContent.append("\n");
				}
			}
		} catch (PluginSessionRunException e) {
			if(logger.isErrorEnabled()){
				logger.error("execute recovery script error!",e);
			}
		}
		if(logger.isInfoEnabled()){
			logger.info("execute recovery script end! result Content:"+(resultContent==null?"0":resultContent.length()));
		}
		return resultContent==null?null:resultContent.toString();
	}
	
	private PluginResultSet execute(FaultExecutPluginParameter paramter) throws PluginSessionRunException{
		if(null!=paramter){
			if(null==paramter.getPluginId()){
				throw new PluginSessionRunException("execute script paramter pluginId is null!",null);
			}
			PluginSession pluginSession = null;
			PluginInitParameterData initParameter = new PluginInitParameterData();
			Map<String, String> paramterMap = new HashMap<String, String>();
			paramterMap.put("IP", paramter.getIp());
			paramterMap.put("username", paramter.getUserName());
			paramterMap.put("password", paramter.getPassword());
			switch (paramter.getPluginId()) {
			case SshPlugin:
				pluginSession = new SSHSession();
				paramterMap.put("port", paramter.getPort());
				break;
			case TelnetPlugin:
				pluginSession = new TelnetSession();
				paramterMap.put("hostType", paramter.getHostType());
				break;
			case WmiPlugin:
				pluginSession = new WmiSession();
				break;
			default:
				break;
			}
			initParameter.setParameter(paramterMap);
			PluginArrayExecutorParameter executeParameter = new PluginArrayExecutorParameter();
			Parameter[] commandParameter = new ParameterValue[1];
			ParameterValue pv =  new ParameterValue();
			pv.setKey("COMMAND");
			pv.setValue(paramter.getCommand());
			commandParameter[0]=pv;
			executeParameter.setParameters(commandParameter);
			pluginSession.init(initParameter);
			PluginResultSet resultSet = pluginSession.execute(executeParameter, null);
			pluginSession.destory();
			return resultSet;
		}
		return null;
	}
	
	public void start(){
		logger.info("ProfileFaultScriptExecuteServiceMBean is started!");
	}

	@Override
	public FaultScriptExecuteResult executScript(FaultExecutPluginParameter paramter, String snapshotScript, String recoveryScript) throws PluginSessionRunException {
		if(null==paramter.getPluginId()){
			throw new PluginSessionRunException("execute script paramter pluginId is null!",null);
		}
		if(logger.isInfoEnabled()){
			logger.info("begin execute script!");
		}
		PluginSession pluginSession = null;
		PluginInitParameterData initParameter = new PluginInitParameterData();
		Map<String, String> paramterMap = new HashMap<String, String>();
		paramterMap.put("IP", paramter.getIp());
		paramterMap.put("username", paramter.getUserName());
		paramterMap.put("password", paramter.getPassword());
		switch (paramter.getPluginId()) {
		case SshPlugin:
			pluginSession = new SSHSession();
			paramterMap.put("port", paramter.getPort());
			break;
		case TelnetPlugin:
			pluginSession = new TelnetSession();
			paramterMap.put("hostType", paramter.getHostType());
			break;
		case WmiPlugin:
			pluginSession = new WmiSession();
			break;
		default:
			break;
		}
		initParameter.setParameter(paramterMap);
		pluginSession.init(initParameter);
		PluginArrayExecutorParameter executeParameter = new PluginArrayExecutorParameter();
		Parameter[] commandParameter = new ParameterValue[1];
		FaultScriptExecuteResult result = new FaultScriptExecuteResult();
		if(null!=snapshotScript && !snapshotScript.isEmpty()){
			ParameterValue snapshotScriptParameter =  new ParameterValue();
			snapshotScriptParameter.setKey("COMMAND");
			snapshotScriptParameter.setValue(snapshotScript);
			commandParameter[0]=snapshotScriptParameter;
			executeParameter.setParameters(commandParameter);
			try {
				PluginResultSet snapshotResultSet = pluginSession.execute(executeParameter, null);
				String snapshotResultContent = convertPluginResultToString(snapshotResultSet);
				if(null!=snapshotResultContent && !snapshotResultContent.isEmpty()){
					result.setSnapshotFileContent(snapshotResultContent);
					if(logger.isInfoEnabled()){
						logger.info("snapshot script execute success; result content length:"+snapshotResultContent.length());
					}
				}else{
					if(logger.isInfoEnabled()){
						logger.info("snapshot script execute fail or result is empty;");
					}
				}
			} catch (PluginSessionRunException e) {
				if(logger.isErrorEnabled()){
					logger.error("execut snapshot script error:"+e);
				}
			}
		}
		
		if(recoveryScript!=null && !recoveryScript.isEmpty()){
			ParameterValue recoveryScriptParameter =  new ParameterValue();
			recoveryScriptParameter.setKey("COMMAND");
			recoveryScriptParameter.setValue(recoveryScript);
			commandParameter[0]=recoveryScriptParameter;
			executeParameter.setParameters(commandParameter);
			try {
				PluginResultSet recoveryResultSet = pluginSession.execute(executeParameter, null);
				String recoveryResultContent = convertPluginResultToString(recoveryResultSet);
				if(null!=recoveryResultContent && !recoveryResultContent.isEmpty()){
					result.setRecoveryFileContent(recoveryResultContent);
					if(logger.isInfoEnabled()){
						logger.info("recovery script execute success; result content length:"+recoveryResultContent.length());
					}
				}else{
					if(logger.isInfoEnabled()){
						logger.info("recovery script execute fail or result is empty;");
					}
				}
			} catch (PluginSessionRunException e) {
				if(logger.isErrorEnabled()){
					logger.error("execut recovery script error:"+e);
				}
			}
		}
		
		pluginSession.destory();
		if(logger.isInfoEnabled()){
			logger.info("execute script end!");
		}
		return result;
	}
	
	private String convertPluginResultToString(PluginResultSet resultSet){
		StringBuffer resultContent = new StringBuffer();
		if(null!=resultSet){
			for (int i = 0; i < resultSet.getRowLength(); i++) {
				for (int j = 0; j < resultSet.getColumnLength(); j++) {
					resultContent.append(resultSet.getValue(i, j));
					if(j!=resultSet.getColumnLength()-1){
						resultContent.append("\t\t\t\t");
					}
				}
				resultContent.append("\n");
			}
		}
		return resultContent.toString();
	}
	
	public FaultScriptExecuteResult networkDeviceTerminalExecute(FaultExecutPluginParameter paramter, String snapshotScript, String recoveryScript){
		FaultScriptExecuteResult result = new FaultScriptExecuteResult();
		if (paramter.getPluginId().equals(PluginIdEnum.SshPlugin)) {
			if(null!=snapshotScript && !snapshotScript.isEmpty()){
				try {
					SshTerminal sshTerminal = new SshTerminal();
					sshTerminal.login(paramter.getIp(), Integer.valueOf(paramter.getPort()), paramter.getUserName(), paramter.getPassword());
					sshTerminal.runCMD(snapshotScript);
					result.setSnapshotFileContent(sshTerminal.getRemoteInfo());
					sshTerminal.destory();
				} catch (Exception e) {
					if(logger.isErrorEnabled()){
						logger.error("NetworkDevice execute ssh snapshot script error:"+e);
					}
					result.setSnapshotFileContent(e.getMessage());
				}
			}
			
			if(recoveryScript!=null && !recoveryScript.isEmpty()){
				try {
					SshTerminal sshTerminal = new SshTerminal();
					sshTerminal.login(paramter.getIp(), Integer.valueOf(paramter.getPort()), paramter.getUserName(), paramter.getPassword());
					sshTerminal.runCMD(recoveryScript);
					result.setRecoveryFileContent(sshTerminal.getRemoteInfo());
					sshTerminal.destory();
				} catch (Exception e) {
					if(logger.isErrorEnabled()){
						logger.error("NetworkDevice execute ssh recovery script error:"+e);
					}
					result.setRecoveryFileContent(e.getMessage());
				}
			}
			
		}else if(paramter.getPluginId().equals(PluginIdEnum.TelnetPlugin)){
			if(null!=snapshotScript && !snapshotScript.isEmpty()){
				try {
					TelnetTerminal telnetTerminal = new TelnetTerminal(paramter.getIp(), Integer.valueOf(paramter.getPort()));
					telnetTerminal.runCMD(paramter.getUserName(),paramter.getPassword());
					String[] commonds = snapshotScript.split("\r\n");
					telnetTerminal.runCMD(commonds);
					Thread.sleep(3000);
					result.setSnapshotFileContent(telnetTerminal.getRemoteInfo());
					Thread.sleep(1000);
					telnetTerminal.disconnect();
				} catch (Exception e) {
					if(logger.isErrorEnabled()){
						logger.error("NetworkDevice execute telnet snapshot script error:"+e);
					}
					result.setSnapshotFileContent(e.getMessage());
				}
			}
			if(recoveryScript!=null && !recoveryScript.isEmpty()){
				try {
					TelnetTerminal telnetTerminal = new TelnetTerminal(paramter.getIp(), Integer.valueOf(paramter.getPort()));
					telnetTerminal.runCMD(paramter.getUserName(),paramter.getPassword());
					String[] commonds = recoveryScript.split("\r\n");
					telnetTerminal.runCMD(commonds);
					Thread.sleep(3000);
					result.setRecoveryFileContent(telnetTerminal.getRemoteInfo());
					Thread.sleep(1000);
					telnetTerminal.disconnect();
				} catch (Exception e) {
					if(logger.isErrorEnabled()){
						logger.error("NetworkDevice execute telnet recovery script error:"+e);
					}
					result.setRecoveryFileContent(e.getMessage());
				}
			}
		}
		return result;
	}
}
