package com.mainsteam.stm.portal.netflow.bo;

import java.util.List;

public class ConfInterfacePageBo {
	private long startRow;
	private long rowCount;
	private long totalRecord;
	private List<ConfInterfaceBo> confInterfaceBos;

	public long getTotalRecord() {
		return totalRecord;
	}

	public void setTotalRecord(long totalRecord) {
		this.totalRecord = totalRecord;
	}

	public List<ConfInterfaceBo> getConfInterfaceBos() {
		return confInterfaceBos;
	}

	public void setConfInterfaceBos(List<ConfInterfaceBo> confInterfaceBos) {
		this.confInterfaceBos = confInterfaceBos;
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
