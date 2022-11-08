package com.mainsteam.stm.portal.resource.bo;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.mainsteam.stm.platform.web.vo.BasePageVo;

public class SessionPageBo implements Serializable, BasePageVo {

	private static final long serialVersionUID = -4643323600024709679L;
	private long startRow;
	private long rowCount;
	private long totalRecord;
	private ArpTableBo condition;
	private List<SessionBo> sessionBoList;

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

	public List<SessionBo> getSessionBoList() {
		return sessionBoList;
	}

	public void setSessionBoList(List<SessionBo> sessionBoList) {
		this.sessionBoList = sessionBoList;
	}

	@Override
	public long getTotal() {
		return this.totalRecord;
	}

	@Override
	public Collection<? extends Object> getRows() {
		return this.sessionBoList;
	}

}
