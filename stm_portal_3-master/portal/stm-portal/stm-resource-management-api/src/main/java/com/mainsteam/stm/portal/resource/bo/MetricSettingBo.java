package com.mainsteam.stm.portal.resource.bo;

import java.io.Serializable;
import java.util.List;


public class MetricSettingBo implements Serializable {
	
	private static final long serialVersionUID = 4835106561320587335L;
	private List<ProfileMetricBo> metrics;

	public List<ProfileMetricBo> getMetrics() {
		return metrics;
	}

	public void setMetrics(List<ProfileMetricBo> metrics) {
		this.metrics = metrics;
	}


	  
	  
	
}
