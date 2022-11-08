/**
 * 
 */
package com.mainsteam.stm.state.chain.step;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.metric.obj.CustomMetric;
import com.mainsteam.stm.metric.obj.CustomMetricInfo;
import com.mainsteam.stm.state.chain.StateChainStep;
import com.mainsteam.stm.state.chain.StateChainStepContext;
import com.mainsteam.stm.state.chain.StateComputeChain;

/**
 * 验证指标类型必须为性能指标。
 * 
 * @author 作者：ziw
 * @date 创建时间：2016年11月15日 上午10:49:01
 * @version 1.0
 */
public class PerformenceMetricTypeValidateStep implements StateChainStep {

	/**
	 * 
	 */
	public PerformenceMetricTypeValidateStep() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * .oc.dataprocess.MetricCalculateData,
	 * com.mainsteam.stm.caplib.resource.ResourceMetricDef,
	 * com.mainsteam.stm.metric.obj.CustomMetric, java.util.Map)
	 */
	@Override
	public void doStepChain(StateChainStepContext context,
			StateComputeChain chain) {
		if (context.getCustomMetric() == null) {
			if (context.getResourceMetricDef().getMetricType() != MetricTypeEnum.PerformanceMetric) {
				return;
			}
		} else if (context.getCustomMetric().getCustomMetricInfo() == null
				|| !context.getCustomMetric().getCustomMetricInfo().isAlert()
				|| MetricTypeEnum.PerformanceMetric != context.getCustomMetric().getCustomMetricInfo().getStyle()) {
			return;
		}
		ResourceMetricDef resourceMetricDef = context.getResourceMetricDef();
		CustomMetric customMetric = context.getCustomMetric();
		if(null == resourceMetricDef && null != customMetric) {
			resourceMetricDef = new ResourceMetricDef();
			CustomMetricInfo customMetricInfo = customMetric.getCustomMetricInfo();
			resourceMetricDef.setAlert(customMetricInfo.isAlert());
			resourceMetricDef.setDefaultFlapping(customMetricInfo.getFlapping());
			resourceMetricDef.setId(customMetricInfo.getId());
			resourceMetricDef.setMonitor(customMetricInfo.isMonitor());
			resourceMetricDef.setMetricType(MetricTypeEnum.PerformanceMetric);
			resourceMetricDef.setResourceDef(null);
			context.setResourceMetricDef(resourceMetricDef);
		}
		chain.doChain();
	}
}
