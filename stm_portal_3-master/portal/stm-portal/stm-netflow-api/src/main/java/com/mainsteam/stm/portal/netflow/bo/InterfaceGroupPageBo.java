package com.mainsteam.stm.portal.netflow.bo;

import java.util.List;

public class InterfaceGroupPageBo {

	private long startRow;
	private long rowCount;
	private long totalRecord;
	private List<InterfaceGroupBo> interfaceGroupBos;

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

	public List<InterfaceGroupBo> getInterfaceGroupBos() {
		return interfaceGroupBos;
	}

	public void setInterfaceGroupBos(List<InterfaceGroupBo> interfaceGroupBos) {
		this.interfaceGroupBos = interfaceGroupBos;
	}

}
