package com.mainsteam.stm.portal.statist.bo;

import java.io.Serializable;
import java.util.List;

public class ChartPieBo extends ChartBaseBo implements Serializable {
	private static final long serialVersionUID = 6108485582376618520L;
	private String type = "Pie";
	private String title;
	private List<ChartPieSeriesBo> series;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<ChartPieSeriesBo> getSeries() {
		return series;
	}
	public void setSeries(List<ChartPieSeriesBo> series) {
		this.series = series;
	}
	
}
