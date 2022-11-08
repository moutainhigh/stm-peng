package com.mainsteam.stm.state.engine;

import com.mainsteam.stm.state.obj.InstanceStateChangeData;
import com.mainsteam.stm.state.obj.MetricStateChangeData;

/**状态处理服务接口
 * @author cx
 *
 */
public interface StateHandle {

	/** 指标状态改变接口
	 * @param state
	 */
	public void onMetricStateChange(MetricStateChangeData stateChange);
	
	/**资源状态改变接口
	 * @param state
	 */
	public void onInstanceStateChange(InstanceStateChangeData stateChage);
	
	/**排序号，用于标注在处理引擎中队列的位置
	 * @return
	 */
	public int getOrder();
}
