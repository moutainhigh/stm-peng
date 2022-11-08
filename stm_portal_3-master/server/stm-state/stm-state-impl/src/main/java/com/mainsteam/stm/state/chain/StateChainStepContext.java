package com.mainsteam.stm.state.chain;

import java.util.HashMap;
import java.util.Map;

import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.dataprocess.MetricCalculateData;
import com.mainsteam.stm.metric.obj.CustomMetric;

/**
 * @author 作者：ziw
 * @date 创建时间：2016年11月15日 上午10:50:18
 * @version 1.0
 */
public class StateChainStepContext {

	private MetricCalculateData metricData;
	private ResourceMetricDef resourceMetricDef;
	private CustomMetric customMetric;
	private Map<String, Object> contextData;

	public StateChainStepContext() {
	}

	public StateChainStepContext(MetricCalculateData metricData,
			ResourceMetricDef resourceMetricDef, CustomMetric customMetric,
			Map<String, Object> contextData) {
		super();
		this.metricData = metricData;
		this.resourceMetricDef = resourceMetricDef;
		this.customMetric = customMetric;
		this.contextData = contextData;
		if (this.contextData == null) {
			this.contextData = new HashMap<String, Object>();
		}
	}

	public MetricCalculateData getMetricData() {
		return metricData;
	}

	public void setMetricData(MetricCalculateData metricData) {
		this.metricData = metricData;
	}

	public ResourceMetricDef getResourceMetricDef() {
		return resourceMetricDef;
	}

	public void setResourceMetricDef(ResourceMetricDef resourceMetricDef) {
		this.resourceMetricDef = resourceMetricDef;
	}

	public CustomMetric getCustomMetric() {
		return customMetric;
	}

	public void setCustomMetric(CustomMetric customMetric) {
		this.customMetric = customMetric;
	}

	public Map<String, Object> getContextData() {
		return contextData;
	}

	public void setContextData(Map<String, Object> contextData) {
		this.contextData = contextData;
	}

}
