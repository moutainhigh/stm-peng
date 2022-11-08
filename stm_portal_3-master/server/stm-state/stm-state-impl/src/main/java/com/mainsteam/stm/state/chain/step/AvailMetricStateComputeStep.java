package com.mainsteam.stm.state.chain.step;

import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.state.obj.MetricStateData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.dataprocess.MetricCalculateData;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.metric.obj.CustomMetric;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.TimelineService;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.ProfileMetric;
import com.mainsteam.stm.state.chain.StateChainStep;
import com.mainsteam.stm.state.chain.StateChainStepContext;
import com.mainsteam.stm.state.chain.StateComputeChain;
import com.mainsteam.stm.state.util.StateCaculatUtils;

/**
 * @author 作者：ziw
 * @date 创建时间：2016年11月23日 上午10:49:30
 * @version 1.0
 */
public class AvailMetricStateComputeStep implements StateChainStep {
	private static final Log logger = LogFactory
			.getLog(AvailMetricStateComputeStep.class);
	private static final int NULL_METRIC_DATA = 0;

	private final TimelineService timelineService;
	private final ProfileService profileService;
	private StateCaculatUtils stateCaculatUtils;

	public AvailMetricStateComputeStep(TimelineService timelineService,
			ProfileService profileService, StateCaculatUtils stateCaculatUtils) {
		super();
		this.timelineService = timelineService;
		this.profileService = profileService;
		this.stateCaculatUtils = stateCaculatUtils;
	}

	@Override
	public void doStepChain(StateChainStepContext context, StateComputeChain chain) {
		ProfileMetric profileMetric = null;
		MetricCalculateData metricData = context.getMetricData();
		CustomMetric customMetric = context.getCustomMetric();
		if (customMetric == null) {
			/*
			 * 需要判断指标是基线采集值，还是策略采集值
			 */
			long timeId = metricData.getTimelineId();
			String metricID = metricData.getMetricId();
			if (timeId <= 0) {
				// 使用策略
				try {
					profileMetric = profileService.getMetricByProfileIdAndMetricId(metricData.getProfileId(), metricID);
				} catch (ProfilelibException e) {
					if (logger.isErrorEnabled()) {
						logger.error("doStepChain", e);
					}
				}
			} else {
				// 使用基线
				try {
					profileMetric = timelineService.getMetricByTimelineIdAndMetricId(timeId, metricID);
				} catch (ProfilelibException e) {
					if (logger.isErrorEnabled()) {
						logger.error("doStepChain", e);
					}
				}
			}
			if (profileMetric == null || !profileMetric.isAlarm()) {
				return;
			}
			context.getContextData().put("profileMetric", profileMetric);
		}else {
			if(!customMetric.getCustomMetricInfo().isAlert())
				return;
		}
		MetricStateData preMetricStateData = null;
		Object obj = context.getContextData().get("preMetricStateData");
		if(null != obj)
			preMetricStateData = (MetricStateData)obj;
		String metricValue = "1";
		if(null != preMetricStateData) {
			if (null != metricData.getMetricData() && metricData.getMetricData().length > NULL_METRIC_DATA) {
				metricValue = metricData.getMetricData()[0];
			}
		}

		try {
			ResourceInstance resourceInstance = (ResourceInstance) context.getContextData().get("resourceInstance");
			MetricStateEnum metricStateEnum = this.stateCaculatUtils.calculateAvailMetricState(resourceInstance,
							metricData.getMetricId(), metricValue, preMetricStateData);
			//如果当前计算状态为空，则不往下计算，因为出现这种情况有两种情况，一是资源未监控，二是状态计算出现异常；
			if(null == metricStateEnum) {
				if(logger.isWarnEnabled()) {
					StringBuilder sb = new StringBuilder();
					sb.append("avail metric [");
					sb.append(metricData.getMetricId());
					sb.append("], instance ");
					sb.append(metricData.getResourceInstanceId());
					sb.append(" can't calculate state.");
					logger.warn(sb.toString());
				}
				return ;
			}
			context.getContextData().put("metricState", metricStateEnum);
			context.getContextData().put("availMetricValue", metricValue);
			chain.doChain();
		} catch (InstancelibException e) {
			if (logger.isErrorEnabled()) {
				logger.error("doStepChain", e);
			}
		}
	}

}
