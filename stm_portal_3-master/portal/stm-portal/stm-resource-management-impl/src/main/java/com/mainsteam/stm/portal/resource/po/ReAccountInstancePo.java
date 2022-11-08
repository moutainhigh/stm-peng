package com.mainsteam.stm.portal.resource.po;

import java.util.Date;

public class ReAccountInstancePo {
	/**
	 * 预置账户ID
	 */
	private Long account_id;
	/**
	 * 资源实例ID
	 */
	private Long instanceid;
	/**
	 * 资源实例名称
	 */
	private String instancename;
	/**
	 * 资源实例类型
	 */
	private String instancetype;
	/**
	 * 资源实例IP
	 */
	private String instanceip;
	// 状态(0无效/1有效)
	private String status;
	// 创建人ID
	private Long entry_id;
	// 创建时间
	private Date entry_datetime;
	// 删除时间
	private Date delete_datetime;

	public Long getAccount_id() {
		return account_id;
	}

	public void setAccount_id(Long account_id) {
		this.account_id = account_id;
	}

	public Long getInstanceid() {
		return instanceid;
	}

	public void setInstanceid(Long instanceid) {
		this.instanceid = instanceid;
	}

	public String getInstancename() {
		return instancename;
	}

	public void setInstancename(String instancename) {
		this.instancename = instancename;
	}

	public String getInstancetype() {
		return instancetype;
	}

	public void setInstancetype(String instancetype) {
		this.instancetype = instancetype;
	}

	public String getInstanceip() {
		return instanceip;
	}

	public void setInstanceip(String instanceip) {
		this.instanceip = instanceip;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getEntry_id() {
		return entry_id;
	}

	public void setEntry_id(Long entry_id) {
		this.entry_id = entry_id;
	}

	public Date getEntry_datetime() {
		return entry_datetime;
	}

	public void setEntry_datetime(Date entry_datetime) {
		this.entry_datetime = entry_datetime;
	}

	public Date getDelete_datetime() {
		return delete_datetime;
	}

	public void setDelete_datetime(Date delete_datetime) {
		this.delete_datetime = delete_datetime;
	}

}
