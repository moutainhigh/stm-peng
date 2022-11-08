package com.mainsteam.stm.tinytool.obj;

import java.io.Serializable;

public class SnmpTarget implements Serializable{
	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -8122059800795071621L;
	
	private String ip="127.0.0.1";
	private String port="161";
	private String community="public";
	private String address="127.0.0.1/161";
	private int version;
	private long timeout=10000;
	private int retries=1;
	
	//安全名称
	private String securityName="";
	//安全等级
	private int securityLevel=1;
	//认证协议
	private String authenticationProtocol;
	//认证密码
	private String authenticationPassphrase;
	//私有协议
	private String  privacyProtocol;
	//私有密码
	private String  privacyPassphrase;
	
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
	public String getCommunity() {
		return community;
	}
	public void setCommunity(String community) {
		this.community = community;
	}
	public String getAddress() {
		return ip+"/"+port;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public long getTimeout() {
		return timeout;
	}
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
	public int getRetries() {
		return retries;
	}
	public void setRetries(int retries) {
		this.retries = retries;
	}
	public int getSecurityLevel() {
		return securityLevel;
	}
	public void setSecurityLevel(int securityLevel) {
		this.securityLevel = securityLevel;
	}
	public String getSecurityName() {
		return securityName;
	}
	public void setSecurityName(String securityName) {
		this.securityName = securityName;
	}
	public String getAuthenticationProtocol() {
		return authenticationProtocol;
	}
	public void setAuthenticationProtocol(String authenticationProtocol) {
		this.authenticationProtocol = authenticationProtocol;
	}
	public String getAuthenticationPassphrase() {
		return authenticationPassphrase;
	}
	public void setAuthenticationPassphrase(String authenticationPassphrase) {
		this.authenticationPassphrase = authenticationPassphrase;
	}
	public String getPrivacyProtocol() {
		return privacyProtocol;
	}
	public void setPrivacyProtocol(String privacyProtocol) {
		this.privacyProtocol = privacyProtocol;
	}
	public String getPrivacyPassphrase() {
		return privacyPassphrase;
	}
	public void setPrivacyPassphrase(String privacyPassphrase) {
		this.privacyPassphrase = privacyPassphrase;
	}
	
}
