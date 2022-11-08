package com.mainsteam.stm.portal.resource.web.vo;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.mainsteam.stm.platform.web.vo.BasePageVo;


public class ChildResourceStrategyPageVo implements Serializable,BasePageVo{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7550256859172543271L;
	private long startRow;
	private long rowCount;
	private long totalRecord;
	private long selectStatus;
	private ChildResourceStrategyVo condition;
	private List<Long> resourceIds;
	private List<ChildResourceStrategyVo> resources;
	public List<Long> getResourceIds() {
		return resourceIds;
	}
	public void setResourceIds(List<Long> resourceIds) {
		this.resourceIds = resourceIds;
	}
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
	public long getSelectStatus() {
		return selectStatus;
	}
	public void setSelectStatus(long selectStatus) {
		this.selectStatus = selectStatus;
	}
	public ChildResourceStrategyVo getCondition() {
		return condition;
	}
	public void setCondition(ChildResourceStrategyVo condition) {
		this.condition = condition;
	}
//	public List<MainResourceStrategyVo> getResources() {
//		return resources;
//	}
	public void setResources(List<ChildResourceStrategyVo> resources) {
		this.resources = resources;
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
