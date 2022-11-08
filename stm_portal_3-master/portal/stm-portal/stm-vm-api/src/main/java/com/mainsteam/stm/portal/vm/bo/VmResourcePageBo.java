package com.mainsteam.stm.portal.vm.bo;

import java.util.List;

import com.mainsteam.stm.portal.resource.bo.ResourceCategoryBo;

public class VmResourcePageBo {

	private long startRow;
	private long rowCount;
	private long totalRecord;
	private String sort;
	private String order;
	private VmResourceBo condition;
	private List<VmResourceBo> vmResources;
	private List<ResourceCategoryBo> ResourceCategoryBos;

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

	public VmResourceBo getCondition() {
		return condition;
	}

	public void setCondition(VmResourceBo condition) {
		this.condition = condition;
	}

	public List<VmResourceBo> getVmResources() {
		return vmResources;
	}

	public void setVmResources(List<VmResourceBo> vmResources) {
		this.vmResources = vmResources;
	}

	public List<ResourceCategoryBo> getResourceCategoryBos() {
		return ResourceCategoryBos;
	}

	public void setResourceCategoryBos(List<ResourceCategoryBo> resourceCategoryBos) {
		ResourceCategoryBos = resourceCategoryBos;
	}

	public void setStartRow(long startRow) {
		this.startRow = startRow;
	}

	public long getStartRow() {
		return startRow;
	}

	public void setRowCount(long rowCount) {
		this.rowCount = rowCount;
	}

	public long getRowCount() {
		return rowCount;
	}

}
