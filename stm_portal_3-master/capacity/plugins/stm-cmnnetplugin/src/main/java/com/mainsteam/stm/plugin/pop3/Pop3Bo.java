package com.mainsteam.stm.plugin.pop3;

import java.util.Properties;

public class Pop3Bo {
	private String ip;
	private String port;
	private String userName;
	private String userPassword;
	private String protocal = "pop3";
	public String getProtocal() {
		return protocal;
	}
	public void setProtocal(String protocal) {
		this.protocal = protocal;
	}
	public Properties getProperties(){
        Properties p = new Properties();
        p.put("mail.pop3.host", this.ip);
        p.put("mail.pop3.port", this.port);
        return p;
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
	public String getUserPassword() {
		return userPassword;
	}
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
}
