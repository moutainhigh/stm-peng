package com.mainsteam.stm.state.calculate;

import com.mainsteam.stm.dataprocess.MetricCalculateData;
import com.mainsteam.stm.state.obj.MetricStateData;

@Deprecated
public interface AvailableStateCalculateService {
	
	public MetricStateData calculate(MetricCalculateData data);

}
