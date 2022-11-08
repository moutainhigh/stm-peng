package com.mainsteam.stm.portal.netflow.bo;

import java.util.List;

public class ApplicationChartDataBo {
	private List<String> timepoints;
	private String sortColum;
	private List<ApplicationChartDataModel> applicationChartDataModel;

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

	public List<ApplicationChartDataModel> getApplicationChartDataModel() {
		return applicationChartDataModel;
	}

	public void setApplicationChartDataModel(
			List<ApplicationChartDataModel> applicationChartDataModel) {
		this.applicationChartDataModel = applicationChartDataModel;
	}

}
