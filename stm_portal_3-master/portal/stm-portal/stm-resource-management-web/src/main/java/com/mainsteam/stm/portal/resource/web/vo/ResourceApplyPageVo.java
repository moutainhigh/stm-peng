package com.mainsteam.stm.portal.resource.web.vo;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.mainsteam.stm.platform.web.vo.BasePageVo;

public class ResourceApplyPageVo implements BasePageVo{

	/**
	 * 
	 */
	private static final long serialVersionUID = -124651674236302639L;
	private long startRow;
	private long rowCount;
	private long totalRecord;
	
	private ResourceApplyVo condition;
	private List<Map<String , String>> rows;
	
	public ResourceApplyVo getCondition() {
		return condition;
	}
	public void setCondition(ResourceApplyVo condition) {
		this.condition = condition;
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
	public void setRows(List<Map<String, String>> rows) {
		this.rows = rows;
	}
	
	@Override
	public long getTotal() {
		return this.totalRecord;
	}
	
	@Override
	public Collection<? extends Object> getRows() {
		return this.rows;
	}
	
}
