package com.mainsteam.stm.plugin.parameter;

public class IBMWebSphereParameter {
	
	private String deployType;
	private String isSecurity;
	private String appDmgrIp;
	private int appDmgrPort;
	private String keyStorePath;
	private String trustStorePath;
	private String keyStorePassword;
	private String trustStorePassword;
	
	public String getDeployType() {
		return deployType;
	}
	public void setDeployType(String deployType) {
		this.deployType = deployType;
	}
	public String getIsSecurity() {
		return isSecurity;
	}
	public void setIsSecurity(String isSecurity) {
		this.isSecurity = isSecurity;
	}
	public String getAppDmgrIp() {
		return appDmgrIp;
	}
	public void setAppDmgrIp(String appDmgrIp) {
		this.appDmgrIp = appDmgrIp;
	}
	public int getAppDmgrPort() {
		return appDmgrPort;
	}
	public void setAppDmgrPort(int appDmgrPort) {
		this.appDmgrPort = appDmgrPort;
	}
	public String getKeyStorePath() {
		return keyStorePath;
	}
	public void setKeyStorePath(String keyStorePath) {
		this.keyStorePath = keyStorePath;
	}
	public String getTrustStorePath() {
		return trustStorePath;
	}
	public void setTrustStorePath(String trustStorePath) {
		this.trustStorePath = trustStorePath;
	}
	public String getKeyStorePassword() {
		return keyStorePassword;
	}
	public void setKeyStorePassword(String keyStorePassword) {
		this.keyStorePassword = keyStorePassword;
	}
	public String getTrustStorePassword() {
		return trustStorePassword;
	}
	public void setTrustStorePassword(String trustStorePassword) {
		this.trustStorePassword = trustStorePassword;
	}
	
}
