package com.mainsteam.stm.portal.resource.bo;

import java.util.List;

import com.mainsteam.stm.instancelib.obj.ResourceInstance;


public class ResourceInstancePageBo {
	
	private long startRow;
	private long rowCount;
	private long totalRecord;
	private List<ResourceInstance> resources;
	public long getStartRow() {
		return startRow;
	}
	public void setStartRow(long startRow) {
		this.startRow = startRow;
	}
	public long getRowCount() {
		return rowCount;
	}
	public void setRowCount(long rowCount) {
		this.rowCount = rowCount;
	}
	public long getTotalRecord() {
		return totalRecord;
	}
	public void setTotalRecord(long totalRecord) {
		this.totalRecord = totalRecord;
	}
	public List<ResourceInstance> getResources() {
		return resources;
	}
	public void setResources(List<ResourceInstance> resources) {
		this.resources = resources;
	}
	
	

}
