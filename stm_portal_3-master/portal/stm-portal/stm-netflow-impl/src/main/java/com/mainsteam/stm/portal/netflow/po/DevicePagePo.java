package com.mainsteam.stm.portal.netflow.po;

import java.util.List;

public class DevicePagePo {
	private long startRow;
	private long rowCount;
	private long totalRecord;
	private List<DevicePo> devicePos;

	public long getTotalRecord() {
		return totalRecord;
	}

	public void setTotalRecord(long totalRecord) {
		this.totalRecord = totalRecord;
	}

	public List<DevicePo> getDevicePos() {
		return devicePos;
	}

	public void setDevicePos(List<DevicePo> devicePos) {
		this.devicePos = devicePos;
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
