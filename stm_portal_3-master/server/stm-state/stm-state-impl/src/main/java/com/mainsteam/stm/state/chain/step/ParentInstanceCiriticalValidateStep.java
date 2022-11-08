package com.mainsteam.stm.state.chain.step;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.state.chain.StateChainStep;
import com.mainsteam.stm.state.chain.StateChainStepContext;
import com.mainsteam.stm.state.chain.StateComputeChain;
import com.mainsteam.stm.state.obj.InstanceStateData;
import com.mainsteam.stm.state.util.StateCaculatUtils;

/**
 * @author 作者：ziw
 * @date 创建时间：2016年11月23日 上午11:13:46
 * @version 1.0
 */
public class ParentInstanceCiriticalValidateStep implements StateChainStep {

	private static final Log logger = LogFactory.getLog(ParentInstanceCiriticalValidateStep.class);
	private final StateCaculatUtils stateCaculatUtils;

	public ParentInstanceCiriticalValidateStep(
			StateCaculatUtils stateCaculatUtils) {
		super();
		this.stateCaculatUtils = stateCaculatUtils;
	}

	@Override
	public void doStepChain(StateChainStepContext context,
			StateComputeChain chain) {
		ResourceInstance resourceInstance = (ResourceInstance) context.getContextData().get("resourceInstance");
		/*
		 * 如果当前计算的时候子资源的状态，但是主资源状态已经是不可用了，那么就不再需要进行下一步计算
		 * 这步操作多用于容错机制，一般情况下，当主资源不可用时，会把所有的带有可用性指标的子资源全部设置为不可用，
		 * 但有一种情况是，当子资源指标正在采集排队时，而此时主资源刚好不可用，就会出现这种情况。
		 * 
		 * 这点针对性能指标也是一样的。当可用性不可用时，性能就没有必要再计算了。
		 */
		if (resourceInstance.getParentId() > 0) {
			InstanceStateData mainInstanceState = this.stateCaculatUtils.getInstanceState(resourceInstance.getParentId());
			if (null != mainInstanceState && InstanceStateEnum.CRITICAL == mainInstanceState.getState()) {
				if (logger.isDebugEnabled()) {
					StringBuilder stringBuilder = new StringBuilder(200);
					stringBuilder.append("Stops to compute child instance state, cause main instance is critical. Instance is {");
					stringBuilder.append(resourceInstance.getId());
					stringBuilder.append(":");
					stringBuilder.append(context.getMetricData().getMetricId());
					stringBuilder.append("}.");
					logger.debug(stringBuilder.toString());
				}
				return;
			}
		}
		chain.doChain();
	}

}
