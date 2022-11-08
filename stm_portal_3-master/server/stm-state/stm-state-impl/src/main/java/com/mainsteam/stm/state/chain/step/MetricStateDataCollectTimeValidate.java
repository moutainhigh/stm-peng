package com.mainsteam.stm.state.chain.step;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.state.chain.StateChainStep;
import com.mainsteam.stm.state.chain.StateChainStepContext;
import com.mainsteam.stm.state.chain.StateComputeChain;
import com.mainsteam.stm.state.obj.MetricStateData;
import com.mainsteam.stm.state.util.StateCaculatUtils;

/**
 * 验证指标状态的采集时间是否已经过时。
 * 
 * @author 作者：ziw
 * @date 创建时间：2016年11月15日 下午4:29:55
 * @version 1.0
 */
public class MetricStateDataCollectTimeValidate implements StateChainStep {

	private static final Log logger = LogFactory
			.getLog(MetricStateDataCollectTimeValidate.class);

	private final StateCaculatUtils stateCaculatUtils;

	public MetricStateDataCollectTimeValidate(
			StateCaculatUtils stateCaculatUtils) {
		super();
		this.stateCaculatUtils = stateCaculatUtils;
	}

	@Override
	public void doStepChain(StateChainStepContext context,
			StateComputeChain chain) {
		long instanceId = context.getMetricData().getResourceInstanceId();
		String metricId = context.getMetricData().getMetricId();
		Date collectTime = context.getMetricData().getCollectTime();
		// 检测状态是否已变迁。
		MetricStateData preMetricStateData = stateCaculatUtils.getMetricState(instanceId, metricId);
		// 如果当前采集时间早于缓存中最新的采集时间，则需要抛弃掉
		if (preMetricStateData != null) {
			if (collectTime.before(preMetricStateData.getCollectTime())) {
				if (logger.isInfoEnabled()) {
					StringBuilder stringBuffer = new StringBuilder(100);
					stringBuffer.append("Metric {");
					stringBuffer.append(instanceId);
					stringBuffer.append(":");
					stringBuffer.append(metricId);
					stringBuffer.append("} has cried off computing state,causing its delayed.");
					logger.info(stringBuffer.toString());
				}
				return;
			} else {
				context.getContextData().put("preMetricState", preMetricStateData.getState());
				context.getContextData().put("preMetricStateData", preMetricStateData);
			}
		}
		chain.doChain();
	}
}
