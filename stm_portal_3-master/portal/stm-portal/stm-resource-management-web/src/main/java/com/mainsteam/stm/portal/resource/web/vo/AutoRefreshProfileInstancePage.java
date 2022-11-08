package com.mainsteam.stm.portal.resource.web.vo;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.mainsteam.stm.platform.web.vo.BasePageVo;


public class AutoRefreshProfileInstancePage implements Serializable,BasePageVo{
	
	private static final long serialVersionUID = 3122370356227155165L;
	private long startRow;
	private long rowCount;
	private long totalRecord;
	private AutoRefreshProfileInstance condition;
	private List<AutoRefreshProfileInstance> resources;

	//策略是否监控了该资源类型下的所有设备
	private boolean isSelectAll;
	public long getStartRow() {
		return startRow;
	}
	public void setStartRow(long startRow) {
		this.startRow = startRow;
	}
	public long getRowCount() {
		return rowCount;
	}
	public boolean isSelectAll() {
		return isSelectAll;
	}
	public void setSelectAll(boolean isSelectAll) {
		this.isSelectAll = isSelectAll;
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
	public AutoRefreshProfileInstance getCondition() {
		return condition;
	}
	public void setCondition(AutoRefreshProfileInstance condition) {
		this.condition = condition;
	}
//	public List<AutoRefreshProfileInstance> getResources() {
//		return resources;
//	}
	public void setResources(List<AutoRefreshProfileInstance> resources) {
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
