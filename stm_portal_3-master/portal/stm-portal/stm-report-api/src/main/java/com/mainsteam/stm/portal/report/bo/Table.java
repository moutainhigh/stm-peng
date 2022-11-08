package com.mainsteam.stm.portal.report.bo;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("table")
public class Table {
	
	//表格名称
	@XStreamAsAttribute
	private String name;
	
//	//是否含有图表
//	@XStreamAsAttribute
//	private String isHaveChart;
	
	
	private ColumnsTitle columnsTitle;
	
	private ColumnsData columnsData;
	


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public ColumnsTitle getColumnsTitle() {
		return columnsTitle;
	}

	public void setColumnsTitle(ColumnsTitle columnsTitle) {
		this.columnsTitle = columnsTitle;
	}

	public ColumnsData getColumnsData() {
		return columnsData;
	}

	public void setColumnsData(ColumnsData columnsData) {
		this.columnsData = columnsData;
	}

	
}
