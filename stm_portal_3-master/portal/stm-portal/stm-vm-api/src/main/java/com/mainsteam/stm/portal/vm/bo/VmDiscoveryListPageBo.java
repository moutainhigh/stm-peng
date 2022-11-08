package com.mainsteam.stm.portal.vm.bo;

import java.util.List;

public class VmDiscoveryListPageBo {
	private long startRow;
	private long rowCount;
	private long totalRecord;
	private String sort;
	private String order;
	private VmDiscoveryListBo condition;
	private List<VmDiscoveryListBo> VmDiscoveryList;

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

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public VmDiscoveryListBo getCondition() {
		return condition;
	}

	public void setCondition(VmDiscoveryListBo condition) {
		this.condition = condition;
	}

	public List<VmDiscoveryListBo> getVmDiscoveryList() {
		return VmDiscoveryList;
	}

	public void setVmDiscoveryList(List<VmDiscoveryListBo> vmDiscoveryList) {
		VmDiscoveryList = vmDiscoveryList;
	}

}
