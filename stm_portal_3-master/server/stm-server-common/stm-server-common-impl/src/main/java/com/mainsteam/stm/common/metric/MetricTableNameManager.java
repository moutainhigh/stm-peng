package com.mainsteam.stm.common.metric;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import com.mainsteam.stm.common.metric.dao.MetricDataDAO;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.common.metric.obj.MetricDataPO;
import com.mainsteam.stm.util.PartitionDateUtil;

public class MetricTableNameManager {

	private static final Log logger = LogFactory
			.getLog(MetricTableNameManager.class);

	private final Map<String, String> HIS_TABLE_NAME_MAP = new HashMap<String, String>();
	private final Map<String, String> REAL_TABLE_NAME_MAP = new HashMap<String, String>();
	private final Map<String, Map<Long, Long>> REAL_TABLE_INSTANCE_MAP = new HashMap<String, Map<Long, Long>>();

	private final Map<String, Map<Long, Long>> AVAIL_TABLE_INSTANCE_MAP = new HashMap<String, Map<Long, Long>>();
	private final Map<String, Map<Long, Long>> INFO_TABLE_INSTANCE_MAP = new HashMap<String, Map<Long, Long>>();
	private final Map<String, Map<Long, Long>> CUSTOM_TABLE_INSTANCE_MAP = new HashMap<String, Map<Long, Long>>();

	private static final String HISTORY_TABLE_NAME_PREFIX = "STM_M_H_";
	private static final String REALTIME_TABLE_NAME_PREFIX = "STM_M_R_";
	private String dbName;
	private String dbUrl;
	private SqlSession session;

    private MetricDataDAO metricDataDAO;

    public void setMetricDataDAO(MetricDataDAO metricDataDAO) {
        this.metricDataDAO = metricDataDAO;
    }
	
	public void setSession(SqlSession session) {
		this.session = session;
	}
	
	public MetricTableNameManager() {
	}

	public void start() throws Exception {
		cacheTableName();
	}

	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}

	public boolean isPerformantMetricDataExist(String metricId, Long instanceId) {
		metricId = metricId.toUpperCase();
		boolean exist = false;
		Map<Long, Long> map = REAL_TABLE_INSTANCE_MAP.get(metricId);
		if (map != null) {
			exist = map.containsKey(instanceId);
			if (!exist) {
				String tableName = getRealtimePerfMetricTable(metricId);
				Map<String, Object> param = new HashMap<>(2);
				param.put("tableName", tableName);
				param.put("resourceInstanceId", instanceId);
				exist = (int) session.selectOne("selectExistRealTimeMetricOne",
						param) > 0;
				if (exist) {
					map.put(instanceId, instanceId);
				}
			}
		}
		return exist;
	}

	public boolean isInformantMetricDataExist(String metricId, Long instanceId) {
		String tempMetricId = metricId.toUpperCase();
		boolean exist = false;
		Map<Long, Long> map = INFO_TABLE_INSTANCE_MAP.get(tempMetricId);
		if (map != null) {
			exist = map.containsKey(instanceId);
			if (!exist) {
				Map<String, Object> param = new HashMap<>(2);
				param.put("metricId", metricId);
				param.put("resourceInstanceId", instanceId);
				exist = (int) session.selectOne("selectInfoMetricOne",
						param) > 0;
				if (exist) {
					map.put(instanceId, instanceId);
				}
			}
		}
		return exist;
	}

	public boolean isAvaMetricDataExist(String metricId, Long instanceId) {
		String tempMetricId = metricId.toUpperCase();
		boolean exist = false;
		Map<Long, Long> map = AVAIL_TABLE_INSTANCE_MAP.get(tempMetricId);
		if (map != null) {
			exist = map.containsKey(instanceId);
			if (!exist) {
				Map<String, Object> param = new HashMap<>(2);
				param.put("metricId", metricId);
				param.put("resourceInstanceId", instanceId);
				exist = (int) session.selectOne("selectAvaMetricOne",
						param) > 0;
				if (exist) {
					map.put(instanceId, instanceId);
				}
			}
		}
		return exist;
	}


	public boolean isCustomMetricDataExist(String metricId, Long instanceId) {
		metricId = metricId.toUpperCase();
		boolean exist = false;
		Map<Long, Long> map = CUSTOM_TABLE_INSTANCE_MAP.get(metricId);
		if (map != null) {
			exist = map.containsKey(instanceId);
			if (!exist) {
				Map<String, Object> param = new HashMap<>(2);
				param.put("metricId", metricId);
				param.put("resourceInstanceId", instanceId);
				exist = (int) session.selectOne("selectCustomMetricOne",
						param) > 0;
				if (exist) {
					map.put(instanceId, instanceId);
				}
			}
		}
		return exist;
	}

	public void updatePerformanceMetricDataExist(String metricId, Long instanceId) {
		metricId = metricId.toUpperCase();
		Map<Long, Long> map = REAL_TABLE_INSTANCE_MAP.get(metricId);
		if (map != null) {
			map.put(instanceId, instanceId);
		} else {
			map = new HashMap<>();
			map.put(instanceId, instanceId);
			REAL_TABLE_INSTANCE_MAP.put(metricId, map);
		}
	}


	public void updateInfoMetricDataExist(String metricId, Long instanceId) {
		String tempMetricId = metricId.toUpperCase();
		Map<Long, Long> map = INFO_TABLE_INSTANCE_MAP.get(tempMetricId);
		if (map != null) {
			map.put(instanceId, instanceId);
		} else {
			map = new HashMap<>();
			map.put(instanceId, instanceId);
			INFO_TABLE_INSTANCE_MAP.put(tempMetricId, map);
		}
	}

	public void updateAvaMetricDataExist(String metricId, Long instanceId) {
		String tempMetricId = metricId.toUpperCase();
		Map<Long, Long> map = AVAIL_TABLE_INSTANCE_MAP.get(tempMetricId);
		if (map != null) {
			map.put(instanceId, instanceId);
		} else {
			map = new HashMap<>();
			map.put(instanceId, instanceId);
			AVAIL_TABLE_INSTANCE_MAP.put(tempMetricId, map);
		}
	}

	public void updateCustomMetricDataExist(String metricId, Long instanceId) {
		metricId = metricId.toUpperCase();
		Map<Long, Long> map = CUSTOM_TABLE_INSTANCE_MAP.get(metricId);
		if (map != null) {
			map.put(instanceId, instanceId);
		} else {
			map = new HashMap<>();
			map.put(instanceId, instanceId);
			CUSTOM_TABLE_INSTANCE_MAP.put(metricId, map);
		}
	}

	public String createRealtimeMetricTable(String metricID, SqlSession session) {
		String mid = metricID.toUpperCase();
		String tbn = REALTIME_TABLE_NAME_PREFIX + mid;
		if (!REAL_TABLE_NAME_MAP.containsKey(mid)) {
			synchronized (this) {
				if (!REAL_TABLE_NAME_MAP.containsKey(mid)) {
					Map<String, String> param = new HashMap<String, String>();
					param.put("tableName", tbn);
					if (logger.isInfoEnabled())
						logger.info("create table:" + tbn);
					try {
						session.update("createRealtimeMetricTable", param);
						REAL_TABLE_NAME_MAP.put(mid, tbn);
					} catch (Exception e) {
						logger.error("fail to create table " + tbn, e);
						tbn = null;
					}
				}
			}
		}
		return tbn;
	}

	/**
	 *   oracle举例：P20170401 values less than (TO_DATE('2017-04-02', 'yyyy-MM-dd')) 
	 *   分区名字P+年月日   分区名字比 日期小一天
	 * @param metricID
	 * @param session
	 * @return
	 */
	public String createHistoryMetricTable(String metricID, SqlSession session) {
		String mid = metricID.toUpperCase();
		String tbn = HISTORY_TABLE_NAME_PREFIX + mid;
		if (null == HIS_TABLE_NAME_MAP.get(mid)) {
			Calendar c = Calendar.getInstance();
			Date curDate = c.getTime();
			String curPartitionName = PartitionDateUtil.partitionNameFormat(curDate);
			String curPartitionDate = PartitionDateUtil.partitionDateStrFormat(curDate);
			c.add(Calendar.DAY_OF_YEAR, -1);
			Date lastDate = c.getTime();
			String lastPartitionName = PartitionDateUtil.partitionNameFormat(lastDate);
			String lastPartitionDate = PartitionDateUtil.partitionDateStrFormat(lastDate);
			c.add(Calendar.DAY_OF_YEAR, 2);
			Date nextDate = c.getTime();
			String nextPartitionName = PartitionDateUtil.partitionNameFormat(nextDate);
			String nextPartitionDate = PartitionDateUtil.partitionDateStrFormat(nextDate);
			synchronized (this) {
				if (null == HIS_TABLE_NAME_MAP.get(mid)) {
					if (logger.isInfoEnabled())
						logger.info("create table:" + tbn);
					Map<String, String> param = new HashMap<>();
					param.put("tableName", tbn);
					param.put("partitionName", curPartitionName);
					param.put("partitionDate", curPartitionDate);
					param.put("lastPartitionName", lastPartitionName);
					param.put("lastPartitionDate", lastPartitionDate);
					param.put("nextPartitionName", nextPartitionName);
					param.put("nextPartitionDate", nextPartitionDate);
					Map<String, String> indexParam = new HashMap<>(2);
					indexParam.put("metricId", mid);
					indexParam.put("tableName", tbn);
					try {
						session.update("createHistoryMetricTable", param);
						session.update("createHistoryMetricIndex", indexParam);
						HIS_TABLE_NAME_MAP.put(mid, tbn);
					} catch (Exception e) {
						logger.warn("fail to create table " + tbn, e);
						tbn = null;
					}
				}
			}
		}
		return tbn;
	}

	public Set<String> getPerformanceMetricList() {
		Map<String, String> param = new HashMap<>();
		param.put("dbName", dbName);
		param.put("tablePrefix", HISTORY_TABLE_NAME_PREFIX + "%");
		logger.error("malachi 11111111111111111111111111 getMetricTableList");
		List<String> tns = session.selectList("getMetricTableList", param);
		logger.error("malachi 22222222222222222222222222222222222 getMetricTableList");
		logger.error("malachi tns = " + tns.size());
		int tableNamePrefixLength = HISTORY_TABLE_NAME_PREFIX.length();
		for (String tn : tns) {
			String tbname = tn.substring(tableNamePrefixLength).toUpperCase();
			HIS_TABLE_NAME_MAP.put(tbname, tn);
		}
		if (logger.isDebugEnabled())
			logger.debug("find history MetricTable:"
					+ JSON.toJSONString(HIS_TABLE_NAME_MAP));

		return HIS_TABLE_NAME_MAP.keySet();
	}

	public void getInfoMetricList() {
		List<MetricDataPO> tempDatas = session.selectList("getAllInfo");
		if(tempDatas != null && !tempDatas.isEmpty()){
			for (MetricDataPO tn : tempDatas) {
				String metricId = tn.getMetricId().toUpperCase();
				Map<Long,Long> instanceIds = INFO_TABLE_INSTANCE_MAP.get(metricId);
				if(null == instanceIds){
					instanceIds = new HashMap<>(20);
					INFO_TABLE_INSTANCE_MAP.put(metricId, instanceIds);
				}
				instanceIds.put(tn.getResourceInstanceId(), tn.getResourceInstanceId());
			}
		}
	}

	public void getAvaMetricList() {
		List<MetricDataPO> tempDatas = session.selectList("getAllAvailable");
		if(tempDatas != null && !tempDatas.isEmpty()){
			for (MetricDataPO tn : tempDatas) {
				String metricId = tn.getMetricId().toUpperCase();
				Map<Long,Long> instanceIds = AVAIL_TABLE_INSTANCE_MAP.get(metricId);
				if(null == instanceIds){
					instanceIds = new HashMap<>(20);
					AVAIL_TABLE_INSTANCE_MAP.put(metricId, instanceIds);
				}
				instanceIds.put(tn.getResourceInstanceId(), tn.getResourceInstanceId());
			}
		}
	}

	public void getCustomMetricList() {
		List<MetricDataPO> tempDatas = session.selectList("getAllCustom");
		if(tempDatas != null && !tempDatas.isEmpty()){
			for (MetricDataPO tn : tempDatas) {
				String metricId = tn.getMetricId().toUpperCase();
				Map<Long,Long> instanceIds = CUSTOM_TABLE_INSTANCE_MAP.get(metricId);
				if(null == instanceIds){
					instanceIds = new HashMap<>(20);
					CUSTOM_TABLE_INSTANCE_MAP.put(metricId, instanceIds);
				}
				instanceIds.put(tn.getResourceInstanceId(), tn.getResourceInstanceId());
			}
		}
	}


	public String getRealtimePerfMetricTable(String metricId) {
		return getTableName(metricId, REAL_TABLE_NAME_MAP,
				REALTIME_TABLE_NAME_PREFIX);
	}

	public String getHistoryPerfMetricTable(String metricId) {
		return getTableName(metricId, HIS_TABLE_NAME_MAP,
				HISTORY_TABLE_NAME_PREFIX);
	}

	private String getTableName(String metricId,
								Map<String, String> metricTableMap, String tablePrefix) {
		String mid = metricId.toUpperCase();
		String tableName = metricTableMap.get(mid);
		if (tableName == null) {
			synchronized (this) {
				tableName = metricTableMap.get(mid);
				if (tableName == null) {
					Map<String, String> param = new HashMap<>(2);
					param.put("dbName", dbName);
					param.put("table", tablePrefix + mid);

					tableName = metricDataDAO.selectMetricTable(param);
//					tableName = session.selectOne("selectMetricTable", param);
					if (tableName != null) {
						metricTableMap.put(mid, tableName);
					}
				}
			}
		}
		return tableName;
	}

	private void cacheTableName() {
		if (dbUrl.contains("?"))
			dbUrl = dbUrl.substring(0, dbUrl.indexOf("?"));
		this.dbName = dbUrl.substring(dbUrl.lastIndexOf("/") + 1);

		if (logger.isInfoEnabled())
			logger.info("find database name:" + dbName);

		// 历史数据表
		getPerformanceMetricList();
		// 实时数据表
		Map<String, String> param = new HashMap<>();
		param.put("dbName", dbName);
		param.put("tablePrefix", REALTIME_TABLE_NAME_PREFIX + "%");
		List<String> rns = session.selectList("getMetricTableList", param);
		//List<String> rns = metricDataDAO.getMetricTableList(param);
		int realtimeTableNamePrefixLength = REALTIME_TABLE_NAME_PREFIX.length();
		for (String tn : rns) {
			String tbname = tn.substring(realtimeTableNamePrefixLength)
					.toUpperCase();
			REAL_TABLE_NAME_MAP.put(tbname, tn);
			param.clear();
			param.put("tableName", tn);
			List<Long> instanceIds = session.selectList("selectExistRealTimeMetric",param);
			//List<Long> instanceIds = metricDataDAO.selectExistRealTimeMetric(param);
			if(instanceIds!=null && instanceIds.size()>0){
				for (Long instanceId : instanceIds) {
					updatePerformanceMetricDataExist(tbname, instanceId);
				}
			}
		}
		//可用性指标表
		getAvaMetricList();
		//信息指标表
		getInfoMetricList();
		//自定义指标
		getCustomMetricList();

		/*System.out.println("malachi REAL_TABLE_NAME_MAP = ");
		for (String key : REAL_TABLE_NAME_MAP.keySet()) {
			System.out.println(key + " = " + REAL_TABLE_NAME_MAP.get(key));
		}
		System.out.println("malachi HIS_TABLE_NAME_MAP = ");
		for (String key : HIS_TABLE_NAME_MAP.keySet()) {
			System.out.println(key + " = " + HIS_TABLE_NAME_MAP.get(key));
		}*/

		if (logger.isInfoEnabled())
			logger.info("find realtime MetricTable:"
					+ JSON.toJSONString(REAL_TABLE_NAME_MAP));
	}
}
