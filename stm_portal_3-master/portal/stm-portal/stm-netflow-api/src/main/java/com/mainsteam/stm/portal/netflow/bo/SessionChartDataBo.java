package com.mainsteam.stm.portal.netflow.bo;

import java.util.List;

public class SessionChartDataBo {
	private List<String> timepoints;
	private String sortColum;
	private List<SessionChartDataModel> sessionChartDataModel;

	public String getSortColum() {
		return sortColum;
	}

	public void setSortColum(String sortColum) {
		this.sortColum = sortColum;
	}

	public List<String> getTimepoints() {
		return timepoints;
	}

	public void setTimepoints(List<String> timepoints) {
		this.timepoints = timepoints;
	}

	public List<SessionChartDataModel> getSessionChartDataModel() {
		return sessionChartDataModel;
	}

	public void setSessionChartDataModel(
			List<SessionChartDataModel> sessionChartDataModel) {
		this.sessionChartDataModel = sessionChartDataModel;
	}



}
