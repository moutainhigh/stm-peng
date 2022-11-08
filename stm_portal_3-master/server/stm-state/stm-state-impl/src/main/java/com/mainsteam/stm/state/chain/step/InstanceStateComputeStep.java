package com.mainsteam.stm.state.chain.step;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.mainsteam.stm.common.instance.obj.CollectStateEnum;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.state.util.CollectionStateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.dataprocess.InstanceSyncUtils;
import com.mainsteam.stm.exception.BaseException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.state.chain.StateChainStep;
import com.mainsteam.stm.state.chain.StateChainStepContext;
import com.mainsteam.stm.state.chain.StateComputeChain;
import com.mainsteam.stm.state.obj.InstanceStateData;
import com.mainsteam.stm.state.util.StateCaculatUtils;

/**
 * @author 作者：ziw
 * @date 创建时间：2016年11月17日 下午2:47:57
 * @version 1.0
 */
public class InstanceStateComputeStep implements StateChainStep {

	private static final Log logger = LogFactory.getLog(InstanceStateComputeStep.class);

	private final ResourceInstanceService resourceInstanceService;
	private final StateCaculatUtils stateCaculatUtils;
	private final Map<Long, StateChainStepContext> causeContextMap;// 每个实例进行对应.
	private final LinkedBlockingQueue<Long> instanceComputeRequestQueue;
	private final InstanceStateComputeDriver driver;
	private final CollectionStateUtils collectionStateUtils;

	public InstanceStateComputeStep(StateCaculatUtils stateCaculatUtils,
			ResourceInstanceService resourceInstanceService, CollectionStateUtils collectionStateUtils) {
		super();
		this.stateCaculatUtils = stateCaculatUtils;
		this.driver = new InstanceStateComputeDriver();
		this.causeContextMap = new ConcurrentHashMap<>();
		this.instanceComputeRequestQueue = new LinkedBlockingQueue<>();
		this.collectionStateUtils = collectionStateUtils;
		new Thread(this.driver, "InstanceStateComputeDriver").start();
		this.resourceInstanceService = resourceInstanceService;
	}

	private class InstanceStateComputeDriver implements Runnable {

		@Override
		public void run() {
			while (true) {
				try {
					doCompute(takeAndClearQueue());
				} catch (InterruptedException e) {
				}
			}
		}
	}

	private StateChainStepContext takeAndClearQueue() throws InterruptedException {
		StateChainStepContext c = null;
		do {
			Long key = instanceComputeRequestQueue.take();
			c = causeContextMap.remove(key);
			if (null != c) {
				break;
			}
		} while (c != null);
		return c;
	}

	/**
	 * 资源实例状态的计算，如果同时有许多指标状态改变，只需要计算一次就可以了。不需要每个指标改变都去计算。
	 * 
	 */
	private void requestCompute(StateChainStepContext c) {
		Long instanceIdValue = c.getMetricData().getResourceInstanceId();
		if (!causeContextMap.containsKey(instanceIdValue)) {
			instanceComputeRequestQueue.add(instanceIdValue);
		}
		causeContextMap.put(instanceIdValue, c);
		// doCompute(c);
	}

	@SuppressWarnings("unused")
	private void doCompute(StateChainStepContext context) {

		InstanceStateData preInstanceStateData = stateCaculatUtils
				.getInstanceState(context.getMetricData().getResourceInstanceId());
		ResourceInstance resourceInstance = null;
		try {
			resourceInstance = this.resourceInstanceService
					.getResourceInstance(context.getMetricData().getResourceInstanceId());
		} catch (InstancelibException e) {
			logger.info(e.getMessage(), e);
		}
		if (null == resourceInstance) {
			logger.error("can't find resource instance " + context.getMetricData().getResourceInstanceId());
			return;
		}
		synchronized (InstanceSyncUtils.getSyncObj(resourceInstance.getId())) {
			try {
				this.stateCaculatUtils.changeInstanceState(resourceInstance, context.getMetricData().getMetricId(),
						context.getResourceMetricDef(), context.getMetricData());
			} catch (BaseException e) {
				if (logger.isErrorEnabled()) {
					logger.error("doStepChain", e);
				}
			}
		}

	}

	@Override
	public void doStepChain(StateChainStepContext context, StateComputeChain chain) {
		if (Boolean.TRUE.equals(context.getContextData().get("computeInstanceState"))) {
			requestCompute(context);
		} else if (Boolean.TRUE.equals(context.getContextData().get("computeCollectionState"))) {
			ResourceInstance resourceInstance = (ResourceInstance) context.getContextData().get("resourceInstance");
			CollectStateEnum collectStateEnum = (CollectStateEnum) context.getContextData().get("collectionStateEnum");
			collectionStateUtils.saveCollectionStateUtils(resourceInstance.getId(),
					context.getMetricData().getCollectTime(), collectStateEnum);
			return;
		}
		chain.doChain();
	}
}
