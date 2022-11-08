package com.mainsteam.stm.portal.business.bo;

import java.io.Serializable;

public class BizStatusDefineParameter implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8729855727315503267L;

	private long nodeId;
	
	private String nodeName;
	
	private int weight;
	
	private int nodeType;
	
	private String nodeTypeName;
	
	//参数情况0.可添加,-1.改参数为业务本身，不可添加，-2.该子业务参数会造成状态计算环路，不可添加
	private int type;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getNodeId() {
		return nodeId;
	}

	public void setNodeId(long nodeId) {
		this.nodeId = nodeId;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int getNodeType() {
		return nodeType;
	}

	public void setNodeType(int nodeType) {
		this.nodeType = nodeType;
	}

	public String getNodeTypeName() {
		return nodeTypeName;
	}

	public void setNodeTypeName(String nodeTypeName) {
		this.nodeTypeName = nodeTypeName;
	}
	
}
