package com.mainsteam.stm.portal.resource.bo;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.mainsteam.stm.platform.web.vo.BasePageVo;


public class FileMetricDataPageBo implements Serializable,BasePageVo{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6891097657635036625L;
	/**
	 * 
	 */
	
	private long startRow;
	private long rowCount;
	private long totalRecord;
	private FileMetricDataBo condition;
	private List<FileMetricDataBo> fileData;
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
	
	public FileMetricDataBo getCondition() {
		return condition;
	}
	public void setCondition(FileMetricDataBo condition) {
		this.condition = condition;
	}
//	public List<FileMetricDataBo> getFileData() {
//		return fileData;
//	}
	public void setFileData(List<FileMetricDataBo> fileData) {
		this.fileData = fileData;
	}
	@Override
	public long getTotal() {
		return this.totalRecord;
	}
	@Override
	public Collection<? extends Object> getRows() {
		return this.fileData;
	}
	
	

}
