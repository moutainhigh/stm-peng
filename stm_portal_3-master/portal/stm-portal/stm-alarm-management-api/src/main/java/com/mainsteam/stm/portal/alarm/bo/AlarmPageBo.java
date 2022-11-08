package com.mainsteam.stm.portal.alarm.bo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mainsteam.stm.alarm.obj.AlarmEvent;
import com.mainsteam.stm.platform.web.vo.BasePageVo;

public class AlarmPageBo implements BasePageVo{

	private static final long serialVersionUID = 1L;
	private long startRow;
	private long rowCount;
	private long totalRecord;
	private AlarmEvent condition;
	private List<AlarmEvent> rows;
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
	public AlarmEvent getCondition() {
		return condition;
	}
	public void setCondition(AlarmEvent condition) {
		this.condition = condition;
	}
	
	@Override
	public long getTotal() {
		
		return this.totalRecord;
	}
	@Override
	public Collection<? extends AlarmEvent> getRows() {
		if(rows!=null){
			return this.rows;
		}
		return new ArrayList<AlarmEvent>();
	}
	
	public void setRows(List<AlarmEvent> rows) {
		this.rows = rows;
	}

	
	
}
