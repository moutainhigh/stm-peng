package com.mainsteam.stm.profilelib.obj;

import java.io.Serializable;

public class Instance implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1475101427539589727L;
	/**
	 * 父资源实例Id
	 */
	private long parentInstanceId;
	/**
	 * 资源实例Id
	 */
	private long instanceId;
	
	public Instance(){};
	/**
	 * @param instanceId        子实例ID
	 * @param parentInstanceId  父实例ID
	 */
	public Instance(long instanceId,long parentInstanceId){
		this.instanceId = instanceId;
		this.parentInstanceId = parentInstanceId;
	}

	public long getParentInstanceId() {
		return parentInstanceId;
	}

	public void setParentInstanceId(long parentInstanceId) {
		this.parentInstanceId = parentInstanceId;
	}

	public long getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(long instanceId) {
		this.instanceId = instanceId;
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder(100);
		b.append("[parentInstanceId=").append(parentInstanceId);
		b.append(", instanceId=").append(instanceId).append("]");
		return b.toString();
	}
	
	
}
