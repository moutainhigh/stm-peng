package com.mainsteam.stm.portal.business.bo;

import java.io.Serializable;

public class BizCanvasLinkBo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3402417489449748664L;

	private long id;
	
	//连线起始节点
	private long fromNode;
	
	//连线终止节点
	private long toNode;
	
	//连线属性
	private String attr;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getFromNode() {
		return fromNode;
	}

	public void setFromNode(long fromNode) {
		this.fromNode = fromNode;
	}

	public long getToNode() {
		return toNode;
	}

	public void setToNode(long toNode) {
		this.toNode = toNode;
	}

	public String getAttr() {
		return attr;
	}

	public void setAttr(String attr) {
		this.attr = attr;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (fromNode ^ (fromNode >>> 32));
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + (int) (toNode ^ (toNode >>> 32));
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
		BizCanvasLinkBo other = (BizCanvasLinkBo) obj;
		if (fromNode != other.fromNode)
			return false;
		if (id != other.id)
			return false;
		if (toNode != other.toNode)
			return false;
		return true;
	}
	
}
