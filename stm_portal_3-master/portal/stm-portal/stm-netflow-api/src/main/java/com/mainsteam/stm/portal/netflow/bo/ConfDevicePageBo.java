package com.mainsteam.stm.portal.netflow.bo;

import java.util.List;

public class ConfDevicePageBo {
	private long startRow;
	private long rowCount;
	private long totalRecord;
	private List<ConfDeviceBo> confDeviceBos;

	public long getTotalRecord() {
		return totalRecord;
	}

	public void setTotalRecord(long totalRecord) {
		this.totalRecord = totalRecord;
	}

	public List<ConfDeviceBo> getConfDeviceBos() {
		return confDeviceBos;
	}

	public void setConfDeviceBos(List<ConfDeviceBo> confDeviceBos) {
		this.confDeviceBos = confDeviceBos;
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
