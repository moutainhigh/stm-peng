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

public class AvailableStateCalculateServiceImpl implements MetricDataProcessor {

	private final StateChainStepContextRunner availableChain;

	public AvailableStateCalculateServiceImpl() throws IOException {
		super();
		this.availableChain = StateChainFactory
				.findStateComputeChain("available");
	}

	@Override
	public MetricDataPersistence process(MetricCalculateData metricData,
			ResourceMetricDef resourceMetricDef, CustomMetric customMetric,
			Map<String, Object> contextData) throws Exception {
		availableChain.startRun(new StateChainStepContext(metricData,
				resourceMetricDef, customMetric, contextData));
		return null;
	}

	@Override
	public int getOrder() {
		return 8;
	}

}
