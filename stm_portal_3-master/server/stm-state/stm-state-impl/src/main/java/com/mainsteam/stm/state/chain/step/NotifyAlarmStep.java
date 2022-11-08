package com.mainsteam.stm.state.chain.step;

import com.mainsteam.stm.state.calculate.PerformanceStateCaluteResult;
import com.mainsteam.stm.state.chain.StateChainStep;
import com.mainsteam.stm.state.chain.StateChainStepContext;
import com.mainsteam.stm.state.chain.StateComputeChain;
import com.mainsteam.stm.state.obj.MetricStateData;
import com.mainsteam.stm.state.util.StateCaculatUtils;

/**
 * 性能指标状态改变，通知产生告警.
 * 
 * @author 作者：ziw
 * @date 创建时间：2016年11月17日 下午2:55:13
 * @version 1.0
 */
public class NotifyAlarmStep implements StateChainStep {

	private final StateCaculatUtils stateCaculatUtils;

	public NotifyAlarmStep(StateCaculatUtils stateCaculatUtils) {
		super();
		this.stateCaculatUtils = stateCaculatUtils;
	}

	@Override
	public void doStepChain(StateChainStepContext context,
			StateComputeChain chain) {
		if (Boolean.TRUE.equals(context.getContextData().get("flapping"))) {
			PerformanceStateCaluteResult performanceStateResult = (PerformanceStateCaluteResult) context
					.getContextData().get("performanceStateResult");
			this.stateCaculatUtils.notifyMetricAlarmMsg(
					context.getMetricData(),
					context.getResourceMetricDef(),
					(MetricStateData) context.getContextData().get(
							"metricStateData"), (MetricStateData) context
							.getContextData().get("preMetricStateData"),
					performanceStateResult.getThreshhold(),
					performanceStateResult.getFlapping() + 1);
		}
		chain.doChain();
	}
}
