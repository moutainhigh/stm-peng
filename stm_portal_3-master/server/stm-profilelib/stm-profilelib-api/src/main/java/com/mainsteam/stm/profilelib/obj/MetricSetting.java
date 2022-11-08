package com.mainsteam.stm.profilelib.obj;

import java.io.Serializable;
import java.util.List;

/**
 * 策略指标设置
 * @author xiaoruqiang
 */
public class MetricSetting implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5655370302799261106L;
	/**
	 * 监控的指标
	 */
	private List<ProfileMetric> metrics;
	
	
	public MetricSetting(){
	}
	
	public MetricSetting(List<ProfileMetric> metrics){
		this.metrics = metrics;
	}
	
	public void setMetrics(List<ProfileMetric> metrics) {
		this.metrics = metrics;
	}

	public List<ProfileMetric> getMetrics() {
		return metrics;
	}

}
