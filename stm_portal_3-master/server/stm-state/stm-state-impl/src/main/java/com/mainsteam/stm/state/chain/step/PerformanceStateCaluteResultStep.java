package com.mainsteam.stm.state.chain.step;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.dict.OperatorEnum;
import com.mainsteam.stm.caplib.dict.PerfMetricStateEnum;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.dataprocess.MetricCalculateData;
import com.mainsteam.stm.metric.obj.CustomMetric;
import com.mainsteam.stm.metric.obj.CustomMetricInfo;
import com.mainsteam.stm.metric.obj.CustomMetricThreshold;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.TimelineService;
import com.mainsteam.stm.profilelib.obj.ProfileMetric;
import com.mainsteam.stm.profilelib.obj.ProfileThreshold;
import com.mainsteam.stm.state.calculate.PerformanceStateCaluteResult;
import com.mainsteam.stm.state.chain.StateChainStep;
import com.mainsteam.stm.state.chain.StateChainStepContext;
import com.mainsteam.stm.state.chain.StateComputeChain;
import com.mainsteam.stm.state.obj.MetricStateData;
import com.mainsteam.stm.state.threshold.ProfileThresholdComputeUtil;
import com.mainsteam.stm.state.util.StateCaculatUtils;

/**
 * 性能指标状态计算
 * 
 * @author 作者：ziw
 * @date 创建时间：2016年11月15日 上午11:17:43
 * @version 1.0
 */
public class PerformanceStateCaluteResultStep implements StateChainStep {

	private static final Log logger = LogFactory
			.getLog(PerformanceStateCaluteResultStep.class);

	private final StateCaculatUtils stateCaculatUtils;
	private final ProfileService profileService;
	private final TimelineService timelineService;
	private final CapacityService capacityService;

	public PerformanceStateCaluteResultStep(
			StateCaculatUtils stateCaculatUtils, ProfileService profileService,
			TimelineService timelineService, CapacityService capacityService) {
		super();
		this.stateCaculatUtils = stateCaculatUtils;
		this.profileService = profileService;
		this.timelineService = timelineService;
		this.capacityService = capacityService;
	}

	@Override
	public void doStepChain(StateChainStepContext context,
							StateComputeChain chain) {

		CustomMetric customMetric = context.getCustomMetric();
		MetricCalculateData metricData = context.getMetricData();
		PerformanceStateCaluteResult performanceStateResult;
		ResourceMetricDef resourceMetricDef = null;
		if (customMetric == null) { // 非自定义指标计算
			performanceStateResult = calculate(context, metricData);
			resourceMetricDef = context.getResourceMetricDef();
		} else {

			List<ProfileThreshold> pts = new ArrayList<>();
			for (CustomMetricThreshold cpt : customMetric.getCustomMetricThresholds()) {
				pts.add(convert(cpt));
			}

			performanceStateResult = calculateByThreshold(metricData, pts);
			if(null == performanceStateResult) {
				if(logger.isWarnEnabled()) {
					StringBuffer stringBuffer = new StringBuffer(100);
					stringBuffer.append("custom metric calculate failed, cause metric data is empty. metric is {");
					stringBuffer.append(metricData.getMetricId());
					stringBuffer.append(":");
					stringBuffer.append(metricData.getResourceInstanceId());
					stringBuffer.append("}");
					logger.warn(stringBuffer.toString());
				}
				return;
			}

			resourceMetricDef = new ResourceMetricDef();
			CustomMetricInfo customMetricInfo = customMetric.getCustomMetricInfo();
			resourceMetricDef.setAlert(customMetricInfo.isAlert());
			resourceMetricDef.setDefaultFlapping(customMetricInfo.getFlapping());
			resourceMetricDef.setId(customMetricInfo.getId());
			resourceMetricDef.setMonitor(customMetricInfo.isMonitor());
			resourceMetricDef.setMetricType(customMetricInfo.getStyle());

			if (metricData.getResourceId() != null) {
				ResourceDef resourceDef = capacityService.getResourceDefById(metricData.getResourceId());
				if (resourceDef != null)
					resourceMetricDef.setResourceDef(resourceDef);
			}
			if (metricData.isCustomMetric()) {
				resourceMetricDef.setResourceDef(null);
			}
			if (null != performanceStateResult)
				performanceStateResult.setFlapping(customMetricInfo.getFlapping());
		}

		if (null == performanceStateResult ) { //指标策略为空或者采集值为空

			Object profileMetric = context.getContextData().get("profileMetric");
			if(null == profileMetric) {
				return;
			}

			Object preMetricStateDataObj = context.getContextData().get("preMetricStateData");
			if(null == preMetricStateDataObj) { //首次计算
				performanceStateResult = buildStateBean(metricData, MetricStateEnum.NORMAL, null, false);
			}else{
				if(logger.isWarnEnabled()){
					StringBuilder bf = new StringBuilder(100);
					bf.append("instance{");
					bf.append(metricData.getResourceInstanceId());
					bf.append("} metric{");
					bf.append(resourceMetricDef.getId());
					bf.append(",");
					bf.append(resourceMetricDef.getMetricType());
					bf.append("} calculate result is NULL,exit!");
					logger.warn(bf.toString());
				}
				return;
			}
		} else if (!performanceStateResult.isAlarmNotifiable()) {// 只监控不报警
			if (logger.isDebugEnabled()) {
				StringBuffer stringBuffer = new StringBuffer(200);
				stringBuffer.append("performance metric {");
				stringBuffer.append(metricData.getResourceInstanceId());
				stringBuffer.append(":");
				stringBuffer.append(metricData.getMetricId());
				stringBuffer.append("} & profile data {");
				stringBuffer.append(metricData.getProfileId());
				stringBuffer.append(":");
				stringBuffer.append(metricData.getTimelineId());
				stringBuffer.append("} don't alarm,just monitor.");
				logger.debug(stringBuffer.toString());
			}
			return;
		}

		MetricStateData metricStateData = performanceStateResult.getMetricStateData();
		context.getContextData().put("performanceStateResult", performanceStateResult);
		context.getContextData().put("metricState", metricStateData.getState());
		context.getContextData().put("metricStateData", metricStateData);
		chain.doChain();
	}

	/** 计算性能指标 */
	public PerformanceStateCaluteResult calculate(
			StateChainStepContext context, MetricCalculateData metricData) {
		if(logger.isDebugEnabled()) {
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append("{").append(metricData.getResourceInstanceId()).append(":");
			stringBuffer.append(metricData.getMetricId()).append("}, profile is ").append(metricData.getProfileId());
			stringBuffer.append(",timeline is ").append(metricData.getTimelineId());
			stringBuffer.append(" starts to calculate.");
			logger.debug(stringBuffer.toString());
		}
		try {
			if (metricData.getTimelineId() > 0) {//基线策略
				ProfileMetric profileMetric = timelineService.getMetricByTimelineIdAndMetricId(metricData.getTimelineId(), metricData.getMetricId());
				if (profileMetric != null) {
					if (!profileMetric.isAlarm()) {
						return new PerformanceStateCaluteResult(false);
					}
					List<ProfileThreshold> ptl = timelineService.getThresholdByTimelineIdAndMetricId(profileMetric.getTimeLineId(), profileMetric.getMetricId());
					profileMetric.setMetricThresholds(ptl);
					context.getContextData().put("profileMetric", profileMetric);

					return calculateByMetricSetting(metricData, profileMetric);
				} else if (logger.isWarnEnabled()) {
					StringBuilder bf = new StringBuilder(100);
					bf.append("metric[");
					bf.append(metricData.getResourceInstanceId());
					bf.append(",");
					bf.append(metricData.getMetricId());
					bf.append("] can't find profileTimeline[");
					bf.append(metricData.getCollectTime());
					bf.append("],exit calculate!");
					logger.warn(bf.toString());
				}
			} else {
				if (metricData.getProfileId() == 0) {
					if (logger.isWarnEnabled()) {
						StringBuilder bf = new StringBuilder(100);
						bf.append("metric[");
						bf.append(metricData.getResourceInstanceId());
						bf.append(",");
						bf.append(metricData.getMetricId());
						bf.append("] profile is 0,That's not correct,exit!");
						logger.warn(bf.toString());
					}
					return null;
				}
				ProfileMetric profileMetric = profileService.getMetricByProfileIdAndMetricId(metricData.getProfileId(), metricData.getMetricId());
				if (profileMetric == null) {
					if (logger.isWarnEnabled()) {
						StringBuilder bf = new StringBuilder(100);
						bf.append("metric[");
						bf.append(metricData.getResourceInstanceId());
						bf.append(",");
						bf.append(metricData.getMetricId());
						bf.append("] can't find profileMetric,exit calculate!");
						logger.warn(bf.toString());
					}
					return null;
				} else if (!profileMetric.isAlarm()) {
					return new PerformanceStateCaluteResult(false);
				}
				context.getContextData().put("profileMetric", profileMetric);
				// 计算默认设置
				return calculateByMetricSetting(metricData, profileMetric);
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()){
				StringBuilder stringBuilder = new StringBuilder(100);
				stringBuilder.append("Performance metric calculate error, metric is ");
				stringBuilder.append(metricData.getMetricId());
				stringBuilder.append(",instance is ");
				stringBuilder.append(metricData.getResourceInstanceId());
				stringBuilder.append(",error message :");
				stringBuilder.append(e.getMessage());
				logger.error(stringBuilder.toString(), e);
			}
		}
		// 无值，默认为未知状态
		return null;
	}

	private PerformanceStateCaluteResult calculateByMetricSetting(
			MetricCalculateData metricData, ProfileMetric profileMetric) {

		if (metricData.getMetricData() == null || metricData.getMetricData().length < 1 ||
				StringUtils.isEmpty(metricData.getMetricData()[0])) {
			return null;
		}

		return calculateByThreshold(metricData, profileMetric.getMetricThresholds());

	}

	private PerformanceStateCaluteResult calculateByThreshold(MetricCalculateData metricData, List<ProfileThreshold> ptl) {
		if (metricData.getMetricData() == null || metricData.getMetricData().length < 1 ||
				StringUtils.isEmpty(metricData.getMetricData()[0])) {
			return null;
		}
		try{
			Float md = Float.valueOf(metricData.getMetricData()[0]);
			ProfileThreshold calPt = null;
			MetricStateEnum calState = null;
			for (ProfileThreshold pt : ptl) {
				MetricStateEnum tmpState = ProfileThresholdComputeUtil.compute(pt, md);
				if (tmpState != null) {
					if (calState == null || calState.getStateVal() < tmpState.getStateVal()) {
						calState = tmpState;
						calPt = pt;
					}
				}
			}
			return buildStateBean(metricData, calState == null ? MetricStateEnum.NORMAL : calState, calPt, true);

		}catch (Throwable throwable) {
			if(logger.isWarnEnabled()) {
				StringBuilder stringBuilder = new StringBuilder(200);
				stringBuilder.append("performance state compute error, instance is ");
				stringBuilder.append(metricData.getResourceInstanceId());
				stringBuilder.append(" and metric is ");
				stringBuilder.append(metricData.getMetricId());
				stringBuilder.append(".Error message is ");
				stringBuilder.append(throwable.getMessage());
				logger.warn(stringBuilder.toString(), throwable);
			}
		}
		return null;
	}

	private PerformanceStateCaluteResult buildStateBean(
			MetricCalculateData metricData, MetricStateEnum state,
			ProfileThreshold threshold, boolean alarmNotifiable) {

		MetricStateData msd = new MetricStateData();
		PerformanceStateCaluteResult psr = new PerformanceStateCaluteResult();
		psr.setMetricStateData(msd);
		psr.setThreshhold(threshold);
		psr.setAlarmNotifiable(alarmNotifiable);

		msd.setCollectTime(metricData.getCollectTime());
		msd.setInstanceID(metricData.getResourceInstanceId());
		msd.setMetricID(metricData.getMetricId());
		msd.setState(state);
		msd.setType(MetricTypeEnum.PerformanceMetric);

		return psr;
	}

	private ProfileThreshold convert(CustomMetricThreshold cpt) {
		ProfileThreshold pt = new ProfileThreshold();
		pt.setMetricId(cpt.getMetricId());
		pt.setThresholdValue(cpt.getThresholdValue());
		pt.setExpressionDesc(cpt.getOperatorDesc());

		if (MetricStateEnum.SERIOUS == cpt.getMetricState()) {
			pt.setPerfMetricStateEnum(PerfMetricStateEnum.Major);
		} else if (MetricStateEnum.WARN == cpt.getMetricState()) {
			pt.setPerfMetricStateEnum(PerfMetricStateEnum.Minor);
		} else if (MetricStateEnum.NORMAL == cpt.getMetricState()) {
			pt.setPerfMetricStateEnum(PerfMetricStateEnum.Normal);
		} else if (MetricStateEnum.NORMAL_UNKNOWN == cpt.getMetricState()) {
			pt.setPerfMetricStateEnum(PerfMetricStateEnum.Normal);
		}

		if (cpt.getOperator() == OperatorEnum.Equal) {
			pt.setExpressionOperator("=");
		} else if (cpt.getOperator() == OperatorEnum.Great) {
			pt.setExpressionOperator(">");
		} else if (cpt.getOperator() == OperatorEnum.GreatEqual) {
			pt.setExpressionOperator(">=");
		} else if (cpt.getOperator() == OperatorEnum.Less) {
			pt.setExpressionOperator("<");
		} else if (cpt.getOperator() == OperatorEnum.LessEqual) {
			pt.setExpressionOperator("<=");
		}

		return pt;
	}


}
