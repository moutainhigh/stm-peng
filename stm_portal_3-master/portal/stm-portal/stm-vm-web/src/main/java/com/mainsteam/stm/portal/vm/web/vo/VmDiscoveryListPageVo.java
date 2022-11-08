package com.mainsteam.stm.portal.vm.web.vo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mainsteam.stm.platform.web.vo.BasePageVo;

public class VmDiscoveryListPageVo implements BasePageVo {
	private static final long serialVersionUID = 3073397846179442353L;
	private long startRow;
	private long rowCount;
	private long totalRecord;
	private String sort;
	private String order;
	private VmDiscoveryListVo condition;
	private List<VmDiscoveryListVo> VmDiscoveryList;

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

	public VmDiscoveryListVo getCondition() {
		return condition;
	}

	public void setCondition(VmDiscoveryListVo condition) {
		this.condition = condition;
	}

	public List<VmDiscoveryListVo> getVmDiscoveryList() {
		return VmDiscoveryList;
	}

	public void setVmDiscoveryList(List<VmDiscoveryListVo> vmDiscoveryList) {
		VmDiscoveryList = vmDiscoveryList;
	}

	public long getStartRow() {
		return startRow;
	}

	public long getRowCount() {
		return rowCount;
	}

	@Override
	public Collection<? extends Object> getRows() {
		if (VmDiscoveryList != null) {
			return this.VmDiscoveryList;
		}
		return new ArrayList<VmDiscoveryListVo>();
	}

	@Override
	public long getTotal() {
		return this.totalRecord;
	}

	@Override
	public void setRowCount(long rowCount) {
		this.rowCount = rowCount;

	}

	@Override
	public void setStartRow(long startRow) {
		this.startRow = startRow;
	}

}
