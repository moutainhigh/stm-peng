/**
 * 
 */
package com.mainsteam.stm.common.metric;

import com.mainsteam.stm.common.metric.obj.MetricData;

/**
 * @author ziw
 * 
 */
public interface MetricDataBatchPersister {


	/**
	 * 
	 * @param data
	 * @return
	 */
	public boolean saveData(MetricData data);
}
