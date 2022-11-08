/**
 * 
 */
package com.mainsteam.stm.dataprocess.engine;

import com.mainsteam.stm.dataprocess.MetricCalculateData;


/**
 * 指标数据处理引擎
 * 
 * @author ziw
 *
 */
public interface MetricDataProcessEngine {
	/**
	 * 处理指标数据
	 * @param data
	 */
	public void handle(MetricCalculateData data);
}
