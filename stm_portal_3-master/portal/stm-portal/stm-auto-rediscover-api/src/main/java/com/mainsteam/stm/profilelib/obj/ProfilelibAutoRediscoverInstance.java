package com.mainsteam.stm.profilelib.obj;

import java.io.Serializable;
import java.util.Date;

/**
 * 自动重新发现策略与资源的关系
 * @author Administrator
 *
 */
public class ProfilelibAutoRediscoverInstance implements Serializable {
	private static final long serialVersionUID = -1503989844053532032L;
	private long id;
	private long profileId;
	private long instanceId;
	private Date executeTime;
	private Date nextExecuteTime;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getProfileId() {
		return profileId;
	}
	public void setProfileId(long profileId) {
		this.profileId = profileId;
	}
	public long getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(long instanceId) {
		this.instanceId = instanceId;
	}
	public Date getExecuteTime() {
		return executeTime;
	}
	public void setExecuteTime(Date executeTime) {
		this.executeTime = executeTime;
	}
	public Date getNextExecuteTime() {
		return nextExecuteTime;
	}
	public void setNextExecuteTime(Date nextExecuteTime) {
		this.nextExecuteTime = nextExecuteTime;
	}
	public ProfilelibAutoRediscoverInstance(long id, long profileId, long instanceId, Date executeTime, Date nextExecuteTime) {
		super();
		this.id = id;
		this.profileId = profileId;
		this.instanceId = instanceId;
		this.executeTime = executeTime;
		this.nextExecuteTime = nextExecuteTime;
	}
	public ProfilelibAutoRediscoverInstance() {
		super();
	}
	public ProfilelibAutoRediscoverInstance(long profileId, long instanceId) {
		super();
		this.profileId = profileId;
		this.instanceId = instanceId;
	}
	
	
}
