package com.mainsteam.stm.state;

import java.io.IOException;
import java.util.Map;

import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.dataprocess.MetricCalculateData;
import com.mainsteam.stm.dataprocess.engine.MetricDataPersistence;
import com.mainsteam.stm.dataprocess.engine.MetricDataProcessor;
import com.mainsteam.stm.metric.obj.CustomMetric;
import com.mainsteam.stm.state.chain.StateChainFactory;
import com.mainsteam.stm.state.chain.StateChainStepContext;
import com.mainsteam.stm.state.chain.StateChainStepContextRunner;

/**
 * @author cx
 * 
 */
public class PerformanceStateCaluteServiceImpl implements MetricDataProcessor {

	private final StateChainStepContextRunner performanceChain;

	public PerformanceStateCaluteServiceImpl() throws IOException {
		super();
		this.performanceChain = StateChainFactory
				.findStateComputeChain("performance");
	}

	@Override
	public MetricDataPersistence process(MetricCalculateData metricData,
			ResourceMetricDef resourceMetricDef, CustomMetric customMetric,
			Map<String, Object> contextData) throws Exception {
		performanceChain.startRun(new StateChainStepContext(metricData,
				resourceMetricDef, customMetric, contextData));
		return null;
	}

	@Override
	public int getOrder() {
		return 10;
	}

}
