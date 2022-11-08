package com.mainsteam.stm.portal.statist.bo;

import java.io.Serializable;
import java.util.List;

public class ChartLineSeriesBo implements Serializable{
	private static final long serialVersionUID = -6684762121386727318L;
	private String name;
	private List<String> data;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getData() {
		return data;
	}
	public void setData(List<String> data) {
		this.data = data;
	}
	
}
