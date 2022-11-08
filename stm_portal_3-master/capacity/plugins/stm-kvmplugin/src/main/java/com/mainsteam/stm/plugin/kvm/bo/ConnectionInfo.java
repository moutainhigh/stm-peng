package com.mainsteam.stm.plugin.kvm.bo;

/**
 * 
 * @author yuanlb TODO code format 下午3:11:57
 */
public class ConnectionInfo {
	public final String vmname, username, password;

	public ConnectionInfo(String vmname, String username, String password) {
		super();
		this.vmname = vmname;
		this.username = username;
		this.password = password;
	}
}
