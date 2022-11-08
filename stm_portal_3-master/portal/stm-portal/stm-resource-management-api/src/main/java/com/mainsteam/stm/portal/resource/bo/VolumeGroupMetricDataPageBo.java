package com.mainsteam.stm.portal.resource.bo;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.mainsteam.stm.platform.web.vo.BasePageVo;

public class VolumeGroupMetricDataPageBo implements Serializable,BasePageVo{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1109250482675068202L;
	
	private long startRow;
	private long rowCount;
	private long totalRecord;
	private VolumeGroupMetricDataBo condition;
	private List<VolumeGroupMetricDataBo> volumeGroupData;
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
	public VolumeGroupMetricDataBo getCondition() {
		return condition;
	}
	public void setCondition(VolumeGroupMetricDataBo condition) {
		this.condition = condition;
	}
	public List<VolumeGroupMetricDataBo> getVolumeGroupData() {
		return volumeGroupData;
	}
	public void setVolumeGroupData(List<VolumeGroupMetricDataBo> volumeGroupData) {
		this.volumeGroupData = volumeGroupData;
	}

	@Override
	public long getTotal() {
		return this.totalRecord;
	}
	@Override
	public Collection<? extends Object> getRows() {
		return this.volumeGroupData;
	}
}
