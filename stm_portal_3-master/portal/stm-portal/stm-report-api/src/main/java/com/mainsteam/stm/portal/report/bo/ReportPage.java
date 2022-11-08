package com.mainsteam.stm.portal.report.bo;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.mainsteam.stm.platform.web.vo.BasePageVo;


public class ReportPage  implements Serializable,BasePageVo{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1126825593700157805L;
	private long startRow;
	private long rowCount;
	private long totalRecord;
	private List<Report> reports;
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
	public void setReports(List<Report> reports) {
		this.reports = reports;
	}
	@Override
	public long getTotal() {
		return this.totalRecord;
	}
	@Override
	public Collection<? extends Object> getRows() {
		return this.reports;
	}

}
