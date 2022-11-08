package com.mainsteam.stm.portal.netflow.po;

import java.util.List;

public class ConfInterfacePagePo {
	private long startRow;
	private long rowCount;
	private long totalRecord;
	private List<ConfInterfacePo> confInterfacePos;

	public long getTotalRecord() {
		return totalRecord;
	}

	public void setTotalRecord(long totalRecord) {
		this.totalRecord = totalRecord;
	}

	public List<ConfInterfacePo> getConfInterfacePos() {
		return confInterfacePos;
	}

	public void setConfInterfacePos(List<ConfInterfacePo> confInterfacePos) {
		this.confInterfacePos = confInterfacePos;
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
}
