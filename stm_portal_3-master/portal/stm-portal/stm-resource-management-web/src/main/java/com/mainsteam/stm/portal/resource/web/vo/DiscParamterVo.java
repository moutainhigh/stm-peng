package com.mainsteam.stm.portal.resource.web.vo;

import java.io.Serializable;

public class DiscParamterVo implements Serializable {

	private static final long serialVersionUID = 5925843380703196426L;

	private int nodeGroupId;

	private String IP;

	private String port;

	private String community;

	private String Version;

	private String SecurityLevel;

	private String Securityname;

	private String AuthProtocol;

	private String PrivProtocol;

	private String PrivPwd;

	private String SnmpTimeout;

	private String SnmpRetry;

	private String IcmpTimeout;

	private String username;

	private String password;

	private String userprompt;

	private String passprompt;

	private String opprompt;

	public int getNodeGroupId() {
		return nodeGroupId;
	}

	public void setNodeGroupId(int nodeGroupId) {
		this.nodeGroupId = nodeGroupId;
	}

	public String getIP() {
		return IP;
	}

	public void setIP(String iP) {
		IP = iP;
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

	public String getVersion() {
		return Version;
	}

	public void setVersion(String version) {
		Version = version;
	}

	public String getSecurityLevel() {
		return SecurityLevel;
	}

	public void setSecurityLevel(String securityLevel) {
		SecurityLevel = securityLevel;
	}

	public String getSecurityname() {
		return Securityname;
	}

	public void setSecurityname(String securityname) {
		Securityname = securityname;
	}

	public String getAuthProtocol() {
		return AuthProtocol;
	}

	public void setAuthProtocol(String authProtocol) {
		AuthProtocol = authProtocol;
	}

	public String getPrivProtocol() {
		return PrivProtocol;
	}

	public void setPrivProtocol(String privProtocol) {
		PrivProtocol = privProtocol;
	}

	public String getPrivPwd() {
		return PrivPwd;
	}

	public void setPrivPwd(String privPwd) {
		PrivPwd = privPwd;
	}

	public String getSnmpTimeout() {
		return SnmpTimeout;
	}

	public void setSnmpTimeout(String snmpTimeout) {
		SnmpTimeout = snmpTimeout;
	}

	public String getSnmpRetry() {
		return SnmpRetry;
	}

	public void setSnmpRetry(String snmpRetry) {
		SnmpRetry = snmpRetry;
	}

	public String getIcmpTimeout() {
		return IcmpTimeout;
	}

	public void setIcmpTimeout(String icmpTimeout) {
		IcmpTimeout = icmpTimeout;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserprompt() {
		return userprompt;
	}

	public void setUserprompt(String userprompt) {
		this.userprompt = userprompt;
	}

	public String getPassprompt() {
		return passprompt;
	}

	public void setPassprompt(String passprompt) {
		this.passprompt = passprompt;
	}

	public String getOpprompt() {
		return opprompt;
	}

	public void setOpprompt(String opprompt) {
		this.opprompt = opprompt;
	}

}
