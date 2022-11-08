package com.mainsteam.stm.portal.statist.po;

import java.util.Date;

public class StatistQueryMainPo {
	private Long id;
	private Long createUserId;
	private Date createTime;
	private String name;
	// 1.性能统计2.资产统计
	private String type;
	private String categoryId;
	private String subResourceId;
	// 0.未删除 1.删除
	private Integer isDelete = 0;
	private Long domainId;
	private Long iReportId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(Long createUserId) {
		this.createUserId = createUserId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getSubResourceId() {
		return subResourceId;
	}

	public void setSubResourceId(String subResourceId) {
		this.subResourceId = subResourceId;
	}

	public Integer getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
	}

	public Long getDomainId() {
		return domainId;
	}

	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}

	public Long getiReportId() {
		return iReportId;
	}

	public void setiReportId(Long iReportId) {
		this.iReportId = iReportId;
	}
}
