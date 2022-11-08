package com.mainsteam.stm.portal.resource.web.vo;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.mainsteam.stm.platform.web.vo.BasePageVo;


public class MainResourceStrategyPageVo implements Serializable,BasePageVo{
	
	private static final long serialVersionUID = 3122370356227155165L;
	private long startRow;
	private long rowCount;
	private long totalRecord;
	private MainResourceStrategyVo condition;
	private List<MainResourceStrategyVo> resources;
	private Set<Long> domainIds;
	private List<Long> instanceIds;
	public Set<Long> getDomainIds() {
		return domainIds;
	}
	public void setDomainIds(Set<Long> domainIds) {
		this.domainIds = domainIds;
	}
	public List<Long> getInstanceIds() {
		return instanceIds;
	}
	public void setInstanceIds(List<Long> instanceIds) {
		this.instanceIds = instanceIds;
	}
	//策略是否监控了该资源类型下的所有设备
	private boolean isSelectAll;
	public long getStartRow() {
		return startRow;
	}
	public void setStartRow(long startRow) {
		this.startRow = startRow;
	}
	public long getRowCount() {
		return rowCount;
	}
	public boolean isSelectAll() {
		return isSelectAll;
	}
	public void setSelectAll(boolean isSelectAll) {
		this.isSelectAll = isSelectAll;
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
	public MainResourceStrategyVo getCondition() {
		return condition;
	}
	public void setCondition(MainResourceStrategyVo condition) {
		this.condition = condition;
	}
//	public List<MainResourceStrategyVo> getResources() {
//		return resources;
//	}
	public void setResources(List<MainResourceStrategyVo> resources) {
		this.resources = resources;
	}
	@Override
	public long getTotal() {
		return this.totalRecord;
	}
	@Override
	public Collection<? extends Object> getRows() {
		return this.resources;
	}
	
	

}
