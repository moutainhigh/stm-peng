package com.mainsteam.stm.system.authentication.bo;

public class Authentication {
	//认证模式
	private String auMode;
	//LDAP服务器地址
	private String ip;
	//服务器端口
	private String port;
	//加密方式(0none 1simple 2strong)
	private String  encryptionMode;
	//从哪个DN下开始搜索
	private String baseDn;
	//用户名
	private String userName;
	//密码
	private String passWd;
		
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
	public String getBaseDn() {
		return baseDn;
	}
	
	public String getEncryptionMode() {
		return encryptionMode;
	}
	public void setEncryptionMode(String encryptionMode) {
		this.encryptionMode = encryptionMode;
	}
	public void setBaseDn(String baseDn) {
		this.baseDn = baseDn;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassWd() {
		return passWd;
	}
	public void setPassWd(String passWd) {
		this.passWd = passWd;
	}
	public String getAuMode() {
		return auMode;
	}
	public void setAuMode(String auMode) {
		this.auMode = auMode;
	}
	
}
