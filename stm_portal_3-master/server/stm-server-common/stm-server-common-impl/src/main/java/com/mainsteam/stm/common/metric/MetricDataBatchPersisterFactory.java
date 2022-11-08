/**
 * 
 */
package com.mainsteam.stm.common.metric;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;

import com.mainsteam.stm.caplib.dict.MetricTypeEnum;

/**
 * @author ziw
 * 
 */
public class MetricDataBatchPersisterFactory {

	private static Map<MetricTypeEnum, MetricDataBatchPersister> persisterMap = new HashMap<>();

	private static MetricDataBatchPersister customMetricMetricDataBatchPersister;

	private SqlSessionFactory myBatisSqlSessionFactory;

	private MetricTableNameManager metricTableNameCache;

	public void setMyBatisSqlSessionFactory(
			SqlSessionFactory myBatisSqlSessionFactory) {
		this.myBatisSqlSessionFactory = myBatisSqlSessionFactory;
	}

	public void setMetricTableNameCache(
			MetricTableNameManager metricTableNameCache) {
		this.metricTableNameCache = metricTableNameCache;
	}

	/**
	 * 
	 */
	public MetricDataBatchPersisterFactory() {
	}

	public synchronized MetricDataBatchPersister getMetricDataBatchPersister(
			MetricTypeEnum typeEnum) {
		if (typeEnum == null) {
			if (customMetricMetricDataBatchPersister == null) {
				MetricDataPersister p = new CustomMetricPersisterImpl(1000,
						false, myBatisSqlSessionFactory, metricTableNameCache,-1,-1,
						"replaceCustomerData","addCustomerHistoryData");
				new Thread(p, "MetricCustom_persist").start();
				customMetricMetricDataBatchPersister = p;
			}
			return customMetricMetricDataBatchPersister;
		}
		if (persisterMap.containsKey(typeEnum)) {
			return persisterMap.get(typeEnum);
		}
		MetricDataPersister p = null;
		if (MetricTypeEnum.PerformanceMetric == typeEnum) {
			p = new PerformanceMetricPersisterImpl(10000, true,
					myBatisSqlSessionFactory, metricTableNameCache,20,20,
					"PerformanceMetric");
			new Thread(p, "MetricPerformance_persist").start();
		} else if (MetricTypeEnum.AvailabilityMetric == typeEnum) {
			p = new AvalMetricPersisterImpl(1000, false,
					myBatisSqlSessionFactory, metricTableNameCache,-1,-1,
					"addMetricAvailableData");
			new Thread(p, "MetricAvailable_persist").start();
		} else if (MetricTypeEnum.InformationMetric == typeEnum) {
			p = new InfoMetricPersisterImpl(500, false,
					myBatisSqlSessionFactory, metricTableNameCache,-5,-10,
					"addMetricInfoData");
			new Thread(p, "MetricInfo_persist").start();
		}
		persisterMap.put(typeEnum, p);
		return p;
	}
}
