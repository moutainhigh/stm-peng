package com.mainsteam.stm.portal.business.bo;

import java.io.Serializable;

public class BizNodeMetricRelBo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8921086891767722765L;

	//id
	private long id;
	
	//资源节点ID
	private long nodeId;
	
	//子资源实例ID
	private long childInstanceId;
	
	//指标ID
	private String metricId;
	
	private String name;
	
	private int status;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getNodeId() {
		return nodeId;
	}

	public void setNodeId(long nodeId) {
		this.nodeId = nodeId;
	}

	public long getChildInstanceId() {
		return childInstanceId;
	}

	public void setChildInstanceId(long childInstanceId) {
		this.childInstanceId = childInstanceId;
	}

	public String getMetricId() {
		return metricId;
	}

	public void setMetricId(String metricId) {
		this.metricId = metricId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (int) (childInstanceId ^ (childInstanceId >>> 32));
		result = prime * result
				+ ((metricId == null) ? 0 : metricId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BizNodeMetricRelBo other = (BizNodeMetricRelBo) obj;
		if (childInstanceId != other.childInstanceId)
			return false;
		if (metricId == null) {
			if (other.metricId != null)
				return false;
		} else if (!metricId.equals(other.metricId))
			return false;
		return true;
	}
	
}
