package com.mainsteam.stm.state.engine;

import com.mainsteam.stm.state.obj.InstanceStateChangeData;
import com.mainsteam.stm.state.obj.MetricStateChangeData;

@Deprecated
/**
 * Use StateHandle instead
 */
public interface StateEngine {
	
	/** 指标状态改变接口
	 * @param state
	 */
	public void handleMetricStateChange(MetricStateChangeData state);
	
	/**资源状态改变接口
	 * @param state
	 */
	public void handleInstanceStateChange(InstanceStateChangeData state);
}
