/**
 * 
 */
package com.mainsteam.stm.rpc.server;

import java.util.Date;

import javax.management.ObjectName;

/**
 * @author ziw
 * 
 */
public class RemoteServiceInfo {

	/**
	 * 远程服务id
	 */
	private int remoteServiceId;

	/**
	 * 远程服务描述
	 */
	private String remoteServiceDesc;

	/**
	 * 注册日期
	 */
	private Date registerDate;

	/**
	 * 注册服务的类名
	 */
	private String className;

	private ObjectName objectName;
	
	private Object instance;
	/**
	 * 
	 */
	public RemoteServiceInfo() {
	}

	public int getRemoteServiceId() {
		return remoteServiceId;
	}

	public void setRemoteServiceId(int remoteServiceId) {
		this.remoteServiceId = remoteServiceId;
	}

	public String getRemoteServiceDesc() {
		return remoteServiceDesc;
	}

	public void setRemoteServiceDesc(String remoteServiceDesc) {
		this.remoteServiceDesc = remoteServiceDesc;
	}

	public Date getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public ObjectName getObjectName() {
		return objectName;
	}

	public void setObjectName(ObjectName objectName) {
		this.objectName = objectName;
	}

	public Object getInstance() {
		return instance;
	}

	public void setInstance(Object instance) {
		this.instance = instance;
	}
}
