package com.mainsteam.stm.portal.resource.web.vo;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.platform.web.vo.BasePageVo;


public class ResourceInstancePageVo implements Serializable,BasePageVo{
	
	private static final long serialVersionUID = 3122370356227155165L;
	private long startRow;
	private long rowCount;
	private long totalRecord;
	private ResourceInstance condition;
	private List<ResourceInstanceVo> resources;
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
//	public List<ResourceInstanceVo> getResources() {
//		return resources;
//	}
	public void setResources(List<ResourceInstanceVo> resources) {
		this.resources = resources;
	}
	public ResourceInstance getCondition() {
		return condition;
	}
	public void setCondition(ResourceInstance condition) {
		this.condition = condition;
	}
	@Override
	public long getTotal() {
		return this.totalRecord;
	}
	@Override
	public Collection<? extends Object> getRows() {
		return this.resources;
	}
	
	

}
