package com.mainsteam.stm.camera.web.vo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mainsteam.stm.platform.web.vo.BasePageVo;
import com.mainsteam.stm.portal.resource.bo.ResourceCategoryBo;
import com.mainsteam.stm.portal.resource.web.vo.ResourceMonitorVo;

public class CameraPageVo implements BasePageVo {
	
	private static final long serialVersionUID = 1L;
	private long startRow;
	private long rowCount;
	private long totalRecord;
	private CameraResourceMonitorVo condition;
	private List<CameraResourceMonitorVo> resourceMonitors;
	private List<ResourceCategoryBo> ResourceCategoryBos;
	private String sort;
	private String order;
	 private long offlineNumber;//离线数
	  
	  private long abnormalNumber;//故障数

	@Override
	public Collection<? extends Object> getRows() {
		if (this.resourceMonitors != null) {
			return this.resourceMonitors;
		}
		return new ArrayList();
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
	
	public long getStartRow() {
		return this.startRow;
	}

	public long getRowCount() {
		return this.rowCount;
	}

	public long getTotalRecord() {
		return totalRecord;
	}

	public void setTotalRecord(long totalRecord) {
		this.totalRecord = totalRecord;
	}


	public List<ResourceCategoryBo> getResourceCategoryBos() {
		return ResourceCategoryBos;
	}

	public void setResourceCategoryBos(List<ResourceCategoryBo> resourceCategoryBos) {
		ResourceCategoryBos = resourceCategoryBos;
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

	public CameraResourceMonitorVo getCondition() {
		return condition;
	}

	public void setCondition(CameraResourceMonitorVo condition) {
		this.condition = condition;
	}

	public List<CameraResourceMonitorVo> getResourceMonitors() {
		return resourceMonitors;
	}

	public void setResourceMonitors(List<CameraResourceMonitorVo> resourceMonitors) {
		this.resourceMonitors = resourceMonitors;
	}

	public long getOfflineNumber() {
		return offlineNumber;
	}

	public void setOfflineNumber(long offlineNumber) {
		this.offlineNumber = offlineNumber;
	}

	public long getAbnormalNumber() {
		return abnormalNumber;
	}

	public void setAbnormalNumber(long abnormalNumber) {
		this.abnormalNumber = abnormalNumber;
	}

	
	
	
	

}
