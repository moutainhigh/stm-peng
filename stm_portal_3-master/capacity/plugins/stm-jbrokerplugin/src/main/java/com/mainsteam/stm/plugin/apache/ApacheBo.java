package com.mainsteam.stm.plugin.apache;

public class ApacheBo {
	/**
	 * apache后台管理地址
	 */
	private String urlParam;
	/**
	 * 是否启用ssl
	 */
	private boolean isSSL;
	/**
	 * apache版本
	 */
	private String apacheVersion;
	
	public String getUrlParam() {
		return urlParam;
	}
	public void setUrlParam(String urlParam) {
		this.urlParam = urlParam;
	}
	public boolean isSSL() {
		return isSSL;
	}
	public void setSSL(boolean isSSL) {
		this.isSSL = isSSL;
	}
	public String getApacheVersion() {
		return apacheVersion;
	}
	public void setApacheVersion(String apacheVersion) {
		this.apacheVersion = apacheVersion;
	}
}
