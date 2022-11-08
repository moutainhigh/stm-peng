package com.mainsteam.stm.portal.report.bo;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("chapter")
public class Chapter {
	
	//章节名称
	@XStreamAsAttribute
	private String name;
	
	//章节类型
	@XStreamAsAttribute
	private String type;
	
	//顺序(1.表格在上2.图表在上)
	@XStreamAsAttribute
	private String sort="1";
	
	@XStreamImplicit(itemFieldName="table")
	private List<Table> table;
	
	@XStreamImplicit(itemFieldName="chart")
	private List<Chart> chart;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public List<Table> getTable() {
		return table;
	}

	public void setTable(List<Table> table) {
		this.table = table;
	}

	public List<Chart> getChart() {
		return chart;
	}

	public void setChart(List<Chart> chart) {
		this.chart = chart;
	}
	
	
}
