package com.mainsteam.stm.state.chain;

import java.util.concurrent.atomic.AtomicLong;

import com.mainsteam.stm.dataprocess.MetricCalculateData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author 作者：ziw
 * @date 创建时间：2016年11月23日 上午11:45:35
 * @version 1.0
 */
public class DefaultStateComputeChain implements StateChainStepContextRunner {

	private static final Log logger = LogFactory
			.getLog(DefaultStateComputeChain.class);

	private final StateChainStep[] stateChainSteps;
	private final AtomicLong[] stateChainStepsOffsetTimes;
	private final AtomicLong[] stateChainStepsTimes;
	private final boolean[] filterStep;

	public DefaultStateComputeChain(StateChainStep[] stateChainSteps) {
		this.stateChainSteps = stateChainSteps;
		this.stateChainStepsOffsetTimes = new AtomicLong[stateChainSteps.length];
		this.stateChainStepsTimes = new AtomicLong[stateChainSteps.length];
		this.filterStep = new boolean[stateChainSteps.length];
		for (int i = 0; i < stateChainStepsOffsetTimes.length; i++) {
			this.stateChainStepsOffsetTimes[i] = new AtomicLong(0);
			this.stateChainStepsTimes[i] = new AtomicLong(0);
			this.filterStep[i] = false;
		}
	}

	@Override
	public void doChain() {
	}

	public boolean[] getFilterStep() {
		return filterStep;
	}

	public StateChainStep[] getStateChainSteps() {
		return stateChainSteps;
	}

	public AtomicLong[] getStateChainStepsOffsetTimes() {
		return stateChainStepsOffsetTimes;
	}

	public AtomicLong[] getStateChainStepsTimes() {
		return stateChainStepsTimes;
	}

	public void filterStep(int index) {
		if (index >= 0 && index < filterStep.length) {
			filterStep[index] = true;
		}
	}

	public void unFilterStep(int index) {
		if (index >= 0 && index < filterStep.length) {
			filterStep[index] = false;
		}
	}

	/**
	 * 循环，观察是否需要执行下一步。
	 * 
	 * @param w
	 */
	private void doChain0(final StateChainStepContextWrapper w,
			final StateComputeChain innerChain) {
		int lastIndex = -1;
		int currentIndex = w.stepIndex;
		while (currentIndex < stateChainSteps.length
				&& lastIndex != currentIndex) {
			lastIndex = currentIndex;
			if (!this.filterStep[currentIndex]) {
				long temp = System.currentTimeMillis();
				stateChainSteps[currentIndex].doStepChain(w.chainStepContext,
						innerChain);
				long offset = System.currentTimeMillis() - temp;
				this.stateChainStepsOffsetTimes[currentIndex].addAndGet(offset);
				this.stateChainStepsTimes[currentIndex].incrementAndGet();
			} else {
				w.stepIndex++;
			}
			currentIndex = w.stepIndex;
		}
	}

	@Override
	public void startRun(StateChainStepContext chainStepContext) {
		final StateChainStepContextWrapper w = new StateChainStepContextWrapper();
		w.chainStepContext = chainStepContext;
		try {
			StateComputeChain innerChain = new StateComputeChain() {
				@Override
				public void doChain() {
					w.stepIndex++;
				}
			};
			doChain0(w, innerChain);
			if (logger.isDebugEnabled()) {
				if (w.stepIndex < stateChainSteps.length) {
					logger.debug("do stop chain at "
							+ stateChainSteps[w.stepIndex]
							+ " instanceId="
							+ chainStepContext.getMetricData().getResourceInstanceId() + " metricId="
							+ chainStepContext.getMetricData().getMetricId());
				}
			}
		} catch (Throwable e) {
			if (logger.isErrorEnabled()) {
				MetricCalculateData metricCalculateData = chainStepContext.getMetricData();
				StringBuffer stringBuffer = new StringBuffer(200);
				stringBuffer.append("stop run at chain step ");
				stringBuffer.append(stateChainSteps[w.stepIndex]);
				if(null != metricCalculateData){
					stringBuffer.append(",metric data is {");
					stringBuffer.append(metricCalculateData.getMetricId());
					stringBuffer.append(":");
					stringBuffer.append(metricCalculateData.getResourceInstanceId());
					stringBuffer.append("},profile is {");
					stringBuffer.append(metricCalculateData.getProfileId());
					stringBuffer.append(":");
					stringBuffer.append(metricCalculateData.getTimelineId());
					stringBuffer.append("}");
				}
				logger.error(stringBuffer.toString(), e);
			}
		} finally {
		}
	}

	private class StateChainStepContextWrapper {
		private StateChainStepContext chainStepContext;
		private int stepIndex = 0;
	}
}
