package com.mainsteam.stm.portal.alarm.bo;

import java.util.List;

public class AlarmKnowledgePageBo {
	private long startRow;
	private long rowCount;
	private long totalRecord;
	private List<AlarmKnowledgeBo> rows;
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
	public List<AlarmKnowledgeBo> getRows() {
		return rows;
	}
	public void setRows(List<AlarmKnowledgeBo> rows) {
		this.rows = rows;
	}
	
	
}
