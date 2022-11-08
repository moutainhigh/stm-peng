package com.mainsteam.stm.portal.resource.bo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mainsteam.stm.profilelib.fault.obj.Profilefault;

public class SnapshotProfilePageBo implements Serializable {
	
	private static final long serialVersionUID = -4202911946627015269L;

	private long startRow;
	
	private long rowCount;
	
	private long totalRecord;
	
	private String sort;
	
	private String order;
	
	private List<Profilefault> pffList;

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


	public List<Profilefault> getPffList() {
		return pffList;
	}

	public void setPffList(List<Profilefault> pffList) {
		this.pffList = pffList;
	}

	public long getStartRow() {
		return startRow;
	}

	public long getRowCount() {
		return rowCount;
	}

	public void setStartRow(long startRow) {
		this.startRow = startRow;
	}

	public void setRowCount(long rowCount) {
		this.rowCount = rowCount;
	}

	public long getTotal() {
		return this.totalRecord;
	}

	public Collection<? extends Object> getRows() {
		if(pffList != null){
			return this.pffList;
		}
		return new ArrayList<Profilefault>();
	}

}
