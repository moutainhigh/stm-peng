package com.mainsteam.stm.instancelib.dao.pojo;

public class InstanceCollectionPO {

	private long instanceId;
	
	private long containInstanceId;
	
	private String containInstanceType;

	public long getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(long instanceId) {
		this.instanceId = instanceId;
	}

	public long getContainInstanceId() {
		return containInstanceId;
	}

	public void setContainInstanceId(long containInstanceId) {
		this.containInstanceId = containInstanceId;
	}

	public String getContainInstanceType() {
		return containInstanceType;
	}

	public void setContainInstanceType(String containInstanceType) {
		this.containInstanceType = containInstanceType;
	}

	
}
