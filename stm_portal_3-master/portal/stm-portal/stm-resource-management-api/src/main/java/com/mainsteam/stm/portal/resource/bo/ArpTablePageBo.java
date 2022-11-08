package com.mainsteam.stm.portal.resource.bo;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.mainsteam.stm.platform.web.vo.BasePageVo;

public class ArpTablePageBo implements Serializable,BasePageVo{

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 3386018331882035770L;
	
	private long startRow;
	private long rowCount;
	private long totalRecord;
	private ArpTableBo condition;
	private List<ArpTableBo> arpTableData;
	
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
	public ArpTableBo getCondition() {
		return condition;
	}
	public void setCondition(ArpTableBo condition) {
		this.condition = condition;
	}
	
	public List<ArpTableBo> getArpTableData() {
		return arpTableData;
	}
	public void setArpTableData(List<ArpTableBo> arpTableData) {
		this.arpTableData = arpTableData;
	}
	@Override
	public long getTotal() {
		return this.totalRecord;
	}
	@Override
	public Collection<? extends Object> getRows() {
		return this.arpTableData;
	}
	
}
