package com.mainsteam.stm.system.um.domain.web.vo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mainsteam.stm.platform.web.vo.BasePageVo;
import com.mainsteam.stm.system.um.domain.bo.Domain;

public class DomainPageVo implements BasePageVo{
	
	private static final long serialVersionUID = 1L;
	private long startRow;
	private long rowCount;
	private long totalRecord;
	private Domain condition;
	private List<Domain> domain;
	
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
	
	
	
	public Domain getCondition() {
		return condition;
	}
	public void setCondition(Domain condition) {
		this.condition = condition;
	}
	public List<Domain> getDomain() {
		return domain;
	}
	public void setDomain(List<Domain> domain) {
		this.domain = domain;
	}
	@Override
	public long getTotal() {
		// TODO Auto-generated method stub
		return this.totalRecord;
	}
	@Override
	public Collection<? extends Object> getRows() {
		if(domain!=null){
			return this.domain;
		}
		return new ArrayList<Domain>();
	}
	
	
	
	
}
