package com.mainsteam.stm.state.chain.step;

import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.dataprocess.MetricCalculateData;
import com.mainsteam.stm.state.obj.InstanceStateChangeData;
import com.mainsteam.stm.state.obj.InstanceStateData;
import com.mainsteam.stm.state.util.StateCaculatUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.state.chain.StateChainStep;
import com.mainsteam.stm.state.chain.StateChainStepContext;
import com.mainsteam.stm.state.chain.StateComputeChain;

import java.util.Map;

/**
 * 判断指标状态是否已经改变。如果没有改变，后续不需要再计算了。
 * 
 * @author 作者：ziw
 * @date 创建时间：2016年11月15日 下午4:59:42
 * @version 1.0
 */
public class MetricStateChangeValidate implements StateChainStep {

	private static final Log logger = LogFactory
			.getLog(MetricStateChangeValidate.class);

	private final StateCaculatUtils stateCaculatUtils;

	public MetricStateChangeValidate(StateCaculatUtils stateCaculatUtils) {
		super();
		this.stateCaculatUtils = stateCaculatUtils;
	}

	@Override
	public void doStepChain(StateChainStepContext context,
			StateComputeChain chain) {
		MetricStateEnum metricState = (MetricStateEnum) context.getContextData().get("metricState");
		MetricStateEnum preMetricState = (MetricStateEnum) context.getContextData().get("preMetricState");
		if (preMetricState != null && preMetricState == metricState) {
			if (logger.isDebugEnabled()) {
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append("Metric state is not changed, instance is ");
				stringBuilder.append(context.getMetricData().getResourceInstanceId());
				stringBuilder.append(",metric is ");
				stringBuilder.append(context.getMetricData().getMetricId());
				logger.debug(stringBuilder.toString());
			}
			ResourceMetricDef resourceMetricDef = context.getResourceMetricDef();
			if(null != resourceMetricDef ) {
				ResourceDef resourceDef = resourceMetricDef.getResourceDef();
				if(resourceDef != null && StringUtils.equals(resourceDef.getId(), "CameraRes") &&
						resourceMetricDef.getMetricType() == MetricTypeEnum.AvailabilityMetric && metricState == MetricStateEnum.CRITICAL){
					//对于摄像头需要根据指标的值来判断，即使状态不变，但采集值变了也需要告警
					MetricCalculateData metricData = context.getMetricData();
					Map<String, String> availMetricData = stateCaculatUtils.getAvailMetricData(metricData.getResourceInstanceId());
					if(null != availMetricData) {
						String next = availMetricData.values().iterator().next();
						String[] metricValue = metricData.getMetricData();
						if(StringUtils.isNotBlank(next) && metricValue != null && metricValue.length > 0){
							if(!StringUtils.equals(next, metricValue[0])) {
								if(logger.isInfoEnabled()) {
									logger.info("cameraRes(" + metricData.getResourceInstanceId() + ") should alarm," +
											" event if state is not changed, values (" + next + "-->" + metricValue[0] + ")");
								}
								InstanceStateChangeData instanceStateChangeData = new InstanceStateChangeData();
								instanceStateChangeData.setCauseByMetricDef(resourceMetricDef);
								instanceStateChangeData.setChangeNum(1);
								instanceStateChangeData.setNotifiable(true);
								instanceStateChangeData.setCauseByMetricID(resourceMetricDef.getId());
								instanceStateChangeData.setMetricData(metricData);
								instanceStateChangeData.setNewState(new InstanceStateData(metricData.getResourceInstanceId(),
										StateCaculatUtils.convertMetricStateToInstanceState(metricState),metricData.getCollectTime(),metricData.getMetricId(),
										metricData.getResourceInstanceId(),null));
								stateCaculatUtils.notifyInstanceAlarmMsg(instanceStateChangeData);

								stateCaculatUtils.saveAvailMetricData(metricData.getResourceInstanceId(), metricData.getMetricId(), metricValue[0]);

							}
						}
					}
					return;
				}
			}
		} else {
			context.getContextData().put("metricStateChange", Boolean.TRUE);
			if(logger.isInfoEnabled()) {
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append("Metric state has been changed ");
				stringBuilder.append(preMetricState == null ? "" : "from " + preMetricState);
				stringBuilder.append(" to ");
				stringBuilder.append(metricState);
				stringBuilder.append(", instance is ");
				stringBuilder.append(context.getMetricData().getResourceInstanceId());
				stringBuilder.append(",metric is ");
				stringBuilder.append(context.getMetricData().getMetricId());
				logger.info(stringBuilder.toString());
			}
		}
		chain.doChain();
	}
}
