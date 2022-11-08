package com.mainsteam.stm.common.instance.dao.obj;

import java.util.Date;

import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;

public class InstanceLifeCycle {
	private long instanceID;
	private InstanceStateEnum state;
	private Date changeTime;
	public Date getChangeTime() {
		return changeTime;
	}
	public void setChangeTime(Date changeTime) {
		this.changeTime = changeTime;
	}
	public InstanceStateEnum getState() {
		return state;
	}
	public void setState(InstanceStateEnum state) {
		this.state = state;
	}
	public long getInstanceID() {
		return instanceID;
	}
	public void setInstanceID(long instanceID) {
		this.instanceID = instanceID;
	}
}
