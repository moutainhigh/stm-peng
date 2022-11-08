/**
 * 
 */
package com.mainsteam.stm.plugin.snmptest;

/**
 * @author fevergreen
 * 
 */
public class SnmpParameter {

	public String[] oids;
	public String ip;
	public int port;
	public String version;
	public String user;
	public String password;
	public String serverLevel;

	/**
	 * 
	 */
	public SnmpParameter() {
		// TODO Auto-generated constructor stub
	}

	public String[] getOids() {
		return oids;
	}

	public void setOids(String[] oids) {
		this.oids = oids;
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

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getServerLevel() {
		return serverLevel;
	}

	public void setServerLevel(String serverLevel) {
		this.serverLevel = serverLevel;
	}
}
