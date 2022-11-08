package com.mainsteam.stm.portal.resource.bo;

import java.io.Serializable;


public class ProcessDeadLockDataBo implements Serializable{

	/**
	 * 
	 */
	private final static String NULL_DATAGRID_VALUE = "--";
	
	private static final long serialVersionUID = -842440575817999532L;
	//资源实例ID
	private long resourceId;
	
	//进程sid
	private String sid = NULL_DATAGRID_VALUE;
	
	//数据库用户
	private String dataBaseUserName = NULL_DATAGRID_VALUE;
	
	//死锁的状态，如果有内容表示被死锁
	private String dataBaseLockWait = NULL_DATAGRID_VALUE;
	
	//状态，active表示被死锁
	private String dataBaseStatus = NULL_DATAGRID_VALUE;
	
	//死锁语句所在的机器
	private String dataBaseMachine = NULL_DATAGRID_VALUE;
	
	//产生死锁的语句主要来自哪个应用程序
	private String dataBaseProgram = NULL_DATAGRID_VALUE;

	public long getResourceId() {
		return resourceId;
	}

	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getDataBaseUserName() {
		return dataBaseUserName;
	}

	public void setDataBaseUserName(String dataBaseUserName) {
		this.dataBaseUserName = dataBaseUserName;
	}

	public String getDataBaseLockWait() {
		return dataBaseLockWait;
	}

	public void setDataBaseLockWait(String dataBaseLockWait) {
		this.dataBaseLockWait = dataBaseLockWait;
	}

	public String getDataBaseStatus() {
		return dataBaseStatus;
	}

	public void setDataBaseStatus(String dataBaseStatus) {
		this.dataBaseStatus = dataBaseStatus;
	}

	public String getDataBaseMachine() {
		return dataBaseMachine;
	}

	public void setDataBaseMachine(String dataBaseMachine) {
		this.dataBaseMachine = dataBaseMachine;
	}

	public String getDataBaseProgram() {
		return dataBaseProgram;
	}

	public void setDataBaseProgram(String dataBaseProgram) {
		this.dataBaseProgram = dataBaseProgram;
	}
	
	
}
