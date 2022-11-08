package com.mainsteam.stm.state.chain.step;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.state.chain.StateChainStep;
import com.mainsteam.stm.state.chain.StateChainStepContext;
import com.mainsteam.stm.state.chain.StateComputeChain;
import com.mainsteam.stm.state.obj.InstanceStateData;
import com.mainsteam.stm.state.util.StateCaculatUtils;

/**
 * 
 * 如果是性能指标状态计算，并当前主资源实例不可用，那么不需要进行计算状态<br>
 * 主要用于容错机制，因为可能性能指标正在排队采集，但此时主资源状态已经不可用，虽然所有的子资源（有可用性指标的子资源）状态已经<br>
 * 设置为不可用，但是可用性指标的缓存值已经保持了上一次的采集值，所以可能子资源的状态又会恢复成可用。<br>
 * 
 * @author 作者：ziw
 * @date 创建时间：2016年11月15日 上午11:13:07
 * @version 1.0
 */
public class PerformanceMetricMaskedStep implements StateChainStep {

	private static final Log logger = LogFactory
			.getLog(PerformanceMetricMaskedStep.class);

	private final StateCaculatUtils stateCaculatUtils;

	public PerformanceMetricMaskedStep(StateCaculatUtils stateCaculatUtils) {
		super();
		this.stateCaculatUtils = stateCaculatUtils;
	}

	@Override
	public void doStepChain(StateChainStepContext context,
			StateComputeChain chain) {
		ResourceInstance resourceInstance = (ResourceInstance) context
				.getContextData().get("resourceInstance");
		if (resourceInstance.getParentId() <= 0) { // 当前为主资源
			InstanceStateData instanceStateData = stateCaculatUtils
					.getInstanceState(resourceInstance.getId());
			if (null != instanceStateData
					&& instanceStateData.getState() == InstanceStateEnum.CRITICAL) {
				if (logger.isDebugEnabled()) {
					logger.debug("main instance{"
							+ resourceInstance.getId()
							+ "} is critical,so don't need compute metric & instance state"
							+ "，metric data is {"
							+ context.getMetricData().getMetricId()
							+ ":"
							+ JSONObject.toJSONString(context.getMetricData()
									.getMetricData()) + "}");
				}
				return;
			}
		}

		chain.doChain();
	}
}
