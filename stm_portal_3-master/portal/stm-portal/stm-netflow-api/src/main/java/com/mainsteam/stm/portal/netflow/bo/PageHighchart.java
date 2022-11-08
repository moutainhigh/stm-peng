package com.mainsteam.stm.portal.netflow.bo;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

public class PageHighchart<Po, Qo> extends Page<Po, Qo> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<HighchartsData> highchartsDatas;

	private List<String> categories;

	public List<HighchartsData> getHighchartsDatas() {
		return highchartsDatas;
	}

	public void setHighchartsDatas(List<HighchartsData> highchartsDatas) {
		this.highchartsDatas = highchartsDatas;
	}

	public List<String> getCategories() {
		return categories;
	}

	public void setCategories(List<String> categories) {
		this.categories = categories;
	}

}
