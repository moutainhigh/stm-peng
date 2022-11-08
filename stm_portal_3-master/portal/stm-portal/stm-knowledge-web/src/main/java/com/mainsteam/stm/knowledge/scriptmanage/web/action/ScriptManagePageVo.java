package com.mainsteam.stm.knowledge.scriptmanage.web.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mainsteam.stm.platform.web.vo.BasePageVo;


public class ScriptManagePageVo implements BasePageVo{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6280922410983661164L;
	private long startRow;
	private long rowCount;
	private long totalRecord;
	private String sort;
	private String order;
	private ScriptManageVo condition;
	private List<ScriptManageVo> scripts;
	private int scriptTypeCode;
	
	
	public int getScriptCode() {
		return scriptTypeCode;
	}
	public void setScriptCode(int scriptTypeCode) {
		this.scriptTypeCode = scriptTypeCode;
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
	public ScriptManageVo getCondition() {
		return condition;
	}
	public void setCondition(ScriptManageVo condition) {
		this.condition = condition;
	}
	public List<ScriptManageVo> getScripts() {
		return scripts;
	}
	public void setScripts(List<ScriptManageVo> scripts) {
		this.scripts = scripts;
	}
	@Override
	public long getTotal() {
		return this.totalRecord;
	}
	@Override
	public Collection<? extends Object> getRows() {
		if(scripts!=null){
			return this.scripts;
		}
		return new ArrayList<ScriptManageVo>();
	}
}
