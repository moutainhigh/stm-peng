package com.mainsteam.stm.export.chart;

import java.util.List;

public class LineData {

	private String name;
	private List<Point> points;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Point> getPoints() {
		return points;
	}
	public void setPoints(List<Point> points) {
		this.points = points;
	}
	
}
