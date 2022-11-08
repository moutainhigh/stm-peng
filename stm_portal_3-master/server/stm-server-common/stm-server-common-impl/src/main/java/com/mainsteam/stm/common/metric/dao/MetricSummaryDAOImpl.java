package com.mainsteam.stm.common.metric.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.common.metric.obj.MetricSummaryData;
import com.mainsteam.stm.common.metric.obj.MetricSummaryType;
import com.mainsteam.stm.common.metric.query.MetricSummaryQuery;
import com.mainsteam.stm.common.metric.report.InstanceMetricSummeryReportData;
import com.mainsteam.stm.common.metric.report.MetricDataTopQuery;
import com.mainsteam.stm.common.metric.report.MetricSummeryReportData;
import com.mainsteam.stm.common.metric.report.MetricSummeryReportQuery;
import com.mainsteam.stm.common.metric.report.MetricWithTypeForReport;
import com.mainsteam.stm.obj.TimePeriod;
import com.mainsteam.stm.util.PartitionDateUtil;

/**
 * @author cx
 * 
 */
public class MetricSummaryDAOImpl implements MetricSummaryDAO {
	private static final Log logger = LogFactory
			.getLog(MetricSummaryDAOImpl.class);

	private SqlSession session;
	private SqlSessionFactory myBatisSqlSessionFactory;

	private static final String SUMMARY_TABLE_NAME_PREFIEX = "STM_MS_";
	private final Map<String, String> SUMMARY_TABLE_NAME_MAP = new HashMap<>();
	// private final Map<String,String> TABLE_NAME_MAP_DAY=new HashMap<>();
	// private static final String
	// SUMMARY_DAY_TABLE_NAME_PREFIX="STM_METRIC_SUMMARY_DAY_";
	//
	// private final Map<String,String> TABLE_NAME_MAP_HOUR=new HashMap<>();
	// private static final String
	// SUMMARY_HOUR_TABLE_NAME_PREFIX="STM_METRIC_SUMMARY_HOUR_";
	//
	private String dbUrl;

	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}

	public void setSession(SqlSession session) {
		this.session = session;
	}

	public void setMyBatisSqlSessionFactory(
			SqlSessionFactory myBatisSqlSessionFactory) {
		this.myBatisSqlSessionFactory = myBatisSqlSessionFactory;
	}

	public void init() {

		if (dbUrl.contains("?"))
			dbUrl = dbUrl.substring(0, dbUrl.indexOf("?"));
		final String dbName = dbUrl.substring(dbUrl.lastIndexOf("/") + 1);

		if (logger.isInfoEnabled())
			logger.info("find database name:" + dbName);

		Map<String, String> param = new HashMap<>();
		param.put("dbName", dbName);
		{
			param.put("tablePrefix", SUMMARY_TABLE_NAME_PREFIEX + "%");
			List<String> tns = session.selectList("getMetricSummaryTableList",
					param);
			int tableNamePrefixLength = SUMMARY_TABLE_NAME_PREFIEX.length();
			for (String tn : tns) {
				String tbname = tn.substring(tableNamePrefixLength)
						.toUpperCase();
				SUMMARY_TABLE_NAME_MAP.put(tbname, tn);
			}
			if (logger.isInfoEnabled()) {
				logger.info("find summary(day) Metric Table:"
						+ JSON.toJSONString(SUMMARY_TABLE_NAME_MAP));
			}
		}
	}

	@Override
	public List<MetricSummaryData> query(List<MetricSummaryQuery> queries) {
		return session.selectList("queryMetricSummaries", queries);
	}

	@Override
	public List<MetricSummaryData> query(MetricSummaryQuery query) {
		return session.selectList("queryMetricSummary", query);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.common.metric.dao.MetricSummaryDAO#summaryMetricData
	 * (java.lang.String, java.util.Date, java.util.Date,
	 * com.mainsteam.stm.common.metric.obj.MetricSummaryType)
	 */
	@Override
	public void summaryMetricData(String metricID, Date startTime,
			Date endTime, MetricSummaryType type) {
		metricID = metricID.toUpperCase();
		createTable(metricID, type);
		/**
		 * 优化指标数据汇总，取消对源表的锁定。
		 * 
		 * @author ziw modify at 2016/6/28
		 */
		Timestamp startTimeStamp = new Timestamp(startTime.getTime());
		Timestamp endTimeStamp = new Timestamp(endTime.getTime());
		Timestamp updateTimeStamp = new Timestamp(System.currentTimeMillis());
		Calendar c = Calendar.getInstance();
		c.setTime(startTime);
//		String computeSummaryDataSql = "SELECT INSTANCE_ID as instanceId,"
//				+ "avg(METRIC_DATA) as metricData,"
//				+ "max(METRIC_DATA) as maxMetricData,"
//				+ "min(METRIC_DATA) as minMetricData " + "FROM STM_M_H_"
//				+ metricID + " PARTITION(P" + c.get(Calendar.DAY_OF_MONTH)
//				+ ")" + " WHERE COLLECT_TIME >= ? AND COLLECT_TIME < ?"
//				+ " GROUP BY INSTANCE_ID";
		StringBuilder computeSummaryDataSql = new StringBuilder(500);
		computeSummaryDataSql.append("SELECT INSTANCE_ID as instanceId,");
		computeSummaryDataSql.append("avg(METRIC_DATA) as metricData,");
		computeSummaryDataSql.append("max(METRIC_DATA) as maxMetricData,");
		computeSummaryDataSql.append("min(METRIC_DATA) as minMetricData ");
		computeSummaryDataSql.append(" FROM STM_M_H_");
		computeSummaryDataSql.append(metricID);
		computeSummaryDataSql.append(" PARTITION(P");
		computeSummaryDataSql.append(PartitionDateUtil.partitionNameFormat(startTime));
		computeSummaryDataSql.append(")");
		computeSummaryDataSql.append(" WHERE COLLECT_TIME >= ? AND COLLECT_TIME < ?");
		computeSummaryDataSql.append( " GROUP BY INSTANCE_ID");
		
		StringBuilder insertSQl = new StringBuilder(500);
		insertSQl.append("INSERT INTO STM_MS_");
		insertSQl.append(type.name());
		insertSQl.append("_");
		insertSQl.append( metricID);
		insertSQl.append("(INSTANCE_ID,METRIC_DATA,METRIC_DATA_MAX,METRIC_DATA_MIN,START_TIME,END_TIME,UPDATE_TIME) ");
		insertSQl.append("VALUES (?,?,?,?,?,?,?)");
		
		SqlSession summarySession = myBatisSqlSessionFactory.openSession();
		Connection conn = summarySession.getConnection();
		final int threshold = 500;
		final int read_threshold=5000;//?能够减少mysql服务器的压力吗？
		try {
			conn.setAutoCommit(false);
			PreparedStatement summaryPs = conn
					.prepareStatement(computeSummaryDataSql.toString());
			PreparedStatement insertPs = conn.prepareStatement(insertSQl.toString(),
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			summaryPs.setTimestamp(1, startTimeStamp);
			summaryPs.setTimestamp(2, endTimeStamp);
			ResultSet rs = summaryPs.executeQuery();
			rs.setFetchSize(read_threshold);
			int count = threshold;
			while (rs.next()) {
				insertPs.setLong(1, rs.getLong(1));
				insertPs.setFloat(2, rs.getFloat(2));
				insertPs.setFloat(3, rs.getFloat(3));
				insertPs.setFloat(4, rs.getFloat(4));
				insertPs.setTimestamp(5, startTimeStamp);
				insertPs.setTimestamp(6, endTimeStamp);
				insertPs.setTimestamp(7, updateTimeStamp);
				insertPs.addBatch();
				count--;
				if (count == 0) {
					insertPs.executeBatch();
					conn.commit();
					insertPs.clearBatch();
					count = threshold;
				}
			}
			rs.close();
			summaryPs.close();
			insertPs.executeBatch();
			insertPs.close();
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			summarySession.close();
		}
	}

	@Override
	public void createTable(String metricID, MetricSummaryType type) {
		String key = type.name() + "_" + metricID.toUpperCase();
		String tbn = SUMMARY_TABLE_NAME_MAP.get(key);
		if (StringUtils.isEmpty(tbn)) {
			tbn = SUMMARY_TABLE_NAME_PREFIEX + type.name() + "_"
					+ metricID.toUpperCase();
			Map<String, String> tbMap = new HashMap<>();
			tbMap.put("tableName", tbn);
			try {
				session.update("createMetricSummaryTable", tbMap);
			} catch (Exception e) {
				logger.warn(e.getMessage());
			}
			if (logger.isInfoEnabled())
				logger.info("create table:" + tbn);
			SUMMARY_TABLE_NAME_MAP.put(key, tbn);
		}
	}

	@Override
	public void addCustomMetricSummary(Date start, Date end,
			MetricSummaryType type) {
		/**
		 * 优化指标数据汇总，取消对源表的锁定。
		 * 
		 * @author ziw modify at 2016/6/28
		 */
		Timestamp startTimeStamp = new Timestamp(start.getTime());
		Timestamp endTimeStamp = new Timestamp(end.getTime());
		Timestamp updateTimeStamp = new Timestamp(System.currentTimeMillis());

		String computeSummaryDataSql = "SELECT"
				+ " INSTANCE_ID as instanceId,"
				+ "METRIC_ID as metricid,"
				+ "avg(METRIC_DATA) as metricData,"
				+ "max(METRIC_DATA) as maxMetricData,"
				+ "min(METRIC_DATA) as minMetricData "
				+ "FROM STM_METRIC_CUSTOM_HISTORY"
				+ " WHERE METRIC_TYPE='PerformanceMetric' AND COLLECT_TIME >= ? AND COLLECT_TIME < ?"
				+ " GROUP BY INSTANCE_ID,METRIC_ID";
		String insertSQl = "INSERT INTO STM_METRIC_CUSTOM_SUMMARY"
				+ "(INSTANCE_ID,METRIC_ID,SUMMARY_TYPE,METRIC_DATA,METRIC_DATA_MAX,METRIC_DATA_MIN,START_TIME,END_TIME,UPDATE_TIME) "
				+ "VALUES (?,?,?,?,?,?,?,?,?)";
		SqlSession summarySession = myBatisSqlSessionFactory.openSession();
		Connection conn = summarySession.getConnection();
		try {
			conn.setAutoCommit(false);
			PreparedStatement summaryPs = conn
					.prepareStatement(computeSummaryDataSql);
			PreparedStatement insertPs = conn.prepareStatement(insertSQl);
			summaryPs.setTimestamp(1, startTimeStamp);
			summaryPs.setTimestamp(2, endTimeStamp);
			ResultSet rs = summaryPs.executeQuery();
			String summaryType = type.name();
			while (rs.next()) {
				insertPs.setLong(1, rs.getLong(1));
				insertPs.setString(2, rs.getString(2));
				insertPs.setString(3, summaryType);
				insertPs.setFloat(4, rs.getFloat(3));
				insertPs.setFloat(5, rs.getFloat(4));
				insertPs.setFloat(6, rs.getFloat(5));
				insertPs.setTimestamp(7, startTimeStamp);
				insertPs.setTimestamp(8, endTimeStamp);
				insertPs.setTimestamp(9, updateTimeStamp);
				insertPs.addBatch();
			}
			rs.close();
			summaryPs.close();
			insertPs.executeBatch();
			insertPs.close();
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			summarySession.close();
		}
	}

	@Override
	public List<InstanceMetricSummeryReportData> findHistorySummaryDataForReport(
			MetricSummeryReportQuery query) {
		Map<String, Object> param = new HashMap<>();

		List<MetricWithTypeForReport> metricList = query.getMetricIDes();
		List<String> pfList = new ArrayList<>();

		for (MetricWithTypeForReport type : metricList) {
			if (MetricTypeEnum.PerformanceMetric == type.getType()) {
				pfList.add(type.getMetricID());
			}
		}

		List<InstanceMetricSummeryReportData> list = new ArrayList<InstanceMetricSummeryReportData>();
		List<TimePeriod> timePeriods = query.getTimePeriods();
		param.put("timePeriods", timePeriods);
		param.put("summaryType", query.getSummaryType() == null ? MetricSummaryType.H : query.getSummaryType());
		param.put("metricIDes", pfList);
		param.put("instanceIDes", query.getInstanceIDes());

		Map<Long, InstanceMetricSummeryReportData> map = new HashMap<>(query.getInstanceIDes().size());
		for (Long instanceID : query.getInstanceIDes()) {

			InstanceMetricSummeryReportData insData = new InstanceMetricSummeryReportData();
			insData.setStartTime(timePeriods.get(0).getStartTime());
			insData.setEndTime(timePeriods.get(timePeriods.size()-1).getEndTime());
			insData.setInstanceID(instanceID);
			insData.setMetricData(new ArrayList<MetricSummeryReportData>());

			map.put(instanceID, insData);
			list.add(insData);
		}

		List<MetricSummeryReportData> dataList = session.selectList(
				"findHistorySummaryDataForReport", param);

		for (MetricSummeryReportData data : dataList) {
			InstanceMetricSummeryReportData insData = map.get(data.getInstanceID());
			if (insData != null)
				insData.getMetricData().add(data);
		}

		Collections.sort(list,
				new Comparator<InstanceMetricSummeryReportData>() {
					@Override
					public int compare(InstanceMetricSummeryReportData o1,
							InstanceMetricSummeryReportData o2) {
						return o1.getInstanceID() > o2.getInstanceID() ? (o1
								.getStartTime().before(o2.getStartTime()) ? 1
								: -1) : -1;
					}
				});
		return list;
	}

	@Override
	public List<MetricSummeryReportData> findInstanceHistorySummaryData(
			List<String> metricIDes, List<Long> instanceIDes, Date startTime,
			Date endTime, MetricSummaryType type) {
		if (logger.isDebugEnabled()) {
			logger.debug("findInstanceHistorySummaryData,start:" + startTime
					+ ",end:" + endTime);
		}

		UUID uuid = UUID.randomUUID();
		Map<String, Object> param = new HashMap<>();
		param.put("tmpID", uuid.toString());

		Calendar cal = Calendar.getInstance();
		cal.setTime(startTime);

		if (MetricSummaryType.HH == type) {
			cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) > 30 ? 30 : 0);
		} else if (MetricSummaryType.H == type) {
			// cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY)-1);
			cal.set(Calendar.MINUTE, 0);
		} else if (MetricSummaryType.SH == type) {
			cal.set(Calendar.HOUR_OF_DAY,
					(cal.get(Calendar.HOUR_OF_DAY) / 6) * 6);
			cal.set(Calendar.MINUTE, 0);
		} else if (MetricSummaryType.D == type) {
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
		} else {
			return null;
		}

		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date tmpStartTime = cal.getTime();
		boolean loop = tmpStartTime.before(endTime);
		SqlSession batchSession = myBatisSqlSessionFactory
				.openSession(ExecutorType.BATCH);
		try {
			while (loop) {
				param.put("timePoint", tmpStartTime);
				for (long instanceID : instanceIDes) {
					param.put("instanceID", instanceID);
					batchSession.insert("insertMetricTimeline", param);
				}
				int den = 1;
				if (MetricSummaryType.D == type) {
					cal.set(Calendar.DAY_OF_MONTH,
							cal.get(Calendar.DAY_OF_MONTH) + 1);
					den = 1000 * 60 * 60 * 24;
				} else if (MetricSummaryType.HH == type) {
					cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + 30);
					den = 1000 * 60 * 30;
				} else if (MetricSummaryType.SH == type) {
					cal.set(Calendar.HOUR_OF_DAY,
							cal.get(Calendar.HOUR_OF_DAY) + 6);
					den = 1000 * 60 * 60 * 6;
				} else if (MetricSummaryType.H == type) {
					cal.set(Calendar.HOUR_OF_DAY,
							cal.get(Calendar.HOUR_OF_DAY) + 1);
					den = 1000 * 60 * 60;
				} else {
					break;
				}
				tmpStartTime = cal.getTime();

				loop = (endTime.getTime() - tmpStartTime.getTime()) >= den;
			}
			batchSession.commit(true);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			batchSession.close();
		}

		param.put("metricIDes", metricIDes);
		param.put("summaryType", type != null ? type : MetricSummaryType.H);

		cal.setTime(startTime);
		cal.add(Calendar.HOUR_OF_DAY, -1);
		param.put("startTime", cal.getTime());
		param.put("endTime", endTime);

		List<MetricSummeryReportData> list = session.selectList(
				"findInstanceHistorySummaryData", param);

		session.delete("deleteMetricTimeline", param);
		return list;
	}

	@Override
	public List<MetricSummeryReportData> findTopSummaryData(
			MetricDataTopQuery query) {
		if (query.getSummaryType() == null) {
			query.setSummaryType(MetricSummaryType.H);
		}
		return session.selectList("findTopSummaryData", query);
	}

	@Override
	public List<MetricSummaryData> queryCustomMetricSummary(
			MetricSummaryQuery query) {
		if (query.getSummaryType() == null) {
			query.setSummaryType(MetricSummaryType.H);
		}
		return session.selectList("queryCustomMetricSummary", query);
	}
}
