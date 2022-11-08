package com.mainsteam.stm.portal.resource.bo;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.mainsteam.stm.platform.web.vo.BasePageVo;

public class LogicalVolumeMetricDataPageBo implements Serializable,BasePageVo{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4066189893799716433L;
	private long startRow;
	private long rowCount;
	private long totalRecord;
	private LogicalVolumeMetricDataBo condition;
	private List<LogicalVolumeMetricDataBo> logicalVolumeData;
	
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
	public LogicalVolumeMetricDataBo getCondition() {
		return condition;
	}
	public void setCondition(LogicalVolumeMetricDataBo condition) {
		this.condition = condition;
	}
	public List<LogicalVolumeMetricDataBo> getLogicalVolumeData() {
		return logicalVolumeData;
	}
	public void setLogicalVolumeData(
			List<LogicalVolumeMetricDataBo> logicalVolumeData) {
		this.logicalVolumeData = logicalVolumeData;
	}
	
	@Override
	public long getTotal() {
		return this.totalRecord;
	}
	@Override
	public Collection<? extends Object> getRows() {
		return this.logicalVolumeData;
	}
}
