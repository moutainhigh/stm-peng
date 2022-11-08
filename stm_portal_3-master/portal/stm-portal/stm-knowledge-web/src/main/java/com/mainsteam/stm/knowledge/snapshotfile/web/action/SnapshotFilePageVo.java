package com.mainsteam.stm.knowledge.snapshotfile.web.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mainsteam.stm.knowledge.scriptmanage.web.action.ScriptManageVo;
import com.mainsteam.stm.platform.web.vo.BasePageVo;

public class SnapshotFilePageVo implements BasePageVo{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3349764489296178191L;
	private long startRow;
	private long rowCount;
	private long totalRecord;
	private String sort;
	private String order;
	private SnapshotFileVo condition;
	private List<SnapshotFileVo> snapshotFiles;
	
	
	
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
	public SnapshotFileVo getCondition() {
		return condition;
	}
	public void setCondition(SnapshotFileVo condition) {
		this.condition = condition;
	}
	public List<SnapshotFileVo> getSnapshotFiles() {
		return snapshotFiles;
	}
	public void setSnapshotFiles(List<SnapshotFileVo> snapshotFiles) {
		this.snapshotFiles = snapshotFiles;
	}
	@Override
	public long getTotal() {
		return this.totalRecord;
	}
	@Override
	public Collection<? extends Object> getRows() {
		if(snapshotFiles!=null){
			return this.snapshotFiles;
		}
		return new ArrayList<SnapshotFileVo>();
	}
}
