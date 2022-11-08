package com.mainsteam.stm.plugin.sunjes.util;

public class SunJESConnInfo {
	private String ip;
	private int port;
	private String instanceName;
	private String userName;
	private String password;
	
	public SunJESConnInfo(String ip, int port, String instanceName,
			String userName, String password) {
		this.ip = ip;
		this.port = port;
		this.instanceName = instanceName;
		this.userName = userName;
		this.password = password;
	}
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getInstanceName() {
		return instanceName;
	}
	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
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
	
	public String getKey() {
		return ip+":"+port+"/"+instanceName+"_"+userName+"_"+password;
	}
}
