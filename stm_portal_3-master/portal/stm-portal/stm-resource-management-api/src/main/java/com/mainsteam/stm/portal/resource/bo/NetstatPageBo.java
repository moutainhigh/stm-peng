package com.mainsteam.stm.portal.resource.bo;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.mainsteam.stm.platform.web.vo.BasePageVo;

public class NetstatPageBo implements Serializable,BasePageVo{

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -139150999122114768L;
	
	private long startRow;
	private long rowCount;
	private long totalRecord;
	private NetstatBo condition;
	private List<NetstatBo> netstatData;
	
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
	public NetstatBo getCondition() {
		return condition;
	}
	public void setCondition(NetstatBo condition) {
		this.condition = condition;
	}
	public List<NetstatBo> getNetstatData() {
		return netstatData;
	}
	public void setNetstatData(List<NetstatBo> netstatData) {
		this.netstatData = netstatData;
	}
	
	@Override
	public long getTotal() {
		return this.totalRecord;
	}
	@Override
	public Collection<? extends Object> getRows() {
		return this.netstatData;
	}

}
