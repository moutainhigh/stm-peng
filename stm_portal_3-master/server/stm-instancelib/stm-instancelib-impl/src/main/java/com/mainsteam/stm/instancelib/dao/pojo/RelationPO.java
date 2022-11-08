package com.mainsteam.stm.instancelib.dao.pojo;

public class RelationPO {

	private long instanceId;
	
	private long fromInstanceId;
	
	private long toInstanceId;
	
	private String fromInstanceType;
	
	public long getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(long instanceId) {
		this.instanceId = instanceId;
	}

	public long getFromInstanceId() {
		return fromInstanceId;
	}

	public void setFromInstanceId(long fromInstanceId) {
		this.fromInstanceId = fromInstanceId;
	}

	public long getToInstanceId() {
		return toInstanceId;
	}

	public void setToInstanceId(long toInstanceId) {
		this.toInstanceId = toInstanceId;
	}

	public String getFromInstanceType() {
		return fromInstanceType;
	}

	public void setFromInstanceType(String fromInstanceType) {
		this.fromInstanceType = fromInstanceType;
	}

	public String getToInstanceType() {
		return toInstanceType;
	}

	public void setToInstanceType(String toInstanceType) {
		this.toInstanceType = toInstanceType;
	}

	public String getRelationType() {
		return relationType;
	}

	public void setRelationType(String relationType) {
		this.relationType = relationType;
	}

	private String toInstanceType;
	
	private String relationType;
	
}
