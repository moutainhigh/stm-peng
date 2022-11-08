package com.mainsteam.stm.camera.bo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mainsteam.stm.platform.web.vo.BasePageVo;

public class CameraDcListPageVo implements BasePageVo {

	private static final long serialVersionUID = -2227461346282884404L;

	private long startRow;
	private long rowCount;
	private long totalRecord;
	private String sort;
	private String order;
	private CameraDcListVo condition;
	private List<CameraDcListVo> CameraDcList;

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

	public CameraDcListVo getCondition() {
		return condition;
	}

	public void setCondition(CameraDcListVo condition) {
		this.condition = condition;
	}

	public List<CameraDcListVo> getCameraDcList() {
		return CameraDcList;
	}

	public void setCameraDcList(List<CameraDcListVo> cameraDcList) {
		CameraDcList = cameraDcList;
	}

	@Override
	public long getTotal() {
		return this.totalRecord;
	}

	@Override
	public Collection<? extends Object> getRows() {
		if (CameraDcList != null) {
			return this.CameraDcList;
		}
		return new ArrayList<CameraDcListVo>();
	}

}
