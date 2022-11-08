package com.mainsteam.stm.portal.alarm.bo;

import java.util.List;

import com.mainsteam.stm.alarm.obj.AlarmNotify;

public class AlarmNotifyPageBo {
	private long startRow;
	private long rowCount;
	private long totalRecord;
	private List<AlarmNotify> rows;
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
	public List<AlarmNotify> getRows() {
		return rows;
	}
	public void setRows(List<AlarmNotify> rows) {
		this.rows = rows;
	}
	
	
}
