package com.mainsteam.stm.portal.resource.web.vo;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.mainsteam.stm.platform.web.vo.BasePageVo;

public class CustomMetricPageVo implements Serializable, BasePageVo {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -504066372894318514L;

	private long startRow;
	private long rowCount;
	private long totalRecord;
	private CustomMetricVo condition;
	private String placeHolder;
	private List<CustomMetricVo> customMetricData;

	public String getPlaceHolder() {
		return placeHolder;
	}

	public void setPlaceHolder(String placeHolder) {
		this.placeHolder = placeHolder;
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

	public long getTotalRecord() {
		return totalRecord;
	}

	public void setTotalRecord(long totalRecord) {
		this.totalRecord = totalRecord;
	}

	public CustomMetricVo getCondition() {
		return condition;
	}

	public void setCondition(CustomMetricVo condition) {
		this.condition = condition;
	}
	

	public List<CustomMetricVo> getCustomMetricData() {
		return customMetricData;
	}

	public void setCustomMetricData(List<CustomMetricVo> customMetricData) {
		this.customMetricData = customMetricData;
	}

	@Override
	public long getTotal() {
		return this.totalRecord;
	}

	@Override
	public Collection<? extends Object> getRows() {
		return this.customMetricData;
	}

}
