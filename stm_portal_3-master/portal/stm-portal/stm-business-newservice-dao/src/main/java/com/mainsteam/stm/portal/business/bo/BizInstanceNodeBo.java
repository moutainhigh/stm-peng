package com.mainsteam.stm.portal.business.bo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class BizInstanceNodeBo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1868342167255852777L;

	private long id;
	
	//业务ID
	private long bizId;
	
	//实例ID(为资源时即是资源实例ID，为业务时即为业务ID)
	private long instanceId;
	
	//节点名称
	private String showName;
	
	//名称是否隐藏(0不隐藏,1隐藏)
	private int nameHidden;
	
	//图标ID
	private long fileId;
	
	//1为默认,2为绑定子资源,3为绑定指标
	private int type;
	
	//节点状态(业务和资源有值)
	private int nodeStatus;
	
	//节点状态改变时间
	private Date statusTime;
	
	private List<BizNodeMetricRelBo> bind;

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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public List<BizNodeMetricRelBo> getBind() {
		return bind;
	}

	public void setBind(List<BizNodeMetricRelBo> bind) {
		this.bind = bind;
	}

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

}
