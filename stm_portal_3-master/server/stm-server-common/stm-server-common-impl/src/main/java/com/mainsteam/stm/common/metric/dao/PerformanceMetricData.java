package com.mainsteam.stm.common.metric.dao;

import com.mainsteam.stm.common.metric.obj.MetricData;

public class PerformanceMetricData extends MetricData{
	private static final long serialVersionUID = 1L;

	private String metricdata;

	public String getMetricdata() {
		String[] metricdata=super.getData();
		return (metricdata!=null&&metricdata.length>0)?metricdata[0]:null;
	}

	public void setMetricdata(String metricdata) {
		super.setData(new String[]{metricdata});
	}

}
