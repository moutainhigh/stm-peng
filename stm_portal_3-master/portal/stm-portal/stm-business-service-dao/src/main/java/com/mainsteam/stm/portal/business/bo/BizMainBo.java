package com.mainsteam.stm.portal.business.bo;

import java.io.Serializable;
import java.util.Date;

public class BizMainBo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3265233713843880715L;

	//id
	private long id;
	
	//业务名称
	private String name;
	
	//业务备注
	private String remark;
	
	//责任人ID
	private long managerId;
	
	//责任人姓名
	private String managerName;
	
	//创建人ID
	private long createId;
	
	//业务创建时间
	private Date createTime;
	
	//业务最后修改时间
	private Date updateTime;
	
	//业务图标ID
	private long fileId;
	
	//状态定义
	private String statusDefine;
	
	//当前状态
	private int status;
	
	//当前健康度
	private int health;
	
	//所属域ID
	private long domainId;

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getManagerName() {
		return managerName;
	}

	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public long getManagerId() {
		return managerId;
	}

	public void setManagerId(long managerId) {
		this.managerId = managerId;
	}

	public long getCreateId() {
		return createId;
	}

	public void setCreateId(long createId) {
		this.createId = createId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public long getFileId() {
		return fileId;
	}

	public void setFileId(long fileId) {
		this.fileId = fileId;
	}

	public String getStatusDefine() {
		return statusDefine;
	}

	public void setStatusDefine(String statusDefine) {
		this.statusDefine = statusDefine;
	}

	public long getDomainId() {
		return domainId;
	}

	public void setDomainId(long domainId) {
		this.domainId = domainId;
	}
	
}
