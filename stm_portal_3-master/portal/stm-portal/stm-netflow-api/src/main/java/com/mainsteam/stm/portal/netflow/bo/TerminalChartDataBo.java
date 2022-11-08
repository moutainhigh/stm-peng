package com.mainsteam.stm.portal.netflow.bo;

import java.util.List;

public class TerminalChartDataBo {
	private List<String> timepoints;
	private String sortColum;
	private List<TerminalChartDataModel> terminalChartDataModel;

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

	public List<TerminalChartDataModel> getTerminalChartDataModel() {
		return terminalChartDataModel;
	}

	public void setTerminalChartDataModel(
			List<TerminalChartDataModel> terminalChartDataModel) {
		this.terminalChartDataModel = terminalChartDataModel;
	}



}
