package com.mainsteam.stm.portal.netflow.bo;

import java.util.List;

public class DeviceNetFlowChartDataBo {
	private List<String> timepoints;
	private String sortColum;
	private List<DeviceNetFlowChartDataModel> DeviceNetFlowChartDataModel;

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

	public List<DeviceNetFlowChartDataModel> getDeviceNetFlowChartDataModel() {
		return DeviceNetFlowChartDataModel;
	}

	public void setDeviceNetFlowChartDataModel(
			List<DeviceNetFlowChartDataModel> deviceNetFlowChartDataModel) {
		DeviceNetFlowChartDataModel = deviceNetFlowChartDataModel;
	}


	

	

}
