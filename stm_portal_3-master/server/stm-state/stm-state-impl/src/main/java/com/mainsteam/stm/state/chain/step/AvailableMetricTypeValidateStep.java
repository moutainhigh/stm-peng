package com.mainsteam.stm.state.chain.step;

import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
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
public class AvailableMetricTypeValidateStep implements StateChainStep {

	/**
	 * 
	 */
	public AvailableMetricTypeValidateStep() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.state.chain.StateChainStep#doStepChain(com.mainsteam
	 * .oc.dataprocess.MetricCalculateData,
	 * com.mainsteam.stm.caplib.resource.ResourceMetricDef,
	 * com.mainsteam.stm.metric.obj.CustomMetric, java.util.Map)
	 */
	@Override
	public void doStepChain(StateChainStepContext context, StateComputeChain chain) {
		CustomMetric customMetric = context.getCustomMetric();
		if (customMetric == null
				&& context.getResourceMetricDef().getMetricType() != MetricTypeEnum.AvailabilityMetric) {
			return;
		}

		if (customMetric != null && (customMetric.getCustomMetricInfo() == null
						|| !customMetric.getCustomMetricInfo().isAlert() ||
				customMetric.getCustomMetricInfo().getStyle() != MetricTypeEnum.AvailabilityMetric)) {
			return;
		}

		if (context.getMetricData().getResourceInstanceId() <= 0
				|| (context.getMetricData().getProfileId() <= 0 &&
				context.getMetricData().getTimelineId() <= 0)) {
			return;
		}
		context.getContextData().put(MetricTypeEnum.AvailabilityMetric.name(), Boolean.TRUE);
		ResourceMetricDef resourceMetricDef = context.getResourceMetricDef();
		if(null == resourceMetricDef && null != customMetric) {
			resourceMetricDef = new ResourceMetricDef();
			CustomMetricInfo customMetricInfo = customMetric.getCustomMetricInfo();
			resourceMetricDef.setAlert(customMetricInfo.isAlert());
			resourceMetricDef.setDefaultFlapping(customMetricInfo.getFlapping());
			resourceMetricDef.setId(customMetricInfo.getId());
			resourceMetricDef.setMonitor(customMetricInfo.isMonitor());
			resourceMetricDef.setMetricType(MetricTypeEnum.AvailabilityMetric);
			resourceMetricDef.setResourceDef(null);
			context.setResourceMetricDef(resourceMetricDef);
		}
		chain.doChain();
	}
}
