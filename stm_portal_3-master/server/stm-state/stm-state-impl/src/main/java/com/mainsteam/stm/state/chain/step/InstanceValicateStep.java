package com.mainsteam.stm.state.chain.step;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.state.chain.StateChainStep;
import com.mainsteam.stm.state.chain.StateChainStepContext;
import com.mainsteam.stm.state.chain.StateComputeChain;

/**
 * 验证资源实例是否有效
 * 
 * @author 作者：ziw
 * @date 创建时间：2016年11月15日 上午11:02:23
 * @version 1.0
 */
public class InstanceValicateStep implements StateChainStep {

	private static final Log logger = LogFactory.getLog(InstanceValicateStep.class);

	private final ResourceInstanceService resourceInstanceService;

	public InstanceValicateStep(ResourceInstanceService resourceInstanceService) {
		super();
		this.resourceInstanceService = resourceInstanceService;
	}

	@Override
	public void doStepChain(StateChainStepContext context,
			StateComputeChain chain) {
		if (context.getMetricData().getResourceInstanceId() <= 0) {
			return;
		}
		// 获取资源状态
		ResourceInstance resourceInstance = null;
		try {
			resourceInstance = resourceInstanceService.getResourceInstance(context.getMetricData().getResourceInstanceId());
		} catch (InstancelibException e) {
			if (logger.isErrorEnabled()) {
				logger.error("doStepChain", e);
			}
		}
		if (resourceInstance == null || resourceInstance.getLifeState() == InstanceLifeStateEnum.NOT_MONITORED) {
			if (logger.isWarnEnabled()) {
				StringBuilder bf = new StringBuilder(100);
				bf.append("ResourceInstance [");
				bf.append(context.getMetricData().getResourceInstanceId());
				bf.append("  not found or not monitored please check ]");
				logger.warn(bf.toString());
			}
			return;
		}
		context.getContextData().put("resourceInstance", resourceInstance);
		chain.doChain();
	}
}
