package com.mainsteam.stm.network.util;

import com.mainsteam.stm.network.ssh.SshTerminal;
import com.mainsteam.stm.network.telnet.TelnetTerminal;

public class NetWorkUtil {
	
	private NetWorkUtil(){}
	
	public static String execute(NetWorkBean bean) throws Exception{
		String remoteInfo = "";
		if(ProtocolEnum.SSH==bean.getProtocol()){
			remoteInfo = executeSSH(bean);
		}else if(ProtocolEnum.TELNET==bean.getProtocol()){
			remoteInfo = executeTELNET(bean);
		}
		return remoteInfo;
	}
	private static String executeSSH(NetWorkBean bean) throws Exception{
		SshTerminal terminal = new SshTerminal();
		boolean success = terminal.login(bean.getIp(), bean.getPort(), bean.getUserName(), bean.getPassword());
		if(!success)
			throw new Exception("login fail");
		for(String script : bean.getScripts()){
			//use ShellChannel,can't use ExecChannel
			terminal.runCMD(script,SshTerminal.ENTER);
		}
		String remoteInfo = terminal.getRemoteInfo();
		terminal.destory();
		return remoteInfo;
	}
	private static String executeTELNET(NetWorkBean bean)  throws Exception{
		TelnetTerminal terminal = new TelnetTerminal(bean.getIp(), bean.getPort());
		String[] loginInfo = terminal.getRemoteInfo().split("\\r?\\n");
		if(loginInfo[loginInfo.length - 1].contains("y/n") || 
				loginInfo[loginInfo.length - 1].contains("Y/N")){
			//登录前需要确认信息
			terminal.runCMD("\r\n");
		}
		terminal.runCMD(bean.getUserName(),bean.getPassword());
		for(String script : bean.getScripts()){
			terminal.runCMD(script);
		}
		String remoteInfo = terminal.getRemoteInfo();
		terminal.disconnect();
		return remoteInfo;
	}
	
}
