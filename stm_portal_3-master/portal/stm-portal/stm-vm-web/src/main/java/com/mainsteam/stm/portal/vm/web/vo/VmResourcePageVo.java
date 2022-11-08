package com.mainsteam.stm.portal.vm.web.vo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mainsteam.stm.platform.web.vo.BasePageVo;
import com.mainsteam.stm.portal.resource.bo.ResourceCategoryBo;

public class VmResourcePageVo implements BasePageVo {

	private static final long serialVersionUID = 1L;
	private long startRow;
	private long rowCount;
	private long totalRecord;
	private String sort;
	private String order;
	private VmResourceVo condition;
	private List<VmResourceVo> vmResources;
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

	public VmResourceVo getCondition() {
		return condition;
	}

	public void setCondition(VmResourceVo condition) {
		this.condition = condition;
	}

	public List<VmResourceVo> getVmResources() {
		return vmResources;
	}

	public void setVmResources(List<VmResourceVo> vmResources) {
		this.vmResources = vmResources;
	}

	public long getStartRow() {
		return startRow;
	}

	public long getRowCount() {
		return rowCount;
	}

	public List<ResourceCategoryBo> getResourceCategoryBos() {
		return ResourceCategoryBos;
	}

	public void setResourceCategoryBos(List<ResourceCategoryBo> resourceCategoryBos) {
		ResourceCategoryBos = resourceCategoryBos;
	}

	@Override
	public Collection<? extends Object> getRows() {
		if (vmResources != null) {
			return this.vmResources;
		}
		return new ArrayList<VmResourceVo>();
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
