/**
 * 
 */
package com.mainsteam.stm.dataprocess.engine;
import java.util.Map;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.dataprocess.MetricCalculateData;
import com.mainsteam.stm.metric.obj.CustomMetric;

/**
 * 指标数据处理器
 * 
 * @author ziw
 *
 */
public interface MetricDataProcessor {
	/**
	 * 处理指标数据
	 * 
	 * @param data
	 * @throws Exception 
	 */
	public MetricDataPersistence process(MetricCalculateData data,ResourceMetricDef rdf,CustomMetric cm,Map<String,Object> contextData) throws Exception;

	/**排序号，用于标注在处理引擎中队列的位置
	 * @return
	 */
	public int getOrder();
	
	
}
