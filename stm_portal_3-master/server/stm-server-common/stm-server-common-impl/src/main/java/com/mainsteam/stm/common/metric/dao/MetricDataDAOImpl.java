package com.mainsteam.stm.common.metric.dao;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.InitializingBean;

import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.common.metric.MetricDataBatchPersister;
import com.mainsteam.stm.common.metric.MetricDataBatchPersisterFactory;
import com.mainsteam.stm.common.metric.MetricTableNameManager;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.common.metric.obj.MetricSummaryData;
import com.mainsteam.stm.common.metric.query.MetricHistoryDataQuery;
import com.mainsteam.stm.common.metric.query.MetricRealtimeDataQuery;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

public class MetricDataDAOImpl implements MetricDataDAO, InitializingBean {
	private static final Log logger = LogFactory
			.getLog(MetricDataDAOImpl.class);

	private MetricTableNameManager metricTableNameCache;

	private SqlSession session;
	private MetricDataBatchPersister metricDataBatchPersister;
	private MetricDataBatchPersister customMetricMetricDataBatchPersister;

	public void setMetricDataBatchPersisterFactory(
			MetricDataBatchPersisterFactory metricDataBatchPersisterFactory) {
		this.metricDataBatchPersister = metricDataBatchPersisterFactory
				.getMetricDataBatchPersister(MetricTypeEnum.PerformanceMetric);
		this.customMetricMetricDataBatchPersister = metricDataBatchPersisterFactory
				.getMetricDataBatchPersister(null);
	}

	public void setSession(SqlSession session) {
		this.session = session;
	}

	public void setMetricTableNameCache(
			MetricTableNameManager metricTableNameCache) {
		this.metricTableNameCache = metricTableNameCache;
	}

	public void init() {
	}

	@Override
	public String selectMetricTable(Map<String, String> param){
		return session.selectOne("selectMetricTable", param);
	}

	@Override
	public List<String> getMetricTableList(Map<String, String> param){
		return session.selectList("getMetricTableList", param);
	}

	@Override
	public List<Long> selectExistRealTimeMetric(Map<String, String> param){
		return session.selectList("selectExistRealTimeMetric", param);
	}

	@Override
	public void updateRealTimeMetricData(MetricData md) {
		metricDataBatchPersister.saveData(md);
	}

	@Override
	public String createRealtimeMetricTable(String metricID) {
		return metricTableNameCache
				.createRealtimeMetricTable(metricID, session);
	}

	@Override
	public String createHistoryMetricTable(String metricID) {
		return createHistoryMetricTable(metricID, this.session);
	}

	private String createHistoryMetricTable(String metricID, SqlSession session) {
		return metricTableNameCache.createHistoryMetricTable(metricID, session);
	}

	@Override
	public void addMetricDataBatch(List<MetricData> metricList) {
		for (MetricData metricData : metricList) {
			metricDataBatchPersister.saveData(metricData);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
	}

	@Override
	public void addCustomerData(MetricData data) {
		customMetricMetricDataBatchPersister.saveData(data);
	}

	@Override
	public List<Map<String, ?>> queryRealtimeMetricDataForPage(
			Page<Map<String, ?>, MetricRealtimeDataQuery> pager) {
		MetricRealtimeDataQuery query = pager.getCondition();
		List<String> tqm = new ArrayList<>();
		for (String metricID : query.getMetricID()) {
			if (metricTableNameCache.getRealtimePerfMetricTable(metricID) != null)
				tqm.add(metricID);
			else
				logger.warn("not find metric:" + metricID);
		}
		if (!tqm.contains(query.getOrderMetricID())) {
			query.setOrderMetricID(null);
		}
		query.setMetricID(tqm.toArray(new String[tqm.size()]));

		int size = (int) Math
				.ceil((double) (query.getInstanceID().length / 1000.0));
		List<Map<String, ?>> result = new ArrayList<Map<String, ?>>(10000);

		long[] old = new long[query.getInstanceID().length];

		System.arraycopy(query.getInstanceID(), 0, old, 0,
				query.getInstanceID().length);

		for (int i = 0; i < size; i++) {
			long[] queryInstanceIds = null;
			int length = 1000;
			// 最后一次
			if (i == size - 1) {
				length = old.length % 1000;
			}
			queryInstanceIds = new long[length];
			System.arraycopy(old, 0 * i, queryInstanceIds, 0, length);
			query.setInstanceID(queryInstanceIds);
			List<Map<String, ?>> temp_result = session.selectList(
					"queryRealTimeMetricDatas", pager);
			if (temp_result != null && !temp_result.isEmpty()) {
				result.addAll(temp_result);
			}
		}
		resoveOracleTimestamp(result);
		return result;
	}

	/**
	 * 处理Timestamp类型，否则后续处理会报错
	 * 
	 */
	private void resoveOracleTimestamp(List<Map<String, ?>> result) {
		if (result.size() > 0) {
			Method dateValueMethod = null;//采用oracle驱动时，需要执行的获取Date的方法
			Object[] emtpyParameters = null;//该方法的参数，这里定义它，避免每次都需要new
			for (Iterator<Map<String, ?>> iterator = result.iterator(); iterator
					.hasNext();) {
				Map<String, ?> map = iterator.next();
				for (Iterator<?> iterator2 = map.entrySet().iterator(); iterator2
						.hasNext();) {
					@SuppressWarnings("unchecked")
					Map.Entry<String, Object> e = (Map.Entry<String, Object>) iterator2
							.next();
					if(e.getValue() == null){
						continue;
					}
					if (e.getValue().getClass().getName().equals("oracle.sql.TIMESTAMP")) {
						try {
							if(dateValueMethod == null){
								dateValueMethod = e.getValue().getClass().getDeclaredMethod("timestampValue");
								emtpyParameters = new Object[0];
							}
							e.setValue(dateValueMethod.invoke(e.getValue(),emtpyParameters));
						} catch (Exception e1) {
							if (logger.isErrorEnabled()) {
								logger.error("resoveOracleTimestamp", e1);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public List<Map<String, ?>> queryRealtimeMetricData(
			MetricRealtimeDataQuery query) {
		List<String> tqm = new ArrayList<>();
		for (String metricID : query.getMetricID()) {
			if (metricTableNameCache.getRealtimePerfMetricTable(metricID) != null)
				tqm.add(metricID);
			else
				logger.warn("not find metric:" + metricID);
		}
		if (!tqm.contains(query.getOrderMetricID())) {
			query.setOrderMetricID(null);
		}
		query.setMetricID(tqm.toArray(new String[tqm.size()]));
		int size = (int) Math
				.ceil((double) (query.getInstanceID().length / 1000.0));
		List<Map<String, ?>> result = new ArrayList<Map<String, ?>>(10000);

		long[] old = new long[query.getInstanceID().length];

		System.arraycopy(query.getInstanceID(), 0, old, 0,
				query.getInstanceID().length);

		for (int i = 0; i < size; i++) {
			long[] queryInstanceIds = null;
			int length = 1000;
			// 最后一次
			if (i == size - 1) {
				length = old.length % 1000;
			}
			queryInstanceIds = new long[length];
			System.arraycopy(old, 0 * i, queryInstanceIds, 0, length);
			query.setInstanceID(queryInstanceIds);
			Map<String, MetricRealtimeDataQuery> param = new HashMap<>();
			param.put("condition", query);
			List<Map<String, ?>> temp_result = session.selectList(
					"queryRealTimeMetricDatas", param);
			if (temp_result != null && !temp_result.isEmpty()) {
				result.addAll(temp_result);
			}
		}
		resoveOracleTimestamp(result);
		return result;
	}

	@Override
	public Page<List<MetricData>, MetricHistoryDataQuery> queryHistoryMetricDatas(
			Page<List<MetricData>, MetricHistoryDataQuery> pager) {
		session.selectList("queryHistoryMetricDatas", pager);
		return pager;
	}

	@Override
	public List<MetricData> queryHistoryMetricDatas(MetricHistoryDataQuery query) {
		Page<List<MetricData>, MetricHistoryDataQuery> pager = new Page<List<MetricData>, MetricHistoryDataQuery>(
				0, 10000, query);
		List<MetricData> result = null;
		try {
			result = session.selectList("queryHistoryMetricDatas", pager);
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error("queryHistoryMetricDatas error", e);
			}
		}
		return result;
	}

	@Override
	public Set<String> getPerformanceMetricList() {
		return metricTableNameCache.getPerformanceMetricList();
	}

	@Override
	public List<MetricSummaryData> countHistory(Date startTime, Date endTime,
			String metricID) {
		String tbn = metricTableNameCache.getHistoryPerfMetricTable(metricID);
		if (null == tbn) {
			tbn = createHistoryMetricTable(metricID);
		}
		Map<String, Object> param = new HashMap<>();
		param.put("tableName", tbn);
		// param.put("startTime", startTime);
		// param.put("endTime", endTime);
		if (startTime != null) {
			SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			param.put("startTimeStr", f.format(startTime));
		}
		param.put("startTime", startTime);
		param.put("endTime", endTime);
		if (endTime != null) {
			SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			param.put("endTimeStr", f.format(endTime));
		}
		param.put("metricID", metricID);
		return session.selectList("countHistory", param);
	}

	@Override
	public List<MetricData> findTop(String metricID, long[] instanceIDes,
			int topn,String order) {
		String tbn = metricTableNameCache.getRealtimePerfMetricTable(metricID
				.toUpperCase());
		if (null == tbn) {
			tbn = createHistoryMetricTable(metricID);
		}
		Map<String, Object> param = new HashMap<>();
		param.put("tableName", tbn);
		param.put("metricID", metricID);
		param.put("instanceIDes", instanceIDes);
		param.put("top", topn);
		param.put("order", order);
		return session.selectList("findTop", param);
	}

	@Override
	public MetricData getMetricPerformanceData(long instanceID, String metricID) {

		Map<String, Object> param = new HashMap<>();
		param.put("instanceID", instanceID);
		param.put("metricID", metricID);

		return session.selectOne("getMetricPerformanceData", param);
	}

	@Override
	public MetricData getMetricAvailableData(long instanceID, String metricID) {
		Map<String, Object> param = new HashMap<>();
		param.put("instanceID", instanceID);
		param.put("metricID", metricID);
		return session.selectOne("com.mainsteam.stm.common.metric.dao.MetricAvailableDAO.getMetricAvailableData", param);
	}

	@Override
	public List<MetricData> getMetricAvailableData(long instanceID, Set<String> metrics) {
		Map<String, Object> param = new HashMap<>();
		param.put("instanceID", instanceID);
		param.put("metricSet", metrics);
		return session.selectList("com.mainsteam.stm.common.metric.dao.MetricAvailableDAO.getMetricAvailableDataBatch", param);
	}

	@Override
	public MetricData getCustomerData(long instanceID, String metricID) {
		Map<String, Object> param = new HashMap<>();
		param.put("instanceID", instanceID);
		param.put("metricID", metricID);
		return session.selectOne("getCustomerMetricData", param);
	}

	@Override
	public List<MetricData> queryHistoryCustomerMetricDatas(
			MetricHistoryDataQuery query) {
		return session.selectList("queryHistoryCustomerMetricData", query);
	}

}
