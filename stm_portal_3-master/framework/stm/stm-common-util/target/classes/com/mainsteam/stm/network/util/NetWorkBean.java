package com.mainsteam.stm.network.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NetWorkBean implements Serializable{
	private static final long serialVersionUID = -9094761238941564312L;
	private String ip;
	private int port;
	private String userName;
	private String password;
	private ProtocolEnum protocol = ProtocolEnum.TELNET;
	private List<String> scripts;

	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public List<String> getScripts() {
		if(scripts == null){
			scripts = new ArrayList<String>();
		}
		return scripts;
	}
	public void setScripts(List<String> scripts) {
		this.scripts = scripts;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
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
	public ProtocolEnum getProtocol() {
		return protocol;
	}
	public void setProtocol(ProtocolEnum protocol) {
		this.protocol = protocol;
	}
	
}
