package com.mainsteam.stm.portal.netflow.po;

import java.util.List;

public class ConfDevicePagePo {
	private long startRow;
	private long rowCount;
	private long totalRecord;
	private List<ConfDevicePo> confDevicePos;

	public long getTotalRecord() {
		return totalRecord;
	}

	public void setTotalRecord(long totalRecord) {
		this.totalRecord = totalRecord;
	}

	public List<ConfDevicePo> getConfDevicePos() {
		return confDevicePos;
	}

	public void setConfDevicePos(List<ConfDevicePo> confDevicePos) {
		this.confDevicePos = confDevicePos;
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
