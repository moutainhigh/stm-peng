package com.mainsteam.stm.portal.resource.web.vo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mainsteam.stm.platform.web.vo.BasePageVo;
import com.mainsteam.stm.portal.resource.bo.ProfileMetricBo;
import com.mainsteam.stm.profilelib.obj.ProfileMetric;

public class ProfileMetricPageVo extends ProfileMetricBo implements BasePageVo{

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;

	private long startRow;
	private long rowCount;
	private long totalRecord;
	
	private ProfileMetricBo condition;
	List<ProfileMetricBo> metrics;
	
	public long getTotalRecord() {
		return totalRecord;
	}

	public void setTotalRecord(long totalRecord) {
		this.totalRecord = totalRecord;
	}

	public ProfileMetric getCondition() {
		return condition;
	}

	public void setCondition(ProfileMetricBo condition) {
		this.condition = condition;
	}


	public List<ProfileMetricBo> getMetrics() {
		return metrics;
	}

	public void setMetrics(List<ProfileMetricBo> metrics) {
		this.metrics = metrics;
	}

	public long getStartRow() {
		return startRow;
	}

	public long getRowCount() {
		return rowCount;
	}

	@Override
	public void setStartRow(long startRow) {
		this.startRow = startRow;
		
	}

	@Override
	public void setRowCount(long rowCount) {
		this.rowCount = rowCount;
		
	}

	@Override
	public long getTotal() {
		
		return this.totalRecord;
	}

	@Override
	public Collection<? extends Object> getRows() {
		if(metrics != null){
			return this.metrics;
		}
		return new ArrayList<StrategyVo>();
	}
	
}
