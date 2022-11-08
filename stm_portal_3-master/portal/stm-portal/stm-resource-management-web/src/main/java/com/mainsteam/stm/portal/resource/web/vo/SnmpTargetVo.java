package com.mainsteam.stm.portal.resource.web.vo;

import java.io.Serializable;

public class SnmpTargetVo implements Serializable{

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -6500517841780746168L;
	
	private String discoverNode="-1";
	private String ip="127.0.0.1";
	private String port="161";
	private String community="public";
	private String address="";
	private int version;
	private long timeout=1000;
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
		return this.ip+"/"+this.port ;
	}

	public int getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = Integer.parseInt(version);
	}
	public long getTimeout() {
		return timeout;
	}
	public void setTimeout(String timeout) {
		this.timeout = Long.parseLong(timeout);
	}
	public int getRetries() {
		return retries;
	}
	public void setRetries(String retries) {
		this.retries = Integer.parseInt(retries);
	}
	public String getSecurityName() {
		return securityName;
	}
	public void setSecurityName(String securityName) {
		this.securityName = securityName;
	}
	public int getSecurityLevel() {
		return securityLevel;
	}
	public void setSecurityLevel(String securityLevel) {
		this.securityLevel = Integer.parseInt(securityLevel);
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
	public String getDiscoverNode() {
		return discoverNode;
	}
	public void setDiscoverNode(String discoverNode) {
		this.discoverNode = discoverNode;
	}
	
}
