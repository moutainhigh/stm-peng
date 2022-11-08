package com.mainsteam.stm.instancelib.obj;

public abstract class AbstractRelation implements Relation {

	private long instanceId;
	
	public long getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(long instanceId) {
		this.instanceId = instanceId;
	}

	
	public abstract String getRelationType();

}
