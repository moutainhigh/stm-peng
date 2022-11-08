package com.mainsteam.stm.state.chain.step;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.state.chain.StateChainStep;
import com.mainsteam.stm.state.chain.StateChainStepContext;
import com.mainsteam.stm.state.chain.StateComputeChain;
import com.mainsteam.stm.state.obj.InstanceStateData;
import com.mainsteam.stm.state.util.StateCaculatUtils;

/**
 * @author 作者：ziw
 * @date 创建时间：2016年11月23日 上午11:16:50
 * @version 1.0
 */
public class PerformanceInstanceCiriticalValidateStep implements StateChainStep {
	private static final Log logger = LogFactory
			.getLog(PerformanceInstanceCiriticalValidateStep.class);
	private final StateCaculatUtils stateCaculatUtils;

	public PerformanceInstanceCiriticalValidateStep(
			StateCaculatUtils stateCaculatUtils) {
		super();
		this.stateCaculatUtils = stateCaculatUtils;
	}

	@Override
	public void doStepChain(StateChainStepContext context,
			StateComputeChain chain) {
		long instanceId = context.getMetricData().getResourceInstanceId();
		InstanceStateData instanceState = this.stateCaculatUtils.getInstanceState(instanceId);
		if (null != instanceState && InstanceStateEnum.CRITICAL == instanceState.getState()) {
			if (logger.isDebugEnabled()) {
				StringBuilder stringBuilder = new StringBuilder(200);
				stringBuilder.append("Stops to compute performance state, cause instance is critical. instance is ");
				stringBuilder.append(instanceId);
				stringBuilder.append(", metric is ");
				stringBuilder.append(context.getMetricData().getMetricId());
				logger.debug(stringBuilder.toString());
			}
			return;
		}
		chain.doChain();
	}
}
