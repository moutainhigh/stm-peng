package com.mainsteam.stm.state.calculate;

import com.mainsteam.stm.dataprocess.MetricCalculateData;

@Deprecated
public interface PerformanceStateCaluteService {
	public PerformanceStateCaluteResult calculate(MetricCalculateData data);
}
