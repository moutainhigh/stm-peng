/**
 * 
 */
package com.mainsteam.stm.common.metric;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.common.metric.query.MetricHistoryDataQuery;
import com.mainsteam.stm.common.metric.query.MetricRealtimeDataQuery;
import com.mainsteam.stm.executor.exception.MetricExecutorException;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;


/**
 * 
 * 指标数据查询接口<br>
 * @author heshengchao
 *
 */
public interface MetricDataService {
	/**
	 * 当前资源是否手动采集数据
	 * @param parentInstanceID
	 * @return
	 */
	public boolean isMetricGather(long parentInstanceID) throws MetricExecutorException;
	
	/**
	 * 触发资源开始采集指标
	 * @param instanceID 父资源实例Id
	 * @param containChild 是否查询子资源指标
	 * @return
	 * @throws MetricExecutorException
	 */
	public void triggerMetricGather(long parentInstanceID, boolean containChild) throws MetricExecutorException;
	
	/**触发资源开始采集信息指标
	 * @param instanceID
	 * @param containChild
	 * @return
	 * @throws MetricExecutorException
	 */
	public void triggerInfoMetricGather(long parentInstanceID, boolean containChild) throws MetricExecutorException;
	
	/**
	 * 通过资源ID与指标ID获取即时指标数据
	 * @param nodeGroupId
	 * @param instanceID
	 * @param metricID
	 * @return
	 * @throws MetricExecutorException
	 */
	public MetricData catchRealtimeMetricData(long instanceID,String metricID) throws MetricExecutorException;
	
	public MetricData catchRealtimeMetricData(long instanceID, String metricID,Map<String,String> params)throws MetricExecutorException ;
	/**查询多（子）资源的多个实时指标数据，可通过指标ID排序;
	 * @param metricID 指标ID数组：不可以为NULL或数组长度小于1
	 * @param instanceID 资源ID数组：不可以为NULL或数组长度小于1
	 * @param orderMetricID 排序指标ID，须为metricID中的某一个值，允许为NULL
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public Page<Map<String,?>,MetricRealtimeDataQuery> queryRealTimeMetricDatas(MetricRealtimeDataQuery query,int pageNo,int pageSize);
	

	List<Map<String, ?>> queryRealTimeMetricData(MetricRealtimeDataQuery query);
	
	/**查询单个（实时—）指标的多资源实例之间的TOPN数据，可通过指标值排序;
	 * @param metricID
	 * @param instanceIDes
	 * @param topn
	 * @return
	 * @deprecated
	 */
	public List<MetricData> findTop(String metricID,long[] instanceIDes ,int topn);
	
	/**查询单个（实时—）指标的多资源实例之间的TOPN数据，可通过指标值排序;
	 * @param metricID
	 * @param instanceIDes
	 * @param topn
	 * @param order 0 表示desc 1表示asc
	 * @return 
	 */
	public List<MetricData> findTop(String metricID,long[] instanceIDes ,int topn,int order);
	/**查询单个资源的历史指标数据;
	 * @param metricID
	 * @param instanceID
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public List<MetricData> queryHistoryMetricData(String metricID,long instanceID,Date startTime,Date endTime);
	
	/**（分页）查询多个资源的历史指标数据;
	 * @param metricID 指标ID：不可以为NULL
	 * @param instanceID 资源I，不可以为NULL
	 * @param startTime
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public Page<List<MetricData>,MetricHistoryDataQuery> queryHistoryMetricDatas(MetricHistoryDataQuery query,int pageNo,int pageSize);
	
	
	/**
	 * 更新指标数据<br>
	 * 
	 * a。更新缓存中的数据<br>
	 * b。更新数据库中的实时数据（如没有，添加到数据库）<br>
	 * c。更新数据库中的历史数据（如没有，添加到数据库）<br>
	 * @param metricDatas
	 */
	public void updateMetricDatas(List<MetricData> metricDatas);

	/** 添加实时指标
	 * @param data
	 */
	public void updatePerformanceMetricData(MetricData data);
	
	/** 添加可用性指标
	 * @param data
	 */
	public void updateAvailableMetricData(MetricData data);
	
	
	/** 添加信息指标
	 * @param data
	 */
	public void addMetricInfoData(MetricData data) ;
	
	/** 获取信息指标
	 * @param data
	 */
	public MetricData getMetricInfoData(long instanceID,String metricID) ;
	/** 获取信息指标
	 * @param data
	 */
	public List<MetricData> getMetricInfoDatas(long instanceID,String[] metricID) ;
	
	/**
	 * @param instanceIDes
	 * @param metricID
	 * @return
	 */
	public List<MetricData> getMetricInfoDatas(long[] instanceIDes, String[] metricID);

	/**
	 * @param instanceID
	 * @param metricID
	 * @return
	 */
	MetricData getMetricPerformanceData(long instanceID, String metricID);

	/**
	 * @param instanceID
	 * @param metricID
	 * @return
	 */
	MetricData getMetricAvailableData(long instanceID, String metricID);

	List<MetricData> getMetricAvailableData(long instanceID, Set<String> metricID);

	/** 保存自定义指标
	 * @param data
	 */
	public void addCustomerMetricData(MetricData data);

	MetricData getCustomerMetricData(long instanceID, String metricID);
	/**查询单个资源的历史指标数据;
	 * @param metricID
	 * @param instanceID
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public List<MetricData> queryHistoryCustomerMetricData(String metricID,long instanceID,Date startTime,Date endTime);

	List<MetricData> catchRealtimeMetricData(int nodeGroupId,List<Long> instanceID, String metricID) throws MetricExecutorException;
}
