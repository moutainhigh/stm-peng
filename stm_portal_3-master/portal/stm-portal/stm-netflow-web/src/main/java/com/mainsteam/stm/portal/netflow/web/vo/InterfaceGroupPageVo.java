package com.mainsteam.stm.portal.netflow.web.vo;

import java.util.ArrayList;
import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.bo.InterfaceGroupBo;
import com.mainsteam.stm.portal.netflow.bo.InterfaceGroupPageBo;

public class InterfaceGroupPageVo {

	private long startRow;
	private long rowCount;
	private long totalRecord;
	private List<InterfaceGroupVo> interfaceGroupVos;

	public InterfaceGroupPageVo() {
	}

	public InterfaceGroupPageVo(InterfaceGroupPageBo pageBo) {
		this.setRowCount(pageBo.getRowCount());
		this.setStartRow(pageBo.getStartRow());
		this.setTotalRecord(pageBo.getTotalRecord());
		this.setInterfaceGroupVos(new ArrayList<InterfaceGroupVo>());
		for (InterfaceGroupBo bo : pageBo.getInterfaceGroupBos()) {
			this.getInterfaceGroupVos().add(new InterfaceGroupVo(bo));
		}
	}

	public Page<InterfaceGroupVo, InterfaceGroupVo> toPage() {
		Page<InterfaceGroupVo, InterfaceGroupVo> page = new Page<>();
		page.setStartRow(this.getStartRow());
		page.setRowCount(this.getRowCount());
		page.setTotalRecord(this.getTotalRecord());
		page.setDatas(this.getInterfaceGroupVos());
		return page;
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

	public List<InterfaceGroupVo> getInterfaceGroupVos() {
		return interfaceGroupVos;
	}

	public void setInterfaceGroupVos(List<InterfaceGroupVo> interfaceGroupVos) {
		this.interfaceGroupVos = interfaceGroupVos;
	}

}
