package com.mainsteam.stm.portal.resource.bo;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.mainsteam.stm.platform.web.vo.BasePageVo;


public class ProcessMetricDataPageBo implements Serializable,BasePageVo{
	
	private static final long serialVersionUID = 3122370356227155165L;
	private long startRow;
	private long rowCount;
	private long totalRecord;
	private ProcessMetricDataBo condition;
	private List<ProcessMetricDataBo> processData;
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
	public ProcessMetricDataBo getCondition() {
		return condition;
	}
	public void setCondition(ProcessMetricDataBo condition) {
		this.condition = condition;
	}
//	public List<ProcessMetricDataVo> getProcessData() {
//		return processData;
//	}
	public void setProcessData(List<ProcessMetricDataBo> processData) {
		this.processData = processData;
	}
	@Override
	public long getTotal() {
		return this.totalRecord;
	}
	@Override
	public Collection<? extends Object> getRows() {
		return this.processData;
	}
	
	

}
