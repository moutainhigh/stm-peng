package com.mainsteam.stm.state.chain.step;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.metric.exception.CustomMetricException;
import com.mainsteam.stm.profilelib.ProfileService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.dataprocess.MetricCalculateData;
import com.mainsteam.stm.metric.obj.CustomMetric;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.ProfileMetric;
import com.mainsteam.stm.state.calculate.PerformanceStateCaluteResult;
import com.mainsteam.stm.state.chain.StateChainStep;
import com.mainsteam.stm.state.chain.StateChainStepContext;
import com.mainsteam.stm.state.chain.StateComputeChain;
import com.mainsteam.stm.state.obj.MetricStateData;
import com.mainsteam.stm.state.util.StateCaculatUtils;

import java.util.List;
import java.util.Map;

/**
 * @author 作者：ziw
 * @date 创建时间：2016年11月15日 下午5:21:00
 * @version 1.0
 */
public class MetricFlappingStep implements StateChainStep {

	private static final Log logger = LogFactory
			.getLog(MetricFlappingStep.class);

	private final StateCaculatUtils stateCaculatUtils;

	public MetricFlappingStep(StateCaculatUtils stateCaculatUtils) {
		super();
		this.stateCaculatUtils = stateCaculatUtils;
	}

	@Override
	public void doStepChain(StateChainStepContext context,
			StateComputeChain chain) {

		Object object = context.getContextData().get("preMetricStateData");
		MetricCalculateData metricData = context.getMetricData();

		CustomMetric customMetric = context.getCustomMetric();
		ProfileMetric profileMetric = null;
		Object profileObj = context.getContextData().get("profileMetric");
		if(null != profileObj)
			profileMetric = (ProfileMetric)profileObj;
		MetricStateEnum metricStateEnum = (MetricStateEnum) context.getContextData().get("metricState");
		MetricStateEnum preMetricStateEnum = null;
		Object preMetricStateObj = context.getContextData().get("preMetricState");
		if(null != preMetricStateObj)
			preMetricStateEnum = (MetricStateEnum)preMetricStateObj;

		boolean matchFlapping = false;
		int flappingValue = customMetric != null ? customMetric.getCustomMetricInfo().getFlapping() : profileMetric.getAlarmFlapping();
		try {

			matchFlapping = stateCaculatUtils.flapping(metricData.getResourceInstanceId(),
					customMetric == null ? metricData.getMetricId() : customMetric.getCustomMetricInfo().getId(),
					metricStateEnum, preMetricStateEnum, flappingValue);
			if(logger.isInfoEnabled()) {
				if(!matchFlapping && null != profileMetric) {
					logger.info("instance " + metricData.getResourceInstanceId() + " matches flapping, profile :" + profileMetric);
				}
			}

		} catch (ProfilelibException e) {
			if (logger.isErrorEnabled()) {
				logger.error("doStepChain", e);
			}
		}
		PerformanceStateCaluteResult performanceStateResult = (PerformanceStateCaluteResult) context.getContextData().get("performanceStateResult");
		MetricStateData metricStateData = null;
		/* 判断是否是性能指标 */
		if (null == performanceStateResult) {
			metricStateData = new MetricStateData();
			metricStateData.setInstanceID(metricData.getResourceInstanceId());
			metricStateData.setMetricID(metricData.getMetricId());
			metricStateData.setType(MetricTypeEnum.AvailabilityMetric);
			metricStateData.setState(metricStateEnum);
			context.getContextData().put(MetricTypeEnum.AvailabilityMetric.name(), Boolean.TRUE);
		} else {
			metricStateData = performanceStateResult.getMetricStateData();
			context.getContextData().put(MetricTypeEnum.AvailabilityMetric.name(), Boolean.FALSE);
		}
		metricStateData.setCollectTime(metricData.getCollectTime());
		if(Boolean.TRUE.equals(context.getContextData().get(MetricTypeEnum.AvailabilityMetric.name()))){
			if(!matchFlapping || null == object) {
				this.stateCaculatUtils.saveAvailMetricData(metricData.getResourceInstanceId(),metricData.getMetricId(),
						(String) context.getContextData().get("availMetricValue"));
			}

		}
		if (!matchFlapping || (matchFlapping && null == preMetricStateEnum) ) {
			if(logger.isInfoEnabled()) {
				if(null != preMetricStateEnum) {
					StringBuilder stringBuilder = new StringBuilder(100);
					stringBuilder.append("Metric state changes from ");
					stringBuilder.append(preMetricStateEnum);
					stringBuilder.append(" to ");
					stringBuilder.append(metricStateEnum);
					stringBuilder.append(", instance is ");
					stringBuilder.append(metricData.getResourceInstanceId());
					stringBuilder.append(",metric is ");
					stringBuilder.append(metricData.getMetricId());
					logger.info(stringBuilder.toString());
				}
			}
			this.stateCaculatUtils.saveMetricState(metricStateData);
			if(null == preMetricStateEnum && MetricTypeEnum.PerformanceMetric == metricStateData.getType()
					&& metricStateEnum == MetricStateEnum.NORMAL) { //性能指标首次状态为“正常”则不计算资源状态
				return;
			}
			context.getContextData().put("flapping", Boolean.TRUE);

		}

		if(!Boolean.TRUE.equals(context.getContextData().get("metricStateChange"))) {
			Map<String, String> map = this.stateCaculatUtils.getAvailMetricData(metricData.getResourceInstanceId());
			if(null == map || map.isEmpty()) {
				try {
					List<MetricStateData> metricStateDataList =  stateCaculatUtils.findMetricState(metricData.getResourceInstanceId(), metricData.getResourceId(),
							MetricTypeEnum.AvailabilityMetric, metricData.getTimelineId());
					if(null != metricStateDataList) {
						for(MetricStateData m : metricStateDataList) {
							this.stateCaculatUtils.saveAvailMetricData(metricData.getResourceInstanceId(), m.getMetricID(),
									MetricStateEnum.NORMAL == m.getState()?"1":"0");
						}
					}
				} catch (ProfilelibException e) {
					logger.error(e.getMessage(), e);
				} catch (CustomMetricException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		chain.doChain();
	}
}
