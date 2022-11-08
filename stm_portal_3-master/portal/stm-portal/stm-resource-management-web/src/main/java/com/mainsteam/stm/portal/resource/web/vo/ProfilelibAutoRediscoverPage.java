package com.mainsteam.stm.portal.resource.web.vo;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.mainsteam.stm.platform.web.vo.BasePageVo;
import com.mainsteam.stm.profilelib.obj.ProfilelibAutoRediscover;


public class ProfilelibAutoRediscoverPage implements Serializable,BasePageVo{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3151845616870866977L;
	private long startRow;
	private long rowCount;
	private long totalRecord;
	private ProfilelibAutoRediscoverVo condition;
	private List<ProfilelibAutoRediscoverVo> resources;

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
	public ProfilelibAutoRediscoverVo getCondition() {
		return condition;
	}
	public void setCondition(ProfilelibAutoRediscoverVo condition) {
		this.condition = condition;
	}
//	public List<ProfilelibAutoRediscoverVo> getResources() {
//		return resources;
//	}
	public void setResources(List<ProfilelibAutoRediscoverVo> resources) {
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
