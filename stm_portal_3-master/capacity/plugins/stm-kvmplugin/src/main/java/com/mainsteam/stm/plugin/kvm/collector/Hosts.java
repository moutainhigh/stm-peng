package com.mainsteam.stm.plugin.kvm.collector;

import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.plugin.kvm.util.LibVirtUtil;

/**
 * 
 * @author yuanlb TODO 下午3:14:13 desc 下午3:14:56
 */
public class Hosts {
	@SuppressWarnings("unused")
	private static final org.apache.commons.logging.Log log = LogFactory
			.getLog(Hosts.class);
	private String ip = null;
	private String username = null;
	private String passwd = null;
	private LibVirtUtil util = null;

	public void setIp(String ip) {
		this.ip = ip;
	}

	public LibVirtUtil getUtil() {
		return util;
	}

	public String getIp() {
		return ip;
	}

	public void setUserName(String name) {
		this.username = name;
	}

	public String getUserName() {
		return this.username;
	}

	public void setPasswd(String pass) {
		this.passwd = pass;
	}

	public String getPasswd() {
		return this.passwd;
	}

	protected Hosts(String ip, String username, String passwd) throws Exception {
		this.setIp(ip);
		this.setUserName(username);
		this.setPasswd(passwd);
		this.init();
	}

	protected void init() throws Exception {
		util = new LibVirtUtil(this.ip, this.username, this.passwd);
		util.login();
	}

	public void close() {
		util.close();
		util = null;
	}
}
