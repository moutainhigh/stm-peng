package com.mainsteam.stm.pluginsession.parameter;

import java.util.List;

/**
 * @todo vcenter vo
 * @author yuanlb
 *
 */
public class JBrokerVcenterParameter {
	/**
	 * 连接参数 host ip username password
	 */
	private String host;
	private String ip;
	private String username;
	private String password;

	private String hostname;// 主机名
	private String descr;// 描述
	private String version;// 版本
	private String vendor;// 供应商

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
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

	@Override
	public String toString() {
		return "\n(数据中心) [(主机名称)name=" + hostname + ", (描述)descr=" + descr
				+ ", (版本)version=" + version + ", (供应商)vendor=" + vendor
				+ ", (esx服务器)esxservers=" + esxservers + ", (集群)clusters="
				+ clusters + "]";
	}

	List<JBrokerESXServerParameter> esxservers;// esx服务器

	List<JBrokerClusterParameter> clusters;

	// private String esxServers;

	public List<JBrokerClusterParameter> getClusters() {
		return clusters;
	}

	public void setClusters(List<JBrokerClusterParameter> clusters) {
		this.clusters = clusters;
	}

	public String getVendor() {
		return vendor;
	}

	public List<JBrokerESXServerParameter> getEsxservers() {
		return esxservers;
	}

	public void setEsxservers(List<JBrokerESXServerParameter> esxservers) {
		this.esxservers = esxservers;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}
