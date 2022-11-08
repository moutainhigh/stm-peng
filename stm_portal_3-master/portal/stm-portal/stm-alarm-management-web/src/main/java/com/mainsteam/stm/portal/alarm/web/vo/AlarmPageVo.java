package com.mainsteam.stm.portal.alarm.web.vo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mainsteam.stm.platform.web.vo.BasePageVo;

public class AlarmPageVo implements BasePageVo{
	
	private static final long serialVersionUID = 1L;
	private long startRow;
	private long rowCount;
	private long totalRecord;
	private String sort;
	private String order;
	private AlarmVo condition;
	private List<AlarmVo> alarms;
	
	
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
	public List<AlarmVo> getAlarms() {
		return alarms;
	}
	public void setAlarms(List<AlarmVo> alarms) {
		this.alarms = alarms;
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
	public AlarmVo getCondition() {
		return condition;
	}
	public void setCondition(AlarmVo condition) {
		this.condition = condition;
	}
	
	
	
	@Override
	public long getTotal() {
		return this.totalRecord;
	}
	@Override
	public Collection<? extends Object> getRows() {
		if(alarms!=null){
			return this.alarms;
		}
		return new ArrayList<AlarmVo>();
	}
	
	
}
