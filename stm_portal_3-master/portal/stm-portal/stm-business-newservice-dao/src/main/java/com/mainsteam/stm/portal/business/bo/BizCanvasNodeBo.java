package com.mainsteam.stm.portal.business.bo;

import java.io.Serializable;
import java.util.Date;

public class BizCanvasNodeBo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1868342167255852777L;

	private long id;
	
	//业务ID
	private long bizId;
	
	//实例ID(为资源时即是资源实例ID，为业务时即为业务ID)
	private long instanceId;
	
	//节点类型(1.资源2.业务3.图标4.文本框5.已删除资源)
	private int nodeType;
	
	//节点名称
	private String showName;
	
	//名称是否隐藏(0不隐藏,1隐藏)
	private int nameHidden;
	
	//图标ID
	private long fileId;
	
	//节点状态(业务和资源有值)
	private int nodeStatus;
	
	//节点状态改变时间
	private Date statusTime;
	
	//节点绘图属性
	private String attr;
	
	public long getBizId() {
		return bizId;
	}

	public void setBizId(long bizId) {
		this.bizId = bizId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(long instanceId) {
		this.instanceId = instanceId;
	}

	public int getNodeType() {
		return nodeType;
	}

	public void setNodeType(int nodeType) {
		this.nodeType = nodeType;
	}

	public String getShowName() {
		return showName;
	}

	public void setShowName(String showName) {
		this.showName = showName;
	}

	public int getNameHidden() {
		return nameHidden;
	}

	public void setNameHidden(int nameHidden) {
		this.nameHidden = nameHidden;
	}

	public long getFileId() {
		return fileId;
	}

	public void setFileId(long fileId) {
		this.fileId = fileId;
	}

	public int getNodeStatus() {
		return nodeStatus;
	}

	public void setNodeStatus(int nodeStatus) {
		this.nodeStatus = nodeStatus;
	}

	public Date getStatusTime() {
		return statusTime;
	}

	public void setStatusTime(Date statusTime) {
		this.statusTime = statusTime;
	}

	public String getAttr() {
		return attr;
	}

	public void setAttr(String attr) {
		this.attr = attr;
	}
	
	
	
}
