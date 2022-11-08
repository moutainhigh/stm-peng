package com.mainsteam.stm.portal.statist.bo;

import java.io.Serializable;

public class ChartPieSeriesBo implements Serializable {
	private static final long serialVersionUID = -4534937104598356744L;
	private String name;
	private Double y;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getY() {
		return y;
	}

	public void setY(Double y) {
		this.y = y;
	}

}
