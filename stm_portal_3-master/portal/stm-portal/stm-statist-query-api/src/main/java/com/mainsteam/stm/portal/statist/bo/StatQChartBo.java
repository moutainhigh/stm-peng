package com.mainsteam.stm.portal.statist.bo;

import java.io.Serializable;
import java.util.List;

public class StatQChartBo implements Serializable {
	private static final long serialVersionUID = 3504444775570959549L;
	private String name;
	private String startTime;
	private String endTime;
	private List<ChartBaseBo> chartBoList;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public List<ChartBaseBo> getChartBoList() {
		return chartBoList;
	}

	public void setChartBoList(List<ChartBaseBo> chartBoList) {
		this.chartBoList = chartBoList;
	}

}
