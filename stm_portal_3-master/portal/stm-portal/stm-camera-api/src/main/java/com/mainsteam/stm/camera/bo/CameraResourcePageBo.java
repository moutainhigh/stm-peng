package com.mainsteam.stm.camera.bo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mainsteam.stm.platform.web.vo.BasePageVo;

public class CameraResourcePageBo implements BasePageVo {

	private static final long serialVersionUID = -783443827467986667L;

	private long startRow;
	private long rowCount;
	private long totalRecord;
	private CameraResourceBo condition;
	private List<CameraResourceBo> resourceMonitors;
	private String sort;
	private String order;

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

	public CameraResourceBo getCondition() {
		return condition;
	}

	public void setCondition(CameraResourceBo condition) {
		this.condition = condition;
	}

	public List<CameraResourceBo> getResourceMonitors() {
		return resourceMonitors;
	}

	public void setResourceMonitors(List<CameraResourceBo> resourceMonitors) {
		this.resourceMonitors = resourceMonitors;
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

	@Override
	public long getTotal() {
		return this.totalRecord;
	}

	@Override
	public Collection<? extends Object> getRows() {
		if (resourceMonitors != null) {
			return this.resourceMonitors;
		}
		return new ArrayList<CameraResourceBo>();
	}

}
