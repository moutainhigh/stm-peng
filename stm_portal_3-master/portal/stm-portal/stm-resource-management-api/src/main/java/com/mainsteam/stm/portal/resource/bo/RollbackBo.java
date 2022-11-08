package com.mainsteam.stm.portal.resource.bo;

import java.io.Serializable;

public class RollbackBo implements Serializable {
	private static final long serialVersionUID = -8424630422884407915L;
	private String segmentName;
	private String segmentId;
	private String owner;
	private String tablespaceName;
	private String status;
	private String extents;
	private String xacts;
	private String curext;
	private String curblk;
	
	public String getSegmentName() {
		return segmentName;
	}
	public void setSegmentName(String segmentName) {
		this.segmentName = segmentName;
	}
	public String getSegmentId() {
		return segmentId;
	}
	public void setSegmentId(String segmentId) {
		this.segmentId = segmentId;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getTablespaceName() {
		return tablespaceName;
	}
	public void setTablespaceName(String tablespaceName) {
		this.tablespaceName = tablespaceName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getExtents() {
		return extents;
	}
	public void setExtents(String extents) {
		this.extents = extents;
	}
	public String getXacts() {
		return xacts;
	}
	public void setXacts(String xacts) {
		this.xacts = xacts;
	}
	public String getCurext() {
		return curext;
	}
	public void setCurext(String curext) {
		this.curext = curext;
	}
	public String getCurblk() {
		return curblk;
	}
	public void setCurblk(String curblk) {
		this.curblk = curblk;
	}
	
}
