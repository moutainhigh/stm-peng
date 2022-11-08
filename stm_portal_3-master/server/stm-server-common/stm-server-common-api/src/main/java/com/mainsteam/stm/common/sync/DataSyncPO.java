package com.mainsteam.stm.common.sync;

import java.util.Date;

public class DataSyncPO {

	private Long id;
	private DataSyncTypeEnum type;
	private String data;
	private Integer state;
	private Date createTime;
	private Date updateTime;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
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
	public DataSyncTypeEnum getType() {
		return type;
	}
	public void setType(DataSyncTypeEnum type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "DataSyncPO{" +
				"id=" + id +
				", type=" + type +
				", data='" + data + '\'' +
				", state=" + state +
				", createTime=" + createTime +
				", updateTime=" + updateTime +
				'}';
	}
}
