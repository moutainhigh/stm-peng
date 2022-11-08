package com.mainsteam.stm.portal.statist.bo;

import java.io.Serializable;

public class ChartTitileBo extends ChartBaseBo implements Serializable {
	private static final long serialVersionUID = -1261807929671576192L;
	private String type = "Title";
	private String title;

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

}
