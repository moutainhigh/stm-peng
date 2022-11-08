package com.mainsteam.stm.common.metric.sync;

import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;

public class InstanceProfileChange {

	private long profileID;
	private long instanceID;
	private long parentID;
	private InstanceLifeStateEnum lifeState;
	
	public long getProfileID() {
		return profileID;
	}
	public void setProfileID(long profileID) {
		this.profileID = profileID;
	}
	public long getInstanceID() {
		return instanceID;
	}
	public void setInstanceID(long instanceID) {
		this.instanceID = instanceID;
	}

	public InstanceLifeStateEnum getLifeState() {
		return lifeState;
	}
	public void setLifeState(InstanceLifeStateEnum lifeState) {
		this.lifeState = lifeState;
	}

	public long getParentID() {
		return parentID;
	}

	public void setParentID(long parentID) {
		this.parentID = parentID;
	}

	@Override
	public String toString() {
		return "InstanceProfileChange{" +
				"profileID=" + profileID +
				", instanceID=" + instanceID +
				", parentID=" + parentID +
				", lifeState=" + lifeState +
				'}';
	}
}
