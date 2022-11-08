/**
 * 
 */
package com.mainsteam.stm.executor;

import com.mainsteam.stm.executor.obj.MetricExecuteParameter;

/**
 * 指标执行过滤器
 * 
 * @author ziw
 * 
 */
public interface MetricExecuteFilter {
	/**
	 * 过滤
	 * 
	 * @param parameter
	 * @return true:过滤，,false:不过滤，执行
	 */
	public boolean filter(MetricExecuteParameter parameter);
}
