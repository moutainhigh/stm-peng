package com.mainsteam.stm.portal.netflow.bo;

public class DeviceGroupNetflowQueryBo {

	private long start;
	private long end;
	private String tableSuffix;

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public String getTableSuffix() {
		return tableSuffix;
	}

	public void setTableSuffix(String tableSuffix) {
		this.tableSuffix = tableSuffix;
	}

}
