package com.mainsteam.stm.portal.resource.bo;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.mainsteam.stm.platform.web.vo.BasePageVo;

public class RollbackPageBo implements Serializable, BasePageVo {
	
	private static final long serialVersionUID = 2436004004307837243L;
	private long instanceId;
	private long startRow;
	private long rowCount;
	private long totalRecord;
	private RollbackBo condition;
	private List<RollbackBo> rollbackBoList;

	public long getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(long instanceId) {
		this.instanceId = instanceId;
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

	public RollbackBo getCondition() {
		return condition;
	}

	public void setCondition(RollbackBo condition) {
		this.condition = condition;
	}

	public List<RollbackBo> getRollbackBoList() {
		return rollbackBoList;
	}

	public void setRollbackBoList(List<RollbackBo> rollbackBoList) {
		this.rollbackBoList = rollbackBoList;
	}

	@Override
	public long getTotal() {
		return this.totalRecord;
	}

	@Override
	public Collection<? extends Object> getRows() {
		return this.rollbackBoList;
	}

}
