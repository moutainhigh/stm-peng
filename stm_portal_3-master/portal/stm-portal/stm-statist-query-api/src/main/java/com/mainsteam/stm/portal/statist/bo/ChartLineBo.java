package com.mainsteam.stm.portal.statist.bo;

import java.io.Serializable;
import java.util.List;

public class ChartLineBo extends ChartBaseBo implements Serializable{
	private static final long serialVersionUID = -4228924195925014296L;
	private String type = "Line";
	private String title;
	private List<String> categories;
	private List<ChartLineSeriesBo> series;
	
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
	public List<String> getCategories() {
		return categories;
	}
	public void setCategories(List<String> categories) {
		this.categories = categories;
	}
	public List<ChartLineSeriesBo> getSeries() {
		return series;
	}
	public void setSeries(List<ChartLineSeriesBo> series) {
		this.series = series;
	}
	
}
