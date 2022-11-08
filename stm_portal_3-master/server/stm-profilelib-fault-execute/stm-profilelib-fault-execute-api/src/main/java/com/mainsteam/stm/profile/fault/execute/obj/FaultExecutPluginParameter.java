package com.mainsteam.stm.profile.fault.execute.obj;

import java.io.Serializable;

import com.mainsteam.stm.caplib.dict.PluginIdEnum;

public class FaultExecutPluginParameter implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5490461170399214056L;
	private String ip;
	private String port;
	private String userName;
	private String password;
	private String hostType;
	private PluginIdEnum pluginId;
	private String command;
	
	
	public FaultExecutPluginParameter(){}
	
	public FaultExecutPluginParameter(String ip,String userName,String password){
		this.ip = ip;
		this.userName = userName;
		this.password = password;
	}
	
	
	
	public FaultExecutPluginParameter(String ip, String port, String userName, String password, String hostType) {
		super();
		this.ip = ip;
		this.port = port;
		this.userName = userName;
		this.password = password;
		this.hostType = hostType;
	}

	
	public FaultExecutPluginParameter(String ip, String port, String userName, String password) {
		super();
		this.ip = ip;
		this.port = port;
		this.userName = userName;
		this.password = password;
	}
	
	

	public FaultExecutPluginParameter(String ip, String port, String userName, String password, String hostType, PluginIdEnum pluginId, String command) {
		super();
		this.ip = ip;
		this.port = port;
		this.userName = userName;
		this.password = password;
		this.hostType = hostType;
		this.pluginId = pluginId;
		this.command = command;
	}
	
	@Override
	public String toString() {
		StringBuffer sub = new StringBuffer();
		sub.append("ip:").append(this.ip).append(",\t");
		sub.append("port:").append(this.port).append(",\t");
		sub.append("userName:").append(this.userName).append(",\t");
		sub.append("password:").append(this.password).append(",\t");
		sub.append("hostType:").append(this.hostType).append(",\t");
		sub.append("pluginId:").append(this.pluginId);
		return sub.toString();
	}
	

	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getHostType() {
		return hostType;
	}
	public void setHostType(String hostType) {
		this.hostType = hostType;
	}
	public PluginIdEnum getPluginId() {
		return pluginId;
	}
	public void setPluginId(PluginIdEnum pluginId) {
		this.pluginId = pluginId;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	
	
	
}
