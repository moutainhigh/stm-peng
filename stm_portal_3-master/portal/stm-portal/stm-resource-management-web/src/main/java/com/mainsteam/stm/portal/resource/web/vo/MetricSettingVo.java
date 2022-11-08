package com.mainsteam.stm.portal.resource.web.vo;

import java.io.Serializable;
import java.util.List;


public class MetricSettingVo implements Serializable {
	
	private static final long serialVersionUID = -504772938661456926L;
	private List<ProfileMetricVo> metrics;

	public List<ProfileMetricVo> getMetrics() {
		return metrics;
	}

	public void setMetrics(List<ProfileMetricVo> metrics) {
		this.metrics = metrics;
	}


	  
	  
	
}
