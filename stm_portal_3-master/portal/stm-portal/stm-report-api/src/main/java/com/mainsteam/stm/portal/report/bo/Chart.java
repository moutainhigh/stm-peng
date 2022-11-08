package com.mainsteam.stm.portal.report.bo;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("chart")
public class Chart {
	
	//类型(1.堆叠柱状图2.折线图3.饼状图4.柱状图(分析报表内的柱状图))
	@XStreamAsAttribute
	private String type;
	
	//图表名称
	@XStreamAsAttribute
	private String name;
	
	//图表的一些辅助信息(折线图的x轴,饼状图的总数等)
	@XStreamAsAttribute
	private String info;
	
	@XStreamImplicit(itemFieldName="chartData")
	private List<ChartData> chartData;

	public List<ChartData> getChartData() {
		return chartData;
	}

	public void setChartData(List<ChartData> chartData) {
		this.chartData = chartData;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
	
	
	
}
