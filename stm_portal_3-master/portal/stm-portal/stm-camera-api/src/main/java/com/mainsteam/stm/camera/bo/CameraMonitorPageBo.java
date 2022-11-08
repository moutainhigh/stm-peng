package com.mainsteam.stm.camera.bo;

import java.util.List;

import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.portal.resource.bo.ResourceCategoryBo;

public class CameraMonitorPageBo {
	
	 private long startRow;
	 private long rowCount;
	   private long totalRecord;
	    private List<CaremaMonitorBo> resourceMonitorBosExtends;
	 private List<ResourceInstance> resourceMonitorBos;
	  private List<ResourceCategoryBo> ResourceCategoryBos;
	  
	  
	  private long offlineNumber;//离线数
	  
	  private long abnormalNumber;//故障数
	  
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
	public List<CaremaMonitorBo> getResourceMonitorBosExtends() {
		return resourceMonitorBosExtends;
	}
	public void setResourceMonitorBosExtends(List<CaremaMonitorBo> resourceMonitorBosExtends) {
		this.resourceMonitorBosExtends = resourceMonitorBosExtends;
	}
	public List<ResourceInstance> getResourceMonitorBos() {
		return resourceMonitorBos;
	}
	public void setResourceMonitorBos(List<ResourceInstance> resourceMonitorBos) {
		this.resourceMonitorBos = resourceMonitorBos;
	}
	public List<ResourceCategoryBo> getResourceCategoryBos() {
		return ResourceCategoryBos;
	}
	public void setResourceCategoryBos(List<ResourceCategoryBo> resourceCategoryBos) {
		ResourceCategoryBos = resourceCategoryBos;
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
