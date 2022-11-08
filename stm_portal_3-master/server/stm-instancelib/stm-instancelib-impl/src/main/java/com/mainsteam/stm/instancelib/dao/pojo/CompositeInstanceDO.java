package com.mainsteam.stm.instancelib.dao.pojo;


public class CompositeInstanceDO {

	/**
	 * 复合实例ID
	 */
	private long instanceId;
	/**
	 * 实例名称
	 */
	private String instanceName;
	/**
	 * 复合实例类型
	 */
	private String instanceType;
	


	public long getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(long instanceId) {
		this.instanceId = instanceId;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

	public String getInstanceType() {
		return instanceType;
	}

	public void setInstanceType(String instanceType) {
		this.instanceType = instanceType;
	}

}
